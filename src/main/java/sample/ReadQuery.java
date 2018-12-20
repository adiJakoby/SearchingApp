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
        int queynum = 1;
        try {
            File file = new File(path);
            Document doc;
            doc = Jsoup.parse(file, "UTF-8");
            StringBuilder writer = new StringBuilder();
            Elements queries = doc.select("top");
            for (Element e : queries) {
                String[] allQueryParts = new String[3];
                String query = e.getElementsByTag("num").toString();
                String[] bodyAfterSplit = CitiesIndexer.mySplit(query," ");
                allQueryParts[1] = "";
                allQueryParts[2] = "";
                int index = 0;
                for (int i = 0; i < bodyAfterSplit.length; i++) {
                    if (bodyAfterSplit[i].equals("Number:")) {
                        allQueryParts[0] = bodyAfterSplit[i + 1];
                        index = i + 2;
                        break;
                    }
                }
                for (int i = index; i < bodyAfterSplit.length; i++) {
                    if (bodyAfterSplit[i].contains("<title>")) {
                        i++;
                        while (!bodyAfterSplit[i].contains("<desc>") && !bodyAfterSplit[i].contains("</title>")) {
                            if (!bodyAfterSplit[i].equals(null)) {
                                writer.append(" " + bodyAfterSplit[i]);
                                i++;
                            }
                        }
                        index = i;
                        allQueryParts[1]=writer.toString();
                        writer = new StringBuilder();
                        break;
                    }
                }
                for (int i = index; i < bodyAfterSplit.length; i++) {
                    if (bodyAfterSplit[i].contains("<desc>")) {
                        i++;
                        while (!bodyAfterSplit[i].contains("<narr>")) {
                            if (!bodyAfterSplit[i].equals(null)) {
                                if(bodyAfterSplit[i].contains("Description:")){
                                    i++;
                                }
                                writer.append(" " + bodyAfterSplit[i]);
                                i++;
                            }
                        }
                        allQueryParts[2] = writer.toString();
                        writer = new StringBuilder();
                        break;
                    }
                }
                System.out.println("Done : query num " + queynum + " Contains the title:" +allQueryParts[1]);
                queynum++;
                queriesArray.add(allQueryParts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queriesArray;
    }
}
