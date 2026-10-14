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
import com.jme3.math.Quaternion;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import org.box2d.jni.b2AABB;
import static org.box2d.jni.b2HexColor.*;

import org.box2d.jni.b2Pos;
import org.box2d.jni.b2Transform;
import org.box2d.jni.b2Vec2;
import org.box2d.jni.b2WorldTransform;
import org.box2d.jni.include.MathFunctions;
import static org.box2d.jni.include.MathFunctions.*;
import org.box2d.jni.include.MathTypes;
import static org.box2d.jni.include.MathTypes.*;
import org.box2d.jni.system.ArenaAlloc;
import static org.box2d.jni.system.ArenaAlloc.*;
import org.je3gl.scene.debug.custom.DebugGraphics;

/**
 *
 * @author wil
 */
public final class Draw {

    public static final float ALPHA_FILL_COLOR = 0.5f;
    public static final float ALPHA_LINE_COLOR = 1.0f;

    private final DText texts;
    private final DLine lines;
    private final DLine bounds;
    private final DPoint points;
    private final DCircle circles;
    private final DLine transforms;
    private final DCapsule capsules;
    private final DPolygon polygons;

    private final Geometry drawableLines;
    private final Geometry drawableBounds;
    private final Geometry drawablePoints;
    private final Geometry drawableCircles;
    private final Geometry drawableCapsules;
    private final Geometry drawablepolygons;
    private final Geometry drawableTransforms;

    public Draw(AssetManager assetManager) {
        texts = new DText();
        lines = new DLine();
        bounds = new DLine();
        points = new DPoint();
        circles = new DCircle();
        transforms = new DLine();
        capsules = new DCapsule();
        polygons = new DPolygon();

        drawableLines = newDrawable(assetManager);
        drawableBounds = newDrawable(assetManager);
        drawablePoints = newDrawable(assetManager);
        drawableCircles = newDrawable(assetManager);
        drawableCapsules = newDrawable(assetManager);
        drawablepolygons = newDrawable(assetManager);
        drawableTransforms = newDrawable(assetManager);
    }

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

    public void drawLine(b2Pos p1, b2Pos p2, int color) {
        lines.addData(p1, p2, color);
    }

    public void drawCircle(b2Pos center, float radius, int color) {
        circles.addData(center, radius, color, false);
    }

    public void drawPoint(b2Pos p, float size, int color) {
        points.addData(p, size, color);
    }

    public void drawSolidCircle(b2WorldTransform transform, b2Pos center, float radius, int color) {
        circles.addData(transform, center, radius, color, true);
    }

    public void drawCapsule(b2Pos p1, b2Pos p2, float radius, int color, boolean solid) {
        capsules.addData(p1, p2, radius, color, solid);
    }

    public void drawPolygon(b2WorldTransform transform, long vertices, int vertexCount, int color) {
        polygons.addData(transform, vertices, vertexCount, color, false);
    }

    public void drawSolidPolygon(b2WorldTransform transform, long vertices, int vertexCount, float radius, int color) {
        polygons.addData(transform, vertices, vertexCount, radius, color, true);
    }

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

    public void drawString(b2Pos p, long s, int color) {
        texts.addData(p, s, color);
    }

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

    public void render(RenderManager rm) {
        lines.draw(rm, drawableLines);
        bounds.draw(rm, drawableBounds);
        points.draw(rm, drawablePoints);
        circles.draw(rm, drawableCircles);
        capsules.draw(rm, drawableCapsules);
        polygons.draw(rm, drawablepolygons);
        transforms.draw(rm, drawableTransforms);
    }

    public void render(DebugGraphics graphics, Node rootNode, Camera camera, int width, int height) {
        texts.setGraphics(graphics);
        texts.draw(rootNode, camera, width, height);
    }

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
