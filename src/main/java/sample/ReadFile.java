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
    File[] files_list = null;
    Parse parser;
    private ExecutorService pool;
    private Object syncObject;
    private static AtomicInteger count = new AtomicInteger(0);


    public ReadFile(String path) {
//        File corpus = new File(ClassLoader.getSystemResource("corpus").getPath());
        File corpus = new File(path);
        files_list = corpus.listFiles();
        parser = new Parse(files_list.length, corpus.getName());
        pool = Executors.newFixedThreadPool(8);
    }

    public void readFiles() {
        syncObject = new Object();
        count.addAndGet(files_list.length);
        for (File file : this.files_list) {
            pool.execute(new Reader(file));
        }
        synchronized (syncObject) {
            try {
                syncObject.wait();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                pool.shutdown();
            }
        }
    }

    class Reader implements Runnable {

        File file;

        public Reader(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            read(file);
        }

        public void read(File file) {
            Document document = null;
            try {
                document = Jsoup.parse(new String(Files.readAllBytes(file.listFiles()[0].toPath())));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("---------------read line 44-------------------");
            }
            Elements docElements = document.getElementsByTag("DOC");
            document = null;
            cDocument[] docToParse = new cDocument[docElements.size()];
            int placeInDoc = 0;
            for (Element element : docElements) {
                Elements IDElement = element.getElementsByTag("DOCNO");
                Elements TitleElement = element.getElementsByTag("TI");
                Elements TextElement = element.getElementsByTag("TEXT");
                Elements fElements = element.getElementsByTag("F");
                String city = "";
                String language = "";

                for (Element fElement : fElements) {
                    if (fElement.attr("P").equals("104")) {
                        city = fElement.text();
                        //Todo more about the city (restcountries.eu API)
                    } else if (fElement.attr("P").equals("105"))
                        language = fElement.text();
                }

                String ID = IDElement.text();
                String title = TitleElement.text();
                String text = TextElement.text();
                cDocument cDoc = new cDocument(ID, title, text);
                cDoc.city = city;
                cDoc.language = language;
                docToParse[placeInDoc++] = cDoc;
            }
            parser.doParsing(docToParse);
            count.getAndDecrement();
            if (count.get() == 0) {
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }

        }
    }
}
