package jp.jyn.zabbigot;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class EventCounter implements Listener {
    public final AtomicInteger ping = new AtomicInteger(0);
    public final AtomicInteger chunkLoad = new AtomicInteger(0);
    public final AtomicInteger chunkUnload = new AtomicInteger(0);
    public final AtomicInteger chunkGenerate = new AtomicInteger(0);

    public EventCounter() {
        // Get already loaded chunks.
        chunkLoad.getAndAdd(
            Bukkit.getWorlds().stream()
                .map(World::getLoadedChunks)
                .mapToInt(c -> c.length)
                .sum()
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onServerListPing(ServerListPingEvent e) {
        ping.incrementAndGet();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent e) {
        chunkLoad.incrementAndGet();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent e) {
        chunkUnload.incrementAndGet();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkPopulate(ChunkPopulateEvent e) {
        chunkGenerate.incrementAndGet();
    }
}
