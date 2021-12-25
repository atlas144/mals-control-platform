package malscontrolplatform;

import java.util.concurrent.PriorityBlockingQueue;
import malscontrolplatform.broker.Broker;
import malscontrolplatform.broker.Message;

/**
 *
 * @author atlas144
 */
public abstract class TaskModule extends Thread {
    
    protected final Broker broker;
    protected final PriorityBlockingQueue<Message> messageQueue;
        
    public TaskModule(Broker broker) {
        this.broker = broker;
        messageQueue = new PriorityBlockingQueue<>();
    }
    
    public void acceptMessage(Message message) {
        messageQueue.add(message);
    }
    
    @Override
    public abstract void run();
    
}