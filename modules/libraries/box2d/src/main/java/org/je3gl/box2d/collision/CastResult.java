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
package org.je3gl.box2d.collision;

import com.jme3.math.Vector2f;
import org.box2d.jni.b2BodyId;

/**
 *
 * @author wil
 */
public class CastResult {

    private Vector2f point;
    private Vector2f normal;
    private b2BodyId bodyId;
    private float fraction;
    private boolean hit;

    public CastResult() {
        this(new Vector2f(), new Vector2f(), null, 0f, false);
    }

    public CastResult(Vector2f point, Vector2f normal, b2BodyId bodyId, float fraction, boolean hit) {
        this.point = point;
        this.normal = normal;
        this.bodyId = bodyId;
        this.fraction = fraction;
        this.hit = hit;
    }
    
    public void reset() {
        if (point != null) {
            point.zero();
        }
        if (normal != null) {
            normal.zero();
        }
        if (bodyId != null) {
            bodyId.close();
        }
        fraction = 0;
        hit = false;
    }

    public void setPoint(Vector2f point) {
        this.point = point;
    }

    public void setNormal(Vector2f normal) {
        this.normal = normal;
    }

    public void setBodyId(b2BodyId bodyId) {
        this.bodyId = bodyId;
    }

    public void setFraction(float fraction) {
        this.fraction = fraction;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public Vector2f getPoint() {
        return point;
    }

    public Vector2f getNormal() {
        return normal;
    }

    public b2BodyId getBodyId() {
        return bodyId;
    }

    public float getFraction() {
        return fraction;
    }

    public boolean isHit() {
        return hit;
    }
}
