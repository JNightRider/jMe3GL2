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
package org.j3gl.box2d.util;

import com.jme3.math.Vector3f;

import org.box2d.jni.b2Pos;

import org.j3gl.box2d.AxisType;

/**
 * Class <code>Converter</code> that is responsible for providing conversion
 * methods between vectors and/or numbers (float &harr; double).
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public final class Converter {

    /**
     * Convert a {@code org.box2d.jni.b2Pos} to a
     * {@link com.jme3.math.Vector3f}.
     *
     * @param val value
     * @param type axis
     * @return A {@link com.jme3.math.Vector3f} object
     */
    public static Vector3f toVector3fValueOfJME3(b2Pos val, AxisType type) {
        switch (type) {
            case AXIS_XOY:
                return new Vector3f(val.x().floatValue(), 0.0F, val.y().floatValue());
            case AXIS_XYO:
            default:
                return new Vector3f(val.x().floatValue(), val.y().floatValue(), 0.0F);
        }
    }
    
    public static Vector3f toUNIT3f(AxisType type) {
        switch (type) {
            case AXIS_XOY:
                return Vector3f.UNIT_Y;
            case AXIS_XYO:
            default:
                return Vector3f.UNIT_Z;
        }
    }
}
