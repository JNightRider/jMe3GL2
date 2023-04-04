/* Copyright (c) 2009-2023 jMonkeyEngine.
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
package jMe3GL2.scene.control;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase <code>IndexAnimatedSprite</code> se encarga de proporcionar un control
 * de manejo para la manipulación de la malla.
 * <br>
 * <b>Con esta clase podemos crear animaciones.</b>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 *
 * @since 1.0.0
 */
public class IndexAnimatedSprite extends AbstractAnimatedControl<Integer>{

    /** Logger de la clase. */
    private static final Logger LOG = Logger.getLogger(IndexAnimatedSprite.class.getName());

    /**
     * Constructor predeterminado de la clase {@code IndexAnimatedSprite}.
     */
    public IndexAnimatedSprite() {
    }
    
    /**
     * Implementación del método <code>addAnimation(String, E[])</code>.
     * @param name Un {@code String} como clave.
     * @param frames Un arreglo de {@code Integer} como valor.
     */
    @Override
    public void addAnimation(String name, Integer[] frames) {
        if ( this.animations.containsKey(name) ) {
            LOG.log(Level.WARNING, "Animation [{0}] existing.", name);
        } else {
            this.animations.put(name, frames);
        }
    }

    /**
     * Implementación del método <code>playAnimation(String, float)</code>.
     * @param name Un {@code String} como clave.
     * @param timePerFrame Un {@code Float} como valor.
     * @see AbstractAnimatedControl#playAnimation(java.lang.String, float) 
     */
    @Override
    public void playAnimation(String name, float timePerFrame) {
        Integer[] idx = this.animations.get(name);
        if ( idx == null ) {
            LOG.log(Level.WARNING, "[{0}] animation not found.", name);
            return;
        }
        
        if ( !(name.equals(this.currentAnimationName)) ) {
            this.currentAnimationName = name;
            this.sprite.showIndex(idx[0]);
            
            this.currentAnimation   = idx;
            this.animationFrameTime = timePerFrame;
            
            this.currentIndex = 0;
            this.elapsedeTime = 0f;
        }
    }

    /**
     * Implementación del método <code>controlUpdate(float)</code>.
     * @param tpf Un {@code Float} como valor.
     * @see AbstractAnimatedControl#controlUpdate(float) 
     */
    @Override
    protected void controlUpdate(float tpf) {
        if ( this.sprite != null
                && this.currentAnimation != null ) {
            
            this.elapsedeTime += (tpf * this.speed);
            if ( this.elapsedeTime >= this.animationFrameTime ) {
                this.currentIndex++;
                
                if ( this.currentIndex >= this.currentAnimation.length ) {
                    this.currentIndex = 0;
                }
                
                this.elapsedeTime = 0f;
                this.sprite.showIndex(this.currentAnimation[this.currentIndex]);
            }
        }
    }    
}
