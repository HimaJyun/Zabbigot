package jp.jyn.zabbigot.command.sub;

import jp.jyn.zabbigot.TpsWatcher;
import jp.jyn.zabbigot.Zabbigot;
import jp.jyn.zabbigot.command.SubBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class Show extends SubBase {

    private final StringBuilder builder = new StringBuilder();
    private final TpsWatcher watcher;

    public Show(Zabbigot zabbigot) {
        super("zabbigot.show");
        this.watcher = zabbigot.getTpsWatcher();
    }

    @Override
    protected void exec(CommandSender sender) {
        /*
         * ======== Zabbigot (Player: 0/20) ========
         * TPS: [####################] 20.00 (100.0%)
         * MEM: [###################_] 7832.4MB/8192.0MB (95.6%)
         */

        Server server = Bukkit.getServer();
        sender.sendMessage(ChatColor.GREEN + "========" + ChatColor.RESET
            + " Zabbigot (Player: "
            + server.getOnlinePlayers().size()
            + "/"
            + server.getMaxPlayers()
            + ") " + ChatColor.GREEN + "========");

        sender.sendMessage(formatTps(watcher.getTPS()));
        sender.sendMessage(formatMem());
    }

    private String formatTps(double tps) {
        builder.setLength(0);

        builder.append("TPS: [");
        gaugeFormat(tps);
        builder.append("] ");

        builder.append(String.format("%.2f", tps));
        builder.append(" (");
        builder.append(String.format("%.1f", (tps / 20) * 100.0D));
        builder.append("%)");

        return builder.toString();
    }

    private String formatMem() {
        builder.setLength(0);
        Runtime runtime = Runtime.getRuntime();

        long total = runtime.totalMemory();
        long free = runtime.freeMemory();

        builder.append("MEM: [");
        gaugeFormat((20.0 / total) * free);
        builder.append("] ");

        // 1048576 = 1024 * 1024;
        builder.append(String.format("%.1f", free / 1048576.0));
        builder.append("MB/");
        builder.append(String.format("%.1f", total / 1048576.0));
        builder.append("MB (");
        builder.append(String.format("%.1f", ((double) free / total) * 100.0D));
        builder.append("%)");

        return builder.toString();
    }

    private void gaugeFormat(double value) {
        boolean reseted = false;

        builder.append(ChatColor.RED);

        for (int i = 0; i < 20; ++i) {
            if (!reseted) {
                switch (i) {
                    case 10: // 10回目
                        builder.append(ChatColor.YELLOW);
                        break;
                    case 17: // 17回目
                        builder.append(ChatColor.GREEN);
                        break;
                    default:
                        break;
                }
            }

            if (value < 1) { // 1以下
                if (!reseted) {
                    builder.append(ChatColor.RESET);
                    reseted = true;
                }
                builder.append('_');
            } else {
                builder.append('#');
            }
            --value;
        }

        builder.append(ChatColor.RESET);
    }
}
