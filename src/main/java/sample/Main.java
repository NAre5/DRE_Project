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
//        ExecutorService parsers_pool = Executors.newCachedThreadPool();
//        parsers_pool.execute(new ReadFile("C:\\Users\\micha\\Desktop\\corpus"));
//        parsers_pool.execute(new Parse());
        long starttime = System.currentTimeMillis();
        ReadFile rf= new ReadFile("C:\\Users\\erant\\Desktop\\STUDIES\\corpus\\tcorpus");
        rf.readFiles();
        System.out.println("The time is " + (System.currentTimeMillis()-starttime)/1000);


    }


    public static void main(String[] args) {
        launch(args);
    }


}
