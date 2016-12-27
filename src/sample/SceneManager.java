package sample;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import sample.client.*;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by Evgenij on 24.12.2016.
 */
public class SceneManager {
    private Scene scene;
    private LoginController loginController;
    private MainSceneController mainController;
    ClientThread client;
    int idNews;
    private Stage stage;

    public void connectToServer(){
        try {
            client = new ClientThread(this);
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authorize(String user, String password){
        AuthMessage message = new AuthMessage(user, password, false);
        try {
            client.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void signin(String user, String password) {
        AuthMessage message = new AuthMessage(user, password, true);
        try {
            client.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authenticated(int role) {
        showMainView(role);
    }

    public void addNews(Pair<String, String> news, int id) {
        AddNewsMessage message = new AddNewsMessage(news.getKey(), news.getValue());
        if (id!=-1){
            message.setId(id);
        }
        try {
            client.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SceneManager (Scene scene, Stage stage) {
        this.client = null;
        this.scene = scene;
        this.stage = stage;
    }

    public void logout() {
        stop();
        showLoginScreen();
    }

    public void showLoginScreen() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("authScene.fxml")
                );
                scene.setRoot((Parent) loader.load());
                stage.setHeight(200);
                stage.setWidth(300);
                loginController =
                        loader.<LoginController>getController();
                loginController.initManager(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mainController = null;
        });
    }

    public void showMainView(int role) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("mainScene.fxml")
                );
                scene.setRoot((Parent) loader.load());
                stage.setHeight(600);
                stage.setWidth(800);
                mainController =
                        loader.<MainSceneController>getController();
                mainController.initManager(this);
                mainController.initRole(role);
                client.sendMessage(new TitlesMessage(null));
            } catch (IOException e) {
                e.printStackTrace();
            }
            loginController = null;
        });
    }

    public void authError(String message) {
        Platform.runLater(() -> loginController.authError(message));
    }

    public void updateTitles(TreeMap<Integer, String> titles) {
        Platform.runLater(() -> mainController.updateTitles(titles));
    }

    public void getNews(int id) {
        NewsMessage message = new NewsMessage(id);
        try {
            client.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showNews(String title, String text, int id) {
        idNews = id;
        Platform.runLater(() ->  mainController.showNews(title, text));
    }

    public void sendComment(String text) {
        if (idNews==0) return;
        CommentMessage message = new CommentMessage(idNews, text);
        try {
            client.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addComment(int idNews, int id, String text, String from) {
        if (idNews!=this.idNews) return;
        Platform.runLater(() ->  mainController.addComment(text, from, id));
    }

    public void deleteComment(int id) {
        CommentMessage message = new CommentMessage(id, true);
        message.setIdNews(idNews);
        try {
            client.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteCommentFromUI(int id) {
        Platform.runLater(() -> mainController.deleteComment(id));
    }

    public void deleteNews(int id) {
        NewsMessage message = new NewsMessage(id, true);
        try {
            client.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteNewsFromUI(int id) {
        Platform.runLater(() -> mainController.deleteNews(id));
    }

    public void stop() {
        client.closeSocket();
    }

    public boolean connected() {
        return client!=null && client.isInterrupted();
    }
}
