package jp.jyn.zabbigot.sender;

import java.util.Arrays;
import java.util.Collection;

@FunctionalInterface
public interface Sender {
    String send(Collection<Status> data);

    default String send(Status... data) {
        return this.send(Arrays.asList(data));
    }
}
