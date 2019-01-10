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
    public String send(Collection<Status> data) {
        try (BufferedWriter writer = Files.newBufferedWriter(
            file, StandardCharsets.UTF_8,
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )) {

            String json = Status.toJson(data);
            writer.write(json);
            writer.newLine();

            return json;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}