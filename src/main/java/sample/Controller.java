package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.*;

import java.io.File;
import java.util.*;

public class Controller {

    Model model = new Model();
    @FXML
    public ProgressBar progressBar;
    public Button fileChooser_stop_words;
    public Button fileChooser_postings;
    public Button fileChooser_corpus;
    public Button resetButton;
    public TextField text_stop_words;
    public TextField text_postings;
    public TextField text_corpus;
    public CheckBox checkBox_stemming;
    public GridPane data;
    public ComboBox<String> languagesComboBox;
    Iterator<Map.Entry<String, String>> lastIter;
    int lastLine = 0;
    private String lastPath;
    private boolean lastStem;
    public Map<String, String> map;

    @FXML
    public void initialize() {
        resetButton.setDisable(true);
        languagesComboBox.setDisable(true);
        progressBar.setVisible(false);

    }

    /**e
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
            progressBar.setProgress(0);
            progressBar.setVisible(true);
            lastPath = text_postings.getText();//save the last path of the last time we save the dictionary
            lastStem = checkBox_stemming.isSelected();
            long startTime = System.nanoTime();//start to calculate how much the the process takes
            model.startIndexing(text_corpus.getText(), text_stop_words.getText(), text_postings.getText(), checkBox_stemming.isSelected());
            long CreateInsexTime = (System.nanoTime() - startTime) / 1000000000;
            resetButton.setDisable(false);//after indexing w can reset the files
            for (String language : model.readFile.languages) {//show te language
                languagesComboBox.getItems().add(language);
            }
            languagesComboBox.setDisable(false);//after add all language we can show them
            int numberOfindexDoc = model.readFile.parser.indexer.docAndexed.get();
            int uniqueTerm = model.readFile.parser.indexer.uniqueTerm.get();
            StringBuilder showText = new StringBuilder();
            showText.append("The numbers of documents indexed: ").append(numberOfindexDoc).append("\n")
                    .append("The number of unique terms: ").append(uniqueTerm).append("\n").append("The time is takes: ").append(CreateInsexTime).append(" sec");
            showAlert(Alert.AlertType.INFORMATION, showText.toString());
            progressBar.setVisible(false);
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
            map = new TreeMap<>(MapSaver.loadMap(lastPath + "\\dicTF"+(lastStem?"stem":"nostem")));
            showAlert(Alert.AlertType.INFORMATION, "done loading");
        }
        else
            showAlert(Alert.AlertType.ERROR,"You need to start indexing before you can load dictionary.");
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
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setTitle("Dictionary");
        stage.initModality(Modality.APPLICATION_MODAL);
        ScrollPane scrollPane = new ScrollPane();
        GridPane gridPane = new GridPane();
        gridPane.addColumn(0, new Label("KEY"));
        gridPane.addColumn(1, new Label("Term Frequency"));
        lastLine++;
        Button seeMore = new Button("See more");
        gridPane.addColumn(0, seeMore);
        seeMore.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (lastIter != null) {
                    deleteRow(gridPane, lastLine);
                    for (int i = 0; i < 200 && lastIter.hasNext(); i++) {
//                    gridPane.
                        Map.Entry<String, String> entry = lastIter.next();
                        gridPane.addColumn(0, new Label(entry.getKey()));
//                        gridPane.add(new Label(entry.getKey()), 0, lastLine);
                        gridPane.addColumn(1, new Label(entry.getValue()));
                        lastLine++;
                    }
                    gridPane.addColumn(0, seeMore);
                    if (!lastIter.hasNext()) {
                        seeMore.setDisable(true);
                    }
                }
            }
        });
        scrollPane.setContent(gridPane);
        if (map != null) {
            lastIter = map.entrySet().iterator();
        }
//        for (Iterator<E> iter = list.iterator(); iter.hasNext(); ) {
        Scene scene = new Scene(scrollPane, 600, 400);
        stage.setScene(scene);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                lastIter = null;
                lastLine = 0;
            }
        });
        stage.show();
    }

    /**
     * delete the row in index "row" from the greed pane
     * we use this function to remove the "see more" button and show him again in the end
     *
     * @param grid - the grid we delete the row from
     * @param row  - the index of the row to delete
     */
    private static void deleteRow(GridPane grid, final int row) {
        Set<Node> deleteNodes = new HashSet<>();
        for (Node child : grid.getChildren()) {
            // get index from child
            Integer rowIndex = GridPane.getRowIndex(child);

            // handle null values for index=0
            int r = rowIndex == null ? 0 : rowIndex;

            if (r > row) {
                // decrement rows for rows after the deleted row
                GridPane.setRowIndex(child, r - 1);
            } else if (r == row) {
                // collect matching rows for deletion
                deleteNodes.add(child);
            }
        }

        // remove nodes from row
        grid.getChildren().removeAll(deleteNodes);
    }

    public void reset(ActionEvent actionEvent) {
        model.reset();
        showAlert(Alert.AlertType.INFORMATION, "done reset");
        ProgressBar progressBar = new ProgressBar(0);
//        progressBar.setProgress();
    }
}
