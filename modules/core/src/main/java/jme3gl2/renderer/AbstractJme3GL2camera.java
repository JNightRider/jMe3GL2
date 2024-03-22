package jme3gl2.renderer;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase <code>AbstractjMe3GL2camera</code> encargado de implementar la interfaz
 * {@link Jme3GL2Camera} donde se prepara una plantilla para el gestionamiento
 * de propiedades.
 * <p>
 * Esta clase solo proporciona una plantilla para administrar la cámara en las
 * escenas.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT 
 * 
 * @since 2.0.5
 */
@SuppressWarnings(value = {"unchecked"})
public abstract 
class AbstractJme3GL2camera implements Jme3GL2Camera {
    
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
    protected static 
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
         * 
         * @param isWorld <code>true</code> si se desea obtener la posición
         * mundial-global del objetivo, de lo contrario <code>false</code> para
         * los posición local-relativa.
         * 
         * @return traslación del objetivo.
         */
        public Vector3f getLocalTranslation(boolean isWorld) {
            if (this.value == null) {
                return this.aux;
            }
            return (isWorld) ? this.value.getWorldTranslation() : this.value.getLocalTranslation();
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
    
    /**
     * Mapa de atributos para el gestionamiento de la cámara.
     */
    private final Map<String, Object> attributes;
    
    /** Objeto-objetivo. */
    protected final Target<Spatial> target;

    /**
     * Constructor de la clase <code>AbstractjMe3GL2camera</code> donde se
     * inicializar las propiedades-atributos de este objeto instanciado.
     */
    public AbstractJme3GL2camera() {
        this.attributes = new HashMap<>();
        this.target     = new Target<>();
    }

    /**
     * (non-JavaDoc)
     * @see Jme3GL2Camera#setTarget(com.jme3.scene.Spatial) 
     */
    @Override
    public <T extends Spatial> void setTarget(T target) {
        this.target.setValue(target);
    }
    
    /**
     * (non-JavaDoc)
     * @see Jme3GL2Camera#setProperty(java.lang.String, java.lang.Object) 
     */
    @Override
    public <T> Jme3GL2Camera setProperty(String key, T value) {
        if (key == null) {
            throw new IllegalArgumentException("Invalid attribute key.");
        }
        this.attributes.put(key, value);
        return this;
    }

    /**
     * (non-JavaDoc)
     * @see Jme3GL2Camera#getProperty(java.lang.String, java.lang.Object) 
     */
    @Override
    public <T> T getProperty(String key, T defaultVal) {
        Object value = this.attributes.get(key);
        if (value == null) {
            return defaultVal;
        }
        return (T) value;
    }

    /**
     * Método encargado de generar un mensage de texto con las información de las
     * propiedades utilizadas por el gestor de cámara.
     * @return mensage-cadena.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[ Class ]: ").append(getClass().getName());     
        builder.append('\n');
        for (final Map.Entry<?,?> entry : this.attributes.entrySet()) {
            builder.append(" *").append(' ');
            builder.append(entry.getKey()).append(": ")
                   .append(entry.getValue()).append('\n');
        }
        return String.valueOf(builder);
    }
}
