package sample;

import javafx.util.Pair;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Indexer {
    //    public HashMap<Indexer,HashSet<String>> ddd;
    String d_path;//directory path
    HashMap<String, Integer> dictionary;
    private ExecutorService pool;
    HashMap<String, File_db> mapper;//filename to file_db thread

    public Indexer(String diretory_name) throws FileAlreadyExistsException {
        pool = Executors.newFixedThreadPool(28);
        d_path = diretory_name;
        File dir = new File(diretory_name);
        if (dir.exists())
            throw new FileAlreadyExistsException("");
        Boolean b = dir.mkdirs();//remove b=

        //create all files, their db thread and connect them with mapper
        File file;
        File_db fdb;
        for (char c = 'a'; c <= 'z'; c++) {
            file = new File(dir, c + ".txt");
            try {
                b = file.createNewFile();//remove b=
            } catch (IOException e) {
                e.printStackTrace();
            }
            fdb = new File_db(file);
            mapper.put(String.valueOf(c), fdb);
            pool.execute(fdb);
        }
        file = new File(dir, "_.txt");
        try {
            b = file.createNewFile();//remove b=
        } catch (IOException e) {
            e.printStackTrace();
        }
        fdb = new File_db(file);
        mapper.put("_", fdb);
        pool.execute(fdb);
        pool.execute(new File_db(file));
        file = new File(dir, "cities.txt");
        try {
            b = file.createNewFile();//remove b=
        } catch (IOException e) {
            e.printStackTrace();
        }
        fdb = new File_db(file);
        mapper.put("cities", fdb);
        pool.execute(fdb);
        pool.execute(new File_db(file));
    }

    public void andex(cDocument document) {
//        Thread t = new Thread(new mehandex(map, new File("")));
//        t.start();
        Object[] objects = new Object[]{""};
        if (document.city != null)
            mapper.get("cities").queue.add(new Pair<>(document.city, objects));
        for (String term : document.terms.keySet()) {
            objects = new Object[]{document.ID, document.terms.get(term)};
            mapper.get(term.toLowerCase().substring(0, 1)).queue.add(new Pair<>(term, objects));
        }
        for (String term : document.terms_s.keySet()) {
            objects = new Object[]{document.ID, document.terms_s.get(term)};
            mapper.get(term.toLowerCase().substring(0, 1)).queue.add(new Pair<>(term, objects));
        }
    }

//    File getDir(String s) {
//        if (Character.isLetter(s.charAt(0)))
//            return new File(d_path,String.valueOf(s.charAt(0)).toLowerCase());
//        else
//            return new File(d_path,"_");
//    }

    //    class mehandex implements Runnable {
//
//        private HashMap<String, List<Object>> map;
//        File directory;
//        mehandex(HashMap<String, List<Object>> map, File directory) {
//            this.map = map;
//        }
//
//        @Override
//        public void run() {
//
//        }
//
//
//    }
    private static final Integer LINE_SIZE = 5; // +1 for \n

    class File_db implements Runnable {
        File db_file;
        HashMap<String, Integer> firstTermLine = new HashMap<>();
        HashMap<String, Integer> lastTermLine = new HashMap<>();
        HashMap<String, Integer> df = new HashMap<>();
        HashMap<String,Integer> docIDMap = new HashMap<>();

        Integer next_line = 0;

        Queue<Pair<String, Object[]>> queue = new ConcurrentLinkedQueue<>();

        FileOutputStream foStream;
        RandomAccessFile raFile;

        File_db(File db_file) {
            this.db_file = db_file;
            try {
                foStream = new FileOutputStream(db_file, true);
                raFile = new RandomAccessFile(db_file, "rw");
            } catch (IOException e) {
                e.printStackTrace();//basa
            }
        }


        @Override
        public void run() {
            while (true) {
                Pair<String, Object[]> pair = queue.poll();//if null? if error?//always first is docID
                Object docno = pair.getValue()[0];
                Integer currentID;
                if((currentID = docIDMap.get(docno))==null)
                {
                    currentID = docIDMap.put(docno.toString(),docIDMap.size());//TODO check docno.toString()
                }

                String term = pair.getKey();
                df.put(term, df.getOrDefault(term, 0) + 1);//TODO check
                Integer ll;
                if ((ll = lastTermLine.get(term)) != null) {
                    //insert
                    try {
                        raFile.seek(ll * LINE_SIZE);//TODO check that seek is from the start of the file
                        raFile.write(ByteBuffer.allocate(4).putInt(next_line).array());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    firstTermLine.put(term, next_line);
                }
                lastTermLine.put(term, next_line++);
                try {
                    foStream.write(new byte[]{});//TODO insert real data
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;//TODO DELETE The break;
                //gaps?//
            }
            MapSaver.save(firstTermLine, "");
            MapSaver.save(df, "");
            MapSaver.saveReverse(docIDMap,"");

//            raFile.close();//Todo michael fix like ATP
//            writer.close();
        }
    }
}
