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
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;

import java.util.ArrayList;
import java.util.List;

import org.box2d.jni.b2Pos;

import org.je3gl.scene.debug.Point2D;
import org.je3gl.utilities.ColorUtilities;

/**
 *
 * @author wil
 */
public class DPoint {

    private static final class Data {

        final Vector3f position;
        final float size;
        final ColorRGBA color;

        public Data(Vector3f position, float size, ColorRGBA color) {
            this.position = position;
            this.size = size;
            this.color = color;
        }

        static Data create(b2Pos p, float size, int color) {
            return new Data(
                    new Vector3f(p.x().floatValue(), p.y().floatValue(), 0.0f),
                    size,
                    ColorUtilities.fromIntRGBA(color, 1.0f)
            );
        }
    }

    public static final int POINT_BATCH_SIZE = 2048;

    private final List<Data> dataList = new ArrayList<>(POINT_BATCH_SIZE);
    private final Point2D mesh = new Point2D();

    public DPoint() {
    }

    public void addData(b2Pos p, float size, int color) {
        dataList.add(Data.create(p, size, color));
    }

    public void draw(RenderManager renderManager, Geometry drawable) {
        for (Data data : dataList) {
            Material mat = drawable.getMaterial();
            mat.getAdditionalRenderState().setWireframe(false);
            mat.setColor("Color", data.color);
            mat.setFloat("PointSize", data.size);

            mesh.updatePoints(data.position, data.size);
            drawable.setMesh(mesh);
            renderManager.renderGeometry(drawable);
        }
    }

    public void clear() {
        dataList.clear();
    }
}
