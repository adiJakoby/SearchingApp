package sample;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import sun.reflect.generics.tree.Tree;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Stemmer {

    SnowballStemmer snowballStemmer = new englishStemmer();
    Indexer indexer = new Indexer();

    public void stemming(Map<String, Integer> tokens, String docNo, String fileName) {
        TreeMap<String, Integer> stems = new TreeMap<>(new MyComp());
        for (String key : tokens.keySet()) {
            snowballStemmer.setCurrent(key);
            snowballStemmer.stem();
            String newStem = snowballStemmer.getCurrent();
            if (stems.containsKey(newStem)) {
                int counter = stems.get(newStem);
                stems.put(newStem, counter + tokens.get(key));
            }
            else{
                stems.put(newStem,tokens.get(key));
            }
        }

        //indexer.indexing(stems, docNo, fileName);
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
