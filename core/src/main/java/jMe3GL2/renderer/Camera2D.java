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

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Un objeto de la clase <code>Camera2D</code> se encarga de gestionar el
 * comportamiento de la cámara predeterminada de los juegos <code>JME</code>
 * en 2D.
 * <p>
 * Con esta clase podemos simular un mundo en dos dimensiones.
 * </p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT 
 * 
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
public class Camera2D {

    /**
     * Clase interna <code>Target</code> encargado de administrar el objetivo
     * de la cámara.
     * <p>
     * Esto se utiliza para mantener un registro del desplazamiento de la cámara
     * durante la ejecución del juego. 
     * 
     * Para evitar que la cámara se mueva en el
     * punto <code>(0,0,0)</code> al quitarle su objetivo.
     * </p>
     * 
     * @param <E> tipo de objetivo
     * @version 1.5.0
     */
    class Target<E extends Spatial> {
        
        /** Objetivo. */
        private E value;
        
        /**
         * Registro de desplazamiento de la cámara durante la ejecución
         * del juego.
         * 
         * <p>
         * Esto se utiliza cuando no hay un objetivo establecido por parte
         * del usuario.
         * </p>
         */
        private Vector3f aux;
        
        /**
         * Constructor predeterminado de la clase interna <code>Target</code>
         * donde se inicializa los objetos para el funcionamiento de este
         * nuevo objeto.
         */
        public Target() {
            this.aux = new Vector3f();
        }

        /**
         * Método encargado de establecer un nuevo objetivo.
         * @param value nuevo-objetivo.
         */
        public void setValue(E value) {
            if (value == null && this.value != null) {
                this.aux = this.value.getLocalTranslation().clone();
            }
            this.value = value;
        }
        
        /**
         * Devuelve la posición del objetivo actual, de lo contrario será
         * el registro de ello.
         * @return traslación del objetivo.
         */
        public Vector3f getLocalTranslation() {
            if (this.value == null) {
                return this.aux;
            }
            return this.value.getLocalTranslation();
        }
        
        /**
         * Devuelve el objetivo si la hay, de lo contrario el valor es
         * <code>null</code>.
         * @return objetivo.
         */
        public E getValue() {
            return value;
        }
    }
    
    /** Logger de la clase. */
    private static final Logger LOG = Logger.getLogger(Camera2D.class.getName());
    
    /**
     * Mapa de atributos para el gestionamiento de la cámara.
     */
    private final Map<String, Object> attributes;
    
    /** Objeto-objetivo. */
    private final Target<Spatial> target;
    
    /** Posición de la cámara. */
    private final Vector3f position;
    
    /** Recortes de la cámara. */
    private final Clipping clipping;
    
    /** Camara-3D a gestionar en 2D. */
    private Camera camera3D;

    /**
     * Constructor predeterminado de la clase <code>Camera2D</code> donde se
     * inicializa los objeto a utiliza.
     */
    public Camera2D() {
        this.attributes = new HashMap<>();
        this.position = new Vector3f();
        this.clipping = new Clipping();
        this.target   = new Target<>();
        
        Camera2D.this.putAttribute("FollowInterpolationAmount", 0.2F);
        Camera2D.this.putAttribute("CameraDistanceFrustum", 10.0F);
    }

    /**
     * Método encargado de establecer la cámara a gestionar.
     * @param camera3D cámara-3D a gestionar en 2D.
     */
    void setCamera3D(Camera camera3D) {
        this.camera3D = camera3D;
    }
    
    /**
     * Método encargado de inicializar la cámara en 2D.
     * <p>
     * <b>NOTA:</b>
     * Esto solo es accesible por clases internas o por está misma biblioteca.
     * </p>
     */
    void initialize() {
        // Debemos cambiar la proyección de la cámara.
        camera3D.setParallelProjection(true);
        
        float aspect = (float) camera3D.getWidth() / camera3D.getHeight();
        float cameraDistanceFrustum = getAttribute("CameraDistanceFrustum", 10.0F);
        
        camera3D.setFrustum(-500, 500, -aspect * cameraDistanceFrustum, aspect * cameraDistanceFrustum, cameraDistanceFrustum, -cameraDistanceFrustum);
        camera3D.setLocation(new Vector3f(0, 0, 0));
        
        LOG.log(Level.INFO, "2D camera initialized:"
                + "\n[ SET ] :FollowInterpolationAmount >> {0}"
                + "\n[ SET ] :CameraDistanceFrustum     >> {1}",
                new Object[]{getAttribute("FollowInterpolationAmount", 0.2F), 
                             cameraDistanceFrustum});
    }
    
    /**
     * Método encargado de las actualizaciones de la cámara.
     * @param tpf tiempo-frames.
     */
    public void update(float tpf) {
        Vector3f translation = target.getLocalTranslation();
        Vector2f pos = clipping.clamp(translation.x, translation.y);
        
        position.set(pos.x, pos.y, getAttribute("CameraDistanceFrustum", 10.0F));
        camera3D.setLocation(camera3D.getLocation().interpolateLocal(position, getAttribute("FollowInterpolationAmount", 0.2F)));
    }
    
    /**
     * Método encargado de establecer una nueva distancia para la cámara, dicho
     * valor se usas para alejar o acercar la cámara de la escenas.
     * @param frustum distancia-cámara.
     */
    public void setCameraDistanceFrustum(float frustum) {
        float aspect = (float) camera3D.getWidth() / camera3D.getHeight();
        camera3D.setFrustum(-1000, 1000, -aspect * frustum, aspect * frustum, frustum, -frustum);
        putAttribute("CameraDistanceFrustum", frustum);
    }
    
    /**
     * Método encargado de agregar un atributo nuvo o bien reescribir uno existente.
     * @param <T> tipo de dato.
     * @param key nombre clave del atributo.
     * @param value valor del atributo.
     */
    public <T extends Object> void putAttribute(String key, T value) {
        if (key == null) {
            throw new IllegalArgumentException("Invalid attribute key.");
        }
        this.attributes.put(key, value);
    }
    
    /**
     * Método encargado de establecer un nuevo objetivo.
     * @param <T> tipo de dato.
     * @param target objetivo-cámara
     */
    public <T extends Spatial> void setTarget(T target) {
        this.target.setValue(target);
    }
    
    /**
     * Devuelve un atributo de la cámara.
     * @param <T> tipo de dato.
     * @param key nombre clave.
     * @param defaultVal valor por defecto.
     * @return valor-atributo.
     */
    public <T extends Object> T getAttribute(String key, T defaultVal) {
        Object value = this.attributes.get(key);
        if (value == null) {
            return defaultVal;
        }
        return (T) value;
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
     * Devuelve el objeto de recorte.
     * @return recortes,
     */
    public Clipping getClipping() {
        return clipping;
    }
}
