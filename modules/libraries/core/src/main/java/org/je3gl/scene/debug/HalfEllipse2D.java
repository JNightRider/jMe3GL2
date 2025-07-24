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
package org.je3gl.scene.debug;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * Class <code>HalfEllipse2D</code> in charge of generating an half-elliptical shape.
 * @author wil
 * @version 1.0.5
 * @since 2.5.0
 */
public class HalfEllipse2D extends AbstractShape2D {
    
    /** Minimum amount of polygons for a decent ellipse. */
    public static final int COUNT = 15;

    /**
     * Class constructor <code>HalfEllipse</code> to generate a '2D' half-ellipse
     * shape with a depth set for all/or the vertices.
     * 
     * @param count number of vertices/polygons to use
     * @param width width of the ellipse
     * @param height height of the ellipse
     */
    public HalfEllipse2D(int count, float width, float height) {
        HalfEllipse2D.this.updateGeometry(count, width, height);
    }

    /**
     * Method in charge of updating the geometries of this mesh.
     * 
     * @param count number of vertices/polygons to use
     * @param width width of the ellipse
     * @param height height of the ellipse
     */
    public void updateGeometry(int count, float width, float height) {
        final float a = width * 0.5f;
        final float b = height * 0.5f;
        float deep = 0;

        // Calculate the angular increment
        final float inc = FastMath.PI / (count + 1);
        // Make sure that the resulting output is an even number of vertices
        final Vector3f[] myVertices = new Vector3f[count + 2];

        // Set the initial and final vertices
        myVertices[0] = new Vector3f(a, 0, deep);
        myVertices[count + 1] = new Vector3f(-a, 0, deep);
        
        // Use parametric equations:
        // x = a * cos(t)
        // y = b * sin(t)
        for (int i = 1; i < count + 1; i++) {
            final float t = inc * i;
            
            // Since the bottom side of the ellipse is the same as the top side,
            // with only a y negated, let's save some time by creating the bottom
            // side at the same time.
            final float x = a * FastMath.cos(t);
            final float y = b * FastMath.sin(t);
            myVertices[i] = new Vector3f(x, y, deep);
        }
        this.updateGeometry(myVertices);
    }
}
