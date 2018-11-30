package sample;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class DocsInformation {
    static HashMap<String, String[]> allDocsInformation = new HashMap<>();

    public void addMaxTf(String term, String docName){
        if(allDocsInformation.containsKey(docName)){
            String[] temp = allDocsInformation.get(docName);
            temp[0] = term;
            allDocsInformation.put(docName, temp);
        }
        else{
            String[] temp = new String[5];
            temp[0] = term;
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

     public void saveTheInformation(String path){
         try {
             FileWriter fw = new FileWriter(path + "Docs Information.txt");
             BufferedWriter WriteFileBuffer = new BufferedWriter(fw);
             StringBuilder toWrite = new StringBuilder();
             for (Map.Entry<String, String[]> entry: allDocsInformation.entrySet()) {
                 String key = entry.getKey();
                 String[] value = entry.getValue();
                 WriteFileBuffer.write("Doc number: " + key + ":\n" +
                         "max_tf: " + value[0] + "\n" + "unique terms: " + value[1] + "\n" +
                 "Origin city: " + value[2] + "\n" + "document length: " + value[3] + "\n" +
                 "date of write: " + value[4] + "\n");
             }
             WriteFileBuffer.write(toWrite.toString());
             WriteFileBuffer.close();
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
}
