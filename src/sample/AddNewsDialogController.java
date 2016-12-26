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
    Button okButton;
    @FXML
    HTMLEditor newsText;
    @FXML
    TextField title;

    public void initialize(){
        okButton.setOnAction(event -> {
            Stage stage = (Stage) okButton.getScene().getWindow();
            // do what you have to do
            stage.close();
        });
    }

    public String getTitle(){
        return title.getText();
    }

    public String getNews(){
        return newsText.getHtmlText().replaceAll("contenteditable=\"true\"", "");
    }

}
