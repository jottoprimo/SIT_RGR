package sample.client;

import sample.Model;

import java.io.*;
import java.net.Socket;

/**
 * Created by Evgenij on 24.12.2016.
 */
public class ClientThread extends Thread{
    Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    boolean interrupt;
    Model model;

    public ClientThread (Model model) throws IOException {
        socket = new Socket("localhost", 8888);
        this.model = model;
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
                            model.authenticated();
                            break;
                        case WRONG_PASSW:
                            model.authError("Неверный пароль");
                            break;
                        case LOGIN_REQUIRED:
                            model.authError("Логин занят");
                            break;
                        case FAIL:
                            model.authError("Неизвестная ошибка");
                            break;
                    }
                    continue;
                }
                if (message instanceof AddNewsMessage){
                    AddNewsMessage news = (AddNewsMessage) message;
                    //TODO: Возможно уведомлялка о улачной загрузке
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
