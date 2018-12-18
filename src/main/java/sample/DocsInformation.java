package sample;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class DocsInformation {
    static HashMap<String, String[]> allDocsInformation = new HashMap<>();
    static HashSet<String> allLanguages = new HashSet<>();
    static double avdl = 0;

    public void addMaxTf(int max_tf, String docName){
        if(allDocsInformation.containsKey(docName)){
            String[] temp = allDocsInformation.get(docName);
            temp[0] = Integer.toString(max_tf);
            allDocsInformation.put(docName, temp);
        }
        else{
            String[] temp = new String[5];
            temp[0] = Integer.toString(max_tf);
            allDocsInformation.put(docName, temp);
        }
    }

    public void addUniqueTermsAmount(String docName, int amount){
        if(allDocsInformation.containsKey(docName)){
            String[] temp = allDocsInformation.get(docName);
            temp[1] = Integer.toString(amount);
            allDocsInformation.put(docName, temp);
        }
        else{
            String[] temp = new String[5];
            temp[1] = Integer.toString(amount);
            allDocsInformation.put(docName, temp);
        }
    }

    public void addOriginCity(String docName, String city){
        if(city==null){
            city="No City";
        }
        if(allDocsInformation.containsKey(docName)){
            String[] temp = allDocsInformation.get(docName);
            temp[2] = city;
            allDocsInformation.put(docName, temp);
        }
        else{
            String[] temp = new String[5];
            temp[2] = city;
            allDocsInformation.put(docName, temp);
        }
    }

    public void addDocLength(String docName, int length){
        if(allDocsInformation.containsKey(docName)){
            String[] temp = allDocsInformation.get(docName);
            temp[3] = Integer.toString(length);
            allDocsInformation.put(docName, temp);
        }
        else{
            String[] temp = new String[5];
            temp[3] = Integer.toString(length);
            allDocsInformation.put(docName, temp);
        }
        avdl = ((avdl*(allDocsInformation.size()-1))+length)/allDocsInformation.size();
    }

    public void addDateOfWrite(String docName, String date){
        if(allDocsInformation.containsKey(docName)){
            String[] temp = allDocsInformation.get(docName);
            temp[4] = date;
            allDocsInformation.put(docName, temp);
        }
        else{
            String[] temp = new String[5];
            temp[4] = date;
            allDocsInformation.put(docName, temp);
        }
     }

     public void saveTheInformation(String path, boolean stemmer){
        String fileName = "";
        if(stemmer){
            fileName = " with stemmer.txt";
        }
        else{
            fileName = " without stemmer.txt";
        }
         try {
             FileOutputStream fos = new FileOutputStream(path + "\\Docs Information" + fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos);
             oos.writeObject(allDocsInformation);
             oos.close();

            /*(BufferedWriter WriteFileBuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + "\\Docs Information" + fileName), "UTF-8"));
             StringBuilder toWrite = new StringBuilder();
             for (Map.Entry<String, String[]> entry: allDocsInformation.entrySet()) {
                 String key = entry.getKey();
                 String[] value = entry.getValue();
                 WriteFileBuffer.write("Doc number: " + key + ", " +
                         "max_tf: " + value[0] + " " + "unique terms: " + value[1] + " " +
                 "Origin city: " + value[2] + " " + "document length: " + value[3] + " " +
                 "date of write: " + value[4] + "\n");
             }
             WriteFileBuffer.write(toWrite.toString());
             WriteFileBuffer.flush();
             WriteFileBuffer.close();*/
         } catch (Exception e) {
             e.printStackTrace();
         }
         TreeMap<String, Integer> t2 = new TreeMap<>();
         for (Map.Entry<String, Integer[]> e:Indexer.dictionary.entrySet()
                 ) {
             t2.put(e.getKey(), e.getValue()[1]);
         }
     }

     public void setAllDocsInformation(String path, boolean stemmer){
         String fileName = "";
         if(stemmer){
             fileName = " with stemmer.txt";
         }
         else{
             fileName = " without stemmer.txt";
         }
         try {
             FileInputStream fis = new FileInputStream(path + "\\Docs Information" + fileName);
             ObjectInputStream ois = new ObjectInputStream(fis);
             allDocsInformation = (HashMap<String, String[]>) ois.readObject();
             ois.close();
         }catch(Exception e){
             System.out.println(e);
         }
     }
}
