package jp.jyn.zabbigot;

import jp.jyn.zabbigot.command.SubExecutor;
import jp.jyn.zabbigot.sender.Status;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Zabbigot extends JavaPlugin {
    private final Deque<Runnable> destructor = new ArrayDeque<>();
    private final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
    private final List<Supplier<Status>> suppliers = new ArrayList<>();
    private final Runtime runtime = Runtime.getRuntime();

    private MainConfig config;
    private AtomicLong free = new AtomicLong(runtime.freeMemory());

    @Override
    public void onEnable() {
        destructor.clear();
        suppliers.clear();

        config = new MainConfig(this);

        // start TPS watcher
        TpsWatcher watcher = new TpsWatcher();
        BukkitTask task = getServer().getScheduler().runTaskTimer(this, watcher, 0, 1);
        destructor.addFirst(task::cancel);

        // start Event count
        EventCounter event = new EventCounter();
        getServer().getPluginManager().registerEvents(event, this);
        destructor.addFirst(() -> HandlerList.unregisterAll(this));

        // add Status
        addStatus(Keys.TPS, () -> new BigDecimal(watcher.getTPS()).setScale(4, RoundingMode.DOWN).toPlainString());
        addStatus(Keys.USER, () -> String.valueOf(Bukkit.getOnlinePlayers().size()));
        addStatus(Keys.PING, event.ping::toString);
        addStatus(Keys.MEMORY_USED, () -> String.valueOf(runtime.totalMemory() - free.get()));
        addStatus(Keys.MEMORY_FREE, free::toString);
        addStatus(Keys.CHUNK_LOAD, event.chunkLoad::toString);
        addStatus(Keys.CHUNK_UNLOAD, event.chunkUnload::toString);
        addStatus(Keys.CHUNK_GENERATE, event.chunkGenerate::toString);

        // status sender
        if (config.interval > 0) {
            ScheduledFuture<?> future = pool.scheduleAtFixedRate(
                this::send,
                config.interval,
                config.interval,
                TimeUnit.SECONDS
            );
            destructor.addFirst(() -> future.cancel(false));
        }

        // command
        SubExecutor executor = new SubExecutor(this, watcher, event);
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

    // TODO: 他のクラスに移動
    public void addStatus(String key, Supplier<String> value) {
        String key2 = config.keys.toKey(key);
        if (!config.disable.contains(key2)) {
            suppliers.add(() -> new Status(config.hostname, key2, value.get()));
        }
    }

    public List<Status> getData() {
        return suppliers.stream().map(Supplier::get).collect(Collectors.toList());
    }

    public String send(Collection<Status> data) {
        free.set(runtime.freeMemory());
        return config.sender.send(data);
    }

    private void send() {
        free.set(runtime.freeMemory());
        config.sender.send(getData());
    }
}
