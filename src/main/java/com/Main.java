package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(this.getClass().getResource("GUI.fxml"));
        primaryStage.setTitle("DRE Project");
        primaryStage.setScene(new Scene(root, 600, 700));
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("icon.png")));
        primaryStage.setResizable(true);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }


}
