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

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapText;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.util.TempVars;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.box2d.jni.b2AABB;
import org.box2d.jni.b2Pos;
import org.box2d.jni.b2Transform;
import org.box2d.jni.b2Vec2;
import org.box2d.jni.b2WorldTransform;
import org.box2d.jni.system.ArenaAlloc;

import static org.box2d.jni.b2HexColor.*;
import static org.box2d.jni.system.ArenaAlloc.*;
import static org.box2d.jni.include.MathFunctions.*;

import org.je3gl.box2d.AxisType;
import org.je3gl.box2d.debug.renderer.MeshRender;
import org.je3gl.box2d.debug.renderer.ShapeRenderManager;
import org.je3gl.box2d.util.Converter;

import org.je3gl.scene.debug.custom.DebugGraphics;
import org.je3gl.utilities.ColorUtilities;

/**
 * An object of the class <code>Graphics2DRenderer</code> is in charge of
 * rendering physical bodies, i.e. it is in charge of finding a form for it.
 * <p>
 * Class in charge of managing the colors, materials and shapes of a physical
 * body to debug it in real time.
 * </p>
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class Graphics2DRenderer {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(Graphics2DRenderer.class.getName());
    
    private static final Node NODE_NULL = new Node("NULL");
    
    /** Resource manager <code>JME</code>. */
    private final AssetManager assetManager;    
    /** Debugger. */
    private final Box2dDebugAppState box2dDebugAppState;
    
    private final ShapeRenderManager shapeRenderManager;
    private final Vector3fPool vector3fPool;
    private final BitmapTextPool bitmapTextPool;
    
    private final List<Object> cache;
    
    private final Vector2f origin = new Vector2f();
    

    /**
     * Class constructor <code>Graphics2DRenderer</code>.
     * @param box2dDebugAppState debugger
     */
    public Graphics2DRenderer(Box2dDebugAppState box2dDebugAppState) {
        this.assetManager = box2dDebugAppState.getApplication().getAssetManager();
        this.box2dDebugAppState = box2dDebugAppState;
        this.bitmapTextPool = new BitmapTextPool();
        this.vector3fPool = new Vector3fPool();
        this.shapeRenderManager = new ShapeRenderManager(assetManager);
        this.cache = new ArrayList<>();
    }

    /**
     * Returns the debug graphs.
     * @return object
     */
    public DebugGraphics getDebugGraphics() {
        return bitmapTextPool.getGraphics();
    }

    /**
     * Set debug graphs
     * @param debugGraphics object
     */
    void setDebugGraphics(DebugGraphics debugGraphics) {
        this.bitmapTextPool.setGraphics(debugGraphics);
    }
    
    public Node renderString(b2Pos pos, String txt, int color) {
        BitmapText text = bitmapTextPool.takePush();
        text.setText(txt);
        text.setColor(ColorUtilities.fromIntRGBA(color, 1.0f));
        text.setLocalTranslation(Converter.toVector3fValueOfJME3(pos, box2dDebugAppState.getPhysicsSpace().getAxisType()));
        text.setQueueBucket(RenderQueue.Bucket.Translucent);
        
        text.move(-(text.getLineWidth() / 2.0f), 0, 0);
        cache.add(text);
        return text;
    }

    /**
     * Method in charge of rendering the physical form to a graphic object.
     *
     * @param transforms the transformation of the body
     * @param vertices physical shape
     * @param color color for physical shape
     * @param solid solid polygons
     * @return generated graphical object
     */
    public Node renderPolygon(b2WorldTransform transforms, Vector3f[] vertices, int color, boolean solid) {
        TempVars vars = TempVars.get();
        Node node = shapeRenderManager.render(MeshRender.POLYGON, vertices, color, solid);
        
        AxisType axisType = box2dDebugAppState.getPhysicsSpace().getAxisType(); 
        float angle = b2Rot_GetAngle(transforms.q());

        node.setLocalTranslation(Converter.toVector3fValueOfJME3(transforms.p(), axisType));
        node.setLocalRotation(vars.quat1.fromAngleAxis(angle, Converter.toUNIT3f(axisType)));
        
        vars.release();
        return node;
    }
    
    public Node renderCircle(b2Pos center, float radius, int color, boolean solid) {
        Node node = shapeRenderManager.render(MeshRender.CIRCLE, radius, color, solid);        
        AxisType axisType = box2dDebugAppState.getPhysicsSpace().getAxisType();
        node.setLocalTranslation(Converter.toVector3fValueOfJME3(center, axisType));
        return node;
    }
    
    public Node renderCapsule(b2Pos p1, b2Pos p2, float radius, int color, boolean solid) {
        TempVars vars = TempVars.get();
        Vector3f d   = vars.vect1;
        Vector3f pos = vars.vect2;
        
        Vector2f rect = vars.vect2d;
        Vector2f axis = vars.vect2d2;
        
        d.set(p2.x().floatValue() - p1.x().floatValue(), p2.y().floatValue() - p1.y().floatValue(), 0f);
        
        float length = d.length();
        if (length < 0.001f) {
            LOGGER.log(Level.WARNING, "debug app: capsule too short!");
            return NODE_NULL;
        }

        rect.setX(radius * 2);
        rect.setY(length + (radius * 2));

        axis.set(d.x / length, d.y / length);
        pos.set((p1.x().floatValue() + p2.x().floatValue()) * 0.5f, 
                (p1.y().floatValue() + p2.y().floatValue()) * 0.5f, 0f);
        
        AxisType axisType = box2dDebugAppState.getPhysicsSpace().getAxisType(); 
        float angle = FastMath.atan2(axis.y, axis.x) + FastMath.HALF_PI;
        
        Node node = shapeRenderManager.render(MeshRender.CAPSULE, rect, color, solid);
        node.setLocalTranslation(pos);
        node.setLocalRotation(vars.quat1.fromAngleAxis(angle, Converter.toUNIT3f(axisType)));
        
        vars.release();
        return node;
    }
    
    public Node renderLine(b2Pos start, b2Pos end, int color) {
        TempVars vars = TempVars.get();
        Vector3f[] points = vars.tri;
        points[0].set(start.x().floatValue(), start.y().floatValue(), 0f);
        points[1].set(end.x().floatValue(), end.y().floatValue(), 0f);
        
        Node node = shapeRenderManager.render(MeshRender.LINE, points, color, false);        
        vars.release();
        return node;
    }
    
    public Node renderTransform(b2WorldTransform transform, float scale) {
        try (ArenaAlloc alloc = allocPush()) {
            Node rootNode    = shapeRenderManager.create();
            Vector3f[] buff1 = vector3fPool.size(2).takePush();
            Vector3f[] buff2 = vector3fPool.size(2).takePush();
            cache.add(buff1);
            cache.add(buff2);
            
            b2Transform xf = b2ToRelativeTransform( transform, b2Pos.ncalloc(alloc).set(origin.x, origin.y), b2Transform.calloc(alloc) );
            b2Vec2 p1 = xf.p();
            
            b2Vec2 p2 = b2MulAdd( p1, scale, b2Rot_GetXAxis( xf.q(), b2Vec2.calloc(alloc) ), b2Vec2.calloc(alloc) );
            
            AxisType axisType = box2dDebugAppState.getPhysicsSpace().getAxisType(); 
            Converter.toVector3fValueOfJME3(p1, axisType, buff1[0]);
            Converter.toVector3fValueOfJME3(p2, axisType, buff1[1]);
            rootNode.attachChild(shapeRenderManager.render(MeshRender.LINE, buff1, b2_colorRed, false));
            
            p2 = b2MulAdd( p1, scale, b2Rot_GetYAxis( xf.q(), b2Vec2.calloc(alloc) ), b2Vec2.calloc(alloc) );
            
            Converter.toVector3fValueOfJME3(p1, axisType, buff2[0]);
            Converter.toVector3fValueOfJME3(p2, axisType, buff2[1]);
            rootNode.attachChild(shapeRenderManager.render(MeshRender.LINE, buff2, b2_colorGreen, false));
            return rootNode;
        }
    }
    
    public Node renderPoint(b2Pos p, float size, int color) {
        TempVars vars = TempVars.get();
        Vector4f vec4 = vars.vect4f1;
        Vector3f vec3 = vars.vect1;
        
        AxisType axisType = box2dDebugAppState.getPhysicsSpace().getAxisType(); 
        Converter.toVector3fValueOfJME3(p, axisType, vec3);
        
        vec4.set(vec3.x, vec3.y, vec3.z, size * 1.5f);
        
        Node node = shapeRenderManager.render(MeshRender.POINT, vec4, color, false);
        vars.release();
        return node;
    }
    
    public Node renderBounds(b2AABB aabb, int color) {
        try (ArenaAlloc alloc = allocPush()) {
            b2Pos norigin = b2Pos.ncalloc(alloc).set(origin.x, origin.y);
            
            b2Vec2 lower = b2SubPos( b2ToPos( aabb.lowerBound(), b2Pos.ncalloc(alloc) ), norigin, b2Vec2.calloc(alloc) );
            b2Vec2 upper = b2SubPos( b2ToPos( aabb.upperBound(), b2Pos.ncalloc(alloc) ), norigin, b2Vec2.calloc(alloc) );

            b2Vec2 p1 = lower;
            b2Vec2 p2 = b2Vec2.calloc(alloc).set( upper.x(), lower.y() );
            b2Vec2 p3 = upper;
            b2Vec2 p4 = b2Vec2.calloc(alloc).set( lower.x(), upper.y() );
            
            Vector3f[] buff1 = vector3fPool.size(2).takePush();
            Vector3f[] buff2 = vector3fPool.size(2).takePush();
            Vector3f[] buff3 = vector3fPool.size(2).takePush();
            Vector3f[] buff4 = vector3fPool.size(2).takePush();
            cache.add(buff1);
            cache.add(buff2);
            cache.add(buff3);
            cache.add(buff4);
            
            AxisType axisType = box2dDebugAppState.getPhysicsSpace().getAxisType(); 
            Converter.toVector3fValueOfJME3(p1, axisType, buff1[0]);
            Converter.toVector3fValueOfJME3(p2, axisType, buff1[1]);
            
            Converter.toVector3fValueOfJME3(p2, axisType, buff2[0]);
            Converter.toVector3fValueOfJME3(p3, axisType, buff2[1]);
            
            Converter.toVector3fValueOfJME3(p3, axisType, buff3[0]);
            Converter.toVector3fValueOfJME3(p4, axisType, buff3[1]);
            
            Converter.toVector3fValueOfJME3(p4, axisType, buff4[0]);
            Converter.toVector3fValueOfJME3(p1, axisType, buff4[1]);
            
            Node rootNode = shapeRenderManager.create();
            rootNode.attachChild(shapeRenderManager.render(MeshRender.LINE, buff1, color, false));
            rootNode.attachChild(shapeRenderManager.render(MeshRender.LINE, buff2, color, false));
            rootNode.attachChild(shapeRenderManager.render(MeshRender.LINE, buff3, color, false));
            rootNode.attachChild(shapeRenderManager.render(MeshRender.LINE, buff4, color, false));
            return rootNode;
        }
    }
    
    public void renderFree() {
        try (shapeRenderManager) {
            for (Object child : cache) {
                if (child instanceof BitmapText txt) {
                    bitmapTextPool.takePop(txt);
                } else if (child instanceof Vector3f[] vec3) {
                    vector3fPool.takePop(vec3);
                }
            }            
            cache.clear();
        }
    }
}
