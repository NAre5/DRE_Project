package com;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Ranker {

    public static Map<String, Double> rank(cQuery query, String d_path, boolean ifStem, HashMap<String, String> documents, HashMap<String, String> dictionary, int numOfdoc, long sumOfDocLenth) {
        ExecutorService reader_pool = Executors.newCachedThreadPool();//Todo change to limit (8) threads?
        HashMap<String, Double> documentsRank = new HashMap<>();
        HashMap<String, String[]> termToDocTf = new HashMap<>();
        HashMap<Character, HashSet<String>> querytermOfChar = new HashMap<>();
        for (String queryTerm : query.terms.keySet()) {
            if (queryTerm.equals(""))
                continue;//divide the term in the query to set of each char to make the search in one time
            Character firstChar = (Character.isLetter(queryTerm.charAt(0)) ? queryTerm.charAt(0) : '_');
            HashSet<String> setOfTerms = querytermOfChar.getOrDefault(firstChar, new LinkedHashSet<>());
            setOfTerms.add(queryTerm.toLowerCase());
            setOfTerms.add(queryTerm.toUpperCase());
            querytermOfChar.put(firstChar, setOfTerms);
        }
        List<Future<Map<String, String[]>>> futuresTerms = new LinkedList<>();
        List<Future<Map<String, String[]>>> futuresCities = new LinkedList<>();

        for (Character ch : querytermOfChar.keySet()) {//start to search for lines of each term in the query
//            termToDocTf.putAll(getLinesFromPosting(querytermOfChar.get(ch), ch, d_path, ifStem));
            Future<Map<String, String[]>> future = reader_pool.submit(new ReadThread(querytermOfChar.get(ch), ch, d_path, ifStem));
            futuresTerms.add(future);
        }

        HashSet<String> documentsWithCities = new LinkedHashSet<>();
        HashMap<Character, HashSet<String>> citytermOfChar = new HashMap<>();

        //Todo maybe because there arnt much cities unnecessary
        for (String city : query.cities) {//divide the cities to set of every char to make the search in one time
            Character firstChar = (Character.isLetter(city.charAt(0)) ? Character.toLowerCase(city.charAt(0)) : '_');
            HashSet<String> setOfcities = citytermOfChar.getOrDefault(firstChar, new LinkedHashSet<>());
            setOfcities.add(city);
            setOfcities.add(city.toLowerCase());
            citytermOfChar.put(firstChar, setOfcities);
        }

        for (Character ch : citytermOfChar.keySet()) {//start the search of every set of cities.
            Future<Map<String, String[]>> future = reader_pool.submit(new ReadThread(citytermOfChar.get(ch), ch, d_path, ifStem));
            futuresCities.add(future);
//            Map<String, String[]> citiesOfChar = getLinesFromPosting(citytermOfChar.get(ch), ch, d_path, ifStem);
//            for (Map.Entry<String, String[]> entry : citiesOfChar.entrySet()) {
//                for (int i = 1; i < entry.getValue().length; i++) {
//                    documentsWithCities.add(entry.getValue()[i].split(";")[0]);
//                }
//            }
        }

        for (Future<Map<String, String[]>> future : futuresTerms) {//collect the line of the terms
            try {
                termToDocTf.putAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }


        for (Future<Map<String, String[]>> future : futuresCities) {//collect the lines of the cities
            Map<String, String[]> citiesOfChar = null;
            try {
                citiesOfChar = future.get();
                for (Map.Entry<String, String[]> entry : citiesOfChar.entrySet()) {
                    for (int i = 1; i < entry.getValue().length; i++) {
                        documentsWithCities.add(entry.getValue()[i].split(";")[0]);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        reader_pool.shutdown();
        double avdl = (double) sumOfDocLenth / numOfdoc;
        double logMplus1 = Math.log(numOfdoc + 1);
        final double b = 0.5;
        final double k = 1.4;
        final double TITLE = 5;
        for (String queryTerm : query.terms.keySet()) {
            if (!dictionary.containsKey(queryTerm))//TODO check if we need case sensitive
                continue;
            String[] docTF = termToDocTf.get(queryTerm);
            for (int i = 1; i < docTF.length; i++) {
                String docID = docTF[i].split(";")[0];
                String[] dataOfDoc = documents.get(docID).split(";");
                if (!(query.cities.isEmpty() || (query.cities.contains(dataOfDoc[4]) || documentsWithCities.contains(docID))))
                    continue;
                if(!(query.languages.isEmpty()||query.languages.contains(dataOfDoc[5])))
                    continue;
                String docTitle = "";//TODo after new indexing not need try and catch
                docTitle = dataOfDoc[6];
                if (docTitle.equals(" "))
                    docTitle = "";
                int tf = Integer.parseInt(docTF[i].split(";")[1]);
                String docName = dataOfDoc[0];
                int docLenth = Integer.parseInt(dataOfDoc[2]);
//                if (docTitle.contains(queryTerm.toUpperCase()))
//                    System.out.println();
                double numerator = query.terms.get(queryTerm) * ((docTitle.contains(queryTerm.toUpperCase()) || docTitle.contains(queryTerm.toLowerCase().toUpperCase())) ? TITLE : 1) * (k + 1) * tf * (logMplus1 - Math.log(Integer.parseInt(dictionary.get(queryTerm))));
                double denominator = tf + k * (1 - b + b * (docLenth / avdl));
                double bm25TodocAndTerm = numerator / denominator;
                documentsRank.put(docID, documentsRank.getOrDefault(docID, 0.0) + bm25TodocAndTerm);
            }
        }
        return documentsRank;
    }


    public static String[] getDocumentEnteties(String docName) {

        return null;
    }
}

class ReadThread implements Callable<Map<String, String[]>> {
    HashSet<String> terms;
    char firstchar;
    String path;
    boolean ifStem;

    public ReadThread(HashSet<String> terms, char firstchar, String path, boolean ifStem) {
        this.terms = terms;
        this.firstchar = firstchar;
        this.path = path;
        this.ifStem = ifStem;
    }

    @Override
    public Map<String, String[]> call() {
        Map<String, String[]> linesOfterms = new HashMap<>();
        File file = new File(path + "\\" + firstchar);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String st;
            while ((st = bufferedReader.readLine()) != null && !terms.isEmpty()) {
                String term = st.substring(0, st.indexOf("~"));
                if (terms.contains(term)) {
                    linesOfterms.put(term, st.split("\\|"));
                    terms.remove(term.toLowerCase());
                    terms.remove(term.toUpperCase());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return linesOfterms;
    }
}