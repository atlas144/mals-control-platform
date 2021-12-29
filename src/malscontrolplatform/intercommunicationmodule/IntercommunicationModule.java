package malscontrolplatform.intercommunicationmodule;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import malscontrolplatform.TaskModule;
import org.tinylog.Logger;

/**
 * A module that acts as a broker for transferring messages between other 
 * components of the platform.
 * It allows the component to subscribe to a topic and then forward messages 
 * with that topic to it.
 *
 * @author atlas144
 */
public class IntercommunicationModule extends Thread {
    
    private final PriorityBlockingQueue<Message> messageQueue;
    private final ConcurrentHashMap<String, HashSet<TaskModule>> subscribtions;
    
    
    /**
     * It sends messages to subscribers of corresponding topic.
     * 
     * @param message message to be sent
     */
    private void sendMessage(Message message) {
        int counter = 0;
        
        for (TaskModule module : subscribtions.get(message.getTopic())) {
            module.acceptMessage(message);
            Logger.trace("Message '{}' successfully sent to the '{}' task module", message, module.getModuleName());
            counter++;
        }
        
        Logger.debug("Message '{}' successfully sent to {} modules", message, counter);
    }
    
    /**
     * Creates module.
     */
    public IntercommunicationModule() {
        messageQueue = new PriorityBlockingQueue<>();
        subscribtions = new ConcurrentHashMap<>();
        Logger.info("Intercommunication module successfully created");
    }
    
    /**
     * Passes the message to the broker so it can send it to its subscribers.
     * 
     * @param topic topic of the message
     * @param payload message content
     * @param priority message priority
     */
    public void publish(String topic, String payload, Priority priority) {
        messageQueue.add(new Message(topic, payload, priority));
        Logger.debug("Message successfully published\nTopic: {}\nPayload: {}\nPriority: {}", topic, payload, priority.toString());
    }
    
    /**
     * Sets subscription to a given topic.
     * 
     * @param topic topic to be subscribed to
     * @param subscriber module which is subscribing
     */
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
    
    /**
     * Ends subscription to a given topic.
     * 
     * @param topic topic to be unsubscribed to
     * @param subscriber task module which is canceling the subscribtion
     */
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
    
    /**
     * Broker action code. Sends messages from queue to the subscribers. 
     */
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
