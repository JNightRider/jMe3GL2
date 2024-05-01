/* Copyright (c) 2009-2024 jMonkeyEngine.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.je3gl.scene.debug;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * Class <code>Circle2D</code> in charge of generating a circle shape.
 * @author wil
 * @version 1.0.5
 * @since 2.5.0
 */
public class Circle2D extends AbstractShape2D {
    
    /** 2 * PI constant */
    public static final float TWO_PI = 2.0F * FastMath.PI;
    
    /** Default counter to generate a circle. */
    public static final int COUNT = 54;
    
    /**
     * Class constructor <code>Circle2D</code> where the values will be set to
     * generate a circular shape.
     * 
     * @param count number of polygons
     * @param radius radius of the circle
     * @param theta theta value
     */
    public Circle2D(int count, float radius, float theta) {
        Circle2D.this.updateGeometry(count, radius, theta);
    }

    /**
     * Method in charge of updating the geometry of this circular mesh.
     * 
     * @param count number of vertices/polygons
     * @param radius radius of the circle
     * @param theta a theta for the circle
     */
    public void updateGeometry(int count, float radius, float theta) {
        // Calculate the angular increment
        final float pin = TWO_PI / (float)count;
        // Make sure the resulting output is an even number of vertices
        final Vector3f[] myVertices = new Vector3f[count];

        final float c = FastMath.cos(pin);
        final float s = FastMath.sin(pin);
        float t, deep = 0;

        float x = radius;
        float y = 0;
        // Initialize in theta if necessary
        if (theta != 0) {
            x = radius * FastMath.cos(theta);
            y = radius * FastMath.sin(theta);
        }

        for (int i = 0; i < count; i++) {
            myVertices[i] = new Vector3f(x, y, deep);

            // Apply the rotation matrix
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }
        setMode(Mode.Lines);
        updateGeometry(myVertices);
    }
}
