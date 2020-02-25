package jp.jyn.zabbigot;

import jp.jyn.zabbigot.sender.Status;
import jp.jyn.zabbigot.sender.StatusSender;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class StatusManager {
    private final String host;
    private final StatusSender sender;
    private final UnaryOperator<String> keyConverter;

    private final List<Runnable> prepare = new CopyOnWriteArrayList<>();
    private final List<Status> statusList = new CopyOnWriteArrayList<>();

    /*package*/ StatusManager(String host, StatusSender sender, UnaryOperator<String> keyConverter) {
        this.host = host;
        this.sender = sender;
        this.keyConverter = keyConverter;
    }

    /**
     * Add status
     *
     * @param key   status key
     * @param value status value
     * @param clock status clock
     * @return for method chain
     */
    public StatusManager addStatus(String key, Supplier<String> value, LongSupplier clock) {
        // Key may be duplicated. However, the possibility is low, so I ignore it.
        statusList.add(new Status(host, keyConverter.apply(key), value, clock));
        return this;
    }

    /**
     * Add status
     *
     * @param key   status key
     * @param value status value
     * @return for method chain
     */
    public StatusManager addStatus(String key, Supplier<String> value) {
        return this.addStatus(key, value, () -> System.currentTimeMillis() / 1000);
    }

    /**
     * Add necessary processing before sending the status.
     *
     * @param runnable runnable
     * @return for method chain
     */
    public StatusManager addPrepare(Runnable runnable) {
        prepare.add(runnable);
        return this;
    }

    /**
     * Send status
     *
     * @return Send result
     */
    public StatusSender.SendResult send() {
        for (Runnable runnable : prepare) {
            runnable.run();
        }
        return sender.send(statusList);
    }
}
