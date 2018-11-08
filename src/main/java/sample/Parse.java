package sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

public class Parse {
    private String[] splitDoc;
    private Map<String, Integer> tokens = new HashMap<String, Integer>();
    private int splitDocIndex = 0;
    private Map<String, String> monthMap = new HashMap<String, String>();

    public Parse() {
        monthMap.put("january", "01");
        monthMap.put("jan", "01");
        monthMap.put("february", "02");
        monthMap.put("feb", "02");
        monthMap.put("march", "03");
        monthMap.put("mar", "03");
        monthMap.put("april", "04");
        monthMap.put("apr", "04");
        monthMap.put("may", "05");
        monthMap.put("june", "06");
        monthMap.put("jun", "06");
        monthMap.put("july", "07");
        monthMap.put("jul", "07");
        monthMap.put("august", "08");
        monthMap.put("aug", "08");
        monthMap.put("september", "09");
        monthMap.put("sep", "09");
        monthMap.put("october", "10");
        monthMap.put("oct", "10");
        monthMap.put("november", "11");
        monthMap.put("nov", "11");
        monthMap.put("december", "12");
        monthMap.put("dec", "12");
    }

    public void parser(String doc) {
        doc = doc.replace('\n', ' ');
        //splitDoc = doc.split(" ");
        splitDoc = mySplit(doc, " ");

        //remove '.' and ',' from the end of the words
        for (int i = 0; i < splitDoc.length; i++) {
            String word = splitDoc[i];
            if (word.length() >= 1) {
                while (word.charAt(word.length() - 1) == '.' || word.charAt(word.length() - 1) == ',' || word.charAt(word.length() - 1) == '?' ||
                        word.charAt(word.length() - 1) == ':' || word.charAt(word.length() - 1) == '!' || word.charAt(word.length() - 1) == ')'
                        || word.charAt(word.length() - 1) == '}' || word.charAt(word.length() - 1) == ']') {
                    splitDoc[i] = word.substring(0, word.length() - 1);
                    word = splitDoc[i];
                }
                while(word.charAt(0) == '(' || word.charAt(0) == '{' || word.charAt(0) == '['){
                    splitDoc[i] = splitDoc[i].substring(1, splitDoc[i].length());
                    word = splitDoc[i];
                }
            }
        }
        //remove stop words
        stopWordsRemover();

        while (splitDocIndex < splitDoc.length) {
            String word = splitDoc[splitDocIndex];
            if (Pattern.compile("[0-9]").matcher(word).find() && isReadyForNumberTest(word)) {
                numberTests(word);
            } else {
                wordTests(word);
            }
        }
        System.out.println("helloooo");

    }

    //TODO: write the function
    private void stopWordsRemover() {

    }

    /**
     * Check if the word is a special case (for examples: 55m or 55bn)
     *
     * @param word
     * @return true if it is a special case od number and false if its a kind of word
     */
    private String isContainSpecialChar(String word) {
        if (word.charAt(word.length() - 1) == 'm' || word.charAt(word.length() - 1) == 'M') {
            String prefix = word.substring(0, word.length() - 1);
            if (prefix.matches(".*[a-z].*")) {
                return "*";
            } else {
                return "M";
            }
        } else if (word.charAt(word.length() - 1) == 'n' || word.charAt(word.length() - 1) == 'N') {
            if (word.charAt(word.length() - 2) == 'b' || word.charAt(word.length() - 2) == 'B') {
                String prefix = word.substring(0, word.length() - 2);
                if (prefix.matches(".*[a-z].*")) {
                    return "*";
                } else {
                    return "B";
                }
            }
        }
        return "Number";
    }


    //TODO: number tests
    private void numberTests(String word) {
        String secondWord = "";
        String thirdWord = "";
        String forthWord = "";
        if (splitDocIndex + 1 < splitDoc.length) {
            secondWord = splitDoc[splitDocIndex + 1];
        }
        if (splitDocIndex + 2 < splitDoc.length) {
            thirdWord = splitDoc[splitDocIndex + 2];
        }
        if (splitDocIndex + 3 < splitDoc.length) {
            forthWord = splitDoc[splitDocIndex + 3];
        }
        secondWord = secondWord.toLowerCase();
        thirdWord = thirdWord.toLowerCase();
        forthWord = forthWord.toLowerCase();
        if (word.contains("%")) {
            addToMap(word);
        } else if (word.contains("$")) {
            dollarSignPriceCase(word);
        }else if(word.contains("/")) {
            String[] checkIfFraction = mySplit(word, "/");
            if (checkIfFraction.length == 2) {
                if (isNumeric(checkIfFraction[0]) && isNumeric(checkIfFraction[1])) {
                    addToMap(word);
                }
            }
        } else if (secondWord.equals("percent") || secondWord.equals("percentage")) {
            addToMap(word + "%");
            splitDocIndex++;
        } else if (secondWord.equals("dollars")) {
            word.replace(",", "");
            numbersInMillionScale(word);
            splitDocIndex++;
        } else if (secondWord.contains("/")) {
            String[] checkIfFraction = mySplit(secondWord, "/");
            if (checkIfFraction.length == 2) {
                if (isNumeric(checkIfFraction[0]) && isNumeric(checkIfFraction[1])) {
                    splitDocIndex++;
                    word.replace(",", "");
                    if (thirdWord.contains("dollars")) {
                        splitDocIndex++;
                        addToMap(word + " " + secondWord + " Dollars");
                    } else {
                        addToMap(word + " " + secondWord);
                    }
                }
            }
        } else if (secondWord.equals("million")) {
            // 100 million u.s. dollars = 100 M Dollars
            if (thirdWord.equals("u.s") && forthWord.equals("dollars")) {
                word.replace(",", "");
                addToMap(word + " M Dollars");
                splitDocIndex++;
                splitDocIndex++;
            }
            // 10 million = 10M
            else {
                word.replace(",", "");
                addToMap(word + "M");
                splitDocIndex++;
            }
        } else if (secondWord.equals("billion")) {
            // 100 billion U.S. Dollars = 100000 M Dollars
            if (thirdWord.equals("u.s") && forthWord.equals("dollars")) {
                word.replace(",", "");
                addToMap(word + "000 M Dollars");
                splitDocIndex++;
                splitDocIndex++;
            }
            // 10 billion = 10B
            else {
                word.replace(",", "");
                addToMap(word + "B");
                splitDocIndex++;
            }
        } else if (secondWord.equals("trillion")) {
            // 100 trillion U.S. Dollars = 100000000 M Dollars
            if (thirdWord.equals("u.s") && forthWord.equals("dollars")) {
                word.replace(",", "");
                addToMap(word + "000000 M Dollars");
                splitDocIndex++;
                splitDocIndex++;
            }
            // 7 trillion = 700B
            else {
                word.replace(",", "");
                addToMap(word + "B");
                splitDocIndex++;
            }
        }
        // 123 thousands = 123K
        else if (secondWord.equals("thousand")) {
            word.replace(",", "");
            addToMap(word + "K");
            splitDocIndex++;
        } else if (secondWord.equals("bn")) {
            //100 bn dollars = 100000 M Dollars
            if (thirdWord.equals("dollars")) {
                word.replace(",", "");
                addToMap(word + "000 M Dollars");
                splitDocIndex++;
            }
        } else if (secondWord.equals("m")) {
            //100 m dollars = 100 M Dollars
            if (thirdWord.equals("dollars")) {
                word.replace(",", "");
                addToMap(word + " M Dollars");
                splitDocIndex++;
            }
        }
        //month case 14 May = 05-14
        else if (monthMap.containsKey(secondWord) && isNumeric(word)) {
            splitDocIndex++;
            addToMap(monthMap.get(secondWord) + "-" + word);
        } else {
            word.replace(",", "");
            if (isNumeric(word)) {
                divideNumbers(word);
            }
        }

        splitDocIndex++;
    }


    private void wordTests(String word) {
        String secondWord = "";
        String thirdWord = "";
        String forthWord = "";
        if (splitDocIndex + 1 < splitDoc.length) {
            secondWord = splitDoc[splitDocIndex + 1];
        }
        if (splitDocIndex + 2 < splitDoc.length) {
            thirdWord = splitDoc[splitDocIndex + 2];
        }
        if (splitDocIndex + 3 < splitDoc.length) {
            forthWord = splitDoc[splitDocIndex + 3];
        }
        if(monthMap.containsKey(word.toLowerCase())){
            if(isNumeric(secondWord)){
                double d = Double.parseDouble(secondWord);
                if(isNaturalNumber(d)){
                    int date = (int) d;
                    if(date >= 1 && date <= 31){
                        addToMap(monthMap.get(word.toLowerCase()) + "-" + secondWord);
                    }else if(secondWord.length() == 4){
                        addToMap( secondWord + "-" + monthMap.get(word.toLowerCase()));
                    }
                    splitDocIndex++;
                }
            }
        }else if((word.toLowerCase()).equals("between") && isNumeric(secondWord) && (thirdWord.toLowerCase()).equals("and") && isNumeric(forthWord)){
            splitDocIndex = splitDocIndex + 3;
            divideNumbers(secondWord);
            divideNumbers(forthWord);
            addToMap(secondWord + "-" + forthWord);
        }else{
            if(Character.isLowerCase(word.charAt(0))){
                addToMapLowCase(word);
            }else{
                addToMapUpCase(word);
            }
        }

        splitDocIndex++;
    }

    /**
     * add a token to the tokens map (if the token is already exist increasing it's counter)
     *
     * @param newToken
     */
    private void addToMap(String newToken) {
        if (tokens.containsKey(newToken)) {
            tokens.put(newToken, tokens.get(newToken) + 1);
        } else {
            tokens.put(newToken, 1);
        }
    }

    /**
     * in case of price with the $ sign.
     *
     * @param word
     */
    private void dollarSignPriceCase(String word) {
        word.replace(",", "");
        word.replace("$", "");
        String nextWord = splitDoc[splitDocIndex + 1];
        nextWord.toLowerCase();
        if (nextWord.equals("million")) {
            addToMap(word + " M Dollars");
        } else if (nextWord.equals("billion")) {
            addToMap(word + "000 M Dollars");
        } else if (nextWord.equals("trillion")) {
            addToMap(word + "000000 M Dollars");
        } else {
            numbersInMillionScale(word);
        }
    }

    /**
     * get number, if the number is smaller than 1 million don't change, else divide and save it in million scale.
     * add it to the map as Dollar
     *
     * @param orgNumber
     */
    private void numbersInMillionScale(String orgNumber) {
        double price = Double.parseDouble(orgNumber);
        if (price >= 1000000) {
            price = price / 1000000;
            addToMap(Double.toString(price) + "M Dollars");
        } else {
            addToMap(Double.toString(price) + " Dollars");
        }
    }

    /**
     * @param toCheck
     * @return true if the string toCheck is a number
     */
    private boolean isNumeric(String toCheck) {
        try {
            double d = Double.parseDouble(toCheck);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void divideNumbers(String toDivide) {
        double d = Double.parseDouble(toDivide);
        if (d >= 1000000000) {
            d = d / 1000000000;
            String toWrite;
            if (isNaturalNumber(d)) {
                int di = (int) d;
                toWrite = Integer.toString(di);
            } else {
                toWrite = Double.toString(d);
            }
            addToMap(toWrite + "B");
        } else if (d >= 1000000) {
            d = d / 1000000;
            String toWrite;
            if (isNaturalNumber(d)) {
                int di = (int) d;
                toWrite = Integer.toString(di);
            } else {
                toWrite = Double.toString(d);
            }
            addToMap(toWrite + "M");
        } else if (d >= 1000) {
            d = d / 1000;
            String toWrite;
            if (isNaturalNumber(d)) {
                int di = (int) d;
                toWrite = Integer.toString(di);
            } else {
                toWrite = Double.toString(d);
            }
            addToMap(toWrite + "K");
        } else {
            String toWrite;
            if (isNaturalNumber(d)) {
                int di = (int) d;
                toWrite = Integer.toString(di);
            } else {
                toWrite = Double.toString(d);
            }
            addToMap(toWrite);
        }
    }

    private static String[] mySplit(String str, String regex) {
        Vector<String> result = new Vector<String>();
        int start = 0;
        int pos = str.indexOf(regex);
        while (pos >= start) {
            if (pos > start) {
                result.add(str.substring(start, pos));
            }
            start = pos + regex.length();
            //result.add(regex);
            pos = str.indexOf(regex, start);
        }
        if (start < str.length()) {
            result.add(str.substring(start));
        }
        String[] array = result.toArray(new String[0]);
        return array;
    }

    /**
     * @param number
     * @return true if a number is an whole number.
     */
    private boolean isNaturalNumber(double number) {
        int x = (int) number;
        double y = number - x;
        if (y > 0) {
            return false;
        }
        return true;
    }

    /**
     * @param word
     * @return false if the word is contain a letter
     */
    private boolean isReadyForNumberTest(String word) {
        char[] chars = word.toCharArray();
        for (char c : chars) {
            if (Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * in case the word saved in up letters - change it to low letters.
     * else add it a usual
     * @param word the 1st char is low char
     */
    private void addToMapLowCase(String word){
        if(tokens.containsKey(word.toUpperCase())){
            int counter = tokens.get(word.toUpperCase());
            counter++;
            tokens.remove(word.toUpperCase());
            tokens.put(word, counter);

        }else{
            addToMap(word);
        }
    }

    /**
     * in case the word saved in low case - saved it in low case
     * otherwise save it in up case
     * @param word - the 1st char is up char
     */
    private void addToMapUpCase(String word){
        if(tokens.containsKey(word.toLowerCase())){
            addToMap(word.toLowerCase());
        }else{
            addToMap(word.toUpperCase());
        }
    }
}
