package sample;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Stemmer {

    SnowballStemmer snowballStemmer = new englishStemmer();
    Indexer indexer = new Indexer();
    DocsInformation docsInformation = new DocsInformation();

    public void stemming(Map<String, Integer> tokens, String docNo, boolean doneFile) {
        int max = 0;
        String maxTerm = "";
        TreeMap<String, Integer> stems = new TreeMap<>(new MyComp());
        for (String key : tokens.keySet()) {
            if(tokens.get(key) > max){
                maxTerm = key;
                max = tokens.get(key);
            }
            snowballStemmer.setCurrent(key);
            snowballStemmer.stem();
            String newStem = snowballStemmer.getCurrent();
            if(Character.isLowerCase(key.charAt(0))) {
                newStem = newStem.toLowerCase();
            }
            else{
                newStem = newStem.toUpperCase();
            }
            if (stems.containsKey(newStem)) {
                int counter = stems.get(newStem);
                stems.put(newStem, counter + tokens.get(key));
            }
            else{
                stems.put(newStem,tokens.get(key));
            }
        }

        docsInformation.addMaxTf(maxTerm, docNo);
        docsInformation.addUniqueTermsAmount(docNo, stems.size());
        indexer.indexing(stems, docNo, doneFile);
    }

    class MyComp implements Comparator<String> {
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
