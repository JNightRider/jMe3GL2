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
package org.je3gl.util.pool;

import com.jme3.util.BufferUtils;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.je3gl.util.Arg;
import org.je3gl.util.Checks;

/**
 * @param <BUFFER>
 * @author wil
 */
public final class BufferPoolHandler<BUFFER extends Buffer> implements PoolHandler<BUFFER> {

    public static final Instantiator<IntBuffer> INTEGER = (args) -> BufferUtils.createIntBuffer(args[0].getInt());
    public static final Instantiator<FloatBuffer> FLOAT = (args) -> BufferUtils.createFloatBuffer(args[0].getInt());
    public static final Instantiator<ShortBuffer> SHORT = (args) -> BufferUtils.createShortBuffer(args[0].getInt());

    private final Instantiator<BUFFER> handler;

    public BufferPoolHandler(Instantiator<BUFFER> handler) {
        this.handler = handler;
    }

    @Override
    public BUFFER create(Arg... args) {
        Checks.checkArgs(1, args);
        BUFFER buffer = handler.newInstance(args);
        return buffer;
    }

    @Override
    public boolean validate(BUFFER value, Arg... args) {
        if (value == null) {
            return false;
        }
        Checks.checkArgs(1, args);
        int limit = args[0].getInt();
        return limit <= value.limit();
    }

    @Override
    public void destroy(BUFFER value) {
        if (value != null) {
            BufferUtils.destroyDirectBuffer(value);
        }
    }
}
