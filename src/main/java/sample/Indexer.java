package sample;

//import org.json.JSONObject;

import sun.awt.Mutex;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class responsible to index process.
 * we save list of records and when it is become 1000 we write to posting file.
 * the posting files are splited according to a-z ans else(numbers, date etc.)
 */
public class Indexer {
    String d_path;//directory path
    private ConcurrentHashMap<String, Integer> dictionary = new ConcurrentHashMap<>();//the dictionay. term ->document frequency
    private ConcurrentHashMap<String, Integer> dictionaryTF = new ConcurrentHashMap<>();//the tf dictionay. term ->term frequency
    private ConcurrentHashMap<String, StringBuilder> waitingRecords = new ConcurrentHashMap<>();//to each char it map from the char from the match file
    TreeMap<String, StringBuilder> cities = new TreeMap<>();//[state,currency,population][doc,position]*
    private HashMap<String, File> mapper = new HashMap<>();//map from string to the match file
    private Mutex[] mutexOnFiles;//mutex of every file
    private Mutex[] mutexOnLists;//mutex on evrt list
    AtomicInteger docAndexed = new AtomicInteger(0);//count the number of docs we indexed
    AtomicInteger uniqueTerm = new AtomicInteger(0);//count the number of unique term
    private AtomicInteger lastID = new AtomicInteger(0);
    private StringBuilder documentsList = new StringBuilder();//IDnumber=docID;max_tf;uniqueterms;city;language;title
    public CityAPI api = new CityAPI();
    boolean ifStem;

    /**
     * c'tor
     *
     * @param diretory_name - where we save the posting files
     * @param ifStem        - if stemin. help to save the files with indecation to stem.
     */
    public Indexer(String diretory_name, boolean ifStem) {
        d_path = diretory_name;
        this.ifStem = ifStem;
        boolean b;
        File dir = new File(diretory_name);
        //create all files and connect them with mapper and initialize the fields.
        File file;
        for (char c = 'a'; c <= 'z'; c++) {
            file = new File(dir, c + "" + (ifStem ? "stem" : "nostem"));
            try {
                b = file.createNewFile();//remove b=
            } catch (IOException e) {
                System.out.println("problem create file");
                e.printStackTrace();
            }
            mapper.put(String.valueOf(c), file);
            waitingRecords.put(String.valueOf(c), new StringBuilder());
        }
        file = new File(dir, "_" + (ifStem ? "stem" : "nostem"));
        try {
            b = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapper.put("_", file);
        waitingRecords.put("_", new StringBuilder());

        file = new File(dir, "documents.properties");
        try {
            b = file.createNewFile();//remove b=
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapper.put("documents", file);

        file = new File(dir, "cities");
        try {
            b = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapper.put("cities", file);

        mutexOnFiles = new Mutex[29];//0-26 to a-z and numbers, 27 to documents , 28 to cities
        for (int i1 = 0; i1 < mutexOnFiles.length; i1++) {
            mutexOnFiles[i1] = new Mutex();
        }
        mutexOnLists = new Mutex[29];//0-26 to a-z and numbers, 27 to documents, 28 to cities
        for (int i1 = 0; i1 < mutexOnLists.length; i1++) {
            mutexOnLists[i1] = new Mutex();
        }

    }

    /**
     * we got document.
     * we gave him the next number to his ID we save in posting the numbers to reduce memory
     * from his feild we create the record to the document file
     * after, we pass on each term in his dictionary and create new records and put it in the match list accirding to the term's first letter.
     * after we pass over 1000 document we write all of the list to the files
     *
     * @param document
     */
    public void andex(cDocument document) {
        int docIDnumber = lastID.getAndAdd(1);//IDnumber=docID;max_tf;uniqueterms;city;language;title
        documentsList.append(docIDnumber).append("=").append(document.ID).append(";").append(document.max_tf).append(";").append(document.terms.size())
                .append(";").append(document.city).append(";").append(document.language).append(";").append(document.title).append("\n");


        if (!document.city.equals("")) {
            mutexOnLists[28].lock();
            String docCityInfo = "{" + document.ID + Arrays.toString(document.cityPosition.toArray()) + "}";
            if (cities.containsKey(document.city)) {
                cities.put(document.city, cities.get(document.city).append(docCityInfo));
            } else {
                StringBuilder cityInfo = new StringBuilder(getcityFromAPI(document.city));
                cities.put(document.city, cityInfo.append(docCityInfo));
            }
            mutexOnLists[28].unlock();
        }
        String term = null;
        Iterator<String> iterator = document.terms.keySet().iterator();
        while (iterator.hasNext()) {
            try {
                term = iterator.next();
                Integer tf = document.terms.get(term);
                updateDictionary(term, document.terms.get(term));
                StringBuilder record = new StringBuilder(term).append("~").append(docIDnumber).append(";").append(tf).append("\n");//"term~docIDNumber;tf;
                char first = term.toString().toLowerCase().substring(0, 1).charAt(0);
                char key = (mapper.containsKey(String.valueOf(first))) ? first : '_';
                int index = Character.toLowerCase(key) - 'a';
                if (index < 0 || index > 25)
                    index = 26;
                mutexOnLists[index].lock();
                waitingRecords.get(String.valueOf(key)).append(record.toString());
                mutexOnLists[index].unlock();
            } catch (Exception ignore) {
            }
        }
        document = null;
//        docAndexed.getAndAdd(1);
        if (docAndexed.addAndGet(1) % 1000 == 0) {//after 1000 doc we write
            writeWaitingToPosting();
        }
    }

    /**
     * This function update the dictionaries.
     * to df dict we add 1 to the term entry
     * to tf dict we add tf to term entry
     *
     * @param term1 - the term we update is entry in dicts
     * @param tf    - the tf we need to add to the entry
     */
    private void updateDictionary(String term1, Integer tf) {
        String term = term1;
        if (Character.isLowerCase(term.charAt(0))) {
            Integer df;
            term = term.toLowerCase();
            if ((df = dictionary.remove(term.toUpperCase())) != null) {
                dictionary.put(term, df);
                dictionaryTF.put(term, dictionaryTF.remove(term.toUpperCase()));
            }
        } else {
            if (dictionary.containsKey(term.toLowerCase())) {
                term = term.toLowerCase();
            } else
                term = term.toUpperCase();
        }

        dictionary.put(term, dictionary.getOrDefault(term, 0) + 1);
        dictionaryTF.put(term, dictionaryTF.getOrDefault(term, 0) + tf);
    }

    /**
     * we creat thread for each posting file.
     * the thread get the mutexes to the file and list.
     * In the end we wait to all threads to end.
     */
    private void writeWaitingToPosting() {
        Thread[] threads = new Thread[28];//todo 29 to cities
        char c = 'a';
        for (int i = 0; i < threads.length - 2; i++) {
            threads[i] = new Thread(new WriterThread(mutexOnFiles[i], mutexOnLists[i], mapper.get(String.valueOf(c)), waitingRecords.get(String.valueOf(c))));
            threads[i].start();
            c++;
        }
        threads[26] = new Thread(new WriterThread(mutexOnFiles[26], mutexOnLists[26], mapper.get("_"), waitingRecords.get("_")));
        threads[26].start();
        threads[27] = new Thread(new WriterThread(mutexOnFiles[27], mutexOnLists[27], mapper.get("documents"), documentsList));
        threads[27].start();
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * This function called when we pass all the corpus and now we want to write the records that nor been writer yet.
     * In addition we write all the city we found to cities file
     */
    public void writeRestRecords() {
        writeWaitingToPosting();
        //write to cities//
        StringBuilder citiesText = new StringBuilder();
        for (Map.Entry<String, StringBuilder> sb : cities.entrySet())
            citiesText.append(sb.getKey() + "-->").append(sb.getValue()).append("\n");
        Thread thread = new Thread(new WriterThread(mutexOnFiles[28], mutexOnLists[28], mapper.get("cities"), citiesText));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * This function use API to get information about city. The API url is https://restcountries.eu
     * The API get a capital city and give information. we wnat the state, currency and population information. if the city is not capital we return string that symbol "no information.
     *
     * @param city - The city of some document we want to add to our index. If it capital we return informative data.
     * @return the information of the city if exist
     */
    private String getcityFromAPI(String city) {
        return api.getCityInfo(city);
    }

    public void setDictionary(Map<String, Integer> map) {
        dictionary = (ConcurrentHashMap<String, Integer>) map;
    }

    public void setDictionaryTF(Map<String, Integer> map) {
        dictionaryTF = (ConcurrentHashMap<String, Integer>) map;
    }

    /**
     * In the end of the index process we sort the files in order to make the answer on query in fast way.
     * before we save the dictionary to reduce in RAM
     * like the thread writer the thread eot the file to sort
     * we sort just 2 files in parallel on order to keep the RAM safe from out of memory.
     */
    public void sortFiles() {
        uniqueTerm.set(dictionary.size());//to the alert after index end
        //save maps
        System.out.println(dictionary.size() + "-" + dictionaryTF.size());
        MapSaver.saveMap(new TreeMap<String, Integer>(dictionary), d_path + "\\dic"+(ifStem ? "stem" : "nostem"));
        dictionary.clear();
        MapSaver.saveMap(new TreeMap<String, Integer>(dictionaryTF), d_path + "\\dicTF"+(ifStem ? "stem" : "nostem"));
        dictionaryTF.clear();
        Thread[] threads = new Thread[27];
        char start = 'a';
        char end = 'z';
        int begin = 0;
        while ('z' >= start) {
            threads[begin] = new Thread(new SortThread(mapper.get(String.valueOf(start))));
//            threads[25 - begin] = new Thread(new SortThread(mapper.get(String.valueOf(end))));
            threads[begin].start();
//            threads[25 - begin].start();
            try {
                threads[begin].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            try {
//                threads[25 - begin].join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            start++;
//            end--;
            begin++;
        }
        threads[26] = new Thread(new SortThread(mapper.get(String.valueOf("_"))));
        threads[26].start();
        try {
            threads[26].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


//    static byte[] intToBytes(int number, int num_of_bytes) {
//        byte[] ans = new byte[num_of_bytes];
//        for (int i = num_of_bytes; i > 0; i--) {
//            ans[num_of_bytes - i] = (byte) (number >> ((i - 1) * 8));
////            number >>= 8;
//        }
//        return ans;
//    }

    /**
     * This function delete all the files that has been created during the process
     * and clear all the memory
     */
    public void reset() {
        {
            File file = new File(d_path);
            File[] list_file = file.listFiles();
            for (File file1 : list_file) {
                File filed = new File(String.valueOf(file1.toPath()));
                boolean b = filed.delete();
                System.out.println();
            }
            dictionary.clear();
            dictionaryTF.clear();
            docAndexed.set(0);
            uniqueTerm.set(0);
            lastID.set(0);

        }
    }
}

/**
 * This thread is responsible to write to file.
 * it write the listToWrite to file
 */
class WriterThread implements Runnable {
    private Mutex mutexOnFile;
    private Mutex mutexOnList;
    private File file;
    private StringBuilder listToWrite;

    /**
     * c'tor
     *
     * @param mutexOnFile - make sure there are not 2 thread that write together to file
     * @param mutexOnList - make sure there are not 2 thread that chanfe tje list
     * @param file        - the file to write into
     * @param listToWrite - the list to write into file
     */
    WriterThread(Mutex mutexOnFile, Mutex mutexOnList, File file, StringBuilder listToWrite) {
        this.mutexOnFile = mutexOnFile;
        this.mutexOnList = mutexOnList;
        this.file = file;
        this.listToWrite = listToWrite;
    }


    /**
     * lock the mutexes and write the list
     * we write the string once to reduce IO
     */
    @Override
    public void run() {
        mutexOnList.lock();
        mutexOnFile.lock();
        BufferedWriter write = null;
        try {
            write = new BufferedWriter(new FileWriter(file, true));
            write.write(listToWrite.toString());
            write.flush();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (write != null)
                try {
                    write.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        listToWrite.setLength(0);//clear the list
        mutexOnFile.unlock();
        mutexOnList.unlock();
    }
}

/**
 * This thread responsible to sort file with ognore case sensetive
 */
class SortThread implements Runnable {
    //the file to sort
    private File file;
    //all the words that start at big char
    private TreeMap<String, String> words;
    //all the words that start at small char
    private TreeMap<String, String> smallWords;
    private LinkedHashMap<String, String> sortedFile;//key: term~docID  value: ;tf

    SortThread(File file) {
        this.file = file;
        words = new TreeMap<>();
        smallWords = new TreeMap<>();
        sortedFile = new LinkedHashMap<>();
    }

    public void run() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            //put each word in match map
//            while ((st = br.readLine()) != null) {
//                if (Character.isLowerCase(st.charAt(0))) {
//                    smallWords.put(st.substring(0, st.indexOf(';')), st.substring(st.indexOf(';')));
//                } else
//                    bigWords.put(st.substring(0, st.indexOf(';')), st.substring(st.indexOf(';')));
//            }
//            br.close();
//            Iterator<Map.Entry<String, String>> bigIterator = bigWords.entrySet().iterator();
//            Iterator<Map.Entry<String, String>> smallIterator = smallWords.entrySet().iterator();
//            String termBigSmall = "";//help to know if we were in case that we have small and big word equals
//            if (smallIterator.hasNext()) {
//                Map.Entry<String, String> smallEntry = smallIterator.next();
//                Map.Entry<String, String> bigEntry = null;
//                try {
//                    bigEntry = bigIterator.next();
//                }
//                catch (NoSuchElementException ignore){}
//                do {//while we have word in both map we need to compare between them
//                    if (smallEntry == null || bigEntry == null)
//                        break;
//                    String smallKey = smallEntry.getKey();
//                    String bigKey = bigEntry.getKey();
//                    //if the word in each map are equals we need to save the big word as lower
//                    while (smallKey.substring(0, smallKey.indexOf('~')).toUpperCase().equals(bigKey.substring(0, bigKey.indexOf('~')).toUpperCase())) {
//                        termBigSmall = smallKey;//todo check if to do toLower
//                        sortedFile.put(smallKey, smallEntry.getValue());
//                        sortedFile.put(bigKey.toLowerCase(), bigEntry.getValue());
//                        smallEntry = smallIterator.next();
//                        bigEntry = bigIterator.next();
//                        smallKey = smallEntry.getKey();
//                        bigKey = bigEntry.getKey();
//                    }
//                    if (smallKey.substring(0, smallKey.indexOf('~')).toUpperCase().compareTo(bigKey.substring(0, bigKey.indexOf('~'))) < 0) {//if the small word befor the big lexicographi save the small
//                        sortedFile.put(smallKey, smallEntry.getValue());
//                        try {
//
//                            smallEntry = smallIterator.next();
//                        }
//                        catch (NoSuchElementException ignore){}
//                    } else {//if the small word befor the big lexicographi save the big
//                        if (!termBigSmall.equals("")) {//if we were in problem with big and small write the big because it was//todo לבדוק שהמילה מופיעה גם אחר כך
//                            termBigSmall = "";
//                            sortedFile.put(bigKey.toLowerCase(), bigEntry.getValue());
//                        } else {
//                            sortedFile.put(bigKey, bigEntry.getValue());
//                        }
//                        try {
//                            bigEntry = bigIterator.next();
//                        }catch (NoSuchElementException ignore){}
//                    }
//
//                } while (bigIterator.hasNext() && smallIterator.hasNext());
//                if (!smallIterator.hasNext()) {//we write all tof the big words
//                    if (bigEntry != null) {
//                        sortedFile.put(bigEntry.getKey(), bigEntry.getValue());
//                    }
//                    while (bigIterator.hasNext()) {
//                        bigEntry = bigIterator.next();
//                        sortedFile.put(bigEntry.getKey(), bigEntry.getValue());
//                    }
//                } else if (!bigIterator.hasNext()) {//we write all tof the small words
//                    if (smallEntry != null) {
//                        sortedFile.put(smallEntry.getKey(), smallEntry.getValue());
//                    }
//                    while (smallIterator.hasNext()) {
//                        smallEntry = smallIterator.next();
//                        sortedFile.put(smallEntry.getKey(), smallEntry.getValue());
//                    }
//                }
//            } else {//special check for '_' doc(number etc.)
//                try {
//                    Map.Entry<String, String> bigEntry = bigIterator.next();
//                    if (!smallIterator.hasNext()) {
//                        if (bigEntry != null) {
//                            sortedFile.put(bigEntry.getKey(), bigEntry.getValue());
//                        }
//                        while (bigIterator.hasNext()) {
//                            bigEntry = bigIterator.next();
//                            sortedFile.put(bigEntry.getKey(), bigEntry.getValue());
//                        }
//                    }
//                }catch (Exception ignore){}
//            }
//            FileWriter fileWriter = new FileWriter(file, false);
//            fileWriter.write("");
//            fileWriter.close();
//            StringBuilder stringBuilder = new StringBuilder();
//            for (Map.Entry<String, String> entry : sortedFile.entrySet()) {//create the string to write
//                stringBuilder.append(entry.getKey()).append(entry.getValue()).append("\n");
//            }
//            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
//            bufferedWriter.write(stringBuilder.toString());//write all together to reduce IO
//            bufferedWriter.flush();
//            bufferedWriter.close();
            while ((st = br.readLine()) != null) {
                int index = st.indexOf(';');
                words.put(st.substring(0, index), st.substring(index));
            }
            br.close();
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write("");
            fileWriter.close();
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : words.entrySet()) {
                sb.append(entry.getKey()).append(entry.getValue()).append('\n');
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.write(sb.toString());//write all together to reduce IO
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}