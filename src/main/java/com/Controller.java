package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import javafx.util.Pair;

import java.io.File;
import java.util.*;

public class Controller {

    Model model = new Model();
    @FXML
    public ProgressBar progressBar;
    public Button fileChooser_stop_words;
    public Button fileChooser_postings;
    public Button fileChooser_corpus;
    public Button button_reset;
    public Button button_showDictionary;
    public Button button_loadDictionary;
    public TextField text_stop_words;
    public TextField text_postings;
    public TextField text_corpus;
    public CheckBox checkBox_stemming;
    public GridPane data;
    public ComboBox<String> languagesComboBox;

    private String lastPath;
    public Map<String, String> map;


    @FXML
    public void initialize() {
        button_reset.setDisable(true);
        button_loadDictionary.setDisable(true);
        button_showDictionary.setDisable(true);
        languagesComboBox.setDisable(true);
        progressBar.setVisible(false);
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
        File file = directoryChooser.showDialog(fileChooser_postings.getScene().getWindow());
        if (file == null)
            return;
        text_postings.setText(file.getPath());
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
        if (text_corpus.getText().equals("") || text_postings.getText().equals("") || text_stop_words.getText().equals("")) {//check that all of fields are not empty
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
            if (!new File(text_postings.getText()).exists()) {
                showAlert(Alert.AlertType.ERROR, "postings text: illegal path");
                return;
            }

            lastPath = text_postings.getText();//save the last path of the last time we save the dictionary
            long startTime = System.nanoTime();//start to calculate how much the the process takes
            model.startIndexing(text_corpus.getText(), text_stop_words.getText(), text_postings.getText(), checkBox_stemming.isSelected());
            long CreateIndexTime = (System.nanoTime() - startTime) / 1000000000;
            button_reset.setDisable(false);//after indexing w can reset the files
            button_loadDictionary.setDisable(false);
            button_showDictionary.setDisable(false);

            for (String language : model.readFile.languages) {//show te language
                languagesComboBox.getItems().add(language);
            }
            languagesComboBox.setDisable(false);//after add all language we can show them
            int numberOfindexDoc = model.readFile.parser.indexer.docAndexed.get();
            int uniqueTerm = model.readFile.parser.indexer.uniqueTerm.get();
            StringBuilder showText = new StringBuilder();
            showText.append("The numbers of documents indexed: ").append(numberOfindexDoc).append("\n")
                    .append("The number of unique terms: ").append(uniqueTerm).append("\n").append("The time is takes: ").append(CreateIndexTime).append(" sec");
            model.initSearch(lastPath);
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
        languagesComboBox.getItems().clear();
        languagesComboBox.setDisable(true);
        showAlert(Alert.AlertType.INFORMATION, "done reset");
//        progressBar.setProgress();
    }
}
