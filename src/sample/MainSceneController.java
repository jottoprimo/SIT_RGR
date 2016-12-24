package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.io.IOException;

public class MainSceneController {
    @FXML private WebView pageView;
    @FXML private TextField commentField;
    @FXML private VBox comments;
    @FXML private MenuItem addNewsBtn;
    SceneManager manager;

    public void initManager(SceneManager manager) {
        this.manager = manager;
        addNewsBtn.setOnAction(event ->{
            Stage stage = new Stage();
            Dialog dialog = new Dialog();
            dialog.setTitle("Add news");

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("addNewsDialog.fxml")
            );
            try {
                dialog.getDialogPane().setContent(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            AddNewsDialogController controller = loader.getController();
            dialog.getDialogPane().setPrefSize(700,300);
            ButtonType buttonTypeClose = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType buttonTypeOk = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
            dialog.getDialogPane().getButtonTypes().add(buttonTypeClose);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeOk) {
                    return new Pair<>(controller.getTitle(), controller.getNews());
                }
                return null;
            });

            dialog.showAndWait();
            Pair<String, String> result = (Pair<String, String>) dialog.getResult();
            if (result==null) return;
            manager.addNews(result);
          /*
            dialog.setR
            VBox box = new VBox();
            box.setAlignment(Pos.CENTER);
            HBox buttons = new HBox();
            buttons.setAlignment(Pos.CENTER);
            buttons.getChildren().addAll(new Button("Ok"), new Button("Cancel"));
            box.getChildren().addAll(new Label("Simple dialog"), new TextField(), buttons);
            Scene scene = new Scene(box);
            dialog.setScene(scene);
            dialog.show();*/
        });
    }

    public enum Type{
        ADMIN,
        REGISTERED,
        OTHER
    }
    public void initialize() {}

    public void test(){
        TextArea area = new TextArea("asdasda");
        area.setMaxHeight(100);
        area.setEditable(false);
        comments.getChildren().add(area);
        commentField.setVisible(false);
        commentField.setManaged(false);
    }
}
