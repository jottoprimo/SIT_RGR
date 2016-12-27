package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class Main extends Application {
    SceneManager manager;

    @Override
    public void start(Stage primaryStage){
        Scene scene = new Scene(new StackPane());
        Model model = new Model();
        manager = new SceneManager(scene, primaryStage);
        manager.showLoginScreen();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        manager.stop();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
