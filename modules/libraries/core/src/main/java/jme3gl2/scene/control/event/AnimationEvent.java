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
package jme3gl2.scene.control.event;

import com.jme3.scene.Spatial;
import jme3gl2.scene.control.AbstractAnimation2DControl;
import jme3gl2.scene.control.Animation2D;

/**
 * Class in charge of managing the data of animation change events ({@link jme3gl2.scene.control.event.AnimationChangeListener}).
 * @param <O> the type of model
 * @param <A> the type of animation
 * @param <E> the type of animated control
 * 
 * @author wil
 * @version 3.0.0
 * @since 1.0.0
 */
public class AnimationEvent<O extends Spatial, A extends Animation2D, E extends AbstractAnimation2DControl<O, A, E>> {
    
    /** 2D model. */
    private O model;
    /** Frame animation. */
    private A animation;
    /** Animated control. */
    private E control;
    
    /** current animation name. */
    private String name;
    /** animation frame index. */
    private int index;
    /** current frame of the animation. */
    private int frame;

    /**
     * Generate a new animated event with class <code>AnimationEvent</code>.
     * @param model the 2D model
     * @param animation frame animation
     * @param control animated control
     * @param name current animation name
     * @param index animation frame index
     * @param frame current frame of the animation
     */
    public AnimationEvent(O model, A animation, E control, String name, int index, int frame) {
        this.model     = model;
        this.animation = animation;
        this.control   = control;
        this.name  = name;
        this.index = index;
        this.frame = frame;
    }

    /**
     * Returns the 2D model.
     * @return object
     */
    public O getModel() {
        return model;
    }

    /**
     * Returns the current animation.
     * @return object
     */
    public A getAnimation() {
        return animation;
    }

    /**
     * Returns the animated control.
     * @return object
     */
    public E getControl() {
        return control;
    }

    /**
     * Returns the name of the active animation.
     * @return string
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the index of the active animation.
     * @return int
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the frame of the active animation (index dependent).
     * @return int
     */
    public int getFrame() {
        return frame;
    }
}
