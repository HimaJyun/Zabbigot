package jp.jyn.zabbigot;

import jp.jyn.zabbigot.sender.Status;
import jp.jyn.zabbigot.sender.StatusSender;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class StatusManager {
    private final Runtime runtime;
    private final AtomicLong freeMemory;

    private final String host;

    private final List<Status> statusList;
    private final StatusSender sender;

    private final UnaryOperator<String> keyConverter;
    private final Set<String> disabledKeys;

    private StatusManager(Builder builder) {
        this.runtime = builder.runtime;
        this.freeMemory = builder.freeMemory;

        this.host = builder.host;
        this.sender = builder.sender;
        this.keyConverter = builder.keyConverter;

        this.disabledKeys = builder.disabledKeys.stream().map(keyConverter).collect(Collectors.toSet());
        this.statusList = builder.statusMap.entrySet()
            .stream()
            .map(v -> new AbstractMap.SimpleEntry<>(keyConverter.apply(v.getKey()), v.getValue()))
            .filter(v -> !disabledKeys.contains(v.getKey()))
            .map(v -> new Status(host, v.getKey(), v.getValue().getKey(), v.getValue().getValue()))
            .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }

    /**
     * Add status
     *
     * @param key   status key
     * @param value status value
     * @param clock status clock
     */
    public void addStatus(String key, Supplier<String> value, LongSupplier clock) {
        String newKey = keyConverter.apply(key);
        if (!disabledKeys.contains(newKey)) {
            // Key may be duplicated. However, the possibility is low, so I ignore it.
            statusList.add(new Status(host, newKey, value, clock));
        }
    }

    /**
     * Add status
     *
     * @param key   status key
     * @param value status value
     */
    public void addStatus(String key, Supplier<String> value) {
        this.addStatus(key, value, () -> System.currentTimeMillis() / 1000);
    }

    /**
     * Send status
     *
     * @return Send result
     */
    public StatusSender.SendResult send() {
        // Consistent values are needed to correctly calculate memory usage.
        freeMemory.set(runtime.freeMemory());
        return sender.send(statusList);
    }

    public static class Builder {
        // Zabbix keeps decimals only up to 4 digits(double(16,4))
        private final static int ZABBIX_MAX_SCALE = 4;

        private final Runtime runtime = Runtime.getRuntime();
        private final AtomicLong freeMemory = new AtomicLong(runtime.freeMemory());
        private final StatusSender sender;

        private String host = "";
        private Map<String, Map.Entry<Supplier<String>, LongSupplier>> statusMap = new LinkedHashMap<>();
        private UnaryOperator<String> keyConverter = s -> s;
        private List<String> disabledKeys = new LinkedList<>();

        public Builder(StatusSender sender) {
            this.sender = sender;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setKeyConverter(UnaryOperator<String> keyConverter) {
            this.keyConverter = keyConverter;
            return this;
        }

        public Builder addDisabledKeys(Collection<String> disabledKeys) {
            this.disabledKeys.addAll(disabledKeys);
            return this;
        }

        public Builder addDisabledKeys(String... disabledKeys) {
            this.disabledKeys.addAll(Arrays.asList(disabledKeys));
            return this;
        }

        public Builder addStatus(String key, Supplier<String> value, LongSupplier clock) {
            statusMap.put(key, new AbstractMap.SimpleEntry<>(value, clock));
            return this;
        }

        public Builder addStatus(String key, Supplier<String> value) {
            statusMap.put(key, new AbstractMap.SimpleEntry<>(value, () -> System.currentTimeMillis() / 1000));
            return this;
        }

        public Builder addDefaultStatus(TpsWatcher watcher, EventCounter counter) {
            // add Status
            addStatus("tps", () -> BigDecimal.valueOf(watcher.getTPS()).setScale(ZABBIX_MAX_SCALE, RoundingMode.DOWN).toPlainString());
            addStatus("user", () -> String.valueOf(Bukkit.getOnlinePlayers().size()));
            addStatus("ping", counter.ping::toString);
            addStatus("memory.used", () -> String.valueOf(runtime.totalMemory() - freeMemory.get()));
            addStatus("memory.free", freeMemory::toString);
            addStatus("chunk.load", counter.chunkLoad::toString);
            addStatus("chunk.unload", counter.chunkUnload::toString);
            addStatus("chunk.loaded", () -> String.valueOf(counter.chunkLoad.get() - counter.chunkUnload.get()));
            addStatus("chunk.generate", counter.chunkGenerate::toString);
            // (unload/load)*100
            addStatus(
                "chunk.ratio",
                () -> BigDecimal.valueOf(counter.chunkUnload.get())
                    .divide(BigDecimal.valueOf(counter.chunkLoad.get()), (ZABBIX_MAX_SCALE + 2), RoundingMode.DOWN)
                    .scaleByPowerOfTen(2)
                    .toPlainString()
            );
            return this;
        }

        public StatusManager build() {
            return new StatusManager(this);
        }
    }
}
