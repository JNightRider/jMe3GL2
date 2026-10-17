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
package org.je3gl.box2d.debug.draw;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import org.box2d.jni.b2AABB;
import org.box2d.jni.b2Pos;
import org.box2d.jni.b2Transform;
import org.box2d.jni.b2Vec2;
import org.box2d.jni.b2WorldTransform;

import org.box2d.jni.system.ArenaAlloc;

import static org.box2d.jni.b2HexColor.*;
import static org.box2d.jni.include.MathFunctions.*;
import static org.box2d.jni.include.MathTypes.*;
import static org.box2d.jni.system.ArenaAlloc.*;

import org.je3gl.scene.debug.custom.DebugGraphics;

/**
 * Class responsible for managing a drawing area for the basic shapes used by
 * the Box2D debugger.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public final class Draw {

    /** Transparency of solid forms. */
    public static final float ALPHA_FILL_COLOR = 0.5f;
    /** Transparency of the shapes' outlines. */
    public static final float ALPHA_LINE_COLOR = 1.0f;

    /** Batch of drawings of type: {@code Text} */
    private final BatchText texts;
    /** Batch of drawings of type: {@code Line} */
    private final BatchLine
            lines,      // Lienes
            bounds,     // Bounds
            transforms; // Math/Transforms

    /** Batch of drawings of type: {@code Point} */
    private final BatchPoint points;
    /** Batch of drawings of type: {@code Circle} */
    private final BatchCircle circles;
    /** Batch of drawings of type: {@code Capsule} */
    private final BatchCapsule capsules;
    /** Batch of drawings of type: {@code Polygon} */
    private final BatchPolygon polygons;

    /** Drawable geometry for the {@link #lines} batch. */
    private final Geometry drawableLines;
    /** Drawable geometry for the {@link #bounds} batch. */
    private final Geometry drawableBounds;
    /** Drawable geometry for the {@link #points} batch. */
    private final Geometry drawablePoints;
    /** Drawable geometry for the {@link #circles} batch. */
    private final Geometry drawableCircles;
    /** Drawable geometry for the {@link #capsules} batch. */
    private final Geometry drawableCapsules;
    /** Drawable geometry for the {@link #polygons} batch. */
    private final Geometry drawablepolygons;
    /** Drawable geometry for the {@link #transforms} batch. */
    private final Geometry drawableTransforms;

    /**
     * Constructor
     *
     * @param assetManager AssetManager
     */
    public Draw(AssetManager assetManager) {
        texts = new BatchText();
        lines = new BatchLine();
        bounds = new BatchLine();
        points = new BatchPoint();
        circles = new BatchCircle();
        transforms = new BatchLine();
        capsules = new BatchCapsule();
        polygons = new BatchPolygon();

        drawableLines = newDrawable(assetManager);
        drawableBounds = newDrawable(assetManager);
        drawablePoints = newDrawable(assetManager);
        drawableCircles = newDrawable(assetManager);
        drawableCapsules = newDrawable(assetManager);
        drawablepolygons = newDrawable(assetManager);
        drawableTransforms = newDrawable(assetManager);
    }

    /**
     * Create a new geometry with the corresponding material.
     *
     * @param assetManager AssetManager
     * @return Geometry
     */
    private static Geometry newDrawable(AssetManager assetManager) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setFloat("AlphaDiscardThreshold", 0.2f);
        mat.getAdditionalRenderState().setDepthTest(false);

        Geometry drawable = new Geometry();
        drawable.setQueueBucket(RenderQueue.Bucket.Transparent);
        drawable.setMaterial(mat);
        return drawable;
    }

    /**
     * Draws a shape of type {@code Line}
     *
     * @param p1 b2Pos
     * @param p2 b2Pos
     * @param color int
     */
    public void drawLine(b2Pos p1, b2Pos p2, int color) {
        lines.addData(p1, p2, color);
    }

    /**
     * Draws a shape of type {@code Circle}
     *
     * @param center b2Pos
     * @param radius float
     * @param color int
     */
    public void drawCircle(b2Pos center, float radius, int color) {
        circles.addData(center, radius, color, false);
    }

    /**
     * Draws a shape of type {@code Point}
     *
     * @param p b2Pos
     * @param size float
     * @param color int
     */
    public void drawPoint(b2Pos p, float size, int color) {
        points.addData(p, size, color);
    }

    /**
     * Draws a shape of type {@code Solid-Circle}
     *
     * @param transform b2WorldTransform
     * @param center b2Pos
     * @param radius float
     * @param color int
     */
    public void drawSolidCircle(b2WorldTransform transform, b2Pos center, float radius, int color) {
        circles.addData(transform, center, radius, color, true);
    }

    /**
     * Draws a shape of type {@code Capsule}
     *
     * @param p1 b2Pos
     * @param p2 b2Pos
     * @param radius float
     * @param color int
     */
    public void drawCapsule(b2Pos p1, b2Pos p2, float radius, int color) {
        capsules.addData(p1, p2, radius, color, true);
    }

    /**
     * Draws a shape of type {@code Polygon}
     *
     * @param transform b2WorldTransform
     * @param vertices long
     * @param vertexCount int
     * @param color int
     */
    public void drawPolygon(b2WorldTransform transform, long vertices, int vertexCount, int color) {
        polygons.addData(transform, vertices, vertexCount, color, false);
    }

    /**
     * Draws a shape of type {@code Solid-Polygon}
     *
     * @param transform b2WorldTransform
     * @param vertices long
     * @param vertexCount int
     * @param radius float
     * @param color int
     */
    public void drawSolidPolygon(b2WorldTransform transform, long vertices, int vertexCount, float radius, int color) {
        polygons.addData(transform, vertices, vertexCount, radius, color, true);
    }

    /**
     * Draws a shape of type {@code Transform}
     *
     * @param transform b2WorldTransform
     * @param scale float
     */
    public void drawTransform(b2WorldTransform transform, float scale) {
        try (ArenaAlloc arna = allocPush()) {
            b2Transform xf = b2ToRelativeTransform(transform, b2Pos_zero, b2Transform.calloc(arna));

            b2Vec2 p1 = xf.p();

            b2Vec2 p2 = b2MulAdd(p1, scale, b2Rot_GetXAxis(xf.q(), b2Vec2.calloc(arna)), b2Vec2.calloc(arna));
            transforms.addData(p1, p2, b2_colorRed);

            p2 = b2MulAdd(p1, scale, b2Rot_GetYAxis(xf.q(), b2Vec2.calloc(arna)), b2Vec2.calloc(arna));
            transforms.addData(p1, p2, b2_colorGreen);
        }
    }

    /**
     * Draws a shape of type {@code String}
     *
     * @param p b2Pos
     * @param s long
     * @param color int
     */
    public void drawString(b2Pos p, long s, int color) {
        texts.addData(p, s, color);
    }

    /**
     * Draws a shape of type {@code Bounds}
     *
     * @param aabb b2AABB
     * @param color int
     */
    public void drawBounds(b2AABB aabb, int color) {
        try (ArenaAlloc arna = allocPush()) {
            b2Vec2 lower = b2SubPos(b2ToPos(aabb.lowerBound(), b2Pos.calloc(arna)), b2Pos_zero, b2Vec2.calloc(arna));
            b2Vec2 upper = b2SubPos(b2ToPos(aabb.upperBound(), b2Pos.calloc(arna)), b2Pos_zero, b2Vec2.calloc(arna));

            b2Vec2 p1 = lower;
            b2Vec2 p2 = b2Vec2.calloc(arna).set(upper.x(), lower.y());
            b2Vec2 p3 = upper;
            b2Vec2 p4 = b2Vec2.calloc(arna).set(lower.x(), upper.y());

            bounds.addData(p1, p2, color);
            bounds.addData(p2, p3, color);
            bounds.addData(p3, p4, color);
            bounds.addData(p4, p1, color);
        }
    }

    /**
     * Renders all the drawings on the screen.
     *
     * @param rm RenderManager
     */
    public void render(RenderManager rm) {
        lines.draw(rm, drawableLines);
        bounds.draw(rm, drawableBounds);
        points.draw(rm, drawablePoints);
        circles.draw(rm, drawableCircles);
        capsules.draw(rm, drawableCapsules);
        polygons.draw(rm, drawablepolygons);
        transforms.draw(rm, drawableTransforms);
    }

    /**
     * Renders all the drawings on the screen (UI).
     *
     * @param graphics DebugGraphics
     * @param rootNode Node
     * @param camera Camera
     * @param width int
     * @param height int
     */
    public void render(DebugGraphics graphics, Node rootNode, Camera camera, int width, int height) {
        texts.setGraphics(graphics);
        texts.draw(rootNode, camera, width, height);
    }

    /**
     * Clear the canvas (drawing space or data batches)
     */
    public void flush() {
        texts.clear();
        lines.clear();
        bounds.clear();
        points.clear();
        circles.clear();
        capsules.clear();
        polygons.clear();
        transforms.clear();
    }
}
