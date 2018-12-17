package com;

import java.util.List;

public class Searcher {

    public static List<String> search(cQuery query, String d_path, boolean ifStem) {
        query = (cQuery) Parse.Parser.parse(query, ifStem);
        return Ranker.rank(query, d_path, ifStem);
    }
}
