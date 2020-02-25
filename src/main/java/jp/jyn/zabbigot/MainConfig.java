package jp.jyn.zabbigot;

import jp.jyn.zabbigot.sender.StatusSender;
import jp.jyn.zabbigot.sender.imple.JsonSender;
import jp.jyn.zabbigot.sender.imple.TsvSender;
import jp.jyn.zabbigot.sender.imple.ZabbixSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.UnaryOperator;

public class MainConfig {
    public final int interval;
    public final Set<StatusKey> disable = EnumSet.noneOf(StatusKey.class);
    public final StatusSender sender;

    public final String hostname;
    public final UnaryOperator<String> keyConverter;

    /*package*/ MainConfig(Plugin plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        ConfigurationSection config = plugin.getConfig();

        if (config.getInt("version", 0) == 0) {
            version0To1(plugin, config);
        }

        interval = config.getInt("Interval");

        for (String key : config.getStringList("Disable")) {
            // 例示
            if (key.equals("example")) {
                continue;
            }

            // memory.free -> memory_free -> MEMORY_FREE
            String enumKey = key.replace('.', '_').toUpperCase(Locale.ENGLISH);
            try {
                disable.add(StatusKey.valueOf(enumKey));
            } catch (IllegalArgumentException ignore) {
                plugin.getLogger().severe(key + " is not found!!");
            }
        }

        switch (config.getString("Sender").toLowerCase(Locale.ENGLISH)) {
            case "zabbix":
                sender = new ZabbixSender(config.getString("Zabbix.Server"), config.getInt("Zabbix.Port"));
                break;
            case "tsv":
                sender = new TsvSender(Paths.get(plugin.getDataFolder().getPath(), "status.tsv"));
                break;
            case "json":
                sender = new JsonSender(Paths.get(plugin.getDataFolder().getPath(), "status.json"));
                break;
            default:
                plugin.getLogger().severe("Unsupported Sender Type: " + config.getString("Sender"));
            case "test":
                sender = ignore -> new StatusSender.SendResult();
        }

        if (sender instanceof ZabbixSender) {
            this.hostname = config.getString("Zabbix.Hostname");

            String tmp = config.getString("Zabbix.Identifier", "");
            String identifier = tmp.isEmpty() ? "" : "[" + tmp + "]";
            this.keyConverter = s -> "minecraft." + s + identifier;
        } else {
            this.hostname = "";
            this.keyConverter = UnaryOperator.identity();
        }
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
