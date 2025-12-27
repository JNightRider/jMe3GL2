/*
BSD 3-Clause License

Copyright (c) 2023-2025, Night Rider (Wilson)

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
package org.je3gl.physics.debug;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Line;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Slice;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Wound;

import org.je3gl.scene.debug.Cross;
import org.je3gl.scene.debug.Capsule2D;
import org.je3gl.scene.debug.Circle2D;
import org.je3gl.scene.debug.Ellipse2D;
import org.je3gl.scene.debug.HalfEllipse2D;
import org.je3gl.scene.debug.Polygon2D;
import org.je3gl.scene.debug.Slice2D;
import org.je3gl.scene.debug.custom.DebugGraphics;
import org.je3gl.physics.util.Converter;
import org.je3gl.physics.control.PhysicsBody2D;
import static org.je3gl.physics.debug.Dyn4jDebugGraphics.*;
import static org.je3gl.physics.debug.AbstractConvexDebugControl.*;

/**
 * An object of the class <code>Graphics2DRenderer</code> is in charge of
 * rendering physical bodies, i.e. it is in charge of finding a form for it.
 * <p>
 * Class in charge of managing the colors, materials and shapes of a physical
 * body to debug it in real time.
 * </p>
 * @author wil
 * @version 1.6.0
 * @since 2.5.0
 */
public final class Graphics2DRenderer {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(Graphics2DRenderer.class.getName());
    
    /** Resource manager <code>JME</code>. */
    private final AssetManager assetManager;    
    /** Debugger. */
    private final Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState;

    /** Graphics debugger. */
    private DebugGraphics debugGraphics;
    
    /**
     * Class constructor <code>Graphics2DRenderer</code>.
     * @param dyn4jDebugAppState debugger
     */
    public Graphics2DRenderer(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState) {
        this.assetManager = dyn4jDebugAppState.getApplication().getAssetManager();
        this.dyn4jDebugAppState = dyn4jDebugAppState;
    }
    
    /**
     * Prints information about the physics engine that was configured on the screen.
     */
    void printInformation() {
        StringBuilder buff = new StringBuilder();
        buff.append("[jMe3GL2] :Charts for debugging Dyn4j bodies")
             .append('\n');        
        buff.append(" *  ").append(GL_DEBUG_AT_RESET).append(": ").append(debugGraphics.getColor(GL_DEBUG_AT_RESET))
            .append('\n');
        buff.append(" *  ").append(GL_DEBUG_BULLET).append(": ").append(debugGraphics.getColor(GL_DEBUG_BULLET))
            .append('\n');
        buff.append(" *  ").append(GL_DEBUG_DEFAULT).append(": ").append(debugGraphics.getColor(GL_DEBUG_DEFAULT))
            .append('\n');
        buff.append(" *  ").append(GL_DEBUG_DISABLED).append(": ").append(debugGraphics.getColor(GL_DEBUG_DISABLED))
            .append('\n');
        buff.append(" *  ").append(GL_DEBUG_KINEMATIC).append(": ").append(debugGraphics.getColor(GL_DEBUG_KINEMATIC))
            .append('\n');
        buff.append(" *  ").append(GL_DEBUG_SENSOR).append(": ").append(debugGraphics.getColor(GL_DEBUG_SENSOR))
            .append('\n');
        buff.append(" *  ").append(GL_DEBUG_STATIC).append(": ").append(debugGraphics.getColor(GL_DEBUG_STATIC))
            .append('\n');   
        buff.append(" *  ").append(GL_DEBUG_BOUNDS).append(": ").append(debugGraphics.getColor(GL_DEBUG_BOUNDS));
        LOGGER.log(Level.INFO, String.valueOf(buff));
    }

    /**
     * Returns the debug graphs.
     * @return object
     */
    public DebugGraphics getDebugGraphics() {
        return debugGraphics;
    }

    /**
     * Set debug graphs
     * @param debugGraphics object
     */
    void setDebugGraphics(DebugGraphics debugGraphics) {
        this.debugGraphics = debugGraphics;
    }
    
    /**
     * Method in charge of creating the materials to be used by the
     * {@code Spatial} for the debugging of the physical bodies.
     * 
     * @param color color of the material
     * @return generated material
     */
    public Material createMat(ColorRGBA color) {
        final Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        return mat;
    }
    
    /**
     * Method in charge of rendering the shape of a <code>Bounds</code> of the
     * world of physics used by <b>dyn4j</b>.
     * 
     * @param <T> geometry type
     * @param attach <code>true</code> if you wish to return a node, otherwise
     * <code>false</code> if only the geometry is desired
     * 
     * @param vs points for the shape of the <code>Bounds</code>
     * @return graphical object <code>Bounds</code>
     */
    @SuppressWarnings("unchecked")
    public <T extends Spatial> T renderBounds(boolean attach, Vector2... vs) {
        final Geometry geom = new Geometry("Bounds-Geometry", new Polygon2D(Converter.allToVector3fValueOfJME3(vs)));        
        final Material mat = createMat(debugGraphics.getColor(GL_DEBUG_BOUNDS));
        geom.setMaterial(mat);
        if (attach) {
            Node node = new Node();
            node.attachChild(geom);
            return (T) node;
        }
        geom.setQueueBucket(RenderQueue.Bucket.Translucent);
        return (T) geom;
    }
    
    /**
     * Method in charge of rendering the physical form to a graphic object.
     * 
     * @param fixture physical shape
     * @param body physical body
     * @param color color for physical shape
     * @return generated graphical object
     */
    public Node render(BodyFixture fixture, PhysicsBody2D body, ColorRGBA color) {
        final Convex shape = fixture.getShape();
        String uid = String.valueOf(fixture);
        
        final Node node = new Node(uid);
        final Node shapeNode = new Node("Shape");

        shapeNode.attachChild(createOriginAxes(shape.getCenter()));
        node.attachChild(shapeNode);
        
        if (color == null) {
            color = Dyn4jDebugColor.DEFAULT;
        }
        
        Geometry geom = null;
        if (shape instanceof Wound) {
            final Wound wound = (Wound) shape;

            final Vector3f[] vertices = Converter.allToVector3fValueOfJME3(wound.getVertices());
            final Polygon2D woundDebug = new Polygon2D(vertices);
            
            geom = new Geometry(GM_BODY_SHAPE, woundDebug);
            if (shape instanceof Rectangle) {
                shapeNode.addControl(new AbstractConvexDebugControl.RectangleDebugControl(dyn4jDebugAppState,fixture, body));
            } else {
                shapeNode.addControl(new AbstractConvexDebugControl.ConvexDebugControl(dyn4jDebugAppState,fixture, body));
            }
        } else if (shape instanceof Circle) {
            final Circle circle = (Circle) shape;
            
            final float radius = Converter.toFloatValue(circle.getRadius());
            final Circle2D circleDebug = new Circle2D(Circle2D.COUNT, radius, 0);
            
            geom = new Geometry(GM_BODY_SHAPE, circleDebug);
            shapeNode.addControl(new AbstractConvexDebugControl.CircleDebugControl(dyn4jDebugAppState, fixture, body));
        } else if (shape instanceof Capsule) {
            final Capsule capsule = (Capsule) shape;

            final float width = Converter.toFloatValue(capsule.getLength());
            final float height = Converter.toFloatValue(capsule.getCapRadius() * 2.0);
            
            final Capsule2D capsuleDebug = new Capsule2D(Capsule2D.COUNT, width, height);
            
            geom = new Geometry(GM_BODY_SHAPE, capsuleDebug);
            shapeNode.addControl(new AbstractConvexDebugControl.CapsuleDebugControl(dyn4jDebugAppState, fixture, body));
        } else if (shape instanceof Ellipse) {
            final Ellipse ellipse = (Ellipse) shape;

            final float width = Converter.toFloatValue(ellipse.getWidth());
            final float height = Converter.toFloatValue(ellipse.getHeight());

            final Ellipse2D ellipseDebug = new Ellipse2D(Ellipse2D.COUNT, width, height);
            
            geom = new Geometry(GM_BODY_SHAPE, ellipseDebug);
            shapeNode.addControl(new AbstractConvexDebugControl.EllipseDebugControl(dyn4jDebugAppState, fixture, body));
        } else if (shape instanceof HalfEllipse) {
            final HalfEllipse halfEllipse = (HalfEllipse) shape;

            final float width = Converter.toFloatValue(halfEllipse.getHalfWidth() * 2.0);
            final float height = Converter.toFloatValue(halfEllipse.getHeight());
            
            final HalfEllipse2D halfEllipseDebug = new HalfEllipse2D(HalfEllipse2D.COUNT, width, height);
            
            geom = new Geometry(GM_BODY_SHAPE, halfEllipseDebug);
            shapeNode.addControl(new AbstractConvexDebugControl.HalfEllipseDebugControl(dyn4jDebugAppState, fixture, body));
        } else if (shape instanceof Slice) {
            final Slice slice = (Slice) shape;
            
            final float radius = Converter.toFloatValue(slice.getSliceRadius());
            final float angle = Converter.toFloatValue(slice.getTheta() * 0.5);
            
            final Slice2D sliceDebug = new Slice2D(Slice2D.COUNT, radius, angle);
            
            geom = new Geometry(GM_BODY_SHAPE, sliceDebug);
            shapeNode.addControl(new AbstractConvexDebugControl.SliceDebugControl(dyn4jDebugAppState, fixture, body));
        } else {
            LOGGER.log(Level.WARNING, "#### Shape ''{0}'' not supported. ####", shape.getClass().getSimpleName());
        }        
        if (geom != null) {
            geom.setMaterial(createMat(color));
            geom.setQueueBucket(RenderQueue.Bucket.Translucent);
            shapeNode.attachChild(geom);
        }
        return node;
    }
    
    /**
     * Method responsible for rendering a shape using geometry.
     * 
     * @param <T> the type of form to represent
     * @param type shape class
     * @param args arguments used by the shape:<pre><code>
     * Line: 
     *  - Vector3f
     *  - Vector3f
     * 
     * Circle2D:
     *  - Vector3f
     *  - float
     *  - float
     * 
     * Cross:
     *  - Vector3f
     *  - float
     *  - boolean
     * 
     * Polygon2D
     *  - boolean (true)
     *      - float
     *      - float
     * 
     *  // otherwise
     *      - Vector3f[]
     * 
     * </code></pre>
     * @return model
     */
    public <T> Geometry renderMesh(Class<T> type, Object ...args) {
        if (type == null) {
            return null;
        }
        Geometry geometry = null;
        if (Line.class.isAssignableFrom(type)) {            
            Vector3f start = (Vector3f) args[0];
            Vector3f end   = (Vector3f) args[1];
            
            Line line = new Line(start, end);
            geometry  = new Geometry("Line", line);            
        } else if (Circle2D.class.isAssignableFrom(type)) {
           Vector3f center = (Vector3f) args[0];
           float radius    = (float)    args[1];
           float theta     = (float)    args[2];
           
           Circle2D circle2D = new Circle2D(Circle2D.COUNT, radius, theta);
           circle2D.setMode(Mesh.Mode.LineLoop);
           
           geometry = new Geometry("Circle", circle2D);
           geometry.setLocalTranslation(center);
        } else if (Cross.class.isAssignableFrom(type)) {
            Vector3f center   = (Vector3f) args[0];
            float segmentSize = (float)    args[1];
            boolean pulley    = (boolean)  args[2];
            
            Cross cross = new Cross(segmentSize, pulley ? Mesh.Mode.LineLoop : Mesh.Mode.Lines);
            geometry    = new Geometry("Cross", cross);
            geometry.setLocalTranslation(center);
        } else if (Polygon2D.class.isAssignableFrom(type)) {            
            boolean square = (boolean) args[0];
            Vector3f[] vertices;
            
            if (!square) {
                vertices = new Vector3f[args.length - 1];
                for (int i = 1; i < args.length; i++) {
                    vertices[i] = (Vector3f) args[i];
                }
            } else {
                vertices = new Vector3f[4];
                float segment = (float) args[1];
                float size = segment / 2;
                
                vertices[0] = new Vector3f(size, size, 0);
                vertices[1] = new Vector3f(-size, size, 0);
                vertices[2] = new Vector3f(-size, -size, 0);
                vertices[3] = new Vector3f(size, -size, 0);
            }
            
            Polygon2D polygon2D = new Polygon2D(vertices);
            geometry = new Geometry("Polugon", polygon2D);
            
            if (square) {
                Vector3f center = (Vector3f) args[2];
                float segment   = (float) args[1];
                center.x -= segment / 2;
                center.y -= segment / 2;
                
                geometry.setLocalTranslation(center);
            }
        }
        
        return geometry;
    }
    
    /**
     * Method in charge of creating a node on the origin axes.
     * 
     * @param center center of the object
     * @return centered node
     */
    private Node createOriginAxes(final Vector2 center) {
        final Node node = new Node(GM_ORIGIN_AXES);
        node.attachChild(createAxisArrow(Vector3f.UNIT_X.mult(.25f), ColorRGBA.Red));
        node.attachChild(createAxisArrow(Vector3f.UNIT_Y.mult(.25f), ColorRGBA.Green));
        node.setLocalTranslation(Converter.toVector3fValueOfJME3(center));
        
        BitmapText x = debugGraphics.createBitmapText(debugGraphics.getBitmapFont("Default"), "x");
        x.setQueueBucket(RenderQueue.Bucket.Translucent);
        x.move(0.25F, -0.05F, 0);
        node.attachChild(x);

        BitmapText y = debugGraphics.createBitmapText(debugGraphics.getBitmapFont("Default"), "y");
        y.setQueueBucket(RenderQueue.Bucket.Translucent);
        y.move(-0.15F, 0.25F, 0);
        node.attachChild(y);
        return node;
    }

    /**
     * Method in charge of creating an arrow for the axes.
     * 
     * @param direction direction of the arrow
     * @param color color of the arrow
     * @return generated graphical object
     */
    private Spatial createAxisArrow(final Vector3f direction, final ColorRGBA color) {
        final Arrow axis = new Arrow(direction);
        final Geometry axisGeomg = new Geometry("axis", axis);
        axisGeomg.setMaterial(createMat(color));
        axisGeomg.setQueueBucket(RenderQueue.Bucket.Translucent);
        return axisGeomg;
    }
}
