package server;

import javafx.util.Pair;
import sample.client.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Evgenij on 18.12.2016.
 */
public class ConnectionThread extends Thread {
    Socket socket;
    boolean interrupt;
    int id;
    String login;
    int role;
    int idNews;


    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    public ConnectionThread(Socket socket){
        interrupt  = false;
        this.id = 0;
        this.socket = socket;
        login = null;
        role = -1;
        idNews = 0;
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
                            "SELECT Id, Role FROM dbo.Users WHERE Login='"+auth.getUser()
                                    +"' AND Password='"+auth.getPassword()+"'"
                    );
                    int cnt = 0;
                    if(rs.next()) cnt = 1;
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
                                    ",2)", Statement.RETURN_GENERATED_KEYS);

                        if (success==1) {
                            auth.setStatus(AuthMessage.Status.SUCCESS);
                            role=2;
                            login = auth.getUser();
                            auth.setRole(role);
                            rs = stmt.getGeneratedKeys();
                            rs.next();
                            id = rs.getInt("Id");
                        } else auth.setStatus(AuthMessage.Status.FAIL);
                    } else {
                        role = rs.getInt("Role");
                        id = rs.getInt("Id");
                        login = auth.getUser();
                        auth.setRole(role);
                        auth.setStatus(AuthMessage.Status.SUCCESS);
                    }
                    con.close();
                    sendMessage(auth);
                    continue;
                }
                if (message instanceof AddNewsMessage){
                    AddNewsMessage news = (AddNewsMessage) message;
                    if (news.getId()==null && role==1){
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
                        ,  Statement.RETURN_GENERATED_KEYS);
                        if (rs==0){
                            news.setSuccess(false);
                            con.close();
                            continue;
                        }
                        ResultSet resultSet = stmt.getGeneratedKeys();
                        resultSet.next();
                        int id = resultSet.getInt(1);
                        con.close();
                        updateTitlesAll(news.getTitle(), id);

                        news.setSuccess(true);


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
                    TreeMap<Integer, String> result = new TreeMap<>();
                    while (rs.next()){
                        int id = rs.getInt("Id");
                        String title = rs.getString("Title").trim();
                        result.put(id, title);
                    }
                    con.close();
                    TitlesMessage titles = new TitlesMessage(result);
                    sendMessage(titles);
                    continue;
                }
                if (message instanceof NewsMessage){
                    NewsMessage newsMessage = (NewsMessage) message;
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    String connectionUrl = "jdbc:sqlserver://localhost;database=News_Service;integratedSecurity=true;";
                    Connection con = DriverManager.getConnection(connectionUrl);
                    Statement stmt = con.createStatement();
                    if (!newsMessage.isDeleted()) {
                        ResultSet rs = stmt.executeQuery(
                                "SELECT Text FROM dbo.News WHERE Id = " + newsMessage.getId()
                        );
                        if (rs.next()) {
                            newsMessage.setText(rs.getString("Text"));
                            idNews = newsMessage.getId();
                        }

                        LinkedList<CommentMessage> comments = new LinkedList<>();
                        rs = stmt.executeQuery(
                                "SELECT Id=A.Id, [Login], [Text] FROM (\n" +
                                        "SELECT [Id]\n" +
                                        ",[User]\n" +
                                        ",[NewsId]" +
                                        ",[Text]\n" +
                                        ",[Time]\n" +
                                        ",IsDeleted\n" +
                                        "FROM [dbo].[Comments]\n" +
                                        ") A\n" +
                                        "JOIN (\n" +
                                        "SELECT Id, Login FROM dbo.Users\n" +
                                        ") B ON A.[User]=B.Id\n" +
                                        "WHERE\n" +
                                        "IsDeleted=0 AND \n" +
                                        "NewsId=" + idNews + "\n" +
                                        "ORDER BY A.[Time] DESC"
                        );
                        while (rs.next()) {
                            int id = rs.getInt("Id");
                            String text = rs.getString("Text").trim();
                            String from = rs.getString("Login").trim();
                            CommentMessage comment = new CommentMessage(idNews, text, from);
                            comment.setId(id);
                            comments.add(comment);
                        }
                        con.close();
                        sendMessage(newsMessage);
                        sendComments(comments);
                    } else {
                        if (role!=1) continue;
                        int rs = stmt.executeUpdate(
                                "UPDATE dbo.News SET IsDeleted=1 WHERE Id="+newsMessage.getId()
                        );
                        updateNewsAll(newsMessage);
                    }
                }
                if (message instanceof CommentMessage){
                    CommentMessage commentMessage = (CommentMessage) message;

                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    String connectionUrl = "jdbc:sqlserver://localhost;database=News_Service;integratedSecurity=true;";
                    Connection con = DriverManager.getConnection(connectionUrl);
                    Statement stmt = con.createStatement();
                    if (!commentMessage.isDeleted()) {
                        int rs = stmt.executeUpdate(
                                "INSERT INTO [dbo].[Comments]\n" +
                                        "           ([NewsId]\n" +
                                        "           ,[Text]\n" +
                                        "           ,[User]\n" +
                                        "           ,[Time])\n" +
                                        "     VALUES\n" +
                                        "           (" + commentMessage.getIdNews() + "\n" +
                                        "           ,'" + commentMessage.getText() + "'\n" +
                                        "           ," + id + "\n" +
                                        "           ,GETDATE())"
                                , Statement.RETURN_GENERATED_KEYS);
                        if (rs == 0) {
                            continue;
                        }
                        ResultSet resultSet = stmt.getGeneratedKeys();
                        resultSet.next();
                        commentMessage.setId(resultSet.getInt(1));
                        commentMessage.setFrom(login);
                    } else {
                        if (role!=1) continue;
                        int rs = stmt.executeUpdate(
                                "UPDATE dbo.Comments SET IsDeleted=1 WHERE Id="+commentMessage.getId()
                        );
                    }
                    updateCommentsAll(commentMessage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void updateNewsAll(NewsMessage message) {
        synchronized (Connections.getInstance()){
            for (ConnectionThread thread: Connections.getInstance().getConnections()){
                try {
                    thread.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendComments(LinkedList<CommentMessage> comments) {
        for (CommentMessage comment :
                comments) {
            try {
                sendMessage(comment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTitlesAll(String title, int id) {
        TreeMap<Integer, String> titles = new TreeMap<>();
        titles.put(id, title);
        TitlesMessage titlesMsg = new TitlesMessage(titles);
        synchronized (Connections.getInstance()){
            for (ConnectionThread thread: Connections.getInstance().getConnections()){
                try {
                    thread.sendMessage(titlesMsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateCommentsAll(CommentMessage message) {
        synchronized (Connections.getInstance()){
            for (ConnectionThread thread: Connections.getInstance().getConnections()){
                try {
                    if (thread.idNews==message.getIdNews())
                        thread.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessage(Message message) throws IOException {
        outStream.writeObject(message);
        outStream.flush();
    }
}
