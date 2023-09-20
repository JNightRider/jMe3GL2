package jMe3GL2.renderer;

import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

/**
 * Interfaz <code>jMe3GL2Camera</code> encargado de listar los métodos para
 * el gestor de la cámara en 2D-Falos.
 * <p>
 * Los objetos-clases que implemente esta interfaz, se encargan de gestionar el
 * control de la cámara en la escenas del juego, normalmente es en un mundo
 * 3D con un enfoque 2D.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT 
 * 
 * @since 2.0.5
 */
public interface jMe3GL2Camera {
    
    /**
     * Método encargado de establecer una nueva distancia para la cámara, dicho
     * valor se usas para alejar o acercar la cámara de la escenas.
     * @param frustum distancia-cámara.
     */
    public void setCameraDistanceFrustum(float frustum);
    
    /**
     * Método encargado de preparar la cámara para que este pueda se utilizada
     * en las escenas del mundo 2D-Falso.
     * <p>
     * Solo se llama una sola vez este método, si se desea cambiar las propiedades
     * de la cámara se utliza los siguientes metodos:
     * {@link #setProperty(java.lang.String, java.lang.Object) }
     * {@link #getProperty(java.lang.String, java.lang.Object) }
     * 
     * @param cam cámara-escenas
     */
    void initialize(Camera cam);
    
    /**
     * Se encarga de actualizar los cambios para la cámara.
     * @param tpf laspo-tiempo.
     */
    void update(float tpf);
    
    /**
     * Devuelve un objeto recortador, donde se encarga de gestionar los
     * límites de la cámara.
     * @return objeto-recortador.
     */
    public jMe3GL2Clipping getClipping();
    
    /**
     * Método encargado de establecer un objetivo a seguir por la cámara en la
     * escena del juego.
     * @param <T> tipo-dato.
     * @param target objetivo-cámara
     */
    public <T extends Spatial> void setTarget(T target);
    
    /**
     * Establece una propiedad al manejador de la cámara, si es una propiedad
     * inexistente, simplemente es ingonador por el administrador.
     * 
     * @param <T> tipo-dato
     * @param key nombre clave de la propiedad.
     * @param value nuevo valor de la propiedad
     * @return devuelve {@link jMe3GL2Camera}.
     */
    public <T extends Object> jMe3GL2Camera setProperty(String key, T value);
    
    /**
     * Devuelve el valor de una propiedad según la clave de ello. Si no 
     * existe tal propiedad; se devuelve el valor predeterminado.
     * 
     * @param <T> tipo-dato
     * @param key nombre clave de la propiedad.
     * @param defaultVal valor predeterminado a devolver.
     * @return valor de la propiedad.
     */
    public <T extends Object> T getProperty(String key, T defaultVal);
    
    /**
     * Devuelve el valor de una propiedad según la clave de ello.
     * @see jMe3GL2Camera#getProperty(java.lang.String, java.lang.Object) 
     * 
     * @param <T> tipo-dato
     * @param key nombre clave de la propiedad.
     * @return valor de la propiedad.
     */
    public default <T extends Object> T getProperty(String key) {
        return getProperty(key, null);
    }
}
