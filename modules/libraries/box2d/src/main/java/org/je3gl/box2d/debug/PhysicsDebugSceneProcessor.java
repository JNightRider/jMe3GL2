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
 * Class responsible for processing physical shapes from the Box2D debugger for
 * visualization on screen.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public final class PhysicsDebugSceneProcessor implements SceneProcessor {
    /** Class logger */
    private static final Logger LOGGER = Logger.getLogger(PhysicsDebugSceneProcessor.class.getName());

    /** AssetManager */
    private final AssetManager assetManager;
    /** AppStateManager */
    private final AppStateManager stateManager;

    /**
     * Box2dAppState
     *
     * @see Box2dAppState
     */
    private Box2dAppState box2dAppState;

    /**
     * The object responsible for managing the batch of drawables.
     */
    private Draw drawables;
    /** RenderManager */
    private RenderManager renderManager;
    /** DebugGraphics */
    private DebugGraphics debugGraphics;

    /**
     * A flag that is responsible for determining whether this processed post
     * has been initialized or not.
     */
    private boolean initialized = false;

    /**
     * A flag that indicates whether this post-processing is enabled or not.
     */
    private boolean enabled = true;

    /**
     * Constructor.
     *
     * @param assetManager AssetManager
     * @param stateManager AppStateManager
     */
    public PhysicsDebugSceneProcessor(AssetManager assetManager, AppStateManager stateManager) {
        this.assetManager = assetManager;
        this.stateManager = stateManager;
    }

    //----------------------------------------------------------------------
    //                              CALLBACKS
    //----------------------------------------------------------------------

    /**
     * {@code DrawPolygonFcnI}
     */
    private final DrawPolygonFcnI DrawPolygonFcn = (transform, vertices, vertexCount, color, context) -> {
        if (checkProcess()) {
            synchronized (LockObject.LOCK) {
                drawables.drawPolygon(transform, vertices, vertexCount, color);
            }
        }
    };

    /**
     * {@code DrawSolidPolygonFcnI}
     */
    private final DrawSolidPolygonFcnI DrawSolidPolygonFcn = (transform, vertices, vertexCount, radius, color, context) -> {
        if (checkProcess()) {
            synchronized (LockObject.LOCK) {
                drawables.drawSolidPolygon(transform, vertices, vertexCount, radius, color);
            }
        }
    };

    /**
     * {@code DrawCircleFcnI}
     */
    private final DrawCircleFcnI DrawCircleFcn = (center, radius, color, context) -> {
        if (checkProcess()) {
            synchronized (LockObject.LOCK) {
                drawables.drawCircle(center, radius, color);
            }
        }
    };

    /**
     * {@code DrawSolidCircleFcnI}
     */
    private final DrawSolidCircleFcnI DrawSolidCircleFcn = (transform, center, radius, color, context) -> {
        if (checkProcess()) {
            synchronized (LockObject.LOCK) {
                drawables.drawSolidCircle(transform, center, radius, color);
            }
        }
    };

    /**
     * {@code DrawSolidCapsuleFcnI}
     */
    private final DrawSolidCapsuleFcnI DrawSolidCapsuleFcn = (p1, p2, radius, color, context) -> {
        if (checkProcess()) {
            synchronized (LockObject.LOCK) {
                drawables.drawCapsule(p1, p2, radius, color);
            }
        }
    };

    /**
     * {@code DrawLineFcnI}
     */
    private final DrawLineFcnI DrawLineFcn = (p1, p2, color, context) -> {
        if (checkProcess()) {
            synchronized (LockObject.LOCK) {
                drawables.drawLine(p1, p2, color);
            }
        }
    };

    /**
     * {@code DrawTransformFcnI}
     */
    private final DrawTransformFcnI DrawTransformFcn = (transform, context) -> {
        if (checkProcess()) {
            synchronized (LockObject.LOCK) {
                drawables.drawTransform(transform, 1.0f);
            }
        }
    };

    /**
     * {@code DrawPointFcnI}
     */
    private final DrawPointFcnI DrawPointFcn = (p, size, color, context) -> {
        if (checkProcess()) {
            synchronized (LockObject.LOCK) {
                drawables.drawPoint(p, size, color);
            }
        }
    };

    /**
     * {@code DrawStringFcnI}
     */
    private final DrawStringFcnI DrawStringFcn = (p, s, color, context) -> {
        if (checkProcess()) {
            synchronized (LockObject.LOCK) {
                drawables.drawString(p, s, color);
            }
        }
    };

    /**
     * {@code DrawBoundsFcnI}
     */
    private final DrawBoundsFcnI DrawBoundsFcn = (aabb, color, context) -> {
        if (checkProcess()) {
            synchronized (LockObject.LOCK) {
                drawables.drawBounds(aabb, color);
            }
        }
    };

    /**
     * (non-Javadoc)
     *
     * @param rm RenderManager
     * @param vp ViewPort
     */
    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        box2dAppState = stateManager.getState(Box2dAppState.class);
        if (box2dAppState == null) {
            throw new NullPointerException("Box2dAppState");
        }

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

    /**
     * Apply the settings to the Box2D debugger.
     *
     * @param debugDraw b2DebugDraw
     * @param settings DrawSettings
     */
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

    /**
     * (non-Javadoc)
     *
     * @param vp ViewPort
     * @param w int
     * @param h int
     */
    @Override
    public void reshape(ViewPort vp, int w, int h) {

    }

    /**
     * (non-Javadoc)
     *
     * @return 
     */
    @Override
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * (non-Javadoc)
     *
     * @param tpf float
     */
    @Override
    public void preFrame(float tpf) {
        if (! checkProcess()) {
            return;
        }

        PhysicsSpace physicsSpace = box2dAppState.getPhysicsSpace();
        DrawSettings settings = box2dAppState.getDrawSettings();
        if (settings.isNeedUpdate()) {
            settings.update();
            applyDrawSettings(physicsSpace.getDebugDraw(), box2dAppState.getDrawSettings());
        }
    }

    /**
     * Updates the text batch data; this method should only be invoked when it
     * is necessary to draw or add text to the screen within a user interface
     * (UI) node.
     *
     * @param rootNode Node
     * @param camera Camera
     * @param width int
     * @param height int
     */
    public void updateUI(Node rootNode, Camera camera, int width, int height) {
        if (! checkProcess()) {
            return;
        }
        synchronized (LockObject.LOCK) {
            drawables.render(debugGraphics, rootNode, camera, width, height);
        }
    }

    /**
     * Verify that the process is valid to proceed.
     *
     * @return boolean
     */
    private boolean checkProcess() {
        return enabled && initialized
                && box2dAppState != null && box2dAppState.isInitialized();
    }

    /**
     * (non-Javadoc)
     *
     * @param rq RenderQueue
     */
    @Override
    public void postQueue(RenderQueue rq) {
        synchronized (LockObject.LOCK) {
            drawables.render(renderManager);
        }
    }

    /**
     * Clear the canvas (drawing space or data batches)
     */
    public void flush() {
        if (drawables != null) {
            drawables.flush();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @param out FrameBuffer
     */
    @Override
    public void postFrame(FrameBuffer out) {

    }

    /**
     * (non-Javadoc)
     */
    @Override
    public void cleanup() {
        synchronized (LockObject.LOCK) {
            flush();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @param profiler AppProfiler
     */
    @Override
    public void setProfiler(AppProfiler profiler) {

    }

    /**
     * Enable or disable this post-processing
     *
     * @param enabled boolean
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the enabled state flag
     *
     * @return boolean
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns the batch of drawables
     *
     * @return Draw
     */
    public Draw getDraw() {
        return drawables;
    }
}
