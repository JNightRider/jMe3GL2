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
package jme3gl2.scene.debug;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * Class <code>Capsule2D</code> in charge of generating a capsule shape.
 * @author wil
 * @version 1.0.5 
 * @since 2.5.0
 */
public class Capsule2D extends AbstractShape2D {
    
    /** Default counter for capsule edges / half circles. */
    public static final int COUNT = 12;

    /**
     * Class constructor <code>GLCapsule</code> where the shape of a capsule is
     * generated.
     * 
     * @param count number of 'pins' for the circular borders, the higher the
     *              number the better the semicircle. Be careful as the higher the number the
     *               more vertices are used
     * @param width width of the form
     * @param height height of the form
     */
    public Capsule2D(int count, float width, float height) {
        Capsule2D.this.updateGeometry(count, width, height);
    }

    /**
     * Method in charge of updating the geometries of this mesh.
     * @param count number of 'pins' for the circular borders, the higher the
     *              number the better the semicircle. Be careful as the higher the number the
     *              more vertices are used
     * @param width width of the form
     * @param height height of the form
     */
    public void updateGeometry(int count, float width, float height) {
        // Calculate the angular increment
        final float pin = FastMath.PI / (count + 1);
        // 4 straight verts plus 2 * half-round verts
        final Vector3f[] myVertices = new Vector3f[4 + 2 * count];

        final float c = FastMath.cos(pin);
        final float s = FastMath.sin(pin);
        float t;

        // Get the major and minor axes
        float major = width;
        float minor = height;
        boolean vertical = false;
        if (width < height) {
            major = height;
            minor = width;
            vertical = true;
        }

        // Obtain the radius of the minor axis
        float radius = minor * 0.5f;

        // Calculating the compensation x/y
        float offset = major * 0.5f - radius;
        float ox = 0;
        float oy = 0;
        if (vertical) {
            // Aligned with y
            oy = offset;
        } else {
            // Aligned with x
            ox = offset;
        }

        int n = 0;
        float deep = 0;

        // Right cap
        float ao = vertical ? 0 : FastMath.PI * 0.5f;
        float x = radius * FastMath.cos(pin - ao);
        float y = radius * FastMath.sin(pin - ao);
        for (int i = 0; i < count; i++) {
            myVertices[n++] = new Vector3f(x + ox, y + oy, deep);

            // Apply the rotation matrix
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }

        // Add top/left vertices
        if (vertical) {
            myVertices[n++] = new Vector3f(-radius, oy, deep);
            myVertices[n++] = new Vector3f(-radius, -oy, deep);
        } else {
            myVertices[n++] = new Vector3f(ox, radius, deep);
            myVertices[n++] = new Vector3f(-ox, radius, deep);
        }

        // Left cap
        ao = vertical ? FastMath.PI : FastMath.PI * 0.5f;
        x = radius * FastMath.cos(pin + ao);
        y = radius * FastMath.sin(pin + ao);
        for (int i = 0; i < count; i++) {
            myVertices[n++] = new Vector3f(x - ox, y - oy, deep);

            // Apply the rotation matrix
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }

        // Add bottom/right vertices
        if (vertical) {
            myVertices[n++] = new Vector3f(radius, -oy, deep);
            myVertices[n++] = new Vector3f(radius, oy, deep);
        } else {
            myVertices[n++] = new Vector3f(-ox, -radius, deep);
            myVertices[n++] = new Vector3f(ox, -radius, deep);
        }
        
        updateGeometry(myVertices);
    }
}