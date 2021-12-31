// SPDX-License-Identifier: LGPL-2.1-only

package malscontrolplatform.intercommunicationmodule;

/**
 * Priority of the message.
 * 
 * 0 - UNIMPORTANT - information that doesn't matter too much (e.g. statistical
 * data)
 * 1 - NORMAL - common information to be processed (e.g. system running
 * information)
 * 2 - IMPORTANT - important information requiring priority attention
 * (e.g. CDS information)
 * 3 - CRITICAL - critical information that requires immediate processing 
 * (e.g. collision information from contact sensors)
 *
 * @author atlas144
 */
public enum Priority {
    
    /**
     * Information that doesn't matter too much (e.g. statistical data)
     */
    UNIMPORTANT((byte) 0),
    /**
     * Common information to be processed (e.g. system running information)
     */
    NORMAL((byte) 1),
    /**
     * Important information requiring priority attention (e.g. CDS information)
     */
    IMPORTANT((byte) 2),
    /**
     * Critical information that requires immediate processing (e.g. collision 
     * information from contact sensors)
     */
    CRITICAL((byte) 3);
    
    private final byte value;

    private Priority(byte value) {
        this.value = value;
    }
    
    /**
     * Returns numerical value of the priority.
     * 
     * @return numerical priority value
     */
    public byte getValue() {
        return value;
    }
    
}
