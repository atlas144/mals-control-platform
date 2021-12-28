package malscontrolplatform;

import java.util.concurrent.ConcurrentHashMap;
import malscontrolplatform.intercommunicationmodule.IntercommunicationModule;
import malscontrolplatform.webmodule.WebModule;
import org.tinylog.Logger;

/**
 *
 * @author atlas144
 */
public class MalsControlPlatform {

    private final ConcurrentHashMap<String, TaskModule> taskModules;
    private final IntercommunicationModule intercommunicationModule;
    private final WebModule webModule;
    
    public MalsControlPlatform(String host, short port) {
        taskModules = new ConcurrentHashMap<>();
        intercommunicationModule = new IntercommunicationModule();
        webModule = new WebModule(host, port);
    }
    
    public void registerModule(TaskModule newModule) {
        taskModules.put(newModule.getModuleName(), newModule);
        Logger.info("Task module '{}' successfully registered", newModule.getModuleName());
    }
    
    public void start() {
        intercommunicationModule.start();
        Logger.info("Intercommunication module successfully started");
        webModule.run();
        Logger.info("Web module successfully started");
        
        for (TaskModule taskModule : taskModules.values()) {
            taskModule.start();
            Logger.info("Task module '{}' successfully started", taskModule.getModuleName());
        }
    }
    
}
