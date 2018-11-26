package sample;

import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Parse {
    ExecutorService pool = Executors.newCachedThreadPool();
    static HashSet<String> stopWords = new HashSet<>();
    static {
        File file = new File("C:\\Users\\micha\\OneDrive\\מסמכים\\intelliJ\\DRE_Project\\target\\classes\\stop_words.txt");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null)
                stopWords.add(st);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static AtomicLong sum = new AtomicLong(0);

    public void parse(cDocument[] docs) {
        List<Future<cDocument>> futures = new ArrayList<>();
        cDocument document;
        for (int i = 0; i < docs.length; i++) {
            document = docs[i];
            Future<cDocument> fpd = pool.submit(new Parser(document));
            futures.add(fpd);
        }
        cDocument pd;
        List<cDocument> documents = new ArrayList<>();
        for (int i = 0; i < docs.length; i++) {
            try {
                pd = futures.get(i).get();
                documents.add(pd);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        long start = System.currentTimeMillis();

        HashMap<String,List<Object>> dictionary = new HashMap<>();
        HashMap<String,List<Object>> dictionary_stem = new HashMap<>();
        HashMap<String,List<Object>> dictionary_cities = new HashMap<>();
        Stemmer stemmer = new Stemmer();
        for (cDocument doc : documents) {
            if (doc.city!=null)
            {
                if(!dictionary_cities.containsKey(doc.city))
                    dictionary_cities.put(doc.city,new ArrayList<>());
                dictionary_cities.get(doc.city).add(doc.ID);

            }
//            HashMap<String,Integer> ddictionary = new HashMap<>();
//            HashMap<String,Integer> ddictionary_stem = new HashMap<>();
//            for (String term : doc.terms) {
//                if (!ddictionary.containsKey(term))
//                    ddictionary.put(term,1);
//                else{
//                    Integer temp = ddictionary.get(term);
//                    ddictionary.put(term,temp+1);
//                }
//                String sterm = stemmer.stemTerm(term);//maybe StringBuilder or something
//                if (!ddictionary_stem.containsKey(sterm))
//                    ddictionary_stem.put(sterm,1);
//                else{
//                    Integer temp = ddictionary_stem.get(sterm);
//                    ddictionary_stem.put(sterm,temp+1);
//                }
//            }
//            for (String t : ddictionary.keySet()) {
//                if (!dictionary.containsKey(t))
//                    dictionary.put(t,new ArrayList<>());
//                dictionary.get(t).add(new Pair<>(doc.ID,ddictionary.get(t)));
//            }
//            for (String t : ddictionary_stem.keySet()) {
//                if (!dictionary_stem.containsKey(t))
//                    dictionary_stem.put(t,new ArrayList<>());
//                dictionary_stem.get(t).add(new Pair<>(doc.ID,ddictionary_stem.get(t)));
//            }
        }
//        long end = System.currentTimeMillis();
//        long s = sum.addAndGet(end-start);
//        System.out.println(s);

//        System.out.println("1164");

//        pool.shutdown(); //Todo check when to close

    }

    public static boolean isDoubleNumber(String str) {
        try {
            double number = Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isIntegernumber(String str) {
        try {
            int number = Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFraction(String str) {
        if (str.contains("/")) {
            String[] splitted = str.split("/");
            if (splitted.length == 2 && isDoubleNumber(splitted[0]) && isDoubleNumber(splitted[1]))
                return true;
        }
        return false;
    }

    // how to save long and big number like 10.123000000000034B or 10B
    public static String parseNumber(String... str) {
        String ans = "";
        Double strAsDouble = Double.parseDouble(str[0]);
        int shift = 0;
        if (str.length == 1) {

            String KMB = "";
            if (Math.abs(strAsDouble) >= Math.pow(10, 9)) {
                shift = 9;
                KMB = "B";
            } else if (Math.abs(strAsDouble) >= Math.pow(10, 6)) {
                shift = 6;
                KMB = "M";
            } else if (Math.abs(strAsDouble) >= Math.pow(10, 3)) {
                shift = 3;
                KMB = "K";
            }
            strAsDouble = strAsDouble / Math.pow(10, shift);
            ans = (strAsDouble % 1 == 0.0 ? strAsDouble.intValue() : strAsDouble.toString()) + KMB;
        } else {
            if (str[1].toLowerCase().equals("trillion")) {
                shift = 12;
            } else if (str[1].toLowerCase().equals("billion")) {
                shift = 9;
            } else if (str[1].toLowerCase().equals("million")) {
                shift = 6;
            } else if (str[1].toLowerCase().equals("thousand")) {
                shift = 3;
            } else if (str[1].contains("/")) {
                return str[0] + " " + str[1];
            }
            strAsDouble = strAsDouble * Math.pow(10, shift);
            ans = parseNumber(strAsDouble.toString());
        }
        return ans;
    }

    public static String parsePrecent(String... str) {
        if (str[1].equals("%"))
            return str[0] + str[1];
        return str[0] + "%";
    }

    public static String parsePrice(String... str) {
        int shift = 0;
        Double price = 0.0;
        if (str[0].equals("$")) {
            price = Double.parseDouble(str[1]);
            if (str.length == 3) {
                if (str[2].toLowerCase().equals("trillion")) {
                    shift = 12;
                } else if (str[2].toLowerCase().equals("billion")) {
                    shift = 9;
                } else if (str[2].toLowerCase().equals("million")) {
                    shift = 6;
                }
            }
        } else {
            price = Double.parseDouble(str[0]);
            if (str.length >= 3) {
                if (str[1].toLowerCase().equals("trillion")) {
                    shift = 12;
                } else if (str[1].toLowerCase().equals("billion") || str[1].toLowerCase().equals("bn")) {
                    shift = 9;
                } else if (str[1].toLowerCase().equals("million") || str[1].toLowerCase().equals("m")) {
                    shift = 6;
                } else if (str[1].contains("/")) {
                    return str[0] + " " + str[1] + " " + str[2];
                }
            }

        }
        if (price >= Math.pow(10, 6)) {
            shift = 6;
            price /= Math.pow(10, 6);
        }
        price = price * Math.pow(10, shift) / (shift > 0 ? Math.pow(10, 6) : 1);
        return (price % 1 == 0.0 ? Integer.toString(price.intValue()) : price.toString()) + (shift > 0 ? " M " : " ") + "Dollars";
    }

    public static String parseDate(String... str) {
        if (Date.DateToDateNum.containsKey(str[0].toUpperCase())) {
            int dayOrYear = Integer.parseInt(str[1]);
            if (dayOrYear > Date.MonthToNumberOfDays.get(str[0].toUpperCase()))//YYYY-MM
                return str[1] + "-" + Date.DateToDateNum.get(str[0].toUpperCase());
            else//MM-DD
                return Date.DateToDateNum.get(str[0].toUpperCase()) + "-" + (str[1].length() == 1 ? "0" : "") + str[1];
        }
        return Date.DateToDateNum.get(str[1].toUpperCase()) + "-" + str[0];
    }

    public static String cleanToken(String str) {
        str = str.replaceAll("['\"+^:,\t*!\\\\@#=`~;)(?><}{_\\[\\]]", "");
        if (str.endsWith("."))
            str = str.substring(0, str.length() - 1);
        return str;
    }

//    @Override/


}

class Parser implements Callable<cDocument> {
    private cDocument document;

    Parser(cDocument document) {
        this.document = document;
    }

    @Override
    public cDocument call() {
        String[] tokens = document.text.replaceAll("[.,][ \n\t\"]"," ").replace("[)(,]","").split("\n|\\s+");
//        ArrayList<String> ans = new ArrayList<>();
        String term = "";
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals(""))
                continue;
            tokens[i] = Parse.cleanToken(tokens[i]);
            if (tokens[i].startsWith("$") && Parse.isDoubleNumber(tokens[i].replace("\\$", ""))) {
                String[] splitted = tokens[i].split("((?<=\\$)|(?=\\$))|\\-");
                if (i + 1 < tokens.length && (tokens[i + 1]).matches("miliion|billion|trillion")) {
                    term = Parse.parsePrice(splitted[0], (splitted[1]), (tokens[++i]));
                } else
                    term = Parse.parsePrice(splitted[0], (splitted[1]));

            }
            else if (Parse.isDoubleNumber(tokens[i]))///check minus number
            {
                if (i + 1 < tokens.length && tokens[i + 1].matches("Dollars"))
                    term = Parse.parsePrice(tokens[i], (tokens[++i]));
                else if (i + 1 < tokens.length && Parse.isFraction(tokens[i + 1]) && i + 2 < tokens.length && tokens[i + 2].equals("Dollars"))
                    term = Parse.parsePrice(tokens[i], (tokens[++i]), (tokens[++i]));
                else if (i + 1 < tokens.length && tokens[i + 1].matches("m|bn") && i + 2 < tokens.length && tokens[i + 2].equals("Dollars"))
                    term = Parse.parsePrice(tokens[i], (tokens[++i]), (tokens[++i]));
                else if (i + 3 < tokens.length && tokens[i + 1].matches("miliion|billion|trillion") && tokens[i + 2].equals("U.S") && tokens[i + 3].equals("dollars"))
                    term = Parse.parsePrice(tokens[i], (tokens[++i]), (tokens[++i]), (tokens[++i]));
                else if (i + 1 < tokens.length && (tokens[i + 1].matches("Thousand|Million|Billion|Trillion") || Parse.isFraction(tokens[i + 1])))
                    term = Parse.parseNumber(tokens[i], (tokens[++i]));
                else if (i + 1 < tokens.length && Date.DateToDateNum.containsKey(tokens[i + 1].toUpperCase()))
                    term = Parse.parseDate(tokens[i], (tokens[++i]));
                else
                    term = Parse.parseNumber(tokens[i]);
            } else if (i + 1 < tokens.length && Date.DateToDateNum.containsKey(tokens[i].toUpperCase())) {
                if (i + 1 < tokens.length && Parse.isIntegernumber(tokens[i + 1]))
                    term = Parse.parseDate(tokens[i], (tokens[++i]));
            } else if (tokens[i].toLowerCase().equals("between") && i + 3 < tokens.length && Parse.isDoubleNumber(tokens[i + 1]) && tokens[i + 2].toLowerCase().equals("and") && Parse.isDoubleNumber(tokens[i + 3]))
                term = tokens[i] + tokens[++i] + tokens[++i] + tokens[++i];
            else
                term = tokens[i];
//            ans.add(term);
            if (!document.terms.containsKey(term))
                document.terms.put(term, 1);
            else {
                document.terms.put(term, document.terms.get(term)+1);
            }
        }
        return document;
//        System.out.println(Arrays.toString(ans.toArray()));
    }

}