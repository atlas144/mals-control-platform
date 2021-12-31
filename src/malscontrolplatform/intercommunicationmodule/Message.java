// SPDX-License-Identifier: LGPL-2.1-only

package malscontrolplatform.intercommunicationmodule;

import org.tinylog.Logger;

/**
 * Message object used to transfer information between platform components.
 *
 * @author atlas144
 */
public class Message implements Comparable {
    
    private final String topic;
    private final String payload;
    private final Priority priority;

    /**
     * Creates message object.
     * 
     * @param topic topic of the message. Used to determine acceptors of the 
     * message.
     * @param payload content of the message
     * @param priority priority of the message. Determines the order in 
     * the message queue.
     */
    public Message(String topic, String payload, Priority priority) {
        this.topic = topic;
        this.payload = payload;
        this.priority = priority;
        Logger.debug("Mesage successfully initialized:\nTopic: {}\nPayload: {}\nPriority: {}", topic, payload, priority.toString());
    }
    
    /**
     * Compares both messages by priority.
     * 
     * @param object message to be compared
     * @return if <i>this priority</i> is greater than the other one, returns 
     * <code>1</code>. If it is smaller, returns <code>-1</code> and if they are
     * equal, returns <code>0</code>.
     */
    @Override
    public int compareTo(Object object) {
        byte thisPriorityValue = priority.getValue();
        byte otherPriorityValue = ((Message) object).getPriority().getValue();
        
        if (thisPriorityValue > otherPriorityValue) {
            return 1;
        } else if (thisPriorityValue < otherPriorityValue) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Returns topic of the message.
     * 
     * @return message topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Returns content of the message.
     * 
     * @return message payload
     */
    public String getPayload() {
        return payload;
    }

    /**
     * Returns priority of the message.
     * 
     * @return message priority
     */
    public Priority getPriority() {
        return priority;
    }
    
}
