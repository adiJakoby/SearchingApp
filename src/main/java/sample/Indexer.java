package sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class Indexer {
    static int postingIndex = 0;
    static String currentFileName = "";
    private static LinkedList<TreeMap<String, Integer>> allMaps = new LinkedList();
    private static LinkedList docsNames = new LinkedList();
    //static Map<String, String path>

    public void indexing(TreeMap<String, Integer> tokens, String docNo, String fileName){
        if(!currentFileName.equals(fileName)){
            if(allMaps.size() > 0) {
                executePosting();
            }
            currentFileName = fileName;
            allMaps.clear();
            docsNames.clear();
            //allMaps = new LinkedList();
            //docsNames = new LinkedList();
        }
        allMaps.add(tokens);
        docsNames.add(fileName);
    }

    private void executePosting(){

        try {
            PrintWriter writer = new PrintWriter("C:\\Users\\adi\\IdeaProjects\\SearchingApp\\src\\main\\java\\" + postingIndex+".txt", "UTF-8");
            StringBuilder toWrite = new StringBuilder();
            String [] terms = new String[allMaps.size()];
            Integer [] counters = new Integer[allMaps.size()];
            for(int i = 0; i < allMaps.size(); i++){
                terms[i] = (allMaps.get(i)).firstKey();
                counters[i] = (allMaps.get(i)).firstEntry().getValue();
                (allMaps.get(i)).pollFirstEntry();
            }
            System.out.println(currentFileName);
            writer.close();
            postingIndex++;
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
}
