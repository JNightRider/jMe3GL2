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
import com.jme3.system.AppSettings;

import org.je3gl.box2d.Box2dAppState;
import org.je3gl.box2d.ThreadingType;
import org.je3gl.renderer.Camera2DAppSate;
import org.je3gl.renderer.UnitComparator;

import org.box2d.jni.*;
import org.box2d.jni.system.*;
import static org.box2d.jni.b2BodyType.*;

import static org.box2d.jni.include.Box2d.*;
import static org.box2d.jni.include.Collision.*;
import static org.box2d.jni.include.Types.*;

/**
 * This is a good example of how to get up and running with Box2D - <b>HelloBox2D</b>.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class HelloBox2D extends SimpleApplication {

    /**
     * The main method; uses zero arguments in args array
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Sys.DISABLE_DEBUG.set(true);
        
        HelloBox2D app = new HelloBox2D();
        AppSettings settings = new AppSettings(true);
        settings.setGammaCorrection(false);
        app.setSettings(settings);
        app.start();
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
        
        Camera2DAppSate camera2DAppSate = new Camera2DAppSate(2);
        camera2DAppSate.setUnitComparator(Vector3f.UNIT_Z, UnitComparator.UType.World, RenderQueue.Bucket.Translucent, RenderQueue.Bucket.Transparent);
        stateManager.attach(camera2DAppSate);

        Box2dAppState box2d = new Box2dAppState(ThreadingType.PARALLEL);
        box2d.setDebugEnabled(true);
        stateManager.attach(box2d);
        
        b2WorldId worldId = box2d.getPhysicsSpace().getWorldId();
        b2BodyDef groundBodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
        groundBodyDef.position(b2Pos.nmalloc().set(0.0f, -10.0f));

        b2BodyId groundId = b2CreateBody(worldId, groundBodyDef, b2BodyId.malloc());
        b2Polygon groundBox = b2MakeBox(50.0f, 10.0f, b2Polygon.malloc());

        b2ShapeDef groundShapeDef = b2DefaultShapeDef(b2ShapeDef.malloc());
        b2CreatePolygonShape(groundId, groundShapeDef, groundBox, b2ShapeId.malloc());

        b2BodyDef bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
        bodyDef.type(b2_dynamicBody);
        bodyDef.position(b2Pos.nmalloc().set(0.0f, 10.0f));
        b2BodyId bodyId = b2CreateBody(worldId, bodyDef, b2BodyId.malloc());

        b2Polygon dynamicBox = b2MakeBox(1.0f, 1.0f, b2Polygon.malloc());

        b2ShapeDef shapeDef = b2DefaultShapeDef(b2ShapeDef.malloc());
        shapeDef.density(1.0f);
        shapeDef.material().friction(0.3f);

        b2CreatePolygonShape(bodyId, shapeDef, dynamicBox, b2ShapeId.malloc());        
        b2Body_SetName(bodyId, "Player");
        
        {
            b2BodyDef def = b2DefaultBodyDef(b2BodyDef.malloc());
            def.type(b2_dynamicBody);
            def.position(b2Pos.nmalloc().set(-1, 20));
            
            b2BodyId body = b2CreateBody(worldId, def, b2BodyId.malloc());
            b2CreateBody(worldId, def, b2BodyId.malloc());
            
            b2ShapeDef shapeDf = b2DefaultShapeDef(b2ShapeDef.malloc());
            shapeDf.material().friction(0.42f);
            
            b2Capsule capsule = b2Capsule.malloc();
            capsule.center1(b2Vec2.malloc().set(0, 0));
            capsule.center2(b2Vec2.malloc().set(10, 0));
            capsule.radius(1f);
            
            b2CreateCapsuleShape(body, shapeDf, capsule, b2ShapeId.malloc());
        }
    }
}
