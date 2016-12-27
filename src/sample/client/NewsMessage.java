package sample.client;

/**
 * Created by Evgenij on 25.12.2016.
 */
public class NewsMessage implements Message {
    int id;
    String text;
    String title;
    boolean isDeleted;

    private NewsMessage(){
        isDeleted = false;
    }

    public NewsMessage(int id){
        this();
        this.id = id;
    }

    public NewsMessage(int id, boolean isDeleted){
        this.id = id;
        this.isDeleted = isDeleted;
    }

    public NewsMessage(String title, String text){
        this.id = -1;
        this.isDeleted = false;
        this.text = text;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
