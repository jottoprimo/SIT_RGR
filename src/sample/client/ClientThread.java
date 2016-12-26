package sample.client;

import javafx.util.Pair;
import sample.Model;
import sample.SceneManager;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by Evgenij on 24.12.2016.
 */
public class ClientThread extends Thread{
    Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    boolean interrupt;
    SceneManager manager;

    public ClientThread (SceneManager manager) throws IOException {
        socket = new Socket("localhost", 8888);
        this.manager = manager;
    }

    @Override
    public void run(){
        try {
            outputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            inputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            while (!interrupt){
                Message message = (Message) inputStream.readObject();
                if (message instanceof AuthMessage){
                    AuthMessage auth = (AuthMessage) message;
                    switch (auth.getStatus()){
                        case SUCCESS:
                            manager.authenticated(auth.getRole());
                            break;
                        case WRONG_PASSW:
                            manager.authError("Неверный пароль");
                            break;
                        case LOGIN_REQUIRED:
                            manager.authError("Логин занят");
                            break;
                        case FAIL:
                            manager.authError("Неизвестная ошибка");
                            break;
                    }
                    continue;
                }
                if (message instanceof AddNewsMessage){
                    AddNewsMessage news = (AddNewsMessage) message;
                    //TODO: Возможно уведомлялка о улачной загрузке
                }
                if (message instanceof TitlesMessage){
                    TitlesMessage titlesMsg = (TitlesMessage) message;
                    TreeMap<Integer, String> titles = titlesMsg.getTitles();
                    manager.updateTitles(titles);
                }
                if (message instanceof NewsMessage){
                    NewsMessage newsMessage = (NewsMessage)message;
                    if (newsMessage.isDeleted()){
                        manager.deleteMessagefromUI(newsMessage.getId());
                    } else {
                        manager.showNews(newsMessage.getText(), newsMessage.getId());
                    }
                }
                if (message instanceof CommentMessage){
                    CommentMessage commentMessage = (CommentMessage)message;
                    if (!commentMessage.isDeleted()) {
                        manager.addComment(commentMessage.getIdNews(), commentMessage.getId(), commentMessage.getText(), commentMessage.getFrom());
                    } else {
                        manager.deleteCommentFromUI(commentMessage.getId());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) throws IOException {
        outputStream.writeObject(message);
        outputStream.flush();
    }
}
