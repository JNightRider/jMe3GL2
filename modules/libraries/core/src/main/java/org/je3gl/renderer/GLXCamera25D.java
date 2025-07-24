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
package org.je3gl.renderer;

import com.jme3.math.Vector3f;
import org.je3gl.renderer.Camera2DRenderer.GLRendererType;
import org.je3gl.renderer.effect.GLXDistanceFrustum;

/**
 * Class in charge of handling a fake 2D camera.
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
final class GLXCamera25D extends AbstractGLXCamera {

    /**
     * Constructor
     */
    public GLXCamera25D() {
    }
    
    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.AbstractGLXCamera#initialize() 
     */
    @Override
    protected void initialize() {
        camera.setParallelProjection(true);
        
        float aspect = (float)camera.getWidth() / (float)camera.getHeight();
        float cameraDistanceFrustum = 10.0F;
        
        GLXDistanceFrustum glxdf = getEffect(GLXDistanceFrustum.class);
        if (glxdf != null) {
            cameraDistanceFrustum = glxdf.getDistanceFrustum();
        }
        
        camera.setFrustum(-1000, 1000, -aspect * cameraDistanceFrustum, aspect * cameraDistanceFrustum, cameraDistanceFrustum, -cameraDistanceFrustum);
        camera.setLocation(new Vector3f(0, 0, 0));
    }

    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.GLXCamera#getType() 
     */
    @Override
    public GLRendererType getType() {
        return GLRendererType.GLX_25D;
    }
}
