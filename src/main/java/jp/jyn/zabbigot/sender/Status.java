package jp.jyn.zabbigot.sender;

import java.time.Instant;
import java.util.Arrays;

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

    public static String toJson(Status... data) {
        return toJson(Arrays.asList(data));
    }

    public static String toJson(Iterable<Status> data) {
        // https://www.zabbix.org/wiki/Docs/protocols/zabbix_sender/3.4
        // https://aoishi.hateblo.jp/entry/2017/12/03/014913

        StringBuilder builder = new StringBuilder();
        builder.append('{');
        builder.append("\"request\":\"sender data\",");
        builder.append("\"clock\":").append(System.currentTimeMillis() / 1000).append(',');

        builder.append("\"data\":[");
        boolean first = true;
        for (Status datum : data) {
            if (first) {
                first = false;
            } else {
                builder.append(',');
            }
            builder.append('{');

            // clock
            if (datum.clock != -1) { // -1 for LLD
                builder.append("\"clock\":");
                builder.append(datum.clock);
                builder.append(',');
            }
            // host
            builder.append("\"host\":\"");
            jsonEscape(datum.host, builder);
            builder.append("\",");
            // key
            builder.append("\"key\":\"");
            jsonEscape(datum.key, builder);
            builder.append("\",");
            // value
            builder.append("\"value\":\"");
            jsonEscape(datum.value, builder);
            builder.append('"');

            builder.append('}');
        }
        builder.append(']');

        builder.append('}');
        return builder.toString();
    }

    private static void jsonEscape(String str, StringBuilder builder) {
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
    }
}
