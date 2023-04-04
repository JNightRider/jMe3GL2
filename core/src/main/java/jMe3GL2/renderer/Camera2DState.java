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
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

import java.util.logging.Logger;

/**
 * Un objeto de la clase <code>Camera2DState</code> se encarga de preparar y
 * manejar la camara en 2D.
 * <p>
 * Con esto podemos utilizar la camara 3D que nos proprociona {@code jme3} en
 * un plano 2D.</p>
 * 
 * @version 1.0-SNAPSHOT
 * @since 1.0.0
 */
public class Camera2DState extends BaseAppState {
    
    /** Logger de la clase. */
    private static final Logger LOG = Logger.getLogger(Camera2DState.class.getName());

    /** Objetivo de la cámara. */
    private final TargetCamera<Spatial> targetCamera;
    
    /** Velocidad de interpolación. */
    private float followInterpolationAmount = 0.2f;
    
    /** Distancia de la cámara.*/
    private float cameraDistanceFrustum = 10f;
    
    /** Punto de atraso o adelanto. */
    private Vector2f targetOffset = new Vector2f(0, 0);
    
    /** Recorte minimo. */
    private Vector2f minimumClipping;
    
    /** Recorte maximo. */
    private Vector2f maxClipping;

    /** Posición objetivo. */
    private final Vector3f targetPosition = new Vector3f(0, 0, 0);
    
    /** cámara. */
    private Camera camera;

    /**
     * Constructor de la clase <code>Camera2DState</code>.
     * @param cameraDistanceFrustum distancia cámara.
     * @param followInterpolationAmount velociad de interpolación.
     */
    public Camera2DState(float cameraDistanceFrustum, float followInterpolationAmount) {
        this(null, cameraDistanceFrustum, followInterpolationAmount);
    }
    
    /**
     * Constructor de la clase <code>Camera2DState</code>.
     * @param target objetivo cámara.
     * @param cameraDistanceFrustum distancia cámara.
     * @param followInterpolationAmount velociad de interpolación.
     */
    public Camera2DState(Spatial target, float cameraDistanceFrustum, float followInterpolationAmount) {
        this.targetCamera = new TargetCamera<>(target);
        this.cameraDistanceFrustum = cameraDistanceFrustum;
        this.followInterpolationAmount = followInterpolationAmount;
        
        
    }

    /**
     * (non-JavaDoc)
     * @param app Application.
     * @see BaseAppState#initialize(com.jme3.app.Application) 
     */
    @Override
    protected void initialize(Application app) {
        camera = app.getCamera();

        //Primero necesitamos eliminar la cámara 
        // de vuelo predeterminada.
        if (app instanceof SimpleApplication) {
            SimpleApplication simpleApplication = (SimpleApplication) app;
            simpleApplication.getFlyByCamera().setEnabled(false);
            simpleApplication.getFlyByCamera().unregisterInput();
            LOG.info("PlatformerCameraState is removing default fly camera");
        }

        // A continuación, debemos cambiar la 
        // proyección de la cámara.
        camera.setParallelProjection(true);
        float aspect = (float) camera.getWidth() / camera.getHeight();
        camera.setFrustum(-500, 500, -aspect * cameraDistanceFrustum, aspect * cameraDistanceFrustum, cameraDistanceFrustum, -cameraDistanceFrustum);
        camera.setLocation(new Vector3f(0, 0, 0));

    }

    /**
     * (non-JavaDoc)
     * @param tpf float
     * @see BaseAppState#update(float) 
     */
    @Override
    public void update(float tpf) {
        super.update(tpf);

        //Actualizar la posición del objetivo de la cámara
        targetPosition.setZ(cameraDistanceFrustum);
        if (minimumClipping != null && maxClipping != null) {
            //Aplicar recorte a la posición 
            //de la cámara
            targetPosition.setX(FastMath.clamp((targetCamera.getTranslation().x + targetOffset.x), minimumClipping.x, maxClipping.x));
            targetPosition.setY(FastMath.clamp((targetCamera.getTranslation().y + targetOffset.y), minimumClipping.y, maxClipping.y));
        } else {
            targetPosition.setX(targetCamera.getTranslation().x + targetOffset.x);
            targetPosition.setY(targetCamera.getTranslation().y + targetOffset.y);
        }

        //Aquí necesitamos controlar la cámara 
        //para seguir al objetivo.
        camera.setLocation(camera.getLocation().interpolateLocal(targetPosition, followInterpolationAmount));
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

    /**
     * Establece una nueva distancia para la cámara.
     * @param frustrum nueva distancia.
     */
    public void setCameraDistanceFrustrum(float frustrum) {
        this.cameraDistanceFrustum = frustrum;
        float aspect = (float) camera.getWidth() / camera.getHeight();
        camera.setFrustum(-1000, 1000, -aspect * cameraDistanceFrustum, aspect * cameraDistanceFrustum, cameraDistanceFrustum, -cameraDistanceFrustum);
    }

    /**
     * Devuelve el objetivo de la cḿara.
     * @return objetivo.
     */
    public TargetCamera<Spatial> getTarget() {
        return targetCamera;
    }

    /**
     * Establece un nuevo objetivo para la cámara.
     * @param target nuevo objetivo.
     */
    public void setTarget(Spatial target) {
        this.targetCamera.setTarget(target);
    }

    /**
     * Devuelve la velocidad de interpolación.
     * @return velocidad interpolación.
     */
    public float getFollowInterpolationAmount() {
        return followInterpolationAmount;
    }

    /**
     * Establece una nueva velocidad de interpolación para la camara.
     * @param followInterpolationAmount nueva velocidad de interpolación.
     */
    public void setFollowInterpolationAmount(float followInterpolationAmount) {
        this.followInterpolationAmount = followInterpolationAmount;
    }

    /**
     * Devueleve el atraso o adelanto de la cámara.
     * @return adelanto/atraso cámara.
     */
    public Vector2f getTargetOffset() {
        return targetOffset;
    }

    /**
     * Estable un atraso o adelanto para la cmámara.
     * <p>
     * Esto se aplica respecto al objetivo, es decir que tan atraso o adelantado
     * estara la camara de su centro que es el objetivo.</p>
     * 
     * @param targetOffset un nuevo punto de desface.
     */
    public void setTargetOffset(Vector2f targetOffset) {
        if ( targetOffset == null ) {
            this.targetOffset = new Vector2f(0, 0);
        } else {
            this.targetOffset = targetOffset;
        }
    }

    /**
     * Método encargado de stablecer los recortes.
     * @param minimumClipping recorte minimo.
     * @param maxClipping recorte maximo.
     */
    public void setCameraClipping(Vector2f minimumClipping, Vector2f maxClipping) {
        this.minimumClipping = minimumClipping;
        this.maxClipping = maxClipping;
    }
}
