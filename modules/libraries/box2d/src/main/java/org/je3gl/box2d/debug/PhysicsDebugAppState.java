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
package org.je3gl.box2d.debug;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;
import java.util.concurrent.atomic.AtomicReference;
import org.box2d.jni.b2DebugDraw;
import org.box2d.jni.b2Pos;
import org.box2d.jni.b2Rot;
import org.box2d.jni.draw.DrawBoundsFcnI;
import org.box2d.jni.draw.DrawCircleFcnI;
import org.box2d.jni.draw.DrawLineFcnI;
import org.box2d.jni.draw.DrawPointFcnI;
import org.box2d.jni.draw.DrawPolygonFcnI;
import org.box2d.jni.draw.DrawSolidCapsuleFcnI;
import org.box2d.jni.draw.DrawSolidCircleFcnI;
import org.box2d.jni.draw.DrawSolidPolygonFcnI;
import org.box2d.jni.draw.DrawStringFcnI;
import org.box2d.jni.draw.DrawTransformFcnI;
import static org.box2d.jni.include.MathFunctions.*;
import org.je3gl.box2d.Box2dAppState;
import org.je3gl.box2d.DrawSettings;
import org.je3gl.box2d.PhysicsSpace;
import org.je3gl.box2d.debug.batch.BatchSnapshot;


/**
 *
 * @author wil
 */
public class PhysicsDebugAppState extends AbstractAppState {
        
    private PhysicsDebugSceneProcessor debugProcessor;
    
    /** Debugger view. */
    protected ViewPort viewPort;
    /** <code>JME3</code> renderer. */
    protected RenderManager rm;
    
    //----------------------------------------------------------------------
    //                              CALLBACKS
    //----------------------------------------------------------------------
    
    private final DrawPolygonFcnI DrawPolygonFcn = (transform, vertices, vertexCount, color, context) -> {
        
    };
    
    private final DrawSolidPolygonFcnI DrawSolidPolygonFcn = (transform, vertices, vertexCount, radius, color, context) -> {
        
    };
    
    private final DrawCircleFcnI DrawCircleFcn = (center, radius, color, context) -> {
        synchronized (debugProcessor.getLock()) {
            BatchSnapshot snapshot = debugProcessor.getSnapshot().get();
            snapshot.drawCircle(center.x().floatValue(), center.y().floatValue(), radius, color);
        }
    };
    
    private final DrawSolidCircleFcnI DrawSolidCircleFcn = (transform, center, radius, color, context) -> {
        synchronized (debugProcessor.getLock()) {
            b2Rot rot = transform.q();
            b2Pos pos = transform.p();

            BatchSnapshot snapshot = debugProcessor.getSnapshot().get();
            snapshot.drawSolidCircle(pos.x().floatValue(), pos.y().floatValue(), radius, b2Rot_GetAngle(rot), color);
        }
    };
    
    private final DrawSolidCapsuleFcnI DrawSolidCapsuleFcn = (p1, p2, radius, color, context) -> {
        
    };
    
    private final DrawLineFcnI DrawLineFcn = (p1, p2, color, context) -> {
        synchronized (debugProcessor.getLock()) {
            BatchSnapshot snapshot = debugProcessor.getSnapshot().get();
            snapshot.drawLine(p1.x().floatValue(), p1.y().floatValue(), p2.x().floatValue(), p2.y().floatValue(), color);
        }
    };

    private final DrawTransformFcnI DrawTransformFcn = (transform, context) -> {
        
    };
    
    private final DrawPointFcnI DrawPointFcn = (p, size, color, context) -> {
        synchronized (debugProcessor.getLock()) {
            BatchSnapshot snapshot = debugProcessor.getSnapshot().get();
            snapshot.drawPoint(p.x().floatValue(), p.y().floatValue(), size, color);
        }
    };
    
    private final DrawStringFcnI DrawStringFcn = (p, s, color, context) -> {
        
    };
    
    private final DrawBoundsFcnI DrawBoundsFcn = (aabb, color, context) -> {
        
    };
    
    public PhysicsDebugAppState() {
        
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        AssetManager assetManager = app.getAssetManager();
        RenderManager renderManager = app.getRenderManager();
        
        Box2dAppState box2dAppState = stateManager.getState(Box2dAppState.class);
        PhysicsSpace physicsSpace = box2dAppState.getPhysicsSpace();
        physicsSpace.setEnableDebugger(true);
        
        viewPort  = renderManager.createMainView("Physics Debug Overlay", app.getCamera());
        viewPort.setClearFlags(false, true, true);
        
        debugProcessor = new PhysicsDebugSceneProcessor(assetManager);
        viewPort.addProcessor(debugProcessor);

        b2DebugDraw debugDraw = physicsSpace.getDebugDraw();
        debugDraw.DrawBoundsFcn(DrawBoundsFcn)
                .DrawCircleFcn(DrawCircleFcn)
                .DrawSolidCircleFcn(DrawSolidCircleFcn)
                .DrawLineFcn(DrawLineFcn)
                .DrawPointFcn(DrawPointFcn)
                .DrawPolygonFcn(DrawPolygonFcn)
                .DrawSolidCapsuleFcn(DrawSolidCapsuleFcn)                
                .DrawSolidPolygonFcn(DrawSolidPolygonFcn)
                .DrawTransformFcn(DrawTransformFcn)
                .DrawStringFcn(DrawStringFcn);
        
        DrawSettings settings = box2dAppState.getDrawSettings();
        debugDraw.drawShapes(settings.drawShapes())
                .drawBodyNames(settings.drawBodyNames())
                .drawJoints(settings.drawJoints())
                .drawAnchorA(settings.drawAnchorA())
                .drawChainNormals(settings.drawChainNormals())
                .drawContactFeatures(settings.drawContactFeatures())
                .drawFrictionForces(settings.drawFrictionForces())
                .drawContactNormals(settings.drawContactNormals())
                .drawContacts(settings.drawContacts())
                .drawGraphColors(settings.drawGraphColors())
                .drawIslands(settings.drawIslands())
                .drawJointExtras(settings.drawJointExtras())
                .drawMass(settings.drawMass())
                .drawBounds(settings.drawBounds());
        super.initialize(stateManager, app);
    }
    
    @Override
    public void render(RenderManager rm) {
        
    }
}
