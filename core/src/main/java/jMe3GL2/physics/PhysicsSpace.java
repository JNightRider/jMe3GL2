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
package jMe3GL2.physics;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import jMe3GL2.physics.control.AbstractBody;
import jMe3GL2.physics.control.PhysicsControl;
import jMe3GL2.util.Converter;

import java.util.List;

import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

/**
 * Un <code>PhysicsSpace</code> es una clase que se encarga de inicializar y
 * preparar el espacio(<code>org.dyn4j.world.World</code>) para la física.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 1.0.0
 * @param <E> el tipo {@code PhysicsBody}.
 */
@SuppressWarnings(value = {"unchecked"})
public class PhysicsSpace<E extends AbstractBody> {

    /**
     * Velocidad predeterminada de la física.
     */
    private static final float DEFAULT_SPEED = 1f;

    /** Mundo de la física {@code Dyn4j}. */
    private World<E> physicsWorld;
    
    /**
     * Velocidad de las actualizaciones de la física que utiliza
     * el mundo {@code Dyn4j}.
     */
    protected float speed = DEFAULT_SPEED;

    /**
     * Instancia un nuevo objeto <code>PhysicsSpace</code>. Establezca los 
     * valores predeterminado del espacio de la física.
     * 
     * @param initialCapacity capacidad inicial (cuerpos en el mundo).
     * @param initialJointCapacity capacidad inicial de las articulaciones.
     * @param bounds Límite del mundo.
     */
    public PhysicsSpace(final Integer initialCapacity, final Integer initialJointCapacity, final Bounds bounds) {
        if (initialCapacity != null && initialJointCapacity != null) {
            this.physicsWorld = new World<>(initialCapacity, initialJointCapacity);
        } else {
            this.physicsWorld = new World<>();
        }        
        if ( bounds != null ) {
            this.physicsWorld.setBounds(bounds);
        }
    }

    /**
     * (non-Javadoc)
     * @param body Body
     */
    public void addBody(final E body) {
        if (body instanceof PhysicsControl) {
            ((PhysicsControl)  body).setPhysicsSpace(this);
        }
        this.physicsWorld.addBody(body);
    }
    
    /**
     * (non-Javadoc)
     * @param body Body
     * @return Boolean
     */
    public boolean removeBody(final E body) {
        boolean b = this.physicsWorld.removeBody(body);
        if ( b && (body instanceof PhysicsControl) ) {
            ((PhysicsControl) body).setPhysicsSpace(null);
        }
        return b;
    }
    
    /**
     * (non-Javadoc)
     * @param body Body
     * @param notify Boolean
     * @return Boolean
     */
    public boolean removeBody(final E body, final boolean notify) {
        boolean b = this.physicsWorld.removeBody(body, notify);
        if ( b && (body instanceof PhysicsControl) ) {
            ((PhysicsControl) body).setPhysicsSpace(null);
        }
        return b;
    }

    /**
     * (non-Javadoc)
     * @param joint Joint
     */
    public void addJoint(final Joint joint) {
        this.physicsWorld.addJoint(joint);
    }
    
    /**
     * (non-Javadoc)
     * @param joint Joint
     * @return Boolean
     */
    public boolean removeJoint(final Joint joint) {
        return this.physicsWorld.removeJoint(joint);
    }

    /**
     * Método encargado de actualizar el mundo de la física.
     * @param elapsedTime lapso de tiempo.
     */
    public void updateFixed(final float elapsedTime) {
        this.physicsWorld.update(elapsedTime);
    }

    /**
     * Limpia el mundo.
     */
    public void clear() {
        this.physicsWorld = null;
    }
    
    /**
     * Establece una nueva velocidad para el mundo de la física.
     * @param speed nueva velocidad.s
     */
    public void setSpeed(final float speed) {
        this.speed = speed;
        this.physicsWorld.getSettings().setMinimumAtRestTime(Settings.DEFAULT_STEP_FREQUENCY * speed);
    }

    /**
     * Devuelve la velocidad actual del mundo.
     * @return velocidad.
     */
    public float getSpeed() {
        return this.speed;
    }
    
    /**
     * (non-Javadoc)
     * @param gravity Vector2f
     */
    public void setGravity(final Vector2f gravity) {
        this.physicsWorld.setGravity(new Vector2(gravity.x, gravity.y));
    }
    
    /**
     * (non-Javadoc)
     * @param x double
     * @param y double
     */
    public void setGravity(final double x, final double y) {
        this.physicsWorld.setGravity(new Vector2(x, y));
    }

    /**
     * (non-Javadoc)
     * @return Vector3f
     */
    public Vector3f getGravity() {
        return Converter.toVector3f(this.physicsWorld.getGravity());
    }
    
    /**
     * Devuelve una lista de cuerpos contenido en el mundo.
     * @return lista de cuerpos.
     */
    public List<E> getBodies() {
        return this.physicsWorld.getBodies();
    }
    
    /**
     * Devuelve una lista de articulaciones.
     * @return lista de articulaciones.
     */
    public List<Joint<E>> getJoints() {
        return this.physicsWorld.getJoints();
    }
    
    /**
     * Devuelve el mundo de la física {@code Dyn4j}.
     * @return mundo física.
     */
    public World<E> getPhysicsWorld() {
        return this.physicsWorld;
    }
    
    /**
     * Devuelve la cantidad de cuerpos existentes en el mundo.
     * @return cantidad de cuerpos.
     */
    public int getBodyCount() {
        return this.physicsWorld.getBodyCount();
    }
}
