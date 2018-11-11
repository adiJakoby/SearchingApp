package sample;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.io.*;
import java.util.LinkedList;

import org.jsoup.nodes.Document;


public class ReadFile {

    /**
     *
     * @param Path
     * @return the string that the file in the path parameter contains
     */
    public LinkedList<String> readFile(String Path) throws IOException {
        File [] fileslist=listFiles(Path);
        File [] eachFolderfiles;
        LinkedList<String> AllFilesContent=new LinkedList<String>();
        String FileContent;
        String DirectoryName="";
        for (File file : fileslist){
            DirectoryName=file.getPath();
            if (file.isDirectory()){
                eachFolderfiles=listFiles(file.getPath());
                FileContent="";
                for (File doc : eachFolderfiles){
                    if (doc.isFile()){
                        FileContent+=readDocFromFile(doc);
                    }
                    AllFilesContent.push(FileContent);
                }
            }
        }
        return AllFilesContent;
    }
    /**
     *
     * @param file
     * @return string that contain all the documents in the file Separated by <Doc> </Doc>
     * @throws FileNotFoundException
         */
    public String readDocFromFile(File file) throws IOException {
        String FileContent = "";
        int i=0;
        String line;
        FileReader fileReader = new FileReader(file.getPath());
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            while ((line = bufferedReader.readLine()) != null) {
                FileContent += line;
                System.out.println("line number:" + i);
                i++;
            }
        }

        /*
        Scanner sc = new Scanner(file);
        int i=0;
        while (sc.hasNextLine()) {
            FileContent =FileContent + sc.nextLine();
            System.out.println("line number:" + i);
            i++;
        }
        */
        Document doc = (Document) Jsoup.parse(FileContent);
        org.jsoup.nodes.Element link =  doc.select("Doc").first();
        String AllDoccontent = link.outerHtml();
        return AllDoccontent;
    }


    /**
     * List all the files under a directory
     * @param directoryName to be listed
     */
    public File[] listFiles(String directoryName){
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        return fList;
    }
}
