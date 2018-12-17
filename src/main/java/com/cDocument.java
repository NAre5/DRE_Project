package com;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class represent document. here we collect all the data on File
 */
public class cDocument extends cItem{
//    String ID;//DOCNO the id of doc
    public String title;
//    public String text;
    int max_tf;//the terms that appear the most in the file
    public String city;
    public String language;
//    public HashMap<String, Integer> terms = new HashMap<>();
    LinkedHashSet cityPosition = new LinkedHashSet();
    static AtomicLong sumOfDoclenth = new AtomicLong(0);
    static AtomicInteger numOfDoc = new AtomicInteger(0);


    cDocument(String ID, String title, String text) {
        this.ID = ID;
        this.title = title;
        this.text = text;
    }

    /**
     * to do stemming to all of the term
     * @param stemmer
     */
    void stem_dictionary(Stemmer stemmer) {
        HashMap<String, Integer> newTerms = new HashMap<>();
        try {
            for (Map.Entry<String, Integer> entry : terms.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                String term_s = stemmer.stemTerm(key);
                if (!terms.containsKey(term_s))
                    newTerms.put(term_s, terms.get(key));
                else
                    newTerms.put(term_s, terms.get(key) + terms.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        terms = newTerms;
    }
}
