package jp.jyn.zabbigot;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TpsWatcher extends BukkitRunnable {

    // 2のn乗
    public final static int MAX_SAMPLING_SIZE = 256;

    private int tickCount = 0;
    private long[] ticks = new long[MAX_SAMPLING_SIZE];

    public TpsWatcher(Plugin plg) {
        this(plg, 0);
    }

    public TpsWatcher(Plugin plg, long delay) {
        this.runTaskTimer(plg, delay, 1);
    }

    /**
     * 100tick分の平均TPSを取得します。
     *
     * @return 取得したTPS
     */
    public double getTPS() {
        return getTPS(100);
    }

    /**
     * TPSを取得します。
     *
     * @param tick 平均のサンプル数
     * @return 指定されたサンプル数での平均TPS
     */
    public double getTPS(int tick) {
        if (tickCount < tick) {
            return 20.0D;
        }
        int target = (tickCount - 1 - tick) & (MAX_SAMPLING_SIZE - 1);
        long elapsed = System.currentTimeMillis() - ticks[target];

        return tick / (elapsed / 1000.0D);
    }

    public void run() {
        // &(MAX_SAMPLING_SIZE-1) == %MAX_SAMPLING_SIZE
        ticks[(tickCount++ & (MAX_SAMPLING_SIZE - 1))] = System.currentTimeMillis();
    }

}
