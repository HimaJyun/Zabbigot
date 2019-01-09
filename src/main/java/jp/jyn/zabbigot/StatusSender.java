package jp.jyn.zabbigot;

import jp.jyn.zabbigot.sender.Sender;
import jp.jyn.zabbigot.sender.Status;
import jp.jyn.zabbigot.sender.imple.ZabbixSender;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class StatusSender implements Runnable {

    private final ConfigStruct config;
    private final TpsWatcher watcher;

    private final String hostname;

    private final String keyTps;
    private final String keyUser;
    private final String keyMemUsed;
    private final String keyMemFree;

    private final Runtime runtime = Runtime.getRuntime();
    private final Sender sender;

    public StatusSender(Zabbigot zabbigot) {
        this.config = zabbigot.getConfigStruct();
        this.hostname = config.getZabbixHostname();
        this.watcher = zabbigot.getTpsWatcher();

        keyTps = "minecraft.tps[" + config.getIdentifier() + "]";
        keyUser = "minecraft.user[" + config.getIdentifier() + "]";
        keyMemUsed = "minecraft.memory.used[" + config.getIdentifier() + "]";
        keyMemFree = "minecraft.memory.free[" + config.getIdentifier() + "]";

        sender = new ZabbixSender(config.getZabbixServer(), config.getZabbixPort());
    }

    @Override
    public void run() {
        sender.send(getData());
    }

    public String send(List<Status> data) {
        return sender.send(data);
    }

    public List<Status> getData() {
        List<Status> data = new ArrayList<>();
        // Zabbixが小数点以下4桁までなので揃える

        data.add(new Status(hostname,
            keyTps,
            (new BigDecimal(watcher.getTPS()))
                .setScale(4, RoundingMode.DOWN)
                .toPlainString()));

        // オンラインユーザ数取得、サブスレッドからの実行でも例外は出ない
        data.add(new Status(hostname, keyUser, String.valueOf(Bukkit.getOnlinePlayers().size())));

        // メモリ
        long free = runtime.freeMemory();
        data.add(new Status(hostname, keyMemUsed, String.valueOf(runtime.totalMemory() - free)));
        data.add(new Status(hostname, keyMemFree, String.valueOf(free)));

        return data;
    }
}
