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

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import java.nio.FloatBuffer;

/**
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class Point2D extends Mesh {

    public Point2D() {
        this(new Vector3f(), 1f);
    }
    
    public Point2D(Vector3f p, float size) {
        setMode(Mode.Points);
        Point2D.this.updateGeometry(p, size);
    }
    
    protected void updateGeometry(Vector3f p, float size) {
        setBuffer(VertexBuffer.Type.Position, 3, new float[]{
            p.x, p.y, p.z
        });
        setBuffer(VertexBuffer.Type.Size, 1, new float[]{
            size
        });
        updateBound();
    }
    
    public void updatePoints(Vector3f p, float size) {
        VertexBuffer posBuf = getBuffer(VertexBuffer.Type.Position);
        VertexBuffer sizeBuf = getBuffer(VertexBuffer.Type.Size);
        
        FloatBuffer fb = (FloatBuffer) posBuf.getData();
        FloatBuffer zb = (FloatBuffer) sizeBuf.getData();
        
        fb.rewind();
        zb.rewind();
        
        fb.put(p.x).put(p.y).put(p.z);
        zb.put(size);
        
        posBuf.updateData(fb);
        sizeBuf.updateData(zb);
        
        updateBound();
    }
}
