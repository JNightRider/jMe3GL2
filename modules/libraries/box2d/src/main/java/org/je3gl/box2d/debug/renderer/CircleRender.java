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
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import java.util.ArrayList;
import java.util.List;
import org.je3gl.box2d.util.ObjectPool;

/**
 *
 * @author wil
 */
public class CircleRender extends ShapeRender {

    private final List<CircleData> list = new ArrayList<>();
    private final ObjectPool<CircleData> dataPool = new CircleData.Pool();

    private final MeshRender<Float> meshRender = MeshRender.CIRCLE;
    
    public CircleRender(AssetManager assetManager) {
        super(assetManager, "Circle");
    }
    
    public void addDrawCircle(
            float x, float y,
            float angle,
            float cx, float cy, 
            float radius, 
            ColorRGBA color
    ) {
        synchronized (lock) {
            CircleData data = dataPool.takePush();
            data.setCenter(x, y);
            data.setRGBA(color);
            data.setRadius(radius);
            list.add(data);
        }
    }

    @Override
    public void flushDraw(RenderManager renderManager, boolean solid) {
        synchronized (lock) {
            for (int i = 0; i < list.size(); i++) {
                CircleData data = list.get(i);

                checkGeometry(drawable);
                meshRender.render(drawable, data.getRGBA(), data.getRadius(), true);

                drawable.setLocalTransform(data.getTransform());
                drawable.updateGeometricState();
                drawable.updateModelBound();
                renderManager.renderGeometry(drawable);

                if (solid) {
                    checkGeometry(border);
                    meshRender.render(border, data.getRGBA(), data.getRadius(), false);

                    border.setLocalTransform(data.getTransform());
                    border.updateGeometricState();
                    border.updateModelBound();
                    renderManager.renderGeometry(border);
                }

                dataPool.takePop(data);
                list.remove(i);
                i--;
            }
        }
    }    
}
