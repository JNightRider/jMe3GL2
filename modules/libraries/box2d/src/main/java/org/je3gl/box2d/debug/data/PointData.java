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

import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import java.util.Objects;
import org.box2d.jni.b2Pos;
import org.je3gl.box2d.debug.renderer.MeshRender;

/**
 *
 * @author wil
 */
public final class PointData extends DrawData<Vector4f> implements Cloneable {
    
    private Vector4f data = new Vector4f();
    private int color;

    public PointData() {
        super(MeshRender.POINT);
    }
    
    @Override
    public PointData clone() {
        try {
            PointData clon = (PointData) super.clone();
            clon.data    = this.data.clone();
            clon.render  = this.render;
            clon.draw    = null;
            clon.color   = color;
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
    
    public void update(b2Pos point, float size, int color) {
        this.data.set(point.x().floatValue(), point.y().floatValue(), 0.0f, size * 0.5f);
        this.color = color;
        check();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(data, color);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PointData other = (PointData) obj;
        if (this.color != other.color) {
            return false;
        }
        return Objects.equals(this.data, other.data);
    }
    
    @Override
    protected void applyUpdate(Geometry child) {
        render.render(child, color, data);
    }
}
