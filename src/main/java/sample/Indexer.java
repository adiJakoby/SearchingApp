package sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Indexer {
    static int postingIndex = 0;
    private static LinkedList<TreeMap<String, Integer>> allMaps = new LinkedList();
    private static LinkedList<String> docsNames = new LinkedList();
    //static Map<String, String path>

    public void indexing(TreeMap<String, Integer> tokens, String docName, boolean doneFile) {
        if (doneFile) {
            allMaps.add(tokens);
            docsNames.add(docName);
            if (allMaps.size() > 0) {
                executePosting();
            }
            allMaps.clear();
            docsNames.clear();
            //allMaps = new LinkedList();
            //docsNames = new LinkedList();
        }
        allMaps.add(tokens);
        docsNames.add(docName);
    }

    private void executePosting() {
        LinkedList<LinkedHashMap<String, String>> afterFirstMerge = new LinkedList<>();
        while(allMaps.size() > 0) {
            String docName1 = "";
            String docName2 = "";
            TreeMap<String, Integer> map1 = new TreeMap<>();
            TreeMap<String, Integer> map2 = new TreeMap<>();
            while(allMaps.size() > 0 && allMaps.peekFirst().size() == 0){
                allMaps.pollFirst();
                docsNames.pollFirst();
            }
            if(allMaps.size() > 0) {
                map1 = allMaps.pollFirst();
                docName1 = docsNames.pollFirst();
            }
            while(allMaps.size() > 0 && allMaps.peekFirst().size() == 0){
                allMaps.pollFirst();
                docsNames.pollFirst();
            }
            if(allMaps.size() > 0) {
                map2 = allMaps.pollFirst();
                docName2 = docsNames.pollFirst();
            }
            afterFirstMerge.add(sort(map1, map2, docName1, docName2));
        }
        // try {
        //PrintWriter writer = new PrintWriter("C:\\Users\\adi\\IdeaProjects\\SearchingApp\\src\\main\\java\\" + postingIndex + ".txt", "UTF-8");
        StringBuilder toWrite = new StringBuilder();
        //writer.close();
        postingIndex++;
        /*} catch (IOException ioe) {
            ioe.printStackTrace();
        }*/
    }

    private LinkedHashMap<String, String> sort(TreeMap<String, Integer> map1, TreeMap<String, Integer> map2,
                                               String docName1, String docName2) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        if(map2.size() == 0){
            while(map1.size() > 0){
                result.put(map1.firstKey(), docName1 + " " + map1.firstEntry().getValue());
                map1.pollFirstEntry();
            }
        }
        else {
            String term1 = map1.firstKey();
            Integer count1 = map1.firstEntry().getValue();
            String term2 = map2.firstKey();
            Integer count2 = map2.firstEntry().getValue();

            while (map1.size() > 0 && map2.size() > 0) {
                int isEquals = OurStringComp(term1, term2);
                if (isEquals == 0) {
                    result.put(term1, docName1 + " " + Integer.toString(count1) + " " + docName2 + " " + Integer.toString(count2));
                    map1.pollFirstEntry();
                    if (map1.size() > 0) {
                        term1 = map1.firstKey();
                        count1 = map1.firstEntry().getValue();
                    }
                    map2.pollFirstEntry();
                    if (map2.size() > 0) {
                        term2 = map2.firstKey();
                        count2 = map2.firstEntry().getValue();
                    }
                } else if (isEquals < 0) {
                    result.put(term1, docName1 + " " + Integer.toString(count1));
                    map1.pollFirstEntry();
                    if (map1.size() > 0) {
                        term1 = map1.firstKey();
                        count1 = map1.firstEntry().getValue();
                    }
                } else {
                    result.put(term2, docName2 + " " + Integer.toString(count2));
                    map2.pollFirstEntry();
                    if (map2.size() > 0) {
                        term2 = map2.firstKey();
                        count2 = map2.firstEntry().getValue();
                    }
                }
            }
            while (map1.size() > 0) {
                result.put(term1, docName1 + " " + Integer.toString(count1));
                map1.pollFirstEntry();
                if (map1.size() > 0) {
                    term1 = map1.firstKey();
                    count1 = map1.firstEntry().getValue();
                }
            }
            while (map2.size() > 0) {
                result.put(term2, docName2 + " " + Integer.toString(count2));
                map2.pollFirstEntry();
                if (map2.size() > 0) {
                    term2 = map2.firstKey();
                    count2 = map2.firstEntry().getValue();
                }
            }
        }
        return result;
    }


    private int OurStringComp(String s1, String s2) {
        String s1l = s1.toLowerCase();
        String s2l = s2.toLowerCase();
        if (s1l.equals(s2l)) {
            return s1.compareTo(s2);
        }
        return s1l.compareTo(s2l);
    }
}
