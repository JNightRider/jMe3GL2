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

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.util.TempVars;

import java.util.Objects;

import org.box2d.jni.b2Pos;
import org.je3gl.box2d.debug.renderer.MeshRender;
import org.je3gl.box2d.util.ComparatorUtils;

/**
 *
 * @author wil
 */
public final class LineData extends DrawData<Vector3f[]> implements Cloneable {
    
    private Vector2f p;
    private Vector2f size;
    
    private int color;

    public LineData() {
        super(MeshRender.LINE);
        this.p = new Vector2f();
        this.size = new Vector2f();
    }

    @Override
    public LineData clone() {
        try {
            LineData clon = (LineData) super.clone();
            clon.p       = this.p.clone();
            clon.size    = this.size.clone();
            clon.render  = this.render;
            clon.draw    = null;
            clon.color   = color;
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
    
    public void update(b2Pos p, b2Pos size, int color) {
        if (!ComparatorUtils.equals(this.p, p) || !ComparatorUtils.equals(this.size, size)) {
            this.p.set(p.x().floatValue(), p.y().floatValue());
            this.size.set(size.x().floatValue(), size.y().floatValue());
            this.color = color;
            check();
        }
    }

    public Vector2f getPoint() {
        return p;
    }

    public Vector2f getSize() {
        return size;
    }

    public int getColor() {
        return color;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(p, size, color);
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
        final LineData other = (LineData) obj;
        if (!Objects.equals(this.p, other.p)) {
            return false;
        }
        return Objects.equals(this.size, other.size);
    }

    @Override
    protected void applyUpdate(Geometry child) {
        TempVars vars = TempVars.get();
        Vector3f[] vec = vars.tri;
        
        vec[0].set(p.x, p.y, 0.0f);
        vec[1].set(size.x, size.y, 0.0f);
        render.render(child, color, vec);
        vars.release();
    }
}
