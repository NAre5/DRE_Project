package sample;

import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Parse{
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        File file = new File("C:\\Users\\erant\\Desktop\\STUDIES\\Third_Year\\אחזור\\search engine\\corpus\\corpus\\FB396001");
        Document document = null;
        Parse p = new Parse();
        try {
            document = Jsoup.parse(new String(Files.readAllBytes(file.listFiles()[0].toPath())));
        } catch (IOException e) {
            e.printStackTrace();
//            System.out.println("---------------read line 44-------------------");
        }
        Elements docElements = document.getElementsByTag("DOC");

        for (Element element : docElements) {
            Document documentFromElement = Jsoup.parse(new String(element.toString()));
            Elements IDElement = documentFromElement.getElementsByTag("DOCNO");
            Elements TitleElement = documentFromElement.getElementsByTag("TI");
            Elements TextElement = documentFromElement.getElementsByTag("TEXT");
            String ID = IDElement.text();
            String title = TitleElement.text();
            String text = TextElement.text();
            cDocument cDoc = new cDocument(ID, title, text);
            DocumentBuffer.getInstance().getBuffer().add(cDoc);
//            pool.execute(parser);
        }
        p.parse(docElements.size());
//        System.out.println("main");
        System.out.println(System.currentTimeMillis()-start);
    }
    ExecutorService pool = Executors.newCachedThreadPool();
    static HashSet<String> stopWords = new HashSet<>();
    static {
        File file = new File(ClassLoader.getSystemResource("stop_words.txt").getPath());
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null)
                stopWords.add(st);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public static Object lock = new Object();
//    public static int counter = 0;

//    public static void main(String[] args) {
//        parse("Europe     Economic Review:  WESTERN EUROPE Vol. III, No. 13, 24 \n" +
//                "March 1994 \n" +
//                "\n" +
//                "   Notice To Readers:  FOREIGN MEDIA SURVEY is a series published by \n" +
//                "FBIS Europe/Latin America Group featuring brief summaries of foreign \n" +
//                "media reports on topical issues, compiled from the most recent \n" +
//                "sources available to FBIS. \n" +
//                "\n" +
//                "   Europe Economic Review (EER) is a subseries of FOREIGN MEDIA \n" +
//                "SURVEY published by the West and East European Divisions of \n" +
//                "Europe/Latin America Group with contributions from FBIS overseas \n" +
//                "bureaus.  The EER is intended to supplement coverage of European \n" +
//                "economic issues by the FBIS Daily Report for West and East Europe, \n" +
//                "and other FBIS publications.  Foreign news, feature, and editorial \n" +
//                "reports selected for summary in this publication normally do not \n" +
//                "appear in other FBIS publications. The EER is published in two \n" +
//                "editions, one for Western Europe (also including media coverage of \n" +
//                "Canada and Turkey), and the other for Eastern Europe.  Drawing \n" +
//                "primarily from domestic media sources of these countries but also \n" +
//                "using other foreign media where appropriate, the EER focuses on \n" +
//                "national and regional economic issues, such as economic \n" +
//                "competitiveness, regional economic integration, economic reforms and \n" +
//                "other changes in economic policies, and foreign trade and \n" +
//                "investment. \n" +
//                "\n" +
//                "    FOREIGN MEDIA SURVEY is a U.S. Government publication.  Its \n" +
//                "contents in no way represent the policies, views, or attitudes of \n" +
//                "the U.S  Government.  All comment or analysis contained herein is \n" +
//                "attributable to the cited media source, unless otherwise indicated. \n" +
//                "\n" +
//                "    TABLE OF CONTENTS \n" +
//                "\n" +
//                "    FEATURES: \n" +
//                "\n" +
//                "    Sweden--Exporters Set Their Sights on China \n" +
//                "\n" +
//                "    EU \n" +
//                "\n" +
//                "    Rapid Conclusions of Leuna Dispute Sought; EBRD Doubles \n" +
//                "Investment in East Europe \n" +
//                "\n" +
//                "    FINLAND \n" +
//                "\n" +
//                "    Commentary Views Reich's Jobs Solutions \n" +
//                "\n" +
//                "    FRANCE \n" +
//                "\n" +
//                "    Editorial Discusses G-7 Job Summit; Commentary Advocates Social \n" +
//                "Welfare Commitment; Job Summit Ignores World Unemployment;Commentary \n" +
//                "on G-7 Job Summit; U.S. Seen Speaking 'Double Talk' on Audiovisual; \n" +
//                "Sapin Discusses Monetary Policy; Editorial on OECD Views on French \n" +
//                "Interest Rates; Editorial on Government Subsidies;Dassault Asks \n" +
//                "Assembly To Stop Rafale Delays; Roots of Unemployment Viewed; U.S. \n" +
//                "'Pressure' Divides EU on Naval Construction \n" +
//                "\n" +
//                "    GERMANY \n" +
//                "\n" +
//                "    Commentators View Jobs Summit; Environmental Technology Seen As \n" +
//                "Creating Jobs; Governments Push Small Business Funding; New \n" +
//                "Semiconductor Plant Opens in East; Bitterfeld Chemical Facilities \n" +
//                "Expanded \n" +
//                "\n" +
//                "    GREECE \n" +
//                "\n" +
//                "    Civil Service 'Inflation' Noted; Greek-Romanian Economic \n" +
//                "Relations;  Energy Pollution Taxes Assessed \n" +
//                "\n" +
//                "    ITALY \n" +
//                "\n" +
//                "    Labor Minister Discusses Black Market Labor; Recent Labor \n" +
//                "Accords Seen As Stopgap Measures; ENI's Plans To Expand East \n" +
//                "\n" +
//                "    NORWAY \n" +
//                "\n" +
//                "    Record Export Year Reported; Pension Fund Called 'Profitable \n" +
//                "Business';  Swedish Bank Predicts Growth for Norway, Denmark; 1993 \n" +
//                "Profitable for Most Businesses; New Oil Drilling Planned; \n" +
//                "Competitiveness of Norwegian Shelf Assessed7 \n" +
//                "\n" +
//                "    SWEDEN \n" +
//                "\n" +
//                "    Swedish Contribution to EU Budget Calculated \n" +
//                "\n" +
//                "    ECONOMIC BRIEFS \n" +
//                "\n" +
//                "    France, Greece, Norway \n" +
//                "\n" +
//                "    FEATURE \n" +
//                "\n" +
//                "    Sweden:   Exporters Set Their Sights on China \n" +
//                "\n" +
//                "    With many Swedish companies hoping to take advantage of the \n" +
//                "Chinese economic boom, exports to China have more than doubled in \n" +
//                "the last two years.  In fact, according to a 29 January SVENSKA \n" +
//                "DAGBLADET survey of Swedish economic activity in China, the Far East \n" +
//                "is well on its way to becoming a more important market for Swedish \n" +
//                "firms than North America.  Swedish exports to China increased from \n" +
//                "1.6 to 3.5 billion kronor between 1991 and 1993, and more Swedish \n" +
//                "firms are establishing joint ventures in China as well.  The \n" +
//                "commercial section at the Swedish embassy in Beijing described 1993 \n" +
//                "as a \"real record year.\" \n" +
//                "\n" +
//                "    Despite the export boom, Sweden's trade picture with China is \n" +
//                "not all rosy.  According to the 29 January SVENSKA DAGBLADET, Sweden \n" +
//                "continues to maintain a trade deficit with China because imports \n" +
//                "from that country--especially textiles--have increased dramatically. \n" +
//                "In recent years, China has replaced Portugal as the leading supplier \n" +
//                "of textiles to Sweden.  The business weekly VECKANS AFFARER also \n" +
//                "reported on 21 February that Sweden's share of Chinese imports has \n" +
//                "decreased from 1.35 percent in 1987 to under 1 percent in 1993. \n" +
//                "This is attributed to Eurocentric attitudes on the part of many \n" +
//                "Swedish firms. \n" +
//                "\n" +
//                "    Ericsson: The Shining Star \n" +
//                "\n" +
//                "    The Ericsson electronics company accounts for the lion's share-- \n" +
//                "60 percent--of Sweden's exports to China, which is well on its way \n" +
//                "to becoming Ericsson's largest market, according to an article in \n" +
//                "DAGENS NYHETER on 29 January.  Ericsson has been most successful in \n" +
//                "exporting mobile phones to China, where it holds a 70-percent market \n" +
//                "share.  The article cites Hans Ekstrom, the company's head of \n" +
//                "operations in China, as saying that \"the sale of mobile telephones \n" +
//                "in China has been a real cash cow for us.\"  Ericsson's sales of \n" +
//                "these phones--a popular status symbol in China--doubled in 1992 and \n" +
//                "1993 and are expected to further increase when China installs a \n" +
//                "standardized digital system.  Ericsson already has an advantage in \n" +
//                "the vast Chinese market because of its ability to build large-scale \n" +
//                "systems.  According to the article, the firm sees its most important \n" +
//                "competitors in establishing a mobile phone system in China as \n" +
//                "Motorola and the Finnish firm Nokia.  The article also notes that a \n" +
//                "large part of the export credits for Ericsson's sales is expected to \n" +
//                "come from foreign institutions because of the low risk tolerance of \n" +
//                "Swedish banks. \n" +
//                "\n" +
//                "    Ericsson, which last year moved its southern China office from \n" +
//                "Hong Kong to Guangzhou to be closer to its customers, has been \n" +
//                "particularly active in Guangdong--a province of 66 million \n" +
//                "inhabitants whose capital is Guangzhou.  According to the 31 January \n" +
//                "SVENSKA DAGBLADET, Ericsson has 40 percent of the overall public \n" +
//                "telecommunications market in Guangdong, second only to Japan's \n" +
//                "NEC, and 80 percent of the market for mobile telephones.  In \n" +
//                "addition, the company has received 80 percent of all new \n" +
//                "telecommunications orders in the province in the last three years. \n" +
//                "Last year it got a 2.5-billion-kronor order from Guangdong Post &amp; \n" +
//                "Telecom to expand the regular telephone system to 2 million new \n" +
//                "subscribers; the order will be 15 percent financed by state- \n" +
//                "backed Swedish export credits.  Ericsson is also one of five \n" +
//                "companies invited to bid on building a digital GSM mobile phone \n" +
//                "system in the province;  according to an article in DAGENS NYHETER \n" +
//                "on 29 January, Ericsson has an advantage in the competition because \n" +
//                "it built Guangdong's GSM test system.  SVENSKA DAGBLADET also \n" +
//                "reported on 1 March that Ericsson got a 1.7-billion- kronor order \n" +
//                "from the province, which the deputy head of Ericsson Radio Systems \n" +
//                "in China called \"one of our largest orders ever,\" to build an \n" +
//                "analogue system for mobile phones, called the Total Access \n" +
//                "Communication System.  It will also deliver mobile phone exchanges, \n" +
//                "radio base stations, a system for maintenance and monitoring, and \n" +
//                "subscriber databases, all of which will be produced in Sweden. \n" +
//                "\n" +
//                "    Other Players \n" +
//                "\n" +
//                "    Other Swedish firms are hoping to take advantage of the \n" +
//                "development of the Chinese infrastructure, especially the railroad \n" +
//                "industry.  Asea Brown Boveri (ABB), a Swedish-Swiss electrical \n" +
//                "engineering giant, is perhaps the second most active Swedish company \n" +
//                "in China.  An article in the 29 January SVENSKA DAGBLADET explains \n" +
//                "how ABB, whose sales in China totalled 1.5 billion kronor in 1992, \n" +
//                "has capitalized on China's energy and transportation needs to \n" +
//                "carve out a niche for itself in the Chinese market.  While the \n" +
//                "company had only 50 employees in Hong Kong and China five years ago, \n" +
//                "that total has now risen to 1,500.  China is its single largest \n" +
//                "market for power generators, power transmitters, and trains.  ABB \n" +
//                "hopes for large sales of its X2000 high speed train as well. \n" +
//                "According to a SVENSKA DAGBLADET report from 27 October, the \n" +
//                "railroad is also among Atlas Copco's biggest Chinese customers. \n" +
//                "The mining engineering firm, which has built dams and tunnels in \n" +
//                "China, is hoping for a contract to construct tunnels for the high- \n" +
//                "speed train route between Beijing and Hong Kong.  Last year SKF, a \n" +
//                "ball bearings manufacturer, also got what the report calls a \n" +
//                "\"breakthrough\" order from the railroad.  SKF had previously \n" +
//                "concentrated on supplying ball bearings and components for \n" +
//                "textile machines in China.  The Swedish trade counselor in Beijing \n" +
//                "told SVENSKA DAGBLADET on 27 October that Swedish exporters can be \n" +
//                "as successful in the railroad industry in China as Ericsson has been \n" +
//                "in the telecommunications sector, providing they can secure \n" +
//                "financing.  However, the Swedish development aid organization, which \n" +
//                "has established strict regulations on deals in China, has not been \n" +
//                "forthcoming. \n" +
//                "\n" +
//                "   Looking at the success of Swedish industry in China, an article \n" +
//                "in SVENSKA DAGBLADET on 29 January notes that the country is also an \n" +
//                "important market for Hagglunds, which produces heavy machinery. \n" +
//                "China makes up 30-40 percent of Hagglunds' market worldwide for \n" +
//                "hydraulic motors.  The company also sells cranes, many of which are \n" +
//                "made in a joint venture in Nanjing, to China's growing shipbuilding \n" +
//                "business.  Its vehicles are also being used in oil exploration on \n" +
//                "the coast and the Yellow River delta. \n" +
//                "\n" +
//                "    Automaker Volvo has been making inroads into the Chinese market \n" +
//                "through local representation and joint ventures, another article in \n" +
//                "SVENSKA DAGBLADET on 29 January claims.  Although sales slacked off \n" +
//                "last year due to tight Chinese economic policies, the company is \n" +
//                "expecting sales to increase this year.  It has 10 retail and service \n" +
//                "locations in China and earlier this year opened an office in \n" +
//                "Beijing.  Last Fall Volvo concluded a joint venture agreement with \n" +
//                "Xian Aircraft Company for the production and marketing of buses. \n" +
//                "The factory will start by producing 1,000 buses a year and then \n" +
//                "increase to 3,000; it will employ 1,100 workers.  An article in \n" +
//                "DAGENS NYHETER on 29 January notes that the company is hoping to \n" +
//                "open another production facility.  (WS) \n" +
//                "\n" +
//                "    EU \n" +
//                "\n" +
//                "    RAPID CONCLUSION OF LEUNA DISPUTE SOUGHT--At a meeting in Bonn \n" +
//                "to discuss economic convergence between the two countries, French \n" +
//                "and German parliamentarians pointed out the symbolic character of \n" +
//                "the proposed Leuna oil refinery, which France's Elf oil company \n" +
//                "agreed to build in eastern Germany.  The refinery project, they \n" +
//                "claim, could solidify industrial links between France and Germany. \n" +
//                "To do so, however, the parliamentarians state that there needs to be \n" +
//                "a quick resolution of the dispute which has arisen between Elf \n" +
//                "and Germany's Trust Agency over Elf's desire to reduce its \n" +
//                "investment commitment to the project.  Noting that this investment \n" +
//                "was \"vital for overcoming the difficulties of eastern Germany's \n" +
//                "chemical industry,\" the parliamentarians added that besides the \n" +
//                "purely political considerations, necessary market considerations \n" +
//                "must not be neglected.  However, any arrangement should proceed \n" +
//                "\"with the general principle of law which says that contracts \n" +
//                "concluded should be respected in their spirit.\" \n" +
//                "\n" +
//                "    Because of the impasse in the negotiations between the Trust \n" +
//                "Agency and Elf, the economy minister of Saxe-Anhalt, where Leuna is \n" +
//                "located, intends to talk directly to Elf.  Additionally, officials \n" +
//                "at Germany's Thyssen, Elf's partner and coinvestor in the refinery, \n" +
//                "declared they were convinced the issue would be resolved soon and \n" +
//                "that the question of Elf's ownership share could be resolved \n" +
//                "internally between Thyssen and Elf. (DiI)  (Paris LES ECHOS \n" +
//                "in French 10 Mar 94 p 10) \n" +
//                "\n" +
//                "    EBRD DOUBLES INVESTMENT IN EAST EUROPE--The European Bank for \n" +
//                "Reconstruction and Development (EBRD) approved 91 projects in East \n" +
//                "Europe in 1993, which represented a financial investment of 2.28 \n" +
//                "billion ECU's or two times more than the preceding year, according \n" +
//                "to an EBRD report published on 9 March.  In 1992, the bank approved \n" +
//                "51 projects representing 1.09 billion ECU's.  The report indicates \n" +
//                "that in 1993, EBRD loans made possible East European projects worth \n" +
//                "11.3 billion ECU's compared to 7 billion ECU's in 1992 and 1.5 \n" +
//                "billion ECU's during the first nine months of the banks existence in \n" +
//                "1991.  According to the report, EBRD wants to maintain the same \n" +
//                "level of investment this year as in 1993. \n" +
//                "\n" +
//                "    However, as in 1992, EBRD only disbursed a small fraction of the \n" +
//                "approved sums: Out of the 2.28 billion ECU's approved, only 435 \n" +
//                "million ECU's were actually made available to projects, according to \n" +
//                "an article in LES ECHOS.  This \"enormous\" difference between \n" +
//                "approved and disbursed funds, although caused by projects structured \n" +
//                "for completion over several years, may rekindle the debate on EBRD's \n" +
//                "ability to lend.  The article questions whether this might thwart \n" +
//                "EBRD's attempt to highlight the good conditions of its accounts \n" +
//                "after the past year when the bank was criticized for former director \n" +
//                "Jacques Attali's management.  In any case, EBRD can point to \n" +
//                "progress in containing bank operating expenses, which at 137.3 \n" +
//                "million ECU's are 8 percent less than forecast, according to the \n" +
//                "report. (DiI)  (Paris LES ECHOS in French 10 Mar 94 p 7) \n" +
//                "\n" +
//                "    FINLAND \n" +
//                "\n" +
//                "    COMMENTARY VIEWS REICH'S JOBS SOLUTIONS--In the lead editorial \n" +
//                "for Helsinki HUFVUDSTADSBLADET on 16 March, editor-in-chief Bo \n" +
//                "Stenstrom commented on U.S. Labor Secretary Robert Reich's message \n" +
//                "at the G-7 Jobs Summit that infrastructure investment is a major \n" +
//                "factor in creating employment.  While admitting that Finland, with \n" +
//                "its over 20-percent unemployment rate, has not been as successful as \n" +
//                "the United States in putting people to work, Stenstrom criticizes \n" +
//                "those who have taken Reich's ideas as a \"job creation recipe in \n" +
//                "itself.\"  Stenstrom argues that investing in infrastructure will not \n" +
//                "automatically lead to better jobs and better wages.  He cites \n" +
//                "assertions that while the United States has indeed a better record \n" +
//                "as far as raw unemployment figures, that many of these jobs are low- \n" +
//                "paid service sector positions.  Stenstrom suggests that both Reich's \n" +
//                "and the G-7's White Papers are too eager to diminish the role of the \n" +
//                "public sector, which Stenstrom sees as vital for \"providing \n" +
//                "training\" to improve workers' skills to make them more competitive \n" +
//                "with those in developing countries. \n" +
//                "\n" +
//                "    Stenstrom also points to another problem particular to Finland's \n" +
//                "virtually flat population growth, a problem he believes \n" +
//                "infrastructure investing alone cannot solve: overproduction as \n" +
//                "industry and workers become more efficient.  He reasons that growth \n" +
//                "in industrial production leads toinvestment in technology, which \n" +
//                "will improve productivity but cut jobs.  The lack of population \n" +
//                "growth, with its attendant reduction in consumption, makes \n" +
//                "Finland unable to absorb more production or create more employment, \n" +
//                "Stenstrom maintains.  He questions whether Finland or Europe are \n" +
//                "going to provide the ever higher-paying and higher-skilled jobs \n" +
//                "demanded by its more educated work force.  As an example of how \n" +
//                "European governments are running out of solutions, he mentions such \n" +
//                "practices as Denmark's sabbatical leave program, which--used as a \n" +
//                "substitute for paying unemployment compensation--is \"a big \n" +
//                "minus\" for the economy.  \"We are resorting to such methods in a \n" +
//                "situation which we cannot in the long run afford.\" (RB)  (Helsinki \n" +
//                "HUFVUDSTADSBLADET in Finnish 16 Mar 94 p 2) \n" +
//                "\n" +
//                "    FRANCE \n" +
//                "\n" +
//                "    EDITORIAL DISCUSSES G-7 JOB SUMMIT--Deputy editor-in-chief for \n" +
//                "macroeconomics Francoise Crouigneau writes in the Paris financial \n" +
//                "daily LES ECHOS that the G-7 job summit in Detroit is strongly \n" +
//                "symbolic despite the fact that the United States has transformed \n" +
//                "\"this think-tank seminar\" into a \"media happening\" and that each of \n" +
//                "the G-7 countries is \"showing off\" its own solutions for \n" +
//                "unemployment.  The meeting, Crouigneau contends, is an \n" +
//                "acknowledgment of \"worry and of powerlessness\" when faced with \n" +
//                "safeguarding the competitiveness of the companies of industrial \n" +
//                "countries and their ability to employ workers.  The world's work \n" +
//                "force seems to have a choice between being poorly paid and unskilled \n" +
//                "overseas or being skilled but unemployed in Europe. \n" +
//                "\n" +
//                "    Crouigneau argues that if the future world job market depends on \n" +
//                "the highly skilled, then Europe, despite its unemployment problems, \n" +
//                "is further along than the United States, which has essentially \n" +
//                "created low-skilled jobs.  However, creating jobs in the future is \n" +
//                "all the more troublesome since the solution is no longer the \n" +
//                "budgetary and monetary management of governments.  The emergence of \n" +
//                "newly industrialized countries creates an unknown factor in the \n" +
//                "rules of the game for foreign trade.  These rules, Crouigneau points \n" +
//                "out, are all the more important now since  economic guerilla \n" +
//                "warfare\" has replaced past relationships built on politics and the \n" +
//                "Cold War.  Today, the most advanced nations are condemned to adapt \n" +
//                "and innovate to survive.  To do so, they must recognize that two \n" +
//                "conditions are needed.  They must agree to at least a minimum of \n" +
//                "coordination so that growth in the G-7 countries can resume, and \n" +
//                "they must reform their economies to address unemployment caused \n" +
//                "by high labor costs.  Crouigneau acknowledges that no one has the \n" +
//                "secret for accomplishing the latter. (DiI)  (Paris LES ECHOS in \n" +
//                "French 14 Mar 94 p 4) \n" +
//                "\n" +
//                "   COMMENTARY ADVOCATES SOCIAL WELFARE COMMITMENT--According to \n" +
//                "Pascal Riche writing in the 15 March Paris daily LIBERATION, U.S. \n" +
//                "Labor Secretary Robert Reich's comments that countries do not have \n" +
//                "to choose between good jobs or a decent life is an attempt to \n" +
//                "achieve a convergence between Europe's need to create unskilled jobs \n" +
//                "and the U.S. need for skilled employment.  However, while this \n" +
//                "approach seems logical, it may not be the right one, Riche argues. \n" +
//                "The United States is just beginning to try creating skilled jobs and \n" +
//                "no conclusion can be drawn from the U.S. experience at this time. \n" +
//                "As for Europe, it tried \"various schemes\" during the \"laissez-faire\" \n" +
//                "1980's to do the same and failed to produce results, which has led \n" +
//                "to the French Government now proposing to modify minimum wage \n" +
//                "requirements for young people.  For Europeans to seek a third way \n" +
//                "between the two models would probably result in a system that would \n" +
//                "only \"preserve the disadvantages of the two systems,\" Riche \n" +
//                "contends.  Instead, he advocates that European governments \"deepen\" \n" +
//                "their commitment to social welfare.  These governments, Roche notes, \n" +
//                "failed in the fight against unemployment because they did not \n" +
//                "deal with unemployment in a timely manner.  While he adds that some \n" +
//                "blame budgetary difficulties for this neglect, he also points out \n" +
//                "that successive governments allowed social costs to escalate and did \n" +
//                "not recognize the importance of job training.  At the same time, \n" +
//                "working Europeans fought to preserve salary raises in disregard of \n" +
//                "labor market conditions.  However, Riche concludes that if there had \n" +
//                "been more commitment to maintaining social welfare during the \n" +
//                "1980's, the government might have formulated policies to \n" +
//                "stimulate economic growth to alleviate unemployment rather than \n" +
//                "trying its various ineffective job creation schemes.  Instead, it \n" +
//                "allowed unemployment to \"swallow up\" working people \"as if a l0- \n" +
//                "percent unemployment rate were considered 'normal'.\"  (RM)  (Paris \n" +
//                "LIBERATION in French 15 Mar 94 p 3) \n" +
//                "\n" +
//                "    JOB SUMMIT IGNORES WORLD UNEMPLOYMENT--Gerard Dupuy, commenting \n" +
//                "on the G-7 Job Summit in the 15 March Paris daily LIBERATION, cites \n" +
//                "a recent report noting that unemployment rates in the rest of the \n" +
//                "world are \"hugely\" higher than the rate in rich countries. \n" +
//                "According to Dupuy, these \"hundreds of millions of unemployed will \n" +
//                "be ignored in Detroit.\"  Given the breadth of the challenge, it is \n" +
//                "easy to see why summit participants have warned that \"no decisions \n" +
//                "should be expected.\"  While unemployment problems in less-developed \n" +
//                "countries will make a \"timid\" appearance at the summit, Dupuy is \n" +
//                "skeptical about any impact it might have since he sees in it \"less \n" +
//                "compassion about the miseries of the present\" and more concern about \n" +
//                "the threat that such a  problem entails. (RM)  (Paris LIBERATION in \n" +
//                "French 15 Mar 94 p 3) \n" +
//                "\n" +
//                "    COMMENTARY ON G-7 JOB SUMMIT--Delphine Girard, commenting in the \n" +
//                "Paris business daily LA TRIBUNE DESFOSSES on the G-7 Job Summit, \n" +
//                "stated that President Bill Clinton's message on unemployment caught \n" +
//                "Europe \"on the wrong foot.\"  Europeans have for months been trying \n" +
//                "to explain to distraught workers, especially in France, that they \n" +
//                "must choose between wages and employment.  Clinton could have used \n" +
//                "the summit to provide a \"final blow\" to \"old [European] complaints\" \n" +
//                "about social welfare and unemployment, Girard claimed.  Instead, \n" +
//                "Clinton stressed that it is not by underpaying wages and \n" +
//                "multiplying small jobs that industrialized countries will improve \n" +
//                "unemployment.  To create employment, he said, they must instead \n" +
//                "raise living standards by increasing salaries, favoring higher \n" +
//                "education to foster technological progress, developing productivity \n" +
//                "in industries of the future, and reinforcing social protection. \n" +
//                "(DiI)  (Paris LA TRIBUNE DESFOSSES in French 17 Mar 94 p 3) \n" +
//                "\n" +
//                "    U.S. SEEN SPEAKING 'DOUBLE TALK' ON AUDIOVISUAL-- According to \n" +
//                "Emmanuel Schwartzenberg writing the economic supplement to LE \n" +
//                "FIGARO, Le Fig-Eco,  the United States is speaking \"double talk\" \n" +
//                "when it comes to audiovisual policy since U.S. Trade Representative \n" +
//                "Mickey Kantor says one thing while President of the Motion Picture \n" +
//                "Association of America (MPAA) Jack Valenti says another.  While \n" +
//                "Kantor has again threatened to use U.S. trade laws to force Europe \n" +
//                "to open its audiovisual market, Valenti has been very conciliatory \n" +
//                "toward Europeans declaring, \"The government position and that of U.S \n" +
//                "producers and distributors is not identical.  Cinema is outside the \n" +
//                "political field and it is I who guards the keys to the house. \n" +
//                "Kantor has never consulted me on the question of trade sanctions.\" \n" +
//                "\n" +
//                "    Schwartzenberg contends that Valenti, since the GATT agreement, \n" +
//                "considers that the \"too intransigent position\" of the U.S. \n" +
//                "Government reinforces European cohesion and may prod the EU to \n" +
//                "establish a \"super\" trade regulation prejudicial to U.S. interests. \n" +
//                "Persuaded that \"new technologies, which allow each television viewer \n" +
//                "to create his own program, will render legislation obsolete,\" \n" +
//                "Valenti considers that \"France, as can the other European countries, \n" +
//                "can legislate all it wants.\"  (DiI)  (Paris LE FIGARO Le Fig-Eco \n" +
//                "supplement in French 10 Mar 94 p XII) \n" +
//                "\n" +
//                "    SAPIN DISCUSSES MONETARY POLICY--In an interview with a panel of \n" +
//                "journalists from LA TRIBUNE DESFOSSES, Bank of France Monetary \n" +
//                "Policy Council (MPC) member and former Socialist Economy and Finance \n" +
//                "Minister Michel Sapin claimed that the Bank of France has \"without \n" +
//                "question\" become independent.  Nonetheless, Sapin stated he would \n" +
//                "continue the policy followed by the current French Government and \n" +
//                "its predecessors of monetary coordination with the Bundesbank.  He \n" +
//                "also refuted the idea that this policy increases unemployment, \n" +
//                "warned the government about increasing deficits, and said there \n" +
//                "was no \"lasting\" relationship between U.S. interest rates and those \n" +
//                "of Europe. \n" +
//                "\n" +
//                "    Although he contended that the Bank of France was \"certainly\" \n" +
//                "independent, Sapin noted that questions were often asked about the \n" +
//                "counterpart of independence, that is, collegiality.\"  \"There is no \n" +
//                "independence without collegiality,\" Sapin argued, \"because it is \n" +
//                "rarely the case that the correct monetary decision is clearcut.\" \n" +
//                "Decisions are made within the context of a network of constraints \n" +
//                "and contradictions.  To appreciate these contradictory elements and \n" +
//                "this complexity, it is better to put several people together having \n" +
//                "different agendas, experiences, and languages so that monetary \n" +
//                "decisions can be made.  Besides, he added, when monetary decisions \n" +
//                "were a government responsibility, they too were made after a \n" +
//                "collegial debate. \n" +
//                "\n" +
//                "    Sapin, pointing out that the MPC was obliged to be accountable \n" +
//                "to the public and to be responsible in what it said since financial \n" +
//                "markets \"watch its every word,\" stated that it was more necessary \n" +
//                "than ever to coordinate French monetary policy with the Bundesbank. \n" +
//                "The problem with France's relationship with the Bundesbank is not a \n" +
//                "question of independence.  It is a question of either thinking of \n" +
//                "monetary policy in a French or a European sense.  In Sapin's view, \n" +
//                "the need to establish a European single currency must not be \n" +
//                "influenced by economic or market conditions.  The objective is to \n" +
//                "quickly establish a single currency to be able then to have more \n" +
//                "decisive margins of maneuver in relation to the United States and \n" +
//                "Asia.  To arrive at that stage, \"we must go through some \n" +
//                "turbulence,\" he said. \n" +
//                "\n" +
//                "    Regarding unemployment, Sapin said that it cannot be reduced \n" +
//                "without economic growth but that growth is not enough.  In France, \n" +
//                "structural unemployment existed before the recession and it will \n" +
//                "remain after the recovery if something is not done about it.  Sapin \n" +
//                "argues that current monetary policy does not aggravate unemployment, \n" +
//                "since its emphasis on stability has given the economy a sound base. \n" +
//                "Nonetheless, Sapin stated that unemployment was one of his main \n" +
//                "preoccupations. \n" +
//                "\n" +
//                "    Budget deficits are another.  Sapin said it did not bother him \n" +
//                "if deficits were used in recessions to help fuel the economy. \n" +
//                "However, when recovery starts the government must change its \n" +
//                "spending policy or consumers and companies either will not have \n" +
//                "access to money or the money will cost too much.  \"We are at this \n" +
//                "point today,\" he said, and the government must now make reducing the \n" +
//                "deficit a priority to meet the very strict budgetary requirements \n" +
//                "for French-German convergence. \n" +
//                "\n" +
//                "    Sapin was also asked how he explained what happened in financial \n" +
//                "markets when the United States raised its interest rates.  He \n" +
//                "replied that the U.S. interest rate hike affected Europe for \"no \n" +
//                "rational reason,\" indicating that European financial agents did not \n" +
//                "understand that increased rates in the United States are not \n" +
//                "contradictory with pursuing lower rates in Europe.  There is no \n" +
//                "lasting connection between the United States and Europe because \n" +
//                "the economic situations are very different, Sapin declared. (Dil) \n" +
//                "(Paris LA TRIBUNE DESFOSSES in French 11 Mar 94 p 25) \n" +
//                "\n" +
//                "    EDITORIAL ON OECD VIEWS ON FRENCH INTEREST RATES--In an \n" +
//                "editorial in LA TRIBUNE DESFOSSES, editor-in-chief Philippe Labarde \n" +
//                "claims that the recent report by the OECD, which advocated \n" +
//                "unilateral French interest rate reductions as the only way to \n" +
//                "sustain demand in France if German rates did not decline, was \n" +
//                "\"priceless\" as a means to resuscitate the debate on French \n" +
//                "monetary policy.  Despite the hard line taken by the Bank of France \n" +
//                "and Prime Minister Edouard Balladur in support of continuing its \n" +
//                "close monetary coordination with Germany, Labarde notes that the \n" +
//                "critics of such a policy are increasing.  Numerous economists and \n" +
//                "heads of companies are adding their voices to that of the OECD. \n" +
//                "Labarde argues that the OECD, as a bastion of free market orthodoxy, \n" +
//                "gives credence to these \"partisans of a French offensive monetary \n" +
//                "policy.\"  Before the OECD report, these critics were considered at \n" +
//                "best as \"sorcerers apprentices or dreamers,\" at worst as secret \n" +
//                "devaluationists, notorious anti-Europeanists, or shameful \n" +
//                "protectionists.\" \n" +
//                "\n" +
//                "    The OECD statement, however, may not lead to monetary \n" +
//                "authorities changing their policy, Labarde cautions.  The government \n" +
//                "has already missed two occasions which would have allowed it freedom \n" +
//                "from the Bundesbank: the first was when it was elected and the \n" +
//                "second during last August's monetary crisis.  Labarde concludes that \n" +
//                "while it is never too late to change monetary direction and that \n" +
//                "economic recovery would be helped by a significant lowering of \n" +
//                "discount rates, he acknowledges that current political and monetary \n" +
//                "conditions make it difficult to do so. (DiI)  (Paris LA TRIBUNE \n" +
//                "DESFOSSES in French 10 Mar 94 p 32) \n" +
//                "\n" +
//                "    EDITORIAL ON GOVERNMENT SUBSIDIES--Deputy editor-in-chief \n" +
//                "Francois Roche, writing in the Paris business daily LA TRIBUNE \n" +
//                "DESFOSSES, observes that government subsidies for public companies \n" +
//                "are expensive for France but that the \"real\" problem of France's \n" +
//                "public companies, especially the computer company Bull and Air \n" +
//                "France, is that they have been mismanaged for a long time.  Roche \n" +
//                "notes that some would find it \"scandalous\" that Bull is receiving 7- \n" +
//                "8 billion francs (Fr) and Air France Fr20 billion from the French \n" +
//                "Government.  He characterizes such government assistance as \"at the \n" +
//                "limit of decency\" in the opinion of many, coming at a time when \n" +
//                "private companies are taking risks to find their own financial \n" +
//                "resources in the market place.  He claims the distortion it causes \n" +
//                "in competition is \"too flagrant\" not to \"set right.\" \n" +
//                "\n" +
//                "    However, Roche states that the cause for Bull's and Air France's \n" +
//                "financial problems lies with long-term government mismanagement. \n" +
//                "These two companies have never been managed as they have should have \n" +
//                "been, that is to say, as part of a competitive free market.  Their \n" +
//                "directors have never been able to cut themselves off, even when they \n" +
//                "wanted to, from larger political issues.  Moreover, the government, \n" +
//                "when it harbored \"great ambitions\" in data processing or air \n" +
//                "transport, never furnished these two companies with the financial \n" +
//                "means to fulfill these \"grandiose\" projects.  Roche concludes that \n" +
//                "Bull and Air France, which have been sheltered from the realities of \n" +
//                "the world in an attempt to make them the \"guarantors of national \n" +
//                "independence,\" are now at the \"end of their rope.\"  Roche adds that \n" +
//                "while it is understandable that the new directors of these companies \n" +
//                "do not want to deal with the consequences of past mismanagement, \n" +
//                "waiting for them to come to grips with their problems wastes money, \n" +
//                "energy, and demoralizes employees. \n" +
//                "(DiI)  (Paris LA TRIBUNE DESFOSSES in French 11 Mar 94 p 40) \n" +
//                "\n" +
//                "    DASSAULT ASKS ASSEMBLY TO STOP RAFALE DELAYS--In a plea to the \n" +
//                "members of the National Assembly defense commission on 1 March, \n" +
//                "Dassault Aviation Chairman Serge Dassault asked that the Rafale \n" +
//                "fighter aircraft program no longer be delayed.  \"The survival of the \n" +
//                "group is linked to three factors,\" he said, \"The Rafale program, \n" +
//                "innovative future projects, and civilian and military aircraft \n" +
//                "exports.\"  The Rafale program was last delayed for six months at the \n" +
//                "end of 1993 and resulted in the loss of 1,000 jobs, half of them in \n" +
//                "the Dassault group.  Serge Dassault pointed out that under present \n" +
//                "conditions, the Ministry of Defense foresees that \"very few\" Rafale \n" +
//                "aircraft will be delivered before the end of the decade.  (RM) \n" +
//                "(Paris LE MONDE in French 4 Mar 94 p 10) \n" +
//                "\n" +
//                "    ROOTS OF UNEMPLOYMENT VIEWED--In the Paris daily LE QUOTIDIEN DE \n" +
//                "PARIS, Economist Michel Drancourt notes that there is a \"French \n" +
//                "cultural exception\" with regard to the causes of unemployment.  As \n" +
//                "early as the 1970's, the Europeans, and notably the French, chose to \n" +
//                "increase the salaries and social benefits of workers, despite \n" +
//                "periodic economic crises, and to finance growing unemployment by \n" +
//                "steadily increasing workers wages and thus employer social \n" +
//                "contributions.  Drancourt remarks that such a \"revenues policy\" must \n" +
//                "be revised in favor of an \"employment policy.\"  He suggests the \n" +
//                "French could learn from the U.S. system, which he notes is \"more \n" +
//                "efficient,\" with job creation increasing because productivity \n" +
//                "increases faster than do wages. \n" +
//                "    Drancourt does not recommend \"systematically reducing\" salaries \n" +
//                "but proposes that they should be linked to productivity.  The high \n" +
//                "cost of salaries, he argues, has limited companies chances to \n" +
//                "increase productivity.  In playing the role of provider, the state \n" +
//                "caused a systematic reduction in employment in both large and small \n" +
//                "companies.  The growth in unemployment, however, did not result in \n" +
//                "lower salaries.  Drancourt lays the blame on social benefit costs, \n" +
//                "which are rising faster than direct salaries because they pay for \n" +
//                "redistribution of capital which does not promote job creation. \n" +
//                "\"Rigidity should be dismantled,\" Drancourt contends, and real \n" +
//                "salaries should be adjusted according to productivity.  This entails \n" +
//                "an adjustment in social benefit costs and their financing--a \n" +
//                "difficult but necessary solution, Drancourt concludes.  (RM)  (Paris \n" +
//                "LE QUOTIDIEN DE PARIS in French 3 Mar 94 p \n" +
//                "4) \n" +
//                "\n" +
//                "    U.S. 'PRESSURE' DIVIDES EU ON NAVAL CONSTRUCTION--A debate is \n" +
//                "beginning in Brussels on the position it should adopt in the last \n" +
//                "round of OECD negotiations on naval construction subsidies.  EU \n" +
//                "shipbuilders fear that the EU Commission will only adopt a \n" +
//                "\"minimalist\" position in the face of U.S. retaliatory threats, \n" +
//                "according to an article in the Paris business daily LES \n" +
//                "ECHOS.  The naval shipbuilders union (CSCN) claimed that EU \n" +
//                "negotiators want an agreement at any cost even if it is an \n" +
//                "inequitable one and shipbuilders fear they will lose the little \n" +
//                "protection they now have.  However, the terms of the agreement which \n" +
//                "EU negotiators were prepared to accept could be modified under \n" +
//                "French Government pressure, the article contends, because \n" +
//                "France is \"sharply opposed\" to the EU \"minimalist\" position. \n" +
//                "\n" +
//                "    The EU position, which was also criticized by Spain, Belgium, \n" +
//                "and Italy, would abolish as of 1 January 1995 aide to naval \n" +
//                "construction and aid to shipowners linked to naval construction. \n" +
//                "Japanese subsidies to its shipbuilding industry, however, would be \n" +
//                "examined to find out if it distort the market, but the results from \n" +
//                "such an examination would not be ready for several years.  No \n" +
//                "safeguards have been provided in case of \"monetary dumping.\"  The \n" +
//                "United States could continue, at least for a while, numerous \n" +
//                "protectionist measures, especially those contained in the Jones Act, \n" +
//                "which stipulates ships must be built in the United States and keeps \n" +
//                "foreign builders from participating in a $1.5-billion market, \n" +
//                "according to a CSCN study.  In addition to the United States \n" +
//                "protecting its shipbuilders in the civilian market at a time when \n" +
//                "military orders have decreased, the United States is also providing \n" +
//                "$2 billion a year for shipyards to convert to civilian work.  The \n" +
//                "article concludes that the United States has entered the \n" +
//                "final round of OECD negotiations by brandishing the threat of \n" +
//                "retaliation if an agreement is not reached because they are \"certain \n" +
//                "of their right, or at least their power, [to do so].\" (DiI)  (Paris \n" +
//                "LES ECHOS in French 10 Mar 94 p \n" +
//                "12) \n" +
//                "\n" +
//                "    GERMANY \n" +
//                "\n" +
//                "    COMMENTATORS VIEW JOBS SUMMIT--Although German press \n" +
//                "commentators generally agreed that the recent G-7 Jobs Summit \n" +
//                "produced few concrete results, some thought the gathering was \n" +
//                "important as a sign of new international priorities and as an \n" +
//                "opportunity for countries to learn from each other's experiences. \n" +
//                "Writing in the 16 March SUEDDEUTSCHE ZEITUNG, Peter De Thier \n" +
//                "complained that the meeting was \"rich in symbols and poor in \n" +
//                "content\" and that it served \"no concrete purpose,\" except as a \n" +
//                "\"glittering self-portrait of the resurgent U.S. economy.\"  Other \n" +
//                "commentators agreed that the meeting yielded few tangible results \n" +
//                "but noted that it was not expected or intended to.  Gerd \n" +
//                "Brueggemann, for example, pointed dut in DIE WELT of 16 \n" +
//                "March that \"quick successes were not to be expected,\" and Josef \n" +
//                "Joffe declared in the same day's SUEDDEUTSCHE ZEITUNG that meager \n" +
//                "results were \"predictable, because summits rarely produce anything \n" +
//                "that was not put on paper beforehand.\" \n" +
//                "\n" +
//                "    Some commentators, however, thought the conference was important \n" +
//                "despite this dearth of concrete achievements.  Carola Kaps, for \n" +
//                "example, argued in the 16 March FRANKFURTER ALLGEMEINE that \"it \n" +
//                "would probably be wrong\" to call the summit a \"failure,\" since its \n" +
//                "only objective was to allow the G-7 countries to \"learn from one \n" +
//                "another through an intensive exchange of opinions\" and \"to take home \n" +
//                "new ideas or motivations.\"  Judged by this standard, she argued, the \n" +
//                "meeting enjoyed at least one \"success,\" since the United States \n" +
//                "seemed to have come away with more understanding and acceptance \n" +
//                "of Germany's reluctance to stimulate its economy.  Kaps, along with \n" +
//                "Joffe, also saw the conference as evidence that the West had changed \n" +
//                "its international economic priorities.  The meeting, both writers \n" +
//                "noted, marked the first time Western leaders had met to discuss \n" +
//                "unemployment, which was previously viewed as too prosaic for their \n" +
//                "attention.  For her part, Kaps believed this change would \"send a \n" +
//                "signal\" to average citizens that their leaders \"are aware of \n" +
//                "people's fears\" and of the socially \"dangerous\" effects \n" +
//                "of a high rate of joblessness.  Joffe, meanwhile, saw the meeting's \n" +
//                "attention to the jobs issue as important \"because it enhances the \n" +
//                "shift in awareness\" toward the unemployment issue, and because it \n" +
//                "showed that the G-7 recognized the \"globalization\" of economic \n" +
//                "problems and solutions.  (RoH)  (Munich SUEDDEUTSCHE ZEITUNG in \n" +
//                "German 16 Mar 94 pp 4, 28; Berlin DIE WELT in German 16 Mar 94 p 6; \n" +
//                "Frankfurt/Main FRANKFURTER ALLGEMEINE in German 16 Mar 94 p \n" +
//                "15) \n" +
//                "\n" +
//                "    ENVIRONMENTAL TECHNOLOGY SEEN AS CREATING JOBS--In the field of \n" +
//                "environmental technology, Germany is the \"undisputed\" world champion \n" +
//                "exporter, ahead of the United States and Japan, according to Federal \n" +
//                "Minister of the Environment Klaus Toepfer, who is a member of the \n" +
//                "Christian Democratic Union (CDU).  Speaking at the Leipzig Spring \n" +
//                "Fair, Toepfer presented the following data: \n" +
//                "\n" +
//                "    --In 1991 (the latest year for which statistics are available) \n" +
//                "Germany captured a 20-percent share of the world environmental \n" +
//                "technology export market, with exports of goods and services \n" +
//                "amounting to 37 billion German marks (DM) or 6 percent of the \n" +
//                "nation's total industrial exports. \n" +
//                "\n" +
//                "    --Currently, 680,000 German workers are employed in the \n" +
//                "environmental technology industry, a number which could grow to 1 \n" +
//                "million by the year 2000. \n" +
//                "\n" +
//                "    Toepfer interprets the data as evidence that \"German \n" +
//                "environmental standards are not the reason for our economic \n" +
//                "problems\" but rather a stimulus for growth, given an environmental \n" +
//                "technology market that is \"nearly limitless.\"  Citing an OECD study \n" +
//                "which estimates the volume of potential environmental sales in East \n" +
//                "Europe alone at $15 billion, Toepfer expressed his hope that German \n" +
//                "industry \"will make use of its lead in exporting and will not gamble \n" +
//                "away its environmental know-how.\"  To this end, he intends to \n" +
//                "champion the establishment in Leipzig of an environmental technology \n" +
//                "center with a \"special\" orientation toward Central and East Europe. \n" +
//                "(JS) (Berlin DIE WELT in German 10 Mar 94 p 14) \n" +
//                "\n" +
//                "    GOVERNMENTS PUSH SMALL BUSINESS FUNDING--The laender governments \n" +
//                "in eastern Germany are trying to encourage the growth of small- and \n" +
//                "medium-sized businesses through a variety of aid programs. \n" +
//                "Thuringia, for example, is providing funds to new businesses to help \n" +
//                "preserve and create jobs and to assist with sales, trade fairs, \n" +
//                "consulting work, travel, credit guarantees, and direct investments. \n" +
//                "The German Bank of Settlements has extended aid to \n" +
//                "35,000 businessmen in Thuringia, using its roughly DM6 billion in \n" +
//                "seed funds to generate DM9 billion in private investment.  This \n" +
//                "program has preserved or created about 250,000 jobs since 1990, \n" +
//                "notably in the trade and craft sector. \n" +
//                "\n" +
//                "    Meanwhile, in heavily industrialized Saxony-Anhalt, the land \n" +
//                "government is attempting to stimulate banks to fund small firms' \n" +
//                "needs through credit guarantees and manufacturing subsidies for \n" +
//                "particular products.  The land of Brandenburg claims to have funded \n" +
//                "48,000 small entrepreneurs with DM15.1 billion since 1991 and to \n" +
//                "have more than offset the loss in manufacturing jobs by the increase \n" +
//                "in trade and craft positions.  Finally, the government of \n" +
//                "Mecklenburg-Vorpommern has instituted two new funding mechanisms for \n" +
//                "small businesses, the \"capital support program,\" which provides \n" +
//                "supplemental capital funding, and the \"consolidation program,\" a \n" +
//                "one-time advance to establish liquidity or to refinance loans.  (CW) \n" +
//                "(Duesseldorf HANDELSBLATT in German 10 Mar 94 p 8) \n" +
//                "\n" +
//                "    NEW SEMICONDUCTOR PLANT OPENS IN EAST--System Microelectronic \n" +
//                "Innovation (SMI), the privatized successor to East Germany's VEB \n" +
//                "Halbleiterwerk of Frankfurt/Oder, is the first European company to \n" +
//                "manufacture silicon-based, superfast bipolar semiconductors.  A new \n" +
//                "DM17-million plant developed by Synergy Semiconductor Corporation, a \n" +
//                "U.S. firm which owns 49 percent of SMI, has just been opened.  SMI \n" +
//                "expects sales of DM33 million in 1994 and estimates selling 50 \n" +
//                "percent more in 1995, largely because of sales of the new \n" +
//                "semiconductor, which is based on a Toshiba license.  SMI believes \n" +
//                "the new device will soon account for nearly 50 percent of the its \n" +
//                "output.  Once profitability is reached and investors are found for \n" +
//                "the Trust Agency's remaining stake, Synergy plans to increase its \n" +
//                "share in SMI to 51 percent and foresees a total investment of DM40 \n" +
//                "million to develop the German company. \n" +
//                "(CW)  (Frankfurt/Main FRANKFURTER ALLGEMEINE in German 7 Mar 94 p \n" +
//                "18) \n" +
//                "\n" +
//                "    BITTERFELD CHEMICAL FACILITIES EXPANDED--The Chemiepark \n" +
//                "Bitterfeld industrial park reports that 178 firms have located in \n" +
//                "its complex--once the center of the East German chemical industry-- \n" +
//                "and that over DM2 billion have been spent on new manufacturing \n" +
//                "facilities and businesses there.  Barely one-half of the complex's \n" +
//                "GDR-era companies have been privatized and some 40 percent of its \n" +
//                "production facilities are shut down and being dismantled. \n" +
//                "Akzo Nobel, a Dutch firm, has just purchased part of a phosphorus \n" +
//                "production company in Bitterfeld from the Trust Agency and plans to \n" +
//                "invest DM50 million by 1996 to establish production lines for fire- \n" +
//                "retardant chemicals. \n" +
//                "\n" +
//                "    The Eschborn-based Ausimont Deutschland company, a subsidiary of \n" +
//                "Italy's Montedison group, is building a DM140 million hydrogen \n" +
//                "peroxide plant at Bitterfeld and expects to produce 40,000 tons of \n" +
//                "the chemical per year, yielding revenues in the \"high double-digit \n" +
//                "millions.\"  Chemiepark Bitterfeld's manufacturing branch reports \n" +
//                "that the complex's inorganic chemicals and dyestuffs facilities are \n" +
//                "still not privatized but that negotiations for sale of the pesticide \n" +
//                "production facilities are progressing. (CW)  (Frankfurt/Main \n" +
//                "FRANKFURTER ALLGEMEINE in German 3 Mar 94 p 19) \n" +
//                "\n" +
//                "    GREECE \n" +
//                "\n" +
//                "    CIVIL SERVICE 'INFLATION' NOTED--According to Fotini Kalliri \n" +
//                "writing in the 3 March Athens daily I KATHIMERINI, \"the thoughtless \n" +
//                "hiring policy\" of both the New Democracy (ND) and the Panhellenic \n" +
//                "Socialist Movement (PASOK) governments in recent years has resulted \n" +
//                "in a \"ponderous, costly, and submissive\" civil service, which \n" +
//                "presently numbers  about 700,000 workers.  For a population of 10 \n" +
//                "million, that is one civil servant for every 14 inhabitants.  Total \n" +
//                "figures have steadily risen: 53,914 in 1972, 100,955 in 1980, and \n" +
//                "589,386 in 1988.  Salary and pension costs have risen from 1,858.3 \n" +
//                "billion drachmas in 1992 to 2,040 billion drachmas in 1993 and are \n" +
//                "projected to reach 2,272.6 billion in 1994.  (RM)  (Athens I \n" +
//                "KATHIMERINI in Greek 3 Mar 94 p 6) \n" +
//                "\n" +
//                "    GREEK-ROMANIAN ECONOMIC RELATIONS--A Greek-Romanian Chamber of \n" +
//                "Commerce was recently established in Athens by a number of Greek \n" +
//                "companies, with a Salonica and a Budapest branch expected soon, \n" +
//                "aimed at intensifying commercial and industrial relations between \n" +
//                "the two countries.  Between January and October 1993, Greek exports \n" +
//                "to Romania dropped to $73.9 million from the 1992 mark of $97.8 \n" +
//                "million.  A simultaneous drop was reported in Romanian exports to \n" +
//                "Greece, which declined from $46.7 million in a 10-month \n" +
//                "period in 1992 to $43 million in the same period in 1993. (RM) \n" +
//                "(Athens TO VIMA TIS KIRIAKIS in Greek 6 Mar 94 p D31) \n" +
//                "\n" +
//                "    ENERGY POLLUTION TAXES ASSESSED--According to a study by \n" +
//                "Ministry of National Economy experts, the EU Commission wants to \n" +
//                "levy a general energy and carbon dioxide environmental tax during \n" +
//                "the Greek EU presidency.  The study claims' the burden of such a tax \n" +
//                "on Greece is \"significant,\" and will: \n" +
//                "\n" +
//                "    *  Increase the inflation rate by 1 percent a year. \n" +
//                "    *  Decrease private investment by 2.5 percent a year. \n" +
//                "    *  Decrease GDP by 0.5 percent. \n" +
//                "    *  Decrease employment by 0.5 percent. \n" +
//                "    *  Increase cost of electricity.  (RM) \n" +
//                "(Athens I KATHIMERINI in Greek 1 Mar 94 p 21) \n" +
//                "\n" +
//                "    ITALY \n" +
//                "\n" +
//                "    LABOR MINISTER DISCUSSES BLACK MARKET LABOR--At an informal EU \n" +
//                "meeting on labor held in Athens on 10 March, Labor Minister Gino \n" +
//                "Giugni discussed a white paper prepared by the Italian Government on \n" +
//                "unemployment.  The paper, which identified most of the 2.5 million \n" +
//                "unemployed as women, youths with little education, and southerners, \n" +
//                "also said that the 2.5 million unemployed is paralleled by the \n" +
//                "\"totally Italian anomaly\" of black market labor involving 2.4 \n" +
//                "million people.  According to Giugni this figure was derived by \n" +
//                "studying the following factors: Italy's extremely high propensity \n" +
//                "for saving, the unusual ratio of the self-employed to employed \n" +
//                "workers, the low participation of women in the \"official\" job \n" +
//                "market, and \"an otherwise unexplainable general ability for the \n" +
//                "public to continue purchasing government bonds.\"  The white paper \n" +
//                "concludes that the extent of the underground economy will make it \n" +
//                "very difficult for Italy's next government to bring the \"value\" of \n" +
//                "Italian labor up to that of the rest of Europe by increasing the \n" +
//                "number of the \"officially employed,\" thereby guaranteeing \n" +
//                "increased tax revenues and social welfare benefits. \n" +
//                "\n" +
//                "    The white paper also details the government's program to create \n" +
//                "1 million new jobs by the year 2000 by raising GDP at least 3 \n" +
//                "percent yearly, raising the mandatory school age to 16 and later to \n" +
//                "18, increasing flexible forms of employment, legalizing many of \n" +
//                "those forms to bring many of the \"underground\" jobs into the system, \n" +
//                "and encouraging part-time work to make it a truly competitive form \n" +
//                "of labor. (AB)  (Milan IL SOLE-24 ORE in Italian 11 Mar 94 \n" +
//                "pp 1, 13) \n" +
//                "\n" +
//                "    RECENT LABOR ACCORDS SEEN AS STOPGAP MEASURES--The recently \n" +
//                "concluded labor agreements between the labor unions and Fiat, \n" +
//                "Olivetti, and Italtel, which were concluded thanks to Labor Minister \n" +
//                "Gino Giugni's intervention, are considered to be mere stopgap \n" +
//                "measures that will cost the government billions of lire and will not \n" +
//                "solve the long-term problem of unemployment.  Originally, the three \n" +
//                "companies had asked to be allowed to lay off 11,150 workers. \n" +
//                "Fearing social unrest, the government came up with a plan that \n" +
//                "would allow most of those workers to be retained under \"solidarity \n" +
//                "contracts.\"  These contracts stipulate that the employees would work \n" +
//                "reduced hours and the government would make up 75 percent of the \n" +
//                "lost pay. \n" +
//                "\n" +
//                "    However, while these measures are designed to preserve the \n" +
//                "workers' jobs and salaries, they are temporary measures that will \n" +
//                "have to be renegotiated soon unless the economy rapidly improves. \n" +
//                "Former union leader and current director of personnel at Italtel \n" +
//                "Luciano Scalia said that unemployment is structural in nature and \n" +
//                "unless the work force is reduced, the problem will return as early \n" +
//                "as December 1995.  At present, it is impossible to assess precisely \n" +
//                "the cost to the government because to do so requires detailed \n" +
//                "information is needed about the age, salary, and seniority of each \n" +
//                "employee--something which is not readily available.  General \n" +
//                "estimates, however, place the total cost to the government at 2.1 \n" +
//                "trillion lire. (AB)  (Milan IL MONDO in Italian 28 Feb-7 Mar pp 34- \n" +
//                "35) \n" +
//                "\n" +
//                "    ENI'S PLANS TO EXPAND EAST--The National Hydrocarbons \n" +
//                "Corporation's (ENI) planned reorganization to prepare for the \n" +
//                "eventual privatization of its two main groups--energy and chemicals- \n" +
//                "-includes expanding its activities in the former Soviet republics of \n" +
//                "Central Asia, Russia, and China, according to ENI's General Manager \n" +
//                "Franco Bernabe in an interview published in Milan's CORRIERE DELLA \n" +
//                "SERA.  Bernabe explained that the changes taking place within \n" +
//                "ENI are intended to correct the overly ambitious expansion plans of \n" +
//                "its former management--involved in corruption and kickback scandals- \n" +
//                "-which led ENI into \"tremendous\" debt.  The current plans are to \n" +
//                "sell off at least 80 of ENI's subsidiaries, keeping ENI's energy and \n" +
//                "chemical companies.  In the meantime, ENI is planning to go from an \n" +
//                "international conglomerate operating primarily in the Middle East \n" +
//                "and western Africa, to a multinational entity focusing primarily on \n" +
//                "the energy field that will expand as far east as Siberia and China. \n" +
//                "Bernabe said ENI will not decrease its presence in the Middle East \n" +
//                "and western Africa, as 80 percent of Italy's oil reserves are \n" +
//                "located there. \n" +
//                "\n" +
//                "    Bernabe said that Russia, Kazakhstan, and the Caspian Sea hold \n" +
//                "tremendous mineral reserves, and ENI is involved in large-scale \n" +
//                "projects in those areas either alone or in joint ventures.  In \n" +
//                "China, ENI will not only pursue mineral exploration there but hopes \n" +
//                "to participate in refining crude oil and its distribution.  (AB) \n" +
//                "(Milan CORRIERE DELLA SERA in Italian 7 Mar p 19) \n" +
//                "\n" +
//                "   NORWAY \n" +
//                "\n" +
//                "    RECORD EXPORT YEAR REPORTED--While Norwegian imports increased \n" +
//                "in 1993 by 5.6 percent, exports had a record year, according to a \n" +
//                "report published by the Norwegian Export Council in late January \n" +
//                "1994.  Total exports increased in value by 4.6 percent to 316.8 \n" +
//                "billion Norwegian kroner.  The greatest increase was in oil and \n" +
//                "natural gas (6.8 percent), while traditional exports from the \n" +
//                "mainland increased by 3.8 percent in value.  Fish exports increased \n" +
//                "by 8 percent in 1993, making that industry Norway's third largest. \n" +
//                "\n" +
//                "    The rise in exports is due to greater Norwegian competitiveness \n" +
//                "and strong growth in foreign markets, the report stated.  Germany \n" +
//                "replaced the Nordic countries as Norway's most important export \n" +
//                "market in 1993 with the UK as the second most important.  Exports to \n" +
//                "the UK increased by 20 percent in 1993.  The export of construction \n" +
//                "materials to the UK quintupled from 1992 to 1993.  Additionally, \n" +
//                "exports to Japan, China, and the Far East grew strongly in 1993. \n" +
//                "(TF)  (Oslo AFTENPOSTEN in Norwegian 26 Jan 94 p 22) \n" +
//                "\n" +
//                "    PENSION FUND CALLED 'PROFITABLE BUSINESS'--The agency charged \n" +
//                "with investing National Pension Fund revenues had a \"boom year\" in \n" +
//                "1993, according to an article in the Oslo daily AFTENPOSTEN.  The \n" +
//                "Fund's operating costs, which are only a minute fraction of its \n" +
//                "income, are covered by only one day's income, according to General \n" +
//                "Manager Tore Lindholt.  The rest is profit, he added.  In 1993, the \n" +
//                "Fund had profits of almost 14.9 billion kroner, or more than 1 \n" +
//                "billion kroner for each member of the Fund's 13-person staff, \n" +
//                "prompting AFTENPOSTEN to call the Fund \"Norway's most profitable \n" +
//                "business.\" \n" +
//                "\n" +
//                "    The Fund increasingly acts as lender to the Norwegian state and \n" +
//                "has become one of the leading actors on the Oslo Stock Exchange, \n" +
//                "having placed 8.5 percent of its capital in stocks there.  The Fund \n" +
//                "is also seeking permission from the government to invest 10 percent \n" +
//                "of its capital abroad.  (TF)  (Oslo AFTENPOSTEN in Norwegian 8 Feb \n" +
//                "94 p 27) \n" +
//                "\n" +
//                "    SWEDISH BANK PREDICTS GROWTH FOR NORWAY, DENMARK--Norway and \n" +
//                "Denmark are identified by economists at Sweden's Handelsbanken as \n" +
//                "being among the European countries that will experience the greatest \n" +
//                "growth in 1994.  The economies of Sweden and Finland are also \n" +
//                "expected to grow, following the worst recession in those countries \n" +
//                "since the depression of the 1930's.  As a whole, the Nordic region \n" +
//                "is expected to lead in growth in Europe. \n" +
//                "\n" +
//                "    The strong growth predicted for Denmark and Norway is attributed \n" +
//                "to increased domestic demand and, in Denmark's case, to several \n" +
//                "years of austere fiscal policies and a surplus in foreign trade. \n" +
//                "Norway's GDP is expected to rise by 3 percent in 1994 and 3.5 \n" +
//                "percent in 1995 according to the Handelsbanken report. (TF)  (Oslo \n" +
//                "AFTENPOSTEN in Norwegian 17 Feb 94 p 26) \n" +
//                "\n" +
//                "   1993 PROFITABLE FOR MOST BUSINESSES--The annual reports of \n" +
//                "Norway's major industrial, shipping, and banking firms indicate \n" +
//                "moderate to fast growth in profits for 1993.  According to an \n" +
//                "article in the Oslo daily AFTENPOSTEN, the total growth in profits \n" +
//                "after taxes for these firms was 70 percent.  The growth trend of \n" +
//                "these companies--Den norske Bank (DnB), Statoil, metals \n" +
//                "exporter Elkem, food manufacturer Rieber, pharmacological products \n" +
//                "exporter Hafslund Nycomed, shipping giant Bergesen--are viewed by \n" +
//                "chief analyst Sigmund Ellingsen of DnB Funds as typical of the \n" +
//                "entire commercial sector.  He feels the growth will only continue. \n" +
//                "To a considerable extent this growth is attributed to the cut in \n" +
//                "costs which has made Norwegian industry more competitive \n" +
//                "internationally.  However, while this increased competitiveness \n" +
//                "boosted exports in 1993, it also increased unemployment.  (TF) \n" +
//                "(Oslo AFTENPOSTEN in Norwegian 11 Feb 94 p 27) \n" +
//                "\n" +
//                "    NEW OIL DRILLING PLANNED--The Ministry of Industry and Energy \n" +
//                "has opened up several new fields on the Norwegian Shelf for explore \n" +
//                "for oil.  The ministry will allow a maximum of six drillings off the \n" +
//                "Nordland coast and four drillings in the Skagerrak.  Responding to \n" +
//                "criticism from environmental organizations and fishermen, Industry \n" +
//                "and Energy Minister Jens Stoltenberg noted that if oil is found, any \n" +
//                "development of the wells would not start until the year 2010 and \n" +
//                "that in any case, the country's best fishing areas are not in the \n" +
//                "area proposed for exploration.  He added that Norway has \"30 \n" +
//                "years' experience in guarding the coexistence of the oil and \n" +
//                "fisheries industries.\" (TF)  (Oslo AFTENPOSTEN in Norwegian 25 Feb \n" +
//                "94 p 21) \n" +
//                "\n" +
//                "    COMPETITIVENESS OF NORWEGIAN SHELF ASSESSED--A report \n" +
//                "commissioned by the Ministry of Industry and Energy on the future \n" +
//                "competitiveness of the Norwegian Shelf states that costs in the oil \n" +
//                "industry will have to be cut in half in the future.  It also \n" +
//                "concludes that the oil industry's taxes and fees must be made more \n" +
//                "predictable for the industry to compete.  The report says that \n" +
//                "controlling costs is the industry's most important task, suggesting \n" +
//                "that projects currently planned should cut costs by 25-30 percent. \n" +
//                "\n" +
//                "    The report states that the North Sea and the Norwegian Shelf are \n" +
//                "no longer the most attractive areas for oil companies to explore. \n" +
//                "This is in large measure because new, more competitive oil fields \n" +
//                "have emerged in Asia, Africa, and the former Soviet Union, according \n" +
//                "to the report, which was written by representatives of the Norwegian \n" +
//                "Government, the oil companies, and the delivery industry.  (TF) \n" +
//                "(Oslo AFTENPOSTEN in Norwegian 17 Feb 94 p \n" +
//                "27) \n" +
//                "\n" +
//                "    SWEDEN \n" +
//                "\n" +
//                "    SWEDISH CONTRIBUTION TO EU BUDGET CALCULATED--The Finance \n" +
//                "Ministry has calculated that Sweden's net contribution to the EU \n" +
//                "budget will be 8.9 billion kronor in 1995--potentially the first \n" +
//                "year of membership--then rise to 11.7 the next year and total about \n" +
//                "18 billion by 1999, when the transition period for budget payments \n" +
//                "is complete.  The total sum for 1995 is expected to be 18.3 billion \n" +
//                "but from this is deducted 5 billion in agricultural and regional \n" +
//                "support and an additional 4.4 billion in rebates.  For 1996, these \n" +
//                "figures will be 19.1 billion total, 3.5 in subsidies, and 3.9 \n" +
//                "billion in rebates.  Sweden will receive an additional 7.6 billion \n" +
//                "kronor per year in agricultural support that will not be counted in \n" +
//                "the state budget.  It can also receive a maximum of 1.8 billion in \n" +
//                "environmental support, if it spends the same amount domestically. \n" +
//                "Under the framework of joint programs, Sweden can also expect to \n" +
//                "receive one-half to one billion kronor in subsidies for research and \n" +
//                "development, culture, and youth, according to the Finance Ministry \n" +
//                "report.  (WS)  (Stockholm DAGENS NYHETER in Swedish 4 Mar 94 p C2) \n" +
//                "\n" +
//                "    ECONOMIC BRIEFS \n" +
//                "\n" +
//                "    France \n" +
//                "\n" +
//                "    --EMIRATES NEWS reports France's Giat Industries has annulled \n" +
//                "contract with French Offset partners group.  company wants to revise \n" +
//                "compensation program linked to Leclerc tank sales to United Arab \n" +
//                "Emirates. (LE FIGARO 17 Mar 94 p 1) \n" +
//                "\n" +
//                "    Greece \n" +
//                "\n" +
//                "    --Government to \"finally\" tax gambling, most heavily on \n" +
//                "establishments masquerading as coffee houses...one-sixth of taxes to \n" +
//                "be paid at time of licensing, rest in five bimonthly payments. \n" +
//                "revenues may reach 10 billion drachmas yearly. (TO VIMA TIS KIRIAKIS \n" +
//                "6 Mar 94 p D12) \n" +
//                "\n" +
//                "    Norway \n" +
//                "\n" +
//                "    State-Owned Statoil considering start-up operations in eight new \n" +
//                "gas, oil fields on Norwegian Shelf...operations to begin in 1994-95. \n" +
//                "(AFTENPOSTEN 5 Feb 94 p 24) \n" +
//                "\n" +
//                "    Anilla B. (703-733-6283), Roger B. (703-733-6508), Thale F. \n" +
//                "(703-733-6294), Robert H. (703-733-6504), Dian I. (703-733-6286), \n" +
//                "Rosa M. (703-733-6287), Joel S. (703-733-6503), Wendy S. (703-733- \n" +
//                "6506), and Charles W. (703-733-6473) contributed to this Foreign \n" +
//                "Media Survey. \n" +
//                "ELAG/24MAR/ECONF/WED/CL 24/2227Z MAR");
//
//
//    }

    static AtomicLong sum=new AtomicLong(0);
    public void parse(int size)
    {
        List<Future<cDocument>> futures = new ArrayList<>();
        cDocument document;
        for (int i = 0; i < size; i++) {
            document = DocumentBuffer.getBuffer().poll();
            Future<cDocument> fpd = pool.submit(new Parser(document));
            futures.add(fpd);
        }
//        System.out.println("1112");
        cDocument pd;
        List<cDocument> documents = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                pd = futures.get(i).get();
                documents.add(pd);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("1123");
//        long start = System.currentTimeMillis();
        /*
        HashMap<String,List<Object>> dictionary = new HashMap<>();
        HashMap<String,List<Object>> dictionary_stem = new HashMap<>();
        HashMap<String,List<Object>> dictionary_cities = new HashMap<>();
        Stemmer stemmer = new Stemmer();
        for (cDocument doc : documents) {
            if (doc.city!=null)
            {
                if(!dictionary_cities.containsKey(doc.city))
                    dictionary_cities.put(doc.city,new ArrayList<>());
                dictionary_cities.get(doc.city).add(doc.ID);

            }
            HashMap<String,Integer> ddictionary = new HashMap<>();
            HashMap<String,Integer> ddictionary_stem = new HashMap<>();
            for (String term : doc.terms) {
                if (!ddictionary.containsKey(term))
                    ddictionary.put(term,1);
                else{
                    Integer temp = ddictionary.get(term);
                    ddictionary.put(term,temp+1);
                }
                String sterm = stemmer.stemTerm(term);//maybe StringBuilder or something
                if (!ddictionary_stem.containsKey(sterm))
                    ddictionary_stem.put(sterm,1);
                else{
                    Integer temp = ddictionary_stem.get(sterm);
                    ddictionary_stem.put(sterm,temp+1);
                }
            }
            for (String t : ddictionary.keySet()) {
                if (!dictionary.containsKey(t))
                    dictionary.put(t,new ArrayList<>());
                dictionary.get(t).add(new Pair<>(doc.ID,ddictionary.get(t)));
            }
            for (String t : ddictionary_stem.keySet()) {
                if (!dictionary_stem.containsKey(t))
                    dictionary_stem.put(t,new ArrayList<>());
                dictionary_stem.get(t).add(new Pair<>(doc.ID,ddictionary_stem.get(t)));
            }
        }
//        long end = System.currentTimeMillis();
//        long s = sum.addAndGet(end-start);
//        System.out.println(s);

//        System.out.println("1164");

//        pool.shutdown(); //Todo check when to close
*/
    }
//    public static void parse(String document) {
//        String[] tokens = document.split("\n|\\s+");
//        ArrayList<String> ans = new ArrayList<>();
//        String term = "";
//        for (int i = 0; i < tokens.length; i++) {
//            if (tokens[i].equals(""))
//                continue;
//            tokens[i] = cleanToken(tokens[i]);
//            if (tokens[i].startsWith("$") && isDoubleNumber(tokens[i].replace("\\$", ""))) {
//                String[] splitted = tokens[i].split("((?<=\\$)|(?=\\$))|\\-");
//                if (i + 1 < tokens.length && cleanToken(tokens[i + 1]).matches("miliion|billion|trillion")) {
//                    term = parsePrice(splitted[0], cleanToken(splitted[1]), cleanToken(tokens[++i]));
//                } else
//                    term = parsePrice(splitted[0], cleanToken(splitted[1]));
//
//            } else if (isDoubleNumber(tokens[i]))///check minus number
//            {
//                if (i + 1 < tokens.length && tokens[i + 1].matches("Dollars"))
//                    term = parsePrice(tokens[i], cleanToken(tokens[++i]));
//                else if (i + 1 < tokens.length && isFraction(tokens[i + 1]) && i + 2 < tokens.length && tokens[i + 2].equals("Dollars"))
//                    term = parsePrice(tokens[i], cleanToken(tokens[++i]), cleanToken(tokens[++i]));
//                else if (i + 1 < tokens.length && tokens[i + 1].matches("m|bn") && i + 2 < tokens.length && tokens[i + 2].equals("Dollars"))
//                    term = parsePrice(tokens[i], cleanToken(tokens[++i]), cleanToken(tokens[++i]));
//                else if (i + 3 < tokens.length && tokens[i + 1].matches("miliion|billion|trillion") && tokens[i + 2].equals("U.S") && tokens[i + 3].equals("dollars"))
//                    term = parsePrice(tokens[i], cleanToken(tokens[++i]), cleanToken(tokens[++i]), cleanToken(tokens[++i]));
//                else if (i + 1 < tokens.length && (tokens[i + 1].matches("Thousand|Million|Billion|Trillion") || isFraction(tokens[i + 1])))
//                    term = parseNumber(tokens[i], cleanToken(tokens[++i]));
//                else if (i + 1 < tokens.length && Date.DateToDateNum.containsKey(tokens[i + 1].toUpperCase()))
//                    term = parseDate(tokens[i], cleanToken(tokens[++i]));
//                else
//                    term = parseNumber(tokens[i]);
//            } else if (i + 1 < tokens.length && Date.DateToDateNum.containsKey(tokens[i].toUpperCase())) {
//                if (i + 1 < tokens.length && isIntegernumber(tokens[i + 1]))
//                    term = parseDate(tokens[i], cleanToken(tokens[++i]));
//            } else if (tokens[i].toLowerCase().equals("between") && i + 3 < tokens.length && isDoubleNumber(tokens[i + 1]) && tokens[i + 2].toLowerCase().equals("and") && isDoubleNumber(tokens[i + 3]))
//                term = tokens[i] + tokens[++i] + tokens[++i] + tokens[++i];
//            else
//                term = tokens[i];
//            ans.add(term);
//        }
////        System.out.println(Arrays.toString(ans.toArray()));
//    }

    public static boolean isDoubleNumber(String str) {
        try {
            double number = Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isIntegernumber(String str) {
        try {
            int number = Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFraction(String str) {
        if (str.contains("/")) {
            String[] splitted = str.split("/");
            if (splitted.length == 2 && isDoubleNumber(splitted[0]) && isDoubleNumber(splitted[1]))
                return true;
        }
        return false;
    }

    // how to save long and big number like 10.123000000000034B or 10B
    public static String parseNumber(String... str) {
        String ans = "";
        Double strAsDouble = Double.parseDouble(str[0]);
        int shift = 0;
        if (str.length == 1) {

            String KMB = "";
            if (Math.abs(strAsDouble) >= Math.pow(10, 9)) {
                shift = 9;
                KMB = "B";
            } else if (Math.abs(strAsDouble) >= Math.pow(10, 6)) {
                shift = 6;
                KMB = "M";
            } else if (Math.abs(strAsDouble) >= Math.pow(10, 3)) {
                shift = 3;
                KMB = "K";
            }
            strAsDouble = strAsDouble / Math.pow(10, shift);
            ans = (strAsDouble % 1 == 0.0 ? strAsDouble.intValue() : strAsDouble.toString()) + KMB;
        } else {
            if (str[1].toLowerCase().equals("trillion")) {
                shift = 12;
            } else if (str[1].toLowerCase().equals("billion")) {
                shift = 9;
            } else if (str[1].toLowerCase().equals("million")) {
                shift = 6;
            } else if (str[1].toLowerCase().equals("thousand")) {
                shift = 3;
            } else if (str[1].contains("/")) {
                return str[0] + " " + str[1];
            }
            strAsDouble = strAsDouble * Math.pow(10, shift);
            ans = parseNumber(strAsDouble.toString());
        }
        return ans;
    }

    public String parsePrecent(String... str) {
        if (str[1].equals("%"))
            return str[0] + str[1];
        return str[0] + "%";
    }

    public static String parsePrice(String... str) {
        int shift = 0;
        Double price = 0.0;
        if (str[0].equals("$")) {
            price = Double.parseDouble(str[1]);
            if (str.length == 3) {
                if (str[2].toLowerCase().equals("trillion")) {
                    shift = 12;
                } else if (str[2].toLowerCase().equals("billion")) {
                    shift = 9;
                } else if (str[2].toLowerCase().equals("million")) {
                    shift = 6;
                }
            }
        } else {
            price = Double.parseDouble(str[0]);
            if (str.length >= 3) {
                if (str[1].toLowerCase().equals("trillion")) {
                    shift = 12;
                } else if (str[1].toLowerCase().equals("billion") || str[1].toLowerCase().equals("bn")) {
                    shift = 9;
                } else if (str[1].toLowerCase().equals("million") || str[1].toLowerCase().equals("m")) {
                    shift = 6;
                } else if (str[1].contains("/")) {
                    return str[0] + " " + str[1] + " " + str[2];
                }
            }

        }
        if (price >= Math.pow(10, 6)) {
            shift = 6;
            price /= Math.pow(10, 6);
        }
        price = price * Math.pow(10, shift) / (shift > 0 ? Math.pow(10, 6) : 1);
        return (price % 1 == 0.0 ? Integer.toString(price.intValue()) : price.toString()) + (shift > 0 ? " M " : " ") + "Dollars";
    }

    public static String parseDate(String... str) {
        if (Date.DateToDateNum.containsKey(str[0].toUpperCase())) {
            int dayOrYear = Integer.parseInt(str[1]);
            if (dayOrYear > Date.MonthToNumberOfDays.get(str[0].toUpperCase()))//YYYY-MM
                return str[1] + "-" + Date.DateToDateNum.get(str[0].toUpperCase());
            else//MM-DD
                return Date.DateToDateNum.get(str[0].toUpperCase()) + "-" + (str[1].length() == 1 ? "0" : "") + str[1];
        }
        return Date.DateToDateNum.get(str[1].toUpperCase()) + "-" + str[0];
    }

    public static String cleanToken(String str) {
        str = str.replaceAll("['\"+^:,\t*!\\\\@#=`~;)(?><}{_\\[\\]]", "");
        if (str.endsWith("."))
            str = str.substring(0, str.length() - 1);
        return str;
    }

//    @Override/


}

class Parser implements Callable<cDocument> {
    private cDocument document;

    Parser(cDocument document) {
        this.document = document;
    }

    @Override
    public cDocument call() {
        String[] tokens = document.text.split("\n|\\s+");
        ArrayList<String> ans = new ArrayList<>();
        String term = "";
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals(""))
                continue;
            tokens[i] = Parse.cleanToken(tokens[i]);
            if (tokens[i].startsWith("$") && Parse.isDoubleNumber(tokens[i].replace("\\$", ""))) {
                String[] splitted = tokens[i].split("((?<=\\$)|(?=\\$))|\\-");
                if (i + 1 < tokens.length && Parse.cleanToken(tokens[i + 1]).matches("miliion|billion|trillion")) {
                    term = Parse.parsePrice(splitted[0], Parse.cleanToken(splitted[1]), Parse.cleanToken(tokens[++i]));
                } else
                    term = Parse.parsePrice(splitted[0], Parse.cleanToken(splitted[1]));

            } else if (Parse.isDoubleNumber(tokens[i]))///check minus number
            {
                if (i + 1 < tokens.length && tokens[i + 1].matches("Dollars"))
                    term = Parse.parsePrice(tokens[i], Parse.cleanToken(tokens[++i]));
                else if (i + 1 < tokens.length && Parse.isFraction(tokens[i + 1]) && i + 2 < tokens.length && tokens[i + 2].equals("Dollars"))
                    term = Parse.parsePrice(tokens[i], Parse.cleanToken(tokens[++i]), Parse.cleanToken(tokens[++i]));
                else if (i + 1 < tokens.length && tokens[i + 1].matches("m|bn") && i + 2 < tokens.length && tokens[i + 2].equals("Dollars"))
                    term = Parse.parsePrice(tokens[i], Parse.cleanToken(tokens[++i]), Parse.cleanToken(tokens[++i]));
                else if (i + 3 < tokens.length && tokens[i + 1].matches("miliion|billion|trillion") && tokens[i + 2].equals("U.S") && tokens[i + 3].equals("dollars"))
                    term = Parse.parsePrice(tokens[i], Parse.cleanToken(tokens[++i]), Parse.cleanToken(tokens[++i]), Parse.cleanToken(tokens[++i]));
                else if (i + 1 < tokens.length && (tokens[i + 1].matches("Thousand|Million|Billion|Trillion") || Parse.isFraction(tokens[i + 1])))
                    term = Parse.parseNumber(tokens[i], Parse.cleanToken(tokens[++i]));
                else if (i + 1 < tokens.length && Date.DateToDateNum.containsKey(tokens[i + 1].toUpperCase()))
                    term = Parse.parseDate(tokens[i], Parse.cleanToken(tokens[++i]));
                else
                    term = Parse.parseNumber(tokens[i]);
            } else if (i + 1 < tokens.length && Date.DateToDateNum.containsKey(tokens[i].toUpperCase())) {
                if (i + 1 < tokens.length && Parse.isIntegernumber(tokens[i + 1]))
                    term = Parse.parseDate(tokens[i], Parse.cleanToken(tokens[++i]));
            } else if (tokens[i].toLowerCase().equals("between") && i + 3 < tokens.length && Parse.isDoubleNumber(tokens[i + 1]) && tokens[i + 2].toLowerCase().equals("and") && Parse.isDoubleNumber(tokens[i + 3]))
                term = tokens[i] + tokens[++i] + tokens[++i] + tokens[++i];
            else
                term = tokens[i];
            ans.add(term);
        }
        document.terms=ans;
        return document;
//        System.out.println(Arrays.toString(ans.toArray()));
    }

}