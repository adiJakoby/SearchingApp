package sample;

import java.io.*;
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
        TreeMap<Double, LinkedList> sortedRanksOfDocuments = getRankDocumentsSortedByRank(ranksOfDocuments);
        try {
            BufferedWriter WriteFileBuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("d:\\documents\\users\\adijak\\Downloads\\results.txt"), "UTF-8"));
            for (double rank: sortedRanksOfDocuments.descendingKeySet()
                 ) {
                LinkedList<String> documentsOfRank = sortedRanksOfDocuments.get(rank);
                while(!documentsOfRank.isEmpty()){
                    WriteFileBuffer.write(documentsOfRank.pollFirst() + ": " +rank + '\n');
                }
            }
            WriteFileBuffer.flush();
            WriteFileBuffer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        List<String> relevantDocuments = new LinkedList<>();

        return relevantDocuments;
    }

    /**
     *
     * @param beforeSort
     * @return a tree map of documents sorted by the ranks
     */
    private TreeMap<Double, LinkedList> getRankDocumentsSortedByRank(HashMap<String, Double> beforeSort){
        TreeMap<Double, LinkedList> afterSort = new TreeMap<>();
        for (String document: beforeSort.keySet()
             ) {
            if(afterSort.containsKey(beforeSort.get(document))){
                LinkedList<String> listOfDocs = afterSort.get(beforeSort.get(document));
                listOfDocs.add(document);
                afterSort.put(beforeSort.get(document), listOfDocs);
            }else{
                LinkedList<String> listOfDocs = new LinkedList<>();
                listOfDocs.add(document);
                afterSort.put(beforeSort.get(document), listOfDocs);
            }
        }

        return afterSort;
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
            if(dictionary.containsKey(token)) {
                tokensToFind.put(dictionary.get(token)[0], token);
            }else{
                System.out.println("the word: " + token + " is not exist in dictionary");
            }
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fullPostingPath), "UTF-8"));
            int pointer = 0;
            int nextLine = tokensToFind.firstKey();
            while(!tokensToFind.isEmpty()){
                String line = reader.readLine();
                while(pointer != nextLine){
                    line = reader.readLine();
                    pointer++;
                }
                String[] splitByColon = mySplit(line, ":");
                if(splitByColon[0].equals(tokensToFind.firstEntry().getValue())) {
                    String[] splitBySpace = mySplit(splitByColon[1], " ");
                    for (int i = 0; i < splitBySpace.length - 1; i = i + 2) {
                        if (result.containsKey(splitBySpace[i])) {
                            ArrayList<String[]> currentValue = result.get(splitBySpace[i]);
                            String[] newArr = {splitByColon[0], splitBySpace[i + 1], Integer.toString(splitBySpace.length / 2)};
                            currentValue.add(newArr);
                            result.put(splitBySpace[i], currentValue);
                        } else {
                            ArrayList<String[]> currentValue = new ArrayList<>();
                            //0: the term from the query, 1: number of appearance of this word in the document, 2: number of appearance of the word in the corpus (without duplicate)
                            String[] newArr = {splitByColon[0], splitBySpace[i + 1], Integer.toString(splitBySpace.length / 2)};
                            currentValue.add(newArr);
                            result.put(splitBySpace[i], currentValue);
                        }
                    }
                }else{
                    System.out.println("Problem!! the line in posting ( " + splitByColon[0] + " ) is not match to the term ( " + tokensToFind.firstEntry().getValue() + " ).");
                    System.out.println("the pointer is: " + pointer + " BUT the line number from the dictionary is: " + nextLine);

                }
                tokensToFind.pollFirstEntry();
                pointer++;
                if(!tokensToFind.isEmpty()){
                    nextLine = tokensToFind.firstKey();
                }
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

    //private List<>
}
