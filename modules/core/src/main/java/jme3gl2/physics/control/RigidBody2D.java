/* Copyright (c) 2009-2024 jMonkeyEngine.
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

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

import jme3gl2.util.Converter;

/**
 * An object of the class <code>RigidBody2D</code> is responsible for the control
 * and application of physics in the <code>Spatial</code> (2D model) with a rigid body.
 *
 * @author wil
 * @version 1.0.2
 * @since 1.0.0
 */
public class RigidBody2D extends PhysicsBody2D {

    /**
     * Class constructor <code>RigidBody2D</code>.
     */
    public RigidBody2D() { }
    
    /**
     * (non-Javadoc).
     * @see jme3gl2.physics.control.RigidBody2D#controlUpdate(float) 
     * @param tpf float
     */
    @Override
    protected void controlUpdate(float tpf) {
        applyPhysicsLocation(this);
        applyPhysicsRotation(this);
    }

    /**
     * Detects if this body contains more than one {@link org.dyn4j.collision.Fixture}.
     * @return <code>true</code> if it has more than one {@link org.dyn4j.collision.Fixture},
     * otherwise it will return <code>false</code>
     */
    public boolean hasMultipleBodies() {
        return this.getFixtureCount() > 1;
    }
    
    /**
     * Method in charge of returning the world position of this body.
     * @return world position.
     */
    public Vector3f getPhysicsLocation() {
        return new Vector3f(Converter.toFloatValue(getTransform().getTranslationX()), Converter.toFloatValue(getTransform().getTranslationY()), this.spatial.getLocalTranslation().z);
    }

    /**
     * Método encargado de devolver la posición mundial de este cuerpo.
     * @return posición mundial.
     */
    public Vector3f getWorldLocation() {
        return new Vector3f(Converter.toFloatValue(getWorldCenter().x), Converter.toFloatValue(getWorldCenter().y), this.spatial.getWorldTranslation().z);
    }

    /**
     * Cleans the forces.
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
     * (non-Javadoc)
     * @see jme3gl2.physics.control.RigidBody2D#controlRender(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort) 
     * @param rm RenderManager
     * @param vp ViewPort
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
}
