package sample;

public class cDocument {
    public String ID;
    public String title;
    public String text;
    public int max_tf;
    public int number_of_unique_terms;
    public String city;

    public cDocument(String ID,String title, String text) {
        this.ID = ID;
        this.title = title;
        this.text = text;
    }
}
