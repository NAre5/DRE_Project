package com;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class us from MVC architecture.
 */
public class Model {

    /**
     * the main object
     */
    ReadFile readFile;
    private Searcher searcher;

    public void startIndexing(String corpusPath, String stopWordsPath, String postingsPath, boolean ifStem) {
        readFile = new ReadFile(corpusPath, stopWordsPath, postingsPath, ifStem);
        readFile.readFiles();
    }

    /**
     * reset all memory and file
     */
    public void reset() {
        readFile.parser.indexer.reset();
    }

    /**
     * @return the computed dictionary
     */
    public Map<String, String> getDictionary() {
        return new TreeMap<>(MapSaver.loadMap(readFile.parser.indexer.d_path + "\\dicTF" + (readFile.parser.ifStem ? "stem" : "nostem")));
    }

    public void initSearch(String postings_dir) {
        searcher = new Searcher(postings_dir);
    }

    public void searchByQuery(String query, boolean ifStem, boolean ifSemantic) {
        List<String> ans = searcher.search(query, ifStem, ifSemantic, new HashSet<>());


    }

    public void searchByQuery_File(Path query, boolean ifStem, boolean ifSemantic) {
        Map<String, List<String>> ans = searcher.search(query, ifStem, ifSemantic, new HashSet<>());

    }


}
