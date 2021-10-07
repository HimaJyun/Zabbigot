package jp.jyn.zabbigot;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EventCounter implements Listener {
    // Zabbix keeps decimals only up to 4 digits(double(16,4))
    public final static int ZABBIX_MAX_SCALE = 4;

    private final Runtime runtime = Runtime.getRuntime();
    private final AtomicLong freeMemory = new AtomicLong(runtime.freeMemory());

    private final AtomicInteger ping = new AtomicInteger(0);
    private final AtomicInteger chunkLoad = new AtomicInteger(0);
    private final AtomicInteger chunkUnload = new AtomicInteger(0);
    private final AtomicInteger chunkGenerate = new AtomicInteger(0);
    private final AtomicInteger inventoryMove = new AtomicInteger(0);

    /*package*/ EventCounter(Plugin plugin, TpsWatcher watcher, StatusManager manager, Set<StatusKey> disable) {
        // 即席関数
        BiConsumer<Class<? extends Event>, EventExecutor> register =
            (c, e) -> Bukkit.getServer().getPluginManager().registerEvent(
                c, this, EventPriority.MONITOR, e, plugin, true
            );

        // 必要のないものはカウントしない
        if (!disable.contains(StatusKey.MEMORY_USED) || !disable.contains(StatusKey.MEMORY_FREE)) {
            manager.addPrepare(() -> freeMemory.set(runtime.freeMemory()));
        }

        if (!disable.contains(StatusKey.PING)) {
            register.accept(ServerListPingEvent.class, (l, e) -> ping.incrementAndGet());
        }

        if (!disable.contains(StatusKey.CHUNK_LOAD) ||
            !disable.contains(StatusKey.CHUNK_LOADED) ||
            !disable.contains(StatusKey.CHUNK_RATIO)) {
            register.accept(ChunkLoadEvent.class, (l, e) -> chunkLoad.incrementAndGet());
            // Get already loaded chunks.
            chunkLoad.getAndAdd(Bukkit.getWorlds().stream().mapToInt(w -> w.getLoadedChunks().length).sum());
        }

        if (!disable.contains(StatusKey.CHUNK_UNLOAD) ||
            !disable.contains(StatusKey.CHUNK_LOADED) ||
            !disable.contains(StatusKey.CHUNK_RATIO)) {
            register.accept(ChunkUnloadEvent.class, (l, e) -> chunkUnload.incrementAndGet());
        }

        if (!disable.contains(StatusKey.CHUNK_GENERATE)) {
            register.accept(ChunkPopulateEvent.class, (l, e) -> chunkGenerate.incrementAndGet());
        }

        if (!disable.contains(StatusKey.INVENTORY_MOVE)) {
            register.accept(InventoryMoveItemEvent.class, (l, e) -> inventoryMove.incrementAndGet());
        }

        // 即席関数
        BiConsumer<StatusKey, Supplier<String>> tmp = (k, v) -> {
            if (!disable.contains(k)) {
                manager.addStatus(k.key, v);
            }
        };

        tmp.accept(StatusKey.TPS,
            () -> BigDecimal.valueOf(watcher.getTPS())
                .setScale(ZABBIX_MAX_SCALE, RoundingMode.DOWN)
                .toPlainString()
        );
        tmp.accept(StatusKey.USER, () -> String.valueOf(Bukkit.getOnlinePlayers().size()));
        tmp.accept(StatusKey.PING, ping::toString);
        tmp.accept(StatusKey.MEMORY_USED, () -> String.valueOf(runtime.totalMemory() - freeMemory.get()));
        tmp.accept(StatusKey.MEMORY_FREE, freeMemory::toString);
        tmp.accept(StatusKey.CHUNK_LOAD, chunkLoad::toString);
        tmp.accept(StatusKey.CHUNK_UNLOAD, chunkUnload::toString);
        tmp.accept(StatusKey.CHUNK_LOADED, () -> String.valueOf(chunkLoad.get() - chunkUnload.get()));
        tmp.accept(StatusKey.CHUNK_GENERATE, chunkGenerate::toString);
        // (unload/load)*100
        tmp.accept(StatusKey.CHUNK_RATIO,
            () -> BigDecimal.valueOf(chunkUnload.get())
                .divide(BigDecimal.valueOf(chunkLoad.get()), (ZABBIX_MAX_SCALE + 2), RoundingMode.DOWN)
                .scaleByPowerOfTen(2)
                .toPlainString()
        );
        tmp.accept(StatusKey.INVENTORY_MOVE, inventoryMove::toString);
        tmp.accept(StatusKey.ENTITY_COUNT, () -> {
            try {
                return String.valueOf(Bukkit.getScheduler().callSyncMethod(plugin,
                    ()-> Bukkit.getWorlds().stream().mapToInt(w -> w.getEntities().size()).sum()
                ).get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
