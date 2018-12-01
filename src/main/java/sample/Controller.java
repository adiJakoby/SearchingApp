package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private void initialize() {
        txt_corpusPathLabel.setEditable(false);
        txt_LanguageLabel.setEditable(false);
        txt_savePathLabel.setEditable(false);
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
        DocsInformation docsInformation = new DocsInformation();
        ReadFile myReader = new ReadFile();
        Indexer indexer = new Indexer(txt_savePath.getText());
        CitiesIndexer citiesIndexer = new CitiesIndexer();
        boolean stemmer = checkBox_stemming.isSelected();

        String workingDir = txt_corpusPath.getText();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43

        try {
            myReader.ReadFile(workingDir, stemmer);
        } catch (IOException e) {
            e.getStackTrace();
        }

        indexer.mergePostingFile();
        docsInformation.saveTheInformation(txt_savePath.getText());
        citiesIndexer.writeCitiesPosting(txt_savePath.getText());

        Date date1 = new Date();
        System.out.println(dateFormat.format(date1));
    }


    public void handleDisplayDictionary() {

    }

    public void initialMemory() {

    }

    public void loadDictionary() {

    }

}
