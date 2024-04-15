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

import com.jme3.math.Vector3f;
import org.jegl.renderer.Camera2DRenderer.GLRendererType;
import org.jegl.renderer.effect.GLXDistanceFrustum;

/**
 * Class in charge of managing the 3D camera of the scene in a fake 2D world.
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
final class GLXCamera30D extends AbstractGLXCamera {

    /**
     * Constructor
     */
    public GLXCamera30D() {
    }
    
    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.AbstractGLXCamera#initialize() 
     */
    @Override
    protected void initialize() {
        camera.setParallelProjection(false);
        camera.setFrustumPerspective(45.0F, (float)camera.getWidth() / (float)camera.getHeight(), 1.0F, 1000.0F);
        
        float cameraDistanceFrustum = 10.0F;        
        GLXDistanceFrustum glxdf = getEffect(GLXDistanceFrustum.class);
        if (glxdf != null) {
            cameraDistanceFrustum = glxdf.getDistanceFrustum();
        }
        
        camera.setLocation(new Vector3f(0.0F, 0.0F, cameraDistanceFrustum));
        camera.lookAt(new Vector3f(0.0F, 0.0F, 0.0F), Vector3f.UNIT_Y);     
    }
    
    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.GLXCamera#getType() 
     */
    @Override
    public GLRendererType getType() {
        return GLRendererType.GLX_30D;
    }
}
