package sample;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Indexer {
    //    public HashMap<Indexer,HashSet<String>> ddd;
    String d_path;//directory path
    HashMap<String, Integer> dictionary;


    public Indexer(String diretory_name) throws FileAlreadyExistsException {
        d_path = diretory_name;
        File dir = new File(diretory_name);
        if (dir.exists())
            throw new FileAlreadyExistsException("");
        dir.mkdirs();
        for (char c = 'a'; c <= 'z'; c++) {
            File file = new File(dir, c + ".txt");
            file.mkdirs();
        }
        new File(dir, "_" + ".txt").mkdirs();
    }

    public void andex(HashMap<String, List<Object>> map) {
        Thread t = new Thread(new mehandex(map, new File("")));
        t.start();

    }

    File getDir(String s) {
        if (Character.isLetter(s.charAt(0)))
            return new File(d_path,String.valueOf(s.charAt(0)).toLowerCase());
        else
            return new File(d_path,"_");
    }

    class mehandex implements Runnable {

        private HashMap<String, List<Object>> map;
        File directory;
        mehandex(HashMap<String, List<Object>> map, File directory) {
            this.map = map;
        }

        @Override
        public void run() {

        }


    }

    class file_db implements Runnable{
        File db_file;
        Queue<String> queue = new ConcurrentLinkedQueue<>();

        public file_db(File db_file)
        {
            this.db_file = db_file;
        }


        @Override
        public void run() {

        }
    }
}
