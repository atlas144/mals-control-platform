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

/**
 *
 * @author atlas144
 */
public class WebModule extends WebSocketServer {
    
    private final String host;
    private final short port;
    private final ConcurrentHashMap<String, TopicInteractorList> topics;
    
    private void subscribe(String topic, WebSocket connection) {        
        if (topics.containsKey(topic)) {
            topics.get(topic).addConnection(connection);
        } else {
            TopicInteractorList topicInteractorList = new TopicInteractorList(topic);
            
            topicInteractorList.addConnection(connection);
            topics.put(topic, topicInteractorList);
        }
    }
    
    public WebModule(String host, short port) {
        super(new InetSocketAddress(host, port));
        
        this.host = host;
        this.port = port;
        topics = new ConcurrentHashMap<>();
    }
    
    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        String topic = URI.create(connection.getResourceDescriptor()).getPath();
        
        subscribe(topic, connection);
    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        String topic = URI.create(connection.getResourceDescriptor()).getPath();
        
        if (topics.containsKey(topic)) {
            topics.get(topic).removeConnection(connection);
        } else {
            // TODO log unregistered topic
        }
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

            for (TaskModule module : topics.get(topic).getModules()) {
                module.acceptMessage(new Message(topic, messagePayload.getString("payload"), priority));            
            }
        } else {
            // TODO log unknown topic
        }
    }

    @Override
    public void onError(WebSocket connection, Exception ex) {
        // TODO log an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex
    }

    @Override
    public void onStart() {
        // TODO log server started successfully on host:port
    }
    
    public void subscribe(String topic, TaskModule module) {        
        if (topics.containsKey(topic)) {
            topics.get(topic).addModule(module);
        } else {
            TopicInteractorList topicInteractorList = new TopicInteractorList(topic);
            
            topicInteractorList.addModule(module);
            topics.put(topic, topicInteractorList);
        }
    }
    
    public void unsubscribe(String topic, TaskModule module) {
        if (topics.containsKey(topic)) {
            topics.get(topic).removeModule(module);
        } else {
            // TODO log unregistered topic
        }
    }
    
    public void publish(String topic, String message) {
        if (topics.containsKey(topic)) {
            HashSet<WebSocket> connections = topics.get(topic).getConnections();

            for (WebSocket connection : connections) {
                connection.send(message);
            }
            
            // TODO log message send to <n> connections
        } else {
            // TODO log unknown endpoint <endpoint>
        }
    }
    
}
