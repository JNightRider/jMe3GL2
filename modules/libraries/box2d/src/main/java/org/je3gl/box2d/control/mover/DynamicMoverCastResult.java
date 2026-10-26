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
import org.box2d.jni.b2BodyId;

import static org.box2d.jni.system.Pointer.*;

/**
 * A class responsible for storing the result of a {@code b2CastResultCallbackFcnI}.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class DynamicMoverCastResult {
    
    /**
     * Point of the resulting lightning strike,
     */
    public Vector3f point;
    /** The normal of the impacted object. */
    public Vector3f normal;
    /**The detected object. */
    public b2BodyId bodyId;
    /** the {@code userMaterialId} */
    public long userMaterialId;;
    /** fraction value */
    public float fraction;
    /** An indicator of whether this {@link DynamicMoverCastResult} is valid or not. */
    public boolean hit;

    public DynamicMoverCastResult() {
        this(new Vector3f(), new Vector3f(), null, NULL, 0.0f, false);
    }

    /**
     * Constructor.
     *
     * @param point Vector3f
     * @param normal Vector3f
     * @param bodyId b2BodyId
     * @param userMaterialId long
     * @param fraction float
     * @param hit boolean
     */
    public DynamicMoverCastResult(Vector3f point, Vector3f normal, b2BodyId bodyId, long userMaterialId, float fraction, boolean hit) {
        this.point = point;
        this.normal = normal;
        this.bodyId = bodyId;
        this.userMaterialId = userMaterialId;
        this.fraction = fraction;
        this.hit = hit;
    }

    /**
     * Reset and free native objects if possible
     */
    public void free() {
        if (bodyId != null)
            bodyId.close();

        point.zero();
        normal.zero();        
        fraction = 0.0f;
        userMaterialId = NULL;
        hit = false;
    }
}
