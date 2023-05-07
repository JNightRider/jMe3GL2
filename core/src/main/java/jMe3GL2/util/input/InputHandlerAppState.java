/* Copyright (c) 2009-2023 jMonkeyEngine.
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
package jMe3GL2.util.input;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wil
 * @version 1.0-SNAPSHOT
 * @since 2.0.0
 */
public class InputHandlerAppState extends AbstractAppState {

    private final List<InputHandler> inputHandlers 
            = new ArrayList<>();
    
    private InputManager inputManager;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        for (final InputHandler handler : this.inputHandlers) {
            if (handler == null) {
                continue;
            }
            if (!handler.isInitialized()) {
                handler.initialize(app.getInputManager());
            }
        }
        this.inputManager = app.getInputManager();
        super.initialize(stateManager, app);
    }
    
    public void addInputHandler(InputHandler inputHandler) {
        if (!inputHandler.isInitialized()) {
            inputHandler.initialize(inputManager);
        }
        this.inputHandlers.add(inputHandler);
    }
    
    public InputHandler getInputHandler(int index) {
        return this.inputHandlers.get(index);
    }
    
    public void removeInputHandler(InputHandler inputHandler) {
        if (inputHandler.isInitialized()) {
            inputHandler.initialize(null);
        }
        this.inputHandlers.remove(inputHandler);
    }
    
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
