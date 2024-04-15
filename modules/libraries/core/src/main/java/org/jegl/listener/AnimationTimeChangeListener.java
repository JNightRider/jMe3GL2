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
package org.jegl.listener;

import com.jme3.scene.Spatial;
import org.jegl.scene.control.AbstractAnimation2DControl;
import org.jegl.scene.control.Animation2D;

/**
 * Interface in charge of managing the progress of an animation throughout its 
 * life cycle. With this interface we can obtain information about the exact time 
 * in which the frames of a certain animation pass.
 * <p>
 * Example:
 * <pre><code>
 * AnimationTimeChangeListener&#60;?, ?, ?&#62; listener = new AnimationTimeChangeListener&#60;&#62;() {
 *     &#64;Override
 *     public void onTime(float range, float elapsedeTime) {
 *         System.out.println("[ " + range + "%, "+ elapsedeTime +"tpf ]");
 *     }
 * }
 * </code></pre>
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 * @param <O> the type of model
 * @param <A> the type of animation
 * @param <E> the type of animated control
 */
public interface AnimationTimeChangeListener<O extends Spatial, A extends Animation2D, E extends AbstractAnimation2DControl<O, A, E>> {
    
    /**
     * Method responsible for reporting the progress of the animations managed by 
     * the animated control, normally providing 2 types of data:
     * <p>
     * <b>Type of information</b>
     * <ul>
     * <li><b>range</b>: A range between 1 and 100%</li>
     * <li><b>elapsedeTime</b>: Frame time lapse</li>
     * </ul>
     * 
     * @param range percentage range (0 - 100)
     * @param elapsedeTime elapsed time per frame
     */
    void onTime(float range, float elapsedeTime);
}
