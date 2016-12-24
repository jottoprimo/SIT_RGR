package sample;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Pair;
import sun.rmi.runtime.Log;

import java.io.IOException;

/**
 * Created by Evgenij on 24.12.2016.
 */
public class SceneManager {
    private Scene scene;
    private Model model;
    private LoginController loginController;
    private MainSceneController mainController;
    private Object stage;

    public SceneManager (Scene scene, Model model) {
        this.model = model;
        this.scene = scene;
    }

    /**
     * Callback method invoked to notify that a user has been authenticated.
     * Will show the main application screen.
     */
    public void authenticated(String sessionID) {
        showMainView();
    }

    /**
     * Callback method invoked to notify that a user has logged out of the main application.
     * Will show the login application screen.
     */
    public void logout() {
        showLoginScreen();
    }

    public void showLoginScreen() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("authScene.fxml")
                );
                scene.setRoot((Parent) loader.load());
                loginController =
                        loader.<LoginController>getController();
                loginController.initManager(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mainController = null;
        });
    }

    public void showMainView() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("mainScene.fxml")
                );
                scene.setRoot((Parent) loader.load());
                mainController =
                        loader.<MainSceneController>getController();

                mainController.initManager(this);
                test();
            } catch (IOException e) {
                e.printStackTrace();
            }
            loginController = null;
        });
    }

    public void authorize(String userTxt, String pswdTxt) {
        model.authorize(userTxt, pswdTxt);
    }

    public void signin(String user, String password) {
        model.signin(user, password);
    }

    public void authError(String message) {
        //TODO: Вывопдить ошибку
        Platform.runLater(() -> loginController.authError(message));
    }

    public void test() {
        mainController.test();
    }

    public void addNews(Pair<String, String> news) {
        model.addNews(news);
    }
}
