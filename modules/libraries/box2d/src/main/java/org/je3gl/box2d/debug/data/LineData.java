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
import java.util.Objects;
import org.box2d.jni.b2Pos;

/**
 *
 * @author wil
 */
public final class LineData implements Cloneable {
    
    private Vector2f p;
    private Vector2f size;
    
    private boolean handled;
    private int color;

    public LineData() {
        this.p = new Vector2f();
        this.size = new Vector2f();
        this.handled = true;
    }

    @Override
    public LineData clone() {
        try {
            LineData clon = (LineData) super.clone();
            clon.p       = this.p.clone();
            clon.size    = this.size.clone();
            clon.color   = color;
            clon.handled = handled;
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
    
    public void update(b2Pos p, b2Pos size, int color) {
        this.p.set(p.x().floatValue(), p.y().floatValue());
        this.size.set(size.x().floatValue(), size.y().floatValue());
        this.color = color;
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

    public boolean isHandled() {
        return handled;
    }

    public void handled() {
        handled = true;
    }
    
    public void kill() {
        handled = false;
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
}
