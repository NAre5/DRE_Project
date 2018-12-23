package com;

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
    Searcher searcher;

    public void startIndexing(String corpusPath, String stopWordsPath,String postingsPath, boolean ifStem)
    {
        readFile = new ReadFile(corpusPath, stopWordsPath,postingsPath,ifStem);
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
    public Map<String,String> getDictionary(){
        return new TreeMap<>(MapSaver.loadMap(readFile.parser.indexer.d_path + "\\dicTF" + (readFile.parser.ifStem ? "stem" : "nostem")));
    }

    public void initSearch(String postings_dir) {
        searcher = new Searcher(postings_dir);
    }


}
