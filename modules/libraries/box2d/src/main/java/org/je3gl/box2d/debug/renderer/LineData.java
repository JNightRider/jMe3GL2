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

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import org.je3gl.box2d.util.ObjectPool;

/**
 *
 * @author wil
 */
public class LineData {
    
    public static final class Pool extends ObjectPool<LineData> {

        @Override
        protected LineData create() {
             return new LineData();
        }

        @Override
        protected boolean validate(LineData o) {
            return o != null;
        }

        @Override
        protected void dead(LineData o) { }
    }

    private Vector3f p1;
    private Vector3f p2;
    private ColorRGBA rgba;

    public LineData() {
        this(new Vector3f(), new Vector3f(), null);
    }

    public LineData(Vector3f p1, Vector3f p2, ColorRGBA rgba) {
        this.p1 = p1;
        this.p2 = p2;
        this.rgba = rgba;
    }

    public void setP1(float x, float y) {
        p1.set(x, y, 0);
    }
    
    public void setP2(float x, float y) {
        p2.set(x, y, 0);
    }

    public void setRGBA(ColorRGBA rgba) {
        this.rgba = rgba;
    }

    public Vector3f getP1() {
        return p1;
    }

    public Vector3f getP2() {
        return p2;
    }

    public ColorRGBA getRGBA() {
        return rgba;
    }
}
