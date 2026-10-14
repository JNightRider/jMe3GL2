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

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.util.TempVars;

import java.util.ArrayList;
import java.util.List;

import org.box2d.jni.b2Pos;
import org.box2d.jni.b2Rot;
import org.box2d.jni.b2Vec2;
import org.box2d.jni.b2WorldTransform;

import static org.box2d.jni.include.MathFunctions.*;

import org.je3gl.scene.debug.Polygon2D;
import org.je3gl.scene.debug.ShapeMode2D;
import org.je3gl.utilities.ColorUtilities;

/**
 *
 * @author wil
 */
public class DPolygon {

    private static final class Data {

        final Vector3f[] vertices;
        final Vector3f position;
        final float angle;
        final boolean solid;
        final ColorRGBA color;

        private Data(Vector3f[] vertices, Vector3f position, float angle, boolean solid, ColorRGBA color) {
            this.vertices = vertices;
            this.position = position;
            this.angle = angle;
            this.solid = solid;
            this.color = color;
        }

        static Data create(b2WorldTransform transform, long vertices, int vertexCount, int color, boolean solid) {
            b2Vec2.Buffer buffer = b2Vec2.createSafe(vertices, vertexCount);
            Vector3f[] vs = new Vector3f[vertexCount];
            int index = 0;
            for (b2Vec2 vec2 : buffer) {
                vs[index++] = new Vector3f(vec2.x(), vec2.y(), 0.0f);
            }
            b2Pos pos = transform.p();
            b2Rot rot = transform.q();
            return new Data(
                    vs,
                    new Vector3f(pos.x().floatValue(), pos.y().floatValue(), 0.0f),
                    b2Rot_GetAngle(rot),
                    solid,
                    ColorUtilities.fromIntRGBA(color, 1.0f)
            );
        }

        static Data create(b2WorldTransform transform, long vertices, int vertexCount, float radius, int color, boolean solid) {
            return create(transform, vertices, vertexCount, color, solid);
        }
    }

    public static final int POLYGON_BATCH_SIZE = 2048;

    private final List<Data> dataList = new ArrayList<>();
    private final Polygon2D meshFilled  = new Polygon2D(ShapeMode2D.FILLED, POLYGON_BATCH_SIZE);
    private final Polygon2D meshOutline = new Polygon2D(ShapeMode2D.OUTLINE, POLYGON_BATCH_SIZE);

    public DPolygon() {
    }

    public void addData(b2WorldTransform transform, long vertices, int vertexCount, int color, boolean solid) {
        dataList.add(Data.create(transform, vertices, vertexCount, color, solid));
    }

    public void addData(b2WorldTransform transform, long vertices, int vertexCount, float radius, int color, boolean solid) {
        dataList.add(Data.create(transform, vertices, vertexCount, radius, color, solid));
    }

    public void draw(RenderManager renderManager, Geometry drawable) {
        for (Data data : dataList) {
            Material mat = drawable.getMaterial();
            TempVars vars = TempVars.get();

            meshFilled.updatePoints(data.vertices);
            meshOutline.updatePoints(data.vertices);
            drawable.setMesh(meshFilled);

            Quaternion q = vars.quat1;
            drawable.setLocalTranslation(data.position);
            drawable.setLocalRotation(q.fromAngleAxis(data.angle + FastMath.PI, Vector3f.UNIT_Z));
            drawable.updateGeometricState();
            drawable.updateModelBound();

            // draw solid
            if (data.solid) {
                mat.setColor("Color", data.color.setAlpha(Draw.ALPHA_FILL_COLOR));
                mat.getAdditionalRenderState().setWireframe(false);
                renderManager.renderGeometry(drawable);
            }

            // draw lines
            drawable.setMesh(meshOutline);
            mat.setColor("Color", data.color.setAlpha(Draw.ALPHA_LINE_COLOR));
            mat.getAdditionalRenderState().setWireframe(true);
            renderManager.renderGeometry(drawable);

            vars.release();
        }
    }

    public void clear() {
        dataList.clear();
    }
}
