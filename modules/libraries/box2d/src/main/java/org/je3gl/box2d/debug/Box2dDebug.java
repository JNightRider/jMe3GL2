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
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.FrameBuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.box2d.jni.b2DebugDraw;
import static org.box2d.jni.b2HexColor.*;
import org.box2d.jni.b2Pos;
import org.box2d.jni.b2Rot;
import org.box2d.jni.b2Transform;
import org.box2d.jni.b2Vec2;
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
import org.box2d.jni.include.MathFunctions;
import static org.box2d.jni.include.MathFunctions.*;

import org.je3gl.box2d.PhysicsSpace;
import org.je3gl.box2d.util.Converter;
import org.je3gl.box2d.DrawSettings;

import org.je3gl.scene.debug.custom.DebugGraphics;

import static org.box2d.jni.include.Types.*;
import org.box2d.jni.libc.LibCStdlib;
import org.box2d.jni.system.ArenaAlloc;
import static org.box2d.jni.system.ArenaAlloc.*;
import static org.box2d.jni.system.MemoryUtil.*;
import org.je3gl.box2d.Box2dAppState;
import org.je3gl.box2d.debug.renderer.CapsuleRender;
import org.je3gl.box2d.debug.renderer.CircleRender;
import org.je3gl.box2d.debug.renderer.LineRender;
import org.je3gl.box2d.debug.renderer.PointRender;
import org.je3gl.box2d.debug.renderer.PolygonRender;
import org.je3gl.box2d.debug.renderer.StringRender;
import org.je3gl.box2d.util.FloatsPool;
import org.je3gl.utilities.ColorUtilities;


/**
 * Class <code>Box2dDebug</code> responsible for managing a state for the
 * debugging of the physical forms of the bodies that are added to the world of
 * <b>Box2d</b>.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class Box2dDebug implements SceneProcessor {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(Box2dDebug.class.getName());
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    
    private Renderer renderer;
    private RenderManager renderManager;
    private ViewPort viewPort;
    
    private Box2dAppState box2dAppState;
    
    /** Rendering manager. */
    
    /** Object indicating which drawings are performed in the debugger. */
    private b2DebugDraw debugDraw;
        
    private PolygonRender polygonRender;
    private PolygonRender solidPolygonRender;
    private CircleRender circleRender;
    private CircleRender solidCircleRender;
    private CapsuleRender solidCapsuleRender;
    private LineRender lineRender;
    private PointRender pointRender;
    private StringRender stringRender;

    //----------------------------------------------------------------------
    //                              CALLBACKS
    //----------------------------------------------------------------------
    
    private final DrawPolygonFcnI DrawPolygonFcn = (transform, vertices, vertexCount, color, context) -> {
        if ( initialized.get() ) {
            b2Vec2.Buffer buffer = b2Vec2.createSafe(vertices, vertexCount);
            Vector3f[] vs = new Vector3f[vertexCount];
            
            int index = 0;
            for (b2Vec2 vec2 : buffer) {
                vs[index++] = new Vector3f(vec2.x(), vec2.y(), 0);
            }
            b2Pos pos = transform.p();
            b2Rot rot = transform.q();
            polygonRender.addDrawPolygon(ColorUtilities.fromIntRGBA(color, 1f), vs, pos.x().floatValue(), pos.y().floatValue(), b2Rot_GetAngle(rot));

        }
        transform.close();
    };
    
    private final DrawSolidPolygonFcnI DrawSolidPolygonFcn = (transform, vertices, vertexCount, radius, color, context) -> {
        if ( initialized.get() ) {
            b2Vec2.Buffer buffer = b2Vec2.createSafe(vertices, vertexCount);
            Vector3f[] vs = new Vector3f[vertexCount];
            
            int index = 0;
            for (b2Vec2 vec2 : buffer) {
                vs[index++] = new Vector3f(vec2.x(), vec2.y(), 0);
            }
            b2Pos pos = transform.p();
            b2Rot rot = transform.q();
            solidPolygonRender.addDrawPolygon(ColorUtilities.fromIntRGBA(color, 1f), vs, pos.x().floatValue(), pos.y().floatValue(), b2Rot_GetAngle(rot));

        }
        transform.close();
    };
    
    private final DrawCircleFcnI DrawCircleFcn = (center, radius, color, context) -> {
        try (center) {
            if (initialized.get()) {
                circleRender.addDrawCircle(0, 0, 0, center.x().floatValue(), center.y().floatValue(), radius, ColorUtilities.fromIntRGBA(color, 1f));
            }
        }
    };
    
    private final DrawSolidCircleFcnI DrawSolidCircleFcn = (transform, center, radius, color, context) -> {
        try (transform; center) {
            if (initialized.get()) {
                b2Pos pos = transform.p();
                b2Rot rot = transform.q();
                solidCircleRender.addDrawCircle(pos.x().floatValue(), pos.y().floatValue(), b2Rot_GetAngle(rot), center.x(), center.y(), radius, ColorUtilities.fromIntRGBA(color, 1f));
            }
        }
    };
    
    private final DrawSolidCapsuleFcnI DrawSolidCapsuleFcn = (p1, p2, radius, color, context) -> {
        try (ArenaAlloc arena = allocPush(); p1; p2) {
            if ( initialized.get() ) {
                b2Vec2 d = b2SubPos( p1, p2, b2Vec2.calloc(arena) );
                float length = b2Length( d );
                if ( length < 0.001f )
                {
                    LOGGER.log(Level.WARNING, "sample app: capsule too short!");
                    return;
                }

                b2Vec2 axis = b2Vec2.calloc(arena).set( d.x() / length, d.y() / length );
                b2Transform transform = b2Transform.calloc(arena);

                transform.p(b2Lerp( b2ToVec2(p1, b2Vec2.calloc(arena)), b2ToVec2(p2, b2Vec2.calloc(arena)), 0.5f, b2Vec2.calloc(arena)) );
                transform.q().c(axis.x());
                transform.q().s(axis.y());


                ColorRGBA rgba = ColorUtilities.fromIntRGBA(color, 1f);
                solidCapsuleRender.addDrawCapsule(radius * 2, length + (radius * 2), b2Rot_GetAngle(transform.q()), transform.p().x(), transform.p().y(), rgba);
            }
        }
    };
    
    private final DrawLineFcnI DrawLineFcn = (p1, p2, color, context) -> {
        try (p1; p2) {
            if ( initialized.get() ) {
                lineRender.addAddLine(p1.x().floatValue(), p1.y().floatValue(), p2.x().floatValue(), p2.y().floatValue(), ColorUtilities.fromIntRGBA(color, 1f));
            }
        }
    };

    private final DrawTransformFcnI DrawTransformFcn = (transform, context) -> {
        try (ArenaAlloc arena = allocPush(); transform) {
            if ( initialized.get() ) {
                b2Vec2 p1 = b2ToVec2( transform.p(), b2Vec2.calloc(arena) );
                b2Vec2 p2 = b2MulAdd( p1, 1.0f, b2Rot_GetXAxis( transform.q(), b2Vec2.calloc(arena) ), b2Vec2.calloc(arena) );
                
                lineRender.addAddLine(p1.x().floatValue(), p1.y().floatValue(), p2.x().floatValue(), p2.y().floatValue(), ColorUtilities.fromIntRGBA(b2_colorRed, 1f));
                
                p2 = b2MulAdd( p1, 1.0f, b2Rot_GetYAxis( transform.q(), b2Vec2.calloc(arena) ), p2 );
                lineRender.addAddLine(p1.x().floatValue(), p1.y().floatValue(), p2.x().floatValue(), p2.y().floatValue(), ColorUtilities.fromIntRGBA(b2_colorGreen, 1f));
            }
        }
    };
    
    private final DrawPointFcnI DrawPointFcn = (p, size, color, context) -> {
        if ( initialized.get() ) {
            pointRender.addDrawPoint(p.x().floatValue(), p.y().floatValue(), size, ColorUtilities.fromIntRGBA(color, 1f));
        }
        p.close();
    };
    
    private final DrawStringFcnI DrawStringFcn = (p, s, color, context) -> {
        try (p) {
            if ( initialized.get() ) {
                String str = memUTF(s);
                stringRender.addDrawString(p.x().floatValue(), p.y().floatValue(), ColorUtilities.fromIntRGBA(color, 1f), str);
            }
        }
    };
    
    private final DrawBoundsFcnI DrawBoundsFcn = (aabb, color, context) -> {
        try (ArenaAlloc arena = allocPush(); aabb) {
            if ( initialized.get() ) {
                b2Vec2 lower = aabb.lowerBound();
                b2Vec2 upper = aabb.upperBound();
                
                b2Vec2 p1 = lower;
                b2Vec2 p2 = b2Vec2.calloc(arena).set( upper.x(), lower.y() );
                b2Vec2 p3 = upper;
                b2Vec2 p4 = b2Vec2.calloc(arena).set( lower.x(), upper.y() );
                
                lineRender.addAddLine(p1.x(), p1.y(), p2.x(), p2.y(), ColorUtilities.fromIntRGBA(color, 1f));
                lineRender.addAddLine(p2.x(), p2.y(), p3.x(), p3.y(), ColorUtilities.fromIntRGBA(color, 1f));
                lineRender.addAddLine(p3.x(), p3.y(), p4.x(), p4.y(), ColorUtilities.fromIntRGBA(color, 1f));
                lineRender.addAddLine(p4.x(), p4.y(), p1.x(), p1.y(), ColorUtilities.fromIntRGBA(color, 1f));
            }
        }
    };
    
    public Box2dDebug(Box2dAppState box2dAppState) {
        this.box2dAppState = box2dAppState;
        this.debugDraw = b2DefaultDebugDraw(b2DebugDraw.calloc());
    }

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        AssetManager assetManager = box2dAppState.getApplication()
                                                 .getAssetManager();
        circleRender = new CircleRender(assetManager);
        solidCircleRender = new CircleRender(assetManager);
        solidCapsuleRender = new CapsuleRender(assetManager);
        polygonRender = new PolygonRender(assetManager);
        solidPolygonRender = new PolygonRender(assetManager);
        pointRender = new PointRender(assetManager);
        lineRender  = new LineRender(assetManager);
        stringRender = new StringRender(assetManager, vp, new StringDebugGraphics(assetManager));
        
        renderManager = rm;
        viewPort = vp;
        renderer = rm.getRenderer();
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
        updateDrawFlags();
        initialized.set(true);
    }

    /**
     * Configure the debug flags to control which objects are drawn.
     */
    private void updateDrawFlags() {
        DrawSettings settings = box2dAppState.getDrawSettings();
        StringBuilder sb = new StringBuilder();
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

        sb.append("[jMe3GL2] :Charts for debugging Box2d-JNI bodies")
                .append('\n').append(" * drawShapes: ").append(settings.drawShapes())
                .append('\n').append(" * drawBodyNames: ").append(settings.drawBodyNames())
                .append('\n').append(" * drawJoints: ").append(settings.drawJoints())
                .append('\n').append(" * drawAnchorA: ").append(settings.drawAnchorA())
                .append('\n').append(" * drawChainNormals: ").append(settings.drawChainNormals())
                .append('\n').append(" * drawContactFeatures: ").append(settings.drawContactFeatures())
                .append('\n').append(" * drawFrictionForces: ").append(settings.drawFrictionForces())
                .append('\n').append(" * drawContactNormals: ").append(settings.drawContactNormals())
                .append('\n').append(" * drawContacts: ").append(settings.drawContacts())
                .append('\n').append(" * drawGraphColors: ").append(settings.drawGraphColors())
                .append('\n').append(" * drawIslands: ").append(settings.drawIslands())
                .append('\n').append(" * drawJointExtras: ").append(settings.drawJointExtras())
                .append('\n').append(" * drawMass: ").append(settings.drawMass())
                .append('\n').append(" * drawBounds: ").append(settings.drawBounds());
        LOGGER.info(String.valueOf(sb));
    }

    @Override
    public void reshape(ViewPort vp, int w, int h) {
        
    }

    @Override
    public boolean isInitialized() {
        return initialized.get();
    }

    @Override
    public void preFrame(float tpf) {
        
    }

    @Override
    public void postQueue(RenderQueue rq) {
        
    }

    @Override
    public void postFrame(FrameBuffer fb) {
        circleRender.flushDraw(renderManager, false);
        solidCircleRender.flushDraw(renderManager, true);
        solidCapsuleRender.flushDraw(renderManager, true);
        polygonRender.flushDraw(renderManager, false);
        solidPolygonRender.flushDraw(renderManager, true);
        lineRender.flushDraw(renderManager, false);
        pointRender.flushDraw(renderManager, false);     
        stringRender.flushDraw(renderManager, false);
    }

    @Override
    public void cleanup() {
        initialized.set(false);
    }

    @Override
    public void setProfiler(AppProfiler ap) {
        
    }

    public b2DebugDraw getDebugDraw() {
        return debugDraw;
    }
}
