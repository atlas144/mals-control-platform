package malscontrolplatform.webmodule;

import java.util.HashSet;
import malscontrolplatform.TaskModule;
import org.java_websocket.WebSocket;

/**
 *
 * @author atlas144
 */
public class TopicInteractorList {
    
    private final String topic;
    private final HashSet<WebSocket> connections;
    private final HashSet<TaskModule> modules;

    public TopicInteractorList(String topic) {
        this.topic = topic;
        connections = new HashSet<>();
        modules = new HashSet<>();
    }

    public String getTopic() {
        return topic;
    }

    public HashSet<WebSocket> getConnections() {
        return connections;
    }

    public HashSet<TaskModule> getModules() {
        return modules;
    }
    
    public void addConnection(WebSocket connection) {
        synchronized (connections) {
            connections.add(connection);
        }
    }
    
    public void addModule(TaskModule module) {
        synchronized (modules) {
            modules.add(module);
        }
    }
    
    public void removeConnection(WebSocket connection) {
        synchronized (connections) {
            connections.remove(connection);
        }
    }
    
    public void removeModule(TaskModule module) {
        synchronized (modules) {
            modules.remove(module);
        }
    }
    
}
