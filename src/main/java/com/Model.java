package com;

import javafx.util.Pair;

import java.io.*;
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
    Searcher searcher;

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
        return new TreeMap<>(MapSaver.loadMap(readFile.parser.indexer.d_path + "\\dicTF"));
    }

    public void initSearch(String postings_dir) {
        searcher = new Searcher(postings_dir);
    }

    public void searchByQuery(String query, boolean ifStem, boolean ifSemantic,HashSet<String> cities,HashSet<String> languages) {
        Map<String, List<Pair<String, String[]>>> ans = searcher.search(query, ifStem, ifSemantic, cities,languages);
//        saveQueryOutput(ans);
    }

    public void saveQueryOutput(Map<String, List<Pair<String, String[]>>> ans, File file) {
        System.out.println("Saving query");
//        File file = new File("C:\\Users\\micha\\OneDrive\\מסמכים\\michael\\שנה ג\\אחזור מידע\\query1.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new FileWriter(file, true));
            for (Map.Entry<String, List<Pair<String, String[]>>> entry : ans.entrySet()) {
                System.out.println("saving "+entry.getKey());
                for (Pair<String, String[]> doc : entry.getValue()) {
                    try {
                        br.write(entry.getKey() + " " + "0 " + doc.getKey() + " 1 42.38 mt\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            br.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(br!=null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public Map<String, List<Pair<String, String[]>>> searchByQuery_File(Path query, boolean ifStem, boolean ifSemantic,HashSet<String> cities,HashSet<String> languages) {
        Map<String, List<Pair<String, String[]>>> ans = searcher.search(query, ifStem, ifSemantic, cities,languages);
//        saveQueryOutput(ans);
        return ans;
    }

}
