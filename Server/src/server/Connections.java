package server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Created by Evgenij on 20.12.2016.
 */
public class Connections {


    private LinkedList<ConnectionThread> connections = new LinkedList<>();
    private static Connections singleton;

    private Connections() {
    }

    public static Connections getInstance(){
        if (singleton==null){
            singleton = new Connections();
        }
        return singleton;
    }

    public LinkedList<ConnectionThread> getConnections(){
        return connections;
    }

    public void remove(ConnectionThread connectionThread) {
        connections.remove(connectionThread);
    }

    public void add(ConnectionThread connection){
        connections.add(connection);
    }
}