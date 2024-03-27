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
package jme3gl2.renderer.effect;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 *
 * @author wil
 */
public class GLXClipping extends AbstractGLXEffect {
    
    private Vector2f minimumClipping;
    private Vector2f maximumClipping;

    public GLXClipping(Vector2f minimumClipping, Vector2f maximumClipping) {
        this.minimumClipping = minimumClipping;
        this.maximumClipping = maximumClipping;
    }
    
    public void setMinimumClipping(Vector2f minimumClipping) {
        this.minimumClipping = minimumClipping;
    }

    public void setMaximumClipping(Vector2f maximumClipping) {
        this.maximumClipping = maximumClipping;
    }

    public Vector2f getMinimumClipping() {
        return minimumClipping;
    }

    public Vector2f getMaximumClipping() {
        return maximumClipping;
    }
    
    @Override
    protected void effectUpdate(float tpf) {        
        if (minimumClipping != null && maximumClipping != null) {
            Vector3f loc = camera.getCamera().getLocation();            
            Vector2f clipping = clipping(loc, Vector2f.ZERO);
            camera.getCamera().setLocation(loc.set(clipping.x, clipping.y, loc.z));
        }
    }
    
    protected final Vector2f clipping(Vector3f loc, Vector2f offset) {
        return new Vector2f(FastMath.clamp((loc.x + offset.x), minimumClipping.x, maximumClipping.x), 
                            FastMath.clamp((loc.y + offset.y), minimumClipping.y, maximumClipping.y));
    }
}
