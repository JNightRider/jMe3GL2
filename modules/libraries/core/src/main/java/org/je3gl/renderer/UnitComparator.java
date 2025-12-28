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
package org.je3gl.renderer;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.GeometryComparator;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;

/**
 * This class is responsible for implementing the geometry comparator for 2D games,
 * so that the models do not overlap each other and respect the order or distance
 * between them defined by the users.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.1.0
 */
public final class UnitComparator implements GeometryComparator {
    
    /**
     * Defines the type of position to use to determine the distances of each 
     * geometry.
     */
    public static enum UType {
        /**
         * Use the world (global) position
         */
        World,
        /**
         * Use local or relative position
         */
        Local
    }
    
    /** Unit vector to obtain the axis to respond to for the comparison. */
    private final Vector3f unit;
    /** The type of positioning to which the comparator will be applied. */
    private final UType type;
    /** layers */
    private RenderQueue.Bucket[] layers;
    
    /**
     * Generate a new unitary comparison with an axis {@code UnitComparator}.
     *
     * @param unit a unit vector
     * @param type the type to use
     */
    public UnitComparator(Vector3f unit, UType type) {
        this.unit = unit.clone();
        this.type = type;
    }

    /**
     * Set the application layers for this comparator
     * @param layers layers
     */
    public void setLayers(RenderQueue.Bucket[] layers) {
        this.layers = layers;
    }

    /**
     * This method has no effect on comparisons.
     *
     * @param camera the camera
     */
    @Override
    public void setCamera(Camera camera) { }

    /**
     * Compares two geometries to determine positioning in 3D or 2.5D space
     * 
     * @param a geometry to compare
     * @param b geometry with whom to compare
     * @return int
     */
    @Override
    public int compare(Geometry a, Geometry b) {
        Vector3f va = getGeometryTranslation(a).mult(unit),
                 vb = getGeometryTranslation(b).mult(unit);
        
        float na = getVectValue(va),
              nb = getVectValue(vb);
        return Float.compare(na, nb);
    }

    /**
     * Returns all layers
     * @return layers
     */
    public RenderQueue.Bucket[] getLayers() {
        return layers;
    }
    
    /**
     * Add the components of a vector to obtain the result. Only one component 
     * is different from 0.
     *
     * @param vec the vector
     * @return value|float
     */
    private float getVectValue(Vector3f vec) {
        return vec.x + vec.y + vec.z;
    }
    
    /**
     * Returns the position of the geometry according to the defined type.
     * 
     * @param geometry the geometry
     * @return position
     */
    private Vector3f getGeometryTranslation(Geometry geometry) {
        if (geometry == null) {
            throw new NullPointerException("Geometry is null");
        }        
        return type == UType.World 
                ? geometry.getWorldTranslation() 
                : geometry.getLocalTranslation();
    }
}
