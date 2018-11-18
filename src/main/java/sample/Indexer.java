package sample;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class Indexer {
    static int postingIndex = 0;
    static String currentFileName = "";
    private LinkedList allMaps = new LinkedList();
    private LinkedList docsNames = new LinkedList();
    //static Map<String, String path>

    public void indexing(TreeMap<String, Integer> tokens, String docNo, String fileName){
        if(!currentFileName.equals(fileName)){
            executePosting();
            currentFileName = fileName;
            allMaps = new LinkedList();
            docsNames = new LinkedList();
        }
        allMaps.add(tokens);
        docsNames.add(fileName);
    }

    private void executePosting(){

        /*try {
            PrintWriter writer = new PrintWriter("C:\\Users\\adijak\\IdeaProjects\\SearchingApp\\src\\main\\java\\" + postingIndex+".txt", "UTF-8");
            for(String key : tokens.keySet()){
                writer.println(key + " " + docNo + " " + tokens.get(key));
            }
            writer.close();
            postingIndex++;
        }catch(IOException ioe){
            ioe.printStackTrace();
        }*/
    }
}
