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
package jme3gl2.plugins.input;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Un objeto de la clase <code>InputHandlerAppState</code> se encarga de 
 * administrar todas las entradas a tarvez de la interfaz {@link InputHandler}
 * que se generen.
 * <p>
 * Se encarga de inicializarlas o destruirlas.
 * </p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * @since 2.0.0
 */
@SuppressWarnings(value = {"unchecked"})
public class InputHandlerAppState extends AbstractAppState {

    /**
     * Listas de entradas entrantes.
     */
    private final List<InputHandler> inputHandlers 
            = new ArrayList<>();
    
    /** Administrador de entradas {@code jme3}. */
    private InputManager inputManager;

    /**
     * (non-JavaDoc)
     * @param stateManager AppStateManager
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
     * Método encargado de agregar e inicializar una entrada que implemente la
     * interfaz {@link InputHandler}.
     * 
     * @param <T> tipo de entrada.
     * @param inputHandler nueva entrada a registrar.
     * @return entrada registrada.
     */
    public <T extends InputHandler> T addInputHandler(T inputHandler) {
        if (!inputHandler.isInitialized()) {
            inputHandler.initialize(inputManager);
        }
        this.inputHandlers.add(inputHandler);
        return inputHandler;
    }
    
    /**
     * Devuelve el primer {@link InputHandler} que es una instancia de subclase 
     * de la clase especificada.
     * 
     * @param <T> el tipo deseado de {@link InputHandler}
     * @param inputClass clase del tipo deseado.
     * @return Primer estado adjunto que es una instancia de {@code inputClass}.
     */
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
     * Devuelve una entrada a traves de un índice.
     * @param <T> tipo de entrada.
     * @param index índice de la entrada.
     * @return entrada registrada.
     */
    public <T extends InputHandler> T getInputHandler(int index) {
        return (T) this.inputHandlers.get(index);
    }
    
    /**
     * Elimina una entrada.
     * @param <T> tipo de entrada.
     * @param inputHandler entrada a eliminar.
     * @return entrada eliminada.
     */
    public <T extends InputHandler> T removeInputHandler(T inputHandler) {
        if (inputHandler.isInitialized()) {
            inputHandler.initialize(null);
        }
        this.inputHandlers.remove(inputHandler);
        return inputHandler;
    }
    
    /**
     * (non-JavaDoc)
     * @param enabled boolean
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
    
    /**
     * (non-JavaDoc)
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
