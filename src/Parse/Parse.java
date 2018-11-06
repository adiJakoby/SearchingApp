package Parse;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Parse {
    private String[] splitDoc;
    private Map<String, Integer> tokens = new HashMap<String, Integer>();

    public void parser(String doc){
        doc = doc.replace('\n', ' ');
        splitDoc = doc.split(" ");

        //remove '.' and ',' from the end of the words
        for(int i = 0; i < splitDoc.length; i++){
            String word = splitDoc[i];
            if(word.charAt(word.length()-1) == '.' || word.charAt(word.length()-1) == ','){
                word = word.substring(0, word.length()-1);
            }
        }
        //remove stop words
        stopWordsRemover();

        for(int i = 0; i < splitDoc.length; i++){
            String word = splitDoc[i];
            if(Pattern.compile( "[0-9]" ).matcher(word).find() && !word.matches(".*[a-z].*")){
                numberTests(word, i);
            }
            else{
                wordTests(word, i);
            }
        }
    }

    //TODO: write the function
    private void stopWordsRemover(){

    }

    /**
     * Check if the word is a special case (for examples: 55m or 55bn)
     * @param word
     * @return true if it is a special case od number and false if its a kind of word
     */
    private String isContainSpecialChar(String word){
        if(word.charAt(word.length()-1) == 'm' || word.charAt(word.length()-1) == 'M'){
            String prefix = word.substring(0, word.length()-1);
            if(prefix.matches(".*[a-z].*")){
                return "*";
            }
            else{
                return "M";
            }
        }
        else if (word.charAt(word.length()-1) == 'n' || word.charAt(word.length()-1) == 'N'){
            if(word.charAt(word.length()-2) == 'b' || word.charAt(word.length()-2) == 'B'){
                String prefix = word.substring(0, word.length()-2);
                if(prefix.matches(".*[a-z].*")){
                    return "*";
                }
                else{
                    return "B";
                }
            }
        }
        return "Number";
    }



    //TODO: number tests
    private void numberTests(String word, int index){
        if(word.contains("%")){
            addToMap(word);
        }
        else if(word.contains("$")){
            priceCase(word, index);
        }
    }


    //TODO: number words
    private void wordTests(String word, int index){

    }

    /**
     * add a token to the tokens map (if the token is already exist increasing it's counter)
     * @param newToken
     */
    private void addToMap(String newToken){
        if(tokens.containsKey(newToken)){
            tokens.put(newToken, tokens.get(newToken)+1);
        }
        else{
            tokens.put(newToken, 1);
        }
    }

    /**
     * in case of price with the $ sign.
     * @param word
     * @param index
     */
    private void priceCase(String word, int index){
        word.replace(",", "");
        word.replace("$", "");
        String nextWord = splitDoc[index+1];
        nextWord.toLowerCase();
        if(nextWord == "million"){
            addToMap(word + " M Dollars");
        }
        else if(nextWord == "billion"){
            addToMap(word + "000 M Dollars");
        }
        else if(nextWord == "trillion"){
            addToMap(word + "000000 M Dollars");
        }
        else{
            int price = Integer.parseInt(word);
            if(price >= 1000000){
                price = price/1000000;
                word = Integer.toString(price);
                addToMap(word + " M Dollars");
            }
            else{
                addToMap(word + " M Dollars");
            }
        }
    }
}
