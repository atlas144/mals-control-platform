package malscontrolplatform;

import java.util.concurrent.PriorityBlockingQueue;
import malscontrolplatform.intercommunicationmodule.IntercommunicationModule;
import malscontrolplatform.intercommunicationmodule.Message;

/**
 *
 * @author atlas144
 */
public abstract class TaskModule extends Thread {
    
    private final String moduleName;
    protected final IntercommunicationModule intercommunicationModule;
    protected final PriorityBlockingQueue<Message> messageQueue;
        
    public TaskModule(String moduleName, IntercommunicationModule intercommunicationModule) {
        this.moduleName = moduleName;
        this.intercommunicationModule = intercommunicationModule;
        messageQueue = new PriorityBlockingQueue<>();
    }
    
    public void acceptMessage(Message message) {
        messageQueue.add(message);
    }
    
    @Override
    public abstract void run();

    public String getModuleName() {
        return moduleName;
    }
    
}