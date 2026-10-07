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
package org.je3gl.box2d.util;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import org.box2d.jni.b2Pos;
import org.box2d.jni.b2Vec2;

import org.je3gl.box2d.AxisType;

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
     * Verify a {@link Vector3f} to check its components according to the given
     * axis.
     *
     * @param in Vector3f
     * @param actual AxisType
     * @param out AxisType
     */
    public static void checkVec3XY0(Vector3f in, AxisType actual, AxisType out) {
        if (actual == out) {
            return;
        }

        float y = actual == AxisType.AXIS_XYO ? in.y : in.z;
        float z = actual == AxisType.AXIS_XYO ? in.z : in.y;
        if (out == AxisType.AXIS_XYO) {
            in.setY(y)
                    .setZ(z);
        } else {
            in.setY(z)
                    .setZ(y);
        }
    }

    /**
     * Convert a {@code Vector2f} to a {@link b2Pos}.
     *
     * @param vec2 value
     * @return A {@link b2Pos} object
     */
    public static b2Pos toB2Pos(Vector2f vec2) {
        return toB2Pos(vec2, b2Pos.malloc());
    }

    /**
     * Convert a {@code Vector2f} to a {@link b2Pos}.
     *
     * @param vec2 value
     * @param __result store
     * @return A {@link b2Pos} object
     */
    public static b2Pos toB2Pos(Vector2f vec2, b2Pos __result) {
        __result.set(vec2.x, vec2.y);
        return __result;
    }

    /**
     * Convert a {@code Vector3f} to a {@link b2Pos}.
     *
     * @param vec3 value
     * @return A {@link b2Pos} object
     */
    public static b2Pos toB2Pos(Vector3f vec3) {
        return toB2Pos(vec3, b2Pos.malloc());
    }
    
    public static b2Pos toB2Pos(Vector3f vec3, b2Pos __result) {
        __result.set(vec3.x, vec3.y);
        return __result;
    }

    public static b2Vec2 toB2Vec2(Vector3f vec3, b2Vec2 __result) {
        return __result.set(vec3.x, vec3.y);
    }
    
    public static b2Vec2 toB2Vec2(Vector2f vec2, b2Vec2 __result) {
        return __result.set(vec2.x, vec2.y);
    }
    
    /**
     * Convert a {@code Vector2f} to a {@link Vector3f}.
     *
     * @param vec2 value
     * @param axisType axis
     * @return A {@link Vector3f} object
     */
    public static Vector3f toVector3f(Vector2f vec2, AxisType axisType) {
        Vector3f vec3 = new Vector3f(vec2.x, vec2.y, 0);
        checkVec3XY0(vec3, AxisType.AXIS_XYO, axisType);
        return vec3;
    }

    /**
     * Convert a {@code b2Pos} to a {@link Vector3f}.
     *
     * @param val value
     * @param type axis
     * @return A {@link Vector3f} object
     */
    public static Vector3f toVector3f(b2Pos val, AxisType type) {
        switch (type) {
            case AXIS_XOY:
                return new Vector3f(val.x().floatValue(), 0.0F, val.y().floatValue());
            case AXIS_XYO:
            default:
                return new Vector3f(val.x().floatValue(), val.y().floatValue(), 0.0F);
        }
    }

    /**
     * Convert a {@code b2Pos} to a {@link Vector3f}.
     *
     * @param val value
     * @param type axis
     * @param __result store
     * @return A {@link Vector3f} object
     */
    public static Vector3f toVector3f(b2Pos val, AxisType type, Vector3f __result) {
        switch (type) {
            case AXIS_XOY:
                __result.set(val.x().floatValue(), 0.0F, val.y().floatValue());
                return __result;
            case AXIS_XYO:
            default:
                __result.set(val.x().floatValue(), val.y().floatValue(), 0.0F);
                return __result;
        }
    }

    /**
     * Returns the unit vector corresponding to the axis.
     *
     * @param type axis
     * @return A {@link Vector3f} object
     */
    public static Vector3f toUNIT3f(AxisType type) {
        switch (type) {
            case AXIS_XOY:
                return Vector3f.UNIT_Y;
            case AXIS_XYO:
            default:
                return Vector3f.UNIT_Z;
        }
    }

    /**
     * Convert a {@code b2Pos} to a {@link Vector2f}.
     *
     * @param val value
     * @return A {@link Vector2f} object
     */
    public static Vector2f toVector2f(b2Pos val) {
        return new Vector2f(val.x().floatValue(), val.y().floatValue());
    }

}
