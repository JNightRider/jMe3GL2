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
package org.jegl.utilities;

import java.util.List;
import org.dyn4j.geometry.*;

/**
 * An <code>jMe3GL2Geometry</code> is in charge of assisting us as an intermediary
 * between the class {@code org.dyn4j.geometry.Geometry}, in order to avoid errors
 * with the class {@code com.jme3.scene.Geometry}.
 * 
 * @author wil
 * @version 1.1.1
 * @since 1.0.0
 */
public final class GeometryUtilities {
    
    /**  Private constructor. */
    private GeometryUtilities() {
    }

    /**
     * (non-Javadoc)
     * @see Geometry#TWO_PI
     */
    public static final double TWO_PI = Geometry.TWO_PI;

    /**
     * (non-Javadoc)
     * @see Geometry#INV_3
     */
    public static final double INV_3 = Geometry.INV_3;

    /**
     * (non-Javadoc)
     * @see Geometry#INV_SQRT_3
     */
    public static final double INV_SQRT_3 = Geometry.INV_SQRT_3;

    /**
     * (non-Javadoc)
     * @see Geometry#getWinding(java.util.List) 
     * 
     * @param points list
     * @return double
     */
    public static final double getWinding(List<Vector2> points) {
        return Geometry.getWinding(points);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#getWinding(org.dyn4j.geometry.Vector2...) 
     * 
     * @param points list
     * @return double
     */
    public static final double getWinding(Vector2... points) {
        return Geometry.getWinding(points);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#reverseWinding(org.dyn4j.geometry.Vector2...) 
     * 
     * @param points list
     */
    public static final void reverseWinding(Vector2... points) {
        Geometry.reverseWinding(points);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#reverseWinding(java.util.List) 
     * 
     * @param points list
     */
    public static final void reverseWinding(List<Vector2> points) {
        Geometry.reverseWinding(points);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#getAverageCenter(java.util.List) 
     * 
     * @param points list
     * @return vector2
     */
    public static final Vector2 getAverageCenter(List<Vector2> points) {
        return Geometry.getAverageCenter(points);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#getAverageCenter(org.dyn4j.geometry.Vector2...) 
     * 
     * @param points list
     * @return vector2
     */
    public static final Vector2 getAverageCenter(Vector2... points) {
        return Geometry.getAverageCenter(points);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#getAreaWeightedCenter(java.util.List) 
     * 
     * @param points list
     * @return vector2
     */
    public static final Vector2 getAreaWeightedCenter(List<Vector2> points) {
        return Geometry.getAreaWeightedCenter(points);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#getAreaWeightedCenter(org.dyn4j.geometry.Vector2...) 
     * 
     * @param points list
     * @return vector2
     */
    public static final Vector2 getAreaWeightedCenter(Vector2... points) {
        return Geometry.getAreaWeightedCenter(points);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#getRotationRadius(org.dyn4j.geometry.Vector2...) 
     * 
     * @param vertices list
     * @return double
     */
    public static final double getRotationRadius(Vector2... vertices) {
        return Geometry.getRotationRadius(new Vector2(), vertices);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#getRotationRadius(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Vector2...) 
     * 
     * @param center vector2
     * @param vertices list
     * @return double
     */
    public static final double getRotationRadius(Vector2 center, Vector2... vertices) {
        return Geometry.getRotationRadius(center, vertices);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#getCounterClockwiseEdgeNormals(org.dyn4j.geometry.Vector2...) 
     * 
     * @param vertices list
     * @return array
     */
    public static final Vector2[] getCounterClockwiseEdgeNormals(Vector2... vertices) {
        return Geometry.getCounterClockwiseEdgeNormals(vertices);
    }

    
    /**
     * (non-Javadoc)
     * @see Geometry#createCircle(double) 
     * 
     * @param radius double
     * @return circle
     */
    public static final Circle createCircle(double radius) {
        return Geometry.createCircle(radius);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createPolygon(org.dyn4j.geometry.Vector2...) 
     * 
     * @param vertices list
     * @return polygon
     */
    public static final Polygon createPolygon(Vector2... vertices) {
        return Geometry.createPolygon(vertices);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createPolygonAtOrigin(org.dyn4j.geometry.Vector2...) 
     * 
     * @param vertices list
     * @return polygon
     */
    public static final Polygon createPolygonAtOrigin(Vector2... vertices) {
        return Geometry.createPolygonAtOrigin(vertices);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createUnitCirclePolygon(int, double) 
     * 
     * @param count int
     * @param radius double
     * @return polygon
     */
    public static final Polygon createUnitCirclePolygon(int count, double radius) {
        return Geometry.createUnitCirclePolygon(count, radius);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createUnitCirclePolygon(int, double, double) 
     * 
     * @param count int
     * @param radius double
     * @param theta double
     * @return polygon
     */
    public static final Polygon createUnitCirclePolygon(int count, double radius, double theta) {
        return Geometry.createUnitCirclePolygon(count, radius, theta);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createSquare(double) 
     * 
     * @param size double
     * @return rectangle
     */
    public static final Rectangle createSquare(double size) {
        return Geometry.createSquare(size);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createRectangle(double, double) 
     * 
     * @param width double
     * @param height double
     * @return rectangle
     */
    public static final Rectangle createRectangle(double width, double height) {
        return Geometry.createRectangle(width, height);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createTriangle(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Vector2) 
     * 
     * @param p1 vector2
     * @param p2 vector2
     * @param p3 vector2
     * @return triangle
     */
    public static final Triangle createTriangle(Vector2 p1, Vector2 p2, Vector2 p3) {
        return Geometry.createTriangle(p1, p2, p3);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createTriangleAtOrigin(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Vector2) 
     * 
     * @param p1 vector2
     * @param p2 vector2
     * @param p3 vector2
     * @return triangle
     */
    public static final Triangle createTriangleAtOrigin(Vector2 p1, Vector2 p2, Vector2 p3) {
        return Geometry.createTriangleAtOrigin(p1, p2, p3);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createRightTriangle(double, double) 
     * 
     * @param width double
     * @param height double
     * @return triangle
     */
    public static final Triangle createRightTriangle(double width, double height) {
        return Geometry.createRightTriangle(width, height);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createRightTriangle(double, double, boolean) 
     * 
     * @param width double
     * @param height double
     * @param mirror double
     * @return triangle
     */
    public static final Triangle createRightTriangle(double width, double height, boolean mirror) {
        return Geometry.createRightTriangle(width, height, mirror);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createEquilateralTriangle(double) 
     * 
     * @param height double
     * @return triangle
     */
    public static final Triangle createEquilateralTriangle(double height) {
        return Geometry.createEquilateralTriangle(height);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createIsoscelesTriangle(double, double) 
     * 
     * @param width double
     * @param height double
     * @return triangle
     */
    public static final Triangle createIsoscelesTriangle(double width, double height) {
        return Geometry.createIsoscelesTriangle(width, height);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createSegment(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Vector2) 
     * 
     * @param p1 vector2
     * @param p2 vector2
     * @return segment
     */
    public static final Segment createSegment(Vector2 p1, Vector2 p2) {
        return Geometry.createSegment(p1, p2);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createSegmentAtOrigin(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Vector2) 
     * 
     * @param p1 vector2
     * @param p2 vector2
     * @return segment
     */
    public static final Segment createSegmentAtOrigin(Vector2 p1, Vector2 p2) {
        return Geometry.createSegmentAtOrigin(p1, p2);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createSegment(org.dyn4j.geometry.Vector2) 
     * 
     * @param end vector2
     * @return segment
     */
    public static final Segment createSegment(Vector2 end) {
        return Geometry.createSegment(end);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createHorizontalSegment(double) 
     * @param length double
     * @return segment
     */
    public static final Segment createHorizontalSegment(double length) {
        return Geometry.createHorizontalSegment(length);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createVerticalSegment(double) 
     * @param length double
     * @return segment
     */
    public static final Segment createVerticalSegment(double length) {
        return Geometry.createVerticalSegment(length);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createCapsule(double, double) 
     * 
     * @param width double
     * @param height double
     * @return capsule
     */
    public static final Capsule createCapsule(double width, double height) {
        return new Capsule(width, height);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createSlice(double, double) 
     * 
     * @param radius double
     * @param theta double
     * @return slice
     */
    public static final Slice createSlice(double radius, double theta) {
        return new Slice(radius, theta);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createSliceAtOrigin(double, double) 
     * 
     * @param radius double
     * @param theta double
     * @return slice
     */
    public static final Slice createSliceAtOrigin(double radius, double theta) {
        return Geometry.createSliceAtOrigin(radius, theta);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createEllipse(double, double) 
     * 
     * @param width double
     * @param height double
     * @return ellipse
     */
    public static final Ellipse createEllipse(double width, double height) {
        return  Geometry.createEllipse(width, height);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createHalfEllipse(double, double) 
     * 
     * @param width double
     * @param height double
     * @return half ellipse
     */
    public static final HalfEllipse createHalfEllipse(double width, double height) {
        return Geometry.createHalfEllipse(width, height);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createHalfEllipseAtOrigin(double, double) 
     * 
     * @param width double
     * @param height double
     * @return half ellipse
     */
    public static final HalfEllipse createHalfEllipseAtOrigin(double width, double height) {
        return Geometry.createHalfEllipseAtOrigin(width, height);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createPolygonalCircle(int, double) 
     * 
     * @param count double
     * @param radius double
     * @return polygon
     */
    public static final Polygon createPolygonalCircle(int count, double radius) {
        return Geometry.createPolygonalCircle(count, radius);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createPolygonalCircle(int, double, double) 
     * 
     * @param count double
     * @param radius double
     * @param theta double
     * @return polygon
     */
    public static final Polygon createPolygonalCircle(int count, double radius, double theta) {
        return Geometry.createPolygonalCircle(count, radius, theta);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createPolygonalSlice(int, double, double) 
     * 
     * @param count double
     * @param radius double
     * @param theta double
     * @return polygon
     */
    public static final Polygon createPolygonalSlice(int count, double radius, double theta) {
        return Geometry.createPolygonalSlice(count, radius, theta);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createPolygonalSliceAtOrigin(int, double, double) 
     * 
     * @param count double
     * @param radius double
     * @param theta double
     * @return polygon
     */
    public static final Polygon createPolygonalSliceAtOrigin(int count, double radius, double theta) {
        return Geometry.createPolygonalSliceAtOrigin(count, radius, theta);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createPolygonalEllipse(int, double, double) 
     * 
     * @param count double
     * @param width double
     * @param height double
     * @return polygon
     */
    public static final Polygon createPolygonalEllipse(int count, double width, double height) {
        return Geometry.createPolygonalEllipse(count, width, height);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createPolygonalHalfEllipse(int, double, double) 
     * 
     * @param count double
     * @param width double
     * @param height double
     * @return polygon
     */
    public static final Polygon createPolygonalHalfEllipse(int count, double width, double height) {
        return Geometry.createPolygonalHalfEllipse(count, width, height);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createPolygonalHalfEllipseAtOrigin(int, double, double) 
     * 
     * @param count double
     * @param width double
     * @param height double
     * @return polygon
     */
    public static final Polygon createPolygonalHalfEllipseAtOrigin(int count, double width, double height) {
        return Geometry.createPolygonalHalfEllipseAtOrigin(count, width, height);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createPolygonalCapsule(int, double, double) 
     * 
     * @param count double
     * @param width double 
     * @param height double
     * @return polygon
     */
    public static final Polygon createPolygonalCapsule(int count, double width, double height) {
        return Geometry.createPolygonalCapsule(count, width, height);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#cleanse(java.util.List) 
     * 
     * @param points list
     * @return list
     */
    public static final List<Vector2> cleanse(List<Vector2> points) {
        return Geometry.cleanse(points);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#cleanse(org.dyn4j.geometry.Vector2...) 
     * 
     * @param points list
     * @return array
     */
    public static final Vector2[] cleanse(Vector2... points) {
        return Geometry.cleanse(points);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#flipAlongTheXAxis(org.dyn4j.geometry.Polygon) 
     * 
     * @param polygon polygon
     * @return polygon
     */
    public static final Polygon flipAlongTheXAxis(Polygon polygon) {
        return Geometry.flipAlongTheXAxis(polygon);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#flipAlongTheYAxis(org.dyn4j.geometry.Polygon) 
     * 
     * @param polygon polygon
     * @return polygon
     */
    public static final Polygon flipAlongTheYAxis(Polygon polygon) {
        return Geometry.flipAlongTheYAxis(polygon);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#flipAlongTheXAxis(org.dyn4j.geometry.Polygon, org.dyn4j.geometry.Vector2) 
     * 
     * @param polygon polygon
     * @param point vector2
     * @return polugon
     */
    public static final Polygon flipAlongTheXAxis(Polygon polygon, Vector2 point) {
        return Geometry.flipAlongTheXAxis(polygon, point);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#flipAlongTheYAxis(org.dyn4j.geometry.Polygon, org.dyn4j.geometry.Vector2) 
     * 
     * @param polygon polygon
     * @param point vector2
     * @return polygon
     */
    public static final Polygon flipAlongTheYAxis(Polygon polygon, Vector2 point) {
        return Geometry.flipAlongTheYAxis(polygon, point);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#flip(org.dyn4j.geometry.Polygon, org.dyn4j.geometry.Vector2) 
     * 
     * @param polygon polygon
     * @param axis vector2
     * @return polygon
     */
    public static final Polygon flip(Polygon polygon, Vector2 axis) {
        return Geometry.flip(polygon, axis);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#flip(org.dyn4j.geometry.Polygon, org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Vector2) 
     * 
     * @param polygon polygon
     * @param axis vector2
     * @param point vector2
     * @return polygon
     */
    public static final Polygon flip(Polygon polygon, Vector2 axis, Vector2 point) {
        return Geometry.flip(polygon, axis, point);
    }

    /**
     * (non-Javadoc)
     * @param <E> tipo
     * @param convex1 convex
     * @param convex2 convex
     * @return polygon
     */
    public static final <E extends Wound & Convex> Polygon minkowskiSum(E convex1, E convex2) {
        return Geometry.minkowskiSum(convex1, convex2);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#minkowskiSum(org.dyn4j.geometry.Circle, org.dyn4j.geometry.Polygon, int) 
     * 
     * @param circle circle
     * @param polygon polygon
     * @param count int
     * @return polygon
     */
    public static final Polygon minkowskiSum(Circle circle, Polygon polygon, int count) {
        return Geometry.minkowskiSum(circle, polygon, count);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#minkowskiSum(org.dyn4j.geometry.Polygon, org.dyn4j.geometry.Circle, int) 
     * 
     * @param polygon polygon
     * @param circle circle
     * @param count int
     * @return polygon
     */
    public static final Polygon minkowskiSum(Polygon polygon, Circle circle, int count) {
        return Geometry.minkowskiSum(polygon, circle, count);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#minkowskiSum(org.dyn4j.geometry.Polygon, double, int) 
     * 
     * @param polygon polygon
     * @param radius double
     * @param count int
     * @return polygon
     */
    public static final Polygon minkowskiSum(Polygon polygon, double radius, int count) {
        return Geometry.minkowskiSum(polygon, radius, count);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#scale(org.dyn4j.geometry.Circle, double) 
     * 
     * @param circle circle
     * @param scale double
     * @return circle
     */
    public static final Circle scale(Circle circle, double scale) {
        return Geometry.scale(circle, scale);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#scale(org.dyn4j.geometry.Capsule, double) 
     * 
     * @param capsule capsule
     * @param scale double
     * @return capsule
     */
    public static final Capsule scale(Capsule capsule, double scale) {
        return Geometry.scale(capsule, scale);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#scale(org.dyn4j.geometry.Ellipse, double) 
     * 
     * @param ellipse ellipse
     * @param scale double
     * @return ellipse
     */
    public static final Ellipse scale(Ellipse ellipse, double scale) {
        return Geometry.scale(ellipse, scale);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#scale(org.dyn4j.geometry.HalfEllipse, double) 
     * 
     * @param halfEllipse half-ellipse
     * @param scale double
     * @return half-ellipse.
     */
    public static final HalfEllipse scale(HalfEllipse halfEllipse, double scale) {
        return Geometry.scale(halfEllipse, scale);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#scale(org.dyn4j.geometry.Slice, double)  
     * 
     * @param slice slice
     * @param scale double
     * @return slice
     */
    public static final Slice scale(Slice slice, double scale) {
        return Geometry.scale(slice, scale);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#scale(org.dyn4j.geometry.Polygon, double) 
     * 
     * @param polygon polygon
     * @param scale double
     * @return polygon
     */
    public static final Polygon scale(Polygon polygon, double scale) {
        return Geometry.scale(polygon, scale);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#scale(org.dyn4j.geometry.Segment, double) 
     * 
     * @param segment segment
     * @param scale double
     * @return segment
     */
    public static final Segment scale(Segment segment, double scale) {
        return Geometry.scale(segment, scale);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createLinks(java.util.List, boolean) 
     * 
     * @param vertices list
     * @param closed boolean
     * @return list
     */
    public static final List<Link> createLinks(List<Vector2> vertices, boolean closed) {
        return Geometry.createLinks(vertices, closed);
    }

    /**
     * (non-Javadoc)
     * @see Geometry#createLinks(org.dyn4j.geometry.Vector2[], boolean) 
     * 
     * @param vertices array
     * @param closed boolean
     * @return list
     */
    public static final List<Link> createLinks(Vector2[] vertices, boolean closed) {
        return Geometry.createLinks(vertices, closed);
    }
}
