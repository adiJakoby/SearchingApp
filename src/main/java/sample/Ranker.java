package sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Ranker {

    /**
     * The function gets list of relevant documents and rankBM25 them by the B25 equation.
     * @param queryAndAppears
     * @param termsOfQuery
     * @return map - foreach document it's rankBM25
     */
    public HashMap rankBM25(Map<String, ArrayList<String[]>> queryAndAppears, Map<String, Integer> termsOfQuery){
        HashMap<String, Double> result = new HashMap<>();

        for (String key: queryAndAppears.keySet()
             ) {
            double sum = 0;
            for (int i = 0; i < queryAndAppears.get(key).size(); i++){
                String[] termsInDoc = queryAndAppears.get(key).get(i);
                int numOfAppearanceInQuery = termsOfQuery.get(termsInDoc[0]);
                int numOfAppearanceInCorpus = Integer.parseInt(termsInDoc[2]);
                //constants of the B25 equation.
                double k = 0.6;
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

    public HashMap totalRanks(HashMap<String, Double> rankBM, HashMap<String, Double> semanticRankBM, boolean semanticCare, HashMap<String, Double> descriptionRankBM, boolean isDescription){
        HashMap<String, Double> result = new HashMap<>();
        double gradeForDescription = 0;
        double gradeForPure = 1;
        double gradeForSemantic = 0;
        if(isDescription && semanticCare){
            gradeForPure = 0.7;
            gradeForSemantic = 0.15;
            gradeForDescription = 0.15;
        }
        else if(isDescription && !semanticCare){
            gradeForPure = 0.8;
            gradeForDescription = 0.2;
        }else if(!isDescription && semanticCare){
            gradeForPure = 0.85;
            gradeForSemantic = 0.15;
        }

        if(!isDescription && semanticCare) {
            for (String document : rankBM.keySet()
                    ) {
                double originRank = rankBM.get(document);
                double semanticRank = 0;
                if (semanticRankBM.containsKey(document)) {
                    semanticRank = semanticRankBM.get(document);
                }
                double rank = (gradeForPure * originRank) + (gradeForSemantic * semanticRank);
                String date = DocsInformation.allDocsInformation.get(document)[4];
                if (!date.equals("")) {
                    String[] splitDate = date.split(" ");
                    System.out.println(0.03*(double)(Double.parseDouble(splitDate[2]) / 2018));
                    rank = (0.97*rank) + (0.03*(double)(Double.parseDouble(splitDate[2]) / 2018));
                }
                result.put(document, rank);
            }
            for (String document : semanticRankBM.keySet()
                    ) {
                if (!result.containsKey(document)) {
                    result.put(document, gradeForSemantic * semanticRankBM.get(document));
                }
            }
            return result;
        }
        else if(isDescription && !semanticCare){
            for (String document : rankBM.keySet()
                    ) {
                double originRank = rankBM.get(document);
                double semanticRank = 0;
                double descriptionRank = 0;
                if(descriptionRankBM.containsKey(document)){
                    descriptionRank = descriptionRankBM.get(document);
                }
                double rank = (gradeForPure * originRank) + (gradeForDescription * descriptionRank);
                String date = DocsInformation.allDocsInformation.get(document)[4];
                if (!date.equals("")) {
                    String[] splitDate = date.split(" ");
                    if(splitDate.length >= 3) {
                        if (!splitDate[2].equals("") && Parse.isNumeric(splitDate[2])) {
                            System.out.println(0.03*(double)(Double.parseDouble(splitDate[2]) / 2018));
                            rank = (0.97*rank) + (0.03*(double)(Double.parseDouble(splitDate[2]) / 2018));
                        }
                    }
                }
                result.put(document, rank);
            }
            for (String document : descriptionRankBM.keySet()
                    ) {
                double descriptionRank = 0;
                if (!result.containsKey(document)) {
                    result.put(document, (gradeForDescription * descriptionRankBM.get(document)));
                }
            }
            return result;
        }
        else if(isDescription && semanticCare){
            for (String document : rankBM.keySet()
                    ) {
                double originRank = rankBM.get(document);
                double semanticRank = 0;
                double descriptionRank = 0;
                if (semanticRankBM.containsKey(document)) {
                    semanticRank = semanticRankBM.get(document);
                }if(descriptionRankBM.containsKey(document)){
                    descriptionRank = descriptionRankBM.get(document);
                }
                double rank = (gradeForPure * originRank) + (gradeForSemantic * semanticRank) + (gradeForDescription * descriptionRank);
                String date = DocsInformation.allDocsInformation.get(document)[4];
                if (!date.equals("")) {
                    String[] splitDate = date.split(" ");
                    if(splitDate.length >= 3) {
                        if (!splitDate[2].equals("") && Parse.isNumeric(splitDate[2])) {
                            System.out.println(0.03*(double)(Double.parseDouble(splitDate[2]) / 2018));
                            rank = (0.97*rank) + (0.03*(double)(Double.parseDouble(splitDate[2]) / 2018));
                        }
                    }
                }
                result.put(document, rank);
            }
            for (String document : semanticRankBM.keySet()
                    ) {
                double descriptionRank;
                double semanticRank;
                if (!result.containsKey(document)) {
                    semanticRank = semanticRankBM.get(document);
                    if(descriptionRankBM.containsKey(document)){
                        descriptionRank = descriptionRankBM.get(document);
                        result.put(document, (gradeForSemantic * semanticRank) + (gradeForDescription * descriptionRank));
                    }
                    else{
                        result.put(document, gradeForSemantic * semanticRank);
                    }

                }
            }
            for (String document : descriptionRankBM.keySet()
                    ) {
                if (!result.containsKey(document)) {
                    result.put(document, gradeForDescription * descriptionRankBM.get(document));
                }
            }
            return result;
        }
       return rankBM;
    }
}
