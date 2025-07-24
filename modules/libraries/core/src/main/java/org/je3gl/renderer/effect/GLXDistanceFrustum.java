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

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import org.je3gl.renderer.Camera2DRenderer.GLRendererType;

/**
 * Class responsible for handling a distance effect between the 2D world scene
 * and the camera.
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class GLXDistanceFrustum extends AbstractGLXEffect {
    
    /** distance. */
    private float distanceFrustum;
    
    /**
     * Generate a new instance of this class <code>GLXDistanceFrustum</code>.
     * @param distanceFrustum distance
     * @param followInterpolationAmount interpolation speed (amount)
     */
    public GLXDistanceFrustum(float distanceFrustum, float followInterpolationAmount) {
        this.distanceFrustum = distanceFrustum;
        this.interpolationAmount = followInterpolationAmount;
    }

    /**
     * Returns the distance
     * @return float
     */
    public float getDistanceFrustum() {
        return distanceFrustum;
    }

    /**
     * Set a new distance
     * @param distanceFrustum float
     */
    @SuppressWarnings("deprecation")
    public void setDistanceFrustum(float distanceFrustum) {
        this.distanceFrustum = distanceFrustum;        
        if (camera.getType() == GLRendererType.GLX_25D || camera.getType() == GLRendererType.GL_2D) {
            final Camera cam = camera.getCamera();
            float aspect     = (float) cam.getWidth() / (float )cam.getHeight();
            cam.setFrustum(-1000, 1000, -aspect * distanceFrustum, aspect * distanceFrustum, distanceFrustum, -distanceFrustum);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.effect.AbstractGLXEffect#effectUpdate(float) 
     */
    @Override
    protected void effectUpdate(float tpf) {
        Camera cam   = camera.getCamera();        
        Vector3f loc = cam.getLocation();
        
        if (interpolationAmount > 0) {
            float changeAmount = tpf * interpolationAmount;
            cam.setLocation(loc.setZ((1.0F - changeAmount) * loc.z + changeAmount * distanceFrustum));
        } else {
            cam.setLocation(loc.setZ(distanceFrustum));
        }
    }
}
