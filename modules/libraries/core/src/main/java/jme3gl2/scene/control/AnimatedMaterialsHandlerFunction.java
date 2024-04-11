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
package jme3gl2.scene.control;

import com.jme3.scene.Spatial;
import jme3gl2.util.Serializable;

/**
 * An <code>AnimatedMaterialsHandlerFunction</code> interface is responsible for 
 * managing a 2D model which is animated using an animation control. With this
 * interface you will be able to independently manage each 2D model and its animated behavior.
 * <p>
 * This interface is useful because if you interact with the materials of the
 * objects (models), each of them has different properties that may or may not 
 * match. Therefore, it is important to maintain independence when applying 
 * animations to each model.
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 * @param <O> the type of model
 * @param <A> the type of animation
 * @param <E> the type of animated control
 */
public interface AnimatedMaterialsHandlerFunction<O extends Spatial, A extends Animation2D, E extends AbstractAnimation2DControl<O, A, E>> extends Serializable {
    
    /**
     * Method responsible for applying animation to the 2D model.
     * @param model the 2D model
     * @param animation the animation
     * @param control the animated control
     */
    void applyAnimation2DControl(O model, A animation, E control);
}
