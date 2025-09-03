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
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * Abstract class <code>AbstractShape2D</code> in charge of implementing the basis
 * for generating primitive shapes.
 * <p>
 * <b>Lines</b> are used to generate the shapes.
 * 
 * @author wil
 * @version 1.0.5
 * @since 2.5.0
 */
public abstract class AbstractShape2D extends Mesh implements Savable, Cloneable {
    
    /**Arrangement of the mesh vertices. */
    protected Vector3f[] vertices;
    
    /**
     * Default internal constructor.
     */
    protected AbstractShape2D() {
        AbstractShape2D.this.setMode(Mode.LineLoop);
    }

    /**
     * (non-Javadoc)
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
    protected void updateGeometry(final Vector3f[] vertices) {
        if (vertices == null ) {
            throw new NullPointerException("vertices is null.");
        }
        
        this.vertices = vertices;
        this.setBuffer(VertexBuffer.Type.Position, 3,
                BufferUtils.createVector3Buffer(getFloatBuffer(VertexBuffer.Type.Position), this.vertices.length));

        this.setBuffer(VertexBuffer.Type.Index, 1, 
                BufferUtils.createShortBuffer(getShortBuffer(VertexBuffer.Type.Index), this.vertices.length));

        final FloatBuffer pb = getFloatBuffer(VertexBuffer.Type.Position);
        final IndexBuffer ib = getIndexBuffer();

        for (int i = 0; i < this.vertices.length; i++) {
            final Vector3f vertice = vertices[i];
            
            pb.put(vertice.getX()).put(vertice.getY()).put(vertice.getZ());
            ib.put(i, i);
        }
        
        updateBound();
    }

    /**
     * (non-Javadoc)
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
