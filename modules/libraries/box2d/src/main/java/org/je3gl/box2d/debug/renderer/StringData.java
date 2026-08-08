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
import com.jme3.math.Vector3f;
import java.util.Objects;
import org.je3gl.box2d.util.ObjectPool;

/**
 *
 * @author wil
 */
public class StringData {
    
    public static class Pool extends ObjectPool<StringData> {

        @Override
        protected StringData create() {
            return new StringData();
        }

        @Override
        protected boolean validate(StringData o) {
            return  o != null;
        }

        @Override
        protected void dead(StringData o) {
            
        }        
    }
    
    private ColorRGBA rgba;
    private String value;
    Vector3f position;

    public StringData() {
        this(null, null, new Vector3f());
    }

    public StringData(ColorRGBA rgba, String value, Vector3f position) {
        this.rgba = rgba;
        this.value = value;
        this.position = position;
    }

    public void setRGBA(ColorRGBA rgba) {
        this.rgba = rgba;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y, 0f);
    }

    public ColorRGBA getRGBA() {
        return rgba;
    }

    public String getValue() {
        return value;
    }

    public Vector3f getPosition() {
        return position;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.rgba);
        hash = 97 * hash + Objects.hashCode(this.value);
        hash = 97 * hash + Objects.hashCode(this.position);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StringData other = (StringData) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.rgba, other.rgba)) {
            return false;
        }
        return Objects.equals(this.position, other.position);
    }
}
