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

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Vector2f;

/**
 * Un que objeto de la <code>AbstractMouseInputHandler</code> se encarga de
 * generar una plantilla base para las entradas del ratón.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * @since 2.0.0
 */
public abstract class AbstractMouseInputHandler extends AbstractInputHandler implements InputHandler {

    /**
     * Un adaptador de mouse personalizado para realizar un seguimiento de
     * los eventos de arrastre del mouse.
     */
    private final class CustomMouseAdapter implements ActionListener, AnalogListener {

        /**
         * (non-JavaDoc)
         * @param name string
         * @param isPressed boolean
         * @param tpf float
         * @see ActionListener#onAction(java.lang.String, boolean, float) 
         */
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isMouseMatch(name) && isInitialized()) {
                if (isPressed) {
                    // almacenar la posición del clic del mouse para usarla más tarde
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

        /**
         * (non-JavaDoc)
         * @param name string
         * @param value float
         * @param tpf float
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

    /** Entradas y salidas de datos. */
    private final CustomMouseAdapter mouseAdapter;
    
    /** Entrada del ratón. */
    private final MouseTrigger mouseTrigger;
    
    /**
     * Posición del cursor actual del ratón.
     */
    private Vector2f dragCurrent;
    
    /**
     * Posición inicial del ratón.
     */
    private Vector2f dragStart;

    /**
     * Instancie un nuevo objeto de la clase 
     * <code>AbstractMouseInputHandler</code>
     * @param mouseTrigger entrada-ratón.
     */
    public AbstractMouseInputHandler(MouseTrigger mouseTrigger) {
        this.mouseAdapter = new CustomMouseAdapter();
        this.mouseTrigger = mouseTrigger;
    }

    /**
     * Método encargado de gestionar si una clave es una entrada.
     * @param name nombre clave.
     * @return estado.
     */
    private boolean isMouseMatch(String name) {
        if (mouseTrigger == null) {
            return false;
        }
        return mouseTrigger.getInputName().equals(name);
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
        im.addMapping(mouseTrigger.getInputName(), mouseTrigger);
        im.addListener(mouseAdapter, mouseTrigger.getInputName());
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
        if (im.hasMapping(mouseTrigger.getInputName())) {
            im.deleteTrigger(mouseTrigger.getInputName(), mouseTrigger);
        }
        im.removeListener(mouseAdapter);
    }
    
    /**
     * (non-JavaDoc)
     * @param flag boolean
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
     * Botón del ratón presionado.
     * @param point posición del cursor.
     */
    protected abstract void onMousePressed(Vector2f point);
    
    /**
     * Ratón a arrastrado por el usuario.
     * @param start posición inidical del cursor.
     * @param current posición actual del cursor.
     */
    protected abstract void onMouseDrag(Vector2f start, Vector2f current);
    
    /**
     * Botón del ratón liberado.
     */
    protected abstract void onMouseRelease();
    
    /**
     * Ruda del ratón en rotación.
     * @param rotation valor.
     */
    protected abstract void onMouseWheel(double rotation);
}
