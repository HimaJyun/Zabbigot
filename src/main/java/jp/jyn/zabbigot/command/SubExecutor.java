package jp.jyn.zabbigot.command;

import jp.jyn.zabbigot.EventCounter;
import jp.jyn.zabbigot.TpsWatcher;
import jp.jyn.zabbigot.Zabbigot;
import jp.jyn.zabbigot.command.sub.Reload;
import jp.jyn.zabbigot.command.sub.Send;
import jp.jyn.zabbigot.command.sub.Show;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class SubExecutor implements CommandExecutor, TabCompleter {
    private final Map<String, CommandExecutor> commands = new HashMap<>();
    private final static String NO_ARGS = "show";

    public SubExecutor(Zabbigot zabbigot, TpsWatcher watcher, EventCounter counter) {
        commands.put("send", new Send(zabbigot));
        commands.put("reload", new Reload(zabbigot));

        commands.put(NO_ARGS, new Show(watcher, counter));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String sub = null;
        CommandExecutor executor = null;
        if (args.length != 0) {
            sub = args[0].toLowerCase(Locale.ENGLISH);
            executor = commands.get(sub);
        }

        if (executor == null) {
            sub = NO_ARGS;
            executor = commands.get(NO_ARGS);
        }

        if (!sender.hasPermission("zabbigot." + sub)) {
            sender.sendMessage("[Zabbigot] You don't have permission!!");
            return true;
        }

        return executor.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return commands.keySet().stream().filter(str -> str.startsWith(args[0])).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
