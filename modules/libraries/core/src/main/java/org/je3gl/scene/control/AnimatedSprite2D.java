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
package org.je3gl.scene.control;

import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

/**
 * The <code>AnimatedSprite2D</code> class is responsible for providing multiple 
 * texture management control.
 * <p>
 * With this control we can create animations with independent textures, that is; 
 * which will form an animation with a list of textures in a linear manner.
 * <p>
 * <b>Example:</b>
 * Suppose you have the following textures (images):
 * <pre><code>
 * [ A ], [ B ], [ C ], [ D ]
 * </code></pre>
 * Each texture is a frame of an animation, with the <b>AnimatedSprite2D</b> control 
 * you can put them all together and generate an animation with it.
 * @author wil
 * @version 1.5.5
 * @since 1.0.0
 */
public class AnimatedSprite2D extends AbstractAnimatedSprite2D<Spatial, SpriteAnimation2D, AnimatedSprite2D> {

    /**
     * Default constructor of class <code>AnimatedSprite2D</code>.
     */
    public AnimatedSprite2D() { }

    /**
     * Generate a new instance of the <code>AnimatedSprite2D</code> class 
     * where you can specify the type of animation renderer to use from among those
     * supported by default by jMe3GL2.
     * <p>
     * Supported list:
     * <ul>
     * <li>{@link org.je3gl.scene.control.UnshadedHandlerFunction}</li>
     * <li>{@link org.je3gl.scene.control.LightingHandlerFunction}</li>
     * </ul>
     * 
     * @param illuminated <code>true</code> if {@link org.je3gl.scene.control.LightingHandlerFunction} 
     * representation is used; otherwise <code>false</code> if {@link org.je3gl.scene.control.UnshadedHandlerFunction} is used.
     */
    public AnimatedSprite2D(boolean illuminated) {
        super(illuminated);
    }

    /**
     * Generate a new instance of <code>AnimatedSprite2D</code> where 
     * a custom animation handler can be specified.
     * 
     * @param handlerFunction A custom {@link org.je3gl.scene.control.AnimatedMaterialsHandlerFunction} object
     */
    public AnimatedSprite2D(AnimatedMaterialsHandlerFunction<Spatial, SpriteAnimation2D, AnimatedSprite2D> handlerFunction) {
        super(handlerFunction);
    }
    
    /**
     * Add a new animation to this animated control.
     * @param name animation name
     * @param frames animation frames
     */
    public void addAnimation(String name, Texture ...frames) {
        SpriteAnimation2D[] anim = new SpriteAnimation2D[frames.length];
        for (int i = 0; i < frames.length; i++) {
            anim[i] = new SpriteAnimation2D(frames[i]);
        }
        addAnimation(name, anim);
    }

    /* (non-Javadoc)
     * @see org.je3gl.scene.control.AbstractAnimation2DControl#getType() 
     */
    @Override
    public Type getType() {
        return Type.Sprite;
    }
}
