// SPDX-License-Identifier: LGPL-2.1-only

package malscontrolplatform;

import java.util.concurrent.ConcurrentHashMap;
import malscontrolplatform.intercommunicationmodule.IntercommunicationModule;
import malscontrolplatform.webmodule.WebModule;
import org.tinylog.Logger;

/**
 * Platform for controlling of robbotic system. It provides a connection between
 * the system modules and communication with the user application.
 *
 * @author atlas144
 */
public class MalsControlPlatform {

    private final ConcurrentHashMap<String, TaskModule> taskModules;
    private final IntercommunicationModule intercommunicationModule;
    private final WebModule webModule;
    
    /**
     * Creates platform instance. And also module for intercommunication and WebSocket
     * server.
     * 
     * @param host address on which the WebSocket server should start
     * @param port port on which the WebSocket server should start
     */
    public MalsControlPlatform(String host, short port) {
        taskModules = new ConcurrentHashMap<>();
        intercommunicationModule = new IntercommunicationModule();
        webModule = new WebModule(host, port);
    }
    
    /**
     * Registers new module in the platform.
     * 
     * @param newModule module to be registered
     */
    public void registerModule(TaskModule newModule) {
        newModule.setIntercommunicationModule(intercommunicationModule);
        newModule.setWebModule(webModule);
        
        taskModules.put(newModule.getModuleName(), newModule);
        Logger.info("Task module '{}' successfully registered", newModule.getModuleName());
    }
    
    /**
     * Starts all components and the platform itself.
     */
    public void start() {
        intercommunicationModule.start();
        Logger.info("Intercommunication module successfully started");
        webModule.start();
        Logger.info("Web module successfully started");
        
        for (TaskModule taskModule : taskModules.values()) {
            taskModule.start();
            Logger.info("Task module '{}' successfully started", taskModule.getModuleName());
        }
    }
    
}
