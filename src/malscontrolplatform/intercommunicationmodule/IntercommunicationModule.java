package malscontrolplatform.intercommunicationmodule;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import malscontrolplatform.TaskModule;
import org.tinylog.Logger;

/**
 *
 * @author atlas144
 */
public class IntercommunicationModule extends Thread {
    
    private final PriorityBlockingQueue<Message> messageQueue;
    private final ConcurrentHashMap<String, HashSet<TaskModule>> subscribtions;
    
    private void sendMessage(Message message) {
        int counter = 0;
        
        for (TaskModule module : subscribtions.get(message.getTopic())) {
            module.acceptMessage(message);
            Logger.trace("Message '{}' successfully sent to the '{}' task module", message, module.getModuleName());
            counter++;
        }
        
        Logger.debug("Message '{}' successfully sent to {} modules", message, counter);
    }
    
    public IntercommunicationModule() {
        messageQueue = new PriorityBlockingQueue<>();
        subscribtions = new ConcurrentHashMap<>();
        Logger.info("Intercommunication module successfully created");
    }
    
    public void publish(String topic, String payload, Priority priority) {
        messageQueue.add(new Message(topic, payload, priority));
        Logger.debug("Message successfully published\nTopic: {}\nPayload: {}\nPriority: {}", topic, payload, priority.toString());
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
            
            Logger.info("Topic '{}' successfully registered", topic);
        }

        Logger.info("Task module '{}' successfully subscribed to '{}' topic", subscriber.getModuleName(), topic);
    }
    
    public void unsubscribe(String topic, TaskModule subscriber) {
        if (subscribtions.contains(topic)) {
            HashSet<TaskModule> subscriberList = subscribtions.get(topic);
            
            if (subscriberList.contains(subscriber)) {
                synchronized (subscriberList) {
                    subscriberList.remove(subscriber);

                    Logger.info("Task module '{}' successfully unsubscribed '{}' topic", subscriber.getModuleName(), topic);
                }
            } else {
                Logger.warn("Task module '{}' has not been subscribing to '{}' topic", subscriber.getModuleName(), topic);
            }
        } else {
            Logger.warn("Topic '{}' in not registered topic", topic);
        }
    }
    
    @Override
    public void run() {
        while (true) {            
            try {
                Message message = messageQueue.take();
                
                sendMessage(message);
            } catch (InterruptedException ex) {
                Logger.info("Intercommunication module stopping");
                return;
            }
        }
    }
    
}
