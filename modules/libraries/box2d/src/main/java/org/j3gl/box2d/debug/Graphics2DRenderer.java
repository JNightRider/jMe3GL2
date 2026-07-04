/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.j3gl.box2d.debug;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Line;
import com.jme3.util.TempVars;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.box2d.jni.b2HexColor;
import org.box2d.jni.b2Pos;
import org.box2d.jni.b2WorldTransform;
import org.box2d.jni.include.MathFunctions;
import static org.box2d.jni.include.MathFunctions.*;
import org.j3gl.box2d.AxisType;
import org.j3gl.box2d.util.Converter;

import org.je3gl.scene.debug.Cross;
import org.je3gl.scene.debug.Capsule2D;
import org.je3gl.scene.debug.Circle2D;
import org.je3gl.scene.debug.Ellipse2D;
import org.je3gl.scene.debug.HalfEllipse2D;
import org.je3gl.scene.debug.Polygon2D;
import org.je3gl.scene.debug.Slice2D;
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
    
    /** Resource manager <code>JME</code>. */
    private final AssetManager assetManager;    
    /** Debugger. */
    private final Box2dDebugAppState box2dDebugAppState;
    
    private GeometryPool geometryPool;
    private NodePool nodePool;
    private BitmapTextPool bitmapTextPool;
    
    private List<Spatial> cache;

    /**
     * Class constructor <code>Graphics2DRenderer</code>.
     * @param box2dDebugAppState debugger
     */
    public Graphics2DRenderer(Box2dDebugAppState box2dDebugAppState) {
        this.assetManager = box2dDebugAppState.getApplication().getAssetManager();
        this.box2dDebugAppState = box2dDebugAppState;
        this.bitmapTextPool = new BitmapTextPool();
        this.geometryPool = new GeometryPool();
        this.nodePool = new NodePool();
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

    /**
     * Method in charge of creating the materials to be used by the
     * {@code Spatial} for the debugging of the physical bodies.
     *
     * @param c color of the material
     * @param alpha the alpha component 
     * @return generated material
     */
    public Material createMat(int c, float alpha) {
        return createMat(ColorUtilities.fromIntRGBA(c, alpha));
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
    
    public Node renderString(b2Pos pos, String txt, int color) {
        BitmapText text = bitmapTextPool.takePush();
        text.setText(txt);
        text.setColor(ColorUtilities.fromIntRGBA(color, 1.0f));

        text.setLocalTranslation(Converter.toVector3fValueOfJME3(pos, box2dDebugAppState.getPhysicsSpace().getAxisType()));
        text.setQueueBucket(RenderQueue.Bucket.Translucent);
        
        text.move(-(text.getLineWidth() / 2.0f) * 0.12f, 0, 0);
        cache.add(text);
        return text;
    }

    /**
     * Method in charge of rendering the physical form to a graphic object.
     *
     * @param transforms the transformation of the body
     * @param vertices physical shape
     * @param color color for physical shape
     * @return generated graphical object
     */
    public Node renderPolygon(b2WorldTransform transforms, Vector3f[] vertices, int color) {
        Node node = nodePool.takePush();
        cache.add(node);
        
        AxisType axisType = box2dDebugAppState.getPhysicsSpace().getAxisType();        
        TempVars vars = TempVars.get();
        float angle = b2Rot_GetAngle(transforms.q());

        
        node.setLocalTranslation(Converter.toVector3fValueOfJME3(transforms.p(), axisType));
        node.setLocalRotation(vars.quat1.fromAngleAxis(angle, Converter.toUNIT3f(axisType)));
        
        Geometry geom0 = renderPolygonGeometry(vertices, color, true);
        Geometry geom1 = renderPolygonGeometry(vertices, color, false);
        
        
        cache.add(geom0);
        cache.add(geom1);
        node.attachChild(geom0);
        node.attachChild(geom1);
        
        vars.release();
        return node;
    }
    
    private Geometry renderPolygonGeometry(Vector3f[] vertices, int color, boolean fill) {
        Geometry geom = geometryPool.takePush();
        Mesh mesh     = geom.getMesh();
        
        if (!(mesh instanceof Polygon2D)) {
            mesh = new Polygon2D(fill, vertices);
        } else {
            ((Polygon2D)mesh).updateGeometry(fill, vertices);
        }

        Material mat = geom.getMaterial();
        float a = fill ? 0.1f : 1.0f;
        if (mat == null) {
            mat = createMat(color, a);
        } else {
            mat.setColor("Color", ColorUtilities.fromIntRGBA(color, a));
        }
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setWireframe(!fill);
        
        if (! fill) {
            mat.getAdditionalRenderState().setLineWidth(2);
            geom.setQueueBucket(RenderQueue.Bucket.Translucent);
        } else {
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
        }
        
        geom.setMesh(mesh);
        geom.setMaterial(mat);
        return geom;
    }
    
    public void renderFree() {
        for (Spatial child : cache) {
            if (child instanceof Geometry geom) {
                geometryPool.takePop(geom);
            } else if (child instanceof BitmapText txt) {
                bitmapTextPool.takePop(txt);
            } else if (child instanceof Node node) {
                nodePool.takePop(node);
            }
        }
        cache.clear();
    }
}
