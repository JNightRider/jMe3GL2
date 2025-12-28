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
package org.je3gl.scene.control;

import com.jme3.scene.Spatial;

/**
 * Abstract class for linear animations where it only depends on its simple frames
 * without any intervention from the animated control.
 * <p>
 * This class is used by the following animated controls:
 * <ul>
 * <li>{@link org.je3gl.scene.control.AnimatedSingleSprite2D}</li>
 * <li>{@link org.je3gl.scene.control.AnimatedSprite2D}</li>
 * </ul>
 * 
 * @author wil
 * @since 1.0.0
 * @version 3.0.1
 * @param <O> the type of model
 * @param <A> the type of animation
 * @param <E> the type of animated control
 */
public abstract class AbstractAnimatedSprite2D<O extends Spatial, A extends Animation2D, E extends AbstractAnimatedSprite2D<O, A, E>> extends AbstractAnimation2DControl<O, A, E> {

    /**
     * Default constructor of class <code>AbstractAnimatedSprite2D</code>.
     */
    public AbstractAnimatedSprite2D() {
    }

    /**
     * Generate a new instance of the <code>AbstractAnimatedSprite2D</code> class 
     * where you can specify the type of animation renderer to use from among those
     * supported by default by jMe3GL2.
     * <p>
     * Supported list
     * <ul>
     * <li>{@link org.je3gl.scene.control.UnshadedHandlerFunction}</li>
     * <li>{@link org.je3gl.scene.control.LightingHandlerFunction}</li>
     * </ul>
     * 
     * @param illuminated <code>true</code> if {@link org.je3gl.scene.control.LightingHandlerFunction} 
     * representation is used; otherwise <code>false</code> if {@link org.je3gl.scene.control.UnshadedHandlerFunction} is used.
     */
    public AbstractAnimatedSprite2D(boolean illuminated) {
        super(illuminated);
    }

    /**
     * Generate a new instance of <code>AbstractAnimatedSprite2D</code> where 
     * a custom animation handler can be specified.
     * 
     * @param handlerFunction A custom {@link org.je3gl.scene.control.AnimatedMaterialsHandlerFunction} object
     */
    public AbstractAnimatedSprite2D(AnimatedMaterialsHandlerFunction<O, A, E> handlerFunction) {
        super(handlerFunction);
    }
    
    /**
     * (non-JavaDoc)
     * @see com.jme3.scene.control.Control#update(float) 
     * @see com.jme3.scene.control.AbstractControl#controlUpdate(float) 
     * @param tpf float
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void controlUpdate(float tpf) {
        if (currentAnimation2D != null) {
            boolean finished = false;
            int old_index = animation2DIndex;
            elapsedeTime += (tpf * animation2DSpeed);
            
            if (elapsedeTime >= animationFrameTime) {
                animation2DIndex++;
                
                if (animation2DIndex >= getQuantity()) {
                    if (isAnimationLoop()) {
                        animation2DIndex = 0;
                    } else {
                        animation2DIndex = getQuantity() - 1;
                    }
                    finished = true;
                }
                
                boolean b = old_index != animation2DIndex;
                if (b) {
                    fireAnimation2DChangeListener(false, animation2DIndex, 0, old_index, finished);
                }
                
                elapsedeTime = 0.0F;
                handlerFunction.applyAnimation2DControl((O) spatial, getCurrentAnimation(animation2DIndex), (E)this);
                
                if (b) {
                    fireAnimation2DChangeListener(true, animation2DIndex, 0, animation2DIndex, finished);
                }
            }
            fireAnimatedTimeChangeListener();
        }
    }
}
