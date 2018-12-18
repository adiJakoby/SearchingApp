package sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Ranker {

    /**
     * The function gets list of relevant documents and rank them by the B25 equation.
     * @param queryAndAppears
     * @param termsOfQuery
     * @return map - foreach document it's rank
     */
    public HashMap rank(Map<String, ArrayList<String[]>> queryAndAppears, Map<String, Integer> termsOfQuery){
        HashMap<String, Double> result = new HashMap<>();

        for (String key: queryAndAppears.keySet()
             ) {
            double sum = 0;
            for (int i = 0; i < queryAndAppears.get(key).size(); i++){
                String[] termsInDoc = queryAndAppears.get(key).get(i);
                int numOfAppearanceInQuery = termsOfQuery.get(termsInDoc[0]);
                int numOfAppearanceInCorpus = Integer.parseInt(termsInDoc[2]);
                //constants of the B25 equation.
                int k = 2;
                double b = 0.75;
                int m = DocsInformation.allDocsInformation.size();
                int numOfAppearanceInDoc = Integer.parseInt(termsInDoc[1]);
                double avdl = DocsInformation.avdl;
                int docLength = Integer.parseInt(DocsInformation.allDocsInformation.get(key)[3]);

                double bXdDivideAvdl = b*docLength/avdl;
                double denominator = numOfAppearanceInDoc + (k * (1 - b + bXdDivideAvdl));
                double numerator = (k+1)*numOfAppearanceInDoc*numOfAppearanceInQuery;
                double log = Math.log10((m+1)/numOfAppearanceInCorpus);

                sum = sum + (numerator*log/denominator);

            }
            result.put(key, sum);
        }

        return result;
    }
}
