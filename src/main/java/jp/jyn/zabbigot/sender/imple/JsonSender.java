package jp.jyn.zabbigot.sender.imple;

import jp.jyn.zabbigot.sender.Status;
import jp.jyn.zabbigot.sender.StatusSender;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

public class JsonSender implements StatusSender {
    private final Path file;

    public JsonSender(Path file) {
        this.file = file;
    }

    @Override
    public SendResult send(Collection<Status> data) {
        try (BufferedWriter writer = Files.newBufferedWriter(
            file, StandardCharsets.UTF_8,
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )) {

            SendResult result = toJson(data);
            writer.write(result.response);
            writer.newLine();

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SendResult toJson(Iterable<Status> data) {
        SendResult result = new SendResult();
        StringBuilder builder = new StringBuilder();
        builder.append('{');

        boolean first = true;
        for (Status datum : data) {
            if (first) {
                first = false;
            } else {
                builder.append(',');
            }

            String value = datum.value.get();

            Status.jsonStr(datum.key, builder).append(':');
            Status.jsonStr(value, builder);
            result.data.put(datum.key, value);
        }

        builder.append('}');
        result.response = builder.toString();
        return result;
    }
}
