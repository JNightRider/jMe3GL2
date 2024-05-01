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
package org.je3gl.scene.tile;

import com.jme3.scene.Geometry;

import org.je3gl.physics.PhysicsSpace;
import org.je3gl.physics.control.PhysicsBody2D;

/**
 * Convenience class to implement the interface {@link SpritesheetPhysicsAdapter}.
 * @author wil
 * @version 1.0.5
 * @since 2.0.0
 */
public abstract class SpritesheetPhysicsAdapter implements SpritesheetPhysics {

    /*
     * (non-Javadoc)
     * @see SpritesheetPhysics#setPhysicsSpace(jMe3GL2.physics.PhysicsSpace) 
     */
    @Override
    public void setPhysicsSpace(PhysicsSpace<PhysicsBody2D> physicsSpace) { }

    /*
     * (non-Javadoc)
     * @see SpritesheetPhysics#onDetachTile(com.jme3.scene.Geometry) 
     */
    @Override
    public void onDetachTile(Geometry geom) { }

    /*
     * (non-Javadoc)
     * @see SpritesheetPhysics#onAttachTile(com.jme3.scene.Geometry) 
     */
    @Override
    public void onAttachTile(Geometry geom) { }

    /*
     * (non-Javadoc)
     * @see SpritesheetPhysics#onTileUnassociated(com.jme3.scene.Geometry) 
     */
    @Override
    public void onTileUnassociated(Geometry geom) { }

    /*
     * (non-Javadoc)
     * @see SpritesheetPhysics#onTransformChange(com.jme3.scene.Geometry) 
     */
    @Override
    public void onTransformChange(Geometry geom) { }

    /*
     * (non-Javadoc)
     * @see SpritesheetPhysics#onMaterialChange(com.jme3.scene.Geometry) 
     */
    @Override
    public void onMaterialChange(Geometry geom) { }

    /*
     * (non-Javadoc)
     * @see SpritesheetPhysics#onMeshChange(com.jme3.scene.Geometry) 
     */
    @Override
    public void onMeshChange(Geometry geom) { }    
}
