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
        ReadFile rf= new ReadFile("C:\\Users\\micha\\OneDrive\\מסמכים\\michael\\שנה ג\\אחזור מידע\\corpus",new Parse());
        rf.readFiles();
        System.out.println("The time is " + (System.currentTimeMillis()-starttime)/1000);


    }


    public static void main(String[] args) {
        launch(args);
    }


}
