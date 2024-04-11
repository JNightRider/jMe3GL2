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
package jme3gl2.demo.core;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetKey;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import jme3gl2.physics.Dyn4jAppState;
import jme3gl2.physics.ThreadingType;
import jme3gl2.physics.control.CharacterBody2D;
import jme3gl2.physics.control.PhysicsBody2D;
import jme3gl2.physics.control.RigidBody2D;
import jme3gl2.plugins.J2OLoader;
import jme3gl2.plugins.asset.J2OKey;
import jme3gl2.renderer.Camera2DRenderer;
import jme3gl2.scene.control.AnimatedSprite2D;
import jme3gl2.scene.shape.Sprite;
import static jme3gl2.utilities.GeometryUtilities.*;
import static jme3gl2.utilities.MaterialUtilities.*;
import org.dyn4j.geometry.MassType;

/**
 *
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class Character2D extends SimpleApplication {
    
    public static void main(String[] args) {
        Character2D app = new Character2D();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLoader(J2OLoader.class, "J2O", "j2o");
        
        Camera2DRenderer camera2DRenderer = new Camera2DRenderer(Camera2DRenderer.GLRendererType.GLX_25D, 5, 45);
        stateManager.attach(camera2DRenderer);
        
        Dyn4jAppState<PhysicsBody2D> dyn4jAppState = new Dyn4jAppState<>(ThreadingType.PARALLEL);
        dyn4jAppState.setDebugEnabled(true);        
        stateManager.attach(dyn4jAppState);
        
        prepareSimpleTerrain();
        prepareCharacter();
    }
    
    @SuppressWarnings("unchecked")
    private void prepareCharacter() {
        Dyn4jAppState<PhysicsBody2D> dyn4jAppState = stateManager.getState(Dyn4jAppState.class);
        
        Spatial player = assetManager.loadAsset(new J2OKey<>("Models/Rabbit.j2o"));
        player.getControl(AnimatedSprite2D.class).playAnimation("walk", 10);
        rootNode.attachChild(player);
        
        CharacterBody2D body2D = new CharacterBody2D() {
            @Override
            protected void physicsProcess(float delta) {
                System.out.println("onGround() " + isOnGround());
            }
        };
        body2D.addFixture(createCapsule(0.8, 1));
        body2D.setMass(MassType.NORMAL);
        body2D.translate(0, 13);
        player.addControl(body2D);
        
        dyn4jAppState.getPhysicsSpace().addBody(body2D);
        rootNode.attachChild(player);
     }
    
    /**
     * Prepare a simple terrain.
     */
    @SuppressWarnings("unchecked")
    private void prepareSimpleTerrain() {        
        Dyn4jAppState<PhysicsBody2D> dyn4jAppState = stateManager.getState(Dyn4jAppState.class);
        
        Geometry ground = new Geometry("Ground", new Sprite(5, 1));
        ground.setMaterial(getUnshadedColorMaterialFromClassPath(assetManager, ColorRGBA.DarkGray));
        ground.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        RigidBody2D body2DGround = new RigidBody2D();
        body2DGround.addFixture(createRectangle(5, 1));
        body2DGround.setMass(MassType.INFINITE);
        body2DGround.translate(0, -2);
        ground.addControl(body2DGround);
        
        dyn4jAppState.getPhysicsSpace().addBody(body2DGround);
        rootNode.attachChild(ground);
    }
}
