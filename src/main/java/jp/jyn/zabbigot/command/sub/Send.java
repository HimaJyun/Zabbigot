package jp.jyn.zabbigot.command.sub;

import io.github.hengyunabc.zabbix.sender.DataObject;
import io.github.hengyunabc.zabbix.sender.SenderResult;
import jp.jyn.zabbigot.StatusSender;
import jp.jyn.zabbigot.Zabbigot;
import jp.jyn.zabbigot.command.SubBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
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
        exector.submit(new Runnable() {
            @Override
            public void run() {
                List<DataObject> data = statusSender.getData();

                SenderResult result;
                try {
                    result = statusSender.send(data);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                sender.sendMessage(ChatColor.GREEN + "========" + ChatColor.RESET + " Zabbigot " + ChatColor.GREEN + "========");
                sender.sendMessage("Send:");
                for (DataObject obj : data) {
                    sender.sendMessage(obj.getKey() + " " + obj.getValue());
                }
                sender.sendMessage("");
                sender.sendMessage("Result:");
                sender.sendMessage(result.toString());
            }
        });
    }

}
