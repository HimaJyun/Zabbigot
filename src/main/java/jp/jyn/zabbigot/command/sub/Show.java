package jp.jyn.zabbigot.command.sub;

import jp.jyn.zabbigot.EventCounter;
import jp.jyn.zabbigot.TpsWatcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Show implements CommandExecutor {
    private final static BigDecimal DECIMAL_20 = BigDecimal.valueOf(20);
    private final static BigDecimal DECIMAL_100 = BigDecimal.valueOf(100);
    private final static BigDecimal DECIMAL_MB = BigDecimal.valueOf(1024 * 1024);

    private final TpsWatcher watcher;
    private final EventCounter counter;

    public Show(TpsWatcher watcher, EventCounter counter) {
        this.watcher = watcher;
        this.counter = counter;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*
         * ======== Zabbigot (Player: 0/20) ========
         * TPS: [####################] 20.00 (100.0%)
         * MEM: [###################_] 7832.4MB/8192.0MB (95.6%)
         * CUR: [###############_____] 585 (75.4%)
         */

        sender.sendMessage(String.format(
            "%s========%s Zabbigot (Player: %d/%d) %s========",
            ChatColor.GREEN,
            ChatColor.RESET,
            Bukkit.getOnlinePlayers().size(),
            Bukkit.getMaxPlayers(),
            ChatColor.GREEN
        ));

        sender.sendMessage(tickPerSecond());
        sender.sendMessage(memoryUsage());
        sender.sendMessage(chunkUnloadRatio());
        return true;
    }

    private String tickPerSecond() {
        BigDecimal tps = BigDecimal.valueOf(watcher.getTPS());

        return String.format(
            "TPS: [%s] %s (%s%%)",
            gauge(tps.intValue()),
            tps.setScale(2, RoundingMode.DOWN).toPlainString(),
            tps.divide(DECIMAL_20, 3, RoundingMode.DOWN).multiply(DECIMAL_100).setScale(1, RoundingMode.DOWN).toPlainString()
        );
    }

    private String memoryUsage() {
        BigDecimal free = BigDecimal.valueOf(Runtime.getRuntime().freeMemory());
        BigDecimal total = BigDecimal.valueOf(Runtime.getRuntime().totalMemory());
        BigDecimal ratio = free.divide(total, 3, RoundingMode.DOWN);

        return String.format(
            "MEM: [%s] %sMB/%sMB (%s%%)",
            gauge(ratio.multiply(DECIMAL_20).intValue()),
            free.divide(DECIMAL_MB, 1, RoundingMode.DOWN).toPlainString(),
            total.divide(DECIMAL_MB, 1, RoundingMode.DOWN).toPlainString(),
            ratio.multiply(DECIMAL_100).setScale(1, RoundingMode.DOWN).toPlainString()
        );
    }

    private String chunkUnloadRatio() {
        int load = counter.chunkLoad.get();
        int unload = counter.chunkUnload.get();
        BigDecimal ratio = BigDecimal.valueOf(unload).divide(BigDecimal.valueOf(load), 3, RoundingMode.DOWN);

        return String.format(
            "CUR: [%s] %d (%s%%)",
            gauge(ratio.multiply(DECIMAL_20).intValue()),
            load - unload,
            ratio.multiply(DECIMAL_100).setScale(1, RoundingMode.DOWN).toPlainString()
        );
    }

    private String gauge(int length) {
        final StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.RED);

        boolean reset = false;
        for (int i = 0; i < 20; i++) {
            if (!reset) {
                if (i == 10) {
                    builder.append(ChatColor.YELLOW);
                } else if (i == 17) {
                    builder.append(ChatColor.GREEN);
                }
            }

            if (length <= i) {
                if (!reset) {
                    builder.append(ChatColor.RESET);
                    reset = true;
                }
                builder.append('_');
            } else {
                builder.append('#');
            }
        }

        builder.append(ChatColor.RESET);
        return builder.toString();
    }
}
