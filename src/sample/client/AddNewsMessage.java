package sample.client;

/**
 * Created by Evgenij on 24.12.2016.
 */
public class AddNewsMessage implements Message {
    Integer id;
    String title;
    String news;
    boolean success;
    boolean isDeleted;

    public AddNewsMessage(String title, String news){
        this.title = title;
        this.news = news;
        isDeleted = false;
        id = null;
    }

    public AddNewsMessage(int id, String title, String news, boolean isDeleted){
        this.id = id;
        this.title = title;
        this.news = news;
        this.isDeleted = isDeleted;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }


}
