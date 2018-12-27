package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import javafx.util.Pair;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

public class Controller {

    public Button fileChooser_postings_in;
    public TextField text_postings_in;
    public TextField text_queries_path;
    public Button fileChooser_queries_file;
    public Button button_search_queries_file;
    public CheckComboBox comboBox_cities;
    Model model = new Model();
    @FXML
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
    public ComboBox<String> comboBox_languages;

    private String lastPath;
    public Map<String, String> map;


    @FXML
    public void initialize() {
        button_reset.setDisable(true);
        button_loadDictionary.setDisable(true);
        button_showDictionary.setDisable(true);
        comboBox_languages.setDisable(true);
        text_queries_path.setText("C:\\Users\\micha\\OneDrive\\מסמכים\\michael\\שנה ג\\אחזור מידע\\queries.txt");
        button_search_queries_file.setDisable(false);
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
            long startTime = System.nanoTime();//start to calculate how much the the process takes
            model.startIndexing(text_corpus.getText(), text_stop_words.getText(), text_postings_out.getText(), checkBox_stemming_IN.isSelected());
            long CreateIndexTime = (System.nanoTime() - startTime) / 1000000000;
            button_reset.setDisable(false);//after indexing w can reset the files
            button_loadDictionary.setDisable(false);
            button_showDictionary.setDisable(false);

            for (String language : model.readFile.languages) {//show te language
                comboBox_languages.getItems().add(language);
            }
            comboBox_languages.setDisable(false);//after add all language we can show them
            int numberOfindexDoc = model.readFile.parser.indexer.docAndexed.get();
            int uniqueTerm = model.readFile.parser.indexer.uniqueTerm.get();
            StringBuilder showText = new StringBuilder();
            showText.append("The numbers of documents indexed: ").append(numberOfindexDoc).append("\n")
                    .append("The number of unique terms: ").append(uniqueTerm).append("\n").append("The time is takes: ").append(CreateIndexTime).append(" sec");
            model.initSearch(lastPath+"\\"+(checkBox_stemming_IN.isSelected()?"stem":"nostem"));
            comboBox_cities.getItems().clear();
//            comboBox_cities.getItems().

            showAlert(Alert.AlertType.INFORMATION, showText.toString());
//            progressBar.setVisible(false);
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
//        DirectoryChooser directoryChooser = new DirectoryChooser();
//        File file = directoryChooser.showDialog(fileChooser_postings_in.getScene().getWindow());
//        if (file == null)
//            return;
        text_postings_in.setText("C:\\Users\\micha\\OneDrive\\מסמכים\\michael\\שנה ג\\אחזור מידע\\ppart3\\nostem");
        //Todo open "wait..." window with text: "loading..."
        lastPath = text_postings_in.getText();
        model.initSearch(text_postings_in.getText());
    }

    public void search_query(ActionEvent actionEvent) {
        model.searchByQuery(text_query.getText(),checkBox_stemming_Q.isSelected(),checkBox_semantic.isSelected());
    }

    public void open_fileChooser_queries_file(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(fileChooser_queries_file.getScene().getWindow());
        if (file == null)
            return;
        text_queries_path.setText(file.getPath());
        button_search_queries_file.setDisable(false);
    }

    public void search_queries_file(ActionEvent actionEvent) {
        model.searchByQuery_File(Paths.get(text_queries_path.getText()),checkBox_stemming_Q.isSelected(),checkBox_semantic.isSelected());

    }
}
