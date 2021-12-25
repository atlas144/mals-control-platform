package malscontrolplatform.broker;

/**
 *
 * @author atlas144
 */
public class Message implements Comparable {
    
    private final String topic;
    private final String payload;
    private final Priority priority;

    public Message(String topic, String payload, Priority priority) {
        this.topic = topic;
        this.payload = payload;
        this.priority = priority;
    }
    
    @Override
    public int compareTo(Object otherMessage) {
        byte thisPriorityValue = priority.getValue();
        byte otherPriorityValue = ((Message) otherMessage).getPriority().getValue();
        
        if (thisPriorityValue > otherPriorityValue) {
            return 1;
        } else if (thisPriorityValue < otherPriorityValue) {
            return -1;
        } else {
            return 0;
        }
    }

    public String getTopic() {
        return topic;
    }

    public String getPayload() {
        return payload;
    }

    public Priority getPriority() {
        return priority;
    }
    
}
