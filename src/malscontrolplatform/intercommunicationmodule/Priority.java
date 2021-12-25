package malscontrolplatform.broker;

/**
 *
 * @author atlas144
 */
public enum Priority {
    
    UNIMPORTANT((byte) 0),
    NORMAL((byte) 1),
    IMPORTANT((byte) 2),
    CRITICAL((byte) 3);
    
    private final byte value;

    private Priority(byte value) {
        this.value = value;
    }
    
    public byte getValue() {
        return value;
    }
    
}
