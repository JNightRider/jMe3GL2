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
import com.jme3.scene.shape.Line;
import java.util.ArrayList;
import java.util.List;
import org.box2d.jni.b2Pos;
import org.je3gl.utilities.ColorUtilities;

/**
 *
 * @author wil
 */
public final class BatchLine {

    private static final class Data {
        final Vector3f p1;
        final Vector3f p2;
        final ColorRGBA color;

        private Data(Vector3f p1, Vector3f p2, ColorRGBA color) {
            this.p1 = p1;
            this.p2 = p2;
            this.color = color;
        }
        
        static Data create(b2Pos p1, b2Pos p2, int color) {
            float x1 = p1.x().floatValue(),
                  y1 = p1.y().floatValue();

            float x2 = p2.x().floatValue(),
                  y2 = p2.y().floatValue();
            return new Data(
                    new Vector3f(x1, y1, 0.0f),
                    new Vector3f(x2, y2, 0.0f),
                    ColorUtilities.fromIntRGBA(color, 1.0f)
            );
        }
    }

    public static final int LINE_BATCH_SIZE = (2 * 2048);

    private final List<Data> dataList = new ArrayList<>(LINE_BATCH_SIZE);
    private final Line mesh = new Line(new Vector3f(), new Vector3f());

    public BatchLine() {
    }

    public void addData(b2Pos p1, b2Pos p2, int color) {
        dataList.add(Data.create(p1, p2, color));
    }

    public void draw(RenderManager renderManager, Geometry drawable) {
        for (Data data : dataList) {
            mesh.updatePoints(data.p1, data.p2);

            Material mat = drawable.getMaterial();
            mat.setColor("Color", data.color);

            drawable.setMesh(mesh);
            renderManager.renderGeometry(drawable);
        }
    }

    public void clear() {
        dataList.clear();
    }
}
