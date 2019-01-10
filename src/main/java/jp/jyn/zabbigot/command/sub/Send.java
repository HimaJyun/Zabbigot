package jp.jyn.zabbigot.command.sub;

import jp.jyn.zabbigot.Zabbigot;
import jp.jyn.zabbigot.sender.Status;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Send implements CommandExecutor {
    private final Zabbigot zabbigot;

    public Send(Zabbigot zabbigot) {
        this.zabbigot = zabbigot;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        zabbigot.getServer().getScheduler().runTaskAsynchronously(zabbigot, () -> {
            List<Status> data = zabbigot.getData();
            String result = zabbigot.send(data);

            sender.sendMessage(ChatColor.GREEN + "========" + ChatColor.RESET + " Zabbigot " + ChatColor.GREEN + "========");
            sender.sendMessage("Send:");
            for (Status datum : data) {
                sender.sendMessage(datum.key + ": " + datum.value);
            }

            sender.sendMessage("");
            sender.sendMessage("Result:");
            sender.sendMessage(result);
        });
        return true;
    }
}
