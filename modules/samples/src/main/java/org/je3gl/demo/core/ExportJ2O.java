/*
BSD 3-Clause License

Copyright (c) 2023-2025, Night Rider (Wilson)

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.je3gl.demo.core;

import com.jme3.app.SimpleApplication;
import com.jme3.export.Savable;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;

import java.io.File;
import java.io.IOException;

import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import org.je3gl.physics.Dyn4jAppState;
import org.je3gl.physics.ThreadingType;
import org.je3gl.physics.control.PhysicsBody2D;
import org.je3gl.physics.control.RigidBody2D;
import org.je3gl.physics.control.Vehicle2D;
import org.je3gl.scene.control.AnimatedSprite2D;
import org.je3gl.scene.shape.Sprite;
import static org.je3gl.utilities.GeometryUtilities.*;
import static org.je3gl.utilities.MaterialUtilities.*;
import static org.je3gl.utilities.TextureUtilities.*;

/**
 * Class where the export (serialization) of objects is exemplified.
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class ExportJ2O extends SimpleApplication {
    
    /** root path (absolute). */
    private static final String ROOT_PATH ;
    /** Margin used by JME materials (transparency). */
    private static final float ALPHA_DISCARD_THRESHOLD;
    // init
    static {
        ROOT_PATH = System.getProperty("user.dir") + "/src/main/resources/Models/";
        ALPHA_DISCARD_THRESHOLD = 0.01f;
    }
    
    /**
     * The main method; uses zero arguments in args array
     * @param args command line arguments
     */
    public static void main(String[] args) {
        ExportJ2O app = new ExportJ2O();
        app.start();
    }

    /** physical space dyn4j. */
    Dyn4jAppState<PhysicsBody2D> dyn4jAppState;
    
    /* (non-Javadoc)
     * @see com.jme3.app.SimpleApplication#simpleInitApp() 
     */
    @Override
    public void simpleInitApp() {
        dyn4jAppState = new Dyn4jAppState<>(ThreadingType.SEQUENTIAL);
        dyn4jAppState.setDebugEnabled(true);
        stateManager.attach(dyn4jAppState);
        
        j3oRabbit();
        //j2oTanks();
    }
    
    /**
     * Building a j2o model.
     */
    private void j2oTanks() {
        Node tanks = new Node("Tanks");
        tanks.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        {
            Sprite sprite = new Sprite(74.0F/53.0F, 1.0F);
            sprite.applyScale(1.5F);
            
            Geometry geom = new Geometry("TankNavy", sprite);
            Material mat  = getUnshadedMaterialFromClassPath(assetManager, "Textures/Tanks/tanks_tankNavy_body4.png");
            
            geom.setMaterial(mat);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
            
            tanks.attachChild(geom);
        }
        
        {
            Sprite sprite = new Sprite(73.0F/18.0F, 1.0F);
            sprite.applyScale(0.25F);
            
            Geometry geom = new Geometry("Turret", sprite);
            Material mat  = getUnshadedMaterialFromClassPath(assetManager, "Textures/Tanks/tanks_turret3.png");
            
            geom.setMaterial(mat);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
            geom.move(0.5F, 0.75F - (sprite.getHeight() / 2.0F), -0.01F);
            
            tanks.attachChild(geom);
        }
        
        {
            Sprite sprite = new Sprite(1.0F, 1.0F);
            sprite.applyScale(0.8F);
            
            Geometry geom = new Geometry("TankRearWheel", sprite);
            Material mat  = getUnshadedMaterialFromClassPath(assetManager, "Textures/Tanks/tanks_tankTracks4.png");
            mat.setFloat("AlphaDiscardThreshold", ALPHA_DISCARD_THRESHOLD);
            
            geom.setMaterial(mat);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
            geom.move(0, 0, 0.1F);
            
            RigidBody2D rbd = new RigidBody2D();
            rbd.addFixture(dyn4jCreateCircle(sprite.getWidth() / 2.0F));
            rbd.translate(-0.5, -0.5);
            rbd.setMass(MassType.NORMAL);
            
            geom.addControl(rbd);
            tanks.attachChild(geom);
        }
        
        {
            Sprite sprite = new Sprite(1.0F, 0.5F);
            sprite.applyScale(0.8F);
            
            Geometry geom = new Geometry("TankRearTracks", sprite);
            Material mat  = getUnshadedMaterialFromClassPath(assetManager, "Textures/Tanks/tanks_tankTracks7.png");
            mat.setFloat("AlphaDiscardThreshold", ALPHA_DISCARD_THRESHOLD);
            
            geom.setMaterial(mat);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
            geom.move(-0.5F, -0.25F, 0.2F);
            
            tanks.attachChild(geom);
        }
        
        {
            Sprite sprite = new Sprite(1.0F, 1.0F);
            sprite.applyScale(0.8F);
            
            Geometry geom = new Geometry("TankFrontWheel", sprite);
            Material mat  = getUnshadedMaterialFromClassPath(assetManager, "Textures/Tanks/tanks_tankTracks4.png");
            mat.setFloat("AlphaDiscardThreshold", MIN_ALPHA_DISCARD_THRESHOLD);
            
            geom.setMaterial(mat);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
            geom.move(0, 0, 0.1F);
            
            RigidBody2D rbd = new RigidBody2D();
            rbd.addFixture(dyn4jCreateCircle(sprite.getWidth() / 2.0F));
            rbd.translate(0.5, -0.5);
            rbd.setMass(MassType.NORMAL);
            
            geom.addControl(rbd);
            tanks.attachChild(geom);
        }
        
        {
            Sprite sprite = new Sprite(1.0F, 0.5F);
            sprite.applyScale(0.8F);
            
            Geometry geom = new Geometry("TankFrontTracks", sprite);
            Material mat  = getUnshadedMaterialFromClassPath(assetManager, "Textures/Tanks/tanks_tankTracks7.png");
            mat.setFloat("AlphaDiscardThreshold", MIN_ALPHA_DISCARD_THRESHOLD);
            
            geom.setMaterial(mat);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
            geom.move(0.5F, -0.25F, 0.2F);
            
            tanks.attachChild(geom);
        }
        
//        {
//            Geometry geom = new Geometry("1", new Sprite(10, 0.5f));
//            geom.setMaterial(MaterialUtilities.getUnshadedColorMaterialFromClassPath(assetManager, ColorRGBA.randomColor()));
//            
//            RigidBody2D rbd = new RigidBody2D();
//            rbd.addFixture(GeometryUtilities.createRectangle(10, 0.5));
//            rbd.setMass(MassType.INFINITE);
//            rbd.translate(0, -3);
//            geom.addControl(rbd);
//            
//            dyn4jAppState.getPhysicsSpace().addBody(rbd);
//            rootNode.attachChild(geom);
//        }
        
        Vehicle2D vd = new Vehicle2D(tanks.getChild("TankRearWheel").getControl(PhysicsBody2D.class), 
                                    tanks.getChild("TankFrontWheel").getControl(PhysicsBody2D.class), new Vector2(-0.5, -0.5), new Vector2(0.5, -0.5), new Vector2(0, -1.0));
        
        vd.addFixture(dyn4jCreateRectangle(2.0F, 1.0F));
        vd.setMass(MassType.NORMAL);
        tanks.addControl(vd);
        
        rootNode.attachChild(tanks);
        //dyn4jAppState.getPhysicsSpace().addBody(vd, true);
        export(tanks, "TankNavy.j2o");
    }
    
    /**
     * Building a j3o model.
     */
    private void j3oRabbit() {
        Geometry player = new Geometry("AnimatedSprite2D", new Sprite(1.0F, 1.0F));
        player.setMaterial(getUnshadedMaterialFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"));
        player.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        // Generate the animation...
        AnimatedSprite2D animatedSprite2D = new AnimatedSprite2D();
        animatedSprite2D.addAnimation("idle", new Texture[] {
            getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png")
        });
        animatedSprite2D.addAnimation("walk", new Texture[] {
            getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"),
            getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0041.png")
        });
        animatedSprite2D.addAnimation("jump", new Texture[] {
            getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0041.png")
        });
        
        player.addControl(animatedSprite2D);
        animatedSprite2D.playAnimation("walk", 10);
        rootNode.attachChild(player);
        
        export(player, "Rabbit.j3o");
    }
    
    /**
     * Save the models to disk.
     * @param obj model
     * @param name path-name
     */
    private static void export(Savable obj, String name) {
        try {
            BinaryExporter exporter = BinaryExporter.getInstance();
            exporter.save(obj, new File(ROOT_PATH, name));
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
