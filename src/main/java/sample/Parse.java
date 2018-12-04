package sample;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Parse {
    private String[] splitDoc;
    private Map<String, Integer> tokens = new HashMap<>();
    private int splitDocIndex = 0;
    private Map<String, String> monthMap = new HashMap<String, String>();
    HashSet<String> stopWordsSet = new HashSet<String>();
    Stemmer stemmer = new Stemmer();
    static CitiesIndexer myCitiesIndexer = new CitiesIndexer();
    String docName;
    DocsInformation docsInformation = new DocsInformation();
    int maxTf = 0;
    String maxTerm = "";


    public Parse(String stopWordPath) {
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

        String filePath = stopWordPath + "\\stop_words.txt";
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
            } catch (IOException ioe) {
                System.out.println("Problem with the stop word file");
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println("Problem with the stop word file");
        }
    }

    static void apiGetStart(){
        myCitiesIndexer.api_Connection();
    }

    public void parser(String doc, String docNo, boolean doneFile,String city, boolean toStemmer) {
        maxTf = 0;
        maxTerm = "";
        //TODO replace ( ) { } [ ] to space
        if(!city.equals("")&&!city.contains("<F P=")) {
            myCitiesIndexer.addCityToCorpusMap(city, docNo);
        }
        this.docName=docNo;
        char[] toReplace = {'_', ',', '¥', '�', ')', '(', '{', '}', '[', ']', '*', '|', '#', '!', ';', '<', '>', '~', '^', '&', '=', '+', ':', '?', '"'};
        doc = OurReplace(doc,"\n", " ");
        doc = OurReplace(doc, toReplace, " ");
        splitDoc = mySplit(doc, " ");
        for (int i = 0; i < splitDoc.length; i++) {
            splitDoc[i] = removeFromTheEdges(splitDoc[i]);
        }

        int docLength = 0;
        while (splitDocIndex < splitDoc.length) {
            String word = splitDoc[splitDocIndex];
            if (!stopWordsSet.contains(word.toLowerCase())) {
                docLength++;
                if (word.contains("-")) {
                    hyphenTests(word);
                } else if (Pattern.compile("[0-9]").matcher(word).find() && isReadyForNumberTest(word)) {
                    numberTests(word);
                } else {
                    wordTests(word);
                }
            } else {
                splitDocIndex++;
            }
        }
        docsInformation.addDocLength(docNo, docLength);
        stemmer.stemming(tokens, docNo, doneFile, toStemmer, maxTf, maxTerm);
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
            word = OurReplace(word,",", "");
            numbersInMillionScale(word);
            splitDocIndex++;
        } else if (secondWord.contains("/")) {
            String[] checkIfFraction = mySplit(secondWord, "/");
            if (checkIfFraction.length == 2) {
                if (isNumeric(checkIfFraction[0]) && isNumeric(checkIfFraction[1])) {
                    splitDocIndex++;
                    word = OurReplace(word,",", "");
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
                word = OurReplace(word,",", "");
                addToMap(word + " M Dollars");
                splitDocIndex++;
                splitDocIndex++;
            }
            // 10 million = 10M
            else {
                word = OurReplace(word,",", "");
                addToMap(word + "M");
                splitDocIndex++;
            }
        } else if (secondWord.equals("billion")) {
            // 100 billion U.S. Dollars = 100000 M Dollars
            if (thirdWord.equals("u.s") && forthWord.equals("dollars")) {
                word = OurReplace(word,",", "");
                addToMap(word + "000 M Dollars");
                splitDocIndex++;
                splitDocIndex++;
            }
            // 10 billion = 10B
            else {
                word = OurReplace(word,",", "");
                addToMap(word + "B");
                splitDocIndex++;
            }
        } else if (secondWord.equals("trillion")) {
            // 100 trillion U.S. Dollars = 100000000 M Dollars
            if (thirdWord.equals("u.s") && forthWord.equals("dollars")) {
                word = OurReplace(word,",", "");
                addToMap(word + "000000 M Dollars");
                splitDocIndex++;
                splitDocIndex++;
            }
            // 7 trillion = 7000B
            else {
                word = OurReplace(word,",", "");
                addToMap(word + "000" + "B");
                splitDocIndex++;
            }
        }
        // 123 thousands = 123K
        else if (secondWord.equals("thousand")) {
            word = OurReplace(word,",", "");
            addToMap(word + "K");
            splitDocIndex++;
        } else if (secondWord.equals("bn")) {
            //100 bn dollars = 100000 M Dollars
            if (thirdWord.equals("dollars")) {
                word = OurReplace(word,",", "");
                addToMap(word + "000 M Dollars");
                splitDocIndex++;
            }
        } else if (secondWord.equals("m")) {
            //100 m dollars = 100 M Dollars
            if (thirdWord.equals("dollars")) {
                word = OurReplace(word,",", "");
                addToMap(word + " M Dollars");
                splitDocIndex++;
            }
        }
        //month case 14 May = 05-14
        else if (monthMap.containsKey(secondWord) && isNumeric(word)) {
            splitDocIndex++;
            addToMap(monthMap.get(secondWord) + "-" + word);
        } else {
            word = OurReplace(word,",", "");
            if (isNumeric(word)) {
                divideNumbers(word);
            }
        }

        splitDocIndex++;
    }


    private void wordTests(String word) {
        if (word.length() > 0) {
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
                    double d = ourParseToDouble(secondWord);
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
            } else if (word.toLowerCase().equals("mr") || word.toLowerCase().equals("mrs")
                    || word.toLowerCase().equals("ms") || word.toLowerCase().equals("dr")){
                addToMap(word.toUpperCase() + "." + " " + secondWord + " " + thirdWord);
                if (Character.isLowerCase(word.charAt(0))) {
                    addToMapLowCase(word);
                } else {
                    addToMapUpCase(word);
                }
            } else if (word.length() > 2 && word.charAt(word.length()-2) == '\'' && (word.charAt(word.length()-1) == 's')){
                if (Character.isLowerCase(word.charAt(0))) {
                    addToMapLowCase(word);
                } else {
                    addToMapUpCase(word);
                }
                word = word.substring(0,word.length()-2);
                if (Character.isLowerCase(word.charAt(0))) {
                    addToMapLowCase(word);
                } else {
                    addToMapUpCase(word);
                }
            }
            else {
                char[] charsToRemove = {'-', '$', '.', '/'};
                word = OurReplace(word, charsToRemove, "");
                if(word.length() > 0) {
                    if (Character.isLowerCase(word.charAt(0))) {
                        addToMapLowCase(word);
                    } else {
                        addToMapUpCase(word);
                    }
                }
            }
        }
        splitDocIndex++;
    }


    private void hyphenTests(String word) {
        String[] splitExpression = mySplit(word, "-");
        String oneBeforeWord = "";
        String oneAfterWord = "";
        String termToAdd = "";
        char[] charsToRemove = {'-', '$', '.', '/'};

        if (splitDocIndex - 1 >= 0) {
            oneBeforeWord = splitDoc[splitDocIndex - 1];
        }
        if (splitDocIndex + 1 < splitDoc.length) {
            oneAfterWord = splitDoc[splitDocIndex + 1];
        }


        //word-word-word case add to the map as an expression and each word by it self.
        if (splitExpression.length == 3) {
            addToMap(splitExpression[0].toLowerCase() + "-" + splitExpression[1].toLowerCase() + "-" + splitExpression[2].toLowerCase());
            for (int i = 0; i < 3; i++) {
                if (!stopWordsSet.contains(splitExpression[i].toLowerCase())) {
                    if (Character.isLowerCase(splitExpression[i].charAt(0))) {
                        String temp = OurReplace(splitExpression[i].toLowerCase(), charsToRemove, "");
                        if(temp.length() > 0) {
                            addToMapLowCase(temp);
                        }
                    } else {
                        String temp = OurReplace(splitExpression[i], charsToRemove, "");
                        if(temp.length() > 0) {
                            addToMapUpCase(temp);
                        }
                    }
                }
            }
            splitDocIndex++;
        } else if (splitExpression.length == 2) {
            if (isNumeric(oneBeforeWord)) {
                if ((splitExpression[0].toLowerCase()).equals("thousand")) {
                    oneBeforeWord = OurReplace(oneBeforeWord,",", "");
                    addToMap(oneBeforeWord + "K");
                    termToAdd = termToAdd + oneBeforeWord + "K";
                } else if ((splitExpression[0].toLowerCase()).equals("million")) {
                    oneBeforeWord = OurReplace(oneBeforeWord,",", "");
                    addToMap(oneBeforeWord + "M");
                    termToAdd = termToAdd + oneBeforeWord + "M";
                } else if ((splitExpression[0].toLowerCase()).equals("billion")) {
                    oneBeforeWord = OurReplace(oneBeforeWord,",", "");
                    addToMap(oneBeforeWord + "B");
                    termToAdd = termToAdd + oneBeforeWord + "B";
                } else if ((splitExpression[0].toLowerCase()).equals("trillion")) {
                    oneBeforeWord = OurReplace(oneBeforeWord,",", "");
                    addToMap(oneBeforeWord + "000" + "B");
                    termToAdd = termToAdd + oneBeforeWord + "000" + "B";
                }else{
                    splitExpression[0] = OurReplace(splitExpression[0],",", "");
                    termToAdd = termToAdd + splitExpression[0].toLowerCase();
                    if (!stopWordsSet.contains(splitExpression[0].toLowerCase())) {
                        if (Character.isLowerCase(splitExpression[0].charAt(0))) {
                            String temp = OurReplace(splitExpression[0].toLowerCase(), charsToRemove, "");
                            if(temp.length() > 0) {
                                addToMapLowCase(temp);
                            }
                        } else {
                            String temp = OurReplace(splitExpression[0], charsToRemove, "");
                            if(temp.length() > 0) {
                                addToMapUpCase(temp);
                            }
                        }
                    }
                }
            } else {
                if (!stopWordsSet.contains(splitExpression[0].toLowerCase())) {
                    if (Character.isLowerCase(splitExpression[0].charAt(0))) {
                        String temp = OurReplace(splitExpression[0].toLowerCase(), charsToRemove, "");
                        if(temp.length() > 0) {
                            addToMapLowCase(temp);
                        }
                    } else {
                        String temp = OurReplace(splitExpression[0], charsToRemove, "");
                        if(temp.length() > 0) {
                            addToMapUpCase(temp);
                        }
                    }
                }
                termToAdd = termToAdd + splitExpression[0].toLowerCase();
            }
            termToAdd = termToAdd + '-';
            if (isNumeric(splitExpression[1])) {
                splitExpression[1] = OurReplace(splitExpression[1],",", "");
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
                termToAdd = termToAdd + splitExpression[1].toLowerCase();
                if (!stopWordsSet.contains(splitExpression[1].toLowerCase())) {
                    if (Character.isLowerCase(splitExpression[1].charAt(0))) {
                        String temp = OurReplace(splitExpression[1].toLowerCase(), charsToRemove, "");
                        if(temp.length() > 0) {
                            addToMapLowCase(temp);
                        }
                    } else {
                        String temp = OurReplace(splitExpression[1], charsToRemove, "");
                        if(temp.length() > 0) {
                            addToMapUpCase(temp);
                        }
                    }
                }
            }
            addToMap(termToAdd);
            splitDocIndex++;
        } else if (splitExpression.length == 1) {
            if (isNumeric(splitExpression[0])) {
                numberTests(word);
            } else {
                if (!stopWordsSet.contains(splitExpression[0].toLowerCase())) {
                    wordTests(splitExpression[0]);
                } else {
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
        //case when the new token it a city
        if(myCitiesIndexer.citiesDetails.containsKey(newToken.toUpperCase())){
            myCitiesIndexer.citiesDetails.get(newToken.toUpperCase()).addDocToMap(docName,Integer.toString(splitDocIndex));
        }
        if (tokens.containsKey(newToken)) {
            tokens.put(newToken, tokens.get(newToken) + 1);
        } else {
            tokens.put(newToken, 1);
        }
        if(tokens.get(newToken) > maxTf){
            maxTerm = newToken;
            maxTf = tokens.get(newToken);
        }
    }

    /**
     * in case of price with the $ sign.
     *
     * @param word
     */
    private void dollarSignPriceCase(String word) {
        word = OurReplace(word,",", "");
        word = OurReplace(word,"$", "");
        String nextWord = "";
        if (splitDoc.length < splitDocIndex + 1) {
            nextWord = splitDoc[splitDocIndex + 1];
        }
        nextWord = nextWord.toLowerCase();
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
        double price = ourParseToDouble(orgNumber);
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
        double d = ourParseToDouble(toDivide);
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
     * else add it as usual
     *
     * @param word the 1st char is low char
     */
    private void addToMapLowCase(String word) {
        word = word.toLowerCase();
        if (tokens.containsKey(word.toUpperCase())) {
            if(myCitiesIndexer.citiesDetails.containsKey(word.toUpperCase())){
                myCitiesIndexer.citiesDetails.get(word.toUpperCase()).addDocToMap(docName,Integer.toString(splitDocIndex));
            }
            int counter = tokens.get(word.toUpperCase());
            counter++;
            tokens.remove(word.toUpperCase());
            tokens.put(word, counter);
            if(tokens.get(word) > maxTf){
                maxTerm = word;
                maxTf = tokens.get(word);
            }

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
     * parse string to double
     *
     * @param toParse
     * @return the double
     */
    private double ourParseToDouble(String toParse) {
        toParse = removeFromTheEdges(toParse);
        double result = 0;
        toParse = OurReplace(toParse,",", "");
        if (toParse.contains("/")) {
            String[] splitToParse = mySplit(toParse, "/");
            if (splitToParse.length == 2) {
                if (isNumeric(splitToParse[0]) && isNumeric(splitToParse[1])) {
                    double before = Double.parseDouble(splitToParse[0]);
                    double after = Double.parseDouble(splitToParse[1]);
                    result = before / after;
                }
            }
        } else {
            if (isNumeric(toParse)) {
                result = Double.parseDouble(toParse);
            } else {
                //System.out.println("***************** " + toParse);
            }
        }
        return result;
    }

    /**
     * remove from the start and the end of the words specific chars.
     * @param toClean - the word we want to clean from those chars
     * @return - the clean word.
     */
    private String removeFromTheEdges(String toClean) {
        String word = toClean;
        if (word.length() >= 1) {
            while (word.length() >= 1 && (word.charAt(word.length() - 1) == '.' || word.charAt(word.length() - 1) == ',' || word.charAt(word.length() - 1) == '?' ||
                    word.charAt(word.length() - 1) == ':' || word.charAt(word.length() - 1) == '!' || word.charAt(word.length() - 1) == ')'
                    || word.charAt(word.length() - 1) == '}' || word.charAt(word.length() - 1) == ']' || word.charAt(word.length() - 1) == ';'
                    || word.charAt(word.length() - 1) == '"' || word.charAt(word.length() - 1) == '|' || word.charAt(word.length() - 1) == '*')) {
                word = word.substring(0, word.length() - 1);
            }
            while (word.length() >= 1 && (word.charAt(0) == '|' || word.charAt(0) == '(' || word.charAt(0) == '*' || word.charAt(0) == '{' || word.charAt(0) == '[' || word.charAt(0) == '"')) {
                word = word.substring(1, word.length());
            }
        }
        return word;
    }


    private String OurReplace(String s, String target, String replacement) {
        StringBuilder sb = null;
        int start = 0;
        for (int i; (i = s.indexOf(target, start)) != -1; ) {
            if (sb == null) sb = new StringBuilder();
            sb.append(s, start, i);
            sb.append(replacement);
            start = i + target.length();
        }
        if (sb == null) return s;
        sb.append(s, start, s.length());
        return sb.toString();
    }

    //TODO to check if it works and use it
    private String OurReplace(String s, char[] targets, String replacement) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < s.length(); i++){
            char check = s.charAt(i);
            boolean contain = false;
            for(int j = 0; !contain && j < targets.length; j++){
                if(check == targets[j]){
                    contain = true;
                }
            }
            if(contain){
                sb.append(replacement);
            }
            else{
                sb.append(check);
            }
        }
        return sb.toString();
    }
}
