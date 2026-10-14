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

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.texture.FrameBuffer;

import java.util.logging.Logger;

import org.box2d.jni.b2DebugDraw;
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

import org.je3gl.box2d.Box2dAppState;
import org.je3gl.box2d.DrawSettings;
import org.je3gl.box2d.PhysicsSpace;
import org.je3gl.box2d.debug.draw.Draw;
import org.je3gl.box2d.util.LockObject;
import org.je3gl.scene.debug.custom.DebugGraphics;

/**
 *
 * @author wil
 */
public class PhysicsDebugSceneProcessor implements SceneProcessor {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PhysicsDebugSceneProcessor.class.getName());

    private final AssetManager assetManager;
    private final AppStateManager stateManager;

    private Box2dAppState box2dAppState;

    private Draw drawables;
    private RenderManager renderManager;
    private DebugGraphics debugGraphics;

    private boolean initialized;

    public PhysicsDebugSceneProcessor(AssetManager assetManager, AppStateManager stateManager) {
        this.assetManager = assetManager;
        this.stateManager = stateManager;
    }

    //----------------------------------------------------------------------
    //                              CALLBACKS
    //----------------------------------------------------------------------
    private final DrawPolygonFcnI DrawPolygonFcn = (transform, vertices, vertexCount, color, context) -> {
        synchronized (LockObject.LOCK) {
            drawables.drawPolygon(transform, vertices, vertexCount, color);
        }
    };

    private final DrawSolidPolygonFcnI DrawSolidPolygonFcn = (transform, vertices, vertexCount, radius, color, context) -> {
        synchronized (LockObject.LOCK) {
            drawables.drawSolidPolygon(transform, vertices, vertexCount, radius, color);
        }
    };

    private final DrawCircleFcnI DrawCircleFcn = (center, radius, color, context) -> {
        synchronized (LockObject.LOCK) {
            drawables.drawCircle(center, radius, color);
        }
    };

    private final DrawSolidCircleFcnI DrawSolidCircleFcn = (transform, center, radius, color, context) -> {
        synchronized (LockObject.LOCK) {
            drawables.drawSolidCircle(transform, center, radius, color);
        }
    };

    private final DrawSolidCapsuleFcnI DrawSolidCapsuleFcn = (p1, p2, radius, color, context) -> {
        synchronized (LockObject.LOCK) {
            drawables.drawCapsule(p1, p2, radius, color, true);
        }
    };

    private final DrawLineFcnI DrawLineFcn = (p1, p2, color, context) -> {
        synchronized (LockObject.LOCK) {
            drawables.drawLine(p1, p2, color);
        }
    };

    private final DrawTransformFcnI DrawTransformFcn = (transform, context) -> {
        synchronized (LockObject.LOCK) {
            drawables.drawTransform(transform, 1.0f);
        }
    };

    private final DrawPointFcnI DrawPointFcn = (p, size, color, context) -> {
        synchronized (LockObject.LOCK) {
            drawables.drawPoint(p, size, color);
        }
    };

    private final DrawStringFcnI DrawStringFcn = (p, s, color, context) -> {
        synchronized (LockObject.LOCK) {
            drawables.drawString(p, s, color);
        }
    };

    private final DrawBoundsFcnI DrawBoundsFcn = (aabb, color, context) -> {
        synchronized (LockObject.LOCK) {
            drawables.drawBounds(aabb, color);
        }
    };

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        box2dAppState = stateManager.getState(Box2dAppState.class);
        PhysicsSpace physicsSpace = box2dAppState.getPhysicsSpace();

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

        applyDrawSettings(debugDraw, box2dAppState.getDrawSettings());
        drawables = new Draw(assetManager);
        
        if (debugGraphics == null) {
            debugGraphics = new StringDebugGraphics(assetManager);
        }
        
        renderManager = rm;
        initialized = true;
    }

    private void applyDrawSettings(b2DebugDraw debugDraw, DrawSettings settings) {
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

        StringBuilder sb = new StringBuilder();
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
        return initialized;
    }

    @Override
    public void preFrame(float tpf) {
        if (box2dAppState == null) {
            return;
        }

        PhysicsSpace physicsSpace = box2dAppState.getPhysicsSpace();
        DrawSettings settings = box2dAppState.getDrawSettings();
        if (settings.isNeedUpdate()) {
            settings.update();
            applyDrawSettings(physicsSpace.getDebugDraw(), box2dAppState.getDrawSettings());
        }
    }
    
    public void updateUI(Node rootNode, Camera camera, int width, int height) {
        if (!initialized) {
            return;
        }        
        synchronized (LockObject.LOCK) {
            drawables.render(debugGraphics,rootNode, camera, width, height);
        }
    }

    @Override
    public void postQueue(RenderQueue rq) {
        synchronized (LockObject.LOCK) {
            drawables.render(renderManager);
        }
    }

    public void flush() {
        if (drawables != null) {
            drawables.flush();
        }
    }

    @Override
    public void postFrame(FrameBuffer out) {

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void setProfiler(AppProfiler profiler) {

    }

    public Draw getDraw() {
        return drawables;
    }
}
