package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Controller {

    @FXML
    public javafx.scene.control.Button btn_searchCorpusPath;
    @FXML
    public javafx.scene.control.Button btn_searchSavePath;
    @FXML
    public javafx.scene.control.Button btn_loadDictionary;
    @FXML
    public javafx.scene.control.Button btn_displayDictionary;
    @FXML
    public javafx.scene.control.Button btn_initialMemory;
    @FXML
    public javafx.scene.control.Button btn_play;
    @FXML
    public javafx.scene.control.TextField txt_corpusPath;
    @FXML
    public javafx.scene.control.TextField txt_savePath;
    @FXML
    public javafx.scene.control.TextField txt_savePathLabel;
    @FXML
    public javafx.scene.control.TextField txt_corpusPathLabel;
    @FXML
    public javafx.scene.control.TextField txt_LanguageLabel;
    @FXML
    public javafx.scene.control.CheckBox checkBox_stemming;
    @FXML
    public javafx.scene.control.ChoiceBox choiceBox_Language;


    @FXML
    private void initialize() {
        txt_corpusPathLabel.setEditable(false);
        txt_LanguageLabel.setEditable(false);
        txt_savePathLabel.setEditable(false);
        btn_initialMemory.setDisable(true);
        btn_loadDictionary.setDisable(true);
        btn_play.setDisable(true);
        btn_displayDictionary.setDisable(true);
    }

    @FXML
    private void editPath() {
        if (txt_savePath.getText() != null && txt_corpusPath.getText() != null) {
            btn_displayDictionary.setDisable(false);
            btn_initialMemory.setDisable(false);
            btn_loadDictionary.setDisable(false);
            btn_play.setDisable(false);
        }
    }

    @FXML
    public void handleSearchCorpusPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(null);
        if (dir != null) {
            txt_corpusPath.setText(dir.getAbsolutePath());
        } else {
            txt_corpusPath.setText(null);
        }
    }

    public void handleSearchSavePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(null);
        if (dir != null) {
            txt_savePath.setText(dir.getAbsolutePath());
        } else {
            txt_savePath.setText(null);
        }
    }

    public void handlePlay() {
        File corpus = new File(txt_corpusPath.getText() + "//corpus");
        File save = new File(txt_savePath.getText());
        if (!corpus.exists() || !save.exists()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wrong Input");
            alert.setHeaderText("One of your paths is wrong! please enter another one");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Good news, it's loading");
            alert.setHeaderText("The engine is working hard for you :-)");
            alert.show();
            btn_initialMemory.setDisable(true);
            btn_loadDictionary.setDisable(true);
            btn_play.setDisable(true);
            btn_searchCorpusPath.setDisable(true);
            btn_searchSavePath.setDisable(true);
            Indexer.dictionary=new HashMap<>();
            Indexer.postingIndex=0;
            DocsInformation docsInformation = new DocsInformation();
            ReadFile myReader = new ReadFile();
            Indexer indexer = new Indexer(txt_savePath.getText());
            CitiesIndexer citiesIndexer = new CitiesIndexer();
            boolean stemmer = checkBox_stemming.isSelected();

            String workingDir = txt_corpusPath.getText();

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
            Long startTime = System.currentTimeMillis();

            try {
                myReader.ReadFile(workingDir, stemmer);
            } catch (IOException e) {
                e.getStackTrace();
            }

            indexer.mergePostingFile(stemmer);
            docsInformation.saveTheInformation(txt_savePath.getText(), stemmer);
            citiesIndexer.writeCitiesPosting(txt_savePath.getText());

            alert.close();
            Date date1 = new Date();
            System.out.println(dateFormat.format(date1));
            Long endTime = System.currentTimeMillis();

            btn_displayDictionary.setDisable(false);
            Alert detailsAlert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Number of document: " + docsInformation.allDocsInformation.size() + "\n" +
                                "Number of unique terms: " + indexer.dictionary.size() + "\n" +
                                "Time in seconds: " + (endTime-startTime/1000));
            alert.show();
            btn_initialMemory.setDisable(false);
            btn_loadDictionary.setDisable(false);
            btn_play.setDisable(false);
            btn_searchCorpusPath.setDisable(false);
            btn_searchSavePath.setDisable(false);
            choiceBox_Language.setItems(FXCollections.observableArrayList(DocsInformation.allLanguages));
        }
    }

    public void handleDisplayDictionary(ActionEvent actionEvent) {
        File save1 = new File(txt_savePath.getText() + "//Dictionary with stemmer.txt");
        File save2 = new File(txt_savePath.getText() + "//Dictionary without stemmer.txt");
        if (!save1.exists() && !save2.exists()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wrong Input");
            alert.setHeaderText("There is no saved dictionary in your save path!");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Good news");
            alert.setHeaderText("loading your dictionary, please wait and don't touch");
            alert.show();
            DictionaryController dictController = new DictionaryController();
            dictController.displayDictionary();
        }
    }


    public void initialMemory() {
        try {
            Files.deleteIfExists(Paths.get(txt_savePath.getText() + "\\Cities Information.txt"));
            Files.deleteIfExists(Paths.get(txt_savePath.getText() + "\\Dictionary without stemmer.txt"));
            Files.deleteIfExists(Paths.get(txt_savePath.getText() + "\\Dictionary with stemmer.txt"));
            Files.deleteIfExists(Paths.get(txt_savePath.getText() + "\\Posting with stemmer.txt"));
            Files.deleteIfExists(Paths.get(txt_savePath.getText() + "\\Posting without stemmer.txt"));
            Files.deleteIfExists(Paths.get(txt_savePath.getText() + "\\Docs Information without stemmer.txt"));
            Files.deleteIfExists(Paths.get(txt_savePath.getText() + "\\Docs Information with stemmer.txt"));
            btn_displayDictionary.setDisable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void loadDictionary() {
        File dic1 = new File(txt_corpusPath.getText() + "\\Dictionary with stemmer.txt");
        File dic2 = new File(txt_corpusPath.getText() + "\\Dictionary without stemmer.txt");
        boolean stemmer = checkBox_stemming.isSelected();
        boolean load = true;
        if (!dic1.exists() && stemmer) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wrong Input");
            alert.setHeaderText("There is no saved dictionary with stemmer");
            alert.show();
            load = false;
        }
        if (!dic2.exists() && !stemmer) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wrong Input");
            alert.setHeaderText("There is no saved dictionary without stemmer");
            alert.show();
            load = false;
        }
        if(load){
            btn_displayDictionary.setDisable(false);
            Indexer indexer = new Indexer(txt_savePath.getText());
            indexer.setDictionary(stemmer);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Good News");
            alert.setHeaderText("Your dictionary is loaded, you can take a look");
            alert.show();
        }
    }

}
