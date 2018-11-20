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

public class ReadFile implements Runnable {
    //    static File[] files;
    static Queue<File> files_list = new LinkedList<>();
    static ExecutorService pool = Executors.newCachedThreadPool();
    static Parse parser = new Parse();

    public static void main(String[] args) throws IOException {
        ReadFile rf = new ReadFile("C:\\Users\\micha\\Desktop\\corpus");
        read();
    }

    public ReadFile(String path) {
        File corpus = new File(path);
        File[] files = corpus.listFiles();
        List<File> l = Arrays.asList(files);
        files_list.addAll(l);//check timing
    }

    public static cDocument read() {
        File file = files_list.poll();
//        System.out.println("-----------------------------"+files_list.size()+"---------------------------------");
        if (files_list.isEmpty())
            return new cDocument("all doc are passed",null, null);//TODO check if return a doc with null fields.
        Document document = null;
        try {
            document = Jsoup.parse(new String(Files.readAllBytes(file.listFiles()[0].toPath())));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("---------------read line 44-------------------");
        }
        Elements docElements = document.getElementsByTag("DOC");
        for (Element element : docElements) {
            Document documentFromElement = Jsoup.parse(new String(element.toString()));
            Elements IDElement = documentFromElement.getElementsByTag("DOCNO");
            Elements TitleElement = documentFromElement.getElementsByTag("TI");
            Elements TextElement = documentFromElement.getElementsByTag("TEXT");
            String ID = IDElement.text();
            String title = TitleElement.text();
            String text = TextElement.text();
            cDocument cDoc = new cDocument(ID, title, text);
//            DocumentBuffer.getInstance().getBuffer().add(cDoc);
            pool.execute(()->Parse.parse(cDoc.text));
        }
        return null;
    }

    @Override
    public void run() {
        int i=0;
        while (true) {
            cDocument doc = read();
//            System.out.println("readfile");
            if (doc==null) {
                continue;
            }
            if(doc.ID.equals("all doc are passed")&&doc.title == null) {
                DocumentBuffer.getInstance().getBuffer().add(new cDocument(null,null,null));
                //TODO maybe insert doc with parameter that say "all doc are passed"
                break;
            }
        }
//        System.out.println("*******************"+files_list.size()+"*************");
        pool.shutdown();
//        System.out.println("pool is shutdown?="+pool.isShutdown());
//        System.out.println("Threa active ="+Thread.activeCount());
    }
}
