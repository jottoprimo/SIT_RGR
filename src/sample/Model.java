package sample;

import sample.client.AuthMessage;
import sample.client.ClientThread;
import sample.client.Message;
import sample.client.NewsMessage;

import java.io.IOException;

/**
 * Created by Evgenij on 24.12.2016.
 */
public class Model {
    ClientThread client;
    SceneManager manager;
    public Model(){
        client = null;
        manager = null;
    }

    public void setManager(SceneManager manager){
        this.manager = manager;
    }

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

    public void authenticated() {
        manager.showMainView();
    }

    public void authError(String message) {
        manager.authError(message);
    }
}
