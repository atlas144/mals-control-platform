package malscontrolplatform;

import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;
import malscontrolplatform.intercommunicationmodule.IntercommunicationModule;
import malscontrolplatform.intercommunicationmodule.Message;
import malscontrolplatform.webmodule.WebModule;

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
    }

    public String getModuleName() {
        return moduleName;
    }
    
    public void acceptMessage(Message message) {
        messageQueue.add(message);
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