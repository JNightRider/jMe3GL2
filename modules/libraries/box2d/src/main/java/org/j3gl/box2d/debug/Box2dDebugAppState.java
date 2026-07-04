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
package org.j3gl.box2d.debug;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.box2d.jni.b2DebugDraw;
import org.box2d.jni.b2Vec2;
import org.box2d.jni.b2WorldId;
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
import static org.box2d.jni.include.Box2d.*;
import static org.box2d.jni.include.Id.*;
import static org.box2d.jni.include.Types.*;
import org.box2d.jni.system.ArenaAlloc;
import static org.box2d.jni.system.ArenaAlloc.*;
import org.box2d.jni.system.MemoryUtil;
import static org.box2d.jni.system.MemoryUtil.*;
import org.j3gl.box2d.PhysicsSpace;
import org.j3gl.box2d.util.Converter;
import org.je3gl.scene.debug.Polygon2D;
import org.je3gl.scene.debug.custom.DebugGraphics;

/**
 * Class <code>Box2dDebugAppState</code> responsible for managing a state for
 * the debugging of the physical forms of the bodies that are added to the world
 * of <b>Box2d</b>.
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class Box2dDebugAppState extends BaseAppState {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(Box2dDebugAppState.class.getName());
    
    /** Main application <code>JME</code>. */
    protected Application application;    
    /** Resource manager <code>JME</code>. */
    protected AssetManager assetManager;
    
    // Graphic part.
    /** Physical space of the bodies. */
    private PhysicsSpace physicsSpace;
    
    /**
     * All objects/bodies for the physical shapes to be added in this node out
     * of scene so that it does not interfere with the main root node.
     */
    private Node debugNode;    
    /** Rendering manager. */
    private Graphics2DRenderer renderer; // Debugger
    
    // Physical bodies and joints.    
    /** Map of physical bodies. */
    protected Map<Long, Spatial> bodies = new HashMap<>();
    /** Joint physical map. */
    protected Map<Long, Spatial> joints = new HashMap<>();
    
    /** Debugger view. */
    protected ViewPort viewPort;
    /** <code>JME3</code> renderer. */
    protected RenderManager rm;

    private b2DebugDraw debugDraw;
    
    /**
     * Class constructor <code>Box2dDebugAppState</code> where it asks for the
     * physical space to manage the shapes of the bodies.
     * 
     * @param physicsSpace physical space
     */
    public Box2dDebugAppState(PhysicsSpace physicsSpace) {
        this.physicsSpace = physicsSpace;
    }
     
    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application) 
     * @param app application
     */
    @Override
    @SuppressWarnings("unchecked")
    public void initialize(Application app) {
        rm           = app.getRenderManager();
        assetManager = app.getAssetManager();
        application  = app;
        
        // Initialize the debug scene
        renderer  = new Graphics2DRenderer(this);
        debugNode = new Node("Debug Node");
        
        viewPort  = rm.createMainView("Physics Debug Overlay", app.getCamera());
        viewPort.setClearFlags(false, true, false);
        
        setDebugGraphics(new Box2dDebugGraphics(app.getAssetManager()));
        
        debugNode.setCullHint(Spatial.CullHint.Never);
        
        debugDraw = b2DefaultDebugDraw(b2DebugDraw.malloc());
        debugDraw.DrawBoundsFcn(DrawBoundsFcn)
                 .DrawCircleFcn(DrawCircleFcn)
                 .DrawLineFcn(DrawLineFcn)
                 .DrawPointFcn(DrawPointFcn)
                 .DrawPolygonFcn(DrawPolygonFcn)
                 .DrawSolidCapsuleFcn(DrawSolidCapsuleFcn)
                 .DrawSolidCircleFcn(DrawSolidCircleFcn)
                 .DrawSolidPolygonFcn(DrawSolidPolygonFcn)
                 .DrawTransformFcn(DrawTransformFcn)
                 .DrawStringFcn(DrawStringFcn)
                .drawShapes(true)
                .drawBodyNames(true)
                .drawJoints(true)
                ;
    }
    
    private final DrawPolygonFcnI DrawPolygonFcn = (transform, vertices, vertexCount, color, context) -> {
        System.out.println("DrawPolygonFcnI");        
    };
    
    private final DrawSolidPolygonFcnI DrawSolidPolygonFcn = (transform, vertices, vertexCount, radius, color, context) -> {
        b2Vec2.Buffer buffer = b2Vec2.createSafe(vertices, vertexCount);
        final Vector3f[] vertx = new Vector3f[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            b2Vec2 vec2 = buffer.get(i);
            vertx[i] = Converter.toVector3fValueOfJME3(vec2, physicsSpace.getAxisType());
        }
               
        application.enqueue(() -> debugNode.attachChild(
            renderer.renderPolygon(transform, vertx, color)
        ));
    };
    
    private final DrawCircleFcnI DrawCircleFcn = (center, radius, color, context) -> {
        System.out.println("DrawCircleFcnI");
    };
    
    private final DrawSolidCircleFcnI DrawSolidCircleFcn = (transform, center, radius, color, context) -> {
        System.out.println("DrawSolidCircleFcnI");
    };
    
    private final DrawSolidCapsuleFcnI DrawSolidCapsuleFcn = (p1, p2, radius, color, context) -> {
        System.out.println("DrawSolidCapsuleFcnI");
    };
    
    private final DrawLineFcnI DrawLineFcn = (p1, p2, color, context) -> {
        System.out.println("DrawLineFcnI");
    };
    
    private final DrawTransformFcnI DrawTransformFcn = (transform, context) -> {
        System.out.println("DrawTransformFcnI");
    };
    
    private final DrawPointFcnI DrawPointFcn = (p, size, color, context) -> {
        System.out.println("DrawPointFcnI");
    };
    
    private final DrawStringFcnI DrawStringFcn = (p, s, color, context) -> {
        
        application.enqueue(() -> debugNode.attachChild(
            renderer.renderString(p, memUTF(s), color)
        ));
    };
    
    private final DrawBoundsFcnI DrawBoundsFcn = (aabb, color, context) -> {
        System.out.println("DrawBoundsFcnI");
    };
    
    /**
     * Sets debug graphics (color manager).
     * @param graphics object
     */
    public void setDebugGraphics(DebugGraphics graphics) {
        if (!isInitialized()) {
            LOGGER.log(Level.WARNING, "Initialize debugging first to set a color palette");
            return;
        }
        renderer.setDebugGraphics(graphics);
        //renderer.printInformation();
    }

    /** (non-Javadoc) */
    @Override
    protected void onEnable() {
        if (viewPort != null) {
            viewPort.attachScene(debugNode);
        }
    }
    /**(non-Javadoc) */
    @Override
    protected void onDisable() {
        if (viewPort != null) {
            viewPort.detachScene(debugNode);
        }
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#cleanup() 
     * @param app application
     */
    @Override
    protected void cleanup(Application app) {
        debugNode.detachAllChildren();
        rm.removeMainView(viewPort);
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#update(float) 
     * @param tpf float
     */
    @Override
    public void update(float tpf) {
        renderer.renderFree();
        
        // Update debug root node
        debugNode.updateLogicalState(tpf);
        debugNode.updateGeometricState();
        
        b2WorldId worldId = physicsSpace.getWorldId();
        if (B2_IS_NON_NULL(worldId)) {
            b2World_Draw(worldId, debugDraw);
        }
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#render(com.jme3.renderer.RenderManager) 
     * @param rm render
     */
    @Override
    public void render(RenderManager rm) {
        if (this.viewPort != null) {
            rm.renderScene(this.debugNode, this.viewPort);
        }
    }
    
    /**
     * Return physical space.
     * @return object
     */
    public PhysicsSpace getPhysicsSpace() {
        return physicsSpace;
    }
    
    /**
     * Returns the 2D graphics renderer.
     * @return object
     */
    public Graphics2DRenderer getGraphics2DRenderer() {
        return renderer;
    }
}
