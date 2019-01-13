package jp.jyn.zabbigot.sender;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@FunctionalInterface
public interface StatusSender {
    SendResult send(Collection<Status> data);

    default SendResult send(Status... data) {
        return this.send(Arrays.asList(data));
    }

    class SendResult {
        public Map<String, String> data = new LinkedHashMap<>();
        public String response;

        public SendResult() { }

        public SendResult(String response) {
            this.response = response;
        }
    }
}
