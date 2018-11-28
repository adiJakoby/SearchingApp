package sample;

import java.io.*;
import java.util.*;

public class Indexer {
    static int postingIndex = 0;
    private static TreeMap<String, String> mergedTerms = new TreeMap<>(new Indexer.MyComp2());
    //private static LinkedList<TreeMap<String, Integer>> allMaps = new LinkedList();
    //private static LinkedList<String> docsNames = new LinkedList();
    private static HashSet<String> dictionary = new HashSet<>();
    private TreeMap<String, Integer> terms;
    //private int[] seekIndex;
    private int[] howMuchTerms;
    //private int finalPostingIndex = 0;
    private static int doneCounter = 0;

    public void indexing(TreeMap<String, Integer> tokens, String docName, boolean doneFile) {
        dictionary.addAll(tokens.keySet());
        if (doneFile) {
            doneCounter++;
        }
        if(doneCounter == 33){
            addToMap(tokens, docName);
            //allMaps.add(tokens);
            //docsNames.add(docName);
            if (mergedTerms.size() > 0) {
                executePosting();
            }
            mergedTerms.clear();
            //docsNames.clear();
            doneCounter = 0;
            //allMaps = new LinkedList();
            //docsNames = new LinkedList();
        }
        addToMap(tokens, docName);
        //docsNames.add(docName);
    }

    private void addToMap(TreeMap<String, Integer> tokens, String docName){
        while(tokens.size() > 0){
            String token = tokens.firstKey();
            int counter = tokens.pollFirstEntry().getValue();
            if (mergedTerms.containsKey(token)){
                StringBuilder temp = new StringBuilder();
                temp.append(mergedTerms.get(token));
                temp.append(" " + docName + " " + counter);
                mergedTerms.put(token, temp.toString());
            }
            else{
                mergedTerms.put(token, docName + " " + counter);
            }
        }
    }

    private void executePosting() {
        /*
        LinkedList<LinkedList<String[]>> afterFirstMerge = new LinkedList<>();
        LinkedList<LinkedList<String[]>> tempMerge = new LinkedList<>();

        while (allMaps.size() > 0) {
            String docName1 = "";
            String docName2 = "";
            TreeMap<String, Integer> map1 = new TreeMap<>();
            TreeMap<String, Integer> map2 = new TreeMap<>();
            while (allMaps.size() > 0 && allMaps.peekFirst().size() == 0) {
                allMaps.pollFirst();
                docsNames.pollFirst();
            }
            if (allMaps.size() > 0) {
                map1 = allMaps.pollFirst();
                docName1 = docsNames.pollFirst();
            }
            while (allMaps.size() > 0 && allMaps.peekFirst().size() == 0) {
                allMaps.pollFirst();
                docsNames.pollFirst();
            }
            if (allMaps.size() > 0) {
                map2 = allMaps.pollFirst();
                docName2 = docsNames.pollFirst();
            }
            afterFirstMerge.add(firstMerge(map1, map2, docName1, docName2));
        }

        boolean done = false;
        while (afterFirstMerge.size() >= 1 && !done) {
            LinkedList<String[]> map1 = new LinkedList<>();
            LinkedList<String[]> map2 = new LinkedList<>();
            map1 = afterFirstMerge.pollFirst();
            if (afterFirstMerge.size() >= 1) {
                map2 = afterFirstMerge.pollFirst();
            }
            tempMerge.add(merge(map1, map2));

            if (afterFirstMerge.size() == 0) {
                afterFirstMerge.addAll(tempMerge);
                tempMerge.clear();
                if (afterFirstMerge.size() == 1) {
                    done = true;
                }
            }
        }*/

        try {
            PrintWriter writer = new PrintWriter("C:\\Users\\adijak\\IdeaProjects\\SearchingApp\\src\\main\\java\\" + postingIndex + ".txt", "UTF-8");
            StringBuilder toWrite = new StringBuilder();
            while(mergedTerms.size() > 0){
                toWrite.append(mergedTerms.firstKey() + " " + mergedTerms.pollFirstEntry().getValue() + '\n');
            }
            /*LinkedList<String[]> allTermsMerged = afterFirstMerge.pollFirst();
            while (allTermsMerged.size() > 0) {
                String[] toWriteA = allTermsMerged.pollFirst();
                toWrite.append(toWriteA[0] + " " + toWriteA[1] + "\n");
            }*/
            writer.print(toWrite);
            writer.close();
            postingIndex++;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /*
    private LinkedList<String[]> firstMerge(TreeMap<String, Integer> map1, TreeMap<String, Integer> map2,
                                            String docName1, String docName2){
        LinkedList<String[]> result = new LinkedList<>();
        if (map2.size() == 0) {
            while (map1.size() > 0) {
                String[] toAdd = {map1.firstKey(), docName1 + " " + map1.firstEntry().getValue()};
                result.add(toAdd);
                map1.pollFirstEntry();
            }
        } else {
            String term1 = map1.firstKey();
            Integer count1 = map1.firstEntry().getValue();
            String term2 = map2.firstKey();
            Integer count2 = map2.firstEntry().getValue();

            while (map1.size() > 0 && map2.size() > 0) {
                int isEquals = OurStringComp(term1, term2);
                if (isEquals == 0) {
                    String[] toAdd = {term1, docName1 + " " + Integer.toString(count1) + " " + docName2 + " " + Integer.toString(count2)};
                    result.add(toAdd);
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
                    String[] toAdd = {term1, docName1 + " " + Integer.toString(count1)};
                    result.add(toAdd);
                    map1.pollFirstEntry();
                    if (map1.size() > 0) {
                        term1 = map1.firstKey();
                        count1 = map1.firstEntry().getValue();
                    }
                } else {
                    String[] toAdd = {term2, docName2 + " " + Integer.toString(count2)};
                    result.add(toAdd);
                    map2.pollFirstEntry();
                    if (map2.size() > 0) {
                        term2 = map2.firstKey();
                        count2 = map2.firstEntry().getValue();
                    }
                }
            }
            while (map1.size() > 0) {
                String[] toAdd = {term1, docName1 + " " + Integer.toString(count1)};
                result.add(toAdd);
                map1.pollFirstEntry();
                if (map1.size() > 0) {
                    term1 = map1.firstKey();
                    count1 = map1.firstEntry().getValue();
                }
            }
            while (map2.size() > 0) {
                String[] toAdd = {term2, docName2 + " " + Integer.toString(count2)};
                result.add(toAdd);
                map2.pollFirstEntry();
                if (map2.size() > 0) {
                    term2 = map2.firstKey();
                    count2 = map2.firstEntry().getValue();
                }
            }
        }
        return result;
    }*/


    private int OurStringComp(String s1, String s2) {
        String s1l = s1.toLowerCase();
        String s2l = s2.toLowerCase();
        if (s1l.equals(s2l)) {
            return s1.compareTo(s2);
        }
        return s1l.compareTo(s2l);
    }

    /*
    private LinkedList<String[]> merge(LinkedList<String[]> map1,
                                       LinkedList<String[]> map2) {
        LinkedList<String[]> result = new LinkedList<>();

        if (map2.size() == 0) {
            return map1;
        } else {
            String term1 = map1.peekFirst()[0];
            String count1 = map1.peekFirst()[1];
            String term2 = map2.peekFirst()[0];
            String count2 = map2.peekFirst()[1];

            while (map1.size() > 0 && map2.size() > 0) {
                int isEquals = OurStringComp(term1, term2);
                if (isEquals == 0) {
                    String[] toAdd = {term1, count1 + " " + count2};
                    result.add(toAdd);
                    map1.pollFirst();
                    if (map1.size() > 0) {
                        term1 = map1.peekFirst()[0];
                        count1 = map1.peekFirst()[1];
                    }
                    map2.pollFirst();
                    if (map2.size() > 0) {
                        term2 = map2.peekFirst()[0];
                        count2 = map2.peekFirst()[1];
                    }
                } else if (isEquals < 0) {
                    String[] toAdd = {term1, count1};
                    result.add(toAdd);
                    map1.pollFirst();
                    if (map1.size() > 0) {
                        term1 = map1.peekFirst()[0];
                        count1 = map1.peekFirst()[1];
                    }
                } else {
                    String[] toAdd = {term2, count2};
                    result.add(toAdd);
                    map2.pollFirst();
                    if (map2.size() > 0) {
                        term2 = map2.peekFirst()[0];
                        count2 = map2.peekFirst()[1];
                    }
                }
            }
            while (map1.size() > 0) {
                String[] toAdd = {term1, count1};
                result.add(toAdd);
                map1.pollFirst();
                if (map1.size() > 0) {
                    term1 = map1.peekFirst()[0];
                    count1 = map1.peekFirst()[1];
                }
            }
            while (map2.size() > 0) {
                String[] toAdd = {term2, count2};
                result.add(toAdd);
                map2.pollFirst();
                if (map2.size() > 0) {
                    term2 = map2.peekFirst()[0];
                    count2 = map2.peekFirst()[1];
                }
            }
            return result;
        }
    }*/

    public void mergePostingFile() {
        try {
            FileWriter fw = new FileWriter("C:\\Users\\adijak\\IdeaProjects\\SearchingApp\\src\\main\\java\\Posting.txt");
            BufferedWriter WriteFileBuffer = new BufferedWriter(fw);
            BufferedReader[] bufferReaders = new BufferedReader[postingIndex];
            terms = new TreeMap<>(new Indexer.MyComp());
            howMuchTerms = new int[postingIndex];
            StringBuilder toWrite = new StringBuilder();

            //initial the tree map terms with the X first lines.
            for (int i = 0; i < postingIndex; i++) {
                File file = new File("C:\\Users\\adijak\\IdeaProjects\\SearchingApp\\src\\main\\java\\" + i + ".txt");
                FileReader fileReader = new FileReader(file);
                bufferReaders[i] = new BufferedReader(fileReader);
                String line;
                int counter = 1;
                line = bufferReaders[i].readLine();
                terms.put(line, i);
                while (line != null && counter != 3) {
                    line = bufferReaders[i].readLine();
                    terms.put(line, i);
                    counter++;
                }
                howMuchTerms[i] = 3;
            }

            String currentTerm = terms.firstKey();
            int index = currentTerm.indexOf(" ");
            currentTerm = currentTerm.substring(0, index);
            toWrite.append(terms.firstKey());
            String lastTerm = currentTerm;
            int j = terms.pollFirstEntry().getValue();
            updateTerm(j, bufferReaders[j]);
            int counter = 1;
            while (terms.size() > 0) {
                currentTerm = terms.firstKey();
                index = currentTerm.indexOf(" ");
                currentTerm = currentTerm.substring(0, index);
                if (currentTerm.equals(lastTerm)) {
                    toWrite.append(terms.firstKey().substring(index));
                } else {
                    if (counter == 10) {
                        writeToPosting(toWrite, WriteFileBuffer);
                        toWrite = new StringBuilder();
                        toWrite.append("\n" + terms.firstKey());
                        counter = 1;
                    } else {
                        toWrite.append("\n" + terms.firstKey());
                        counter++;
                    }
                    lastTerm = currentTerm;
                }
                int i = terms.pollFirstEntry().getValue();
                updateTerm(i, bufferReaders[i]);
            }

            WriteFileBuffer.close();
            for (int i = 0; i < postingIndex; i++) {
                bufferReaders[i].close();
            }
        } catch (IOException Ex) {
            System.out.println(Ex.getMessage());
        }
    }

    private void updateTerm(int fileIndex, BufferedReader bufferedReader) {
        howMuchTerms[fileIndex]--;
        if (howMuchTerms[fileIndex] == 0) {
            try {
                String line;
                int counter = 1;
                line = bufferedReader.readLine();
                if(line != null) {
                    terms.put(line, fileIndex);
                    howMuchTerms[fileIndex]++;
                }
                while (line != null && counter != 3) {
                    line = bufferedReader.readLine();
                    if(line != null) {
                        terms.put(line, fileIndex);
                        howMuchTerms[fileIndex]++;
                        counter++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeToPosting(StringBuilder toWrite, BufferedWriter WriteFileBuffer) {
        try {
            WriteFileBuffer.write(toWrite.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    class MyComp implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            int index1 = s1.indexOf(" ");
            int index2 = s2.indexOf(" ");
            String s11 = s1.substring(0, index1);
            String s22 = s2.substring(0, index2);
            String s1l = s11.toLowerCase();
            String s2l = s22.toLowerCase();
            if (s1l.equals(s2l)) {
                if(s11.equals(s22)){
                    return s1.compareTo(s2);
                }else{
                    return s1.compareTo(s2);
                }

            }
            return s1l.compareTo(s2l);
        }
    }

    static class MyComp2 implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            String s1l = s1.toLowerCase();
            String s2l = s2.toLowerCase();
            if(s1l.equals(s2l)){
                return s1.compareTo(s2);
            }
            return s1l.compareTo(s2l);
        }
    }
}
