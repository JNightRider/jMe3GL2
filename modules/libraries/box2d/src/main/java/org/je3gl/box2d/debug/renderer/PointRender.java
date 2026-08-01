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
package org.je3gl.box2d.debug.renderer;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.util.TempVars;

import java.util.ArrayList;
import java.util.List;

import org.je3gl.box2d.util.ObjectPool;

/**
 *
 * @author wil
 */
public class PointRender extends Render {

    private final List<PointData> points = new ArrayList<>();
    private final ObjectPool<PointData> dataPool = new PointData.Pool();

    private final MeshRender<Vector4f> meshRender = MeshRender.POINT;

    public PointRender(AssetManager assetManager) {
        super(assetManager);
    }

    public void addDrawPoint(float x, float y, float size, ColorRGBA rgba) {
        synchronized (lock) {
            PointData data = dataPool.takePush();
            data.setPosition(x, y);
            data.setRGBA(rgba);
            data.setSize(size);
            points.add(data);
        }
    }

    @Override
    public void flushDraw(RenderManager renderManager, boolean solid) {
        synchronized (lock) {
            for (int i = 0; i < points.size(); i++) {
                PointData data = points.get(i);
                Geometry geom  = gp.takePush();
                checkGeometry(geom);

                TempVars vars = TempVars.get();
                Vector4f attr = vars.vect4f1;
                attr.set(data.getPosition().x, data.getPosition().y, 0.0f, data.getSize() * 1.5f);

                meshRender.render(geom, data.getRGBA(), attr, solid);
                renderManager.renderGeometry(geom);

                vars.release();
                gp.takePop(geom);
                dataPool.takePop(data);
                points.remove(i);
                i--;
            }
        }
    }
}
