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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.awt.event.KeyEvent;
import java.io.*;
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
    public javafx.scene.control.Button btn_browseQueries;
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
    public javafx.scene.control.TextField txt_queriesPath;
    @FXML
    public javafx.scene.control.TextField txt_queryLabel;
    @FXML
    public javafx.scene.control.CheckBox checkBox_semanticCare;
    @FXML
    public javafx.scene.control.CheckBox checkBox_stemming;
    @FXML
    public javafx.scene.control.ChoiceBox choiceBox_Language;
    @FXML
    public org.controlsfx.control.CheckComboBox<String> citiesFilter;

    boolean dictionaryLoaded;
    static int queryID;


    @FXML
    private void initialize() {
        txt_corpusPathLabel.setEditable(false);
        txt_LanguageLabel.setEditable(false);
        txt_savePathLabel.setEditable(false);
        btn_initialMemory.setDisable(true);
        btn_loadDictionary.setDisable(true);
        btn_play.setDisable(true);
        btn_displayDictionary.setDisable(true);
        dictionaryLoaded = false;
        queryID = 0;
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
                                "Time in seconds: " + ((endTime-startTime)/1000));
            alert.show();
            dictionaryLoaded = true;
            btn_initialMemory.setDisable(false);
            btn_loadDictionary.setDisable(false);
            btn_play.setDisable(false);
            btn_searchCorpusPath.setDisable(false);
            btn_searchSavePath.setDisable(false);
            choiceBox_Language.setItems(FXCollections.observableArrayList(CitiesIndexer.allCitiesInCorpus));
            handleCitiesFilter();
        }
    }

    //the function set up the choice combo box for cities filter
    private void handleCitiesFilter() {
        final ObservableList<String> allCities = FXCollections.observableArrayList();
        for (String city: CitiesIndexer.allCitiesInCorpus.keySet()) {
            allCities.add(city);
        }
        citiesFilter.getItems().setAll(allCities);
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
            dictionaryLoaded = false;
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void loadDictionary() {
        if(txt_savePath.getText().trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wrong Input");
            alert.setHeaderText("Please enter the path of the saved dictionary to the save field");
            alert.show();
        }
        else {
            File dic1 = new File(txt_savePath.getText() + "\\Dictionary with stemmer.txt");
            File dic2 = new File(txt_savePath.getText() + "\\Dictionary without stemmer.txt");
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
            if (load) {
                btn_displayDictionary.setDisable(false);
                Indexer indexer = new Indexer(txt_savePath.getText());
                DocsInformation docsInformation = new DocsInformation();
                CitiesIndexer citiesIndexer = new CitiesIndexer();
                indexer.setDictionary(stemmer);
                docsInformation.setAllDocsInformation(txt_savePath.getText(), stemmer);
                citiesIndexer.setAllCitiesInCorpus(txt_savePath.getText());

                dictionaryLoaded = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Good News");
                alert.setHeaderText("Your dictionary is loaded, you can take a look");
                alert.show();
                handleCitiesFilter();
            }
        }
    }

    public void browseQueriesPath(){
        if(txt_queryLabel.getText().trim().isEmpty()) {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                txt_queriesPath.setText(file.getAbsolutePath());
                txt_queryLabel.setEditable(false);
            } else {
                txt_queriesPath.setText(null);
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ooops");
            alert.setHeaderText("You can't run both file of queries and the query you have inserted");
            alert.show();
        }
    }

    public void handleSearch(){
        if(dictionaryLoaded) {
            Searcher searcher = new Searcher(txt_savePath.getText());
            List<String> cities = new LinkedList<>();
            final ObservableList<Integer> allCitiesChoosenIdex = citiesFilter.getCheckModel().getCheckedIndices();
            for (int cityIdex :allCitiesChoosenIdex) {
                cities.add(citiesFilter.getCheckModel().getItem(cityIdex));
            }
            if(!txt_queryLabel.getText().trim().isEmpty()) {
                List<String> result = searcher.getRelevantDocuments(txt_queryLabel.getText(), "NO DESCRIPTION", checkBox_stemming.isSelected(), checkBox_semanticCare.isSelected(), cities);
                if(result != null){
                    try {
                        BufferedWriter WriteFileBuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + "\\results.txt"), "UTF-8"));
                        for(int i = 0; i < result.size(); i++){
                            WriteFileBuffer.write(queryID + " 0 " + result.get(i) + " 0 0 mt\n");
                        }
                        WriteFileBuffer.flush();
                        WriteFileBuffer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    queryID++;
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ho NO!");
                    alert.setHeaderText("There is no relevant documents to your query, Please try again");
                    alert.show();
                }
            }else if(!txt_queriesPath.getText().isEmpty()){
                DisplayerController displayerController = new DisplayerController();
                LinkedHashMap<String, List<String>> queriesResult = new LinkedHashMap();
                ReadQuery readQuery = new ReadQuery();
                ArrayList<String[]> queries = readQuery.getQueryFromFile(txt_queriesPath.getText());
                for(int i = 0; i < queries.size(); i++){
                    List<String> result = searcher.getRelevantDocuments(queries.get(i)[1], queries.get(1)[2], checkBox_stemming.isSelected(), checkBox_semanticCare.isSelected(), cities);
                    queriesResult.put(queries.get(i)[0], result);
                }
                try {
                    BufferedWriter WriteFileBuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + "\\results.txt"), "UTF-8"));
                    for (String query: queriesResult.keySet()
                         ) {
                        List<String> result = queriesResult.get(query);
                        if(result != null) {
                            for (int i = 0; i < result.size(); i++) {
                                WriteFileBuffer.write(query + " 0 " + result.get(i) + " 0 0 mt\n");
                            }
                        }
                    }
                    WriteFileBuffer.flush();
                    WriteFileBuffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //displayerController.displayrelevantDocs(queriesResult);
            }

        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Oooops!");
            alert.setHeaderText("Before running some queries you have to load a dictionary!");
            alert.show();
        }
    }

}
