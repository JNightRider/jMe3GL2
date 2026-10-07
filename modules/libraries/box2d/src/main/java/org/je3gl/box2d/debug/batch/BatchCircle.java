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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.util.TempVars;

import java.util.ArrayList;
import java.util.List;

import org.je3gl.scene.debug.Circle2D;

/**
 *
 * @author wil
 */
public class BatchCircle {
    public static final int CIRCLE_BATCH_SIZE = 2048;
    
    private final List<CircleData> circleData = new ArrayList<>(CIRCLE_BATCH_SIZE);
    
    private final Geometry drawable;
    private final Circle2D circle;

    public BatchCircle(AssetManager assetManager) {
        this.circle = new Circle2D(Circle2D.COUNT, 1f, 0);
        this.drawable = Batch.newDrawable(assetManager, circle);
    }

    public void addCircle(float x, float y, float radius, float angle, int rgba, boolean solid) {
        if (circleData.size() >= Integer.MAX_VALUE) {
            return;
        }
        CircleData data = new CircleData();
        data.setColor(rgba);
        data.setPosition(new Vector3f(x, y, 0f));
        data.setRadius(radius);
        data.setSolid(solid);
        data.setAngle(angle);
        circleData.add(data);
    }

    public void flushCircle(RenderManager renderManager) {
        for (int i = 0; i < circleData.size(); i++) {
            CircleData data = circleData.get(i);
            circle.updateGeometry(Circle2D.COUNT, data.getRadius(), 0);

            Material mat = drawable.getMaterial();    
            

            TempVars vars = TempVars.get();
            Quaternion q = vars.quat1;
            drawable.setLocalTranslation(data.getPosition());
            drawable.setLocalRotation(q.fromAngleAxis(data.getAngle(), Vector3f.UNIT_Z));
            drawable.updateGeometricState();
            drawable.updateModelBound();
            
            if (data.isSolid()) {
                circle.fill(true);                
                mat.setColor("Color", data.getColor().setAlpha(0.5f));
                mat.getAdditionalRenderState().setWireframe(false);
                renderManager.renderGeometry(drawable);
                
                circle.fill(false);
            }
            
            mat.setColor("Color", data.getColor().setAlpha(1f));
            mat.getAdditionalRenderState().setWireframe(true);                
            renderManager.renderGeometry(drawable);
            vars.release();
        }
    }

    public void clear() {
        circleData.clear();
    }
}
