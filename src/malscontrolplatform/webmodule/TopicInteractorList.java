// SPDX-License-Identifier: LGPL-2.1-only

package malscontrolplatform.webmodule;

import java.util.HashSet;
import malscontrolplatform.TaskModule;
import org.java_websocket.WebSocket;
import org.tinylog.Logger;

/**
 * A structure that stores references to all subscribers of a given topic.
 *
 * @author atlas144
 */
public class TopicInteractorList {
    
    private final String topic;
    private final HashSet<WebSocket> connections;
    private final HashSet<TaskModule> modules;

    /**
     * Builds topic interactor list.
     * 
     * @param topic topic of the list
     */
    public TopicInteractorList(String topic) {
        this.topic = topic;
        connections = new HashSet<>();
        modules = new HashSet<>();
        Logger.info("Interactor list successfully created for topic '{}'", topic);
    }

    /**
     * Returns topic of the list.
     * 
     * @return list topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Returns list of connections.
     * 
     * @return connections list
     */
    public HashSet<WebSocket> getConnections() {
        return connections;
    }

    /**
     * Returns list of task modules.
     * 
     * @return task modules list
     */
    public HashSet<TaskModule> getModules() {
        return modules;
    }
    
    /**
     * Adds new connection to the list of topic interactors.
     * 
     * @param connection new connection to be added
     */
    public void addConnection(WebSocket connection) {
        synchronized (connections) {
            connections.add(connection);
        }
        Logger.info("Connection '{}' added to '{}' topic", connection.getRemoteSocketAddress().toString(), topic);
    }
    
    /**
     * Adds new task module to the list of topic interactors.
     * 
     * @param module new task module to be added
     */
    public void addModule(TaskModule module) {
        synchronized (modules) {
            modules.add(module);
        }
        Logger.info("Task module '{}' added to '{}' topic", module.getModuleName(), topic);
    }
    
    /**
     * Removes connection from the list of topic interactors.
     * 
     * @param connection connection to be removed
     */
    public void removeConnection(WebSocket connection) {
        synchronized (connections) {
            connections.remove(connection);
        }
        Logger.info("Connection '{}' removed from '{}' topic", connection.getRemoteSocketAddress().toString(), topic);
    }
    
    /**
     * Removes task module from the list of topic interactors.
     * 
     * @param module task module to be removed
     */
    public void removeModule(TaskModule module) {
        synchronized (modules) {
            modules.remove(module);
        }
        Logger.info("Task module '{}' removed from '{}' topic", module.getModuleName(), topic);
    }
    
}
