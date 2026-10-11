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

import com.jme3.export.*;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.je3gl.util.ObjectPool;
import org.je3gl.util.pool.BufferPoolHandler;

/**
 * Abstract class <code>AbstractShape2D</code> in charge of implementing the basis
 * for generating primitive shapes.
 * <p>
 * <b>Lines</b> are used to generate the shapes.
 * 
 * @author wil
 * @version 2.0.0
 * @since 2.5.0
 */
public abstract class AbstractShape2D extends Mesh implements Savable, Cloneable {

    /**
     * Arrangement of the mesh vertices.
     */
    protected Vector3f[] vertices = new Vector3f[0];

    protected Boolean fill = null;
    
    protected final ObjectPool<FloatBuffer> floatsPool = new ObjectPool<>(new BufferPoolHandler<>(BufferPoolHandler.FLOAT));
    protected final ObjectPool<ShortBuffer> shortsPool = new ObjectPool<>(new BufferPoolHandler<>(BufferPoolHandler.SHORT));

    /**
     * Default internal constructor.
     */
    protected AbstractShape2D() {
        AbstractShape2D.this.setMode(Mode.LineLoop);
        setBuffer(VertexBuffer.Type.Position, 3, floatsPool.takePush(3));
        setBuffer(VertexBuffer.Type.Index, 1, shortsPool.takePush(3));
        setDynamic();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.jme3.scene.Mesh#deepClone()
     * @return this
     */
    @Override
    public AbstractShape2D deepClone() {
        AbstractShape2D shape = (AbstractShape2D) super.deepClone();
        shape.vertices = vertices.clone();
        for (int i = 0; i < vertices.length; i++) {
            shape.vertices[i] = shape.vertices[i].clone();
        }
        return shape;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.jme3.scene.Mesh#clone()
     * @return this
     */
    @Override
    public AbstractShape2D clone() {
        AbstractShape2D shape = (AbstractShape2D) super.clone();
        shape.vertices = vertices.clone();
        return shape;
    }

    /**
     * Method responsible for updating the geometry of this mesh to generate the 
     * new coordinates of the <code>lines</code>.
     * 
     * @param vertices new arrangement of the vertices
     */
    @Deprecated
    protected void updateGeometry(final Vector3f[] vertices) {
        updatePoints(vertices);
    }
    
    /**
     * Method responsible for updating the geometry of this mesh to generate the 
     * new coordinates of the <code>lines</code>.
     * 
     * @param vertices new arrangement of the vertices
     * @return 
     */
    protected AbstractShape2D updatePoints(Vector3f... vertices) {
        // ----- Check vertices ----
        if (vertices == null) {
            throw new NullPointerException("The vertices cannot be null.");
        }
        
        // ----- Index buffer -----
        VertexBuffer buffPosition = getBuffer(VertexBuffer.Type.Position);
        FloatBuffer position = (FloatBuffer) buffPosition.getData();

        int newLimit = vertices.length * 3;
        if (position.limit() != newLimit) {
            floatsPool.takePop(position);
            position = floatsPool.takePush(newLimit);
        }

        position.rewind();
        for (Vector3f point : vertices) {
            position.put(point.x).put(point.y).put(point.z);
        }
        buffPosition.updateData(position);
        updateBound();
        fill(false);
        this.vertices = vertices;
        return this;
    }

    public boolean fill(boolean isFill) {
        // ------ Check fill ------
        if (fill != null && fill == isFill) {
            return false;
        }

        // ----- Index buffer -----
        VertexBuffer idxBuff = getBuffer(VertexBuffer.Type.Index);
        ShortBuffer index = (ShortBuffer) idxBuff.getData();

        int newLimit = isFill ? (vertices.length - 2) * 3 : vertices.length;
        if (index.limit() != newLimit) {
            shortsPool.takePop(index);
            index = shortsPool.takePush(newLimit);
        }

        index.rewind();
        if (isFill) {
            setMode(Mode.Triangles);
            for (short i = 1; i < vertices.length - 1; i++) {
                index.put((short) 0)
                     .put(i)
                     .put((short) (i + 1));
            }
            index.flip();
            idxBuff.resetObject();
            idxBuff.setupData(VertexBuffer.Usage.Dynamic, 3, VertexBuffer.Format.UnsignedShort, index);
        } else {
            setMode(Mode.LineLoop);
            for (short i = 0; i < vertices.length; i++) {
                index.put((short) i);
            }
            index.flip();
            idxBuff.resetObject();
            idxBuff.setupData(VertexBuffer.Usage.Dynamic, 1, VertexBuffer.Format.UnsignedShort, index);
        }
        updateCounts();
        this.fill = isFill;
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.jme3.export.Savable#read(com.jme3.export.JmeImporter)
     *
     * @param im {@link com.jme3.export.JmeImporter}
     * @throws IOException throws
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        final InputCapsule in = im.getCapsule(this);
        super.read(im);

        vertices = (Vector3f[]) 
                in.readSavableArray("vertices", vertices);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.jme3.export.Savable#write(com.jme3.export.JmeExporter)
     *
     * @param ex {@link com.jme3.export.JmeExporter}
     * @throws IOException throws
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        final OutputCapsule out = ex.getCapsule(this);
        super.write(ex);

        out.write(vertices, "vertices", null);
    }
}
