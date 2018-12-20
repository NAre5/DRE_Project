package com;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.jsoup.select.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Searcher {

    private HashMap<String, String[]> termToCloseTerms = new HashMap<>();
    private String postings_dir;
    private HashMap<String, String> documents = new HashMap<>();
    private HashMap<String, String> dictionary = new HashMap<>();

    public Searcher(String postings_dir) {
        this.postings_dir = postings_dir;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(Searcher.class.getResource("termtoterm.properties").toURI())));
            String st;
            while ((st = br.readLine()) != null) {
                int index = st.indexOf('=');
                termToCloseTerms.put(st.substring(0, index), st.substring(index + 2, st.length() - 1).split(", "));//Todo or length -2
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Close.close(br);
        }
        documents = new HashMap<>(MapSaver.loadMap(postings_dir + "documents"));
    }

    public List<String> search(String query, boolean ifStem, boolean ifSemantic, HashSet<String> cities) {
        dictionary = new HashMap<>(MapSaver.loadMap(postings_dir + "dic" + (ifStem ? "stem" : "nostem")));//Todo replace
        cQuery cquery = new cQuery(String.valueOf(Math.random() * 100000), query, cities);//Todo change ID
        cquery = (cQuery) Parse.Parser.parse(cquery, ifStem);

        if (ifSemantic) {
            Set<String> termsCopy = new HashSet<>(cquery.terms.keySet());
            for (String s : termsCopy) {
                if (termToCloseTerms.containsKey(s.toLowerCase())) {
                    for (String s2 : termToCloseTerms.get(s)) {
                        cquery.terms.put(s2, 1);
                    }
                }
            }
        }
        Map<String, Double> rankedDocuments = Ranker.rank(cquery, postings_dir, ifStem, documents, dictionary);
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double>> list = new LinkedList<>(rankedDocuments.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        List<String> temp = new LinkedList<>();
        int i = 50;
        for (Map.Entry<String, Double> aa : list) {
            if (aa.getValue() > 0) {
                temp.add(aa.getKey());
                i--;
            }
            if (i == 0)
                break;
        }
        return temp;
    }


    public static void main(String[] args) {
//        search(Paths.get("C:\\Users\\erant\\Desktop\\STUDIES\\corpus\\queries.txt"), false, false);
    }

    public Map<String, List<String>> search(Path path, boolean ifStem, boolean ifSemantic, HashSet<String> cities) {
        Map<String, List<String>> relevantDocToQuery = new HashMap<>();
        Document document = null;
        try {
            document = Jsoup.parse(new String(Files.readAllBytes(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(document);
        Elements queries = document.getElementsByTag("top");
        for (Element qElement : queries) {
            String qid = qElement.getElementsByTag("num").get(0).childNode(0).toString().trim().split(":")[1];
            String qtitle = qElement.getElementsByTag("title").get(0).text();
            String qdesc = qElement.getElementsByTag("desc").get(0).childNode(0).toString().trim().split(":")[1];
            String qnarr = qElement.getElementsByTag("narr").get(0).text();
            System.out.println();
            cQuery cquery = new cQuery(qid, qtitle, cities);
            cquery.description = qdesc;
            cquery.narrative = qnarr;
            Map<String, Double> rankedDocuments = Ranker.rank(cquery, postings_dir, ifStem, documents, dictionary);
            // Create a list from elements of HashMap
            List<Map.Entry<String, Double>> list = new LinkedList<>(rankedDocuments.entrySet());

            // Sort the list
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                public int compare(Map.Entry<String, Double> o1,
                                   Map.Entry<String, Double> o2) {
                    return (o2.getValue()).compareTo(o1.getValue());
                }
            });
            List<String> temp = new LinkedList<>();
            int i = 50;
            for (Map.Entry<String, Double> aa : list) {
                if (aa.getValue() > 0) {
                    temp.add(aa.getKey());
                    i--;
                }
                if (i == 0)
                    break;
            }
            relevantDocToQuery.put(qid, new LinkedList<>(temp));
        }
        return relevantDocToQuery;
    }
}
