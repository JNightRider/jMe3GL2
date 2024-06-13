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
package org.je3gl.utilities;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *  Utility class responsible for providing transformation methods.
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class TransformUtilities {
    
    /**
     * Rotate the input vector using the specified quaternion.
     * <p>
     * Unlike {@link com.jme3.math.Quaternion#mult(com.jme3.math.Vector3f,
     * com.jme3.math.Vector3f)}, this method doesn't assume the quaternion is
     * normalized. Instead, rotation is performed using a normalized version of
     * the quaternion.
     *
     * @param rotation the desired rotation (not null, not zero, unaffected)
     * @param input the vector to rotate (not null, finite, unaffected unless
     * it's {@code storeResult})     * 
     * @param storeResult storage for the result (modified if not null, may be
     * {@code input})
     * 
     * @return the rotated vector (either {@code storeResult} or a new instance)
     */
    public static Vector3f rotate(Quaternion rotation, Vector3f input, Vector3f storeResult) {
        Vector3f result = (storeResult == null) ? new Vector3f() : storeResult;

        float x = rotation.getX();
        float y = rotation.getY();
        float z = rotation.getZ();
        float w = rotation.getW();
        double lengthSquared = lengthSquared(rotation);
        if (lengthSquared < 0.9999998 || lengthSquared > 1.0000002) {
            double dScale = Math.sqrt(lengthSquared);
            x /= dScale;
            y /= dScale;
            z /= dScale;
            w /= dScale;
        }

        float x2 = x * x;
        float y2 = y * y;
        float z2 = z * z;
        float w2 = w * w;

        float vx = input.x;
        float vy = input.y;
        float vz = input.z;

        result.x = vx * (x2 - y2 - z2 + w2)
                + 2f * y * (x * vy + w * vz) + 2f * z * (x * vz - w * vy);
        result.y = vy * (y2 - z2 - x2 + w2)
                + 2f * z * (y * vz + w * vx) + 2f * x * (y * vx - w * vz);
        result.z = vz * (z2 - x2 - y2 + w2)
                + 2f * x * (z * vx + w * vy) + 2f * y * (z * vy - w * vx);

        return result;
    }
    
    /**
     * Return the squared length of the argument.
     * <p>
     * Unlike {@link com.jme3.math.Quaternion#norm()}, this method returns a
     * double-precision value for precise comparison of lengths.
     *
     * @param q the input value (not null, unaffected)
     * @return the squared length (&ge;0)
     */
    public static double lengthSquared(Quaternion q) {
        double xx = q.getX();
        double yy = q.getY();
        double zz = q.getZ();
        double ww = q.getW();
        return(xx * xx + yy * yy + zz * zz + ww * ww);
    }
}
