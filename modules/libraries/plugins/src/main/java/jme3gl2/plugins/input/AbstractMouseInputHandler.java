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
package jme3gl2.plugins.input;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Vector2f;

/**
 * A <code>AbstractMouseInputHandler</code> object is responsible for generating
 * a base template for mouse inputs.
 * 
 * @author wil
 * @version 1.0.5
 * @since 2.0.0
 */
public abstract class AbstractMouseInputHandler extends AbstractInputHandler implements InputHandler {

    /**
     * A custom mouse adapter to track mouse drag events.
     */
    private final class CustomMouseAdapter implements ActionListener, AnalogListener {

        /*
         * (non-Javadoc)
         * @see ActionListener#onAction(java.lang.String, boolean, float) 
         */
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isMouseMatch(name) && isInitialized()) {
                if (isPressed) {
                    // Store mouse click position for later use
                    dragCurrent = getInputManager().getCursorPosition().clone();
                    dragStart   = dragCurrent;
                    
                    if (isEnabled() && !isDependentBehaviorActive()) {
                        onMousePressed(dragStart);
                    }
                } else {
                    dragCurrent = null;
                    dragStart   = null;
                    if (isEnabled() && !isDependentBehaviorActive()) {
                    	onMouseRelease();
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
            if (isMouseMatch(name) && isInitialized()) {
                dragCurrent = getInputManager().getCursorPosition().clone();
                if (isEnabled() && !isDependentBehaviorActive() &&
                        dragStart != null && dragStart.distance(dragCurrent) != 0.0F) {
                    onMouseDrag(dragStart, dragCurrent);
		}
                
                double wheelRotation = value;
                if (isEnabled() && !isDependentBehaviorActive()) {
                    onMouseWheel(wheelRotation);
		}
            }
        }
    }

    /** Data inputs and outputs. */
    private final CustomMouseAdapter mouseAdapter;    
    /** Mouse input. */
    private final MouseTrigger mouseTrigger;
    
    /** Current mouse cursor position. */
    private Vector2f dragCurrent;    
    /** Initial mouse position. */
    private Vector2f dragStart;

    /**
     * Instantiate a new object of the class <code>AbstractMouseInputHandler</code>.
     * @param mouseTrigger mouse input
     */
    public AbstractMouseInputHandler(MouseTrigger mouseTrigger) {
        this.mouseAdapter = new CustomMouseAdapter();
        this.mouseTrigger = mouseTrigger;
    }

    /**
     * Method in charge of managing whether a key is an input.
     * @param name key name
     * @return status
     */
    private boolean isMouseMatch(String name) {
        if (mouseTrigger == null) {
            return false;
        }
        return mouseTrigger.getInputName().equals(name);
    }
    
    /*
     * (non-Javadoc)
     * @see AbstractInputHandler#install() 
     */
    @Override
    public void install() {
        if (!isInitialized()) {
            return;
        }
        
        InputManager im = getInputManager();
        im.addMapping(mouseTrigger.getInputName(), mouseTrigger);
        im.addListener(mouseAdapter, mouseTrigger.getInputName());
    }

    /*
     * (non-Javadoc)
     * @see AbstractKeyboardInputHandler#uninstall() 
     */
    @Override
    public void uninstall() {
        if (!isInitialized()) {
            return;
        }
        
        InputManager im = getInputManager();
        if (im.hasMapping(mouseTrigger.getInputName())) {
            im.deleteTrigger(mouseTrigger.getInputName(), mouseTrigger);
        }
        im.removeListener(mouseAdapter);
    }
    
    /*
     * (non-Javadoc)
     * @see AbstractInputHandler#setEnabled(boolean) 
     */
    @Override
    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
        if (!flag) {
            this.dragCurrent = null;
            this.dragStart   = null;
        }
    }
    
    /**
     * Mouse button pressed.
     * @param point cursor position
     */
    protected abstract void onMousePressed(Vector2f point);
    
    /**
     * Mouse dragged by the user.
     * @param start initial position of the cursor
     * @param current current cursor position
     */
    protected abstract void onMouseDrag(Vector2f start, Vector2f current);
    
    /**
     * Mouse button released.
     */
    protected abstract void onMouseRelease();
    
    /**
     * Mouse wheel in rotation.
     * @param rotation value
     */
    protected abstract void onMouseWheel(double rotation);
}
