package sample;

/**
 * This class us from MVC architecture.
 */
public class Model {

    /**
     * the main object
     */
    ReadFile readFile;

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
}
