package server;

import sample.client.AddNewsMessage;
import sample.client.AuthMessage;
import sample.client.Message;
import sample.client.TitlesMessage;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Evgenij on 18.12.2016.
 */
public class ConnectionThread extends Thread {
    Socket socket;
    boolean interrupt;
    UUID id;
    String login;
    String role;

    public UUID getConnectionId() {
        return id;
    }

    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    public ConnectionThread(Socket socket, UUID id){
        interrupt  = false;
        this.id = id;
        this.socket = socket;
        login = null;
        role = null;
    }

    @Override
    public void run(){
        try {
            outStream = new ObjectOutputStream(new BufferedOutputStream (socket.getOutputStream()));
            inStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            while (true){
                Message message = (Message) inStream.readObject();
                if (message instanceof AuthMessage) {
                    AuthMessage auth = (AuthMessage) message;
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    String connectionUrl = "jdbc:sqlserver://localhost;database=News_Service;integratedSecurity=true;";
                    Connection con = DriverManager.getConnection(connectionUrl);
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(
                            "SELECT cnt = COUNT(*) FROM dbo.Users WHERE Login='"+auth.getUser()
                                    +"' AND Password='"+auth.getPassword()+"'"
                    );
                    rs.next();
                    int cnt = rs.getInt("cnt");
                    if (cnt>0 && auth.IsSignIn()){
                        auth.setStatus(AuthMessage.Status.LOGIN_REQUIRED);
                    }
                    else if (cnt==0 && !auth.IsSignIn()) {
                        auth.setStatus(AuthMessage.Status.WRONG_PASSW);
                    }
                    else if (cnt==0) {
                        int success = stmt.executeUpdate("INSERT INTO [dbo].[Users]\n" +
                                    "([Login]\n" +
                                    ",[Password]\n" +
                                    ",[Role])\n" +
                                "VALUES\n" +
                                    "('"+auth.getUser()+"'\n" +
                                    ",'"+auth.getPassword()+"'\n" +
                                    ",2)");

                        if (success==1) {
                            auth.setStatus(AuthMessage.Status.SUCCESS);
                        } else auth.setStatus(AuthMessage.Status.FAIL);
                    } else {
                        auth.setStatus(AuthMessage.Status.SUCCESS);
                    }
                    con.close();
                    outStream.writeObject(auth);
                    outStream.flush();
                    continue;
                }
                if (message instanceof AddNewsMessage){
                    AddNewsMessage news = (AddNewsMessage) message;
                    if (news.getId()==null){
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        String connectionUrl = "jdbc:sqlserver://localhost;database=News_Service;integratedSecurity=true;";
                        Connection con = DriverManager.getConnection(connectionUrl);
                        Statement stmt = con.createStatement();
                        int rs = stmt.executeUpdate(
                                "INSERT INTO [dbo].[News]\n" +
                                    "([Title]\n" +
                                    ",[Text]\n" +
                                    ",[Date]\n" +
                                    ",[IsDeleted])\n" +
                                "VALUES\n" +
                                    "('"+news.getTitle()+"'\n" +
                                    ",'"+news.getNews()+"'\n" +
                                    ",GETDATE()\n" +
                                    ",0)"
                        );
                        if (rs==0) news.setSuccess(false);
                        else news.setSuccess(true);
                        con.close();
                        outStream.writeObject(news);
                        outStream.flush();
                    }
                    continue;
                }
                if (message instanceof TitlesMessage){
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    String connectionUrl = "jdbc:sqlserver://localhost;database=News_Service;integratedSecurity=true;";
                    Connection con = DriverManager.getConnection(connectionUrl);
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(
                            "SELECT TOP(10) Id, Title FROM dbo.News"
                    );
                    HashMap<Integer, String> result = new HashMap<>();
                    while (rs.next()){
                        int id = rs.getInt("Id");
                        String title = rs.getString("Title");
                        result.put(id, title);
                    }
                    con.close();
                    TitlesMessage titles = new TitlesMessage(result);
                    sendMessage(titles);
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while (!interrupt){

        } /*try {
            if (socket.isClosed() )return;
            Message msg = (Message) inStream.readObject();
            System.out.println(msg.getType()+" "+ msg.getFrom()+" "+ msg.getTo());
            switch (msg.getType()) {
                case CONNECT:
                    break;
                case DISCONNECT:
                    synchronized (server.Connections.getInstance()) {
                        server.Connections.getInstance().remove(id);
                    }
                    socket.close();
                    updateConnections();
                    return;
                case UPDATE_CONNECTIONS:
                    break;
                case COPY_HOUSES:
                    server.ConnectionThread target;
                    synchronized (server.Connections.getInstance()) {
                        if (msg.getHouses()!=null)
                            System.out.println("SIZE :"+msg.getHouses().size());
                        target = server.Connections.getInstance().getConnection(msg.to);
                        target.sendMessage(msg);
                    }
                    break;
            }
        } catch (IOException | ClassNotFoundException e ) {
            e.printStackTrace();
        }*/
    }

    public void sendMessage(Message message) throws IOException {
        outStream.writeObject(message);
        outStream.flush();
    }
}
