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
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
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
import org.box2d.jni.b2Pos;
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

import org.je3gl.box2d.PhysicsSpace;
import org.je3gl.box2d.util.Converter;
import org.je3gl.box2d.DrawSettings;

import org.je3gl.scene.debug.custom.DebugGraphics;

import static org.box2d.jni.include.Types.*;
import org.box2d.jni.system.ArenaAlloc;
import static org.box2d.jni.system.ArenaAlloc.*;
import static org.box2d.jni.system.MemoryUtil.*;
import org.je3gl.box2d.Box2dAppState;
import org.je3gl.box2d.debug.renderer.LineRender;
import org.je3gl.box2d.debug.renderer.PointRender;
import org.je3gl.box2d.debug.renderer.PolygonRender;
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

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    
    private Renderer renderer;
    private RenderManager renderManager;
    private ViewPort viewPort;
    
    private Box2dAppState box2dAppState;
    
    /** Rendering manager. */
    private Graphics2DRenderer graphics; // Debugger
    
    /** Object indicating which drawings are performed in the debugger. */
    private b2DebugDraw debugDraw;
    
    private FloatsPool floats = new FloatsPool();
    
    private PolygonRender polygonRender;
    private PolygonRender solidPolygonRender;
    private LineRender lineRender;
    private PointRender pointRender;

    //----------------------------------------------------------------------
    //                              CALLBACKS
    //----------------------------------------------------------------------
    
    private final DrawPolygonFcnI DrawPolygonFcn = (transform, vertices, vertexCount, color, context) -> {
         
    };
    
    private final DrawSolidPolygonFcnI DrawSolidPolygonFcn = (transform, vertices, vertexCount, radius, color, context) -> {
        if ( initialized.get() ) {
            b2Vec2.Buffer buffer = b2Vec2.createSafe(vertices, vertexCount);
            Vector3f[] vs = new Vector3f[vertexCount];
            
            int index = 0;
            for (b2Vec2 vec2 : buffer) {
                vs[index++] = new Vector3f(vec2.x(), vec2.y(), 0);
            }
            polygonRender.addDrawPolygon(ColorUtilities.fromIntRGBA(color, 1f), vs);

        }
        transform.close();
    };
    
    private final DrawCircleFcnI DrawCircleFcn = (center, radius, color, context) -> {
        
    };
    
    private final DrawSolidCircleFcnI DrawSolidCircleFcn = (transform, center, radius, color, context) -> {
        
    };
    
    private final DrawSolidCapsuleFcnI DrawSolidCapsuleFcn = (p1, p2, radius, color, context) -> {
        
    };
    
    private final DrawLineFcnI DrawLineFcn = (p1, p2, color, context) -> {
        try (p1; p2) {
            if ( initialized.get() ) {
                 lineRender.addAddLine(p1.x().floatValue(), p1.y().floatValue(), p2.x().floatValue(), p2.y().floatValue(), ColorUtilities.fromIntRGBA(color, 1f));
            }
        }
    };

    private final DrawTransformFcnI DrawTransformFcn = (transform, context) -> {
        
    };
    
    private final DrawPointFcnI DrawPointFcn = (p, size, color, context) -> {
        if ( initialized.get() ) {
            pointRender.addDrawPoint(p.x().floatValue(), p.y().floatValue(), size, ColorUtilities.fromIntRGBA(color, 1f));
        }
        p.close();
    };
    
    private final DrawStringFcnI DrawStringFcn = (p, s, color, context) -> {
        
    };
    
    private final DrawBoundsFcnI DrawBoundsFcn = (aabb, color, context) -> {
        
    };
    
    public Box2dDebug(Box2dAppState box2dAppState) {
        this.box2dAppState = box2dAppState;
        this.debugDraw = b2DefaultDebugDraw(b2DebugDraw.calloc());
    }

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        AssetManager assetManager = box2dAppState.getApplication()
                                                 .getAssetManager();
        polygonRender = new PolygonRender(assetManager);
        solidPolygonRender = new PolygonRender(assetManager);
        pointRender = new PointRender(assetManager);
        lineRender  = new LineRender(assetManager);
        
        renderManager = rm;
        viewPort = vp;
        renderer = rm.getRenderer();
        graphics = new Graphics2DRenderer(box2dAppState);
        debugDraw/*.DrawBoundsFcn(DrawBoundsFcn)
                .DrawCircleFcn(DrawCircleFcn)*/
                .DrawLineFcn(DrawLineFcn)
                .DrawPointFcn(DrawPointFcn)
//                .DrawPolygonFcn(DrawPolygonFcn)
//                .DrawSolidCapsuleFcn(DrawSolidCapsuleFcn)
//                .DrawSolidCircleFcn(DrawSolidCircleFcn)
                .DrawSolidPolygonFcn(DrawSolidPolygonFcn)
//                .DrawTransformFcn(DrawTransformFcn)
//                .DrawStringFcn(DrawStringFcn);
                ;
        initialized.set(true);
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
        polygonRender.flushDraw(renderManager, true);
        solidPolygonRender.flushDraw(renderManager, true);
        lineRender.flushDraw(renderManager, true);
        pointRender.flushDraw(renderManager, true);
    }

    @Override
    public void postFrame(FrameBuffer fb) {
        
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
