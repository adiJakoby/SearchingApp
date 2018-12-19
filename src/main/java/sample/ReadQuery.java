package sample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class ReadQuery {

    public ArrayList<String[]> getQueryFromFile(String path) {
        ArrayList<String[]> queriesArray = new ArrayList<>();
        try {
            File file = new File(path + "\\queries.txt");
            Document doc;
            doc = Jsoup.parse(file, "UTF-8");
            Elements queries = doc.select("top");
            for (Element e : queries) {
                String[] allQueryParts = new String[3];
                String query = e.getElementsByTag("num").toString();
                String[] bodyAfterSplit = query.split(" ");
                int index = 0;
                for (int i = 0; i < bodyAfterSplit.length; i++) {
                    if (bodyAfterSplit[i].equals("Number:")) {
                        allQueryParts[0] = bodyAfterSplit[i + 1];
                        index = i + 2;
                        break;
                    }
                }
                for (int i = index; i < bodyAfterSplit.length; i++) {
                    if (bodyAfterSplit[i].equals("<title>")) {
                        i++;
                        while (!bodyAfterSplit[i].contains("<desc>") && !bodyAfterSplit[i].contains("</title>")) {
                            if (!bodyAfterSplit[i].equals(null)) {
                                allQueryParts[1] += " " + bodyAfterSplit[i];
                                i++;
                            }
                        }
                        index = i;
                        break;
                    }
                }
                for (int i = index; i < bodyAfterSplit.length; i++) {
                    if (bodyAfterSplit[i].contains("<desc>")) {
                        i++;
                        while (!bodyAfterSplit[i].equals("<narr>")) {
                            if (!bodyAfterSplit[i].equals(null)) {
                                allQueryParts[2] += " " + bodyAfterSplit[i];
                                i++;
                            }
                        }
                        break;
                    }
                }
                queriesArray.add(allQueryParts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queriesArray;
    }
}
