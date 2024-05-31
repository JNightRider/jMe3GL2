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
package org.je3gl.demo.core;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.je3gl.physics.Dyn4jAppState;
import org.je3gl.physics.ThreadingType;
import org.je3gl.physics.control.PhysicsBody2D;
import org.je3gl.physics.control.RigidBody2D;
import org.je3gl.physics.control.Vehicle2D;
import org.je3gl.plugins.J2OLoader;
import org.je3gl.plugins.asset.J2OKey;
import org.je3gl.plugins.input.BooleanStateKeyboardInputHandler;
import org.je3gl.plugins.input.InputHandlerAppState;
import org.je3gl.plugins.input.Key;
import org.je3gl.renderer.Camera2DRenderer;
import org.je3gl.scene.shape.Sprite;
import static org.je3gl.utilities.GeometryUtilities.*;
import static org.je3gl.utilities.MaterialUtilities.*;

/**
 *
 * @author wil
 */
public class Tanks2D extends SimpleApplication {
    
    public static void main(String[] args) {
        Tanks2D app = new Tanks2D();
        app.start();
    }
    
    private static final BooleanStateKeyboardInputHandler VK_LEFT = new BooleanStateKeyboardInputHandler(new Key(KeyInput.KEY_T, "left"));
    private static final BooleanStateKeyboardInputHandler VK_RIGHT = new BooleanStateKeyboardInputHandler(new Key(KeyInput.KEY_Y, "right"));
    
    private static class TanksControl extends AbstractControl {

        @Override
        protected void controlUpdate(float tpf) {
            if (VK_LEFT.isActive()) {
                spatial.getControl(Vehicle2D.class).forward();
            } else if (VK_RIGHT.isActive()) {
                spatial.getControl(Vehicle2D.class).reverse();
            } else {
                spatial.getControl(Vehicle2D.class).brake();
            }
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
            
        }
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLoader(J2OLoader.class, "J2O", "j2o");
        
        Camera2DRenderer camera2DRenderer = new Camera2DRenderer(Camera2DRenderer.GLRendererType.GLX_25D, 5, 45);
        stateManager.attach(camera2DRenderer);
        
        Dyn4jAppState<PhysicsBody2D> dyn4jAppState = new Dyn4jAppState<>(ThreadingType.PARALLEL);
        dyn4jAppState.setDebugEnabled(true);        
        stateManager.attach(dyn4jAppState);
        
        InputHandlerAppState inputHandlerAppState = new InputHandlerAppState();
        stateManager.attach(inputHandlerAppState);
        
        inputHandlerAppState.addInputHandler(VK_LEFT).install();
        inputHandlerAppState.addInputHandler(VK_RIGHT).install();
        
        prepareSimpleTerrain();
        prepareCharacters();
    }
    
    @SuppressWarnings("unchecked")
    private void prepareCharacters() {
        Dyn4jAppState<PhysicsBody2D> dyn4jAppState = stateManager.getState(Dyn4jAppState.class);
        
        Spatial tank = assetManager.loadAsset(new J2OKey<>("Models/TankNavy.j2o"));
        tank.getControl(PhysicsBody2D.class).setMass(new Mass(new Vector2(), 10, 0));
        tank.addControl(new TanksControl());
        
        dyn4jAppState.getPhysicsSpace().addBody(tank.getControl(Vehicle2D.class), true);
        rootNode.attachChild(tank);
    }
    
    @SuppressWarnings("unchecked")
    private void prepareSimpleTerrain() {  
        Dyn4jAppState<PhysicsBody2D> dyn4jAppState = stateManager.getState(Dyn4jAppState.class);
        
        {
            Geometry geom = new Geometry("1", new Sprite(10, 0.5f));
            geom.setMaterial(getUnshadedColorMaterialFromClassPath(assetManager, ColorRGBA.randomColor()));
            
            BodyFixture bf = new BodyFixture(createRectangle(10, 0.5));
            bf.setFriction(5);
                
            
            RigidBody2D rbd = new RigidBody2D();
            rbd.addFixture(bf);
            rbd.setMass(MassType.INFINITE);
            rbd.translate(0, -3);
            geom.addControl(rbd);
            
            dyn4jAppState.getPhysicsSpace().addBody(rbd);
            rootNode.attachChild(geom);
        }
    }
}
