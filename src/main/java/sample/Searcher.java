package sample;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class Searcher {
    Parse parser;
    Stemmer stemmer;
    private static HashMap<String, Integer[]> dictionary;
    String postingPath;
    String fullPostingPath;
    Ranker ranker;
    private org.json.simple.parser.JSONParser myParser;


    public Searcher(String postingPath) {
        dictionary = Indexer.dictionary;
        this.postingPath = postingPath;
        ranker = new Ranker();

    }

    /**
     * @param query
     * @param toStem - if the corpus has been stemmed or not
     * @return a list of the most 50 relevant documents (if exist) to the given query.
     */
    public List<String> getRelevantDocuments(String query, String description, boolean toStem, boolean semanticCare, List<String> cities) {
        Map<String, Integer> allDocsByCity = new HashMap<>();
        boolean cityFilter = false;
        boolean isDescription = false;
        if(cities.size() > 0){
            allDocsByCity = getAllDocsByCity(cities);
            cityFilter = true;
        }
        if(toStem){
            fullPostingPath = postingPath + "\\Posting with stemmer.txt";
        }else{
            fullPostingPath = postingPath + "\\Posting without stemmer.txt";
        }

        Map<String, Integer> tokens;
        Map<String, Integer> descriptionTokens;
        Map<String, Integer> semanticTokens = new HashMap<>();
        parser = new Parse(System.getProperty("user.dir") + "\\src\\main\\java");
        stemmer = new Stemmer();
        tokens = parser.queryParser(query);

        HashMap<String, Double> descriptionRanksOfDocuments = new HashMap<>();
        if(!description.equals("NO DESCRIPTION")){
            descriptionTokens = parser.queryParser(description);
            if(!descriptionTokens.isEmpty()){
                isDescription = true;
                if (toStem) {
                    descriptionTokens = stemmer.queryStemmer(descriptionTokens);
                }
                Map<String, ArrayList<String[]>> descriptionAllDocumentBeforeRank = getAllDocuments(descriptionTokens, allDocsByCity, cityFilter);
                descriptionRanksOfDocuments = ranker.rankBM25(descriptionAllDocumentBeforeRank, descriptionTokens);
            }
        }

        if(!tokens.isEmpty()) {
            if(semanticCare) {
                semanticTokens = getSemanticWords(tokens);
            }
            if (toStem) {
                tokens = stemmer.queryStemmer(tokens);
                if(semanticCare) {
                    semanticTokens = stemmer.queryStemmer(semanticTokens);
                }

            }

            Map<String, ArrayList<String[]>> allDocumentBeforeRank = getAllDocuments(tokens, allDocsByCity, cityFilter);
            Map<String, ArrayList<String[]>> semanticAllDocumentBeforeRank = new HashMap<>();
            if(semanticCare) {
                semanticAllDocumentBeforeRank = getAllDocuments(semanticTokens, allDocsByCity, cityFilter);
            }

            HashMap<String, Double> ranksOfDocuments = ranker.rankBM25(allDocumentBeforeRank, tokens);
            HashMap<String, Double> semanticRankOfDocuments = new HashMap<>();
            if(semanticCare) {
                semanticRankOfDocuments = ranker.rankBM25(semanticAllDocumentBeforeRank, semanticTokens);
            }

            HashMap<String, Double> totalRanks = ranker.totalRanks(ranksOfDocuments, semanticRankOfDocuments, semanticCare, descriptionRanksOfDocuments, isDescription);

            TreeMap<Double, LinkedList> sortedRanksOfDocuments = getRankDocumentsSortedByRank(totalRanks);

            List<String> relevantDocuments = new LinkedList<>();
            int counter  = 0;
            for (double rank : sortedRanksOfDocuments.descendingKeySet()
                    ) {
                LinkedList<String> documentsOfRank = sortedRanksOfDocuments.get(rank);
                while (!documentsOfRank.isEmpty() && counter < 50) {
                    relevantDocuments.add(documentsOfRank.pollFirst());
                    counter++;
                }
                if(counter == 50){
                    break;
                }
            }
            return relevantDocuments;
        }
        return null;
    }

    /**
     * @param beforeSort
     * @return a tree map of documents sorted by the ranks
     */
    private TreeMap<Double, LinkedList> getRankDocumentsSortedByRank(HashMap<String, Double> beforeSort) {
        TreeMap<Double, LinkedList> afterSort = new TreeMap<>();
        for (String document : beforeSort.keySet()
                ) {
            if (afterSort.containsKey(beforeSort.get(document))) {
                LinkedList<String> listOfDocs = afterSort.get(beforeSort.get(document));
                listOfDocs.add(document);
                afterSort.put(beforeSort.get(document), listOfDocs);
            } else {
                LinkedList<String> listOfDocs = new LinkedList<>();
                listOfDocs.add(document);
                afterSort.put(beforeSort.get(document), listOfDocs);
            }
        }

        return afterSort;
    }

    /**
     * @param tokens
     * @return a map for each relevant document to the query's words, a list of the words from the query that appears in, and the amount of appearance.
     */
    private Map<String, ArrayList<String[]>> getAllDocuments(Map<String, Integer> tokens, Map<String, Integer> allDocsByCity, boolean cityFilter) {
        Map<String, ArrayList<String[]>> result = new HashMap<>();
        TreeMap<Integer, String> tokensToFind = new TreeMap<>();
        for (String token : tokens.keySet()
                ) {
            if (dictionary.containsKey(token)) {
                tokensToFind.put(dictionary.get(token)[0], token);
            } else {
                //System.out.println("the word: " + token + " is not exist in dictionary");
            }
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fullPostingPath), "UTF-8"));
            int pointer = 0;
            int nextLine = tokensToFind.firstKey();
            while (!tokensToFind.isEmpty()) {
                String line = reader.readLine();
                while (pointer != nextLine) {
                    line = reader.readLine();
                    pointer++;
                }
                String[] splitByColon = mySplit(line, ":");
                if (splitByColon[0].equals(tokensToFind.firstEntry().getValue())) {
                    String[] splitBySpace = mySplit(splitByColon[1], " ");
                    for (int i = 0; i < splitBySpace.length - 1; i = i + 2) {
                        if(cityFilter){
                            if(allDocsByCity.containsKey(splitBySpace[i])){
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
                            }else{
                                //the document doesn't contain the city so it's not relevant.
                            }
                        }else {
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
                    }
                } else {
                    System.out.println("Problem!! the line in posting ( " + splitByColon[0] + " ) is not match to the term ( " + tokensToFind.firstEntry().getValue() + " ).");
                    System.out.println("the pointer is: " + pointer + " BUT the line number from the dictionary is: " + nextLine);

                }
                tokensToFind.pollFirstEntry();
                pointer++;
                if (!tokensToFind.isEmpty()) {
                    nextLine = tokensToFind.firstKey();
                }
            }
        } catch (IOException e) {
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
     * @param cities
     * @return map of all the docs that contain the cities, and the amount of appearances
     */
    private Map<String, Integer> getAllDocsByCity(List<String> cities) {
        Map<String, Integer> allDocsForAllCities = new HashMap<>();
        for (int i = 0; i < cities.size(); i++) {
            String city = cities.get(i);
            City currentCity = CitiesIndexer.allCitiesInCorpus.get(city);
            Map<String, String> allDocsByCity = currentCity.getDocsList();
            Map<String, Integer> allDocsContainsCity = new HashMap<>();
            for (String doc : allDocsByCity.keySet()) {
                String[] allLocationsOfCity = mySplit(allDocsByCity.get(doc), ",");
                int numOfAppearsInDoc = allLocationsOfCity.length;
                allDocsContainsCity.put(doc, numOfAppearsInDoc);
            }
            for (String doc: allDocsContainsCity.keySet()
                 ) {
                if(allDocsForAllCities.containsKey(doc)){
                    int appearance = allDocsForAllCities.get(doc);
                    appearance = appearance + allDocsContainsCity.get(doc);
                    allDocsForAllCities.put(doc, appearance);
                }else{
                    int appearance = allDocsContainsCity.get(doc);
                    allDocsForAllCities.put(doc, appearance);
                }
            }
        }
        return allDocsForAllCities;
    }

    private Map<String, Integer> getSemanticWords(Map<String, Integer> tokens) {
        Map<String, Integer> semanticWords = new HashMap<>();
        OkHttpClient myClient = new OkHttpClient();
        //String[] queryAfterSplit = mySplit(query, " ");
        //for(int i=0;i<queryAfterSplit.length;i++) {
        for (String key : tokens.keySet()
                ) {
            String url = "https://api.datamuse.com/words?ml=" + key;
            Request req = new Request.Builder().url(url).build();
            Response res = null;
            try {
                res = myClient.newCall(req).execute();

            } catch (IOException e) {
                e.printStackTrace();
            }
            Object object = null;
            myParser = new org.json.simple.parser.JSONParser();
            try {
                try {
                    object = myParser.parse(res.body().string());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (object != null) {
                Object[] parsed_json = ((JSONArray) object).toArray();
                String word = "";
                int counter = 0;
                for (Object O : parsed_json) {
                    word = (String) ((JSONObject) O).get("word");
                    semanticWords.put(word, tokens.get(key));
                    counter++;
                    if (counter == 7) {
                        break;
                    }
                }
            }
        }
        return semanticWords;
    }

}

