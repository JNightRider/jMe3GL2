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
package org.je3gl.box2d.control.mover;

import com.jme3.math.Vector3f;
import org.box2d.jni.b2Pos;
import org.je3gl.box2d.AxisType;
import org.je3gl.box2d.util.Converter;

/**
 *
 * @author wil
 */
public class DynamicMoverProbe {

    public Vector3f origin;
    public Vector3f translation;
    public float fraction;
    public boolean hit;

    public DynamicMoverProbe() {
        this(new Vector3f(), new Vector3f(), 0.0f, false);
    }

    public DynamicMoverProbe(Vector3f origin, Vector3f translation, float fraction, boolean hit) {
        this.origin = origin;
        this.translation = translation;
        this.fraction = fraction;
        this.hit = hit;
    }
    
    public void set(b2Pos origin, b2Pos translation, float fraction, boolean hit, AxisType axis ) {
        this.origin = Converter.toVector3f(origin, axis, this.origin);
        this.translation = Converter.toVector3f(translation, axis, this.translation);
        this.fraction = fraction;
        this.hit = hit;
    }

    public void reset() {
        origin.zero();
        translation.zero();
        fraction = 0.0f;
        hit = false;
    }
}
