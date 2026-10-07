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

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;

import java.io.IOException;

import org.box2d.jni.b2BodyId;
import org.box2d.jni.b2Polygon;
import org.box2d.jni.b2ShapeDef;
import org.box2d.jni.b2ShapeId;

import org.box2d.jni.system.Struct;
import static org.box2d.jni.include.Types.*;

/**
 * Class responsible for encapsulating a physical form so that it can be safely
 * exported and imported.
 *
 * @param <E> shape
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
@SuppressWarnings("unchecked")
public class CollisionShape<E extends Struct<E>> implements Savable, Cloneable, AutoCloseable {

    /** shape. */
    private E shape;
    
    private b2ShapeDef shapeDef;
    
    private ShapeCreator<E> creator;

    /**
     * Constructor.
     */
    public CollisionShape() {
    }

    /**
     * Generates a new <code>CollisionShape</code> to manage a physical form.
     *
     * @param shape shape
     */
    public CollisionShape(E shape) {
        this(shape, b2DefaultShapeDef(b2ShapeDef.malloc()));
    }

    /**
     * Generates a new <code>CollisionShape</code> to manage a physical form.
     *
     * @param shape shape
     * @param shapeDef shape def
     */
    public CollisionShape(E shape, b2ShapeDef shapeDef) {
        this.shape = shape;
        this.shapeDef = shapeDef;
        if (shape instanceof b2Polygon) {
            creator = (ShapeCreator<E>) ShapeCreator.POLYGON;
        }
    }

    /**
     * Returns physical shape
     *
     * @return shape
     */
    public E getShape() {
        return shape;
    }

    public b2ShapeDef getShapeDef() {
        return shapeDef;
    }
    
    public b2ShapeId createNewShapeId(b2BodyId bodyId) {
        return creator.create(bodyId, shapeDef, shape);
    }

    public ShapeCreator<E> getCreator() {
        return creator;
    }

    @Override
    public void close() {
        if (shape != null) {
            shape.close();
        }
        if (shapeDef != null) {
            shapeDef.close();
        }
    }

    @Override
    public void write(JmeExporter ex) throws IOException {

    }

    @Override
    public void read(JmeImporter im) throws IOException {

    }
}
