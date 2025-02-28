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
import com.jme3.scene.Geometry;

import org.je3gl.physics.Dyn4jAppState;
import org.je3gl.physics.ThreadingType;
import org.je3gl.physics.control.PhysicsBody2D;
import org.je3gl.physics.control.RigidBody2D;
import org.je3gl.scene.shape.Sprite;
import org.je3gl.scene.tile.TileMap;
import org.je3gl.utilities.TileMapUtilities;
import static org.je3gl.utilities.GeometryUtilities.*;
import static org.je3gl.utilities.MaterialUtilities.*;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;


/**
 * Test class where the use of physics is exemplified.
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class Physical2D extends SimpleApplication {
    
    /**
     * The main method; uses zero arguments in args array
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Physical2D app = new Physical2D();
        app.start();
    }

    /*
     * (non-Javadoc)
     * @see com.jme3.app.SimpleApplication#simpleInitApp() 
     */
    @Override
    public void simpleInitApp() {
        Dyn4jAppState<PhysicsBody2D> dyn4jAppState = new Dyn4jAppState<>(ThreadingType.PARALLEL);
        dyn4jAppState.setDebugEnabled(true);
        stateManager.attach(dyn4jAppState);
        
        Geometry cube = new Geometry("Cube", new Sprite(1, 1, 22, 12, 17, 9));
        cube.setMaterial(getUnshadedMaterialFromClassPath(assetManager, "Textures/tilesheet_complete_2X.png"));
        
        BodyFixture bf1 = new BodyFixture(dyn4jCreateRectangle(1, 1));
        BodyFixture bf2 = new BodyFixture(dyn4jCreateCircle(1));
        
        bf2.setSensor(false);
        bf1.setSensor(true);
        
        RigidBody2D body2D = new RigidBody2D();
        cube.addControl(body2D);
        
        body2D.addFixture(bf1);
        body2D.addFixture(bf2);
        body2D.setMass(MassType.NORMAL);
        
        dyn4jAppState.getPhysicsSpace().addBody(body2D);
        rootNode.attachChild(cube);
        
        prepareGround();
    }
    
    /**
     * Prepare a simple terrain.
     */
    @SuppressWarnings(value = {"unchecked", "deprecation"})
    private void prepareGround() {
        TileMap map = TileMapUtilities.gl2GetTileMap("Map01", "Textures/tilesheet_complete_2X.png", 22, 12, assetManager);
        map.setPhysicsSpace(stateManager.getState(Dyn4jAppState.class).getPhysicsSpace());
        rootNode.attachChild(map);
        
        //----------------------------------------------------------------------
        //                              Block - 1
        //----------------------------------------------------------------------
        map.addTile(TileMapUtilities.gl2GetTile(1, 0, 1, 1, -1, -3, 0, true));
        map.addTile(TileMapUtilities.gl2GetTile(2, 0, 1, 1, 0, -3, 0, true));
        map.addTile(TileMapUtilities.gl2GetTile(5, 1, 1, 1, 1, -3, 0, false));
        
        map.addTile(TileMapUtilities.gl2GetTile(0, 0, 1, 1, -1, -4, 0, false));
        map.addTile(TileMapUtilities.gl2GetTile(0, 0, 1, 1, 0, -4, 0, false));
        map.addTile(TileMapUtilities.gl2GetTile(0, 2, 1, 1, 1, -4, 0, false));
        
        map.addTile(TileMapUtilities.gl2GetTile(10, 1, 1, 1, -1, -2, 0, false));
        
        //----------------------------------------------------------------------
        //                              Block - 2
        //----------------------------------------------------------------------
        map.addTile(TileMapUtilities.gl2GetTile(1, 0, 1, 1, 1, -2, 0, true));
        map.addTile(TileMapUtilities.gl2GetTile(2, 0, 1, 1, 2, -2, 0, true));
        map.addTile(TileMapUtilities.gl2GetTile(3, 0, 1, 1, 3, -2, 0, true));
        
        map.addTile(TileMapUtilities.gl2GetTile(0, 0, 1, 1, 2, -3, 0, false));
        map.addTile(TileMapUtilities.gl2GetTile(0, 1, 1, 1, 3, -3, 0, false));
        map.addTile(TileMapUtilities.gl2GetTile(0, 0, 1, 1, 2, -4, 0, false));
        map.addTile(TileMapUtilities.gl2GetTile(0, 0, 1, 1, 3, -4, 0, false));
        
        map.addTile(TileMapUtilities.gl2GetTile(9, 0, 1, 1, 3, -1, 0, false));
    }
}
