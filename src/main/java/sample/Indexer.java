package sample;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Indexer {
    static int postingIndex = 0;
    public static TreeMap<String, String> mergedTerms = new TreeMap<>(new Indexer.MyComp2());
    public static HashMap<String, Integer[]> dictionary = new HashMap<>();
    private TreeMap<String, Integer> terms;
    private int[] howMuchTerms;
    public static int doneCounter = 0;
    static String workingDir;

    public Indexer(String path){
        workingDir = path;
    }

    public Indexer(){}

    /**
     * get a sorted map of tokens of doc, add the tokens to the generic dictionary of the corpus, and every few calls to
     * this function write to the disk the temporally posting files.
     *
     * @param tokens   - the given map of a docName doc
     * @param docName  - the name of the doc.
     * @param doneFile - if a specific file is done.
     */
    public void indexing(TreeMap<String, Integer> tokens, String docName, boolean doneFile) {
        //dictionary.putAll(tokens);
        addToDictionary(tokens);
        //dictionary.addAll(tokens.keySet());
        if (doneFile) {
            doneCounter++;
        }
        if (doneCounter == 15) {
            addToMap(tokens, docName);
            if (mergedTerms.size() > 0) {
                executePosting();
            }
            mergedTerms.clear();
            doneCounter = 0;
        }
        addToMap(tokens, docName);
    }

    private void addToDictionary(TreeMap<String, Integer> tokens){
        for (Map.Entry<String, Integer> e : tokens.entrySet()
                ) {
            String key = e.getKey();
            Integer count = e.getValue();
            if(dictionary.containsKey(key)){
                Integer[] value = {dictionary.get(key)[0], dictionary.get(key)[1] + count};
                dictionary.replace(key, value);
            }
            else{
                Integer[] value = {count, count};
                dictionary.put(key, value);
            }
        }
    }

    /**
     * adding the map of the tokens of a doc to the local map of a few docs. merge the tokens map.
     *
     * @param tokens
     * @param docName
     */
    private void addToMap(TreeMap<String, Integer> tokens, String docName) {
        while (tokens.size() > 0) {
            String token = tokens.firstKey();
            int counter = tokens.pollFirstEntry().getValue();
            if (mergedTerms.containsKey(token)) {
                StringBuilder temp = new StringBuilder();
                temp.append(mergedTerms.get(token));
                temp.append(" " + docName + " " + counter);
                mergedTerms.replace(token, temp.toString());
            } else {
                mergedTerms.put(token, docName + " " + counter);
            }
        }
    }
    /**
     * writing to the disk a temporally posting file of a few documents.
     */
    public void executePosting() {
        try {
            //PrintWriter writer = new PrintWriter(workingDir + "\\" + postingIndex + ".txt", "UTF-8");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(workingDir + "\\" + postingIndex + ".txt"),"UTF-8"));
            StringBuilder toWrite = new StringBuilder();
            while (mergedTerms.size() > 0) {
                toWrite.append(mergedTerms.firstKey() + ": " + mergedTerms.pollFirstEntry().getValue() + '\n');
            }
            writer.write(toWrite.toString());
            writer.flush();
            writer.close();
            postingIndex++;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * merge all the temporally posting files to one.
     */
    public void mergePostingFile(boolean stemmer) {
        String fileName = "";
        if(stemmer){
            fileName = " with stemmer.txt";
        }
        else{
            fileName = " without stemmer.txt";
        }
        try {
            int pointer = 0;
            // open a buffer writer to the final posting file
            //FileWriter fw = new FileWriter(workingDir + "\\Posting" + fileName);
            BufferedWriter WriteFileBuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(workingDir + "\\Posting" + fileName),"UTF-8"));
            // open a buffer reader to each temp posting file
            BufferedReader[] bufferReaders = new BufferedReader[postingIndex];
            //tree map that keeps 3 terms from the each temp posting files
            terms = new TreeMap<>(new Indexer.MyComp());
            howMuchTerms = new int[postingIndex];
            StringBuilder toWrite = new StringBuilder();

            //initial the tree map terms with the X first lines.
            for (int i = 0; i < postingIndex; i++) {
                bufferReaders[i] = new BufferedReader(new InputStreamReader(new FileInputStream(workingDir + "\\" + i + ".txt"), "UTF-8"));
                String line;
                int counter = 1;
                line = bufferReaders[i].readLine();
                if(line != null) {
                    terms.put(line, i);
                    howMuchTerms[i] = counter;
                }
                while (line != null && counter != 3) {
                    line = bufferReaders[i].readLine();
                    terms.put(line, i);
                    counter++;
                    howMuchTerms[i] = counter;
                }

            }

            //running over the the tree map with 3 term from each temp posting file. and writing to the final posting file every 10 terms.
            String currentTerm = terms.firstKey();
            int index = currentTerm.indexOf(":");
            currentTerm = currentTerm.substring(0, index);
            if (Character.isUpperCase(currentTerm.charAt(0))){
                //dictionary.contains(currentTerm.toLowerCase())
                if(dictionary.containsKey(currentTerm.toLowerCase())){
                    if(dictionary.containsKey(currentTerm)) {
                        Integer upCount = dictionary.get(currentTerm)[1];
                        dictionary.remove(currentTerm);
                        Integer[] updateVal = dictionary.get(currentTerm.toLowerCase());
                        updateVal[1] = updateVal[1] + upCount;
                        dictionary.replace(currentTerm.toLowerCase(), updateVal);
                    }
                    currentTerm = currentTerm.toLowerCase();
                }else{
                    addEntities(terms.firstKey());
                }
            }
            toWrite.append(terms.firstKey());
            String lastTerm = currentTerm;
            int j = terms.pollFirstEntry().getValue();
            updateTerm(j, bufferReaders[j]);
            int counter = 1;
            while (terms.size() > 0) {
                currentTerm = terms.firstKey();
                index = currentTerm.indexOf(":");
                currentTerm = currentTerm.substring(0, index);
                if (Character.isUpperCase(currentTerm.charAt(0))){
                    //dictionary.contains(currentTerm.toLowerCase()
                    if(dictionary.containsKey(currentTerm.toLowerCase())){
                        if(dictionary.containsKey(currentTerm)) {
                            Integer upCount = dictionary.get(currentTerm)[1];
                            dictionary.remove(currentTerm);
                            Integer[] updateVal = dictionary.get(currentTerm.toLowerCase());
                            updateVal[1] = updateVal[1] + upCount;
                            dictionary.replace(currentTerm.toLowerCase(), updateVal);
                        }
                        currentTerm = currentTerm.toLowerCase();
                    }else{
                        addEntities(terms.firstKey());
                    }
                }
                if (currentTerm.equals(lastTerm)) {
                    toWrite.append(terms.firstKey().substring(index+1));
                } else {
                    Integer[] newValue = new Integer[2];
                            newValue[0] = pointer;
                            newValue[1] = (dictionary.get(lastTerm))[1];

                    dictionary.put(lastTerm, newValue);
                    pointer++;
                    if (counter == 1000 || terms.size() == 1) {
                        writeToPosting(toWrite, WriteFileBuffer);
                        toWrite = new StringBuilder();
                        toWrite.append("\n" + currentTerm + terms.firstKey().substring(index));
                        counter = 1;
                    } else {
                        toWrite.append("\n" + currentTerm + terms.firstKey().substring(index));
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
                try
                {
                    Files.deleteIfExists(Paths.get(workingDir + "\\" + i + ".txt"));
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        } catch (IOException Ex) {
            System.out.println(Ex.getMessage());
        }
        try {
            //FileWriter fw = new FileWriter(workingDir + "\\Dictionary" + fileName);
            BufferedWriter WriteFileBuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(workingDir + "\\Dictionary" + fileName), "UTF-8"));
            for (HashMap.Entry<String, Integer[]> e : dictionary.entrySet()
                    ) {
                WriteFileBuffer.write(e.getKey() + ";" + Integer.toString(e.getValue()[0]) + ";" + Integer.toString(e.getValue()[1]) + '\n');
            }
            WriteFileBuffer.flush();
            WriteFileBuffer.close();
        }catch(IOException Ex) {
            System.out.println(Ex.getMessage());
        }
    }

    /**
     * checks if the tree map of the terms contain at least 1 term from the temp posting file (file Index)
     * if not, add 3 terms fron this temp posting file to the tree map.
     *
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
                if (line != null) {
                    terms.put(line, fileIndex);
                    howMuchTerms[fileIndex]++;
                }
                while (line != null && counter != 3) {
                    line = bufferedReader.readLine();
                    if (line != null) {
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
     *
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
            int index1 = s1.indexOf(":");
            int index2 = s2.indexOf(":");
            String s11 = s1.substring(0, index1);
            String s22 = s2.substring(0, index2);
            String s1l = s11.toLowerCase();
            String s2l = s22.toLowerCase();
            if (s1l.equals(s2l)) {
                if (s11.equals(s22)) {
                    return s1.compareTo(s2);
                } else {
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
            if (s1l.equals(s2l)) {
                return s1.compareTo(s2);
            }
            return s1l.compareTo(s2l);
        }
    }

    /**
     * reading and setting the dictionary from the disk to the app memory
     * @param stemmer
     */
    public void setDictionary(boolean stemmer){
        dictionary = new HashMap<>();
        String fileName = "";
        if(stemmer){
            fileName = " with stemmer.txt";
        }
        else{
            fileName = " without stemmer.txt";
        }
        try{
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(workingDir + "\\Dictionary" + fileName), "UTF-8"));
            while ((line = reader.readLine()) != null)
            {
                String[] parts = line.split(";");
                if (parts.length >= 3)
                {
                    String key = parts[0];
                    Integer[] value = {Integer.parseInt(parts[1]), Integer.parseInt((parts[2]))};
                    dictionary.put(key, value);
                }
            }
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addEntities(String term){
        int index = term.indexOf(":");
        String entity = term.substring(0, index);
        String docsInfo = term.substring(index+1);
        String[] splitDocsInfo = Parse.mySplit(docsInfo, " ");
        for(int i = 0; i < splitDocsInfo.length; i = i + 2){
            DocsInformation.addDominantEntities(splitDocsInfo[0], entity, Integer.parseInt(splitDocsInfo[1]));
        }
    }
}
