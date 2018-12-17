package com;

import java.util.HashMap;

public abstract class cItem {
    String ID;//DOCNO the id of doc
    public String text;
    public String city;
    public HashMap<String, Integer> terms = new HashMap<>();
}
