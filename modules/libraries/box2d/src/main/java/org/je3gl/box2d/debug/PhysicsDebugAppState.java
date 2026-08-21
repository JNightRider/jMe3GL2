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
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.je3gl.box2d.Box2dAppState;
import org.je3gl.box2d.DrawSettings;
import org.je3gl.box2d.PhysicsSpace;
import org.je3gl.box2d.debug.batch.BatchSnapshot;
import org.je3gl.box2d.debug.batch.TextData;
import org.je3gl.scene.debug.custom.DebugGraphics;

import org.box2d.jni.b2DebugDraw;
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
import org.box2d.jni.system.ArenaAlloc;

import static org.box2d.jni.b2HexColor.*;
import static org.box2d.jni.include.MathFunctions.*;
import static org.box2d.jni.system.ArenaAlloc.*;
import static org.box2d.jni.system.MemoryUtil.*;

/**
 * Class <code>PhysicsDebugAppState</code> responsible for managing a state for
 * the debugging of the physical forms of the bodies that are added to the world
 * of
 * <b>Box2d</b>.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class PhysicsDebugAppState extends AbstractAppState {

    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(PhysicsDebugAppState.class.getName());

    private Box2dAppState box2dAppState;
    private PhysicsDebugSceneProcessor debugProcessor;

    /**
     * Debugger view.
     */
    protected ViewPort viewPort;
    /** <code>JME3</code> renderer.
     */
    protected RenderManager rm;
    private Application application;

    private Camera camera;

    private Node ui;
    private BitmapTextPool bitmapTextPool = new BitmapTextPool();
    private List<BitmapText> textList = new ArrayList<>();

    private float width;
    private float height;

    //----------------------------------------------------------------------
    //                              CALLBACKS
    //----------------------------------------------------------------------
    private final DrawPolygonFcnI DrawPolygonFcn = (transform, vertices, vertexCount, color, context) -> {
        synchronized (debugProcessor.getLock()) {
            BatchSnapshot snapshot = debugProcessor.getSnapshot().get();
            b2Vec2.Buffer buffer = b2Vec2.createSafe(vertices, vertexCount);
            Vector3f[] vs = new Vector3f[vertexCount];

            int index = 0;
            for (b2Vec2 vec2 : buffer) {
                vs[index++] = new Vector3f(vec2.x(), vec2.y(), 0);
            }
            b2Pos pos = transform.p();
            b2Rot rot = transform.q();
            snapshot.drawPolygon(pos.x().floatValue(), pos.y().floatValue(), b2Rot_GetAngle(rot), color, vs);
        }
    };

    private final DrawSolidPolygonFcnI DrawSolidPolygonFcn = (transform, vertices, vertexCount, radius, color, context) -> {
        synchronized (debugProcessor.getLock()) {
            BatchSnapshot snapshot = debugProcessor.getSnapshot().get();
            b2Vec2.Buffer buffer = b2Vec2.createSafe(vertices, vertexCount);
            Vector3f[] vs = new Vector3f[vertexCount];

            int index = 0;
            for (b2Vec2 vec2 : buffer) {
                vs[index++] = new Vector3f(vec2.x(), vec2.y(), 0);
            }
            b2Pos pos = transform.p();
            b2Rot rot = transform.q();
            snapshot.drawSolidPolygon(pos.x().floatValue(), pos.y().floatValue(), b2Rot_GetAngle(rot), color, vs);
        }
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
        synchronized (debugProcessor.getLock()) {
            try (ArenaAlloc arena = allocPush()) {
                b2Vec2 d = b2SubPos(p1, p2, b2Vec2.calloc(arena));
                float length = b2Length(d);
                if (length < 0.001f) {
                    LOGGER.log(Level.WARNING, "sample app: capsule too short!");
                    return;
                }

                b2Vec2 axis = b2Vec2.calloc(arena).set(d.x() / length, d.y() / length);
                b2Transform transform = b2Transform.calloc(arena);

                transform.p(b2Lerp(b2ToVec2(p1, b2Vec2.calloc(arena)), b2ToVec2(p2, b2Vec2.calloc(arena)), 0.5f, b2Vec2.calloc(arena)));
                transform.q().c(axis.x());
                transform.q().s(axis.y());

                BatchSnapshot snapshot = debugProcessor.getSnapshot().get();
                snapshot.drawCapsule(transform.p().x(), transform.p().y(), b2Rot_GetAngle(transform.q()), radius, length, color);
            }
        }
    };

    private final DrawLineFcnI DrawLineFcn = (p1, p2, color, context) -> {
        synchronized (debugProcessor.getLock()) {
            BatchSnapshot snapshot = debugProcessor.getSnapshot().get();
            snapshot.drawLine(p1.x().floatValue(), p1.y().floatValue(), p2.x().floatValue(), p2.y().floatValue(), color);
        }
    };

    private final DrawTransformFcnI DrawTransformFcn = (transform, context) -> {
        synchronized (debugProcessor.getLock()) {
            try (ArenaAlloc arena = allocPush()) {
                BatchSnapshot snapshot = debugProcessor.getSnapshot().get();
                b2Vec2 p1 = b2ToVec2(transform.p(), b2Vec2.calloc(arena));
                b2Vec2 p2 = b2MulAdd(p1, 1.0f, b2Rot_GetXAxis(transform.q(), b2Vec2.calloc(arena)), b2Vec2.calloc(arena));

                snapshot.drawLineTransform(p1.x(), p1.y(), p2.x(), p2.y(), b2_colorRed);

                p2 = b2MulAdd(p1, 1.0f, b2Rot_GetYAxis(transform.q(), b2Vec2.calloc(arena)), p2);
                snapshot.drawLineTransform(p1.x(), p1.y(), p2.x(), p2.y(), b2_colorGreen);
            }
        }
    };

    private final DrawPointFcnI DrawPointFcn = (p, size, color, context) -> {
        synchronized (debugProcessor.getLock()) {
            BatchSnapshot snapshot = debugProcessor.getSnapshot().get();
            snapshot.drawPoint(p.x().floatValue(), p.y().floatValue(), size, color);
        }
    };

    private final DrawStringFcnI DrawStringFcn = (p, s, color, context) -> {
        synchronized (debugProcessor.getLock()) {
            String str = memUTF(s);
            debugProcessor.addDrawString(p.x().floatValue(), p.y().floatValue(), color, str);
        }
    };

    private final DrawBoundsFcnI DrawBoundsFcn = (aabb, color, context) -> {
        synchronized (debugProcessor.getLock()) {
            try (ArenaAlloc arena = allocPush()) {
                BatchSnapshot snapshot = debugProcessor.getSnapshot().get();
                b2Vec2 lower = aabb.lowerBound();
                b2Vec2 upper = aabb.upperBound();

                b2Vec2 p1 = lower;
                b2Vec2 p2 = b2Vec2.calloc(arena).set(upper.x(), lower.y());
                b2Vec2 p3 = upper;
                b2Vec2 p4 = b2Vec2.calloc(arena).set(lower.x(), upper.y());

                snapshot.drawLineBounds(p1.x(), p1.y(), p2.x(), p2.y(), color);
                snapshot.drawLineBounds(p2.x(), p2.y(), p3.x(), p3.y(), color);
                snapshot.drawLineBounds(p3.x(), p3.y(), p4.x(), p4.y(), color);
                snapshot.drawLineBounds(p4.x(), p4.y(), p1.x(), p1.y(), color);
            }
        }
    };

    public PhysicsDebugAppState() {

    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        AssetManager assetManager   = app.getAssetManager();
        RenderManager renderManager = app.getRenderManager();

        box2dAppState = stateManager.getState(Box2dAppState.class);
        PhysicsSpace physicsSpace = box2dAppState.getPhysicsSpace();
        physicsSpace.setEnableDebugger(true);

        if (bitmapTextPool.getGraphics() == null) {
            bitmapTextPool.setGraphics(new StringDebugGraphics(assetManager));
        }

        application = app;
        camera      = app.getCamera();
        rm          = renderManager;
        ui          = new Node("Box2d-JNI UI");
        ((SimpleApplication) app).getGuiNode().attachChild(ui);

        ViewPort uiViewPort = ((SimpleApplication) app).getGuiViewPort();
        Camera uicam = uiViewPort.getCamera();
        width  = uicam.getWidth();
        height = uicam.getHeight();

        viewPort = renderManager.createMainView("Physics Debug Overlay", app.getCamera());
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

        applyDrawSettings(debugDraw, box2dAppState.getDrawSettings());
        super.initialize(stateManager, app);
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
    public void update(float tpf) {
        PhysicsSpace physicsSpace = box2dAppState.getPhysicsSpace();
        DrawSettings settings = box2dAppState.getDrawSettings();
        if (settings.isNeedUpdate()) {
            settings.update();
            applyDrawSettings(physicsSpace.getDebugDraw(), box2dAppState.getDrawSettings());
        }
        updateGUIText();
    }

    private void updateGUIText() {
        synchronized (debugProcessor.getLock()) {
            List<BitmapText> cache = textList;
            textList = new ArrayList<>();

            for (TextData data : debugProcessor.getListText()) {
                Vector3f pos = camera.getScreenCoordinates(data.getPosition());

                if ((pos.x >= 0 && pos.x <= width) && (pos.y >= 0 && pos.y <= height)) {

                    BitmapText text;
                    if (!cache.isEmpty()) {
                        text = cache.get(0);
                        ui.attachChild(text);

                        cache.remove(0);
                        textList.add(text);
                    } else {
                        text = bitmapTextPool.takePush();
                        textList.add(text);
                        ui.attachChild(text);
                    }
                    text.setText(data.getText());
                    text.setSize(16.5f);
                    text.setColor(data.getColor());
                    text.setLocalTranslation(
                            pos.x - text.getLineWidth() / 2f,
                            pos.y + text.getLineHeight(),
                            pos.z
                    );
                }
            }
            for (BitmapText text : cache) {
                text.removeFromParent();
                bitmapTextPool.takePop(text);
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (isInitialized()) {
            debugProcessor.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        for (BitmapText text : textList) {
            bitmapTextPool.takePop(text);
        }
        textList.clear();
        ui.detachAllChildren();

        viewPort.removeProcessor(debugProcessor);
        rm.removeMainView(viewPort);
    }

    public void setGraphics(DebugGraphics graphics) {
        bitmapTextPool.setGraphics(graphics);
    }

    public DebugGraphics getGraphics() {
        return bitmapTextPool.getGraphics();
    }

    public PhysicsDebugSceneProcessor getDebugProcessor() {
        return debugProcessor;
    }
}
