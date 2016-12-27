package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

/**
 * Created by Evgenij on 24.12.2016.
 */
public class AddNewsDialogController {
    @FXML
    HTMLEditor newsText;
    @FXML
    TextField title;

    public void initialize(){
    }

    public String getTitle(){
        return title.getText();
    }
    public void setTitle(String titleTxt) { title.setText(titleTxt);}

    public String getNews(){
        return newsText.getHtmlText().replaceAll("contenteditable=\"true\"", "");
    }
    public void setNews(String news) {
        newsText.setHtmlText(news);
    }

}
