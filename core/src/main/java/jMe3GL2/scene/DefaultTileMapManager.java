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
package jMe3GL2.scene;

import com.jme3.scene.Geometry;

import jMe3GL2.geometry.jMe3GL2Geometry;
import jMe3GL2.physics.PhysicsSpace;
import jMe3GL2.physics.control.PhysicsBody2D;
import jMe3GL2.scene.shape.Sprite;

/**
 * Un <code>DefaultTileMapManager</code> es una clase encargado de implementar la
 * interfaz <code>TileMapManager</code> con las funciones predeterminadas.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 1.5.0
 */
public class DefaultTileMapManager implements TileMapManager {
    
    /** Espacio físico. */
    private final PhysicsSpace<PhysicsBody2D> physicsSpace;

    /**
     * Constructor predeterminado de la clase <code>DefaultTileMapManager</code>.
     * @param physicsSpace Espacio físico.
     */
    public DefaultTileMapManager(PhysicsSpace<PhysicsBody2D> physicsSpace) {
        this.physicsSpace = physicsSpace;
    }

    /**
     * (non-JavaDoc)
     * @see TileMapManager#onAttachChild(com.jme3.scene.Geometry)
     * @param geom {@code Geometry}
     */
    @Override
    public void onAttachChild(Geometry geom) { 
        if (physicsSpace != null) {
            PhysicsBody2D pbd = geom.getControl(PhysicsBody2D.class);
            Sprite sprite = (Sprite) geom.getMesh();
            
            if (pbd != null) {
                boolean collision = geom.getUserData(TileMap.COLLISION);                
                if (pbd.getFixtureCount() == 0 && collision) {
                    pbd.addFixture(jMe3GL2Geometry.createRectangle(sprite.getWidth(), sprite.getHeight()));
                }                
                physicsSpace.addBody(pbd);
            }
        }
    }

    /**
     * (non-JavaDoc)
     * @see TileMapManager#onDetachChild(com.jme3.scene.Geometry) 
     * @param geom {@code Geometry}
     */
    @Override
    public void onDetachChild(Geometry geom) {
        if (physicsSpace != null) {
            PhysicsBody2D pbd = geom.getControl(PhysicsBody2D.class);
            if (pbd != null) {
                physicsSpace.removeBody(pbd);
            }
        }
    }

    /**
     * (non-JavaDoc)
     * @see TileMapManager#onTransformChange(com.jme3.scene.Geometry, jMe3GL2.scene.TileMap) 
     * @param geom {@code Geometry}
     * @param map {@link TileMap}
     */
    @Override
    public void onTransformChange(Geometry geom, TileMap map) {
        if (physicsSpace != null) {
            PhysicsBody2D pbd = geom.getControl(PhysicsBody2D.class);
            Sprite sprite = (Sprite) geom.getMesh();
            
            if (pbd != null) {
                boolean collision = geom.getUserData(TileMap.COLLISION);                
                if (pbd.getFixtureCount() == 0 && collision) {
                    pbd.addFixture(jMe3GL2Geometry.createRectangle(sprite.getWidth(), sprite.getHeight()));
                } else {
                    pbd.removeFixture(0);
                }
            }
        }
    }

    /**
     * (non-JavaDoc)
     * @see TileMapManager#onMaterialChange(com.jme3.scene.Geometry, jMe3GL2.scene.TileMap) 
     * @param geom {@code Geometry}
     * @param map {@link TileMap}
     */
    @Override
    public void onMaterialChange(Geometry geom, TileMap map) { }

    /**
     * (non-JavaDoc)
     * @see TileMapManager#onMeshChange(com.jme3.scene.Geometry, jMe3GL2.scene.TileMap) 
     * @param geom {@code Geometry}
     * @param map {@link TileMap}
     */
    @Override
    public void onMeshChange(Geometry geom, TileMap map) { }

    /**
     * (non-JavaDoc)
     * @see TileMapManager#onGeometryUnassociated(com.jme3.scene.Geometry, jMe3GL2.scene.TileMap) 
     * @param geom {@code Geometry}
     * @param map {@link TileMap}
     */
    @Override
    public void onGeometryUnassociated(Geometry geom, TileMap map) { }
}
