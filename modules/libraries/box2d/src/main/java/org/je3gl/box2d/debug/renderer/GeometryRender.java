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

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;

import org.je3gl.box2d.debug.GeometryPool;
import org.je3gl.scene.debug.AbstractShape2D;
import org.je3gl.utilities.ColorUtilities;

/**
 * An individual shape manager for the physics engine.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class GeometryRender extends ShapeRender<Geometry, GeometryPool> {

    /**
     * Constructor
     * @param assetManager AssetManager
     */
    public GeometryRender(AssetManager assetManager) {
        super(assetManager, new GeometryPool());
    }

    /**
     * Renders a new object based on the information provided by the physics engine.
     *
     * @param <T> type mesh
     *
     * @param meshRender mesh
     * @param value value for the mesh
     * @param color color rgb
     * @param fill boolean
     *
     * @return Node
     */
    public <T> Geometry render(MeshRender<T> meshRender, T value, int color, boolean fill) {
        Geometry geom = new Geometry();
        Material mat  = checkMaterial(geom, color, fill);
        geom.setQueueBucket(RenderQueue.Bucket.Translucent);
        geom.setMaterial(mat);
        geom.setUserData("box2d.jni#fill", fill);

        meshRender.render(geom, color, value);
        Mesh mesh = geom.getMesh();
        if (fill && (mesh instanceof AbstractShape2D)) {
            ((AbstractShape2D) mesh).fill(fill);
        }
        return geom;
    }

    /**
     * Check the material values ​​to change its state to solid (or not) and its color.
     *
     * @param geom Geometry
     * @param color int rgb
     * @param fill boolean
     *
     * @return Material
     */
    private Material checkMaterial(Geometry geom, int color, boolean fill) {
        Material mat = geom.getMaterial();
        float a = fill ? 0.1f : 1.0f;
        if (mat == null) {
            mat = createMat(color, a, fill);
        } else {
            mat.setColor("Color", ColorUtilities.fromIntRGBA(color, a));
        }
        mat.setFloat("PointSize", 1f);
        return mat;
    }

    /*(non-Javadoc)
     */
    @Override
    protected void free() {
    }
}
