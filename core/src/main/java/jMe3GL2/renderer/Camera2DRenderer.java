/* Copyright (c) 2009-2023 jMonkeyEngine.
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
package jMe3GL2.renderer;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

import java.util.logging.Logger;

/**
 * Un objeto de la clase <code>Camera2DState</code> se encarga de preparar y
 * manejar la cámara en 2D.
 * <p>
 * Con esto podemos utilizar la camara 3D que nos proprociona {@code JME} en
 * un mundo 2D.
 * </p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT 
 * 
 * @since 2.0.0
 */
public class Camera2DRenderer extends BaseAppState {

    /** Logger de la clase. */
    private static final Logger LOG = Logger.getLogger(Camera2DRenderer.class.getName());

    /** Camara-2D. */
    private Camera2D camera2D;
    
    /**
     * Constructor predeterminado.
     */
    public Camera2DRenderer() {
        super("Camera2DRenderer");
        this.camera2D = new Camera2D();
    }
    
    /**
     * Genera un objeto de la clase <code>Camera2DRenderer</code> con las
     * características principales a gestionar.
     * @param cameraDistanceFrustum distancia-cámara.
     * @param followInterpolationAmount velocidad-interpolación.
     */
    public Camera2DRenderer(float cameraDistanceFrustum, float followInterpolationAmount) {
        this();
        
        /*
            Simplemente agregamos los nuevos atributos del objeto 'Camara2D' 
            para porder inicializarlos despues.
        */
        this.camera2D.putAttribute("CameraDistanceFrustum", cameraDistanceFrustum);
        this.camera2D.putAttribute("FollowInterpolationAmount", followInterpolationAmount);
    }
    
    /**
     * (non-JavaDoc)
     * @param app {@code Application}
     * @see BaseAppState#initialize(com.jme3.app.Application) 
     */
    @Override
    protected void initialize(Application app) {
        final Camera camera = app.getCamera();
        if (app instanceof SimpleApplication) {
            SimpleApplication simpleApplication = (SimpleApplication) app;
            simpleApplication.getFlyByCamera().setEnabled(false);
            simpleApplication.getFlyByCamera().unregisterInput();
            LOG.info("PlatformerCameraState is removing default fly camera");
        }
        
        camera2D.setCamera3D(camera);
        camera2D.initialize();
    }

    /**
     * (non-JavaDoc)
     * @param tpf {@code float}
     * @see BaseAppState#update(float) 
     * @see Camera2D#update(float) 
     */
    @Override
    public void update(float tpf) {
        camera2D.update(tpf);
    }

    /**
     * (non-JavaDoc)
     * @param s {@code Spatial}
     * @see Camera2D#setTarget(com.jme3.scene.Spatial) 
     */
    public void setTarget(Spatial s) {
        camera2D.setTarget(s);
    }
    
    /**
     * Método encargado de establecer la velocidad de interpolación de la
     * cámara en 2D.
     * <p>
     * Esta velocidad se utiliza para mover la cámara hacia su objetivo si
     * tiene uno establecido.
     * </p>
     * @param follow valor-interpolación.
     */
    public void setFollowInterpolationAmount(float follow) {
        camera2D.putAttribute("FollowInterpolationAmount", follow);
    }

    /**
     * (non-JavaDoc)
     * @param frustum {@code float}
     * @see Camera2D#setCameraDistanceFrustum(float) 
     */
    public void setDistanceFrustum(float frustum) {
        camera2D.setCameraDistanceFrustum(frustum);
    }
    
    /**
     * Establece nuevos valores de recorte para la cámara.
     * <p>
     * Si se desea quitar los recortes, con valores <code>null</code> como
     * párametro se borrarám.
     * </p>
     * 
     * @param minimumClipping recorte-minimo.
     * @param maxClipping recorte-maximo.
     */
    public void setClipping(Vector2f minimumClipping, Vector2f maxClipping) {
        camera2D.getClipping().setMinimum(minimumClipping);
        camera2D.getClipping().setMaximum(maxClipping);
    }
    
    /**
     * (non-JavaDoc)
     * @param offset {@code Vector2f}
     * @see Clipping#setOffset(com.jme3.math.Vector2f) 
     */
    public void setOffset(Vector2f offset) {
        camera2D.getClipping().setOffset(offset);
    }
    
    /**
     * Devuelve el objeto que administra la cámara en 2D.
     * @return cámara-2D.
     */
    public Camera2D getCamera2D() {
        return camera2D;
    }

    /**
     * (non-JavaDoc)
     * @param app  application
     * @see BaseAppState#cleanup(com.jme3.app.Application) 
     */
    @Override protected void cleanup(Application app) { }
    
    /**
     * (non-JavaDoc)
     * @see BaseAppState#onEnable() 
     */
    @Override protected void onEnable() { }
    
    /**
     * (non-JavaDoc)
     * @see BaseAppState#onDisable() 
     */
    @Override protected void onDisable() { }   
}
