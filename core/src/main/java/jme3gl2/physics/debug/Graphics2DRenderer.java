/* Copyright (c) 2009-2023 jMonkeyEngine.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jme3gl2.physics.debug;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;

import jme3gl2.physics.control.PhysicsBody2D;
import jme3gl2.physics.debug.control.AbstractConvexDebugControl;
import jme3gl2.physics.debug.shape.Capsule2D;
import jme3gl2.physics.debug.shape.Circle2D;
import jme3gl2.physics.debug.shape.Ellipse2D;
import jme3gl2.physics.debug.shape.HalfEllipse2D;
import jme3gl2.physics.debug.shape.Polygon2D;
import jme3gl2.physics.debug.shape.Slice2D;
import jme3gl2.util.Converter;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Slice;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Wound;

/**
 * Un objeto de la clase <code>raphics2DRenderer</code> se encarga de renderizar
 * los cuerpos físicos, es decir que se encarga de buscar una forma para ello.
 * <p>
 * Clase encargado de gestionar los colores, materiales y formas de un cuerpo
 * físico para depurarlo en tiempo real.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT 
 * 
 * @since 2.5.0
 */
public final 
class Graphics2DRenderer {

    /** Loggre de la clase. */
    private static final Logger LOGGER = Logger.getLogger(Graphics2DRenderer.class.getName());
    
    ///** Administrador-recursos <code>JME</code> */
    private final AssetManager assetManager;

    /**
     * Constructor de la clase <code>Graphics2DRenderer</code>.
     * @param assetManager administrador de recursos <b>JME</b>.
     */
    public Graphics2DRenderer(AssetManager assetManager) {
        LOGGER.log(Level.INFO, "[ ok ] :{0}", "Initializing Graphics2DRenderer");
        this.assetManager = assetManager;
    }
        
    /**
     * Método encargado de crear los materiales que utilizarán los
     * {@code Spatial} para la depuración de los cuerpos físicos.
     * 
     * @param color color del material.
     * @return material generado.
     */
    public Material createMat(ColorRGBA color) {
        final Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        return mat;
    }
    
    /**
     * Método encargado de renderizar la forma de un <code>Bounds</code> del
     * mundo de las física utilizado por <b>dyn4j</b>.
     * 
     * @param <T> tipo-geometría
     * @param attach <code>true</code> si se desea devolver un nodo, de lo
     * contrario <code>false</code> si solo se quiere la geometría.
     * 
     * @param vs puntos para la forma del <code>Bounds</code>
     * @return objeto-bounds gráfico.
     */
    @SuppressWarnings("unchecked")
    public <T extends Spatial> T renderBounds(boolean attach, Vector2... vs) {
        final Geometry geom = new Geometry("Bounds-Geometry", new Polygon2D(Converter.toArrayVector3f(vs)));        
        final Material mat = createMat(Dyn4jDebugColor.BOUNDS);
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
     * Método encargado de renderizar la forma física a un objeto gráfico.
     * 
     * @param fixture forma física
     * @param body cuerpo físico
     * @param color color para la forma física.
     * @return objeto gráficos generado.
     */
    public Node render(BodyFixture fixture, PhysicsBody2D body, ColorRGBA color) {
        final Convex shape = fixture.getShape();
        String uid = String.valueOf(fixture);
        
        final Node node = new Node(uid);
        node.attachChild(createOriginAxes(shape.getCenter()));
        
        if (color == null) {
            color = Dyn4jDebugColor.DEFAULT;
        }
        
        Geometry geom = null;
        if (shape instanceof Wound) {
            final Wound wound = (Wound) shape;

            final Vector3f[] vertices = 
                    Converter.toArrayVector3f(wound.getVertices());
            final Polygon2D woundDebug = new Polygon2D(vertices);
            
            geom = new Geometry(uid, woundDebug);            
            geom.addControl(new AbstractConvexDebugControl.ConvexDebugControl(fixture, body));
        } else if (shape instanceof Circle) {
            final Circle circle = (Circle) shape;
            
            final float radius = Converter.toFloat(circle.getRadius());
            final Circle2D circleDebug = 
                    new Circle2D(Circle2D.COUNT, radius, 0, 0);
            
            geom = new Geometry(uid, circleDebug);
            geom.addControl(new AbstractConvexDebugControl.CircleDebugControl(fixture, body));
        } else if (shape instanceof Capsule) {
            final Capsule capsule = (Capsule) shape;

            final float width = Converter.toFloat(capsule.getLength());
            final float height = Converter.toFloat(capsule.getCapRadius() * 2.0);
            
            final Capsule2D capsuleDebug = 
                    new Capsule2D(Capsule2D.COUNT, width, height, 0);
            geom = new Geometry(uid, capsuleDebug);
            geom.addControl(new AbstractConvexDebugControl.CapsuleDebugControl(fixture, body));
        } else if (shape instanceof Ellipse) {
            final Ellipse ellipse = (Ellipse) shape;

            final float width = Converter.toFloat(ellipse.getWidth());
            final float height = Converter.toFloat(ellipse.getHeight());

            final Ellipse2D ellipseDebug = 
                    new Ellipse2D(Ellipse2D.COUNT, width, height, height);
            geom = new Geometry(uid, ellipseDebug);
            geom.addControl(new AbstractConvexDebugControl.EllipseDebugControl(fixture, body));
        } else if (shape instanceof HalfEllipse) {
            final HalfEllipse halfEllipse = (HalfEllipse) shape;

            final float width = Converter.toFloat(halfEllipse.getHalfWidth());
            final float height = Converter.toFloat(halfEllipse.getHeight());
            
            final HalfEllipse2D halfEllipseDebug = 
                    new HalfEllipse2D(HalfEllipse2D.COUNT, width, height, 0);
            geom = new Geometry(uid, halfEllipseDebug);
            geom.addControl(new AbstractConvexDebugControl.HalfEllipseDebugControl(fixture, body));
        } else if (shape instanceof Slice) {
            final Slice slice = (Slice) shape;
            
            final float radius = Converter.toFloat(slice.getSliceRadius());
            final float angle = Converter.toFloat(slice.getTheta() * 0.5);
            
            final Slice2D sliceDebug = 
                    new Slice2D(Slice2D.COUNT, radius, angle, 0);
            geom = new Geometry(uid, sliceDebug);
            geom.addControl(new AbstractConvexDebugControl.SliceDebugControl(fixture, body));
        } else {
            LOGGER.log(Level.WARNING, "#### Shape ''{0}'' not supported. ####", 
                    shape.getClass().getSimpleName());
        }        
        if (geom != null) {
            geom.setMaterial(createMat(color));
            geom.setQueueBucket(RenderQueue.Bucket.Translucent);
            node.attachChild(geom);
        }
        return node;
    }
    
    /**
     * Método encargado de crear un nodo en los ejes de origen.
     * 
     * @param center centro del objeto.
     * @return nodo centrado.
     */
    private Node createOriginAxes(final Vector2 center) {
        final Node node = new Node("Origin");
        node.attachChild(createAxisArrow(Vector3f.UNIT_X.mult(.25f), ColorRGBA.Red));
        node.attachChild(createAxisArrow(Vector3f.UNIT_Y.mult(.25f), ColorRGBA.Green));
        node.setLocalTranslation(Converter.toVector3f(center));
        return node;
    }

    /**
     * Método encargado de crear una flecha para los ejes.
     * 
     * @param direction dirección de la flecha.
     * @param color color de la flecha.
     * @return Objeto gráfico generado.
     */
    private Spatial createAxisArrow(final Vector3f direction, final ColorRGBA color) {
        final Arrow axis = new Arrow(direction);
        final Geometry axisGeomg = new Geometry("axis", axis);
        
        axisGeomg.setMaterial(createMat(color));
        axisGeomg.setQueueBucket(RenderQueue.Bucket.Translucent);
        return axisGeomg;
    }
}
