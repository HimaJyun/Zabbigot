package jp.jyn.zabbigot;

public enum StatusKey {
    USER("user"),
    TPS("tps"),
    PING("ping"),
    MEMORY_FREE("memory.free"),
    MEMORY_USED("memory.used"),
    CHUNK_LOAD("chunk.load"),
    CHUNK_UNLOAD("chunk.unload"),
    CHUNK_LOADED("chunk.loaded"),
    CHUNK_GENERATE("chunk.generate"),
    CHUNK_RATIO("chunk.ratio"),
    INVENTORY_MOVE("inventory.move"),
    ENTITY_COUNT("entity.count");

    public final String key;
    StatusKey(String key) {this.key = key;}
}
