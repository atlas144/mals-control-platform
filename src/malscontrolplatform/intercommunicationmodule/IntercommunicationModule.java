package malscontrolplatform.broker;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import malscontrolplatform.TaskModule;

/**
 *
 * @author atlas144
 */
public class Broker extends Thread {
    
    private final PriorityBlockingQueue<Message> messageQueue;
    private final ConcurrentHashMap<String, HashSet<TaskModule>> subscribtions;
    
    private void sendMessage(Message message) {
        for (TaskModule module : subscribtions.get(message.getTopic())) {
            module.acceptMessage(message);
        }
    }
    
    public Broker() {
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
    
    public void unSubscribe(String topic, TaskModule subscriber) {
        if (subscribtions.contains(topic)) {
            if (subscribtions.contains(subscriber)) {
                subscribtions.get(topic).add(subscriber);
            }
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
