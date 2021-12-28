package malscontrolplatform.webmodule;

import java.util.HashSet;
import malscontrolplatform.TaskModule;
import org.java_websocket.WebSocket;
import org.tinylog.Logger;

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
        Logger.info("Interactor list successfully created for topic '{}'", topic);
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
        Logger.info("Connection '{}' added to '{}' topic", connection.getRemoteSocketAddress().toString(), topic);
    }
    
    public void addModule(TaskModule module) {
        synchronized (modules) {
            modules.add(module);
        }
        Logger.info("Task module '{}' added to '{}' topic", module.getModuleName(), topic);
    }
    
    public void removeConnection(WebSocket connection) {
        synchronized (connections) {
            connections.remove(connection);
        }
        Logger.info("Connection '{}' removed from '{}' topic", connection.getRemoteSocketAddress().toString(), topic);
    }
    
    public void removeModule(TaskModule module) {
        synchronized (modules) {
            modules.remove(module);
        }
        Logger.info("Task module '{}' removed from '{}' topic", module.getModuleName(), topic);
    }
    
}
