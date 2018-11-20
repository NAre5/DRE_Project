package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {

    AtomicInteger fileNum = new AtomicInteger();

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
//        ExecutorService pool = Executors.newCachedThreadPool();
//        pool.execute(new ReadFile("C:\\Users\\micha\\Desktop\\corpus"));
//        pool.execute(new Parse());
        long starttime = System.currentTimeMillis();
        ReadFile rf= new ReadFile("C:\\Users\\erant\\Desktop\\STUDIES\\corpus\\corpus");
//        Parse parse = new Parse();
        Thread t1 = new Thread(rf);
//        Thread t4 = new Thread(rf);
//        Thread t2 = new Thread(parse);
//        Thread t3 = new Thread(parse);
//        Thread t5 = new Thread(parse);
        t1.start();
//        t2.start();
//        t3.start();
//        t4.start();
//        t5.start();
        t1.join();
//        t2.join();
//        t3.join();
//        t4.join();
//        t5.join();
        System.out.println("The time is " + (System.currentTimeMillis()-starttime)/1000);


    }


    public static void main(String[] args) {
        launch(args);
    }


}
