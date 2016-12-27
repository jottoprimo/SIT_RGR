package sample.client;

/**
 * Created by Evgenij on 24.12.2016.
 */
public class AddNewsMessage implements Message {
    Integer id;
    String title;
    String news;
    boolean success;

    public AddNewsMessage(String title, String news){
        this.title = title;
        this.news = news;
        id = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



}
