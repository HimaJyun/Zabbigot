package jp.jyn.zabbigot;

import jp.jyn.zabbigot.command.SubExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Zabbigot extends JavaPlugin {
    private final Deque<Runnable> destructor = new ArrayDeque<>();
    private final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

    private StatusManager manager;

    @Override
    public void onEnable() {
        destructor.clear();

        MainConfig config = new MainConfig(this);

        // start TPS watcher
        TpsWatcher watcher = new TpsWatcher();
        BukkitTask task = getServer().getScheduler().runTaskTimer(this, watcher, 0, 1);
        destructor.addFirst(task::cancel);

        // StatusManager
        manager = new StatusManager(config.hostname, config.sender, config.keyConverter);
        if (config.interval > 0) {
            ScheduledFuture<?> future = pool.scheduleAtFixedRate(
                manager::send,
                config.interval,
                config.interval,
                TimeUnit.SECONDS
            );
            destructor.addFirst(() -> future.cancel(false));
        }

        // start Event count
        EventCounter event = new EventCounter(this, watcher, manager, config.disable);
        destructor.addFirst(() -> HandlerList.unregisterAll(this));

        // command
        SubExecutor executor = new SubExecutor(this, watcher);
        PluginCommand command = getCommand("zabbigot");
        command.setExecutor(executor);
        command.setTabCompleter(executor);
        destructor.addFirst(() -> command.setExecutor(this));
        destructor.addFirst(() -> command.setTabCompleter(this));
    }

    @Override
    public void onDisable() {
        while (!destructor.isEmpty()) {
            destructor.removeFirst().run();
        }
    }

    /**
     * Get status manager
     *
     * @return StatusManager
     */
    public StatusManager getManager() {
        return manager;
    }
}
