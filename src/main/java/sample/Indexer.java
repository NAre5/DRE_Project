package sample;

import com.sun.deploy.util.ArrayUtil;
import javafx.util.Pair;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Indexer {
    //    public HashMap<Indexer,HashSet<String>> ddd;
    String d_path;//directory path
    HashMap<String, Integer> dictionary;
    private ExecutorService pool = Executors.newFixedThreadPool(28);;
    private ExecutorService doAndex_pool= Executors.newFixedThreadPool(8);;//Executors.newCachedThreadPool();
    HashMap<String, File_db> mapper = new HashMap<>();//filename to file_db thread

    public Indexer(String diretory_name) throws FileAlreadyExistsException {
        d_path = diretory_name;
        File dir = new File(diretory_name);
        if (dir.exists())
            throw new FileAlreadyExistsException("");
        Boolean b = dir.mkdirs();//remove b=

        //create all files, their db thread and connect them with mapper
        File file;
        File_db fdb;
        for (char c = 'a'; c <= 'z'; c++) {
            file = new File(dir, c+"");
            try {
                b = file.createNewFile();//remove b=
            } catch (IOException e) {
                e.printStackTrace();
            }
            fdb = new File_db(file);
            mapper.put(String.valueOf(c), fdb);
            pool.execute(fdb);
        }
        file = new File(dir, "_");
        try {
            b = file.createNewFile();//remove b=
        } catch (IOException e) {
            e.printStackTrace();
        }
        fdb = new File_db(file);
        mapper.put("_", fdb);
        pool.execute(fdb);
        pool.execute(new File_db(file));
        file = new File(dir, "cities");
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

    public void doAndexing(cDocument document) {
        doAndex_pool.execute(new doAndex(this,document));
    }

    class doAndex implements Runnable{
        Indexer indexer;
        cDocument document;

        public doAndex(Indexer indexer, cDocument document) {
            this.indexer = indexer;
            this.document = document;
        }

        @Override
        public void run() {
            indexer.andex(document);
        }
    }

    public void andex(cDocument document) {
        Object[][] objects = new Object[][]{};
//        if (document.city != null)
//            mapper.get("cities").queue.add(new Pair<>(document.city, objects));
        if(document.ID.equals("shutdown"))
        {
            doAndex_pool.shutdown();
            objects = new Object[][]{{"","shutdown"},{}};
            for (File_db file_db : mapper.values()) {
                file_db.queue.add(new Pair<>("",objects));
            }
            pool.shutdown();
            return;
        }
        for (String term : document.terms.keySet()) {
            objects = new Object[][]{{document.ID, 3}, {document.terms.get(term), 2}};
            try {
                String first = term.toLowerCase().substring(0, 1);
                String key = (mapper.containsKey(first)) ? first : "_";
                mapper.get(key).queue.add(new Pair<>(term, objects));
            } catch (Exception e) {
                System.out.println("but why?");
            }
        }
//        for (String term : document.terms_s.keySet()) {
//            objects = new Object[][]{{document.ID, 3}, {document.terms_s.get(term), 2}};
//            mapper.get(term.toLowerCase().substring(0, 1)).queue.add(new Pair<>(term, objects));
//        }
    }

    static byte[] intToBytes(int number, int num_of_bytes) {
        byte[] ans = new byte[num_of_bytes];
        for (int i = num_of_bytes; i > 0; i--) {
            ans[num_of_bytes - i] = (byte) (number >> ((i - 1) * 8));
//            number >>= 8;
        }
        return ans;
    }


    private static final Integer LINE_SIZE = 9; // +1 for \n



    class File_db implements Runnable {
        File db_file;
        HashMap<String, Integer> firstTermLine = new HashMap<>();
        HashMap<String, Integer> lastTermLine = new HashMap<>();
        HashMap<String, Integer> df = new HashMap<>();
        HashMap<String, Integer> docIDMap = new HashMap<>();

        Integer next_line = 0;

        Queue<Pair<String, Object[][]>> queue = new ConcurrentLinkedQueue<>();

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
                Pair<String, Object[][]> pair = queue.poll();//if null? if error?//always first is docID
                if (pair == null)
                    continue;
                if(pair.getValue()[0][1].toString().equals("shutdown"))
                    break;
                Object docno = null;
                try {
                    docno = pair.getValue()[0][0];
                } catch (Exception e) {
                    System.out.println("but why?");
                }
//                Integer currentID;
                try {
                    if ((pair.getValue()[0][0] = docIDMap.get(docno.toString())) == null) {
                        int file_id = docIDMap.size();
                        docIDMap.put(docno.toString(), docIDMap.size());//TODO check docno.toString()
                        pair.getValue()[0][0] = file_id;
                    }
                } catch (Exception e) {
                    System.out.println("but why?");
                }


                String term = pair.getKey();
                df.put(term, df.getOrDefault(term, 0) + 1);//TODO check
                Integer ll;
                if ((ll = lastTermLine.get(term)) != null) {
                    //insert
                    try {
                        raFile.seek((ll * LINE_SIZE) + 5);//TODO check that seek is from the start of the file
                        raFile.write(intToBytes(3, next_line));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    firstTermLine.put(term, next_line);
                }
                lastTermLine.put(term, next_line++);
                try {
                    byte[] arr = new byte[0];
                    for (Object[] object : pair.getValue()) {
                        arr = ArrayUtils.addAll(arr, (object[0] instanceof Integer) ?
                                intToBytes(((Integer) object[0]), ((Integer) object[1])) :
                                ((String) object[0]).getBytes(StandardCharsets.UTF_8));
                    }
//                    byte[] arrTF = intToBytes(,2);
                    arr = ArrayUtils.addAll(arr, new byte[]{0, 0, 0});
                    arr = ArrayUtils.add(arr, (byte) '\n');
                    foStream.write(arr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                break;//TODO DELETE The break;
                //gaps?//
            }
            MapSaver.save(firstTermLine, db_file.getPath()+ "firstTermLine");
            MapSaver.save(df, db_file.getPath()+ "df");
            MapSaver.saveReverse(docIDMap, db_file.toPath()+ "docIDMap");

//            raFile.close();//Todo michael fix like ATP
//            writer.close();
        }
    }
}
