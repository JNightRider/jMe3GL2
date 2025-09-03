/*
BSD 3-Clause License

Copyright (c) 2023-2025, Night Rider (Wilson)

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.je3gl.plugins.input;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;

/**
 * An object of class <code>AbstractKeyboardInputHandler</code> is responsible
 * for generating a template for keyboard input by the user.
 * <p>
 * <b>jMonkeyEngine3</b> provides 2 inputs, by means of action or analog, each
 * having different ways of interacting.
 * <p>
 * The action input is activated only once, while the analog input remains active
 * until the input exits.
 * 
 * @author wil
 * @version 1.0.5
 * @since 2.0.0
 */
public abstract class AbstractKeyboardInputHandler extends AbstractInputHandler implements InputHandler {
    
    /**
     * Internal key in charge of ticket management.
     */
    private final class CustomKeyListener implements ActionListener, AnalogListener {
        /*
         * (non-Javadoc)
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

        /*
         * (non-Javadoc)
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

    /** Data inputs and outputs. */
    private final CustomKeyListener keyAdapter;    
    /** Input type. */
    protected InputHandlerType type;    
    /** Keys to the inputs. */
    protected Key[] keys;

    /**
     * Instantiate a new object of the class <code>AbstractKeyboardInputHandler</code>.
     * @param type input type
     * @param keys key of the inputs (keyboard inputs)
     */
    public AbstractKeyboardInputHandler(InputHandlerType type, Key... keys) {
        this.keyAdapter = new CustomKeyListener();
        this.type = type;
        this.keys = keys;
    }
    
    /**
     * Method in charge of managing whether a key is an input.
     * @param name key name
     * @return status
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

    /*
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

    /*
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
     * It is activated when an input is pressed.
     */
    protected abstract void onKeyPressed();

    /**
     * It is activated when an input is released.
     */
    protected abstract void onKeyReleased();
}
