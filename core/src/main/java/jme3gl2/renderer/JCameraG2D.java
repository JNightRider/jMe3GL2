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
package jme3gl2.renderer;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Un objeto de la clase <code>JCameraG2D</code> se encarga de gestionar el
 * comportamiento de la cámara predeterminada de los juegos <code>JME</code>
 * en 2D.
 * <p>
 * Con esta clase podemos simular un mundo en dos dimensiones.
 * </p>
 * 
 * @author wil
 * @version 2.0
 * 
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
public class JCameraG2D extends AbstractjMe3GL2camera {
    
    /** Logger de la clase. */
    private static final Logger LOG = Logger.getLogger(JCameraG2D.class.getName());
    
    /** Posición de la cámara. */
    private final Vector3f position;
    
    /** Recortes de la cámara. */
    private final Jme3GL2Clipping clipping;
    
    /** Camara-3D a gestionar en 2D. */
    private Camera camera3D;

    /**
     * Constructor predeterminado de la clase <code>Camera2D</code> donde se
     * inicializa los objeto a utiliza.
     */
    public JCameraG2D() {
        this.position = new Vector3f();
        this.clipping = new Jme3GL2Clipping();
        
        JCameraG2D.this.setProperty("FollowInterpolationAmount", 0.2F);
        JCameraG2D.this.setProperty("CameraDistanceFrustum", 10.0F);
    }
    
    /**
     * (non-JavaDoc)
     */
    @Override
    public void initialize(Camera cam) {
        // Debemos cambiar la proyección de la cámara.
        camera3D = cam;
        camera3D.setParallelProjection(true);
        
        float aspect = (float) camera3D.getWidth() / camera3D.getHeight();
        float cameraDistanceFrustum = getProperty("CameraDistanceFrustum", 10.0F);
        
        camera3D.setFrustum(-1000, 1000, -aspect * cameraDistanceFrustum, aspect * cameraDistanceFrustum, cameraDistanceFrustum, -cameraDistanceFrustum);
        camera3D.setLocation(new Vector3f(0, 0, 0));
        
        LOG.log(Level.INFO, toString());
    }
    
    /**
     * (non-JavaDoc)
     */
    @Override
    public void update(float tpf) {
        Vector3f translation = target.getLocalTranslation(false);
        Vector2f pos = clipping.clamp(translation.x, translation.y);
        
        position.set(pos.x, pos.y, getProperty("CameraDistanceFrustum", 10.0F));
        if (getProperty("InterpolationByTPF", true)) {
            camera3D.setLocation(camera3D.getLocation().interpolateLocal(position, getProperty("FollowInterpolationAmount", 0.2F) * tpf));
        } else {
            camera3D.setLocation(camera3D.getLocation().interpolateLocal(position, getProperty("FollowInterpolationAmount", 0.2F)));
        }
    }
    
    /**
     * (non-JavaDoc)
     */
    @Override
    public void setCameraDistanceFrustum(float frustum) {
        float aspect = (float) camera3D.getWidth() / camera3D.getHeight();
        camera3D.setFrustum(-1000, 1000, -aspect * frustum, aspect * frustum, frustum, -frustum);
        setProperty("CameraDistanceFrustum", frustum);
    }
    
    /**
     * Devuelve el objetio de la cámara.
     * @param <T> tipo de dato.
     * @return objetivo.
     */
    public <T extends Spatial> T getTarget() {
        return (T) target.getValue();
    }

    /**
     * (non-JavaDoc)
     */
    @Override
    public Jme3GL2Clipping getClipping() {
        return clipping;
    }
}
