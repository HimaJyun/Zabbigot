package jp.jyn.zabbigot.sender.imple;

import jp.jyn.zabbigot.sender.StatusSender;
import jp.jyn.zabbigot.sender.Status;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class ZabbixSender implements StatusSender {
    private final static byte[] HEADER = {'Z', 'B', 'X', 'D', '\1'};
    private final static int BIT_LENGTH = 8;
    private final static int BYTE64_LENGTH = 64 / BIT_LENGTH;

    private final String host;
    private final int port;
    private final static int timeout = 3 * 1000;

    public ZabbixSender(String host) {
        this(host, 10051);
    }

    public ZabbixSender(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String send(Collection<Status> data) {
        byte[] body = toBytes(Status.toJson(data).getBytes());
        try (Socket socket = new Socket()) {
            socket.setSoTimeout(timeout);
            socket.connect(new InetSocketAddress(host, port), timeout);
            try (InputStream in = socket.getInputStream();
                 OutputStream out = socket.getOutputStream()) {
                out.write(body);
                out.flush();

                byte[] buffer = new byte[512];
                int read = 0, count = 0;
                while (true) {
                    read = in.read(buffer, count, buffer.length - count);
                    if (read <= 0) {
                        break;
                    }
                    count += read;
                }

                if (count < 13) {
                    return "[]";
                }
                return new String(buffer, HEADER.length + BYTE64_LENGTH, count - (HEADER.length + BYTE64_LENGTH), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] toBytes(byte[] jsonBytes) {
        // https://www.zabbix.org/wiki/Docs/protocols/zabbix_sender/1.8/java_example
        // https://siguniang.wordpress.com/2012/10/13/notes-on-zabbix-1-4-protocol/
        // https://github.com/hengyunabc/zabbix-sender/
        byte[] result = new byte[HEADER.length + BYTE64_LENGTH + jsonBytes.length];

        // write header
        System.arraycopy(HEADER, 0, result, 0, HEADER.length);

        // write length(64bit little-endian)
        result[HEADER.length] = (byte) (jsonBytes.length & 0xFF);
        result[HEADER.length + 1] = (byte) ((jsonBytes.length >> 8) & 0x00FF);
        result[HEADER.length + 2] = (byte) ((jsonBytes.length >> 16) & 0x0000FF);
        result[HEADER.length + 3] = (byte) ((jsonBytes.length >> 24) & 0x000000FF);
        //result[HEADER.length + 4...7] = 0;

        // write body
        System.arraycopy(jsonBytes, 0, result, HEADER.length + BYTE64_LENGTH, jsonBytes.length);
        return result;
    }

}
