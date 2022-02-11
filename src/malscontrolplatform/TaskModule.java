// SPDX-License-Identifier: LGPL-2.1-only

package malscontrolplatform;

import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final AtomicBoolean running;
    protected final PriorityBlockingQueue<Message> messageQueue;
    protected IntercommunicationModule intercommunicationModule;
    protected WebModule webModule;
    
    /**
     * There should be a code that should be executed only once,
     * at the beginning of the module action.
     */
    protected abstract void setup();
    
    /**
     * Here should be the code that is executed in a loop for the whole running
     * time of the module.
     */
    protected abstract void loop();
        
    /**
     * Creates instance of the task module.
     * 
     * @param moduleName name of the module (must be unique)
     */
    public TaskModule(String moduleName) {
        this.moduleName = moduleName;
        running = new AtomicBoolean(true);
        messageQueue = new PriorityBlockingQueue<>();
        Logger.info("Task module '{}' successfully initialized", moduleName);
    }
    
    /**
     * Sets Intercommunication module.
     * 
     * @param intercommunicationModule module for communication between platform
     * components
     */
    public void setIntercommunicationModule(IntercommunicationModule intercommunicationModule) {
        this.intercommunicationModule = intercommunicationModule;
    }
    
    /**
     * Sets Web module.
     * 
     * @param webModule server for communication with user application
     */
    public void setWebModule(WebModule webModule) {
        this.webModule = webModule;
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
    
    public void kill() {
        running.set(false);
        Logger.info("Module killed successfully");
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
    
    @Override
    public void run() {
        setup();
        
        while (running.get()) {            
            loop();
        }
        
        Logger.info("Module stopped successfully");
    }
    
}