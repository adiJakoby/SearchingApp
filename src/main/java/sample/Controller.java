package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.File;

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

    }

    public void handleDisplayDictionary() {

    }

    public void initialMemory() {

    }

    public void loadDictionary() {

    }

}
