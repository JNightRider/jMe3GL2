/* Copyright (c) 2009-2024 jMonkeyEngine.
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
package org.je3gl.physics.collision;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector2f;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.je3gl.util.Converter;
import org.je3gl.utilities.GeometryUtilities;

import org.dyn4j.Epsilon;
import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Slice;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;

/**
 * Class responsible for encapsulating a physical form so that it can be safely 
 * exported and imported.
 * @param <E> type {@link org.dyn4j.geometry.Convex}
 * @author wil
 * @version 2.0.0
 * @since 1.0.0
 */
public class CollisionShape<E extends Convex> implements Savable, Cloneable {
    /** Logger class. */
    private static final Logger LOGGER = Logger.getLogger(CollisionShape.class.getName());
    
    /**
     * Enumerated class responsible for identifying the collision type (shape).
     */
    public static enum Type {
        
        /**
         * For a <code>Circle</code> shape.
         * @see org.dyn4j.geometry.Circle
         */
        Circle,
        
        /**
         * For a <code>Rectangle</code> shape.
         * @see org.dyn4j.geometry.Rectangle
         */
        Rectangle,
        
        /**
         * For a <code>Triangle</code> shape.
         * @see org.dyn4j.geometry.Triangle
         */
        Triangle,
        
        /**
         * For a <code>Polygon</code> shape.
         * @see org.dyn4j.geometry.Polygon
         */
        Polygon,
        
        /**
         * For a <code>Segment|Link</code> shape.
         * @see org.dyn4j.geometry.Segment
         * @see org.dyn4j.geometry.Link
         */
        Segment,
        
        /**
         * For a <code>Capsule</code> shape.
         * @see org.dyn4j.geometry.Capsule
         */
        Capsule,
        
        /**
         * For a <code>Ellipse</code> shape.
         * @see org.dyn4j.geometry.Ellipse
         */
        Ellipse,
        
        /**
         * For a <code>HalfEllipse</code> shape.
         * @see org.dyn4j.geometry.HalfEllipse
         */
        HalfEllipse,
        
        /**
         * For a <code>Slice</code> shape.
         * @see org.dyn4j.geometry.Slice
         */
        Slice,
        
        /**
         * For custom shapes
         */
        Custom;
        
        /**
         * Determines the type of collision shape.
         * @param convex a convex shape
         * @return this
         */
        public static Type valueOf(Convex convex) {
            if (convex instanceof org.dyn4j.geometry.Circle) {
                return Circle;
            } else if (convex instanceof org.dyn4j.geometry.Rectangle) {
                return Rectangle;
            } else if (convex instanceof org.dyn4j.geometry.Triangle) {
                return Triangle;
            } else if (convex instanceof org.dyn4j.geometry.Polygon) {
                return Polygon;
            } else if ((convex instanceof org.dyn4j.geometry.Segment) || (convex instanceof Link)) {
                return Segment;
            } else if (convex instanceof org.dyn4j.geometry.Capsule) {
                return Capsule;
            } else if (convex instanceof org.dyn4j.geometry.Ellipse) {
                return Ellipse;
            } else if (convex instanceof org.dyn4j.geometry.HalfEllipse) {
                return HalfEllipse;
            } else if (convex instanceof org.dyn4j.geometry.Slice) {
                return Slice;
            }
            return Custom;
        }
    }
    
    /** shape. */
    private E shape;
    /** type. */
    private Type type;

    /**
     * Constructor.
     */
    protected CollisionShape() {
    }
    
    /**
     * Generates a new <code>CollisionShape</code> to manage a physical form.
     * @param shape shape
     */
    public CollisionShape(E shape) {
        this.type  = Type.valueOf(shape);
        this.shape = shape;
    }

    /**
     * (non-Javadoc)
     * @see java.lang.Object#clone() 
     * @return clon
     */
    @Override
    @SuppressWarnings("unchecked")
    public CollisionShape<E> clone() {
        try {
            CollisionShape<E> clon = (CollisionShape<E>)
                                    super.clone();
            clon.type = type;            
            switch (type) {
                case Circle:
                    Circle circle = (Circle) shape;
                    clon.shape    = (E) GeometryUtilities.createCircle(circle.getRadius());
                    clon.shape.translate(circle.getCenter().copy());
                    break;
                case Rectangle:
                    Rectangle rectangle = (Rectangle) shape;
                    clon.shape = (E) GeometryUtilities.createRectangle(rectangle.getWidth(), rectangle.getHeight());
                    if (Math.abs(rectangle.getRotationAngle()) > Epsilon.E) {
                        clon.shape.rotate(rectangle.getRotationAngle());
                    }
                    clon.shape.translate(rectangle.getCenter().copy());
                    break;
                case Triangle:
                    Triangle triangle   = (Triangle) shape;
                    Vector2[] tVertices = triangle.getVertices();                    
                    clon.shape = (E) GeometryUtilities.createTriangle(tVertices[0].copy(), tVertices[1].copy(), tVertices[2].copy());
                    break;
                case Polygon:
                    Polygon polygon     = (Polygon) shape;
                    Vector2[] pVertices = new Vector2[polygon.getVertices().length];

                    for (int i = 0; i < pVertices.length; i++) {
                        pVertices[i] = polygon.getVertices()[i].copy();
                    }
                    clon.shape = (E) GeometryUtilities.createPolygon(pVertices);
                    break;
                case Segment: /*case Link:*/
                    Segment segment   = (Segment) shape;
                    Vector2[] segVert = segment.getVertices();                    
                    clon.shape = (E) GeometryUtilities.createSegment(segVert[0].copy(), segVert[1].copy());
                    break;
                case Capsule:
                    Capsule capsule = (Capsule) shape;
                    clon.shape      = (E) GeometryUtilities.createCapsule(capsule.getLength(), capsule.getCapRadius() * 2.0);
                    if (Math.abs(capsule.getRotationAngle()) > Epsilon.E) {
                        clon.shape.rotate(capsule.getRotationAngle());
                    }
                    clon.shape.translate(capsule.getCenter().copy());
                    break;
                case Ellipse:
                    Ellipse ellipse = (Ellipse) shape;
                    clon.shape      = (E) GeometryUtilities.createEllipse(ellipse.getHalfWidth() * 2.0, ellipse.getHalfHeight() * 2.0);
                    if (Math.abs(ellipse.getRotationAngle()) > Epsilon.E) {
                        clon.shape.rotate(ellipse.getRotationAngle());
                    }
                    clon.shape.translate(ellipse.getCenter().copy());
                    break;
                case HalfEllipse:
                    HalfEllipse halfEllipse = (HalfEllipse) shape;

                    double width = halfEllipse.getHalfWidth() * 2.0;
                    double height = halfEllipse.getHeight();
                    double originalY = (4.0 * height) / (3.0 * Math.PI);

                    clon.shape = (E) GeometryUtilities.createHalfEllipse(width, height);
                    if (Math.abs(halfEllipse.getRotationAngle()) > Epsilon.E) {
                        clon.shape.rotate(halfEllipse.getRotationAngle());
                    }
                    clon.shape.translate(halfEllipse.getCenter().copy());
                    break;
                case Slice:
                    Slice slice = (Slice) shape;

                    double theta = slice.getTheta();
                    double radius = slice.getSliceRadius();
                    double originalX = 2.0 * radius * Math.sin(theta * 0.5) / (1.5 * theta);

                    clon.shape = (E) GeometryUtilities.createSlice(radius, theta);

                    if (Math.abs(slice.getRotationAngle()) > Epsilon.E) {
                        clon.shape.rotate(slice.getRotationAngle());
                    }
                    clon.shape.translate(slice.getCenter().copy());
                    break;
                case Custom:
                    LOGGER.log(Level.WARNING, "This physical shape cannot be cloned");
                    break;
                default:
                    throw new AssertionError();
            }
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    
    /**
     * Returns physical shape
     * @return shape
     */
    public E getShape() {
        return shape;
    }

    /**
     * Returns the type
     * @return type
     */
    public Type getType() {
        return type;
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#read(com.jme3.export.JmeImporter) 
     * 
     * @param im {@link com.jme3.export.JmeImporter}
     * @throws IOException throws
     */
    @Override
    @SuppressWarnings("unchecked")
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);        
        type            = in.readEnum("type", Type.class, null);
        
        if (type == null) {
            throw new NullPointerException("Error reading collision shape type");
        }
        
        switch (type) {
            case Circle:
                double cRadius    = in.readDouble("Radius", 0.5);
                Vector2 cTraslate = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Translate", new Vector2f()));
                
                shape = (E) GeometryUtilities.createCircle(cRadius);
                shape.translate(cTraslate);
                break;
            case Rectangle:
                double rWidth  = in.readDouble("Width", 1.0),
                       rHeight = in.readDouble("Height", 1.0);
                
                double rRotationAngle = in.readDouble("RotationAngle", 0);
                Vector2 rTraslate     = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Translate", new Vector2f()));
                
                shape = (E) GeometryUtilities.createRectangle(rWidth, rHeight);
                shape.rotate(rRotationAngle);
                shape.translate(rTraslate);
                break;
            case Triangle:
                Vector2[] tVertices = {
                    Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("P1", new Vector2f(0.0F, 0.5F))),
                    Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("P2", new Vector2f(-0.5F, -0.0F))),
                    Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("P3", new Vector2f(0.5F, 0.0F)))
                };
                shape = (E) GeometryUtilities.createTriangle(tVertices[0], tVertices[1], tVertices[2]);
                break;
            case Polygon:
                int vSize = in.readInt("vSize", 0);
                Vector2[] pVertices = new Vector2[vSize];                
                for (int i = 0; i < vSize; i++) {
                    pVertices[i] = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable(String.valueOf(i), new Vector2f()));
                }
                
                shape = (E) GeometryUtilities.createPolygon(pVertices);
                break;
            case Segment: /*case Link:*/
                Vector2 sP1 = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("P1", new Vector2f())), 
                        sP2 = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("P2", new Vector2f()));
                shape = (E) GeometryUtilities.createSegment(sP1, sP2);
                break;
            case Capsule:
                double capWidth  = in.readDouble("Width", 0.5),
                       capHeight = in.readDouble("Height", 1.0);
                
                double capRotationAngle = in.readDouble("RotationAngle", 0);
                Vector2 capTraslate     = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Translate", new Vector2f()));
                
                shape = (E) GeometryUtilities.createCapsule(capWidth, capHeight);
                shape.rotate(capRotationAngle);
                shape.translate(capTraslate);
                break;
            case Ellipse:
                double eWidth  = in.readDouble("Width", 1.0),
                       eHeight = in.readDouble("Height", 0.5);
                
                double eRotationAngle = in.readDouble("RotationAngle", 0);
                Vector2 eTraslate     = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Translate", new Vector2f()));
                
                shape = (E) GeometryUtilities.createEllipse(eWidth, eHeight);
                shape.rotate(eRotationAngle);
                shape.translate(eTraslate);
                break;
            case HalfEllipse:
                double hWidth  = in.readDouble("Width", 1.0),
                       hHeight = in.readDouble("Height", 0.5);
                
                double hRotationAngle = in.readDouble("RotationAngle", 0);
                Vector2 hTraslate     = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Translate", new Vector2f()));
                
                shape = (E) GeometryUtilities.createHalfEllipse(hWidth, hHeight);
                shape.rotate(hRotationAngle);
                shape.translate(hTraslate);
                break;
            case Slice:
                double sRadius = in.readDouble("Radius", 0.5),
                        sTheta = in.readDouble("Theta", 0.2);
                
                double sRotationAngle = in.readDouble("RotationAngle", 0);
                Vector2 sTraslate     = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Translate", new Vector2f()));
                
                shape = (E) GeometryUtilities.createSlice(sRadius, sTheta);
                shape.rotate(sRotationAngle);
                shape.translate(sTraslate);
                break;
            case Custom:
                LOGGER.log(Level.WARNING, "Unknown/Unsupported class {0}", shape.getClass().getName());
                break;
            default:
                throw new AssertionError();
        }
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#write(com.jme3.export.JmeExporter) 
     * 
     * @param ex {@link com.jme3.export.JmeExporter}
     * @throws IOException throws
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(type, "type", null);
        
        switch (type) {
            case Circle:
                Circle circle = (Circle) shape;
                out.write(circle.getRadius(), "Radius", 0.5);
                
                if (!circle.getCenter().isZero()) {
                    out.write(Converter.toVector2fValueOfJME3(circle.getCenter()), "Translate", new Vector2f());
                }
                break;
            case Rectangle:
                Rectangle rectangle = (Rectangle) shape;
                
                out.write(rectangle.getWidth(), "Width", 1.0);
                out.write(rectangle.getHeight(), "Height", 1.0);
                
                if (Math.abs(rectangle.getRotationAngle()) > Epsilon.E) {
                    out.write(rectangle.getRotationAngle(), "RotationAngle", 0);
                }
                if (!rectangle.getCenter().isZero()) {
                    out.write(Converter.toVector2fValueOfJME3(rectangle.getCenter()), "Translate", new Vector2f());
                }
                break;
            case Triangle:
                Triangle triangle = (Triangle) shape;                
                out.write(Converter.toVector2fValueOfJME3(triangle.getVertices()[0]), "P1", null);
                out.write(Converter.toVector2fValueOfJME3(triangle.getVertices()[1]), "P2", null);
                out.write(Converter.toVector2fValueOfJME3(triangle.getVertices()[2]), "P3", null);
                break;
            case Polygon:
                Polygon polygon = (Polygon) shape;
                
                int vSize = polygon.getVertices().length;                
                out.write(vSize, "vSize", 0);     
                
                for (int i = 0; i < vSize; i++) {
                    Vector2 v = polygon.getVertices()[i];                    
                    out.write(Converter.toVector2fValueOfJME3(v), String.valueOf(i), null);
                }
                break;
            case Segment: /*case Link:*/
                Segment segment = (Segment) shape;                
                out.write(Converter.toVector2fValueOfJME3(segment.getVertices()[0]), "P1", null);
                out.write(Converter.toVector2fValueOfJME3(segment.getVertices()[1]), "P2", null);
                break;
            case Capsule:
                Capsule capsule = (Capsule) shape;
                
                out.write(capsule.getLength(), "Width", 0);
                out.write(capsule.getCapRadius() * 2.0, "Height", 0);
                
                if (Math.abs(capsule.getRotationAngle()) > Epsilon.E) {
                    out.write(capsule.getRotationAngle(), "RotationAngle", 0);
                }
                if (!capsule.getCenter().isZero()) {
                    out.write(Converter.toVector2fValueOfJME3(capsule.getCenter()), "Translate", new Vector2f());
                }
                break;
            case Ellipse:
                Ellipse ellipse = (Ellipse) shape;
                
                out.write(ellipse.getHalfWidth() * 2.0, "Width", 0);
                out.write(ellipse.getHalfHeight() * 2.0, "Height", 0);
                
                if (Math.abs(ellipse.getRotationAngle()) > Epsilon.E) {
                    out.write(ellipse.getRotationAngle(), "RotationAngle", 0);
                }
                if (!ellipse.getCenter().isZero()) {
                    out.write(Converter.toVector2fValueOfJME3(ellipse.getCenter()), "Translate", new Vector2f());
                }
                break;
            case HalfEllipse:
                HalfEllipse halfEllipse = (HalfEllipse) shape;
                
                double width = halfEllipse.getHalfWidth() * 2.0;
                double height = halfEllipse.getHeight();
                double originalY = (4.0 * height) / (3.0 * Math.PI);
		
                out.write(width, "Width", 0);
                out.write(height, "Height", 0);
                
                if (Math.abs(halfEllipse.getRotationAngle()) > Epsilon.E) {
                    out.write(halfEllipse.getRotationAngle(), "RotationAngle", 0);
                }
                if (halfEllipse.getCenter().y != originalY) {
                    out.write(Converter.toVector2fValueOfJME3(halfEllipse.getCenter()), "Translate", new Vector2f());
                }
                break;
            case Slice:
                Slice slice = (Slice) shape;
                
                double theta = slice.getTheta();
                double radius = slice.getSliceRadius();
                double originalX = 2.0 * radius * Math.sin(theta * 0.5) / (1.5 * theta);

                out.write(radius, "Radius", 0);
                out.write(theta, "Theta", 0);
                
                if (Math.abs(slice.getRotationAngle()) > Epsilon.E) {
                    out.write(slice.getRotationAngle(), "RotationAngle", 0);
                }
                if (slice.getCenter().x != originalX) {
                    out.write(Converter.toVector2fValueOfJME3(slice.getCenter()), "Translate", new Vector2f());
                }
                break;
            case Custom:
                LOGGER.log(Level.WARNING, "Unknown/Unsupported class {0}", shape.getClass().getName());
                break;
            default:
                throw new AssertionError();
        }
    }
}
