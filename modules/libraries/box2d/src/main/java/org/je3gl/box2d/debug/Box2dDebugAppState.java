///*
//BSD 3-Clause License
//
//Copyright (c) 2023-2026, Night Rider (Wilson)
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//1. Redistributions of source code must retain the above copyright notice, this
//   list of conditions and the following disclaimer.
//
//2. Redistributions in binary form must reproduce the above copyright notice,
//   this list of conditions and the following disclaimer in the documentation
//   and/or other materials provided with the distribution.
//
//3. Neither the name of the copyright holder nor the names of its
//   contributors may be used to endorse or promote products derived from
//   this software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
//IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
//FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
//DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
//SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
//CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
//OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
//OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//*/
//package org.je3gl.box2d.debug;
//
//import com.jme3.app.Application;
//import com.jme3.app.state.BaseAppState;
//import com.jme3.asset.AssetManager;
//import com.jme3.math.Vector3f;
//import com.jme3.renderer.RenderManager;
//import com.jme3.renderer.ViewPort;
//import com.jme3.scene.Node;
//import com.jme3.scene.Spatial;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import org.box2d.jni.b2DebugDraw;
//import org.box2d.jni.b2Vec2;
//import org.box2d.jni.draw.DrawBoundsFcnI;
//import org.box2d.jni.draw.DrawCircleFcnI;
//import org.box2d.jni.draw.DrawLineFcnI;
//import org.box2d.jni.draw.DrawPointFcnI;
//import org.box2d.jni.draw.DrawPolygonFcnI;
//import org.box2d.jni.draw.DrawSolidCapsuleFcnI;
//import org.box2d.jni.draw.DrawSolidCircleFcnI;
//import org.box2d.jni.draw.DrawSolidPolygonFcnI;
//import org.box2d.jni.draw.DrawStringFcnI;
//import org.box2d.jni.draw.DrawTransformFcnI;
//
//import org.je3gl.box2d.PhysicsSpace;
//import org.je3gl.box2d.util.Converter;
//import org.je3gl.box2d.DrawSettings;
//
//import org.je3gl.scene.debug.custom.DebugGraphics;
//
//import static org.box2d.jni.include.Types.*;
//import static org.box2d.jni.system.MemoryUtil.*;
//import org.je3gl.box2d.debug.data.DrawData;
//import org.je3gl.box2d.debug.data.LineData;
//import org.je3gl.box2d.debug.data.PointData;
//import org.je3gl.box2d.debug.data.PolygonData;
//import org.je3gl.box2d.util.ComparatorUtils;
//import org.je3gl.box2d.util.StackUtils;
//
///**
// * Class <code>Box2dDebugAppState</code> responsible for managing a state for
// * the debugging of the physical forms of the bodies that are added to the world
// * of <b>Box2d</b>.
// * 
// * @author wil
// * @version 1.0.0
// * @since 3.2.0
// */
//public class Box2dDebugAppState extends BaseAppState {
//    /** Class logger. */
//    private static final Logger LOGGER = Logger.getLogger(Box2dDebugAppState.class.getName());
//    
//    /** Main application <code>JME</code>. */
//    protected Application application;    
//    /** Resource manager <code>JME</code>. */
//    protected AssetManager assetManager;
//    
//    // Graphic part.
//    /** Physical space of the bodies. */
//    private PhysicsSpace physicsSpace;
//    
//    /**
//     * All objects/bodies for the physical shapes to be added in this node out
//     * of scene so that it does not interfere with the main root node.
//     */
//    private Node debugNode;    
//    /** Rendering manager. */
//    private Graphics2DRenderer renderer; // Debugger
//    
//    /** Debugger view. */
//    protected ViewPort viewPort;
//    /** <code>JME3</code> renderer. */
//    protected RenderManager rm;
//    
//    private List<DrawData> drawDataList = new ArrayList<>();
//
//    private final Object lock = new Object();
//    
//    /**
//     * List that temporarily stores matrices of vectors.
//     */
//    private List<Vector3f[]> cache = Collections.synchronizedList(new ArrayList<>());
//    /** Vector handler (Object-pool). */
//    private Vector3fPool vector3fPool;
//    /** Object indicating which drawings are performed in the debugger. */
//    private b2DebugDraw debugDraw;
//    /** Debugger settings. */
//    private DrawSettings settings;
//
//    /**
//     * An atomic flag indicating whether this state has been initialized
//     * (debugger).
//     */
//    private AtomicBoolean initialized = new AtomicBoolean(false);
//
//    /**
//     * Class constructor <code>Box2dDebugAppState</code> where it asks for the
//     * physical space to manage the shapes of the bodies.
//     * 
//     * @param physicsSpace physical space
//     * @param settings Debugger settings.
//     */
//    public Box2dDebugAppState(PhysicsSpace physicsSpace, DrawSettings settings) {
//        this.physicsSpace = physicsSpace;
//        this.settings     = settings;
//        this.vector3fPool = new Vector3fPool();
//        this.settings     = new DrawSettings();
//        this.startDebugPhysics();
//    }
//
//    /**
//     * Initializes the Box2D drawing object.
//     */
//    private void startDebugPhysics() {
//        debugDraw = b2DefaultDebugDraw(b2DebugDraw.malloc());
//        debugDraw.DrawBoundsFcn(DrawBoundsFcn)
//                .DrawCircleFcn(DrawCircleFcn)
//                .DrawLineFcn(DrawLineFcn)
//                .DrawPointFcn(DrawPointFcn)
//                .DrawPolygonFcn(DrawPolygonFcn)
//                .DrawSolidCapsuleFcn(DrawSolidCapsuleFcn)
//                .DrawSolidCircleFcn(DrawSolidCircleFcn)
//                .DrawSolidPolygonFcn(DrawSolidPolygonFcn)
//                .DrawTransformFcn(DrawTransformFcn)
//                .DrawStringFcn(DrawStringFcn);
//        this.updateDrawFlags();
//        this.physicsSpace.setDebugDraw(debugDraw);
//    }
//
//    /**
//     * Configure the debug flags to control which objects are drawn.
//     */
//    private void updateDrawFlags() {
//        StringBuilder sb = new StringBuilder();
//        debugDraw.drawShapes(settings.drawShapes())
//                .drawBodyNames(settings.drawBodyNames())
//                .drawJoints(settings.drawJoints())
//                .drawAnchorA(settings.drawAnchorA())
//                .drawChainNormals(settings.drawChainNormals())
//                .drawContactFeatures(settings.drawContactFeatures())
//                .drawFrictionForces(settings.drawFrictionForces())
//                .drawContactNormals(settings.drawContactNormals())
//                .drawContacts(settings.drawContacts())
//                .drawGraphColors(settings.drawGraphColors())
//                .drawIslands(settings.drawIslands())
//                .drawJointExtras(settings.drawJointExtras())
//                .drawMass(settings.drawMass())
//                .drawBounds(settings.drawBounds());
//
//        sb.append("[jMe3GL2] :Charts for debugging Box2d-JNI bodies")
//                .append('\n').append("drawShapes: ").append(settings.drawShapes())
//                .append('\n').append("drawBodyNames: ").append(settings.drawBodyNames())
//                .append('\n').append("drawJoints: ").append(settings.drawJoints())
//                .append('\n').append("drawAnchorA: ").append(settings.drawAnchorA())
//                .append('\n').append("drawChainNormals: ").append(settings.drawChainNormals())
//                .append('\n').append("drawContactFeatures: ").append(settings.drawContactFeatures())
//                .append('\n').append("drawFrictionForces: ").append(settings.drawFrictionForces())
//                .append('\n').append("drawContactNormals: ").append(settings.drawContactNormals())
//                .append('\n').append("drawContacts: ").append(settings.drawContacts())
//                .append('\n').append("drawGraphColors: ").append(settings.drawGraphColors())
//                .append('\n').append("drawIslands: ").append(settings.drawIslands())
//                .append('\n').append("drawJointExtras: ").append(settings.drawJointExtras())
//                .append('\n').append("drawMass: ").append(settings.drawMass())
//                .append('\n').append("drawBounds: ").append(settings.drawBounds());
//        LOGGER.info(String.valueOf(sb));
//    }
//
//    /**
//     * (non-Javadoc)
//     * @see com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application) 
//     * @param app application
//     */
//    @Override
//    @SuppressWarnings("unchecked")
//    public void initialize(Application app) {
//        rm           = app.getRenderManager();
//        assetManager = app.getAssetManager();
//        application  = app;
//        
//        // Initialize the debug scene
//        renderer  = new Graphics2DRenderer(null);
//        debugNode = new Node("Debug Node");
//        
//        viewPort  = rm.createMainView("Physics Debug Overlay", app.getCamera());
//        viewPort.setClearFlags(false, true, true);
//        
//        setDebugGraphics(new StringDebugGraphics(app.getAssetManager()));        
//        debugNode.setCullHint(Spatial.CullHint.Never);
//        initialized.set(true);
//    }
//
//    //----------------------------------------------------------------------
//    //                              CALLBACKS
//    //----------------------------------------------------------------------
//    
//    private final DrawPolygonFcnI DrawPolygonFcn = (transform, vertices, vertexCount, color, context) -> {
////        synchronized (lock) {
////            try (StackUtils stack = StackUtils.get(); transform) {
////                PolygonData data = stack.allocPolygon(vertexCount);
////
////                b2Vec2.Buffer buffer = b2Vec2.createSafe(vertices, vertexCount);
////                final Vector3f[] vertx = vector3fPool.size(vertexCount)
////                        .takePush();
////
////                for (int i = 0; i < vertexCount; i++) {
////                    b2Vec2 vec2 = buffer.get(i);
////                    Converter.toVector3f(vec2, physicsSpace.getAxisType(), vertx[i]);
////                }
////                
////                data.update(transform, vertx, color, color, false);
////                int index = drawDataList.indexOf(data);
////                
////                PolygonData buff;
////                if (index == -1) {
////                    buff = new PolygonData(vertexCount);
////                    buff.update(transform, vertx, color, color, false);
////                    buff.setDraw(renderer.renderPolygon(transform, buff.getVertices(), color, false));
////                    drawDataList.add(buff);
////                    
////                } else {
////                    buff = (PolygonData) drawDataList.get(index);
////                    buff.update(transform, vertx, color, color, false);
////                }
////                vector3fPool.takePop(vertx);
////            }
////        }
//    };
//    
//    private final DrawSolidPolygonFcnI DrawSolidPolygonFcn = (transform, vertices, vertexCount, radius, color, context) -> {
////        synchronized (lock) {
////            try (StackUtils stack = StackUtils.get(); transform) {
////                PolygonData data = stack.allocPolygon(vertexCount);
////
////                b2Vec2.Buffer buffer = b2Vec2.createSafe(vertices, vertexCount);
////                final Vector3f[] vertx = vector3fPool.size(vertexCount)
////                        .takePush();
////
////                for (int i = 0; i < vertexCount; i++) {
////                    b2Vec2 vec2 = buffer.get(i);
////                    Converter.toVector3f(vec2, physicsSpace.getAxisType(), vertx[i]);
////                }
////                
////                data.update(transform, vertx, color, color, true);
////                int index = drawDataList.indexOf(data);
////                
////                PolygonData buff;
////                if (index == -1) {
////                    buff = new PolygonData(vertexCount);
////                    buff.update(transform, vertx, color, color, true);
////                    buff.setDraw(renderer.renderPolygon(transform, buff.getVertices(), color, true));
////                    drawDataList.add(buff);
////                    
////                } else {
////                    buff = (PolygonData) drawDataList.get(index);
////                    buff.update(transform, vertx, color, color, true);
////                }
////                vector3fPool.takePop(vertx);
////            }
////        }
//    };
//    
//    private final DrawCircleFcnI DrawCircleFcn = (center, radius, color, context) -> {
//        if (!initialized.get()) {
//            return;
//        }
//        
////        application.enqueue(() -> debugNode.attachChild(
////            renderer.renderCircle(null, center, radius, color, false)
////        ));
//    };
//    
//    private final DrawSolidCircleFcnI DrawSolidCircleFcn = (transform, center, radius, color, context) -> {
//        if (!initialized.get()) {
//            return;
//        }
////        application.enqueue(() -> debugNode.attachChild(
////            renderer.renderCircle(transform, center, radius, color, true)
////        ));
//    };
//    
//    private final DrawSolidCapsuleFcnI DrawSolidCapsuleFcn = (p1, p2, radius, color, context) -> {
//        if (!initialized.get()) {
//            return;
//        }
////        application.enqueue(() -> debugNode.attachChild(
////            renderer.renderCapsule(p1, p2, radius, color, true)
////        ));
//    };
//    
//    private final DrawLineFcnI DrawLineFcn = (p1, p2, color, context) -> {
////        synchronized (lock) {
////            try (StackUtils stack = StackUtils.get(); p1; p2) {
////                LineData data = stack.allocLine();
////                data.update(p1, p2, color);
////
////                int index = drawDataList.indexOf(data);
////                LineData buff;
////                if (index == -1) {
////                    buff = data.clone();
////                    buff.setDraw(renderer.renderLine(p1, p2, color));
////                    drawDataList.add(buff);
////                } else {
////                    buff = (LineData) drawDataList.get(index);
////                    buff.update(p1, p2, color);
////                }
////            }
////        }
//    };
//
//    private final DrawTransformFcnI DrawTransformFcn = (transform, context) -> {
//        if (!initialized.get()) {
//            return;
//        }
////        application.enqueue(() -> debugNode.attachChild(
////            renderer.renderTransform(transform, 1.0f)
////        ));
//    };
//    
//    private final DrawPointFcnI DrawPointFcn = (p, size, color, context) -> {
//        synchronized (lock) {
//            try (StackUtils stack = StackUtils.get(); p) {
//                PointData data = stack.allocPoint();
//                data.update(p, size, color);
//
//                int index = drawDataList.indexOf(data);
//                PointData buff;
//                if (index == -1) {
//                    buff = data.clone();
//                    buff.setDraw(renderer.renderPoint(p, size, color));
//                    drawDataList.add(buff);
//                } else {
//                    buff = (PointData) drawDataList.get(index);
//                    buff.update(p, size, color);
//                }
//            }
//        }
//    };
//    
//    private final DrawStringFcnI DrawStringFcn = (p, s, color, context) -> {
//        if (!initialized.get()) {
//            return;
//        }
////        String value = memUTF(s);
////        if (value == null || value.trim().isEmpty()) {
////            return;
////        }
////        
////        application.enqueue(() -> debugNode.attachChild(
////            renderer.renderString(p, value, color)
////        ));
//    };
//    
//    private final DrawBoundsFcnI DrawBoundsFcn = (aabb, color, context) -> {
//        if (!initialized.get()) {
//            return;
//        }
////        application.enqueue(() -> debugNode.attachChild(
////            renderer.renderBounds(aabb, color)
////        ));
//    };
//    
//    /**
//     * Sets debug graphics (color manager).
//     * @param graphics object
//     */
//    public void setDebugGraphics(DebugGraphics graphics) {
//        if (!isInitialized()) {
//            LOGGER.log(Level.WARNING, "Initialize debugging first to set a color palette");
//            return;
//        }
//        renderer.setDebugGraphics(graphics);
//    }
//
//    /** (non-Javadoc) */
//    @Override
//    protected void onEnable() {
//        if (viewPort != null) {
//            viewPort.attachScene(debugNode);
//        }
//    }
//    /**(non-Javadoc) */
//    @Override
//    protected void onDisable() {
//        if (viewPort != null) {
//            viewPort.detachScene(debugNode);
//        }
//    }
//    
//    /**
//     * (non-Javadoc)
//     * @see com.jme3.app.state.AbstractAppState#cleanup() 
//     * @param app application
//     */
//    @Override
//    protected void cleanup(Application app) {
//        debugNode.detachAllChildren();
//        rm.removeMainView(viewPort);
//        debugDraw.close();
//    }
//    
//    /**
//     * (non-Javadoc)
//     * @see com.jme3.app.state.AbstractAppState#update(float) 
//     * @param tpf float
//     */
//    @Override
//    public void update(float tpf) {
//        drawLineNode();
//
//        if (settings.isNeedUpdate()) {
//            settings.update();
//            updateDrawFlags();
//        }
//        
//        // Update debug root node
//        debugNode.updateLogicalState(tpf);
//        debugNode.updateGeometricState();
////        
////        for (Vector3f[] v : cache) {
////            vector3fPool.takePop(v);
////        }
////        cache.clear();
//    }
//
//    private void drawLineNode() {
//        synchronized (lock) {
//            Iterator<DrawData> it = drawDataList.iterator();
//            while (it.hasNext()) {
//                DrawData next = it.next();
//                Spatial object = next.getDraw();
//
//                if (next.isNeedsUpdating()) {
//                    next.update();
//                }
//                if (next.isLivingDrawing()) {
//                    if (!debugNode.hasChild(object)) {
//                        debugNode.attachChild(object);
//                    }
//                } else {
//                    it.remove();
//                    debugNode.detachChild(object);
//                }
//                next.kill();
//            }
////            renderer.renderFree();
//        }
//
//    }
//
//    /**
//     * (non-Javadoc)
//     * @see com.jme3.app.state.AbstractAppState#render(com.jme3.renderer.RenderManager) 
//     * @param rm render
//     */
//    @Override
//    public void render(RenderManager rm) {
//        if (this.viewPort != null) {
//            rm.renderScene(this.debugNode, this.viewPort);
//        }
//    }
//    
//    /**
//     * Return physical space.
//     * @return object
//     */
//    public PhysicsSpace getPhysicsSpace() {
//        return physicsSpace;
//    }
//    
//    /**
//     * Returns the 2D graphics renderer.
//     * @return object
//     */
//    public Graphics2DRenderer getGraphics2DRenderer() {
//        return renderer;
//    }
//}
