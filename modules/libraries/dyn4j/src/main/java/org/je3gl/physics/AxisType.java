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
package org.je3gl.physics;

import com.jme3.math.Vector3f;

/**
 * An enumerated class that is responsible for defining the coordinate types when
 * applying physics to an object or model.
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.1.0
 */
public enum AxisType {
    
    /**
     * In physical space, the XYO axis is used, with positive X to the right
     * and positive Y upwards.
     * <pre><code>
     * y
     * |
     * |
     * |
     * 0------- X
     * </code></pre>
     */
    AXIS_XYO("xy", Vector3f.UNIT_Z, 1),
    
    /**
     * In physical space, the XYO axis is used, X positive to right and Y positive to down
     * <pre><code>
     * O------- X
     * |
     * |
     * |
     * Y
     * </code></pre>
     */
    AXIS_XOY("xz", Vector3f.UNIT_Y, -1);
    
    /** A name. */
    private final String name;
    /** The corresponding unit vector */
    private final Vector3f unit;
    /** A rotating steering multiplier. */
    private final int multiplier;

    /**
     * Constructor of class <code>AxisType</code>.
     * 
     * @param name a name
     * @param unit the corresponding unit vector
     * @param multiplier a rotating steering multiplier
     */
    private AxisType(String name, Vector3f unit, int multiplier) {
        this.name = name;
        this.unit = unit;
        this.multiplier = multiplier;
    }
    
    /**
     * Returns the corresponding unit vector.
     *
     * @return unit vector
     */
    public Vector3f getUnit() {
        return unit;
    }

    /**
     * Returns the corresponding multiplier.
     *
     * @return multiplier
     */
    public int getMultiplier() {
        return multiplier;
    }

    /**
     * (non-javadoc)
     * @see Object#toString() 
     * 
     * @return string
     */
    @Override
    public String toString() {
        return "Axis " + name + " ~ " + unit;
    }

    /**
     * Returns the default axis used in physical space
     * @return axis type
     */
    public static AxisType getDefault() {
        return AXIS_XYO;
    }
}
