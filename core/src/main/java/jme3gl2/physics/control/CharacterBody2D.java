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
package jme3gl2.physics.control;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;

import jme3gl2.physics.Dyn4jAppState;

import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.World;
import org.dyn4j.world.listener.ContactListener;
import org.dyn4j.world.listener.ContactListenerAdapter;
import org.dyn4j.world.listener.StepListener;
import org.dyn4j.world.listener.StepListenerAdapter;

import java.util.List;

/**
 * Clase <code>CharacterBody2D</code> especializada para cuerpos físicos que 
 * deben ser controlados por el usuario. Proporciona métodos para la detección 
 * de la hubicación del cuero(piso, techo y pared).
 * <p>
 * Cada objeto que se instancie de esta clase debe contar con un identificador
 * unico que se aloja en <code>UserData</code> que proprociona <code>dyn4j</code>,
 * para gestionar los contactos físicos, determina que badera se activan, etc.
 * <br>
 * Si un <code>CharacterBody2D</code> colisicona o hace contacto con otro
 * <code>CharacterBody2D</code> y están en la misma capa, la colisicón de 
 * cuerpos físico se desactiva, es decir que no chocan.
 * <p>
 * Para deterctar si un cuerpo físico hace contacto con un <code>CharacterBody2D</code>
 * y que dicho objeto es un cuerpo que forma parte del terreno, es necesario
 * que esté objeto tenga como dato de usuario lo siguiente:
 * <ul>
 * <li><b>FLOOR:</b> Un cuerpo físico que actua como terreno</li>
 * <li><b>ONE_WAY_PLATFORM:</b> Un cuerpo físico que actua como plataforma, que
 * se puede traspasarla</li>
 * </ul>
 * 
 * NOTA: Solo los cuerpos  que formar parte del terreno-mapa del mundo puede
 * tener estos datos de usuario (repetidos).
 * 
 * @author wil
 * @param <E> tipo aplicación
 * 
 * @version 1.1-SNAPSHOT
 * @since 1.0.0
 * 
 */
@SuppressWarnings("unchecked")
public class CharacterBody2D<E extends Application> extends PhysicsBody2D {

    /**
     * Este objeto se puede utiliza como identificador para un solo objeto
     * del tipo {@link CharacterBody2D}, evite utilizaro si es posible para no
     * generar problemas a futuro.
     */
    public static final Object CHARACTER = new Object();
    
    /**
     * Identificador para cuerpos físico que forman parte del terreno del
     * mapa escena.
     */
    public static final Object FLOOR = new Object();
    
    /**
     * Identificador para cuerpos físico que forman parte del terreno (plataformas) 
     * del mapa escena.
     */
    public static final Object ONE_WAY_PLATFORM = new Object();

    /** * Error aceptable para la detección de contactos. */
    private static final double ERROR = 0.01;

    /**
     * Objeto encargado de limpiar las banderas si este <code>CharacterBody2D</code>
     * tiene como identificador:
     * <ul>
     * <li>FLOOR</li>
     * <li>ONE_WAY_PLATFORM</li>
     * </ul>
     */
    protected final StepListener<PhysicsBody2D> physicsBody2DStepListener = new StepListenerAdapter<>() {
        @Override
        public void begin(TimeStep step, PhysicsWorld<PhysicsBody2D, ?> world) {
            boolean isGround = false;
            List<ContactConstraint<PhysicsBody2D>> contacts = world.getContacts(CharacterBody2D.this);
            for (ContactConstraint<PhysicsBody2D> cc : contacts) {
                if (is(cc.getOtherBody(CharacterBody2D.this), FLOOR, ONE_WAY_PLATFORM) && cc.isEnabled()) {
                    isGround = true;
                }
            }

            // solo límpialo
            if (!isGround) {
                onGround = onCeiling = onWall = false;
            }
        }
    };

    /**
     * Oyente encargdo de verificar, administrar las colisiciones-contactos
     * de cada cuerpo con este <code>CharacterBody2D</code>.
     */
    protected final ContactListener<PhysicsBody2D> physicsBody2DContactListener = new ContactListenerAdapter<>() {
        @Override
        public void collision(ContactCollisionData<PhysicsBody2D> collision) {
            ContactConstraint<PhysicsBody2D> cc = collision.getContactConstraint();

            // configure el otro cuerpo en unidireccional si es necesario
            disableContactForOneWay(cc);

            // seguimiento del estado en tierra
            trackIsOnGround(cc);
        }
    };

    /**
     * <code>true</code> si este cuerpo físico esta aterrizado en tierra, de
     * lo contrario su valor es <code>false</code>.
     */
    private boolean onGround;
    
    /**
     * <code>true</code> si este cuerpo físico hace contacto con el techo(parte baja)
     * de otro cuerpo, lo contrario su valor es <code>false</code>.
     */
    private boolean onCeiling;
    
    /**
     * <code>true</code> si este cuerpo físico esta en contacto con las paredes
     * de otro cuerpo, lo contrario su valor es <code>false</code>.
     */
    private boolean onWall;

    /**
     * Capa donde pertenece este cuerpo físico, esto se utiliza para verificar
     * los contactos dentre cuerpos. Si 2 cuerpo colisionan y son del tipo
     * <code>CharacterBody2D</code> que estan en la misma capa, simplemente
     * pasan de largo.
     */
    private int layer = 0;

    /** Aplicación JME. */
    protected E application;
    
    /** Estado encargado de administar la física. */
    protected Dyn4jAppState<PhysicsBody2D> dyn4jAppState;

    /**
     * Genere un <code>CharacterBody2D</code> para controlar un personaje 
     * capas de detercar si esta en el piso, techo o pared.
     * 
     * @param application aplicación principal
     */
    public CharacterBody2D(E application) {
        this.application = application;
        
        final AppStateManager stateManager = application.getStateManager();
        dyn4jAppState = stateManager.getState(Dyn4jAppState.class);
    }

    // getters/settes
    
    /**
     * Devuelve la capa perteneciente para este cuerpo físico.
     * @return int
     */
    public int getLayer() {
        return layer;
    }

    /**
     * Establece una nueva capa para este cuerpo físico.
     * @param layer int
     */
    public void setLayer(int layer) {
        this.layer = layer;
    }

    /**
     * (non-JavaDoc)
     * @see jme3gl2.physics.control.PhysicsBody2D#setSpatial(com.jme3.scene.Spatial) 
     * 
     * @param spatial Spatial
     */
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        final World<PhysicsBody2D> physicsBody2DWorld = dyn4jAppState.getPhysicsSpace().getPhysicsWorld();
        physicsBody2DWorld.addStepListener(physicsBody2DStepListener);
        physicsBody2DWorld.addContactListener(physicsBody2DContactListener);
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.dyn4j.DataContainer#getUserData()
     * @return Object
     */
    @Override
    public Object getUserData() {
        Object object = super.getUserData();
        if (object == null) {
            return CHARACTER;
        }
        return object;
    }

    /**
     * Método auxiliar para determinar si un cuerpo es uno de los tipos dados, asumiendo
     * el tipo se almacena en los datos del usuario.
     *
     * @param body el cuerpo
     * @param types el conjunto de tipos
     * @return boolean
     */
    protected boolean is(PhysicsBody2D body, Object... types) {
        for (Object type : types) {
            if (body.getUserData() == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * Devuelve <code>true</code> si la plataforma dada debe alternarse como 
     * unidireccional dada la posición del cuerpo del personaje.
     * 
     * @param character el cuerpo del personaje
     * @param platform el cuerpo de la plataforma
     * @return boolean
     */
    protected boolean allowOneWayUp(PhysicsBody2D character, PhysicsBody2D platform) {
        AABB wAABB = character.createAABB();
        AABB pAABB = platform.createAABB();
        
        // NOTA: esto debería cambiar según la forma de la plataforma y su orientación.
        //
        // Una idea podría ser almacenar la normal permitida de la plataforma
        // en el cuerpo de la plataforma y compararla con la normal de 
        // ContactConstraint para ver si apuntan en la misma dirección.
        //
        // Otra opción podría ser proyectar ambos en la plataforma normal para
        // ver dónde se superponen.
        return wAABB.getMinY() < pAABB.getMinY();
    }

    /**
     * Desactiva la restricción si es entre el personaje y la plataforma y
     * si el escenario cumple la condición de unidireccional.
     * 
     * @param contactConstraint la restricción
     */
    protected void disableContactForOneWay(ContactConstraint<PhysicsBody2D> contactConstraint) {
        PhysicsBody2D b1 = contactConstraint.getBody1();
        PhysicsBody2D b2 = contactConstraint.getBody2();

        if ((b1 instanceof CharacterBody2D<?>) && (b2 instanceof CharacterBody2D<?>)) {
            if (b1.getUserData() == b2.getUserData()) {
                throw new IllegalStateException("Each CharacterBody2D<?> must have a unique [UserData] to identify itself.");
            }

            int layerB1 = ((CharacterBody2D) b1).getLayer(),
                layetB2 = ((CharacterBody2D) b2).getLayer();

            if (layerB1 == layetB2) {
                contactConstraint.setEnabled(false);
                return;
            }
        }

        if (is(b1, getUserData()) && is(b2, ONE_WAY_PLATFORM)) {
            if (allowOneWayUp(b1, b2) || isActiveButNotHandled()) {
                setHasBeenHandled(true);
                contactConstraint.setEnabled(false);
            }
        } else if (is(b1, ONE_WAY_PLATFORM) && is(b2, getUserData())) {
            if (allowOneWayUp(b2, b1) || isActiveButNotHandled()) {
                setHasBeenHandled(true);
                contactConstraint.setEnabled(false);
            }
        }
    }

    /**
     * Establece los indicadores-banderas si la restricción de contacto dada 
     * está entre el cuerpo del personaje y un piso o plataforma unidireccional.
     * 
     * @param contactConstraint la restricción
     */
    protected void trackIsOnGround(ContactConstraint<PhysicsBody2D> contactConstraint) {
        PhysicsBody2D b1 = contactConstraint.getBody1();
        PhysicsBody2D b2 = contactConstraint.getBody2();

        if (is(b1, getUserData()) &&
                is(b2, FLOOR, ONE_WAY_PLATFORM) &&
                contactConstraint.isEnabled()) {
            collision(b1, b2);
        } else if (is(b1, FLOOR, ONE_WAY_PLATFORM) &&
                is(b2, getUserData()) &&
                contactConstraint.isEnabled()) {
            collision(b2, b1);
        }
    }

    /**
     * Determina las banderas, si el cuerpo enta contra una pared, piso o
     * techo.
     * 
     * @param character el cuerpo del personaje
     * @param platform el cuerpo de la plataforma
     */
    protected void collision(PhysicsBody2D character, PhysicsBody2D platform) {
        AABB wAABB = character.createAABB();
        AABB pAABB = platform.createAABB();

        if (Double.compare(Math.abs(wAABB.getMinY() - pAABB.getMaxY()), ERROR) < 0) {
            onGround  = true;
            onCeiling = false;
            onWall    = false;
        } else if (Double.compare(Math.abs(pAABB.getMinY() - wAABB.getMaxY()), ERROR) < 0) {
            onGround  = false;
            onCeiling = true;
            onWall    = false;
        } else {
            onGround  = false;
            onCeiling = false;
            onWall    = true;
        }
    }

    /**
     * (non-JavaDoc)
     * 
     * @see com.jme3.scene.control.AbstractControl#controlUpdate(float) 
     * @param tpf float
     */
    @Override
    protected void controlUpdate(float tpf) {
        applyPhysicsLocation(this);
        applyPhysicsRotation(this);
    }

    /**
     * Libera este cuerpo físico de la escena así como del espacio físico.
     */
    public void queueFree() {
        synchronized (this) {
            if (spatial.removeFromParent()) {
                dyn4jAppState.getPhysicsSpace().removeBody(CharacterBody2D.this);

                final World<PhysicsBody2D> physicsBody2DWorld = dyn4jAppState.getPhysicsSpace().getPhysicsWorld();
                physicsBody2DWorld.removeStepListener(physicsBody2DStepListener);
                physicsBody2DWorld.removeContactListener(physicsBody2DContactListener);
            }
        }
    }

    /**
     * (non-JavaDoc)
     * 
     * @see com.jme3.scene.control.AbstractControl#render(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort) 
     * @param rm RenderManager
     * @param vp ViewPort
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        /* NO */
    }

    /**
     * Devuelve el estado del jugador para bajar de una plataforma.
     * @return boolean
     */
    protected boolean isActiveButNotHandled() {
        return false;
    }

    /**
     * Establece el estado del jugador.
     * @param b boolean
     */
    protected void setHasBeenHandled(boolean b) {
    }

    // banderas
    
    /**
     * Devuelve el estado del juegor contra el techo.
     * @return boolean
     */
    public boolean isOnCeiling() {
        return this.onCeiling;
    }

    /**
     * Devuelve el estado del juegor contra el piso.
     * @return boolean
     */
    public boolean isOnFloor() {
        return this.onGround;
    }

    /**
     * Devuelve el estado del juegor con las paredes.
     * @return boolean
     */
    public boolean isOnWall() {
        return this.onWall;
    }
}
