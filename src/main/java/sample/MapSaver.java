package sample;

import java.io.*;
import java.util.*;

public class MapSaver {

    public static void save(HashMap<String, Integer> map, String path) {
        Properties properties = new Properties();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }
        try {
            properties.store(new FileOutputStream(path), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveReverse(HashMap<String, Integer> map, String path) {
        Properties properties = new Properties();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            properties.put(entry.getValue(),entry.getKey());
        }
        try {
            properties.store(new FileOutputStream(path), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
