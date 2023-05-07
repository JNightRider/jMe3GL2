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

/**
 * Clase <code>BooleanStateKeyboardInputHandler</code> que exitiene de la clase
 * padre {@link AbstractKeyboardInputHandler} en donde gestiona el estado 
 * lógico de una entrada, activo o no.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * @since 2.0.0
 */
public class BooleanStateKeyboardInputHandler extends AbstractKeyboardInputHandler {

    /** Si el estado de la tecla está activo (presionado)  */
    private boolean active;

    /** {@code true} si se ha manejado el estado activo */
    private boolean hasBeenHandled;

    /**
     * Instancie un nuevo objeto de la clase 
     * <code>BooleanStateKeyboardInputHandler</code>.
     * 
     * @param type tipo de entrada.
     * @param keys clave de las entradas(las entradas por teclado).
     */
    public BooleanStateKeyboardInputHandler(InputHandlerType type, Key... keys) {
        super(type, keys);
        this.active = false;
        this.hasBeenHandled = false;
    }

    /**
     * (non-JavaDoc)
     * @see AbstractKeyboardInputHandler#onKeyPressed() 
     */
    @Override
    protected void onKeyPressed() {		
	// salvar el antiguo estado
	boolean active0 = this.active;
	
	// establecer el estado en activo
	this.active = true;
	
	// si el estado pasó de inactivo a activo
        // marca que necesita ser manejado
	if (!active0) {
            this.hasBeenHandled = false;
	}
    }

    /**
     * (non-JavaDoc)
     * @see AbstractKeyboardInputHandler#onKeyReleased() 
     */
    @Override
    protected void onKeyReleased() {
        this.active = false;
    }

    /**
     * (non-JavaDoc)
     * @see AbstractKeyboardInputHandler#isActive() 
     * @return boolean
     */
    @Override
    public boolean isActive() {
        return this.active;
    }
    
    /**
     * Método encargado de gestionar si la entrada esta activa y no ha sido
     * manejado.
     * @return boolean.
     */
    public boolean isActiveButNotHandled() {
    	if (this.hasBeenHandled) {
            return false;
        }
    	return this.active;
    }
	
    /**
     * Establece el estado de la entrada.
     * @param hasBeenHandled estado.
     */
    public void setHasBeenHandled(boolean hasBeenHandled) {
    	this.hasBeenHandled = hasBeenHandled;
    }
}
