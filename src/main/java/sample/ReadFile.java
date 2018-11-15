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
                        for (Element e : docs) {
                            Parse p = new Parse();
                            //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            //Date date = new Date();
                            //System.out.println("Start doc number " + i + " " + dateFormat.format(date)); //2016/11/16 12:08:43
                            //         Documents d=new Documents(e.getElementsByTag("DOCNO").text(),e.getElementsByTag("TEXT").text());
                            //  System.out.println(e.getElementsByTag("DOCNO").text());
                            //System.out.println(e.getElementsByTag("TEXT").text());
                            p.parser(e.getElementsByTag("TEXT").text());
                            Date date1 = new Date();
                            //System.out.println("Finish doc number " + i + " " + dateFormat.format(date1));
                            i++;
                        }
                    }
                }
            }
        }
        Date date1 = new Date();
        System.out.println("Finish time: " + dateFormat.format(date1));
    }
}


