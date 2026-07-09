/*
BSD 3-Clause License

Copyright (c) 2023-2026, Night Rider (Wilson)

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
package org.je3gl.demo.box2d;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Geometry;
import org.box2d.jni.b2BodyType;
import org.box2d.jni.b2Polygon;
import org.box2d.jni.b2ShapeDef;
import org.box2d.jni.b2ShapeId;
import org.box2d.jni.include.Box2d;
import org.box2d.jni.include.Collision;
import static org.box2d.jni.include.Collision.*;
import org.box2d.jni.include.Types;
import static org.box2d.jni.include.Types.*;
import org.je3gl.box2d.Box2dAppState;
import org.je3gl.box2d.ThreadingType;
import org.je3gl.box2d.control.RigidBody2D;
import org.je3gl.box2d.debug.Box2dDebugAppState;
import org.je3gl.box2d.scene.tile.Box2dTilesheet;
import org.je3gl.scene.shape.Sprite;
import org.je3gl.scene.tile.TileMap;
import static org.je3gl.utilities.MaterialUtilities.getUnshadedMaterialFromClassPath;
import static org.je3gl.utilities.TileMapUtilities.*;

/**
 * Test class where the use of physics is exemplified.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class TileMap2D extends SimpleApplication {

    /**
     * The main method; uses zero arguments in args array
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        TileMap2D app = new TileMap2D();
        app.start();
    }

    /* (non-Javadoc) 
     */
    @Override
    public void simpleInitApp() {
        Box2dAppState box2dAppState = new Box2dAppState(ThreadingType.PARALLEL);
        box2dAppState.setDebugEnabled(true);
        stateManager.attach(box2dAppState);
    
        Geometry cube = new Geometry("Cube", new Sprite(1, 1, 22, 12, 17, 9));
        cube.setMaterial(getUnshadedMaterialFromClassPath(assetManager, "Textures/tilesheet_complete_2X.png"));
        
        {
            RigidBody2D body2D = new RigidBody2D();
            body2D.setType(b2BodyType.b2_dynamicBody);
            box2dAppState.getPhysicsSpace().addBody(body2D);
            cube.addControl(body2D);
            
            b2ShapeDef shapeDef = b2DefaultShapeDef(b2ShapeDef.malloc());
            b2Polygon box = b2MakeBox(0.5f, 0.5f, b2Polygon.malloc());
            body2D.addPolygonShape(shapeDef, box);
        }
        
        rootNode.attachChild(cube);
        prepareGround();
    }
    
    /**
     * Prepare a simple terrain.
     */
    @SuppressWarnings("unchecked")
    private void prepareGround() {        
        TileMap map = gl2GetTileMap("Map01", "Textures/tilesheet_complete_2X.png", 22, 12, Box2dTilesheet.getInstance() ,assetManager);
        map.setPhysicsSpace(stateManager.getState(Box2dAppState.class).getPhysicsSpace());
        rootNode.attachChild(map);
        
        //----------------------------------------------------------------------
        //                              Block - 1
        //----------------------------------------------------------------------
        map.addTile(gl2GetTile(1, 0, 1, 1, -1, -3, 0, true));
        map.addTile(gl2GetTile(2, 0, 1, 1, 0, -3, 0, true));
        map.addTile(gl2GetTile(5, 1, 1, 1, 1, -3, 0, false));
        
        map.addTile(gl2GetTile(0, 0, 1, 1, -1, -4, 0, false));
        map.addTile(gl2GetTile(0, 0, 1, 1, 0, -4, 0, false));
        map.addTile(gl2GetTile(0, 2, 1, 1, 1, -4, 0, false));
        
        map.addTile(gl2GetTile(10, 1, 1, 1, -1, -2, 0, false));
        
        //----------------------------------------------------------------------
        //                              Block - 2
        //----------------------------------------------------------------------
        map.addTile(gl2GetTile(1, 0, 1, 1, 1, -2, 0, true));
        map.addTile(gl2GetTile(2, 0, 1, 1, 2, -2, 0, true));
        map.addTile(gl2GetTile(3, 0, 1, 1, 3, -2, 0, true));
        
        map.addTile(gl2GetTile(0, 0, 1, 1, 2, -3, 0, false));
        map.addTile(gl2GetTile(0, 1, 1, 1, 3, -3, 0, false));
        map.addTile(gl2GetTile(0, 0, 1, 1, 2, -4, 0, false));
        map.addTile(gl2GetTile(0, 0, 1, 1, 3, -4, 0, false));
        
        map.addTile(gl2GetTile(9, 0, 1, 1, 3, -1, 0, false));
    }
}
