package malscontrolplatform;

import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;
import malscontrolplatform.intercommunicationmodule.IntercommunicationModule;
import malscontrolplatform.intercommunicationmodule.Message;
import malscontrolplatform.webmodule.WebModule;
import org.tinylog.Logger;

/**
 * Template for subroutines representing individual modules of the system.
 * 
 * User extends the template and adds the code for the subsystem activity 
 * to the <code>run()</code> method.
 *
 * @author atlas144
 */
public abstract class TaskModule extends Thread {
    
    private final String moduleName;
    protected final IntercommunicationModule intercommunicationModule;
    protected final WebModule webModule;
    protected final PriorityBlockingQueue<Message> messageQueue;
    
    /**
     * Creates instance of the task module.
     * 
     * @param moduleName name of the module (must be unique)
     * @param intercommunicationModule module for communication between platform components
     * @param webModule server for communication with user application
     */
    public TaskModule(String moduleName, IntercommunicationModule intercommunicationModule, WebModule webModule) {
        this.moduleName = moduleName;
        this.intercommunicationModule = intercommunicationModule;
        this.webModule = webModule;
        messageQueue = new PriorityBlockingQueue<>();
        Logger.info("Task module '{}' successfully initialized", moduleName);
    }

    /**
     * Returns name of the task module.
     * 
     * @return module name
     */
    public String getModuleName() {
        return moduleName;
    }
    
    /**
     * Inserts message to the message queue of the task module.
     * 
     * @param message message to be inserted
     */
    public void acceptMessage(Message message) {
        messageQueue.add(message);
        Logger.debug("Message successfully accepted: {}", message);
    }

    /**
     * Counts hash code from module name.
     * 
     * @return hash code of the module
     */
    @Override
    public int hashCode() {
        return 59 * 5 + Objects.hashCode(this.moduleName);
    }
    
    /**
     * Compares names of both modules. If they are same, both modules are same.
     * 
     * @param object module to be compared
     * @return <code>true</code> if both module names are equal
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
         
        TaskModule module = (TaskModule) object;
        
        return moduleName.equals(module.getModuleName());
    }
    
    /**
     * Here should be inserted action code for the task module.
     */
    @Override
    public abstract void run();
    
}