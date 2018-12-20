package com;

import java.util.HashMap;

public abstract class cItem {
    String ID;//DOCNO the id of doc
    public String text;

    public HashMap<String, Integer> terms = new HashMap<>();

    public cItem(String ID, String text) {
        this.ID = ID;
        this.text = text;

    }
}
