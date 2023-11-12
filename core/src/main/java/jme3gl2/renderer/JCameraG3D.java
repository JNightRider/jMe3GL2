package jme3gl2.renderer;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Un objeto de la clase <code>JCameraG3D</code> se encarga de gestionar la
 * cámara en 3D.
 * <p>
 * Con este gestor de cámara podemos crera escenas minimalistas, es decir que
 * podemos acercar los sprites a la cámara lo mas que podemos para tener varias
 * perspectivas de la escena-mundo.
 * <p>
 * Para manejar las
 * propiedades del gestor de la cámara se pueden utilizar lo siguiente:
 * Lista de propiedades:
 * <ul>
 * <li><b>FollowInterpolationAmount</b>: Define la velociad de interpolación con
 * que se mueve la cámara</li>
 * <li><b>CameraDistanceFrustum</b>: Define la distancia de la cámra con los
 * objetos de la escena.</li>
 * <li><b>InterpolationByTPF</b>: <code>true</code> si se utiliza una transición
 * ademas de la interpolación normal, es decir el valor de interpolación es
 * multiplicado por los fps</li>
 * <li><b>SmoothingDepth</b>: <code>true</code> si se aplica la distancia sin
 * ninguna interpolación, de lo contrario <code>false</code> donde la distancia
 * de la cámara tiene una trancición. Esto es valído si <code>InterpolationByTPF</code>
 * no esta activa(<code>false</code>)</li>
 * </ul>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT 
 * 
 * @since 2.0.5
 */
public class JCameraG3D extends AbstractJme3GL2camera {

    /** Logger de la clase. */
    private static final Logger LOG = Logger.getLogger(JCameraG3D.class.getName());

    /** Posición de la cámara. */
    private final Vector3f position;
    
    /** Recortes de la cámara. */
    private final Jme3GL2Clipping clipping;
    
    /** Camara-3D a gestionar en 2D. */
    private Camera cam;

    /**
     * Constructor predeterminado de la clase <code>JCameraG3D</code> donde se
     * inicializa los objeto a utiliza.
     */
    public JCameraG3D() {
        this.position = new Vector3f(0.0F, 0.0F, 0.0F);
        this.clipping = new Jme3GL2Clipping();
        
        JCameraG3D.this.setProperty("FollowInterpolationAmount", 0.2F);
        JCameraG3D.this.setProperty("CameraDistanceFrustum", 10.0F);
    }
    
    /**
     * (non-JavaDoc)
     */
    @Override
    public void initialize(Camera cam) {
        cam.setParallelProjection(false);
        cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(0f, 0f, 10f));
        cam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);        
        this.cam = cam;
        LOG.log(Level.INFO, toString());
    }

    /**
     * (non-JavaDoc)
     */
    @Override
    public void update(float tpf) {
        Vector3f translation = target.getLocalTranslation(true);
        Vector2f pos = clipping.clamp(translation.x, translation.y);
        
        position.set(pos.x, pos.y, getProperty("CameraDistanceFrustum", 10.0F));
        if (getProperty("InterpolationByTPF", true)) {
            cam.setLocation(cam.getLocation().interpolateLocal(position, getProperty("FollowInterpolationAmount", 0.2F) * tpf));
        } else {
            Vector3f newpos = cam.getLocation().interpolateLocal(position, getProperty("FollowInterpolationAmount", 0.2F));
            if (getProperty("SmoothingDepth", true)) {
                cam.setLocation(newpos);
            } else {
                cam.setLocation(newpos.setZ(position.z));
            }            
        }
    }

    /**
     * (non-JavaDoc)
     */
    @Override
    public void setCameraDistanceFrustum(float frustum) {
        setProperty("CameraDistanceFrustum", frustum);
    }

    /**
     * (non-JavaDoc)
     */
    @Override
    public Jme3GL2Clipping getClipping() {
        return clipping;
    }
}
