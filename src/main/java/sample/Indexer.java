package sample;

import java.io.*;
import java.util.*;

public class Indexer {
    static int postingIndex = 0;
    private static TreeMap<String, String> mergedTerms = new TreeMap<>(new Indexer.MyComp2());
    private static HashSet<String> dictionary = new HashSet<>();
    private TreeMap<String, Integer> terms;
    private int[] howMuchTerms;
    private static int doneCounter = 0;

    /**
     * get a sorted map of tokens of doc, add the tokens to the generic dictionary of the corpus, and every few calls to
     * this function write to the disk the temporally posting files.
     * @param tokens - the given map of a docName doc
     * @param docName - the name of the doc.
     * @param doneFile - if a specific file is done.
     */
    public void indexing(TreeMap<String, Integer> tokens, String docName, boolean doneFile) {
        dictionary.addAll(tokens.keySet());
        if (doneFile) {
            doneCounter++;
        }
        if(doneCounter == 33){
            addToMap(tokens, docName);
            if (mergedTerms.size() > 0) {
                executePosting();
            }
            mergedTerms.clear();
            doneCounter = 0;
        }
        addToMap(tokens, docName);
    }

    /**
     * adding the map of the tokens of a doc to the local map of a few docs. merge the tokens map.
     * @param tokens
     * @param docName
     */
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

    /**
     * writing to the disk a temporally posting file of a few documents.
     */
    private void executePosting() {
        try {
            PrintWriter writer = new PrintWriter("C:\\Users\\adijak\\IdeaProjects\\SearchingApp\\src\\main\\java\\" + postingIndex + ".txt", "UTF-8");
            StringBuilder toWrite = new StringBuilder();
            while(mergedTerms.size() > 0){
                toWrite.append(mergedTerms.firstKey() + " " + mergedTerms.pollFirstEntry().getValue() + '\n');
            }
            writer.print(toWrite);
            writer.close();
            postingIndex++;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    private int OurStringComp(String s1, String s2) {
        String s1l = s1.toLowerCase();
        String s2l = s2.toLowerCase();
        if (s1l.equals(s2l)) {
            return s1.compareTo(s2);
        }
        return s1l.compareTo(s2l);
    }


    /**
     * merge all the temporally posting files to one.
     */
    public void mergePostingFile() {
        try {
            // open a buffer writer to the final posting file
            FileWriter fw = new FileWriter("C:\\Users\\adijak\\IdeaProjects\\SearchingApp\\src\\main\\java\\Posting.txt");
            BufferedWriter WriteFileBuffer = new BufferedWriter(fw);
            // open a buffer reader to each temp posting file
            BufferedReader[] bufferReaders = new BufferedReader[postingIndex];
            //tree map that keeps 3 terms from the each temp posting files
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

            //running over the the tree map with 3 term from each temp posting file. and writing to the final posting file every 10 terms.
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

    /**
     * checks if the tree map of the terms contain at least 1 term from the temp posting file (file Index)
     * if not, add 3 terms fron this temp posting file to the tree map.
     * @param fileIndex
     * @param bufferedReader
     */
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

    /**
     * writing to the final posting file the string to write
     * @param toWrite
     * @param WriteFileBuffer
     */
    private void writeToPosting(StringBuilder toWrite, BufferedWriter WriteFileBuffer) {
        try {
            WriteFileBuffer.write(toWrite.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * compare just by the term
     */
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
