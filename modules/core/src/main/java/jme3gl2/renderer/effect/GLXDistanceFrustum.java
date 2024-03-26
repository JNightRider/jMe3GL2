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

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import jme3gl2.renderer.Camera2DRenderer.GLRendererType;

/**
 *
 * @author wil
 */
public class GLXDistanceFrustum extends AbstractGLXEffect {
    
    private float distanceFrustum;
    private float followInterpolationAmount;

    public GLXDistanceFrustum(float distanceFrustum, float followInterpolationAmount) {
        this.distanceFrustum = distanceFrustum;
        this.followInterpolationAmount = followInterpolationAmount;
    }

    public float getDistanceFrustum() {
        return distanceFrustum;
    }

    public float getFollowInterpolationAmount() {
        return followInterpolationAmount;
    }

    public void setDistanceFrustum(float distanceFrustum) {
        this.distanceFrustum = distanceFrustum;        
        if (camera.getType() == GLRendererType.GLX_25D) {
            final Camera cam = camera.getCamera();
            float aspect     = (float) cam.getWidth() / (float )cam.getHeight();
            cam.setFrustum(-1000, 1000, -aspect * distanceFrustum, aspect * distanceFrustum, distanceFrustum, -distanceFrustum);
        }
    }

    public void setFollowInterpolationAmount(float followInterpolationAmount) {
        this.followInterpolationAmount = followInterpolationAmount;
    }
    
    @Override
    protected void effectUpdate(float tpf) {
        Camera cam   = camera.getCamera();        
        Vector3f loc = cam.getLocation();
        
        if (followInterpolationAmount > 0) {
            float changeAmount = tpf * followInterpolationAmount;
            cam.setLocation(loc.setZ((1.0F - changeAmount) * loc.z + changeAmount * distanceFrustum));
        } else {
            cam.setLocation(loc.setZ(distanceFrustum));
        }
    }
}
