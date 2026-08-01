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
import com.jme3.math.Vector2f;
import org.je3gl.box2d.util.ObjectPool;

/**
 *
 * @author wil
 */
public class PointData {
    
    public static class Pool extends ObjectPool<PointData> {

        @Override
        protected PointData create() {
            return new PointData();
        }

        @Override
        protected boolean validate(PointData o) {
            return o != null;
        }

        @Override
        protected void dead(PointData o) { }        
    }
    
    private Vector2f position;
    private float size;
    private ColorRGBA rgba;

    public PointData() {
        this(new Vector2f(), 0, null);
    }

    public PointData(Vector2f position, float size, ColorRGBA rgba) {
        this.position = position;
        this.size = size;
        this.rgba = rgba;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setRGBA(ColorRGBA rgba) {
        this.rgba = rgba;
    }

    public Vector2f getPosition() {
        return position;
    }

    public float getSize() {
        return size;
    }

    public ColorRGBA getRGBA() {
        return rgba;
    }
}
