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

/**
 * Class <code>BooleanStateKeyboardInputHandler</code> which exists from the
 * parent class {@link AbstractKeyboardInputHandler} where it manages the logical
 * state of an input, active or not.
 * 
 * @author wil
 * @version 1.0.5
 * @since 2.0.0
 */
public class BooleanStateKeyboardInputHandler extends AbstractKeyboardInputHandler {

    /** If the key status is active (pressed).  */
    private boolean active;
    /** {@code true} if the active status has been handled. */
    private boolean hasBeenHandled;

    /**
     * Instantiate a new object of the class <code>BooleanStateKeyboardInputHandler</code>.
     * @param keys key of the inputs (keyboard inputs)
     */
    public BooleanStateKeyboardInputHandler(Key... keys) {
        super(InputHandlerType.Action, keys);
        this.active = false;
        this.hasBeenHandled = false;
    }

    /*
     * (non-Javadoc)
     * @see AbstractKeyboardInputHandler#onKeyPressed() 
     */
    @Override
    protected void onKeyPressed() {		
	// Saving the old state
	boolean active0 = this.active;
	
	// Set active status
	this.active = true;
	
	// If the status changed from inactive to active mark 
        // that needs to be managed
	if (!active0) {
            this.hasBeenHandled = false;
	}
    }

    /*
     * (non-Javadoc)
     * @see AbstractKeyboardInputHandler#onKeyReleased() 
     */
    @Override
    protected void onKeyReleased() {
        this.active = false;
    }

    /*
     * (non-Javadoc)
     * @see AbstractKeyboardInputHandler#isActive() 
     */
    @Override
    public boolean isActive() {
        return this.active;
    }
    
    /**
     * Method in charge of managing if the input is active and has not been
     * handled.
     * @return boolean
     */
    public boolean isActiveButNotHandled() {
    	if (this.hasBeenHandled) {
            return false;
        }
    	return this.active;
    }
	
    /**
     * Sets the status of the input.
     * @param hasBeenHandled status
     */
    public void setHasBeenHandled(boolean hasBeenHandled) {
    	this.hasBeenHandled = hasBeenHandled;
    }
}
