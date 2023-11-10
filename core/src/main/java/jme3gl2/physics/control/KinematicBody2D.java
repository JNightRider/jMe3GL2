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

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

import jme3gl2.physics.collision.AbstractCollisionShape;

import org.dyn4j.geometry.Convex;

/**
 * Los cuerpos <code>KinematicBody2D</code> son tipos especiales de cuerpos
 * destinados a ser controlados por el usuario.
 *
 * <p>
 * No se ven afectados por la física en absoluto; a comparación de otros tipo de
 * cuerpos, como un personaje o un cuerpo rígido, estos son lo mismo que un
 * cuerpo estático.</p>
 *
 * @author wil
 * @version 1.5-SNAPSHOT
 *
 * @since 1.2.0
 */
public class KinematicBody2D extends PhysicsBody2D {

    /**
     * Constructor de la clase <code>KinematicBody2D</code>.
     */
    public KinematicBody2D() {
    }
    
    /**
     * Constructor de la clase <code>KinematicBody2D</code>.
     * @param collisionShape forma físico.
     */
    public KinematicBody2D(AbstractCollisionShape<? extends Convex> collisionShape) {
        this.addFixture(collisionShape.getCollisionShape());
    }
    
    /**
     * (non-JavaDoc).
     * @param tpf float
     * @see PhysicsBody2D#controlUpdate(float) 
     */
    @Override
    protected void controlUpdate(float tpf) {
        setGravityScale(0);        
        setAtRest(true);
        
        setPhysicsLocation(this);
        setPhysicsRotation(this);
    }

    /**
     * (non-JavaDoc).
     * @param rm RenderManager
     * @param vp ViewPort
     * @see PhysicsBody2D#render(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort) 
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
}
