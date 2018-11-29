package sample;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.io.*;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;


public class ReadFile {

    public void ReadFile(String path) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println("Start time:" + dateFormat.format(date));
        int i = 0;
        File file = new File(path);
        for (File file2 : file.listFiles()) {
            if (file2.isDirectory()) {
                for (File file3 : file2.listFiles()) {
                    if (file3.isFile()) {
                        Document doc = Jsoup.parse(new String(Files.readAllBytes(file3.toPath())), "", Parser.xmlParser());
                        Elements docs = doc.getElementsByTag("DOC");
                        //System.out.println("File name: " + file3.getName());
                        //System.out.println(docs.size());
                        //System.out.println();
                        for (Element e : docs) {
                            boolean done = e.siblingIndex() == docs.last().siblingIndex();
                            Parse p = new Parse();
                            String city = e.getElementsByTag("F").toString();
                            if(!city.equals("")) {
                                city = city.substring(city.indexOf("<f p=\"104\">", city.indexOf("</f>")));
                                if (city.length() > 15) {
                                    city = city.substring(city.indexOf("\n "), city.indexOf(" \n"));
                                    city = city.replaceAll("\n", "");
                                    String cityline[] = city.split(" ");
                                    city = cityline[2].toUpperCase();

                                }
                            }
                            p.parser(e.getElementsByTag("TEXT").text(), e.getElementsByTag("DOCNO").text(), done,city);
                            Date date1 = new Date();
                            i++;
                        }
                    }
                }
            }
        }
        Date date1 = new Date();
        //System.out.println("Finish time: " + dateFormat.format(date1));
    }
}


