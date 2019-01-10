package jp.jyn.zabbigot;

public class Keys {
    public final static Keys DEFAULT = new Keys("", "");
    public final static String TPS = "tps";
    public final static String USER = "user";
    public final static String PING = "ping";
    public final static String MEMORY_USED = "memory.used";
    public final static String MEMORY_FREE = "memory.free";
    public final static String CHUNK_LOAD = "chunk.load";
    public final static String CHUNK_UNLOAD = "chunk.unload";
    public final static String CHUNK_GENERATE = "chunk.generate";

    private final String prefix;
    private final String identifier;

    public Keys(String prefix, String identifier) {
        if (!prefix.isEmpty()) {
            prefix += ".";
        }
        this.prefix = prefix;

        if (!identifier.isEmpty()) {
            identifier = "[" + identifier + "]";
        }
        this.identifier = identifier;
    }

    public String toKey(String key) {
        return prefix + key + identifier;
    }
}
