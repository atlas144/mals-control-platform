package malscontrolplatform;

import java.util.concurrent.ConcurrentHashMap;
import malscontrolplatform.intercommunicationmodule.IntercommunicationModule;
import malscontrolplatform.webmodule.WebModule;

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
    }
    
    public void start() {
        intercommunicationModule.start();
        webModule.run();
        
        for (TaskModule taskModule : taskModules.values()) {
            taskModule.start();
        }
    }
    
}
