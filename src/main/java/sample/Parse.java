package sample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

public class Parse {
    private String[] splitDoc;
    private Map<String, Integer> tokens = new HashMap<String, Integer>();
    private int splitDocIndex = 0;
    private Map<String, String> monthMap = new HashMap<String, String>();
    HashSet<String> stopWordsSet = new HashSet<String>();


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

        String filePath = "C:\\Users\\adi\\IdeaProjects\\SearchingApp\\src\\main\\java\\stop_words.txt";
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            try {
                line = reader.readLine();
                while (line != null) {
                    stopWordsSet.add(line);
                    line = reader.readLine();
                }
                reader.close();
            }catch (IOException ioe){
                System.out.println("Problem with the stop word file");
            }
        }catch(FileNotFoundException fnfe){
            System.out.println("Problem with the stop word file");
        }
    }

    public void parser(String doc) {
        doc = doc.replace('\n', ' ');
        splitDoc = mySplit(doc, " ");

        //remove '.' and ',' from the end of the words
        for (int i = 0; i < splitDoc.length; i++) {
            String word = splitDoc[i];
            if (word.length() >= 1) {
                while (word.charAt(word.length() - 1) == '.' || word.charAt(word.length() - 1) == ',' || word.charAt(word.length() - 1) == '?' ||
                        word.charAt(word.length() - 1) == ':' || word.charAt(word.length() - 1) == '!' || word.charAt(word.length() - 1) == ')'
                        || word.charAt(word.length() - 1) == '}' || word.charAt(word.length() - 1) == ']' || word.charAt(word.length() - 1) == ';'
                        || word.charAt(word.length() - 1) == '"') {
                    splitDoc[i] = word.substring(0, word.length() - 1);
                    word = splitDoc[i];
                }
                while (word.charAt(0) == '(' || word.charAt(0) == '{' || word.charAt(0) == '[' || word.charAt(0) == '"') {
                    splitDoc[i] = splitDoc[i].substring(1, splitDoc[i].length());
                    word = splitDoc[i];
                }
            }
        }

        while (splitDocIndex < splitDoc.length) {
            String word = splitDoc[splitDocIndex];
            if(!stopWordsSet.contains(word.toLowerCase())) {
                if (word.contains("-")) {
                    hyphenTests(word);
                } else if (Pattern.compile("[0-9]").matcher(word).find() && isReadyForNumberTest(word)) {
                    numberTests(word);
                } else {
                    wordTests(word);
                }
            }
            else{
                splitDocIndex++;
            }
            System.out.println(splitDocIndex);
        }
        System.out.println("helloooo");

    }

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
        } else if (word.contains("/")) {
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
            // 7 trillion = 7000B
            else {
                word.replace(",", "");
                addToMap(word + "000" + "B");
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
        if (monthMap.containsKey(word.toLowerCase())) {
            if (isNumeric(secondWord)) {
                double d = Double.parseDouble(secondWord);
                if (isNaturalNumber(d)) {
                    int date = (int) d;
                    if (date >= 1 && date <= 31) {
                        addToMap(monthMap.get(word.toLowerCase()) + "-" + secondWord);
                    } else if (secondWord.length() == 4) {
                        addToMap(secondWord + "-" + monthMap.get(word.toLowerCase()));
                    }
                    splitDocIndex++;
                }
            }
        } else if ((word.toLowerCase()).equals("between") && isNumeric(secondWord) && (thirdWord.toLowerCase()).equals("and") && isNumeric(forthWord)) {
            splitDocIndex = splitDocIndex + 3;
            divideNumbers(secondWord);
            divideNumbers(forthWord);
            addToMap(secondWord + "-" + forthWord);
        } else {
            if (Character.isLowerCase(word.charAt(0))) {
                addToMapLowCase(word);
            } else {
                addToMapUpCase(word);
            }
        }
        splitDocIndex++;
    }


    private void hyphenTests(String word) {
        String[] splitExpression = mySplit(word, "-");
        String oneBeforeWord = "";
        String oneAfterWord = "";
        String termToAdd = "";

        if (splitDocIndex - 1 >= 0) {
            oneBeforeWord = splitDoc[splitDocIndex - 1];
        }
        if (splitDocIndex + 1 < splitDoc.length) {
            oneAfterWord = splitDoc[splitDocIndex + 1];
        }


        //word-word-word case add to the map as an expression and each word by it self.
        if (splitExpression.length == 3) {
            addToMap(word);
            for (int i = 0; i < 3; i++) {
                if(!stopWordsSet.contains(splitExpression[i].toLowerCase())) {
                    if (Character.isLowerCase(splitExpression[i].charAt(0))) {
                        addToMapLowCase(splitExpression[i]);
                    } else {
                        addToMapUpCase(splitExpression[i]);
                    }
                }
            }
            splitDocIndex++;
        } else if (splitExpression.length == 2) {
            if (isNumeric(oneBeforeWord)) {
                if ((splitExpression[0].toLowerCase()).equals("thousand")) {
                    oneBeforeWord.replace(",", "");
                    addToMap(oneBeforeWord + "K");
                    termToAdd = termToAdd + oneBeforeWord + "K";
                } else if ((splitExpression[0].toLowerCase()).equals("million")) {
                    oneBeforeWord.replace(",", "");
                    addToMap(oneBeforeWord + "M");
                    termToAdd = termToAdd + oneBeforeWord + "M";
                } else if ((splitExpression[0].toLowerCase()).equals("billion")) {
                    oneBeforeWord.replace(",", "");
                    addToMap(oneBeforeWord + "B");
                    termToAdd = termToAdd + oneBeforeWord + "B";
                } else if ((splitExpression[0].toLowerCase()).equals("trillion")) {
                    oneBeforeWord.replace(",", "");
                    addToMap(oneBeforeWord + "000" + "B");
                    termToAdd = termToAdd + oneBeforeWord + "000" + "B";
                }
            } else {
                if(!stopWordsSet.contains(splitExpression[0].toLowerCase())){
                    addToMap(splitExpression[0]);
                }
                termToAdd = termToAdd + splitExpression[0];
            }
            termToAdd = termToAdd + '-';
            if (isNumeric(splitExpression[1])) {
                splitExpression[1].replace(",", "");
                if ((oneAfterWord.toLowerCase()).equals("thousand")) {
                    addToMap(splitExpression[1] + "K");
                    termToAdd = termToAdd + splitExpression[1] + "K";
                    splitDocIndex++;
                } else if ((oneAfterWord.toLowerCase()).equals("million")) {
                    addToMap(splitExpression[1] + "M");
                    termToAdd = termToAdd + splitExpression[1] + "M";
                    splitDocIndex++;
                } else if ((oneAfterWord.toLowerCase()).equals("billion")) {
                    addToMap(splitExpression[1] + "B");
                    termToAdd = termToAdd + splitExpression[1] + "B";
                    splitDocIndex++;
                } else if ((oneAfterWord.toLowerCase()).equals("trillion")) {
                    addToMap(splitExpression[1] + "000" + "B");
                    termToAdd = termToAdd + splitExpression[1] + "000" + "B";
                    splitDocIndex++;
                } else {
                    termToAdd = termToAdd + splitExpression[1];
                }
            } else {
                termToAdd = termToAdd + splitExpression[1];
                if(!stopWordsSet.contains(splitExpression[1].toLowerCase())){
                    addToMap(splitExpression[1]);
                }
            }
            addToMap(termToAdd);
            splitDocIndex++;
        } else if (splitExpression.length == 1) {
            if (isNumeric(splitExpression[0])) {
                numberTests(word);
            } else {
                if(!stopWordsSet.contains(splitExpression[0].toLowerCase())){
                    wordTests(splitExpression[0]);
                }
                else{
                    splitDocIndex++;
                }
            }
        } else {
            splitDocIndex++;
        }
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
     *
     * @param word the 1st char is low char
     */
    private void addToMapLowCase(String word) {
        if (tokens.containsKey(word.toUpperCase())) {
            int counter = tokens.get(word.toUpperCase());
            counter++;
            tokens.remove(word.toUpperCase());
            tokens.put(word, counter);

        } else {
            addToMap(word);
        }
    }

    /**
     * in case the word saved in low case - saved it in low case
     * otherwise save it in up case
     *
     * @param word - the 1st char is up char
     */
    private void addToMapUpCase(String word) {
        if (tokens.containsKey(word.toLowerCase())) {
            addToMap(word.toLowerCase());
        } else {
            addToMap(word.toUpperCase());
        }
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

    /**
     *
     * @param file
     * @return the text tag content
     */
    private String getTextTagContent(String file){
        Document doc = (Document) Jsoup.parse(file);
        org.jsoup.nodes.Element link =  doc.select("Text").first();
        String AllDoccontent = link.outerHtml();
        return AllDoccontent;
    }
}
