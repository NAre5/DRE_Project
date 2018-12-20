package com;

import java.io.*;
import java.util.*;

public class Ranker {

    public static Map<String,Double> rank(cQuery query, String d_path, boolean ifStem, HashMap<String, String> documents, HashMap<String, String> dictionary) {
        HashMap<String, Double> documentsRank = new HashMap<>();
        HashMap<String, String[]> termToDocTf = new HashMap<>();
        HashMap<Character, HashSet<String>> querytermOfChar = new HashMap<>();
        for (String queryTerm : query.terms.keySet()) {
            Character firstChar = (Character.isLetter(queryTerm.charAt(0)) ? queryTerm.charAt(0) : '_');
            HashSet<String> setOfTerms = querytermOfChar.getOrDefault(firstChar, new LinkedHashSet<>());
            setOfTerms.add(queryTerm);
            querytermOfChar.put(firstChar, setOfTerms);
        }
        for (Character ch : querytermOfChar.keySet()) {
            termToDocTf.putAll(getLinesFromPosting(querytermOfChar.get(ch), ch, d_path, ifStem));
        }
        double avdl = (double) cDocument.sumOfDoclenth.get() / cDocument.numOfDoc.get();
        double logMplus1 = Math.log((cDocument.numOfDoc.get() + 1));
        final double b = 0.75;
        final double k = 1.5;
        final double TITLE = 5;
        for (String queryTerm : query.terms.keySet()) {
            if (!dictionary.containsKey(queryTerm))//TODO check if we need case sensative
                continue;
            String[] docTF = termToDocTf.get(queryTerm);
            for (int i = 1; i < docTF.length; i++) {
                String docID = docTF[i].split(";")[0];
                String[] dataOfDoc = documents.get(docID).split(";");
                if(!query.cities.contains(dataOfDoc[4]))
                    continue;
                String docTitle = dataOfDoc[6];
                int tf = Integer.parseInt(docTF[i].split(";")[1]);
                String nameDoc = dataOfDoc[0].split("=")[1];
                int docLenth = Integer.parseInt(dataOfDoc[2]);
                double numerator = query.terms.get(queryTerm) * ((docTitle.contains(queryTerm.toLowerCase()) || docTitle.contains(queryTerm.toLowerCase().toUpperCase())) ? TITLE : 1) * (k + 1) * tf * (logMplus1 - Math.log(Integer.parseInt(dictionary.get(queryTerm))));
                double denominator = tf + k * (1 - b + b * (docLenth / avdl));
                double bm25TodocAndTerm = numerator / denominator;
                documentsRank.put(nameDoc, documentsRank.getOrDefault(nameDoc, 0.0) + bm25TodocAndTerm);
            }
        }
        return documentsRank;
    }

    public static Map<String, String[]> getLinesFromPosting(HashSet<String> terms, char firstchar, String path, boolean ifStem) {
        Map<String, String[]> linesOfterms = new HashMap<>();
        File file = new File(path + "\\" + firstchar + (ifStem ? "stem" : "nostem"));
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String st;
            while ((st = bufferedReader.readLine()) != null) {
                String term = st.substring(0, st.indexOf("~"));
                if (terms.contains(term)) {
                    linesOfterms.put(term, st.split("\\|"));
                    terms.remove(term);
                }
            }
            return linesOfterms;
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
        return null;
    }


    public static double cosineSimilarity(String vectorA, String vectorB) {
        String[] vectorAsplit = vectorA.split(" ");
        String[] vectorBsplit = vectorB.split(" ");
        double[] vectorAdouble = new double[vectorAsplit.length];
        double[] vectorBdouble = new double[vectorBsplit.length];
        for (int i = 0; i < vectorAsplit.length; i++) {
            vectorAdouble[i] = Double.parseDouble(vectorAsplit[i]);
            vectorBdouble[i] = Double.parseDouble(vectorBsplit[i]);
        }
        return cosineSimilarity(vectorAdouble, vectorBdouble);
    }

    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / ((Math.sqrt(normA) * Math.sqrt(normB)));
    }
}
