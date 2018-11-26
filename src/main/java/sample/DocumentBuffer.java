package sample;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class DocumentBuffer {
    private static DocumentBuffer ourInstance = new DocumentBuffer();
    static Queue<cDocument> documentBuffer;

    public static DocumentBuffer getInstance() {
        return ourInstance;
    }

    private DocumentBuffer() {
        documentBuffer = new ConcurrentLinkedQueue<>();
    }

    public static Queue<cDocument> getBuffer()
    {
        return documentBuffer;
    }

}
