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
package org.je3gl.box2d.debug.renderer;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import org.je3gl.box2d.util.ObjectPool;

/**
 *
 * @author wil
 */
public class CircleData {
    
    public static class Pool extends ObjectPool<CircleData> {

        @Override
        protected CircleData create() {
            return new CircleData();
        }

        @Override
        protected boolean validate(CircleData o) {
            return o != null;
        }

        @Override
        protected void dead(CircleData o) {
            
        }        
    }
    
    private Transform transform;
    private Vector3f center;
    private float radius;
    private ColorRGBA rgba;

    public CircleData() {
        this(new Transform(), new Vector3f(), 0, null);
    }

    public CircleData(Transform transform, Vector3f center, float radius, ColorRGBA rgba) {
        this.transform = transform;
        this.center = center;
        this.radius = radius;
        this.rgba = rgba;
    }

    public void setTransform(float x, float y, float angle) {
        transform.setTranslation(x, y, 0);
        transform.getRotation().fromAngleAxis(angle, Vector3f.UNIT_Z);
    }
    
    public void setCenter(float x, float y) {
        this.center.set(x, y, 0);
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setRGBA(ColorRGBA rgba) {
        this.rgba = rgba;
    }

    public Transform getTransform() {
        return transform;
    }

    public Vector3f getCenter() {
        return center;
    }

    public float getRadius() {
        return radius;
    }

    public ColorRGBA getRGBA() {
        return rgba;
    }
}
