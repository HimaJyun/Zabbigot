package jp.jyn.zabbigot.command.sub;

import jp.jyn.zabbigot.StatusSender;
import jp.jyn.zabbigot.Zabbigot;
import jp.jyn.zabbigot.command.SubBase;
import jp.jyn.zabbigot.sender.Status;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Send extends SubBase {

    private final StatusSender statusSender;
    ExecutorService exector = Executors.newSingleThreadExecutor();

    public Send(Zabbigot zabbigot) {
        super("zabbigot.send");
        this.statusSender = zabbigot.getStatusSender();
    }

    @Override
    protected void exec(final CommandSender sender) {
        exector.submit(() -> {
            List<Status> data = statusSender.getData();
            String result = statusSender.send(data);

            sender.sendMessage(ChatColor.GREEN + "========" + ChatColor.RESET + " Zabbigot " + ChatColor.GREEN + "========");
            sender.sendMessage("Send:");
            for (Status datum : data) {
                sender.sendMessage(datum.key + ": " + datum.value);
            }

            sender.sendMessage("");
            sender.sendMessage("Result:");
            sender.sendMessage(result);
        });
    }

}
