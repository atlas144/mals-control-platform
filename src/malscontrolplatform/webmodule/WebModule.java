package malscontrolplatform.webmodule;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
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
    private final ConcurrentHashMap<String, ArrayList<WebSocket>> endpoints;
    private final ConcurrentHashMap<String, TaskModule> modules;
    
    public WebModule(String host, short port, ConcurrentHashMap modules) {
        super(new InetSocketAddress(host, port));
        
        this.host = host;
        this.port = port;
        this.modules = modules;
        endpoints = new ConcurrentHashMap<>();
    }
    
    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        String endpoint = URI.create(connection.getResourceDescriptor()).getPath();
        
        registerTopic(endpoint);
        
        ArrayList<WebSocket> connections = endpoints.get(endpoint);
        
        synchronized (connections) {
            connections.add(connection);
        }
    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        String endpoint = URI.create(connection.getResourceDescriptor()).getPath();
        
        endpoints.get(endpoint).remove(connection);
    }

    @Override
    public void onMessage(WebSocket connection, String message) {
        String[] endpointLayers = URI.create(connection.getResourceDescriptor()).getPath().split("/");
        
        switch (endpointLayers[0]) {
            case "module": {
                if (modules.containsKey(endpointLayers[1])) {
                    String topic = "";
                    JSONObject messagePayload = new JSONObject(message);
                    Priority priority;
                                     
                    for (byte i = 2; i < endpointLayers.length; i++) {
                        topic += endpointLayers[i];
                    }
                    
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
                    
                    modules.get(endpointLayers[1]).acceptMessage(new Message(topic, messagePayload.getString("payload"), priority));
                } else {
                    // TODO log unexpected module endpoint
                }
                break;
            } default: {
                // TODO log unexpected endpoint
                break;
            }
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
    
    public void registerTopic(String endpoint) {        
        if (!endpoints.contains(endpoint)) {
            endpoints.put(endpoint, new ArrayList<>());
            // TODO log topic <topic> for module <module> succesfully registered
        } else {
            // TODO log topic <topic> for module <module> already exist
        }
    }
    
    public void registerTopic(String moduleName, String topic) {
        String endpoint = moduleName + "/" + topic;
        
        registerTopic(endpoint);
    }
    
    public void publish(String moduleName, String topic, String message) {
        String endpoint = moduleName + "/" + topic;
        
        if (endpoints.contains(endpoint)) {
            ArrayList<WebSocket> connections = endpoints.get(endpoint);

            for (WebSocket connection : connections) {
                connection.send(message);
            }
            
            // TODO log message send to <n> connections
        } else {
            // TODO log unknown endpoint <endpoint>
        }
    }
    
}
