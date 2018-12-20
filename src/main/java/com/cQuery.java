package com;

import java.util.HashSet;

public class cQuery extends cItem {
    //    Integer num;
//    String title;
    String description;
    String narrative;
    HashSet<String> cities;

    public cQuery(String ID, String text, HashSet<String> cities) {
        super(ID, text);
        this.cities = (HashSet<String>)cities.clone();
    }
//    terms
}
