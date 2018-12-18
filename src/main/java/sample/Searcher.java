package sample;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Searcher {
    Parse parser;
    Stemmer stemmer;
    private static HashMap<String, Integer[]> dictionary;
    String postingPath;
    String fullPostingPath;
    Ranker ranker;


    public Searcher(String postingPath){
        dictionary = Indexer.dictionary;
        this.postingPath = postingPath;
        ranker = new Ranker();

    }

    /**
     *
     * @param query
     * @param toStem - if the corpus has been stemmed or not
     * @return a list of the most 50 relevant documents (if exist) to the given query.
     */
    public List<String> getRelevantDocuments(String query, boolean toStem){
        Map<String, Integer> tokens = new HashMap<>();
        parser = new Parse(System.getProperty("user.dir") + "\\src\\main\\java");
        stemmer = new Stemmer();
        tokens = parser.queryParser(query);
        if(toStem){
            tokens = stemmer.queryStemmer(tokens);
            fullPostingPath = postingPath + "\\Posting with stemmer.txt";
        }else{
            fullPostingPath = postingPath + "\\Posting without stemmer.txt";
        }

        Map<String, ArrayList<String[]>> allDocumentBeforeRank = getAllDocuments(tokens);
        HashMap<String, Double> ranksOfDocuments = ranker.rank(allDocumentBeforeRank, tokens);
        System.out.println(ranksOfDocuments);
        List<String> relevantDocuments = new LinkedList<>();

        return relevantDocuments;
    }

    /**
     *
     * @param tokens
     * @return a map for each relevant document to the query's words, a list of the words from the query that appears in, and the amount of appearance.
     */
    private Map<String, ArrayList<String[]>> getAllDocuments(Map<String, Integer> tokens){
        Map<String, ArrayList<String[]>> result = new HashMap<>();
        TreeMap<Integer, String> tokensToFind = new TreeMap<>();
        for (String token: tokens.keySet()
             ) {
            tokensToFind.put(dictionary.get(token)[0], token);
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fullPostingPath), "UTF-8"));
            int pointer = 0;
            while(!tokensToFind.isEmpty()){
                int nextLine = tokensToFind.pollFirstEntry().getKey();
                String line = reader.readLine();
                while(pointer != nextLine){
                    line = reader.readLine();
                    pointer++;
                }
                //ArrayList<String> docList = new ArrayList<>();

                String[] splitByColon = mySplit(line, ":");
                String[] splitBySpace = mySplit(splitByColon[1], " ");
                for(int i = 0; i < splitBySpace.length-1; i = i + 2){
                    if(result.containsKey(splitBySpace[i])){
                        ArrayList<String[]> currentValue = result.get(splitBySpace[i]);
                        String[] newArr = {splitByColon[0], splitBySpace[i+1], Integer.toString(splitBySpace.length/2)};
                        currentValue.add(newArr);
                        result.put(splitBySpace[i], currentValue);
                    }
                    else{
                        ArrayList<String[]> currentValue = new ArrayList<>();
                        //0: the term from the query, 1: number of appearance of this word in the document, 2: number of appearance of the word in the corpus (without duplicate)
                        String[] newArr = {splitByColon[0], splitBySpace[i+1], Integer.toString(splitBySpace.length/2)};
                        currentValue.add(newArr);
                        result.put(splitBySpace[i], currentValue);
                    }
                    //docList.add(splitBySpace[i] + " " + splitBySpace[i+1]);
                }
                //result.put(splitByColon[0], docList);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }


    private static String[] mySplit(String str, String regex) {
        Vector<String> result = new Vector<String>();
        int start = 0;
        int pos = str.indexOf(regex);
        while (pos >= start) {
            if (pos > start) {
                result.add(str.substring(start, pos));
            }
            start = pos + regex.length();
            //result.add(regex);
            pos = str.indexOf(regex, start);
        }
        if (start < str.length()) {
            result.add(str.substring(start));
        }
        String[] array = result.toArray(new String[0]);
        return array;
    }

    /**
     *
     * @param city
     * @return array list of all the docs that contain this city
     *///
    private Map<String,String> getAllDocsByCity(String city){
        City currentCity = CitiesIndexer.allCitiesInCorpus.get(city);
        Map<String, String>  allDocsByCity = currentCity.getDocsList();
        Map <String,String> allDocsContainsCity = new HashMap<>();
        for (String doc: allDocsByCity.keySet()) {
            String[] allLocationsOfCity = mySplit(allDocsByCity.get(doc),",");
            String numOfAppearsInDoc = Integer.toString(allLocationsOfCity.length);
            allDocsContainsCity.put(doc,numOfAppearsInDoc);
        }
        return allDocsContainsCity;
    }
}
