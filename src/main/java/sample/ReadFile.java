package sample;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;


public class ReadFile {

    DocsInformation docsInformation = new DocsInformation();

    public void ReadFile(String path, boolean stemmer) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println("Start time:" + dateFormat.format(date));
        int i = 0;
        String language = "";
        File file = new File(path + "\\corpus");
        Parse.apiGetStart();
        for (File file2 : file.listFiles()) {
            if (file2.isDirectory()) {
                for (File file3 : file2.listFiles()) {
                    if (file3.isFile()) {
                        Document doc;
                        doc = Jsoup.parse(file3, "UTF-8");
                        Elements docs = doc.select("DOC");
                        for (Element e : docs) {
                            boolean done = e.siblingIndex() == docs.last().siblingIndex();
                            Parse p = new Parse(path);
                            String city = e.getElementsByTag("F").toString();
                            if (!city.equals("")) {
                                int cityIndex = city.indexOf("<F P=\"104\">");
                                int langIndex = city.indexOf("<F P=\"105\">");
                                if (city.charAt(langIndex + 1) != ('<')) {
                                    language = city.substring(city.indexOf("<F P=\"105\">", city.indexOf("</F>")));
                                    if (language.length() > 15) {
                                        language = language.substring(language.indexOf("\n "), language.indexOf(" \n"));
                                        language = language.replaceAll("\n", "");
                                        language = language.replaceAll(" ", "").toUpperCase();
                                        language = language.replaceAll(",", "").toUpperCase();
                                    }
                                }
                                if (city.charAt(cityIndex + 1) != ('<')) {
                                    city = city.substring(city.indexOf("<F P=\"104\">", city.indexOf("</F>")));
                                    if (city.length() > 15) {
                                        city = city.substring(city.indexOf("\n "), city.indexOf(" \n"));
                                        city = city.replaceAll("\n", "");
                                        String cityline[] = city.split(" ");
                                        city = cityline[2].toUpperCase();

                                    }
                                }

                            }
                            p.parser(e.getElementsByTag("TEXT").text(), e.getElementsByTag("DOCNO").text(), done, city, stemmer);
                            docsInformation.addDateOfWrite(e.getElementsByTag("DOCNO").text(), e.getElementsByTag("Date1").text());
                            if (!language.equals("")) {
                                if(!Pattern.compile( "[0-9]" ).matcher( language ).find() && !language.equals("THE")) {
                                    DocsInformation.allLanguages.add(language);
                                }
                            }
                            Date date1 = new Date();
                            i++;
                        }
                    }
                }
            }
        }
        Indexer indexer = new Indexer();
        indexer.executePosting();
    }
}


