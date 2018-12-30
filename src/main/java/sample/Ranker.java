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

        /*for (String key: queryAndAppears.keySet()
                ) {
            double sum = 0;
            for (int i = 0; i < queryAndAppears.get(key).size(); i++) {
                String[] termsInDoc = queryAndAppears.get(key).get(i);
                int numOfAppearanceInQuery = termsOfQuery.get(termsInDoc[0]);
                int numOfAppearanceInCorpus = Integer.parseInt(termsInDoc[2]);
                int numOfAppearanceInDoc = Integer.parseInt(termsInDoc[1]);
                int docLength = Integer.parseInt(DocsInformation.allDocsInformation.get(key)[3]);
                double avdl = DocsInformation.avdl;
                //constants of the BM25 equation.
                double k = 1.6;
                double b = 0.75;

                double idf = Math.log10((DocsInformation.allDocsInformation.size() - numOfAppearanceInCorpus + 0.5) / (numOfAppearanceInCorpus + 0.5));
                double numerator = numOfAppearanceInDoc*(k + 1);
                double denominator = numOfAppearanceInDoc + (k*(1 - b + (b * docLength / avdl)));

                //System.out.println("doc: " + key + " word: " + termsInDoc[0] + " ");

                sum = sum + (idf * numerator / denominator);
            }
            result.put(key, sum);
        }

        return result;*/

        for (String key: queryAndAppears.keySet()
             ) {
            double sum = 0;
            for (int i = 0; i < queryAndAppears.get(key).size(); i++){
                String[] termsInDoc = queryAndAppears.get(key).get(i);
                int numOfAppearanceInQuery = termsOfQuery.get(termsInDoc[0]);
                int numOfAppearanceInCorpus = Integer.parseInt(termsInDoc[2]);
                //constants of the B25 equation.
                double k = 1.5;
                double b = 0.7;
                int m = DocsInformation.allDocsInformation.size();
                int numOfAppearanceInDoc = Integer.parseInt(termsInDoc[1]);
                double avdl = DocsInformation.avdl;
                int docLength = Integer.parseInt(DocsInformation.allDocsInformation.get(key)[3]);

                double bXdDivideAvdl = b*docLength/avdl;
                double denominator = numOfAppearanceInDoc + (k * (1 - b + bXdDivideAvdl));
                double numerator = (k+1)*numOfAppearanceInDoc*numOfAppearanceInQuery;
                double log = Math.log10((m+1)/numOfAppearanceInCorpus);

//                if(DocsInformation.entities.get(key) != null) {
//                    if (DocsInformation.entities.get(key).containsKey(termsInDoc[0])) {
//                        sum = sum + (DocsInformation.entities.get(key).get(termsInDoc[0]) / DocsInformation.entities.get(key).size());
//                    }
//                }

                sum = sum + (numerator*log/denominator);

            }
            result.put(key, sum);
        }

        return result;
    }

    public HashMap tfIdf(Map<String, ArrayList<String[]>> queryAndAppears, Map<String, Integer> termsOfQuery) {
        HashMap<String, Double> result = new HashMap<>();
        for (String key: queryAndAppears.keySet()
                ) {
            double sum = 0;
            for (int i = 0; i < queryAndAppears.get(key).size(); i++) {
                String[] termsInDoc = queryAndAppears.get(key).get(i);
                int numOfAppearanceInDoc = Integer.parseInt(termsInDoc[1]);
                int numOfAppearanceInCorpus = Integer.parseInt(termsInDoc[2]);
                int m = DocsInformation.allDocsInformation.size();

                sum = sum + (numOfAppearanceInDoc * (Math.log10(m/numOfAppearanceInCorpus)));
            }
            result.put(key, sum);
        }
        return result;
    }

    public HashMap<String, Double> tfIdfAndBM25(HashMap<String, Double> rankBM, HashMap<String, Double> tfIdf){
        HashMap<String, Double> result = new HashMap<>();
        for (String document : rankBM.keySet()
                ) {
            double bm = rankBM.get(document);
            if(tfIdf.containsKey(document)) {
                double tf = tfIdf.get(document);
                result.put(document, (0.98*bm + 0.02*tf));
            }else{
                result.put(document, bm);
            }
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
            gradeForPure = 0.8;
            gradeForSemantic = 0.2;
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
                    rank = (0.99*rank) + (0.01*(2018 - Double.parseDouble(splitDate[2])));
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
                            rank = (0.99*rank) + (0.01*(2018 - Double.parseDouble(splitDate[2])));
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
                            rank = (0.99*rank) + (0.01*(2018 - Double.parseDouble(splitDate[2])));
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
