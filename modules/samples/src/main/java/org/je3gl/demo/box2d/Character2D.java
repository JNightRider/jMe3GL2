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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import org.je3gl.box2d.Box2dAppState;
import org.je3gl.box2d.ThreadingType;
import org.je3gl.renderer.Camera2DAppSate;
import org.je3gl.renderer.UnitComparator;
import org.je3gl.utilities.ColorUtilities;

import org.box2d.jni.*;
import org.box2d.jni.system.*;

import static org.box2d.jni.b2BodyType.*;

import static org.box2d.jni.include.Box2d.*;
import static org.box2d.jni.include.Collision.*;
import static org.box2d.jni.include.MathFunctions.*;
import static org.box2d.jni.include.Types.*;
import static org.box2d.jni.system.ArenaAlloc.*;
import org.je3gl.box2d.util.ParsePath;

/**
 * Class where a small platform game is exemplified and how it can be controlled 
 * using the jMe3GL2 library.
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class Character2D extends SimpleApplication  {

    /**
     * The main method; uses zero arguments in args array
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Character2D app = new Character2D();
        app.start();
    }

    /*(non-Javadoc)
     */
    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(
            ColorUtilities.darker(new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f))
        );
        
//        Camera2DAppSate camera2DAppSate = new Camera2DAppSate(2);
//        camera2DAppSate.setUnitComparator(Vector3f.UNIT_Z, UnitComparator.UType.World, RenderQueue.Bucket.Translucent, RenderQueue.Bucket.Transparent);
//        stateManager.attach(camera2DAppSate);

        Box2dAppState box2d = new Box2dAppState(ThreadingType.PARALLEL);
        box2d.setDebugEnabled(true);
        stateManager.attach(box2d);
        
        prepareGround();
    }
    
    private void prepareGround() {
        Box2dAppState box2dAppState = stateManager.getState(Box2dAppState.class);
        b2WorldId worldId = box2dAppState.getPhysicsSpace().getWorldId();
        
        b2BodyId groundId1;
        {
            b2BodyDef bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
            bodyDef.position(b2Pos.malloc().set( 0.0f, 0.0f ));
            groundId1 = b2CreateBody( worldId, bodyDef, b2BodyId.malloc() );

            String path =
                    """
                    M 2.6458333,201.08333 H 293.68751 v -47.625 h -2.64584 l -10.58333,7.9375 -13.22916,7.9375 -13.24648,5.29167 
                    -31.73269,7.9375 -21.16667,2.64583 -23.8125,10.58333 H 142.875 v -5.29167 h -5.29166 v 5.29167 H 119.0625 v 
                    -2.64583 h -2.64583 v -2.64584 h -2.64584 v -2.64583 H 111.125 v -2.64583 H 84.666668 v -2.64583 h -5.291666 v 
                    -2.64584 h -5.291667 v -2.64583 H 68.791668 V 174.625 h -5.291666 v -2.64584 H 52.916669 L 39.6875,177.27083 H 
                    34.395833 L 23.8125,185.20833 H 15.875 L 5.2916669,187.85416 V 153.45833 H 2.6458333 v 47.625
                    """;

            b2Vec2.Buffer points = b2Vec2.malloc(64);

            b2Vec2 offset = b2Vec2.malloc().set( -50.0f, -200.0f );
            float scale = 0.2f;

            int count = ParsePath.parse( path, offset, points, 64, scale, false );

            b2ChainDef chainDef = b2DefaultChainDef(b2ChainDef.malloc());
            chainDef.points(points);
            chainDef.count(count);
            chainDef.isLoop(true);

            b2CreateChain( groundId1, chainDef, b2ChainId.malloc() );
        }

        b2BodyId groundId2;
        {
            b2BodyDef bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
            bodyDef.position(b2Pos.malloc().set( 98.0f, 0.0f ));
            groundId2 = b2CreateBody( worldId, bodyDef, b2BodyId.malloc() );

            String path = """
                    M 2.6458333,201.08333 H 293.68751 l 0,-23.8125 h -23.8125 l 21.16667,21.16667 h -23.8125 l -39.68751,-13.22917 
                    -26.45833,7.9375 -23.8125,2.64583 h -13.22917 l -0.0575,2.64584 h -5.29166 v -2.64583 l -7.86855,-1e-5 
                    -0.0114,-2.64583 h -2.64583 l -2.64583,2.64584 h -7.9375 l -2.64584,2.64583 -2.58891,-2.64584 h -13.28609 v 
                    -2.64583 h -2.64583 v -2.64584 l -5.29167,1e-5 v -2.64583 h -2.64583 v -2.64583 l -5.29167,-1e-5 v -2.64583 h 
                    -2.64583 v -2.64584 h -5.291667 v -2.64583 H 92.60417 V 174.625 h -5.291667 v -2.64584 l -34.395835,1e-5 
                    -7.9375,-2.64584 -7.9375,-2.64583 -5.291667,-5.29167 H 21.166667 L 13.229167,158.75 5.2916668,153.45833 H 
                    2.6458334 l -10e-8,47.625
                    """;

            b2Vec2.Buffer points = b2Vec2.malloc(64);

            b2Vec2 offset = b2Vec2.malloc().set( 0.0f, -200.0f );
            float scale = 0.2f;

            int count = ParsePath.parse(path, offset, points, 64, scale, false );

            b2ChainDef chainDef = b2DefaultChainDef(b2ChainDef.malloc());
            chainDef.points(points);
            chainDef.count(count);
            chainDef.isLoop(true);

            b2CreateChain( groundId2, chainDef, b2ChainId.malloc() );
        }

        {
            b2Polygon box = b2MakeBox( 0.5f, 0.125f, b2Polygon.malloc() );

            b2ShapeDef shapeDef = b2DefaultShapeDef(b2ShapeDef.malloc());

            b2RevoluteJointDef jointDef = b2DefaultRevoluteJointDef(b2RevoluteJointDef.malloc());
            jointDef.maxMotorTorque(10.0f);
            jointDef.enableMotor(true);
            jointDef.hertz(3.0f);
            jointDef.dampingRatio(0.8f);
            jointDef.enableSpring(true);

            float xBase = 48.7f;
            float yBase = 9.2f;
            int count = 50;
            b2BodyId prevBodyId = groundId1;
            for ( int i = 0; i < count; ++i )
            {
                b2BodyDef bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
                bodyDef.type(b2_dynamicBody);
                bodyDef.position(b2Pos.malloc().set( xBase + 0.5f + 1.0f * i, yBase ));
                bodyDef.angularDamping(0.2f);
                b2BodyId bodyId = b2CreateBody( worldId, bodyDef, b2BodyId.malloc() );
                b2CreatePolygonShape( bodyId, shapeDef, box, b2ShapeId.malloc() );

                b2Pos pivot = b2Pos.malloc().set( xBase + 1.0f * i, yBase );
                jointDef.base().bodyIdA(prevBodyId);
                jointDef.base().bodyIdB(bodyId);
                jointDef.base().localFrameA().p( b2Body_GetLocalPoint( jointDef.base().bodyIdA(), pivot, b2Vec2.malloc() ) );
                jointDef.base().localFrameB().p( b2Body_GetLocalPoint( jointDef.base().bodyIdB(), pivot, b2Vec2.malloc() ) );
                b2CreateRevoluteJoint( worldId, jointDef, b2JointId.malloc() );

                prevBodyId = bodyId;
            }

            b2Pos pivot = b2Pos.malloc().set( xBase + 1.0f * count, yBase );
            jointDef.base().bodyIdA(prevBodyId);
            jointDef.base().bodyIdB(groundId2);
            jointDef.base().localFrameA().p(b2Body_GetLocalPoint( jointDef.base().bodyIdA(), pivot, b2Vec2.malloc() ));
            jointDef.base().localFrameB().p(b2Body_GetLocalPoint( jointDef.base().bodyIdB(), pivot, b2Vec2.malloc() ));
            b2CreateRevoluteJoint( worldId, jointDef, b2JointId.malloc() );
        }
    }
}
