package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import sample.client.CommentMessage;

import java.awt.*;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

public class MainSceneController {
    @FXML private WebView pageView;
    @FXML private TextField commentField;
    @FXML private VBox comments;
    @FXML private MenuItem addNewsBtn;
    @FXML private MenuItem logoutBtn;
    @FXML private VBox titleBox;
    SceneManager manager;
    ContextMenu newsMenu;
    ContextMenu commentMenu;
    int role;
    int getNewsTarget = 0;
    AddNewsDialogController dialogController;

    public void initManager(SceneManager manager) {
        this.manager = manager;
        addNewsBtn.setOnAction(event ->{
           openNewsDialog();
        });
        logoutBtn.setOnAction(event -> {
            manager.logout();
        });
    }

    public void updateTitles(TreeMap<Integer, String> titles) {
        LinkedList<Button> showTitles = new LinkedList<>();
        for (Integer id:
             titles.keySet()) {
            boolean fl = false;
            for (Node title: titleBox.getChildren()){
                if (title.getProperties().get("Id").equals(id)){
                    ((Button) title).setText(titles.get(id));
                    fl = true;
                    break;
                }
            }
            if (fl) continue;
            Button field = new Button(titles.get(id));
            field.setOnAction(event -> {
                getNewsTarget = 0;
                manager.getNews(id);
            });
            field.getProperties().put("Id", id);
            field.setStyle("-fx-background-color: #ffffff;\n" +
                    "-fx-background-radius: 8,7,6;\n" +
                    "-fx-background-insets: 0,1,2;\n" +
                    "-fx-border-color: #000000; \n" +
                    "-fx-text-fill: black;\n" +
                    "-fx-font-size: 14pt;" +
                    "-fx-cursor: hand;");
            field.setMaxWidth(Double.MAX_VALUE);

            if (role == 1) {
                ContextMenu contextMenu = new ContextMenu();
                MenuItem update = new MenuItem("Update");
                update.setOnAction(event -> {
                    getNewsTarget = 1;
                    openNewsDialog(id);
                });
                MenuItem delete = new MenuItem("Delete");
                delete.setOnAction(event -> {
                    manager.deleteNews(id);
                });
                contextMenu.getItems().addAll(update, delete);
                field.setContextMenu(contextMenu);
            }
            showTitles.addFirst(field);
        }
        titleBox.getChildren().addAll(0, showTitles);
    }

    public void showNews(String title, String text) {
        if (getNewsTarget == 0){
            WebEngine engine = pageView.getEngine();
            engine.loadContent("<h1>"+title+"</h1><p>"+text);
            comments.getChildren().clear();
        } else {
            Platform.runLater(() -> {
                dialogController.setTitle(title);
                dialogController.setNews(text);}
            );
        }

    }

    public void initRole(int role) {
        this.role = role;
        if (role==0){
            commentField.setVisible(false);
        }
        if (role!=1){
            addNewsBtn.setVisible(false);
        }
    }

    @FXML
    public void onEnter(ActionEvent actionEvent) {
        manager.sendComment(commentField.getText());
        commentField.setText("");
    }

    public void addComment(String text, String from, int id) {
        if (getNewsTarget == 1) return;
        TextArea commText = new TextArea(from+"\n"+text);
        commText.getProperties().put("Id", id);
        commText.setEditable(false);
        commText.setMinHeight(0);
        commText.setContextMenu(commentMenu);

        if (role==1) {
            ContextMenu menu = new ContextMenu();
            MenuItem item = new MenuItem("Delete");
            item.setOnAction(event -> {
                manager.deleteComment(id);
            });
            menu.getItems().add(item);
            commText.setContextMenu(menu);

        }
        comments.getChildren().add(0, commText);
    }

    public void initialize() {}

    public void deleteComment(int id) {
        Integer idInt = id;
        for (Node comment: comments.getChildren()){
            if (comment.getProperties().get("Id").equals(idInt)){
                comments.getChildren().remove(comment);
                return;
            }
        }
    }

    public void deleteNews(int id) {
        Integer idInt = id;

        for (Node title: titleBox.getChildren()){
            if (title.getProperties().get("Id").equals(idInt)){
                titleBox.getChildren().remove(title);
                return;
            }
        }
    }

    public void openNewsDialog(){
        openNewsDialog(-1);
    }

    public void openNewsDialog(int id){
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
        dialogController = loader.getController();
        dialog.getDialogPane().setPrefSize(700,300);
        if (id!=-1){
            manager.getNews(id);
        }
        ButtonType buttonTypeClose = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType buttonTypeOk = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeClose);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                return new Pair<>(dialogController.getTitle(), dialogController.getNews());
            }
            return null;
        });

        dialog.showAndWait();
        Pair<String, String> result = (Pair<String, String>) dialog.getResult();
        if (result==null) return;
        manager.addNews(result, id);
    }
}
