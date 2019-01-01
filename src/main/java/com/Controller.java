package com;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.util.Callback;
import javafx.util.Pair;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Controller {

    public Button fileChooser_postings_in;
    public TextField text_postings_in;
    public TextField text_queries_path;
    public Button fileChooser_queries_file;
    public Button button_search_queries_file;
    public Button button_search_query;
    public CheckComboBox<String> comboBox_cities;
    Model model = new Model();
    public Button fileChooser_stop_words;
    public Button fileChooser_postings_out;
    public Button fileChooser_corpus;
    public Button button_reset;
    public Button button_showDictionary;
    public Button button_loadDictionary;
    public TextField text_stop_words;
    public TextField text_postings_out;
    public TextField text_corpus;
    public TextField text_query;
    public CheckBox checkBox_stemming_IN;
    public CheckBox checkBox_stemming_Q;
    public CheckBox checkBox_semantic;
    public GridPane data;
    public CheckComboBox<String> comboBox_languages;

    private String lastPath;
    public Map<String, String> map;


    @FXML
    public void initialize() {
        button_reset.setDisable(true);
        button_loadDictionary.setDisable(true);
        button_showDictionary.setDisable(true);
        text_queries_path.setText("C:\\Users\\erant\\Desktop\\STUDIES\\corpus\\queries.txt");
    }

    /**
     * e
     * This function we choose the stop words file
     *
     * @param actionEvent - press on fileChooser_stop_words
     */
    public void choose_stop_words_file(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(fileChooser_stop_words.getScene().getWindow());
        if (file == null)
            return;
        text_stop_words.setText(file.getPath());
    }

    /**
     * This function we choose the directory we write to ut the postings files
     *
     * @param actionEvent - press on fileChooser_postings
     */
    public void choose_postings_file(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(fileChooser_postings_out.getScene().getWindow());
        if (file == null)
            return;
        text_postings_out.setText(file.getPath());
    }

    /**
     * This function we choose the corpus directory
     * //     * @param actionEvent - press on fileChooser_corpus
     * //
     */
    public void choose_corpus(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(fileChooser_corpus.getScene().getWindow());
        if (file == null)
            return;
        text_corpus.setText(file.getPath());
    }

    /**
     * This function start the indexing process
     *
     * @param actionEvent - press on create button
     */
    public void createInvertedIndex(ActionEvent actionEvent) {
        if (text_corpus.getText().equals("") || text_postings_out.getText().equals("") || text_stop_words.getText().equals("")) {//check that all of fields are not empty
            showAlert(Alert.AlertType.ERROR, "Please fill all paths");
        } else {
//            progressBar.setProgress(0);
//            progressBar.setVisible(true);
            if (!new File(text_corpus.getText()).exists()) {
                showAlert(Alert.AlertType.ERROR, "corpus text: illegal path");
                return;
            }
            if (!new File(text_stop_words.getText()).exists()) {
                showAlert(Alert.AlertType.ERROR, "stop_words text: illegal path");
                return;
            }
            if (!new File(text_postings_out.getText()).exists()) {
                showAlert(Alert.AlertType.ERROR, "postings text: illegal path");
                return;
            }
            lastPath = text_postings_out.getText();//save the last path of the last time we save the dictionary
            Stage waitStage = new Stage();
            raiseWaitPage(waitStage);
            Thread indexThread = new Thread(() -> {
                long startTime = System.nanoTime();//start to calculate how much the the process takes
                model.startIndexing(text_corpus.getText(), text_stop_words.getText(), text_postings_out.getText(), checkBox_stemming_IN.isSelected());
                long CreateIndexTime = (System.nanoTime() - startTime) / 1000000000;
                button_reset.setDisable(false);//after indexing w can reset the files
                button_loadDictionary.setDisable(false);
                button_showDictionary.setDisable(false);
                int numberOfindexDoc = model.readFile.parser.indexer.docAndexed.get();
                int uniqueTerm = model.readFile.parser.indexer.uniqueTerm.get();
                StringBuilder showText = new StringBuilder();
                showText.append("The numbers of documents indexed: ").append(numberOfindexDoc).append("\n")
                        .append("The number of unique terms: ").append(uniqueTerm).append("\n").append("The time is takes: ").append(CreateIndexTime).append(" sec");
                model.initSearch(lastPath + "\\" + (checkBox_stemming_IN.isSelected() ? "stem" : "nostem"));
                setDisableToFalse();
                comboBox_cities.getItems().clear();
//            comboBox_cities.getItems().
                Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, showText.toString()));
                Platform.runLater(waitStage::close);
            });
            indexThread.start();
//            progressBar.setVisible(false);
        }
    }

    private void raiseWaitPage(Stage waitStage) {
//        waitStage.initStyle(StageStyle.UNDECORATED);
        try {
            Parent waitParent = FXMLLoader.load(this.getClass().getResource("waitPage.fxml"));
            waitStage.setScene(new Scene(waitParent));
//            waitStage.getIcons().add(new Image(this.getClass().getResourceAsStream("tenor.gif")));
            waitStage.setResizable(false);
//            waitStage.initModality(Modality.APPLICATION_MODAL);
//            waitStage.setAlwaysOnTop(true);
            waitStage.setOnCloseRequest(event -> event.consume());
            waitStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * generic function to show alert
     *
     * @param type - thr type of the alert
     * @param text - the context of the alert
     */
    private void showAlert(Alert.AlertType type, String text) {
        Alert alert = new Alert(type);
        alert.setContentText(text);
        alert.show();
    }

    public void loadDictionary(ActionEvent actionEvent) {
        if (lastPath != null) {
            map = model.getDictionary();
            showAlert(Alert.AlertType.INFORMATION, "done loading");
        } else
            showAlert(Alert.AlertType.ERROR, "You need to start indexing before you can load dictionary.");
    }

    /**
     * show the dictionay that loaded from the last path
     * because it's took many time to load all the dic and show we show every time 50 term
     * if press on "see more" we show 50 more (and also the previous 50 terms
     *
     * @param actionEvent - press on show_dictionary
     */
    public void showDictionary(ActionEvent actionEvent) {
        if (map == null) {
            showAlert(Alert.AlertType.ERROR, "you need to load the dictionary before");
            return;
        }
        Stage stage = new Stage();
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("icon.png")));
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setTitle("Dictionary");
        stage.initModality(Modality.APPLICATION_MODAL);
        ScrollPane scrollPane = new ScrollPane();
        TableView<Pair<String, String>> dictionary = new TableView<>();
        dictionary.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn term = new TableColumn<>("Term");
        TableColumn cf = new TableColumn<>("Cf");

        term.setCellValueFactory(new PropertyValueFactory<Pair<String, String>, String>("key"));
        cf.setCellValueFactory(new PropertyValueFactory<Pair<String, String>, Button>("value"));

        for (Map.Entry<String, String> entry : map.entrySet()) {
            dictionary.getItems().add(new Pair<>(entry.getKey(), entry.getValue()));
        }

        dictionary.getColumns().addAll(term, cf);
        scrollPane.setContent(dictionary);
        dictionary.setPrefHeight(600);
        Scene scene = new Scene(scrollPane, dictionary.getMinWidth(), dictionary.getPrefHeight());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * reset the output dictionary and map
     *
     * @param actionEvent
     */
    public void reset(ActionEvent actionEvent) {
        model.reset();
//        ProgressBar progressBar = new ProgressBar(0);
        button_reset.setDisable(true);
        button_loadDictionary.setDisable(true);
        button_showDictionary.setDisable(true);
        comboBox_languages.getItems().clear();
        comboBox_languages.setDisable(true);
        showAlert(Alert.AlertType.INFORMATION, "done reset");
//        progressBar.setProgress();
    }

    public void choose_postings_file_and_load(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(fileChooser_postings_in.getScene().getWindow());
        if (file == null)
            return;
        text_postings_in.setText(file.getPath());
        //Todo open "wait..." window with text: "loading..."
//        lastPath = text_postings_in.getText();
        Stage waitStage = new Stage();
        raiseWaitPage(waitStage);
        Thread initThread = new Thread(() -> {
            model.initSearch(file.getPath());
            for (String city : model.searcher.cities) {//show the city
                comboBox_cities.getItems().add(city);
            }
            for (String language : model.searcher.languages) {
                comboBox_languages.getItems().add(language);
            }
            setDisableToFalse();
            Platform.runLater(waitStage::close);
//            waitStage.close();
        });
        initThread.start();
//        try {
//            initThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        model.initSearch(file.getPath());

    }

    public void search_query(ActionEvent actionEvent) {
        ObservableList<String> cities_Chosen = comboBox_cities.getCheckModel().getCheckedItems();
        HashSet<String> cities = new HashSet<>(cities_Chosen);
        ObservableList<String> languages_Chosen = comboBox_languages.getCheckModel().getCheckedItems();
        HashSet<String> languages = new HashSet<>(languages_Chosen);
        Map<String, List<Pair<String, String[]>>> results = model.searchByQuery(text_query.getText(), checkBox_stemming_Q.isSelected(), checkBox_semantic.isSelected(), cities, languages);
        TableView<Map.Entry<String, List<Pair<String, String[]>>>> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Map.Entry<String, List<Pair<String, String[]>>>, String> queryNum = new TableColumn<>("query num");
        queryNum.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));

        TableColumn<Map.Entry<String, List<Pair<String, String[]>>>, String> seeMore_buttons = new TableColumn<>();//Button

        seeMore_buttons.setCellFactory(param -> new TableCell<Map.Entry<String, List<Pair<String, String[]>>>, String>() {

            final Button btn = new Button("see more");

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    btn.setOnAction(event -> {
                        Map.Entry<String, List<Pair<String, String[]>>> query = getTableView().getItems().get(getIndex());
                        Stage stage = new Stage();
                        stage.setAlwaysOnTop(false);
                        stage.setResizable(false);
                        stage.setTitle("query " + query.getKey());
                        stage.initModality(Modality.APPLICATION_MODAL);
                        ScrollPane scrollPane = new ScrollPane();
                        TableView<Pair<String, String[]>> queryTable = getQueryTable();
                        queryTable.getItems().addAll(query.getValue());
                        scrollPane.setContent(queryTable);
                        Scene scene = new Scene(scrollPane);
                        stage.setScene(scene);
                        stage.show();
                    });
                    setGraphic(btn);
                    setText(null);
                }
            }
        });

        tableView.getColumns().addAll(queryNum, seeMore_buttons);
        tableView.getItems().addAll(results.entrySet());
        Stage stage = new Stage();
        stage.setResizable(true);
        VBox vBox = new VBox();
        Button button = new Button("save queries results");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("queries_results");
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("txt", ".txt"));
            File file = fileChooser.showSaveDialog(button.getScene().getWindow());
            if (file == null)
                return;
            model.saveQueryOutput(results, file);
        });
        button.setPrefWidth(vBox.getMaxWidth());
        button.setMaxWidth(vBox.getMaxWidth());
//        button.setMaxWidth(vBox.widthProperty().doubleValue());
        vBox.getChildren().addAll(button, tableView);
        stage.setScene(new Scene(vBox));
        stage.setTitle(results.size() + " query's results");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public void open_fileChooser_queries_file(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(fileChooser_queries_file.getScene().getWindow());
        if (file == null)
            return;
        text_queries_path.setText(file.getPath());
        button_search_queries_file.setDisable(false);
    }

    private TableView<Pair<String, String[]>> getQueryTable() {
        TableView<Pair<String, String[]>> queryTable = new TableView<>();
        queryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Pair<String, String[]>, String> docColumn = new TableColumn<>("document");
        docColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));

        TableColumn<Pair<String, String[]>, String> seeMore_buttons = new TableColumn<>();//Button

        seeMore_buttons.setCellFactory(param -> new TableCell<Pair<String, String[]>, String>() {

            final Button btn = new Button("see entities");

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    btn.setOnAction(event -> {
                        Pair<String, String[]> doc = getTableView().getItems().get(getIndex());
                        Stage stage = new Stage();
                        stage.setAlwaysOnTop(false);
                        stage.setResizable(false);
                        stage.setTitle("document " + doc + " entities");
                        stage.initModality(Modality.APPLICATION_MODAL);
                        ScrollPane scrollPane = new ScrollPane();
                        TableView<String> queryTable = new TableView<>();
                        TableColumn<String, String> entities = new TableColumn<>("entity");
                        entities.setCellValueFactory(param1 -> new SimpleStringProperty(param1.getValue()));
                        queryTable.getColumns().add(entities);
                        queryTable.getItems().addAll(doc.getValue());
                        scrollPane.setContent(queryTable);
                        Scene scene = new Scene(scrollPane);
                        stage.setScene(scene);
                        stage.show();
                    });
                    setGraphic(btn);
                    setText(null);
                }
            }
        });

        queryTable.getColumns().addAll(docColumn, seeMore_buttons);
        return queryTable;
    }

    public void search_queries_file(ActionEvent actionEvent) {
        ObservableList<String> cities_Chosen = comboBox_cities.getCheckModel().getCheckedItems();
        HashSet<String> cities = new HashSet<>(cities_Chosen);
        ObservableList<String> languages_Chosen = comboBox_languages.getCheckModel().getCheckedItems();
        HashSet<String> languages = new HashSet<>(languages_Chosen);
        Map<String, List<Pair<String, String[]>>> results = model.searchByQuery_File(Paths.get(text_queries_path.getText()), checkBox_stemming_Q.isSelected(), checkBox_semantic.isSelected(), cities, languages);
        TableView<Map.Entry<String, List<Pair<String, String[]>>>> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Map.Entry<String, List<Pair<String, String[]>>>, String> queryNum = new TableColumn<>("query num");
        queryNum.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));

        TableColumn<Map.Entry<String, List<Pair<String, String[]>>>, String> seeMore_buttons = new TableColumn<>();//Button

        seeMore_buttons.setCellFactory(param -> new TableCell<Map.Entry<String, List<Pair<String, String[]>>>, String>() {

            final Button btn = new Button("see more");

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    btn.setOnAction(event -> {
                        Map.Entry<String, List<Pair<String, String[]>>> query = getTableView().getItems().get(getIndex());
                        Stage stage = new Stage();
                        stage.setAlwaysOnTop(false);
                        stage.setResizable(false);
                        stage.setTitle("query " + query.getKey());
                        stage.initModality(Modality.APPLICATION_MODAL);
                        ScrollPane scrollPane = new ScrollPane();
                        TableView<Pair<String, String[]>> queryTable = getQueryTable();
                        queryTable.getItems().addAll(query.getValue());

//                        queryTable.setPrefWidth(2500);
                        scrollPane.setContent(queryTable);
//                        queryTable.setPrefHeight(600);
//                        scrollPane.setPrefHeight(600);
//                        scrollPane.setPrefWidth(800);
                        Scene scene = new Scene(scrollPane);
                        stage.setScene(scene);
                        stage.show();
                    });
                    setGraphic(btn);
                    setText(null);
                }
            }
        });

        tableView.getColumns().addAll(queryNum, seeMore_buttons);
        tableView.getItems().addAll(results.entrySet());
        Stage stage = new Stage();
        stage.setResizable(true);
        VBox vBox = new VBox();
        Button button = new Button("save queries results");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("queries_results");
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("text", "*.txt"));
            File file = fileChooser.showSaveDialog(button.getScene().getWindow());
            if (file == null)
                return;
            model.saveQueryOutput(results, file);
        });
        button.setPrefWidth(vBox.getMaxWidth());
        button.setMaxWidth(vBox.getMaxWidth());
//        button.setMaxWidth(vBox.widthProperty().doubleValue());
        vBox.getChildren().addAll(button, tableView);
        stage.setScene(new Scene(vBox));
        stage.setTitle(results.size() + " query's results");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    private void setDisableToFalse() {
        button_search_queries_file.setDisable(false);
        button_search_query.setDisable(false);
        text_query.setDisable(false);
        text_queries_path.setDisable(false);
        fileChooser_queries_file.setDisable(false);
        checkBox_stemming_Q.setDisable(false);
        checkBox_semantic.setDisable(false);
        comboBox_cities.setDisable(false);
        comboBox_languages.setDisable(false);

    }
}
