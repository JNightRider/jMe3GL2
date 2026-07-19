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
package org.je3gl.box2d.debug;

import com.jme3.util.BufferUtils;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author wil
 */
public class BufferPool extends ObjectPool<Buffer> {

    private int capacity = 10;
    private Class type = FloatBuffer.class;

    public BufferPool() {
    }
    
    public BufferPool capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }
    
    public <T extends Buffer> BufferPool type(Class<T> type) {
        this.type = type;
        return this;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected Buffer create() {
        if (type.isAssignableFrom(ShortBuffer.class)) {
            return BufferUtils.createShortBuffer(capacity);
        } else if (type.isAssignableFrom(FloatBuffer.class)) {
            return BufferUtils.createFloatBuffer(capacity);
        } else if (type.isAssignableFrom(IntBuffer.class)) {
            return BufferUtils.createIntBuffer(capacity);
        } else if (type.isAssignableFrom(ByteBuffer.class)) {
            return  BufferUtils.createByteBuffer(capacity);
        }
        throw new UnsupportedOperationException("buffer: " + type);
    }

    @Override
    protected boolean validate(Buffer o) {
        if (o == null) {
            return false;
        }
        return capacity <= o.capacity();
    }

    @Override
    protected void dead(Buffer o) {
        BufferUtils.destroyDirectBuffer(o);
    }
}
