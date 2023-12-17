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
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase <code>AnimatedSprite</code> se encarga de proporcionar un control de
 * manejo de múltiples texturas.
 * <br>
 * <b>Con esta clase podemos crear animaciones.</b>
 * 
 * @author wil
 * @version 1.5-SNAPSHOT
 *
 * @since 1.0.0
 */
public class AnimatedSprite extends AbstractAnimatedControl<Texture> {
    
    /** Logger de la clase. */
    private static final Logger LOG = Logger.getLogger(AnimatedSprite.class.getName());

    /** Material del 'modelos 2D.' */
    private Material mat;
    
    /**
     * {@code true} si la malla del modelo se redimensiona deacuerdo con las
     * dimensiones de su textura-sprite, de lo contrario {@code false}.
     */
    protected boolean dynamic;
    
    /**
     * Tipo de escalado.
     * @see ScaleType
     */
    protected ScaleType scaleType;

    /** Dimensiones originales de la malla. */
    protected Vector2f originalDim;
    
    /**
     * Constructor predeterminado de la clase {@code AnimatedSprite}.
     */
    public AnimatedSprite() {
    }

    /**
     * Construcor de la clase <code>AnimatedSprite</code>.
     * @param lighting un valor boolean.
     */
    public AnimatedSprite(boolean lighting) {
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
        originalDim = new Vector2f(sprite.getWidth(), sprite.getHeight());
    }

    /**
     * Implementación del método <code>addAnimation(String, E[])</code>.
     * @param name Un {@code String} como clave.
     * @param frames Un arreglo de {@code Texture} como valor.
     */
    @Override
    public void addAnimation(String name, Texture[] frames) {
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
     */
    @Override
    public void playAnimation(String name, float timePerFrame) {
        Texture[] text = this.animations.get(name);
        if ( text == null ) {
            LOG.log(Level.WARNING, "[{0}] animation not found.", name);
            return;
        }

        if ( !(name.equals(this.currentAnimationName)) ) {
            this.currentAnimationName = name;

            if ( this.mat != null ) {
                if (dynamic) {
                    Vector2f newSize = getDynamicSize(text[0]);
                    sprite.updateVertexSize(newSize.x, newSize.y);
                }
                
                this.mat.setTexture(getNameParam(), text[0]);
            }

            this.currentAnimation = text;
            this.animationFrameTime = timePerFrame;
            this.currentIndex = 0;
            this.elapsedeTime = 0.0F;
            fireSpriteAnimationChangeListener();
        }
    }

    /**
     * Establece un nuevo estado de animación.
     * @param dynamic boolean
     * @see AnimatedSprite#dynamic
     */
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    /**
     * Establece un nuevo tipo de escalado.
     * @param scaleType tipo-escala.
     */
    public void setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
    }
    
    /**
     * Método encargado de gestionar las dimensiones dinamicas de un modelo
     * 2D animado.
     * 
     * @param text textura.
     * @return nuevo tamaño.
     */
    private Vector2f getDynamicSize(Texture text) {
        float max, min;
        float mm_dim;
        if (null == scaleType) {
            throw new NullPointerException("LockScaling is Null.");
        } else switch (scaleType) {
            case GL2_HEIGHT:
                max = text.getImage().getHeight();
                mm_dim = originalDim.y;
                return new Vector2f((text.getImage().getWidth() / max) *  mm_dim, mm_dim);
            case GL2_WIDTH:
                max = text.getImage().getWidth();
                mm_dim = originalDim.x;
                return new Vector2f(mm_dim, (text.getImage().getHeight() / max) * mm_dim);
            case GL2_MAX:
                max = text.getImage().getHeight();
                min = text.getImage().getWidth();
                if (max >= min) {
                    mm_dim = originalDim.y;
                    return new Vector2f((min / max) * mm_dim, mm_dim);
                } else {
                    mm_dim = originalDim.x;
                    return new Vector2f(mm_dim, (max / min) * mm_dim);
                }
            case GL2_MIN:
                max = text.getImage().getHeight();
                min = text.getImage().getWidth();
                if (max >= min) {
                    mm_dim = originalDim.x;
                    return new Vector2f(mm_dim, (max / min) * mm_dim);
                } else {
                    mm_dim = originalDim.y;
                    return new Vector2f((min / max) * mm_dim, mm_dim);
                }
            default:
                throw new AssertionError();
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
                
                if ( this.currentIndex >= this.currentAnimation.length ) {
                    if (isLoop()) {
                        this.currentIndex = 0;
                    } else {
                        this.currentIndex = this.currentAnimation.length - 1;
                    }
                }
                
                if (oldIndex != this.currentIndex) {
                    fireSpriteAnimationChangeListener();
                }
                
                if (dynamic) {
                    Vector2f newSize = getDynamicSize(this.currentAnimation[this.currentIndex]);
                    sprite.updateVertexSize(newSize.x, newSize.y);
                }
                
                this.elapsedeTime = 0f;
                this.mat.setTexture(getNameParam(), this.currentAnimation[this.currentIndex]);
            }
        }
    }
}
