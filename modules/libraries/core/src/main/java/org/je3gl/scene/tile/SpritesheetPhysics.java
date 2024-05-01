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
 * Interface <code>SpritesheetPhysics</code> is in charge of managing the changes
 * of a {@link TileMap}.
 * 
 * <p>
 * With this interface we can know and manage every change made by the geometries
 * of the parent node.
 * </p>
 * 
 * <p>
 * Note that the {@link TileMap} inherits from <code>GeometryGroupNode</code> so
 * most of the events of this interface correspond to the abstract implementation
 * of the methods of the parent of the class {@link TileMap}.
 * </p>
 * 
 * @author wil
 * @version 1.0.5
 * @since 2.0.0
 */
public interface SpritesheetPhysics {
    
    /**
     * Method in charge of establishing the physical space of the rigid bodies
     * of the tile map.
     * @param physicsSpace physical space
     */
    public void setPhysicsSpace(PhysicsSpace<PhysicsBody2D> physicsSpace);
    
    /**
     * It is activated when a geometry is removed from the parent node.
     * @param geom geometry eliminated.
     */
    public void onDetachTile(Geometry geom);
    
    /**
     * It is activated when a new geometry is added to the parent node.
     * @param geom new geometry
     */
    public void onAttachTile(Geometry geom);
    
    /**
     * The geometry is no longer associated with the parent node.
     * @param geom geometry
     */
    public void onTileUnassociated(Geometry geom);
    
    /**
     * It is activated when a change is applied to a geometry.
     * @param geom geometry
     */
    public void onTransformChange(Geometry geom);
    
    /**
     * It is activated when a change in the material of a geometry is applied.
     * @param geom geometry
     */
    public void onMaterialChange(Geometry geom);
    
    /**
     * It is activated when the mesh or level of a geometry is changed.
     * @param geom geometry
     */
    public void onMeshChange(Geometry geom);
}
