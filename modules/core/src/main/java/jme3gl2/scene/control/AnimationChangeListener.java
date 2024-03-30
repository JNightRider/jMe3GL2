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

/**
 * Interface in charge of managing an animation change detector, said change is
 * notified by the animated control when changing frames of an active animation.
 * <p>
 * There are two ways to tell if the animation has made a change: the <code>beforeAnimation2DChange()</code>
 * method reports the previous frame and the <code>afterAnimation2DChange</code> method reports the new change.
 * <p>
 * Example:
 * <pre><code>
 * AnimationChangeListener&#60;?, ?, ?&#62; listener = new AnimationChangeListener&#60;&#62;() {
 *     &#64;Override
 *     public void beforeAnimation2DChange(Spatial model, Animation2D animation, AbstractAnimation2DControl control, int index) {
 *         System.out.println("before("+ index+")");
 *     }
 *     &#64;Override
 *     public void afterAnimation2DChange(Spatial model, Animation2D animation, AbstractAnimation2DControl control, int index) {
 *         System.out.println("after("+ index+")");
 *     }
 *  }
 * 
 * &#62;&#62; If the animated control has 3 animations loaded, and from this it goes
 * &#62;&#62; to the second; the output would be the following:
 * 
 * &#62;&#62; before(0)
 * &#62;&#62; before(1)
 * </code></pre>
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 * @param <O> the type of model
 * @param <A> the type of animation
 * @param <E> the type of animated control
 */
public interface AnimationChangeListener<O extends Spatial, A extends Animation2D, E extends AbstractAnimation2DControl<O, A, E>> {
    
    /**
     * Notify the animation frame before applying the new one.
     * @param model the 2D model
     * @param animation frame animation
     * @param control animated control
     * @param index animation frame index
     */
    void beforeAnimation2DChange(O model, A animation, E control, int index);
    
    /**
     * Notify the animation frame after applying the new one.
     * @param model the 2D model
     * @param animation frame animation
     * @param control animated control
     * @param index animation frame index
     */
    void afterAnimation2DChange(O model, A animation, E control, int index);
}