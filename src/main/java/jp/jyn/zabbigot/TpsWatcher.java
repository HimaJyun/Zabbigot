package jp.jyn.zabbigot;

public class TpsWatcher implements Runnable {
    // 2^n
    private final static int MAX_SAMPLING_SIZE = 256;
    private final static double TIMER_UNIT =  1_000_000_000.0D;

    private int tickCount = -1;
    private final long[] ticks = new long[MAX_SAMPLING_SIZE];

    public double getTPS() {
        return getTPS(100);
    }

    public double getTPS(int tick) {
        if (tickCount < tick) {
            return 20.0D;
        }
        int target = (tickCount - 1 - tick) & (MAX_SAMPLING_SIZE - 1);
        long elapsed = System.nanoTime() - ticks[target];

        return tick / (elapsed / TIMER_UNIT);
    }

    public void run() {
        // &(MAX_SAMPLING_SIZE-1) == %MAX_SAMPLING_SIZE
        ticks[(++tickCount & (MAX_SAMPLING_SIZE - 1))] = System.nanoTime();
    }

}
