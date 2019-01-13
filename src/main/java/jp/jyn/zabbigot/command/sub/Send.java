package jp.jyn.zabbigot.command.sub;

import jp.jyn.zabbigot.Zabbigot;
import jp.jyn.zabbigot.sender.StatusSender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class Send implements CommandExecutor {
    private final Zabbigot zabbigot;

    public Send(Zabbigot zabbigot) {
        this.zabbigot = zabbigot;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        zabbigot.getServer().getScheduler().runTaskAsynchronously(zabbigot, () -> {
            StatusSender.SendResult result = zabbigot.getManager().send();

            sender.sendMessage(ChatColor.GREEN + "========" + ChatColor.RESET + " Zabbigot " + ChatColor.GREEN + "========");
            sender.sendMessage("Send:");
            for (Map.Entry<String, String> datum : result.data.entrySet()) {
                sender.sendMessage(datum.getKey() + ": " + datum.getValue());
            }

            sender.sendMessage("");
            sender.sendMessage("Response:");
            sender.sendMessage(result.response);
        });
        return true;
    }
}
