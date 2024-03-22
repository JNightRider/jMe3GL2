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
package jme3gl2.scene.control;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Una animación con la clase <code>RibbonBoxAnimationSprite</code> utilza una
 * imagen como cinta animada, en donde se puede controlar la animación por
 * medio de cuadros.
 * 
 * <p>
 * <b>Ejemplo:</b>
 * <pre><code>
 * Imagen:
 * +----+----+----+---+  // Donde A,B,C y D son imagenes que actuan como
 * | A  | B  | C  | D |  // un cuadros de una cinta.
 * +----+----+----+---+
 *   0    1    2    3
 * 
 * Cada cuadro corresponde a un movimineto de la animación, por medio de los
 * cuadros podemos animar un modelo 2D, es como una cinta de película
 * </code></pre>
 * 
 * @author wil
 * @version 1.5-SNAPSHOT
 *
 * @since 2.0.0
 */
public class RibbonBoxAnimationSprite extends AbstractAnimatedControl<RibbonBox> {

    /** Logger de la clase. */
    private static final Logger LOG = Logger.getLogger(RibbonBoxAnimationSprite.class.getName());
    
    /** Índice actual de la caja de animaciones. */
    private int index;

    /** Material del 'modelos 2D.' */
    private Material mat;

    /**
     * Constructor predeterminado de la clase {@code RibbonBoxAnimationSprite}.
     */
    public RibbonBoxAnimationSprite() {
    }

    /**
     * Construcor de la clase <code>ibbonBoxAnimationSprite</code>.
     * @param lighting un valor boolean.
     */
    public RibbonBoxAnimationSprite(boolean lighting) {
        super(lighting);
    }
        
    /**
     * (non-JavaDoc)
     * @param spatial Spatial
     * @see AbstractAnimatedControl#setSpatial(com.jme3.scene.Spatial) 
     */
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        mat = ((Geometry) spatial).getMaterial();
    }
    
    /**
     * Implementación del método <code>addAnimation(String, E[])</code>.
     * @param name Un {@code String} como clave.
     * @param frames Un arreglo de {@code Integer} como valor.
     */
    @Override
    public void addAnimation(String name, RibbonBox... frames) {
        if ( this.animations.containsKey(name) ) {
            LOG.log(Level.WARNING, "Animation [{0}] existing.", name);
        } else {
            if (frames == null) {
                throw new IllegalArgumentException("Invalid animation");
            }
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
        RibbonBox[] rb = this.animations.get(name);
        if ( rb == null ) {
            LOG.log(Level.WARNING, "[{0}] animation not found.", name);
            return;
        }
        
        if ( !(name.equals(this.currentAnimationName)) ) {
            this.currentAnimationName = name;
            this.index = 0;
            
            if ( this.mat != null ) {
                this.mat.setTexture(getNameParam(), rb[0].getTexture());
            }
            
            this.sprite.updateMeshCoords(rb[0].getColumns(), rb[0].getRows());
            this.sprite.showIndex(rb[0].getFrames()[0]);
            
            this.currentAnimation   = rb;
            this.animationFrameTime = timePerFrame;
            
            this.currentIndex = 0;
            this.elapsedeTime = 0f;
            fireSpriteAnimationChangeListener();
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
            
            int oldIndex = this.currentIndex;
            this.elapsedeTime += (tpf * this.speed);
            if ( this.elapsedeTime >= this.animationFrameTime ) {
                this.currentIndex++;
                
                if ( this.currentIndex >= this.currentAnimation[index].getFrames().length ) {
                    if (currentAnimation.length > 1) {
                        index++;
                        currentIndex = 0;
                        if (index >= currentAnimation.length) {
                            if (isLoop()) {
                                index = 0;
                            } else {
                                index        = currentAnimation.length - 1;
                                currentIndex = currentAnimation[index].getFrames().length - 1;
                            }
                        }
                    } else {
                        if (isLoop()) {
                            currentIndex = 0;
                        } else {
                            currentIndex = currentAnimation[index].getFrames().length - 1;
                        }
                    }
                }
                
                if (oldIndex != this.currentIndex) {
                    fireSpriteAnimationChangeListener();
                }
                
                this.elapsedeTime = 0f;
                this.sprite.showIndex(this.currentAnimation[index].getFrames()[this.currentIndex]);
                this.mat.setTexture(getNameParam(), this.currentAnimation[index].getTexture());
            }
        }
    }
}
