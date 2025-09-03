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

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.je3gl.renderer.effect.GLXClipping;
import org.je3gl.renderer.effect.GLXDistanceFrustum;
import org.je3gl.renderer.effect.GLXFollowing;
import org.je3gl.renderer.effect.GLXEffect;

/**
 * An object of the class <code>Camera2DState</code> is responsible for preparing
 * and operating the 2D camera.
 * <p>
 * With this we can use the 3D camera that provides us with {@code JME} in a 2D
 * world.
 * 
 * @author wil
 * @version 2.5.0
 * @since 2.0.0
 */
public class Camera2DRenderer extends BaseAppState {
    /** Logger class. */
    private static final Logger LOGGER = Logger.getLogger(Camera2DRenderer.class.getName());
    
    /**
     * Internal enumerated class <code>GLRendererType</code> responsible for listing 
     * the types of camera managers.
     * 
     * @version 1.5.0
     */
    public static enum GLRendererType {
        
        /**
         * It uses a manager where it places the camera in a parallel projection.
         * <p>
         * Objects will never touch the camera, i.e. no matter how close they
         * get, they cannot reach the front view.
         */
        GLX_25D,
        
        /**
         * It uses a manager where the 3D camera is used to ensure a more
         * realistic approach.
         */
        GLX_30D,
        
        /* Use {@link GLRendererType#GLX_25D}. */
        @Deprecated
        GL_2D,
        /* Use {@link GLRendererType#GLX_30D}. */
        @Deprecated
        GL_3D;
    }

    /** Type of manager/camera. */
    private GLRendererType rendererType;
     /** 2D camera. */
    private GLXCamera xCamera;
    
    /**
     * Class constructor <code>Camera2DRenderer</code> where the type of
     * manager/camera is specified.
     * @param rendererType type of manager/camera
     */
    protected Camera2DRenderer(GLRendererType rendererType) {
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
    
    /**
     * Constructor of class <code>Camera2DRenderer</code>.
     * @param rendererType type of manager/camera
     * @param effects camera effects
     */
    public Camera2DRenderer(GLRendererType rendererType, GLXEffect ...effects) {
        this(rendererType);
        for (final GLXEffect ce : effects) {
            xCamera.addEffect(ce);
        }
    }
    
     /**
     * Generates an object of the class <code>Camera2DRenderer</code> with the
     * main characteristics to be managed.
     * 
     * @param rendererType camera rendering type
     * @param cameraDistanceFrustum camera distance
     * @param followInterpolationAmount interpolation speed
     */
    public Camera2DRenderer(GLRendererType rendererType, float cameraDistanceFrustum, float followInterpolationAmount) {
        this(rendererType, new GLXFollowing(followInterpolationAmount), new GLXDistanceFrustum(cameraDistanceFrustum, 0), new GLXClipping(null, null));
    }
    
    /*
     *(non-Javadoc)
     * @see com.jme3.app.state.BaseAppState#initialize(com.jme3.app.Application) 
     */
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
    
    /*
     * (non-Javadoc)
     * @see com.jme3.app.state.BaseAppState#update(float)
     */
    @Override
    public void update(float tpf) {
        xCamera.update(tpf);
    }

    /*
     * (non-Javadoc)
     * @see com.jme3.app.state.BaseAppState#cleanup(com.jme3.app.Application) 
     */
    @Override
    protected void cleanup(Application app) { }

    /**
     * Method in charge of returning the fake 2D camera manager
     * @return camera manager
     */
    public GLXCamera getGLXCamera() {
        return xCamera;
    }

    /**
     * Agrega un effecto a la camara.
     * @param effect camera-effect
     */
    public void addEffect(GLXEffect effect) {
        xCamera.addEffect(effect);
    }

    /**
     * Remove a camera effect.
     * @param effect camera-effect
     */
    public void removeEffect(GLXEffect effect) {
        xCamera.removeEffect(effect);
    }
    
    /**
     * Delete a camera effect through its class.
     * @param <T> type
     * @param clazz effect-class
     */
    public <T extends GLXEffect> void removeEffect(Class<T> clazz) {
        removeEffect(getEffect(clazz));
    }

    /**
     * Returns a camera effect through its class.
     * @param <T> type
     * @param clazz effect-class
     * @return effect
     */
    public <T extends GLXEffect> T getEffect(Class<T> clazz) {
        return xCamera.getEffect(clazz);
    }

    /*
     * (non-Javadoc)
     * @see com.jme3.app.state.BaseAppState#onEnable()
     */
    @Override
    protected void onEnable() {
        if (xCamera instanceof AbstractGLXCamera) {
            ((AbstractGLXCamera) xCamera).setEnabled(true);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.jme3.app.state.BaseAppState#onDisable()
     */
    @Override
    protected void onDisable() {
        if (xCamera instanceof AbstractGLXCamera) {
            ((AbstractGLXCamera) xCamera).setEnabled(false);
        }
    }

    /**
     * Returns the type of camera used.
     * @return rendering type
     */
    public GLRendererType getRendererType() {
        return rendererType;
    }
}
