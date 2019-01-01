package com;

import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.jsoup.select.*;

import java.io.*;
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
    HashSet<String> cities = new HashSet<>();
    HashSet<String> languages = new HashSet<>();
    private long sumOfDocLenth = 0;
    private int numOfdoc = 0;
    /**
     * The stopWords
     */
    HashSet<String> stopWords = new HashSet<>();

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
        documents = new HashMap<>(MapSaver.loadMap(postings_dir + "\\documents"));
        for (Map.Entry<String, String> entry : documents.entrySet()) {
            String[] docInfo = entry.getValue().split(";");
            sumOfDocLenth += Long.parseLong(docInfo[2]);
            cities.add(docInfo[4]);
            languages.add(docInfo[5]);
        }
        dictionary = new HashMap<>(MapSaver.loadMap(postings_dir + "\\dic"));//Todo replace
        numOfdoc = documents.size();
        File file = new File(postings_dir + "\\" + "stop_words.txt");
        br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null)
                stopWords.add(st.toLowerCase());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopWords.remove("between");
        stopWords.remove("may");

    }

    public Map<String, List<Pair<String, String[]>>> search(String query, boolean ifStem, boolean ifSemantic, HashSet<String> cities, HashSet<String> languages) {
        cQuery cquery = new cQuery(String.valueOf((int) (Math.random() * 1000)), query, cities, languages);//Todo change ID
        cquery = (cQuery) Parse.Parser.parse(cquery, ifStem, stopWords);
        if (ifSemantic) {
            Set<String> termsCopy = new HashSet<>(cquery.terms.keySet());
            for (String s : termsCopy) {
                if (termToCloseTerms.containsKey(s.toLowerCase())) {
                    for (String s2 : termToCloseTerms.get(s.toLowerCase())) {
                        if (cquery.terms.containsKey(s2.toUpperCase()))
                            cquery.terms.put(s2.toUpperCase(), cquery.terms.get(s2.toUpperCase()) + 1);
                        else if (cquery.terms.containsKey(s2.toLowerCase()))
                            cquery.terms.put(s2.toLowerCase(), cquery.terms.get(s2.toLowerCase()) + 1);
                        else
                            cquery.terms.put(s2.toLowerCase(), cquery.terms.get(s2.toLowerCase()) + 1);
                    }
                }
            }
        }

        Map<String, Double> rankedDocuments = Ranker.rank(cquery, postings_dir, ifStem, documents, dictionary, numOfdoc, sumOfDocLenth);
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double>> list = new LinkedList<>(rankedDocuments.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        List<Pair<String, String[]>> temp = new LinkedList<>();
        int i = 50;
        for (Map.Entry<String, Double> aa : list) {
            if (aa.getValue() > 0) {
                temp.add(new Pair<>(documents.get(aa.getKey()).split(";")[0], getDocumentEntities(aa.getKey())));
                i--;
            } else
                break;
            if (i == 0)
                break;
        }
        Map<String, List<Pair<String, String[]>>> map = new TreeMap<>();
        map.put(cquery.ID, temp);
        return map;
    }

    public Map<String, List<Pair<String, String[]>>> search(Path path, boolean ifStem, boolean ifSemantic, HashSet<String> cities, HashSet<String> languages) {
//        dictionary = new HashMap<>(MapSaver.loadMap(postings_dir + "\\dic" + (ifStem ? "stem" : "nostem")));//Todo replace
        Map<String, List<Pair<String, String[]>>> relevantDocToQuery = new TreeMap<>();
        Document document = null;
        try {
            document = Jsoup.parse(new String(Files.readAllBytes(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(document);
        List<Thread> threads = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        Elements queries = document.getElementsByTag("top");
        for (Element qElement : queries) {

            String qid = qElement.getElementsByTag("num").get(0).childNode(0).toString().trim().split(":")[1];
            String qtitle = qElement.getElementsByTag("title").get(0).text();
            String qdesc = qElement.getElementsByTag("desc").get(0).childNode(0).toString().trim().split(":")[1];
            String qnarr = qElement.getElementsByTag("narr").get(0).text();
//            System.out.println();
            cQuery cquery = new cQuery(qid, qtitle + " " + qdesc, cities, languages);
            cquery.description = qdesc;
            cquery.narrative = qnarr;


            cquery = (cQuery) Parse.Parser.parse(cquery, ifStem, stopWords);
            if (ifSemantic) {
                Set<String> termsCopy = new HashSet<>(cquery.terms.keySet());
                for (String s : termsCopy) {
                    if (termToCloseTerms.containsKey(s.toLowerCase())) {
                        for (String s2 : termToCloseTerms.get(s.toLowerCase())) {
                            if (cquery.terms.containsKey(s2.toUpperCase()))
                                cquery.terms.put(s2.toUpperCase(), cquery.terms.get(s2.toUpperCase()) + 1);
                            else if (cquery.terms.containsKey(s2.toLowerCase()))
                                cquery.terms.put(s2.toLowerCase(), cquery.terms.get(s2.toLowerCase()) + 1);
                            else
                                cquery.terms.put(s2.toLowerCase(), 1);
                        }
                    }
                }
            }
//            Map<String, Double> rankedDocuments = Ranker.rank(cquery, postings_dir + "\\" + (ifStem ? "stem" : "nostem"), ifStem, documents, dictionary, numOfdoc, sumOfDocLenth);
            Map<String, Double> rankedDocuments = Ranker.rank(cquery, postings_dir, ifStem, documents, dictionary, numOfdoc, sumOfDocLenth);
            // Create a list from elements of HashMap
            List<Map.Entry<String, Double>> list = new LinkedList<>(rankedDocuments.entrySet());

            // Sort the list
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                public int compare(Map.Entry<String, Double> o1,
                                   Map.Entry<String, Double> o2) {
                    return (o2.getValue()).compareTo(o1.getValue());
                }
            });
            List<Pair<String, String[]>> temp = new LinkedList<>();
            int i = 50;
            for (Map.Entry<String, Double> aa : list) {
                if (aa.getValue() > 0) {//todo maybe if we arrive to 0 we can finish the for because it sort
                    temp.add(new Pair<>(documents.get(aa.getKey()).split(";")[0], getDocumentEntities(aa.getKey())));
                    i--;
                } else
                    break;
                if (i == 0)
                    break;
            }
            relevantDocToQuery.put(qid, new LinkedList<>(temp));
        }
        return relevantDocToQuery;
    }

    public String[] getDocumentEntities(String doc) {
        String entry = documents.get(doc);
        return entry.substring(entry.lastIndexOf(";") + 1).split("[, ]");
    }
}
