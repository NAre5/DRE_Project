package com;

import com.jfoenix.controls.JFXTogglePane;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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

//        TableView<String> stringTableView = new TableView<>();
//        stringTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        TableColumn<String,String> tableColumn = new TableColumn<>();
//        tableColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()));
//        stringTableView.getColumns().add(tableColumn);
//        stringTableView.getItems().addAll("dw","dww","dwww");
//        VBox vBox = new VBox();
//        vBox.getChildren().addAll(new Button(),stringTableView);
////        TitledPane titledPane = new TitledPane();
//////        titledPane.getChildrenUnmodifiable().addAll(new Button(),stringTableView);
////        titledPane.setContent(stringTableView);
////        anchorPane.getChildren().add(new Button());
////        anchorPane.getChildren().add(stringTableView);
//        Scene scene = new Scene(vBox);
//        Stage stage = new Stage();
//        stage.setScene(scene);
//        stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }


}
