package sample;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ReadFile {
    //    static File[] files;
    File[] files_list = null;
    //    ExecutorService pool = Executors.newCachedThreadPool();
    Parse parser;
    private ExecutorService pool;
    private Object syncObject;
    private static AtomicInteger count=new AtomicInteger(0);

    public static void main(String[] args) throws IOException {
//        ReadFile rf = new ReadFile("C:\\Users\\micha\\Desktop\\corpus");
//        read();
    }

    public ReadFile(String path, Parse parser) {
        this.parser = parser;
        File corpus = new File(path);
        files_list = corpus.listFiles();
        pool=Executors.newFixedThreadPool(8);
    }


//    public cDocument read(File file) {
////        File file = files_list.poll();
//////        System.out.println("-----------------------------"+files_list.size()+"---------------------------------");
////        if (files_list.isEmpty())
////            return new cDocument("all doc are passed", null, null);//TODO check if return a doc with null fields.
//        Document document = null;
//        try {
//            document = Jsoup.parse(new String(Files.readAllBytes(file.listFiles()[0].toPath())));
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("---------------read line 44-------------------");
//        }
//        Elements docElements = document.getElementsByTag("DOC");
//
//        for (Element element : docElements) {
//            Document documentFromElement = Jsoup.parse(new String(element.toString()));
//            Elements IDElement = documentFromElement.getElementsByTag("DOCNO");
//            Elements TitleElement = documentFromElement.getElementsByTag("TI");
//            Elements TextElement = documentFromElement.getElementsByTag("TEXT");
//            String ID = IDElement.text();
//            String title = TitleElement.text();
//            String text = TextElement.text();
//            cDocument cDoc = new cDocument(ID, title, text);
//            DocumentBuffer.getInstance().getBuffer().add(cDoc);
//
////            pool.execute(parser);
//        }
////        parser.parse(docElements.size());
//        return null;
//    }
    public void readFiles() {
        syncObject = new Object();
        for (File file : this.files_list) {
            count.addAndGet(1);
            pool.execute(new Reader(file));
        }
        synchronized(syncObject) {
            try {
                syncObject.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                pool.shutdown();
            }
        }

//        pool.shutdown();
//        System.out.println("pool is shutdown?="+pool.isShutdown());
//        System.out.println("Threa active ="+Thread.activeCount());
    }
    class Reader implements Runnable{

        File file;
        public Reader(File file)
        {
            this.file = file;
        }
        @Override
        public void run() {
            read(file);
        }
        public void read(File file) {
//        File file = files_list.poll();
////        System.out.println("-----------------------------"+files_list.size()+"---------------------------------");
//        if (files_list.isEmpty())
//            return new cDocument("all doc are passed", null, null);//TODO check if return a doc with null fields.
            long start = System.currentTimeMillis();
            Document document = null;
            try {
                document = Jsoup.parse(new String(Files.readAllBytes(file.listFiles()[0].toPath())));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("---------------read line 44-------------------");
            }
            Elements docElements = document.getElementsByTag("DOC");
            document = null;

            for (Element element : docElements) {
                Document documentFromElement = Jsoup.parse(new String(element.toString()));
                Elements IDElement = documentFromElement.getElementsByTag("DOCNO");
                Elements TitleElement = documentFromElement.getElementsByTag("TI");
                Elements TextElement = documentFromElement.getElementsByTag("TEXT");
                String ID = IDElement.text();
                String title = TitleElement.text();
                String text = TextElement.text();
                cDocument cDoc = new cDocument(ID, title, text);
//                DocumentBuffer.getInstance().getBuffer().add(cDoc);

//            pool.execute(parser);
            }
//        parser.parse(docElements.size());
            count.getAndDecrement();
            if(count.get()==0)
            {
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
//            System.out.println(System.currentTimeMillis()-start);
//            return null;
        }
    }
}
