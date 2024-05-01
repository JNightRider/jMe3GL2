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
package org.je3gl.renderer.effect;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 * A limit effect for the camera (clipping).
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class GLXClipping extends AbstractGLXEffect {
    
    /** Minimum clipping. */
    private Vector2f minimumClipping;
    /** Maximum clipping. */
    private Vector2f maximumClipping;

    /**
     * Generate a new instance of this class <code>GLXClipping</code>.
     * @param minimumClipping Minimum clipping
     * @param maximumClipping Maximum clipping
     */
    public GLXClipping(Vector2f minimumClipping, Vector2f maximumClipping) {
        this.minimumClipping = minimumClipping;
        this.maximumClipping = maximumClipping;
    }
    
    /**
     * Sets new clipping values for the camera.
     * <p>
     * If you want to remove the trimmings, with values <code>null</code> as a
     * parameter they will be erased.
     * 
     * @param minimumClipping minimum clipping.
     * @param maximumClipping maximum clipping.
     */
    public void setClipping(Vector2f minimumClipping, Vector2f maximumClipping) {
        this.minimumClipping = minimumClipping;
        this.maximumClipping = maximumClipping;
    }

    /**
     * Returns a minimum clipping value.
     * @return clipping.
     */
    public Vector2f getMinimumClipping() {
        return minimumClipping;
    }

    /**
     * Returns a maximum clipping value.
     * @return clipping.
     */
    public Vector2f getMaximumClipping() {
        return maximumClipping;
    }
    
    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.effect.AbstractGLXEffect#effectUpdate(float) 
     */
    @Override
    protected void effectUpdate(float tpf) {        
        if (minimumClipping != null && maximumClipping != null) {
            Vector3f loc = camera.getCamera().getLocation();            
            Vector2f clipping = clipping(loc, Vector2f.ZERO);
            camera.getCamera().setLocation(loc.set(clipping.x, clipping.y, loc.z));
        }
    }
    
    /**
     * Method responsible for calculating the camera position within the
     * clipping ranges.
     * 
     * @param loc position
     * @param offset offset
     * @return new calculated position.
     */
    protected final Vector2f clipping(Vector3f loc, Vector2f offset) {
        if (minimumClipping == null || maximumClipping == null) {
            return new Vector2f(loc.x +  offset.y, loc.y + offset.y);
        }        
        return new Vector2f(FastMath.clamp((loc.x + offset.x), minimumClipping.x, maximumClipping.x), 
                            FastMath.clamp((loc.y + offset.y), minimumClipping.y, maximumClipping.y));
    }
}
