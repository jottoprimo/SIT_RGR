package sample;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

public class MainSceneController {
    @FXML private WebView pageView;
    public enum Type{
        ADMIN,
        REGISTERED,
        OTHER
    }
    public void initialize() {}
}
