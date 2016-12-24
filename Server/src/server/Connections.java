package server;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Evgenij on 20.12.2016.
 */
public class Connections {


    private HashMap<UUID, ConnectionThread> connections = new HashMap<>();
    private static Connections singleton;

    private Connections() {
    }

    public static Connections getInstance(){
        if (singleton==null){
            singleton = new Connections();
        }
        return singleton;
    }

    public HashMap<UUID, ConnectionThread> getConnections(){
        return connections;
    }

    public void remove(int id) {
        connections.remove(id);
    }

    public ConnectionThread getConnection(int id){
        connections.get(id);
        return null;
    }

    public void add(UUID id, ConnectionThread connection){
        connections.put(id, connection);
    }
}