package malscontrolplatform.webmodule;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import malscontrolplatform.TaskModule;
import malscontrolplatform.intercommunicationmodule.Message;
import malscontrolplatform.intercommunicationmodule.Priority;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;
import org.tinylog.Logger;

/**
 *
 * @author atlas144
 */
public class WebModule extends WebSocketServer {
    
    private final String host;
    private final short port;
    private final ConcurrentHashMap<String, TopicInteractorList> topics;
    
    private void subscribe(String topic, WebSocket subscriber) {        
        if (topics.containsKey(topic)) {
            topics.get(topic).addConnection(subscriber);
        } else {
            TopicInteractorList topicInteractorList = new TopicInteractorList(topic);
            
            topicInteractorList.addConnection(subscriber);
            topics.put(topic, topicInteractorList);
            
            Logger.info("Topic '{}' successfully registered", topic);
        }
        
        Logger.info("Connection '{}' successfully subscribed to '{}' topic", subscriber.getRemoteSocketAddress().toString(), topic);
    }
    
    public WebModule(String host, short port) {
        super(new InetSocketAddress(host, port));
        
        this.host = host;
        this.port = port;
        topics = new ConcurrentHashMap<>();
        Logger.info("Web module successfully created");
    }
    
    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        String topic = URI.create(connection.getResourceDescriptor()).getPath();
        
        subscribe(topic, connection);
            
        Logger.info("Connection '{}' successfully opened", connection.getRemoteSocketAddress().toString());
    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        String topic = URI.create(connection.getResourceDescriptor()).getPath();
        
        if (topics.containsKey(topic)) {
            topics.get(topic).removeConnection(connection);
            Logger.info("Connection '{}' successfully unsubscribed '{}' topic", connection.getRemoteSocketAddress().toString(), topic);
        } else {
            Logger.warn("Connection '{}' has not been subscribing to '{}' topic", connection.getRemoteSocketAddress().toString(), topic);
        }
        
        Logger.info("Connection '{}' successfully closed\nReason: {}\nCode: {}\nRemote: {}", connection.getRemoteSocketAddress().toString(), reason, code, remote);
    }

    @Override
    public void onMessage(WebSocket connection, String message) {
        String topic = URI.create(connection.getResourceDescriptor()).getPath();
        
        if (topics.containsKey(topic)) {
            JSONObject messagePayload = new JSONObject(message);
            Priority priority;

            switch (messagePayload.getInt("priority")) {
                case 0: {
                    priority = Priority.UNIMPORTANT;
                    break;
                } case 1: {
                    priority = Priority.NORMAL;
                    break;
                } case 2: {
                    priority = Priority.IMPORTANT;
                    break;
                } case 3: {
                    priority = Priority.CRITICAL;
                    break;
                } default:{
                    // TODO log unexpected priority level -> falling to normal
                    priority = Priority.NORMAL;
                    break;
                }
            }
            
            Logger.debug("Message successfully accepted\nTopic: {}\nPayload: {}\nPriority: {}", topic, message, priority.toString());
        
            int counter = 0;

            for (TaskModule module : topics.get(topic).getModules()) {
                module.acceptMessage(new Message(topic, messagePayload.getString("payload"), priority));
                Logger.trace("Message '{}' successfully sent to the '{}' task module", message, module.getModuleName());
                counter++;
            }
            
            Logger.debug("Message '{}' successfully sent to {} task modules", message, counter);
        } else {
            Logger.warn("Topic '{}' in not registered topic", topic);
        }
    }

    @Override
    public void onError(WebSocket connection, Exception ex) {
        Logger.warn(ex);
    }

    @Override
    public void onStart() {
        Logger.info("WebSocket server started successfully on {}:{}", host, port);
    }
    
    public void subscribe(String topic, TaskModule subscriber) {        
        if (topics.containsKey(topic)) {
            topics.get(topic).addModule(subscriber);
        } else {
            TopicInteractorList topicInteractorList = new TopicInteractorList(topic);
            
            topicInteractorList.addModule(subscriber);
            topics.put(topic, topicInteractorList);
            
            Logger.info("Topic '{}' successfully registered", topic);
        }

        Logger.info("Task module '{}' successfully subscribed to '{}' topic", subscriber.getModuleName(), topic);
    }
    
    public void unsubscribe(String topic, TaskModule subscriber) {
        if (topics.containsKey(topic)) {
            topics.get(topic).removeModule(subscriber);

            Logger.info("Task module '{}' successfully unsubscribed '{}' topic", subscriber.getModuleName(), topic);
        } else {
            Logger.warn("Topic '{}' in not registered topic", topic);
        }
    }
    
    public void publish(String topic, String message) {
        if (topics.containsKey(topic)) {
            HashSet<WebSocket> connections = topics.get(topic).getConnections();
            int counter = 0;

            for (WebSocket connection : connections) {
                connection.send(message);
                Logger.trace("Message '{}' successfully sent to the '{}' connection", message, connection.getRemoteSocketAddress().toString());
                counter++;
            }
            
            Logger.debug("Message '{}' successfully sent to {} connections", message, counter);
        } else {
            Logger.warn("Topic '{}' in not registered topic", topic);
        }
    }
    
}
