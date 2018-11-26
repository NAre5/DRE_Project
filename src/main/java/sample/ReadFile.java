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


    public ReadFile(String path, Parse parser) {
        this.parser = parser;
        File corpus = new File(path);
        files_list = corpus.listFiles();
        pool = Executors.newFixedThreadPool(8);
    }

    public void readFiles() {
        syncObject = new Object();
        for (File file : this.files_list) {
            count.addAndGet(1);
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
                String ID = IDElement.text();
                String title = TitleElement.text();
                String text = TextElement.text();
                cDocument cDoc = new cDocument(ID, title, text);
                docToParse[placeInDoc++] = cDoc;
            }
            parser.parse(docToParse);
            count.getAndDecrement();
            if (count.get() == 0) {
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }

        }
    }
}
