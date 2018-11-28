package sample;

import java.util.HashMap;
import java.util.List;

public class cDocument {
    public String ID;
    public String title;
    public String text;
    public int max_tf;
//    public int number_of_unique_terms;
    public String city;
    public String language;
    public HashMap<String,Integer> terms = new HashMap<>();
    public HashMap<String,Integer> terms_s = new HashMap<>();


    public cDocument(String ID, String title, String text) {
        this.ID = ID;
        this.title = title;
        this.text = text;
    }

    public void stem_dictionary(Stemmer stemmer)
    {
        for (String term : terms.keySet()) {
            String term_s = stemmer.stemTerm(term);
            if (!terms_s.containsKey(term_s))
                terms_s.put(term_s,terms.get(term));
            else
                terms_s.put(term_s,terms.get(term)+terms_s.get(term_s));
        }
    }
}
