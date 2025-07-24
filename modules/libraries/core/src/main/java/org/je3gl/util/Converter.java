/*
BSD 3-Clause License

Copyright (c) 2023-2025, Night Rider (Wilson)

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
package org.je3gl.util;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Vector3;

/**
 * Class <code>Converter</code> that is responsible for providing conversion methods 
 * between vectors and/or numbers (float &harr; double).
 * 
 * @author wil
 * @version 2.0.0
 * @since 1.0.0
 */
public final class Converter {
    
    /* Use {@link jme3gl2.util.Converter#allToVector3fValueOfJME3(org.dyn4j.geometry.Vector3[]) } */
    @Deprecated(since = "3.0.0")
    public static Vector3f[] toArrayVector3f(final Vector3[] vertices) { return allToVector3fValueOfJME3(vertices); }
    /* Use {@link jme3gl2.util.Converter#allToVector3fValueOfJME3(org.dyn4j.geometry.Vector2[]) } */
    @Deprecated(since = "3.0.0")
    public static Vector3f[] toArrayVector3f(final Vector2[] vertices) { return allToVector3fValueOfJME3(vertices); }
    /* Use {@link jme3gl2.util.Converter#toVector2fValueOfJME3(org.dyn4j.geometry.Vector2) } */
    @Deprecated(since = "3.0.0")
    public static Vector2f toVector2f(Vector2 v) { return toVector2fValueOfJME3(v); }
    /* Use {@link jme3gl2.util.Converter#toVector3fValueOfJME3(org.dyn4j.geometry.Vector3) } */
    @Deprecated(since = "3.0.0")
    public static Vector3f toVector3f(Vector3 v) { return toVector3fValueOfJME3(v); }
    /* Use {@link jme3gl2.util.Converter#toVector3fValueOfJME3(org.dyn4j.geometry.Vector2) } */
    @Deprecated(since = "3.0.0")
    public static Vector3f toVector3f(Vector2 v) { return toVector3fValueOfJME3(v); }
    /* Use {@link jme3gl2.util.Converter#toVector2ValueOfDyn4j(com.jme3.math.Vector2f) } */
    @Deprecated(since = "3.0.0")
    public static Vector2 toVector2(Vector2f v) { return toVector2ValueOfDyn4j(v); }
    /* Use {@link jme3gl2.util.Converter#toVector2ValueOfDyn4j(com.jme3.math.Vector3f) } */
    @Deprecated(since = "3.0.0")
    public static Vector2 toVector2(Vector3f v) { return toVector2ValueOfDyn4j(v); }
    /* Use {@link jme3gl2.util.Converter#toVector3ValueOfDyn4j(com.jme3.math.Vector3f) } */
    @Deprecated(since = "3.0.0")
    public static Vector3 toVector3(Vector3f v) { return toVector3ValueOfDyn4j(v); }
    
    /* Use {@link jme3gl2.util.Converter#toFloatValuej(double) } */
    @Deprecated(since = "3.0.0")
    public static float toFloat(double d) { return toFloatValue(d); }
    /* Use {@link jme3gl2.util.Converter#toDoubleValue(float) } */
    @Deprecated(since = "3.0.0")
    public static double toDouble(float f) { return toDoubleValue(f); }
    
    /**
     * Convert all {@code org.dyn4j.geometry.Vector2} to {@link com.jme3.math.Vector3f}.
     * @param val value
     * @return All {@link com.jme3.math.Vector3f} objects
     */
    public static Vector3f[] allToVector3fValueOfJME3(Vector2[] val) {
        Vector3f[] array = new Vector3f[val.length];
        for (int i = 0; i < val.length; i++) {
            array[i] = toVector3fValueOfJME3(val[i]);
        }
        return array;
    }
    
    /**
     * Convert all {@code org.dyn4j.geometry.Vector2} to {@link com.jme3.math.Vector2f}.
     * @param val value
     * @return All {@link com.jme3.math.Vector2f} objects
     */
    public static Vector2f[] allToVector2fValueOfJME3(Vector2[] val) {
        Vector2f[] array = new Vector2f[val.length];
        for (int i = 0; i < val.length; i++) {
            array[i] = toVector2fValueOfJME3(val[i]);
        }
        return array;
    }
    
    /**
     * Convert all {@code org.dyn4j.geometry.Vector3} to {@link com.jme3.math.Vector3f}.
     * @param val value
     * @return All {@link com.jme3.math.Vector3f} objects
     */
    public static Vector3f[] allToVector3fValueOfJME3(Vector3[] val) {
        Vector3f[] array = new Vector3f[val.length];
        for (int i = 0; i < val.length; i++) {
            array[i] = toVector3fValueOfJME3(val[i]);
        }
        return array;
    }
    
    /**
     * Convert all {@link com.jme3.math.Vector2f} to {@code org.dyn4j.geometry.Vector2}.
     * @param val value
     * @return All {@code org.dyn4j.geometry.Vector2} objects
     */
    public static Vector2[] allToVector2ValueOfDyn4j(Vector2f[] val) {
        Vector2[] array = new Vector2[val.length];
        for (int i = 0; i < val.length; i++) {
            array[i] = toVector2ValueOfDyn4j(val[i]);
        }
        return array;
    }
    
    /**
     * Convert all {@link com.jme3.math.Vector3f} to {@code org.dyn4j.geometry.Vector3}.
     * @param val value
     * @return All {@code org.dyn4j.geometry.Vector3} objects
     */
    public static Vector3[] allToVector3ValueOfDyn4j(Vector3f[] val) {
        Vector3[] array = new Vector3[val.length];
        for (int i = 0; i < val.length; i++) {
            array[i] = toVector3ValueOfDyn4j(val[i]);
        }
        return array;
    }
    
    /**
     * Convert a {@code org.dyn4j.geometry.Vector2} to a {@link com.jme3.math.Vector2f}.
     * @param val value
     * @return A {@link com.jme3.math.Vector2f} object
     */
    public static Vector2f toVector2fValueOfJME3(Vector2 val) {
        return new Vector2f(toFloatValue(val.x), toFloatValue(val.y));
    }
    
    /**
     * Convert a {@code org.dyn4j.geometry.Vector2} to a {@link com.jme3.math.Vector3f}.
     * @param val value
     * @return A {@link com.jme3.math.Vector3f} object
     */
    public static Vector3f toVector3fValueOfJME3(Vector2 val) {
        return new Vector3f(toFloatValue(val.x), toFloatValue(val.y), 0.0F);
    }
    
    /**
     * Convert a {@code org.dyn4j.geometry.Vector3} to a {@link com.jme3.math.Vector3f}.
     * @param val value
     * @return A {@link com.jme3.math.Vector3f} object
     */
    public static Vector3f toVector3fValueOfJME3(Vector3 val) {
        return new Vector3f(toFloatValue(val.x), toFloatValue(val.y), toFloatValue(val.z));
    }
    
    /**
     * Convert a {@link com.jme3.math.Vector2f} to a {@code org.dyn4j.geometry.Vector2}.
     * @param val value
     * @return A {@code org.dyn4j.geometry.Vector2} object
     */
    public static Vector2 toVector2ValueOfDyn4j(Vector2f val) {
        return new Vector2(val.x, val.y);
    }
    
    /**
     * Convert a {@link com.jme3.math.Vector3f} to a {@code org.dyn4j.geometry.Vector2}.
     * @param val value
     * @return A {@code org.dyn4j.geometry.Vector2} object
     */
    public static Vector2 toVector2ValueOfDyn4j(Vector3f val) {
        return new Vector2(val.x, val.y);
    }
    
    /**
     * Convert a {@link com.jme3.math.Vector3f} to a {@code org.dyn4j.geometry.Vector3}.
     * @param val value
     * @return A {@code org.dyn4j.geometry.Vector3} object
     */
    public static Vector3 toVector3ValueOfDyn4j(Vector3f val) {
        return new Vector3(val.x, val.y, val.z);
    }
    
    /**
     * Convert a <code>double</code> to a <code>float</code>.
     * @param val value
     * @return float
     */
    public static float toFloatValue(double val) {
        return Double.valueOf(val).floatValue();
    }
    
    /**
     * Convert a <code>float</code> to a <code>double</code>.
     * @param val value
     * @return double
     */
    public static double toDoubleValue(float val) {
        return Float.valueOf(val).doubleValue();
    }
    
    /** Private constructor. */
    private Converter() {}
}
