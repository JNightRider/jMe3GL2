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
package jme3gl2.scene.tile;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;

import jme3gl2.physics.PhysicsSpace;
import jme3gl2.physics.collision.AbstractCollisionShape;
import jme3gl2.physics.control.PhysicsBody2D;
import jme3gl2.physics.control.RigidBody2D;
import jme3gl2.scene.shape.Sprite;
import jme3gl2.util.Converter;
import jme3gl2.utilities.ColorUtilities;
import jme3gl2.utilities.MaterialUtilities;
import jme3gl2.utilities.TextureUtilities;
import jme3gl2.utilities.TileMapUtilities;

import org.dyn4j.geometry.MassType;

/**
 * Clase que implementa los administradores predeterminados que utiliza la clase
 * {@link TileMap} como valores predeterminados.
 * 
 * @author wil
 * @version 1.5.0
 * 
 * @since 2.0.0
 */
class Jme3GLDefTilesheet implements Tilesheet {
    
    class Jme3GLDefTileModel implements Spritesheet {

        @Override
        public Geometry render(TileMap tileMap, Tile tile, AssetManager assetManager) {
            Properties properties = tile.getProperties();
            Geometry geom = renderGeometry(null, properties, tileMap.getProperties(), assetManager);
            renderPhysicsBody2D(geom, properties);
            return geom;
        }
        
        private void renderPhysicsBody2D(Geometry geom, Properties pTle) {
            Vector3f translation = pTle.optSavable("Translation", new Vector3f(0.0F, 0.0F, 0.0F));
            Vector2f offset = pTle.optSavable("offset", new Vector2f(0.0F, 0.0F));
            
            if (pTle.optBoolean("PhysicsBody", false)) {
                PhysicsBody2D pbd = geom.getControl(PhysicsBody2D.class);
                if (pbd == null) {
                    pbd = pTle.optSavable("PhysicsControl", new RigidBody2D());
                }

                AbstractCollisionShape<?> collisionShape = pTle.optSavable("CollisionShape", null);
                if (collisionShape != null && (pbd.getFixtureCount() == 0)) {
                    pbd.addCollisionShape(collisionShape);
                }
                
                pbd.rotate(pTle.optFloat("Rotate", 0.0F));
                pbd.setMass(pTle.optEnum("MassType", MassType.INFINITE));
                pbd.getTransform().setTranslation(Converter.toVector2(translation));
                pbd.translate(Converter.toVector2(offset));
                geom.addControl(pbd);
            } else {
                geom.setLocalRotation(new Quaternion().fromAngleAxis(pTle.optFloat("Rotate", 0.0F), new Vector3f(0.0F, 0.0F, 1.0F)));
                geom.setLocalTranslation(translation);
                geom.move(new Vector3f(offset.x, offset.y, 0.0F));
            }
        }
        
        private Geometry renderGeometry(Geometry defG, Properties pTle, Properties pMap, AssetManager assetManager) {
            Geometry geom = defG == null ? new Geometry() : defG;            
            geom.setName(pTle.optString("Id", TileMapUtilities.getStrigRandomUUID()));
            geom.setMesh(renderMesh(pTle, pMap));
            geom.setMaterial(renderMat(pTle, pMap, assetManager));
            geom.setQueueBucket(pTle.optEnum("RenderQueue.Bucket", RenderQueue.Bucket.Transparent));
            geom.setLocalScale(pTle.optSavable("LocalScale", new Vector3f(1.0F, 1.0F, 1.0F)));
            return geom;
        }
        
        private Sprite renderMesh(Properties pTle, Properties pMap) {
            Sprite sprite;            
            boolean useSprite = pTle.optBoolean("StandaloneSprite", false);
            if (useSprite) {
                if (pTle.optBoolean("SpritesHeets", false)) {
                    sprite = new Sprite(pTle.optFloat("Width", 1), pTle.optFloat("Height", 1),
                            pTle.optInt("Columns", 1), pTle.optInt("Rows", 1),
                            pTle.optInt("Column", 0), pTle.optInt("Row", 0));
                } else {
                    sprite = new Sprite(pTle.optFloat("Width"), pTle.optFloat("Height"));
                }
            } else {
                sprite = new Sprite(pTle.optFloat("Width", 1), pTle.optFloat("Height", 1),
                        pMap.optInt("Columns", 1), pMap.optInt("Rows", 1), pMap.optInt("Column", 0), pMap.optInt("Row", 0));
            }
            
            sprite.flipH(pTle.optBoolean("FlipH", false));
            sprite.flipV(pTle.optBoolean("FlipV", false));
            return sprite;
        }
        
        private Material renderMat(Properties pTle, Properties pMap, AssetManager assetManager) {
            ColorRGBA color = pTle.optSavable("Color", new ColorRGBA(1.0F, 1.0F, 1.0F, 1.0F));
            String texture;
            if (pTle.optBoolean("StandaloneSprite", false)) {
                texture = pTle.optString("Texture", (String) null);
            } else {
                texture = pMap.optString("Texture", (String) null);
            }
            
            Material mat;
            if (pTle.optBoolean("LightingMaterial", false)) {
                mat = MaterialUtilities.getLightingMaterialFromClassPath(assetManager, texture);
                mat.setFloat("Shininess", pTle.optFloat("Shininess", 32.0F));
                mat.setBoolean("UseMaterialColors", pTle.optBoolean("UseMaterialColors", true));
                mat.setColor("Ambient", pTle.optSavable("Ambient", new ColorRGBA(0.0F, 0.0F, 0.0F, 1.0F)));
                mat.setColor("Diffuse", pTle.optSavable("Diffuse", new ColorRGBA(1.0F, 1.0F, 1.0F, 1.0F)));
                mat.setColor("Specular", pTle.optSavable("Specular", new ColorRGBA(1.0F, 1.0F, 1.0F, 1.0F)));
            } else {
                mat = MaterialUtilities.getUnshadedMaterialFromClassPath(assetManager, texture);
            }
            
            mat.setFloat("AlphaDiscardThreshold", pTle.optFloat("AlphaDiscardThreshold", MaterialUtilities.MIN_ALPHA_DISCARD_THRESHOLD));
            mat.setColor("Color", color);
            mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            mat.setTransparent(true);
            
            if (pTle.optBoolean("SupportsGlow", false)) {
                mat.setColor("GlowColor", ColorUtilities.brighter(pTle.optSavable("GlowColor", color.clone())));
                mat.setTexture("GlowMap", TextureUtilities.getTextureFromClassPath(assetManager, pTle.optString("GlowMap", texture)));
            }
            return mat;
        }

        @Override
        public void update(TileMap tileMap, Tile tile, AssetManager assetManager, Geometry geom) {
            renderGeometry(geom, tile.getProperties(), tileMap.getProperties(), assetManager);
            renderPhysicsBody2D(geom, tile.getProperties());
        }
    }
    
    class Jme3GLDefTileSpace implements SpritesheetPhysics {

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

    private static final Tilesheet TILESHEET;
    
    static {
        TILESHEET = new Jme3GLDefTilesheet();
    }
    
    public static Tilesheet getInstance() {
        return Jme3GLDefTilesheet.TILESHEET;
    }
    
    private final Spritesheet spritesheet;
    private final SpritesheetPhysics spritesheetPhysics;
    
    private Jme3GLDefTilesheet() {
        spritesheet = new Jme3GLDefTileModel();
        spritesheetPhysics = new Jme3GLDefTileSpace();
    }

    @Override
    public Spritesheet getSpritesheet() {
        return spritesheet;
    }

    @Override
    public SpritesheetPhysics getSpritesheetPhysics() {
        return spritesheetPhysics;
    }
}
