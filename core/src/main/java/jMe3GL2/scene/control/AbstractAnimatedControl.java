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

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import jMe3GL2.scene.shape.Sprite;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Un <code>AbstractAnimatedControl</code> es el encargado de gestionar una plantilla
 * para aplicar una cierta animación a un <code>Sprite</code>.
 * <p>
 * Dependerá de cómo se quiere aplicar la animación, es decir si es una 
 * animación con texturas o una que manipule los vértices.</p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * @param <E> Tipo de animación.
 * 
 * @since 1.0.0
 */
public abstract 
class AbstractAnimatedControl<E extends Object> extends AbstractControl {
    
    /**
     * Mapa de animaciones.
     */
    protected Map<String, E[]> animations 
                                = new HashMap<>();
    
    /**
     * Animación actual activa.
     */
    protected E[] currentAnimation;
    
    /** {@code Sprite} en donde se aplicara la animación. */
    protected Sprite sprite;
    
    /**
     * Velocidad con la que se aplicará la animación.
     */
    protected float speed = 1;
    
    /** Índice actual de las animaciones. */
    protected int currentIndex;
    
    /** Nombre actual de la animación. */
    protected String currentAnimationName;
    
    /** Tiempo con que activará dicha animación. */
    protected float animationFrameTime = 1f;
    
    /** El lapso de tiempo transcurrido. */
    protected float elapsedeTime = 0f;

    /**
     * Solo serialización. No utilice.
     */
    public AbstractAnimatedControl() {
    }
    
    /**
     * (non-JavaDoc)
     * 
     * @param spatial Spatial.
     * @see AbstractControl#setSpatial(com.jme3.scene.Spatial) 
     */
    @Override
    public void setSpatial(Spatial spatial) {
        if ( !(spatial instanceof Geometry) ) {
            throw new UnsupportedOperationException("Spatial not supported.");
        }
        
        Mesh mesh = ((Geometry) spatial).getMesh();
        if ( !(mesh instanceof Sprite) ) {
            throw new IllegalArgumentException("The Spatial does not have a Sprite.");
        }
        
        sprite = (Sprite)mesh;
        super.setSpatial(spatial);
    }

    /**
     * Devuelve una {@code Set} con los nombres de las animaciones.
     * @return Lista de nombres.
     */
    public Set<String> getAnimations() {
        return animations.keySet();
    }

    /**
     * Devuelve la velocidad de la animación.
     * @return Velocidad de animación.
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Establece una nueva velocidad para las animaciones.
     * @param speed Velocidad de animación.
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * (non-JavaDoc)
     * @param rm RenderManager
     * @param vp ViewPort
     * @see AbstractControl#render(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort) 
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
    
    /**
     * Método encargado de agregar una nueva animación a la lista.
     * @param name Nombre de la animación.
     * @param frames Areglo de la nimación (datos).
     */
    public abstract void addAnimation(String name, E[] frames);
    
    /**
     * Método encargado de iniciar con una animación.
     * @param name Nombre de la animación.
     * @param timePerFrame Tiempo por cuadro.
     */
    public abstract void playAnimation(String name, float timePerFrame);
}
