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

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Abstract class <code>AbstractShape2D</code> in charge of implementing the
 * basis for generating primitive shapes.
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
    protected Vector3f[] vertices;

    /** mode */
    private ShapeMode2D mode2D;
    /** size buffer */
    private int count;

    /**
     * Default internal constructor.
     */
    protected AbstractShape2D() {
    }

    /**
     * constructor {@code AbstractPolygon2D}
     *
     * @param mode2D mode of this mesh {@link ShapeMode2D}
     * @param count vertext count
     */
    public AbstractShape2D(ShapeMode2D mode2D, int count) {
        AbstractShape2D.this.updateGeometry(mode2D, count);
    }

    /**
     * (non-Javadoc)
     *
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
     * @return this
     */
    @Override
    public AbstractShape2D clone() {
        AbstractShape2D shape = (AbstractShape2D) super.clone();
        shape.vertices = vertices.clone();
        return shape;
    }

    /**
     * Returns mesh mode.
     *
     * @return ShapeMode2D
     */
    public ShapeMode2D getMode2D() {
        return mode2D;
    }

    /**
     * Returns the number of vertices used.
     *
     * @return int
     */
    public int getCount() {
        return count;
    }

    /**
     * Updates the geometry buffers.
     *
     * @param mode2D mode of this mesh {@link ShapeMode2D}
     * @param count vertext count
     */
    public void updateGeometry(ShapeMode2D mode2D, int count) {
        setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(count * 3));
        if (mode2D == ShapeMode2D.FILLED) {
            setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createShortBuffer((count - 2) * 3));
        } else {
            setBuffer(VertexBuffer.Type.Index, 2, BufferUtils.createShortBuffer(count * 2));
        }
        setMode(mode2D.mode);
        this.mode2D = mode2D;
        this.count = count;
    }

    /**
     * Method responsible for updating the geometry of this mesh to generate the
     * new coordinates of the <code>lines</code>.
     *
     * @param vertices new arrangement of the vertices
     */
    public void updatePoints(Vector3f... vertices) {
        if (vertices == null) {
            throw new NullPointerException("The vertices cannot be null.");
        }
        if (count < vertices.length) {
            throw new IllegalStateException("The memory is insufficient for the new vertices.");
        }

        // ----- Index buffer -----
        VertexBuffer buffPosition = getBuffer(VertexBuffer.Type.Position);
        FloatBuffer position = (FloatBuffer) buffPosition.getData();

        position.rewind();
        for (Vector3f point : vertices) {
            position.put(point.x).put(point.y).put(point.z);
        }
        buffPosition.updateData(position);

        // ----- Index buffer -----
        VertexBuffer idxBuff = getBuffer(VertexBuffer.Type.Index);
        ShortBuffer index = (ShortBuffer) idxBuff.getData();
        index.rewind();

        if (mode2D == ShapeMode2D.FILLED) {
            for (short i = 1; i < vertices.length - 1; i++) {
                index.put((short) 0)
                        .put(i)
                        .put((short) (i + 1));
            }
        } else {
            for (int i = 0; i < vertices.length; i++) {
                int next = (i + 1) % vertices.length;
                index.put((short) i)
                        .put((short) next);
            }
        }
        index.flip();
        idxBuff.updateData(index);

        updateBound();
        updateCounts();
        this.vertices = vertices;
    }

    /**
     * (non-Javadoc)
     *
     * @param im JmeImporter
     * @throws IOException throws
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        Vector3f[] vertices0 = (Vector3f[]) in.readSavableArray("vertices", vertices);
        ShapeMode2D shapeMode2D = in.readEnum("mode2D", ShapeMode2D.class, ShapeMode2D.OUTLINE);
        int size = in.readInt("count2D", 0);

        updateGeometry(shapeMode2D, size);
        updatePoints(vertices0);
    }

    /**
     * (non-Javadoc)
     *
     * @param ex JmeExporter
     * @throws IOException throws
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        out.write(vertices, "vertices", null);
        out.write(mode2D, "mode2D", ShapeMode2D.OUTLINE);
        out.write(count, "count2D", 0);
    }
}
