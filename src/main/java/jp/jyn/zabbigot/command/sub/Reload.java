package jp.jyn.zabbigot.command.sub;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Reload implements CommandExecutor {

    private final Plugin plugin;

    public Reload(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // reload
        plugin.getServer().getPluginManager().callEvent(new PluginEnableEvent(plugin));
        plugin.onDisable();
        plugin.onEnable();
        plugin.getServer().getPluginManager().callEvent(new PluginDisableEvent(plugin));

        sender.sendMessage("[Zabbigot] Config has been reloaded.");
        return true;
    }
}
