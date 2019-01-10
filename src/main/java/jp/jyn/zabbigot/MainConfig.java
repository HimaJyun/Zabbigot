package jp.jyn.zabbigot;

import jp.jyn.zabbigot.sender.Sender;
import jp.jyn.zabbigot.sender.imple.ZabbixSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class MainConfig {
    public final int interval;

    public final String hostname;
    public final Sender sender;
    public final Keys keys;

    public final Set<String> disable;

    public MainConfig(Plugin plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        ConfigurationSection config = plugin.getConfig();

        if (config.getInt("version", 0) == 0) {
            version0To1(plugin, config);
        }

        interval = config.getInt("Interval");

        switch (config.getString("Sender").toLowerCase(Locale.ENGLISH)) {
            case "zabbix":
                sender = new ZabbixSender(config.getString("Zabbix.Server"), config.getInt("Zabbix.Port"));
                hostname = config.getString("Zabbix.Hostname");
                keys = new Keys("minecraft", config.getString("Zabbix.Identifier"));
                break;
            default:
                plugin.getLogger().severe("Unsupported Sender Type: " + config.getString("Sender"));
            case "test":
                sender = ignore -> "error";
                hostname = "";
                keys = Keys.DEFAULT;
        }

        disable = config.getStringList("Disable").stream()
            .map(keys::toKey)
            .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
    }

    private void version0To1(Plugin plugin, ConfigurationSection config) {
        if (!config.contains("Zabbix.Identifier", true)) {
            config.set("Zabbix.Identifier", config.getString("Identifier", "Minecraft"));
            config.set("Identifier", null);
        }
        if (!config.contains("Sender", true)) {
            config.set("Sender", "zabbix");
        }
        if (!config.contains("Disable", true)) {
            config.set("Disable", Collections.singletonList("example"));
        }

        config.set("version", 1);
        plugin.saveConfig();
    }
}
