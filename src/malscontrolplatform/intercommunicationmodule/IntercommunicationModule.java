package malscontrolplatform.intercommunicationmodule;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import malscontrolplatform.TaskModule;

/**
 *
 * @author atlas144
 */
public class IntercommunicationModule extends Thread {
    
    private final PriorityBlockingQueue<Message> messageQueue;
    private final ConcurrentHashMap<String, HashSet<TaskModule>> subscribtions;
    
    private void sendMessage(Message message) {
        for (TaskModule module : subscribtions.get(message.getTopic())) {
            module.acceptMessage(message);
        }
    }
    
    public IntercommunicationModule() {
        messageQueue = new PriorityBlockingQueue<>();
        subscribtions = new ConcurrentHashMap<>();
    }
    
    public void publish(String topic, String Payload, Priority priority) {
        messageQueue.add(new Message(topic, Payload, priority));
    }
    
    public void subscribe(String topic, TaskModule subscriber) {
        if (subscribtions.contains(topic)) {
            synchronized(subscribtions) {
                subscribtions.get(topic).add(subscriber);
            }
        } else {
            HashSet subscriberList = new HashSet<>();
            
            subscriberList.add(subscriber);
            synchronized(subscribtions) {
                subscribtions.put(topic, subscriberList);
            }
        }
    }
    
    public void unsubscribe(String topic, TaskModule subscriber) {
        if (subscribtions.contains(topic)) {
            HashSet<TaskModule> subscriberList = subscribtions.get(topic);
            
            if (subscriberList.contains(subscriber)) {
                synchronized (subscriberList) {
                    subscriberList.remove(subscriber);
                }
            } else {
                // TODO log unknown subscriber
            }
        } else {
            // TODO log unknown topic
        }
    }
    
    @Override
    public void run() {
        while (true) {            
            try {
                Message message = messageQueue.take();
                
                sendMessage(message);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }
    
}
