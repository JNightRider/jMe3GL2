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
package org.jegl.renderer;

import com.jme3.renderer.Camera;
import com.jme3.util.SafeArrayList;
import org.jegl.renderer.effect.GLXEffect;
import org.ietf.jgss.GSSContext;

/**
 * Abstract class that implements the {@link org.jegl.renderer.GLXCamera} interface.
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public abstract class AbstractGLXCamera implements GLXCamera {
    
    /** GLX (Camera) effects list. */
    private SafeArrayList<GLXEffect> effects = new SafeArrayList<>(GLXEffect.class);
    
    /** Status (enabled | disabled).*/
    protected boolean enabled = true;
    /** Camera. */
    protected Camera camera;

    /**
     * Constructor.
     */
    public AbstractGLXCamera() {
    }
    
    /**
     * Returns the status of this camera
     * @return boolean
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the status of this camera
     * @param enabled boolean
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.GLXCamera#setCamera(com.jme3.renderer.Camera) 
     */
    @Override
    public void setCamera(Camera camera) {
        if (this.camera != null && camera != null && camera != this.camera) {
            throw new IllegalStateException("This control already has a camera");
        }
        for (final GLXEffect effect : effects) {
            effect.setCamera(this);
        }
        this.camera = camera;
        this.initialize();
    }

    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.GLXCamera#getCamera()
     */
    @Override
    public Camera getCamera() {
        return this.camera;
    }
    
    /**
     * Abstract method implemented by the classes that inherit this object to 
     * initialize the camera with its corresponding configurations.
     */
    protected abstract void initialize();

    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.GLXCamera#update(float)
     */
    @Override
    public void update(float tpf) {
        if (!enabled)
            return;
        
        for (final GLXEffect effect : effects) {
            effect.update(tpf);
        }
    }

    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.GLXCamera#addEffect(jme3gl2.renderer.effect.GLXEffect) 
     */
    @Override
    public void addEffect(GLXEffect effect) {
        if (effect == null) {
            throw new NullPointerException("Can't add a null effect");
        }
        
        this.effects.add(effect);
        if (camera != null) {
            effect.setCamera(this);
        }
    }

    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.GLXCamera#removeEffect(jme3gl2.renderer.effect.GLXEffect) 
     */
    @Override
    public void removeEffect(GLXEffect effect) {
        if (this.effects.remove(effect)) {
            effect.setCamera(null);
        }
    }

    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.GLXCamera#getEffect(int) 
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends GLXEffect> T getEffect(int index) {
        return (T) this.effects.get(index);
    }

    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.GLXCamera#getEffect(java.lang.Class) 
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends GLXEffect> T getEffect(Class<T> clazz) {
        for (GLXEffect effect : this.effects) {
            if (clazz.isAssignableFrom(effect.getClass())) {
                return (T) effect;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.GLXCamera#getEffectQuantity() 
     */
    @Override
    public int getEffectQuantity() {
        return this.effects.size();
    }
}
