package sample;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import java.util.HashMap;
import java.util.Map;

public class Stemmer {

    SnowballStemmer snowballStemmer = new englishStemmer();

    public void stemming(Map<String, Integer> tokens) {
        Map<String, Integer> stems = new HashMap<>();
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
    }
}
