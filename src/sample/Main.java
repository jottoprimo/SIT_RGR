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

    @Override
    public void start(Stage primaryStage){
        Scene scene = new Scene(new StackPane());
        Model model = new Model();
        SceneManager manager = new SceneManager(scene, model);
        model.setManager(manager);
        manager.showLoginScreen();
        //WebView web = (WebView)root.lookup("#PageView");
        //WebEngine engine = web.getEngine();
        //engine.loadContent("<h1>FUCK IT</h1>");
        primaryStage.setScene(scene);
        primaryStage.show();
        model.connectToServer();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
