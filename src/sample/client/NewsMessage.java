package sample.client;

import java.util.ArrayList;

/**
 * Created by Evgenij on 24.12.2016.
 */
public class NewsMessage implements Message{
    long idNews;
    String comment;
    ArrayList<String> comments;

    public NewsMessage(long idNews){
        this.idNews = idNews;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public String getComment(String comment){
        return comment;
    }
}
