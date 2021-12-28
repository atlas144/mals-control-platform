package malscontrolplatform;

import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;
import malscontrolplatform.intercommunicationmodule.IntercommunicationModule;
import malscontrolplatform.intercommunicationmodule.Message;
import malscontrolplatform.webmodule.WebModule;
import org.tinylog.Logger;

/**
 *
 * @author atlas144
 */
public abstract class TaskModule extends Thread {
    
    private final String moduleName;
    protected final IntercommunicationModule intercommunicationModule;
    protected final WebModule webModule;
    protected final PriorityBlockingQueue<Message> messageQueue;
        
    public TaskModule(String moduleName, IntercommunicationModule intercommunicationModule, WebModule webModule) {
        this.moduleName = moduleName;
        this.intercommunicationModule = intercommunicationModule;
        this.webModule = webModule;
        messageQueue = new PriorityBlockingQueue<>();
        Logger.info("Task module '{}' successfully initialized", moduleName);
    }

    public String getModuleName() {
        return moduleName;
    }
    
    public void acceptMessage(Message message) {
        messageQueue.add(message);
        Logger.debug("Message successfully accepted: {}", message);
    }

    @Override
    public int hashCode() {
        return 59 * 5 + Objects.hashCode(this.moduleName);
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
         
        TaskModule module = (TaskModule) object;
        
        return moduleName.equals(module.getModuleName());
    }
    
    @Override
    public abstract void run();
    
}