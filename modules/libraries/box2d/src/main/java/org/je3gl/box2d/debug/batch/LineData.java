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

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import org.je3gl.utilities.ColorUtilities;

/**
 *
 * @author wil
 */
public class LineData {
    
    private Vector3f p1;
    private Vector3f p2;
    private ColorRGBA color;

    public LineData() {
        this(new Vector3f(), new Vector3f(), new ColorRGBA());
    }

    public LineData(Vector3f p1, Vector3f p2, ColorRGBA color) {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
    }

    public void setP1(float x, float y, float z) {
        this.p1.set(x, y, z);
    }

    public void setP2(float x, float y, float z) {
        this.p2.set(x, y, z);
    }

    public void setColor(int rgba) {
        ColorUtilities.fromIntRGBA(rgba, 1.0f, color);
    }

    public Vector3f getP1() {
        return p1;
    }

    public Vector3f getP2() {
        return p2;
    }

    public ColorRGBA getColor() {
        return color;
    }
}
