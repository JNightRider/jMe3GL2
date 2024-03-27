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
package jme3gl2.renderer;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.renderer.Camera;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3gl2.renderer.effect.GLXDistanceFrustum;
import jme3gl2.renderer.effect.GLXFollowing;
import jme3gl2.renderer.effect.GLXEffect;

/**
 *
 * @author wil
 */
public class Camera2DRenderer extends BaseAppState {

    private static final Logger LOGGER = Logger.getLogger(Camera2DRenderer.class.getName());
    
    public static enum GLRendererType {
        GLX_25D,
        
        GLX_30D,
        
        @Deprecated
        GL_2D,
        @Deprecated
        GL_3D;
    }

    private GLRendererType rendererType;
    private GLXCamera xCamera;
    
    public Camera2DRenderer(GLRendererType rendererType) {
        if (rendererType == null) {
            throw new NullPointerException("Render type cannot be null");
        }
        
        switch (rendererType) {
            case GLX_25D: case GL_2D:
                xCamera = new GLXCamera25D();
                break;
            case GLX_30D: case GL_3D:
                xCamera = new GLXCamera30D();
                break;
            default:
                throw new AssertionError();
        }
        this.rendererType = rendererType;
    }
        
    public Camera2DRenderer(GLRendererType rendererType, GLXEffect ...effects) {
        this(rendererType);
        for (final GLXEffect ce : effects) {
            xCamera.addEffect(ce);
        }
    }
    
    public Camera2DRenderer(GLRendererType rendererType, float cameraDistanceFrustum, float followInterpolationAmount) {
        this(rendererType, new GLXFollowing(followInterpolationAmount), new GLXDistanceFrustum(cameraDistanceFrustum, 0));
    }
    
    @Override
    protected void initialize(Application app) {
        final Camera camera = app.getCamera();
        if (app instanceof SimpleApplication) {
            SimpleApplication simpleApplication = (SimpleApplication) app;
            simpleApplication.getFlyByCamera().setEnabled(false);
            simpleApplication.getFlyByCamera().unregisterInput();
            
            LOGGER.log(Level.INFO, "PlatformerCameraState is removing default fly camera");
        }
        xCamera.setCamera(camera);
    }

    @Override
    public void update(float tpf) {
        xCamera.update(tpf);
    }

    @Override
    protected void cleanup(Application app) { }

    public GLXCamera getGLXCamera() {
        return xCamera;
    }

    public void addEffect(GLXEffect effect) {
        xCamera.addEffect(effect);
    }

    public void removeEffect(GLXEffect effect) {
        xCamera.removeEffect(effect);
    }
    
    public <T extends GLXEffect> void removeEffect(Class<T> clazz) {
        removeEffect(getEffect(clazz));
    }

    public <T extends GLXEffect> T getEffect(Class<T> clazz) {
        return xCamera.getEffect(clazz);
    }

    @Override
    protected void onEnable() {
        if (xCamera instanceof AbstractGLXCamera) {
            ((AbstractGLXCamera) xCamera).setEnabled(true);
        }
    }

    @Override
    protected void onDisable() {
        if (xCamera instanceof AbstractGLXCamera) {
            ((AbstractGLXCamera) xCamera).setEnabled(false);
        }
    }

    public GLRendererType getRendererType() {
        return rendererType;
    }
}
