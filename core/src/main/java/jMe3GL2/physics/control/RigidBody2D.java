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
package jMe3GL2.physics.control;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.util.TempVars;

import jMe3GL2.physics.collision.AbstractCollisionShape;
import jMe3GL2.util.Converter;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Transform;

/**
 * Un objeto de la clase <code>RigidBody2D</code> se encarga de controlar y
 * aplicar la física en el <code>Spatial</code> (modelo 2D) con un cuerpo
 * rigido.
 *
 * @author wil
 * @version 1.0-SNAPSHOT
 *
 * @since 1.0.0
 */
public class RigidBody2D extends AbstractBody {

    /**
     * Constructor de la clase <code>RigidBody2D</code>.
     */
    public RigidBody2D() { }
    
    /**
     * Constructor de la clase <code>RigidBody2D</code>.
     * @param collisionShape forma físico.
     */
    public RigidBody2D(AbstractCollisionShape<? extends Convex> collisionShape) {
        this.addFixture(collisionShape.getCollisionShape());
    }
    
    /**
     * Otra forma de como agregar una forma física.
     * @param shape forma física.  
     * @see RigidBody2D#addFixture(org.dyn4j.geometry.Convex) 
     * @see RigidBody2D#addFixture(org.dyn4j.collision.Fixture) 
     * @see RigidBody2D#addFixture(org.dyn4j.geometry.Convex, double, double, double) 
     */
    public void addCollisionShape(AbstractCollisionShape<? extends Convex> shape) {
        this.addFixture(shape.getCollisionShape());
    }    
    
    /**
     * (non-JavaDoc).
     * @param tpf float
     * @see AbstractBody#controlUpdate(float) 
     */
    @Override
    protected void controlUpdate(float tpf) {
        setPhysicsLocation(this);
        setPhysicsRotation(this);
    }

    /**
     * Detecta si este cuerpo contiene mas de un {@code Fixture}.
     * @return {@code true} si tiene mas de un {@code Fixture}, de lo contrario
     *              devolverá {@code false}.
     */
    public boolean hasMultipleBodies() {
        return this.getFixtureCount() > 1;
    }
    
    /**
     * Método encaegado de devolver la posición actual.
     * @return posición actual.
     */
    public Vector3f getPhysicsLocation() {
        return new Vector3f(Converter.toFloat(getTransform().getTranslationX()), Converter.toFloat(getTransform().getTranslationY()), this.spatial.getLocalTranslation().z);
    }

    /**
     * Método encargado de devolver la posición mundial de este cuerpo.
     * @return posición mundial.
     */
    public Vector3f getWorldLocation() {
        return new Vector3f(Converter.toFloat(getWorldCenter().x), Converter.toFloat(getWorldCenter().y), this.spatial.getWorldTranslation().z);
    }

    /**
     * Limpia las fuerzas.
     */
    public void clearForces() {
        this.clearForce();
        this.clearAccumulatedForce();
        this.clearTorque();
        this.clearAccumulatedTorque();
        this.setAngularVelocity(0);
        this.setLinearVelocity(0, 0);
    }
    
    /**
     * (non-JavaDoc).
     * @param rm RenderManager
     * @param vp ViewPort
     * @see AbstractBody#render(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort) 
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }

    /**
     * Método encargado de aplicar una rotación física.
     * @param physicBody cuerpo físico.
     */
    protected void setPhysicsRotation(final AbstractBody physicBody) {
        final Transform trans = physicBody.getTransform();

        final float rotation = Converter.toFloat(trans.getRotationAngle());

        final TempVars tempVars = TempVars.get();
        final Quaternion quaternion = tempVars.quat1;
        quaternion.fromAngleAxis(rotation, new Vector3f(0, 0, 1));

        this.spatial.setLocalRotation(quaternion);

        tempVars.release();
    }

    /**
     * Método encargado de aplicar una traslación físico.
     * @param physicBody cuerpo físico.
     */
    protected void setPhysicsLocation(final AbstractBody physicBody) {
        final Transform trans = physicBody.getTransform();

        final float posX = Converter.toFloat(trans.getTranslationX());
        final float posY = Converter.toFloat(trans.getTranslationY());

        this.spatial.setLocalTranslation(posX, posY, this.spatial.getLocalTranslation().z);
    }
}
