package jp.jyn.zabbigot.sender;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import static org.junit.Assert.*;

public class StatusTest {

    @Test
    public void toJsonTest() throws ParseException {
        JSONParser parser = new JSONParser();
        String jsonStr = Status.toJson(
            new Status("host", "test.key1", "value"),
            new Status("host", "test.key2", "\"/\\")
        );
        System.out.println(jsonStr);
        JSONObject json = (JSONObject) parser.parse(jsonStr);
        assertTrue(json.containsKey("request"));
        assertTrue(json.containsKey("clock"));
        assertTrue(json.containsKey("data"));

        for (Object data : ((JSONArray) json.get("data"))) {
            json = (JSONObject) data;
            assertEquals("host", json.get("host").toString());
            switch (json.get("key").toString()) {
                case "test.key1":
                    assertEquals("value", json.get("value").toString());
                    break;
                case "test.key2":
                    assertEquals("\"/\\", json.get("value").toString());
                    break;
                default:
                    fail();
            }
        }

        jsonStr = Status.toJson(new Status("host", "test.key3", "single"));
        System.out.println(jsonStr);
        json = (JSONObject) parser.parse(jsonStr);

        assertEquals(((JSONArray) json.get("data")).size(), 1);
    }
}
