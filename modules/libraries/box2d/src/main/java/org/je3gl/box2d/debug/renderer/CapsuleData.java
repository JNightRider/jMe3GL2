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
import com.jme3.math.FastMath;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import org.je3gl.box2d.util.ObjectPool;

/**
 *
 * @author wil
 */
public class CapsuleData {
    
    public static class Pool extends ObjectPool<CapsuleData> {

        @Override
        protected CapsuleData create() {
            return new CapsuleData();
        }

        @Override
        protected boolean validate(CapsuleData o) {
            return o != null;
        }

        @Override
        protected void dead(CapsuleData o) {
            
        }        
    }
    
    private ColorRGBA rgba;
    private Transform transform;
    private Vector2f size;

    public CapsuleData() {
        this(new Transform(), new Vector2f());
    }

    public CapsuleData(Transform transform, Vector2f size) {
        this.transform = transform;
        this.size = size;
    }

    public void setRGBA(ColorRGBA rgba) {
        this.rgba = rgba;
    }

    public void setTransform(float x, float y, float angle) {
        this.transform.setTranslation(x, y, 0);
        this.transform.getRotation().fromAngleAxis(angle + + FastMath.HALF_PI, Vector3f.UNIT_Z);
    }

    public void setSize(float w, float h) {
        this.size.set(w, h);
    }

    public ColorRGBA getRGBA() {
        return rgba;
    }

    public Transform getTransform() {
        return transform;
    }

    public Vector2f getSize() {
        return size;
    }

}
