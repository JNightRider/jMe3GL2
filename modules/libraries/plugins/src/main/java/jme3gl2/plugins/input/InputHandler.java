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
import java.util.List;

/**
 * Any class that implements the <code>InputHandler</code> interface will have
 * access to manage user input data.
 * 
 * @author wil
 * @version 1.0.5
 * @since 2.0.0
 */
public interface InputHandler {
    
    /**
     * Method in charge of initializing the input, i.e. it establishes the
     * manager of the inputs.
     * 
     * @param im input manager
     */
    public void initialize(InputManager im);
    
    /**
     * Determines whether the input manager has been initialized.
     * @return status
     */
    public boolean isInitialized();

    /**  Method responsible for installing the inputs.  */
    public void install();
    /** Method responsible for uninstalling the inputs. */
    public void uninstall();

    /**
     * Determines the status of the inputs.
     * @return logical state
     */
    public boolean isEnabled();

    /**
     * Method in charge of establishing the status of the inputs.
     * @param flag {@code true} if inputs are enabled, otherwise {@code false}
     *              if disabled
     */
    public void setEnabled(boolean flag);

    /**
     * Method in charge of managing the inputs to determine their status.
     * @return {@code true} if the input has been activated, {@code false} if
     *          there have been no changes
     */
    public boolean isActive();

    /**
     * Manages a list of behavioral dependencies.
     * @return list of dependencies
     */
    public List<InputHandler> getDependentBehaviors();

    /**
     * Responsible for returning the status of the dependencies of this input.
     * @return status
     */
    public boolean isDependentBehaviorActive();

    /**
     * Method to determine whether the dependencies are additive or not.
     * @return status
     */
    public boolean isDependentBehaviorsAdditive();

    /**
     * Sets whether the dependencies of this input are additive.
     * @param flag status
     */
    public void setDependentBehaviorsAdditive(boolean flag);
}
