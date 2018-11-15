package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
        ReadFile Myreader = new ReadFile();
        Myreader.ReadFile("C:\\Users\\adijak\\IdeaProjects\\SearchingApp\\src\\main\\java\\corpus");
        Date date1 = new Date();
        System.out.println(dateFormat.format(date1));
       // Parse p = new Parse();

        /*p.parser("the a The-Dollar XX-XX-XX -aabbccdd -133 million hey 122 Million-44 word))) January 23 November 1993 Feb 76/15   Dorin Dorin Adi adi   78/13 28/2023Z FEB 12.1 percent POLITICIANS,  PARTY PREFERENCES \n" +
                "\n" +
                "   Summary:  Newspapers in the Former Yugoslav Republic of \n" +
                "   Macedonia have published the results of opinion polls, \n" +
                "   indicating the relative popularity of politicians, \n" +
                "   political parties, and attitudes toward the political system. \n" +
                "\n" +
                "   The 22-23 January edition of the Skopje newspaper VECER in \n" +
                "Macedonian published on pages 6-7 the results of an opinion poll \n" +
                "conducted by the \"BriMa\" agency in November 1993. According to \n" +
                "VECER, 1,036 respondents were classified by age and residence, but \n" +
                "the paper did not explain the methodology or give the margin of \n" +
                "error.  For the purpose of comparison, the paper cited the results \n" +
                "of an unidentified poll made in May 1993. The approval/disapproval \n" +
                "ratings, in percent, for ten Macedonian politicians were: \n" +
                "\n" +
                "                                           November 1993    May 1993 \n" +
                "\n" +
                "Kiro Gligorov, President of the Republic      76/15           78/13 \n" +
                "\n" +
                "Vasil Tupurkovski, former Macedonian          50/36           43/37 \n" +
                "   official in Federal Yugoslavia \n" +
                "\n" +
                "Ljubomir Frckovski, Interior Minister         50/42           42/43 \n" +
                "\n" +
                "Stojan Andov, Parliamentary Chairman          48/41           48/39 \n" +
                "\n" +
                "Branko Crvenkovski, Prime Minister            46/41           44/38 \n" +
                "\n" +
                "Vlado Popovski, Defense Minister              41/41           36/37 \n" +
                "\n" +
                "Stevo Crvenkovski, Foreign Minister           40/43   No Data Given \n" +
                "\n" +
                "Petar Gosev, Democratic Party leader          34/53           40/42 \n" +
                "\n" +
                "Todor Petrov, Independent parliamentarian     32/53   No Data Given \n" +
                "\n" +
                "Nikola Popovski, Social Democratic            29/46           32/42 \n" +
                "   Party parliamentarian \n" +
                "\n" +
                "   VECER noted that President Gligorov's very high approval rating \n" +
                "of 90 percent among those over age 65 fell off to a still high 70 \n" +
                "percent among respondents between 18 and 24.  Residents of Skopje \n" +
                "ranked the politicians in a slightly different order from the \n" +
                "ranking given by the whole sample: Gligorov, Tupurkovski, Frckovski, \n" +
                "Andov, Gosev, Branko Crvenkovski, Vlado Popovski, Petrov, Nikola \n" +
                "Popovski, and Stevo Crvenkovski. \n" +
                "\n" +
                "   The results of a series of opinion polls conducted by the Agency \n" +
                "for Public Opinion Research and published \"exclusively\" by the \n" +
                "Skopje weekly PULS newspaper, confirmed Gligorov's substantial lead \n" +
                "in popularity among political figures.  According to the 31 December \n" +
                "1993 issue of PULS (pages 16-18), the agency gathered the data by \n" +
                "means of telephone interviews with 300 residents in the Republic of \n" +
                "Macedonia during 20-24 December. PULS also provided data from \n" +
                "surveys made in March, June, and September for comparison.  Some of \n" +
                "the following percentages are approximate values that were derived \n" +
                "from the graph published by the paper: \n" +
                "\n" +
                "                         March       June      September    December \n" +
                "\n" +
                "Kiro Gligorov             87          82.33      89.33           89 \n" +
                "Stevo Crvenkovski         54          65         49              63 \n" +
                "Stojan Andov              61          62         60              61 \n" +
                "Branko Crvenkovski        56          60         54 \n" +
                "53.5 \n" +
                "Ljubomir Frckovski        35          45         48              50 \n" +
                "Petar Gosev               50          31         52 \n" +
                "49.53 \n" +
                "Jovan Andonov, \n" +
                " Deputy Prime Minister    39          39         50              37 \n" +
                "Vlado Popovski            18          25         36              35 \n" +
                "Kiro Popovski, Deputy \n" +
                " Chairman, Parliament     26          27         33              32 \n" +
                "Ante Popovski, leader of \n" +
                " MAAK (Movement for All- \n" +
                " Macedonian Action)       29          32         32 \n" +
                "indistinct \n" +
                "Jane Miljovski, Minister \n" +
                " without Portfolio        --          23         31              24 \n" +
                "Vladimir Golubovski \n" +
                " VMRO-DP (Internal \n" +
                " Macedonian Revolutionary \n" +
                " Organization-Democratic \n" +
                " Party) leader            --          30         25              23 \n" +
                "Nevzat Halili \n" +
                " Party for Democratic \n" +
                " Prosperity official      38.33       38         18              18 \n" +
                "\n" +
                "Lj upco Georgievski \n" +
                "VMRO-DPMNE (Internal \n" +
                "Macedonian Revolutionary \n" +
                "Organization-Democratic \n" +
                "Party for Macedonian \n" +
                "National Unity) \n" +
                "official                  18          10         16              17 \n" +
                "Dosta Dimovska \n" +
                "VMRO-DPMNE \n" +
                "official                  --          11         17              16 \n" +
                "\n" +
                "   On pages 6 and 7 of its 15-16 January issue, VECER also published \n" +
                "the results of a November 1993 survey on party preferences. \n" +
                "\"BriMa,\" working with the Gallup organization, interviewed 1,036 \n" +
                "people. \n" +
                "\n" +
                "   Question: \"If elections were held today, for which party would \n" +
                "you vote?\" (all numbers are percentages) \n" +
                "\n" +
                "SDSM (Social Democratic Alliance of Macedonia)  22.8 \n" +
                "VMRO-DPMNE                                      11.2 \n" +
                "Democratic Party (DP, led by Petar Gosev)        6.3 \n" +
                "Socialist Party                                  3.3 \n" +
                "Liberal Party (LP)                               3.2 \n" +
                "Workers Party                                    2.9 \n" +
                "PCERM (Party for the Full Emancipation of \n" +
                "    Romanies in Macedonia)                       1.8 \n" +
                "Democratic Party of Turks in Macedonia           0.8 \n" +
                "MAAK                                             0.3 \n" +
                "Another party                                    4.0 \n" +
                "Undecided                                       18.6 \n" +
                "Would not vote                                   6.6 \n" +
                "\n" +
                "   VECER noted that some parties fared better in certain cities than \n" +
                "their overall scores indicate.  For example, the DP was about twice \n" +
                "as popular in Skopje as elsewhere, getting 12.1 percent in the \n" +
                "capital; the VMRO-DPMNE was more popular in Bitola, getting 15.7 \n" +
                "percent, than in the remainder of the country; and the LP in the \n" +
                "Bregalnica area got the support of 10.6 percent, substantially \n" +
                "higher than the 3.2 percent support it received overall. \n" +
                "\n" +
                "   Question: \"Do you have confidence in the following parties?\" (all \n" +
                "numbers are percentages) \n" +
                "\n" +
                "              Yes           No       Do Not Know \n" +
                "\n" +
                "SDSM           28           51          21 \n" +
                "VMRO-DPMNE     15           72          14 \n" +
                "LP             19           59          22 \n" +
                "PDP-NDP*       20           73           7 \n" +
                "\n" +
                "*Party for Democratic Prosperity-People's Democratic Party \n" +
                "\n" +
                "   The poll clearly indicated that Macedonians have little \n" +
                "confidence in any of the parties currently active in the country. \n" +
                "Respondents were also asked whether it would be good for the country \n" +
                "to have elections sooner than scheduled; 62 percent agreed, 20 \n" +
                "percent disagreed, and 18 percent did not know. These findings were \n" +
                "correlated with party preferences, producing the following results: \n" +
                "Of those who would vote for the SDSM, 54 percent wanted elections \n" +
                "soon, while 34 percent were against early elections. However, 80 \n" +
                "percent of VMRO-DPMNE supporters favored elections soon, as did 79 \n" +
                "percent of LP supporters and 71 percent of DP supporters. While 80 \n" +
                "percent of those surveyed thought that a person should vote (14 \n" +
                "percent did not agree), only 40 percent thought that it was very \n" +
                "important which party won the elections and 27 percent thought it \n" +
                "was somewhat significant. \n" +
                "\n" +
                "   (AUTHOR:  GALYEAN.  QUESTIONS AND/OR COMMENTS, PLEASE CALL CHIEF, \n" +
                "BALKANS BRANCH AT (703) 733-6481) \n" +
                "\n" +
                "ELAG/25 February/POLCHF/EED/DEW 28/2023Z FEB");*/
    }
}
