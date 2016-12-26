package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by Evgenij on 24.12.2016.
 */
public class Main {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            while (true){
                Socket client = serverSocket.accept();
                ConnectionThread connection = new ConnectionThread(client);
                Connections.getInstance().add(connection);
                connection.start();
            }
            /*Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String connectionUrl = "jdbc:sqlserver://localhost;database=News_Service;integratedSecurity=true;";
            Connection con = DriverManager.getConnection(connectionUrl);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM dbo.Roles");
            while (rs.next()){
                int id = rs.getInt("Id");
                String role = rs.getString("Role");
                System.out.println(id+" "+role);
            }
            con.close();*/
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
