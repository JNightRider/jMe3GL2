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
package jMe3GL2.scene.tile;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import jMe3GL2.physics.PhysicsSpace;
import jMe3GL2.physics.collision.AbstractCollisionShape;
import jMe3GL2.physics.control.PhysicsBody2D;
import jMe3GL2.physics.control.RigidBody2D;
import jMe3GL2.scene.shape.Sprite;
import jMe3GL2.util.Converter;
import jMe3GL2.util.jMe3GL2Utils;
import org.dyn4j.geometry.MassType;

/**
 * Clase que implementa los administradores predeterminados que utiliza la clase
 * {@link TileMap} como valores predeterminados.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 2.0.0
 */
class jMe3GLDefTilesHeet implements TilesHeet {
    
    class jMe3GLDefTileModel implements TileModel {

        @Override
        public Geometry tileModel(TileMap tileMap, Tile tile, AssetManager assetManager) {
            Properties property = tile.getProperties();
            
            Sprite sprite = new Sprite(property.getProperty("Width"), 
                                       property.getProperty("Height"), 
                                       tileMap.getColumns(), tileMap.getRows(), 
                                       tile.getColumn(),     tile.getRow());
            
            final Material mat = jMe3GL2Utils.loadMaterial(assetManager, tileMap.getProperties().getProperty("Texture", (String)null));
            mat.setFloat("AlphaDiscardThreshold", 0.0F);
        
            final Geometry geom = new Geometry(tile.getId(), sprite);
            geom.setMaterial(mat);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
                
            Vector3f translation = tile.getTranslation();
            if (property.getProperty("RigidBody2D", false)) {
                RigidBody2D rbd = new RigidBody2D();
                
                AbstractCollisionShape<?> collisionShape = property.getProperty("CollisionShape", null);
                if ( collisionShape != null ) {
                    rbd.addCollisionShape(collisionShape);
                }
                
                rbd.setMass(property.getProperty("MassType", MassType.INFINITE));
                rbd.translate(Converter.toVector2(translation));
                geom.addControl(rbd);
            } else {
                geom.setLocalTranslation(translation);
            }
            
            return geom;
        }

        @Override
        public void updateModel(TileMap tileMap, Tile tile, AssetManager assetManager, Geometry geom) {
             Mesh mesh = geom.getMesh();
             if ( !(mesh instanceof Sprite) ) {
                 throw new UnsupportedOperationException("Not supported yet.");
             }
             
             final Properties property = tile.getProperties();
             ((Sprite) mesh).updateVertexSize(property.getProperty("Width"), property.getProperty("Height"));
             ((Sprite) mesh).updateMeshCoords(tileMap.getColumns(), tileMap.getRows(), tile.getColumn(), tile.getRow());
             geom.setMesh(mesh);
             
             RigidBody2D rbd = geom.getControl(RigidBody2D.class);
             Vector3f translation = property.getProperty("Translation", new Vector3f());
             if ( rbd != null ) {
                 rbd.translate(Converter.toVector2(translation));
            } else {
                geom.setLocalTranslation(translation);
            }
        }
    }
    
    class jMe3GLDefTileSpace implements TileSpace {

        PhysicsSpace<PhysicsBody2D> physicsSpace;
        
        @Override
        public void onDetachTile(Geometry geom) {
            if (isPhysicsSpace()) {
                PhysicsBody2D body2D = geom.getControl(PhysicsBody2D.class);
                if ( body2D != null ) {
                    physicsSpace.removeBody(body2D);
                }
            }
        }

        @Override
        public void onAttachTile(Geometry geom) {
            if (isPhysicsSpace()) {
                PhysicsBody2D body2D = geom.getControl(PhysicsBody2D.class);
                if ( body2D != null ) {
                    physicsSpace.addBody(body2D);
                }
            }
        }

        @Override public void onTileUnassociated(Geometry geom) { }
        @Override public void onTransformChange(Geometry geom) { }
        @Override public void onMaterialChange(Geometry geom) { }
        @Override public void onMeshChange(Geometry geom) { }        

        @Override
        public void setPhysicsSpace(PhysicsSpace<PhysicsBody2D> physicsSpace) {
            this.physicsSpace = physicsSpace;
        }
        
        private boolean isPhysicsSpace() {
            return physicsSpace != null;
        }
    }

    private final TileModel tileModel;
    private final TileSpace tileSpace;
    
    public jMe3GLDefTilesHeet() {
        tileModel = new jMe3GLDefTileModel();
        tileSpace = new jMe3GLDefTileSpace();
    }

    @Override
    public TileModel getTileModel() {
        return tileModel;
    }

    @Override
    public TileSpace getTileSpace() {
        return tileSpace;
    }
}
