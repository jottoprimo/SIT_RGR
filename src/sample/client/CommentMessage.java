package sample.client;

/**
 * Created by Evgenij on 25.12.2016.
 */
public class CommentMessage implements Message {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    int id;
    String text;
    String from;
    int idNews;
    boolean isDeleted;

    private CommentMessage(){
        isDeleted = false;
    }

    public CommentMessage(int id, boolean isDeleted){
        this.id = id;
        this.isDeleted = isDeleted;
    }

    public CommentMessage(int idNews, String text){
        this();
        this.idNews = idNews;
        this.text = text;
    }

    public CommentMessage(int idNews, String text, String from){
        this();
        this.idNews = idNews;
        this.text = text;
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getIdNews() {
        return idNews;
    }

    public void setIdNews(int idNews) {
        this.idNews = idNews;
    }
}
