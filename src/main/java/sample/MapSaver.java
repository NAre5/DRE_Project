package sample;

import java.io.*;
import java.util.*;

public class MapSaver {

    public static void save(HashMap<String, Integer> map, String path) {
        path = path + ".properties";
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Properties properties = new Properties();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            properties.put(entry.getKey(), entry.getValue().toString());
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(path);
            if (properties.size()!=0)
                properties.store(fout, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveReverse(HashMap<String, Integer> map, String path) {
        path = path + ".properties";
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Properties properties = new Properties();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            properties.put(entry.getValue().toString(), entry.getKey());
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(path);
            if (properties.size()!=0)
                properties.store(fout, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
