/* Copyright (c) 2009-2024 jMonkeyEngine.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jegl.plugins.input;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;

import java.util.ArrayList;
import java.util.List;

/**
 * An object of class <code>InputHandlerAppState</code> is in charge of managing
 * all the inputs generated through the {@link InputHandler} interface.
 * <p>
 * It is responsible for initializing or destroying them.
 * 
 * @author wil
 * @version 1.0.5
 * @since 2.0.0
 */
public class InputHandlerAppState extends AbstractAppState {

    /** Lists of incoming inputs. */
    private final List<InputHandler> inputHandlers = new ArrayList<>();    
    /** Input manager {@code jme3}. */
    private InputManager inputManager;

    /*
     * (non-Javadoc)
     * @see AbstractAppState#stateAttached(com.jme3.app.state.AppStateManager) 
     */
    @Override
    public void stateAttached(AppStateManager stateManager) {
        Application app = stateManager.getApplication();
        for (final InputHandler handler : this.inputHandlers) {
            if (handler == null) {
                continue;
            }
            if (!handler.isInitialized()) {
                handler.initialize(app.getInputManager());
            }
        }
        this.inputManager = app.getInputManager();
    }
    
    /**
     * Method in charge of adding and initializing an input that implements the
     * interface. {@link InputHandler}.
     * 
     * @param <T> input type
     * @param inputHandler new input to be registered
     * @return registered input
     */
    public <T extends InputHandler> T addInputHandler(T inputHandler) {
        if (!inputHandler.isInitialized()) {
            inputHandler.initialize(inputManager);
        }
        this.inputHandlers.add(inputHandler);
        return inputHandler;
    }
    
    /**
     * Returns the first {@link InputHandler} which is a subclass instance of
     * the specified class.
     * 
     * @param <T> the desired type of {@link InputHandler}
     * @param inputClass class of the desired type
     * @return first state attached which is an instance of {@code inputClass}
     */
    @SuppressWarnings("unchecked")
    public <T extends InputHandler> T getInputHandler(Class<T> inputClass) {
        synchronized (inputHandlers) {
            for (InputHandler handler : inputHandlers) {
                if (inputClass.isAssignableFrom(handler.getClass())) {
                    return (T) handler;
                }
            }
        }
        return null;
    }
    
    /**
     * Returns an input through an index.
     * @param <T> input type
     * @param index index of the input
     * @return registered input
     */
    @SuppressWarnings("unchecked")
    public <T extends InputHandler> T getInputHandler(int index) {
        return (T) this.inputHandlers.get(index);
    }
    
    /**
     * Deletes an input.
     * @param <T> input type
     * @param inputHandler input to be deleted
     * @return input deleted
     */
    public <T extends InputHandler> T removeInputHandler(T inputHandler) {
        if (inputHandler.isInitialized()) {
            inputHandler.initialize(null);
        }
        this.inputHandlers.remove(inputHandler);
        return inputHandler;
    }
    
    /*
     * (non-Javadoc)
     * @see AbstractAppState#setEnabled(boolean) 
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (final InputHandler handler : this.inputHandlers) {
            if (handler == null) {
                continue;
            }
            handler.setEnabled(enabled);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see AbstractAppState#cleanup() 
     */
    @Override
    public void cleanup() {
        super.cleanup();
        for (final InputHandler handler : this.inputHandlers) {
            if (handler == null) {
                continue;
            }
            handler.uninstall();
        }
        this.inputHandlers.clear();
    }
}
