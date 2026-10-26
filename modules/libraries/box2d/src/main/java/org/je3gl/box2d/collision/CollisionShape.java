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
import org.box2d.jni.b2Capsule;
import org.box2d.jni.b2ChainSegment;
import org.box2d.jni.b2Circle;
import org.box2d.jni.b2Polygon;
import org.box2d.jni.b2Segment;
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

    /** the shape. */
    private E shape;
    /** The default properties of the physical shape being managed. */
    private b2ShapeDef shapeDef;

    /**
     * A physical shape manager; this object is responsible for creating the
     * shape, adding it to the world, and associating it with the body.
     */
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
        if (shape instanceof b2Circle){
            creator = (ShapeCreator<E>) ShapeCreator.CIRCLE;
        }
        else if (shape instanceof b2Capsule) {
            creator = (ShapeCreator<E>) ShapeCreator.CAPSULE;
        }
        else if (shape instanceof b2Segment) {
            creator = (ShapeCreator<E>) ShapeCreator.SEGMENT;
        }
        else if (shape instanceof b2Polygon) {
            creator = (ShapeCreator<E>) ShapeCreator.POLYGON;
        }
        else if (shape instanceof b2ChainSegment) {
            creator = (ShapeCreator<E>) ShapeCreator.SEGMENT;
        } else {
            throw new IllegalArgumentException(
                    String.valueOf(shape)
            );
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

    /**
     * Returns {@code b2ShapeDef}
     *
     * @return b2ShapeDef
     */
    public b2ShapeDef getShapeDef() {
        return shapeDef;
    }

    /**
     * Create the collision shape (physical shape) for the body, based on the
     * data in this wrapper.
     *
     * @param bodyId b2BodyId
     * @return b2ShapeId
     */
    public b2ShapeId createNewShapeId(b2BodyId bodyId) {
        return creator.create(bodyId, shapeDef, shape);
    }

    /**
     * Returns the physical shape manager
     *
     * @return ShapeCreator
     */
    public ShapeCreator<E> getCreator() {
        return creator;
    }

    /**
     * Free native objects
     */
    @Override
    public void close() {
        if (shape != null) {
            shape.close();
        }
        if (shapeDef != null) {
            shapeDef.close();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @param ex JmeExporter
     * @throws IOException IOException
     */
    @Override
    public void write(JmeExporter ex) throws IOException {

    }

    /**
     * (non-Javadoc)
     *
     * @param im JmeImporter
     * @throws IOException IOException
     */
    @Override
    public void read(JmeImporter im) throws IOException {

    }
}
