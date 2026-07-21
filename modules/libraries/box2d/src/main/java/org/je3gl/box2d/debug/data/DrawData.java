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
package org.je3gl.box2d.debug.data;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.je3gl.box2d.AxisType;
import org.je3gl.box2d.debug.renderer.MeshRender;

/**
 *
 * @author wil
 * @param <T>
 */
public abstract class DrawData<T> {

    private boolean livingDrawing = false;
    private boolean needsUpdating = false;

    protected Node draw;
    protected MeshRender<T> render;
    
    protected AxisType axisType;

    public DrawData(MeshRender<T> render) {
        this.render = render;
        this.axisType = AxisType.AXIS_XYO;
    }

    public void setDraw(Node draw) {
        this.draw = draw;
        this.livingDrawing = true;
    }

    public void update() {
        for (Spatial child : draw.getChildren()) {
            rupdateChild(child);
        }
        needsUpdating = false;
    }
    
    private void rupdateChild(Spatial spatial) {
        if (spatial instanceof Geometry) {
            applyUpdate((Geometry) spatial);
        } else {
            for (Spatial child : ((Node) spatial).getChildren()) {
                rupdateChild(spatial);
            }
        }
    }
    
    protected abstract void applyUpdate(Geometry child);

    public void kill() {
        livingDrawing = false;
    }
    
    protected void check() {
        livingDrawing = true;
        needsUpdating = true;
    }
    
    public Node getDraw() {
        return draw;
    }

    public boolean isLivingDrawing() {
        return livingDrawing;
    }

    public boolean isNeedsUpdating() {
        return needsUpdating;
    }
}
