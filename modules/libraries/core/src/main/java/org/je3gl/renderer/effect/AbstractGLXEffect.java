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
package org.je3gl.renderer.effect;

import org.je3gl.renderer.GLXCamera;

/**
 * Abstract class where the {@link org.je3gl.renderer.effect.GLXDistanceFrustum}
 * interface for camera effects is implemented.
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public abstract class AbstractGLXEffect implements GLXEffect {
    /** Status (enabled | disabled).*/
    protected boolean enabled = true;
    /** GLX-Camera. */
    protected GLXCamera camera;

    /** Interpolation speed (amount). */
    protected float interpolationAmount;
    
    /**
     * Constructor.
     */
    public AbstractGLXEffect() {
    }

    /**
     * Returns the state of this effect.
     * @return boolean
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the state of this effect.
     * @param enabled boolean
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.effect.GLXEffect#setCamera(jme3gl2.renderer.GLXCamera) 
     */
    @Override
    public void setCamera(GLXCamera camera) {
        if (this.camera != null && camera != null && camera != this.camera) {
            throw new IllegalStateException("This control already has a camera");
        }
        this.camera = camera;
    }

    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.effect.GLXEffect#update(float) 
     */
    @Override
    public void update(float tpf) {
        if (!enabled)
            return;
        effectUpdate(tpf);
    }
    
    /**
     * Updates the status of all loaded effects.
     * @param tpf float
     */
    protected abstract void effectUpdate(float tpf);

    /**
     * Returns the interpolation amount (speed).
     * @return float
     */
    public float getInterpolationAmount() {
        return interpolationAmount;
    }

    /**
     * Sets a new interpolation amount (speed)
     * @param interpolationAmount float
     */
    public void setInterpolationAmount(float interpolationAmount) {
        this.interpolationAmount = interpolationAmount;
    }
}
