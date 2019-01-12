package jp.jyn.zabbigot.sender;

import java.time.Instant;

public class Status {
    public final String host;
    public final String key;
    public final String value;
    public final long clock;

    public Status(String host, String key, String value) {
        this(host, key, value, System.currentTimeMillis() / 1000);
    }

    public Status(String host, String key, String value, Instant clock) {
        this.host = host;
        this.key = key;
        this.value = value;
        this.clock = clock.getEpochSecond();
    }

    public Status(String host, String key, String value, long clock) {
        this.host = host;
        this.key = key;
        this.value = value;
        this.clock = clock;
    }

    public static StringBuilder jsonStr(String str, StringBuilder builder) {
        builder.append('"');

        for (char c : str.toCharArray()) {
            switch (c) {
                case '"':
                    builder.append("\\\"");
                    break;
                case '\\':
                    builder.append("\\\\");
                    break;
                case '/':
                    builder.append("\\/");
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                default:
                    builder.append(c);
                    break;
            }
        }

        builder.append('"');
        return builder;
    }
}
