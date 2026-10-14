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
import org.box2d.jni.b2Transform;
import org.box2d.jni.b2Vec2;

import org.box2d.jni.system.ArenaAlloc;
import static org.box2d.jni.include.MathFunctions.*;
import static org.box2d.jni.system.ArenaAlloc.*;

import org.je3gl.scene.debug.Capsule2D;
import org.je3gl.scene.debug.ShapeMode2D;
import org.je3gl.utilities.ColorUtilities;

/**
 *
 * @author wil
 */
public class DCapsule {

    private static final class Data {

        final Vector3f position;
        final float angle;
        final float radius;
        final float length;
        final boolean solid;
        final ColorRGBA color;

        public Data(Vector3f position, float angle, float radius, float length, boolean solid, ColorRGBA color) {
            this.position = position;
            this.angle = angle;
            this.radius = radius;
            this.length = length;
            this.solid = solid;
            this.color = color;
        }

        static Data create(b2Pos p1, b2Pos p2, float radius, int color, boolean solid) {
            try (ArenaAlloc arena = allocPush()) {
                b2Vec2 d = b2SubPos(p1, p2, b2Vec2.calloc(arena));
                float length = b2Length(d);
                if (length < 0.001f) {
                    System.err.println("sample app: capsule too short!");
                    return null;
                }

                b2Vec2 axis = b2Vec2.calloc(arena).set(d.x() / length, d.y() / length);
                b2Transform transform = b2Transform.calloc(arena);

                transform.p(b2Lerp(b2ToVec2(p1, b2Vec2.calloc(arena)), b2ToVec2(p2, b2Vec2.calloc(arena)), 0.5f, b2Vec2.calloc(arena)));
                transform.q().c(axis.x());
                transform.q().s(axis.y());

                return new Data(
                        new Vector3f(transform.p().x(), transform.p().y(), 0.0f),
                        b2Rot_GetAngle(transform.q()),
                        radius,
                        length,
                        solid,
                        ColorUtilities.fromIntRGBA(color, 1.0f)
                );
            }
        }
    }

    public static final int APSULE_BATCH_SIZE = 2048;

    private final List<Data> dataList = new ArrayList<>(APSULE_BATCH_SIZE);
    private final Capsule2D meshFilled  = new Capsule2D(ShapeMode2D.FILLED, Capsule2D.COUNT);
    private final Capsule2D meshOutline = new Capsule2D(ShapeMode2D.OUTLINE, Capsule2D.COUNT);

    public DCapsule() {
    }

    public void addData(b2Pos p1, b2Pos p2, float radius, int color, boolean solid) {
        dataList.add(Data.create(p1, p2, radius, color, solid));
    }

    public void draw(RenderManager renderManager, Geometry drawable) {
        for (Data data : dataList) {
            TempVars vars = TempVars.get();
            Material mat  = drawable.getMaterial();

            float radius = data.radius;
            float length = data.length;
            meshFilled.updateGeometry(Capsule2D.COUNT, radius * 2, length + (radius * 2));
            meshOutline.updateGeometry(Capsule2D.COUNT, radius * 2, length + (radius * 2));
            drawable.setMesh(meshFilled);

            Quaternion q = vars.quat1;
            drawable.setLocalTranslation(data.position);
            drawable.setLocalRotation(q.fromAngleAxis(data.angle + FastMath.PI / 2, Vector3f.UNIT_Z));
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
