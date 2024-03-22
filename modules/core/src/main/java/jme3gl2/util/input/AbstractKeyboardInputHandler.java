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
package jme3gl2.util.input;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;

/**
 * Un objeto de la clase <code>AbstractKeyboardInputHandler</code> se encarga
 * de generar una plantilla para las entradas por teclado por parte del usuario.
 * <p>
 * <b>jMonkeyEngine3</b> proporciona 2 entradas, por medio de acción o analogo,
 * cada uno tien formas diferentes de interactuar.
 * </p>
 * <p>
 * La entrada por acción se activa una sola vez, en cambio la analoga se mantien
 * activa hasta que la entrada sale.
 * </p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * @since 2.0.0
 */
public abstract class AbstractKeyboardInputHandler extends AbstractInputHandler implements InputHandler {
    
    /**
     * Clave interna encargado de gestionar las entradas.
     */
    private final 
    class CustomKeyListener implements ActionListener,
                                        AnalogListener {
        
        /**
         * (non-JavaDoc)
         * @param name string
         * @param isPressed boolean
         * @param tpf float
         * @see ActionListener#onAction(java.lang.String, boolean, float) 
         */
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (type != InputHandlerType.Action) {
                return;
            }
            
            if (isKeyMatch(name)) {
                if (isEnabled() && !isDependentBehaviorActive()) {
                    if (isPressed) {
                        onKeyPressed();
                    } else {
                        onKeyReleased();
                    }
                }
            }
        }

        /**
         * (non-JavaDoc)
         * @param name string
         * @param value float
         * @param tpf float
         * @see AnalogListener#onAnalog(java.lang.String, float, float) 
         */
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (type != InputHandlerType.Analog) {
                return;
            }
            
            if (isKeyMatch(name)) {
                if (isEnabled() && !isDependentBehaviorActive()) {
                    onKeyPressed();
                }
            }
        }
    }

    /** Entradas y salidas de datos. */
    private final CustomKeyListener keyAdapter;
    
    /** Tipo de entrada. */
    protected InputHandlerType type;
    
    /** Claves de las entradas. */
    protected Key[] keys;

    /**
     * Instancie un nuevo objeto de la clase 
     * <code>AbstractKeyboardInputHandler</code>.
     * 
     * @param type tipo de entrada.
     * @param keys clave de las entradas(las entradas por teclado).
     */
    public AbstractKeyboardInputHandler(InputHandlerType type, Key... keys) {
        this.keyAdapter = new CustomKeyListener();
        this.type = type;
        this.keys = keys;
    }
    
    /**
     * Método encargado de gestionar si una clave es una entrada.
     * @param name nombre clave.
     * @return estado.
     */
    private boolean isKeyMatch(String name) {
        if (this.keys == null) {
            return false;
        }        
        for (Key key : this.keys) {
            if (key.getKeyName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * (non-JavaDoc)
     * @see AbstractInputHandler#install() 
     */
    @Override
    public void install() {
        if (!isInitialized()) {
            return;
        }
        
        InputManager im = getInputManager();
        String[] mapp = new String[this.keys.length];
        for (int i = 0; i < this.keys.length; i++) {
            Key key = this.keys[i];
            mapp[i] = key.getKeyName();
            
            im.addMapping(key.getKeyName(), key);
        }
        im.addListener(keyAdapter, mapp);
    }

    /**
     * (non-JavaDoc)
     * @see AbstractKeyboardInputHandler#uninstall() 
     */
    @Override
    public void uninstall() {
        if (!isInitialized()) {
            return;
        }
        
        InputManager im = getInputManager();
        for (final Key key : this.keys) {
            if (im.hasMapping(key.getKeyName())) {
                im.deleteTrigger(key.getKeyName(), key);
            }
        }
        im.removeListener(keyAdapter);
    }

    /**
     * Se activa cuando una entrada es presionada.
     */
    protected abstract void onKeyPressed();

    /**
     * Se activa cuando una entrada es liberado
     */
    protected abstract void onKeyReleased();
}
