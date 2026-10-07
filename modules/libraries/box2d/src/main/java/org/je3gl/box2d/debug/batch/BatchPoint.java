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
package org.je3gl.box2d.debug.batch;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;

import java.util.ArrayList;
import java.util.List;

import org.je3gl.scene.debug.Point2D;

/**
 *
 * @author wil
 */
public class BatchPoint {
    public static final int POINT_BATCH_SIZE = 2048;
    
    private final List<PointData> pointDatas = new ArrayList<>(POINT_BATCH_SIZE);
    
    private final Geometry drawable;
    private final Point2D point;

    public BatchPoint(AssetManager assetManager) {
        this.point = new Point2D();
        this.drawable = Batch.newDrawable(assetManager, point);
    }

    public void addPoint(float x, float y, float size, int rgba) {
        if (pointDatas.size() >= Integer.MAX_VALUE) {
            return;
        }
        PointData data = new PointData();
        data.setColor(rgba);
        data.setPosition(new Vector3f(x, y, 0f));
        data.setSize(size);
        pointDatas.add(data);
    }

    public void flushPoints(RenderManager renderManager) {
        for (int i = 0; i < pointDatas.size(); i++) {
            PointData data = pointDatas.get(i);
            point.updatePoints(data.getPosition(), data.getSize());

            Material mat = drawable.getMaterial();
            mat.getAdditionalRenderState().setWireframe(false);
            mat.setColor("Color", data.getColor());
            mat.setFloat("PointSize", data.getSize());

            renderManager.renderGeometry(drawable);
        }
    }

    public void clear() {
        pointDatas.clear();
    }
}
