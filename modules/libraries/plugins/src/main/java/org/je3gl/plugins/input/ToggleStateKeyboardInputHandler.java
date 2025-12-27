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

/**
 * Class responsible for managing an alternate state controller for keyboard inputs.
 * 
 * @author wil
 * @version 1.0,0
 * @version 3.1.0
 */
public class ToggleStateKeyboardInputHandler extends AbstractKeyboardInputHandler {

    /** If the key status is active (pressed).  */
    private boolean active;
    
    /**
     * Instantiate a new object of the class <code>BooleanStateKeyboardInputHandler</code>.
     * @param keys key of the inputs (keyboard inputs)
     */
    public ToggleStateKeyboardInputHandler(Key... keys) {
        super(InputHandlerType.Action, keys);
        this.active = false;
    }

    /**
     * It is activated when an input is pressed.
     */
    @Override
    protected void onKeyPressed() {
        this.active = !this.active;
    }

    /**
     * Change the state of this handler.
     * @param flag boolean
     */
    public void setActive(boolean flag) {
        this.active = flag;
    }

    /**
     * Method in charge of managing the inputs to determine their status.
     * @return {@code true} if the input has been activated, {@code false} if
     *          there have been no changes
     */
    @Override
    public boolean isActive() {
        return this.active;
    }

    /**
     * It is activated when an input is released.
     */
    @Override
    protected void onKeyReleased() {
        
    }
}
