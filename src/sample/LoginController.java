package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


public class LoginController {
    @FXML private TextField user;
    @FXML private PasswordField password;
    @FXML private Button loginButton;
    @FXML private Button signinButton;
    @FXML private Button openButton;
    @FXML private VBox container;
    private Label error;
    private SceneManager manager;
    public void initialize() {

    }

    public void initManager(SceneManager manager) {
        this.manager = manager;

        manager.connectToServer();
        loginButton.setOnAction(event-> {
                authorize();
        });
        signinButton.setOnAction(event -> {
            signin();
        });
        openButton.setOnAction(event -> {

            manager.showMainView(0);
        });
    }

    private void signin() {
        manager.signin(user.getText(), password.getText());
    }

    private String authorize() {
        String userTxt = user.getText();
        String pswdTxt = password.getText();
        manager.authorize(userTxt, pswdTxt);

        return null;
    }

    public void authError(String message){
        if (error==null){
            error = new Label(message);
            container.getChildren().add(0,error);
            error.setTextFill(Color.rgb(210, 39, 30));
            return;
        }
        error.setText(message);
    }
}