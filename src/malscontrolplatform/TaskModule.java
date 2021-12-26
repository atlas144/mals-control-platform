package malscontrolplatform;

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
    public abstract void run();
    
}