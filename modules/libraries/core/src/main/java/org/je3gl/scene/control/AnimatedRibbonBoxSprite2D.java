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
 * An animation with the <code>AnimatedRibbonBoxSprite2D</code> class uses one 
 * or more images as an animated ribbon, where you can control the animation through frames.
 * <p>
 * <b>Example:</b>
 * <pre><code>
 * Image:
 * +----+----+----+---+
 * | A  | B  | C  | D |
 * +----+----+----+---+
 *   0    1    2    3
 * </code></pre>
 * Where A, B, C and D are images that act like the frame of a tape.<br>
 * Each frame corresponds to a movement of the animation, through the frames we can animate a 2D model, it is like a film strip.
 * 
 * @author wil
 * @version 1.5.1
 * @since 2.0.0
 */
public class AnimatedRibbonBoxSprite2D extends AbstractAnimation2DControl<Spatial, RibbonBoxAnimation2D, AnimatedRibbonBoxSprite2D> {

    /** Index of current animation frame. */
    private int index = 0;
    
    /**
     * Default constructor of class <code>AnimatedRibbonBoxSprite2D</code>.
     */
    public AnimatedRibbonBoxSprite2D() { }

    /**
     * Generate a new instance of the <code>AnimatedRibbonBoxSprite2D</code> class 
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
    public AnimatedRibbonBoxSprite2D(boolean illuminated) {
        super(illuminated);
    }

    /**
     * Generate a new instance of <code>AnimatedRibbonBoxSprite2D</code> where 
     * a custom animation handler can be specified.
     * 
     * @param handlerFunction A custom {@link org.je3gl.scene.control.AnimatedMaterialsHandlerFunction} object
     */
    public AnimatedRibbonBoxSprite2D(AnimatedMaterialsHandlerFunction<Spatial, RibbonBoxAnimation2D, AnimatedRibbonBoxSprite2D> handlerFunction) {
        super(handlerFunction);
    }

    /**
     * Add a new animation to this animated control.
     * @param name animation name
     * @param frames animation frames
     */
    @Override
    public void addAnimation(String name, RibbonBoxAnimation2D... frames) {
        super.addAnimation(name, frames);
    }
    
    /* (non-Javadoc)
     * @see org.je3gl.scene.control.AbstractAnimation2DControl#playAnimation(java.lang.String, float) 
     */
    @Override
    @SuppressWarnings("unchecked")    
    public boolean playAnimation(String name, float timePerFrame) {
        if (super.playAnimation(name, timePerFrame)) {
            index = 0;
            for (int i = 0; i < getQuantity(); i++) {
                getCurrentAnimation(i).index = 0;
            }            
            return true;
        }
        return false;
    }
    
    /**
     * (non-JavaDoc)
     * @see com.jme3.scene.control.Control#update(float) 
     * @see com.jme3.scene.control.AbstractControl#controlUpdate(float) 
     * @param tpf float
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (currentAnimation2D != null) {
            boolean finished = false;
            int old_index = animation2DIndex,
                old_anim  = index;
            elapsedeTime += (tpf * animation2DSpeed);
            
            if (elapsedeTime >= animationFrameTime) {
                animation2DIndex++;
                
                if (animation2DIndex >= getCurrentAnimation(index).getFrames().length) {
                    if (getQuantity() > 1) {
                        index++;
                        animation2DIndex = 0;
                        
                        if (index >= getQuantity()) {
                            if (isAnimationLoop()) {
                                index = 0;
                            } else {
                                index = getQuantity() - 1;
                                animation2DIndex = getCurrentAnimation(index).getFrames().length - 1;
                            }
                            finished = true;
                        }
                    } else {
                        if (isAnimationLoop()) {
                            animation2DIndex = 0;
                        } else {
                            animation2DIndex = getCurrentAnimation(index).getFrames().length;
                        }
                        finished = true;
                    }
                }
                
                boolean b = old_index != animation2DIndex || old_anim != index;
                if (b) {
                    fireAnimation2DChangeListener(false, index, old_anim, old_index, finished);
                }
                
                RibbonBoxAnimation2D animation2D = getCurrentAnimation(index);
                animation2D.index = animation2DIndex;                
                elapsedeTime      = 0.0F;
                
                handlerFunction.applyAnimation2DControl(spatial, animation2D, this);                
                if (b) {
                    fireAnimation2DChangeListener(true, index, index, animation2DIndex, finished);
                }
            }
            fireAnimatedTimeChangeListener();
        }
    }
    
    /* (non-Javadoc)
     * @see org.je3gl.scene.control.AbstractAnimation2DControl#getType() 
     */
    @Override
    public Type getType() {
        return Type.RibbonBox;
    }
}
