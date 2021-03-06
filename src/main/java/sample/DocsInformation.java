package sample;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DocsInformation {
    static HashMap<String, String[]> allDocsInformation = new HashMap<>();
    static HashMap<String, HashMap<String, Integer>> entities = new HashMap<>();
    static HashSet<String> allLanguages = new HashSet<>();
    static double avdl = 0;

    /**
     * set the max term frequency of a doc
     * @param max_tf
     * @param docName
     */
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

    /**
     * set the number of the unique terms of a doc
     * @param docName
     * @param amount
     */
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

    /**
     * set the city that the doc has been written in
     * @param docName
     * @param city
     */
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

    /**
     * set the doc length
     * @param docName
     * @param length
     */
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

    /**
     * set the posting date of the doc
     * @param docName
     * @param date
     */
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

    /**
     * add a dominant entity of the doc
     * @param docName
     * @param newEntity
     * @param numOfAppearances
     */
    public static void addDominantEntities(String docName, String newEntity, int numOfAppearances){
        if(entities.containsKey(docName)){
            HashMap<String, Integer> currentDocEntities = entities.get(docName);
            if(currentDocEntities.size() == 5){
                if(currentDocEntities.get(getMinEntityString(docName)) < numOfAppearances){
                    currentDocEntities.remove(getMinEntityString(docName));
                    currentDocEntities.put(newEntity, numOfAppearances);
                    entities.put(docName, currentDocEntities);
                }
            }else{
                currentDocEntities.put(newEntity, numOfAppearances);
                entities.put(docName, currentDocEntities);
            }
        }else{
            HashMap<String, Integer> currentDocEntities = new HashMap<>();
            currentDocEntities.put(newEntity, numOfAppearances);
            entities.put(docName, currentDocEntities);
        }
    }

    /**
     * write all the information about documents on the disk
     * @param path
     * @param stemmer
     */
     public void saveTheInformation(String path, boolean stemmer){
        String[] avdlVal = {Double.toString(avdl)};
        allDocsInformation.put("avdl", avdlVal);
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
             FileOutputStream fos1 = new FileOutputStream(path + "\\Entities Information" + fileName);
             ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
             oos1.writeObject(entities);
             oos1.close();
             FileOutputStream fos2 = new FileOutputStream(path + "\\Languages Information" + fileName);
             ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
             oos2.writeObject(allLanguages);
             oos2.close();

         } catch (Exception e) {
             e.printStackTrace();
         }
         TreeMap<String, Integer> t2 = new TreeMap<>();
         for (Map.Entry<String, Integer[]> e:Indexer.dictionary.entrySet()
                 ) {
             t2.put(e.getKey(), e.getValue()[1]);
         }
     }

    /**
     * reading and setting all the information on the documents from the disk to the app memory
     * @param path
     * @param stemmer
     */
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
             avdl = Double.parseDouble(allDocsInformation.get("avdl")[0]);
             FileInputStream fis1 = new FileInputStream(path + "\\Entities Information" + fileName);
             ObjectInputStream ois1 = new ObjectInputStream(fis1);
             entities = (HashMap<String, HashMap<String, Integer>>) ois1.readObject();
             ois1.close();
             FileInputStream fis2 = new FileInputStream(path + "\\Languages Information" + fileName);
             ObjectInputStream ois2 = new ObjectInputStream(fis2);
             allLanguages = (HashSet<String>) ois2.readObject();
             ois2.close();
         }catch(Exception e){
             System.out.println(e);
         }
     }

    /**
     *
     * @param docNum
     * @return the entity with the minimum rank of the given document
     */
     private static String getMinEntityString(String docNum){
        int min = Integer.MAX_VALUE;
        String result = "";
         for (String entity: entities.get(docNum).keySet()
              ) {
             if(entities.get(docNum).get(entity) < min){
                 min = entities.get(docNum).get(entity);
                 result = entity;
             }
         }
         return result;
     }
}
