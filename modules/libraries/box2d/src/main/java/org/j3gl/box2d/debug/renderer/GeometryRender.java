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
package org.j3gl.box2d.debug.renderer;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;

import org.j3gl.box2d.debug.GeometryPool;
import org.je3gl.scene.debug.AbstractShape2D;
import org.je3gl.utilities.ColorUtilities;

/**
 *
 * @author wil
 */
public class GeometryRender extends ShapeRender<Geometry, GeometryPool> {

    public GeometryRender(AssetManager assetManager) {
        super(assetManager, new GeometryPool());
    }

    public <T> Geometry render(MeshRender<T> meshRender, T value, int color, boolean fill) {
        Geometry geom = pool();
        Material mat  = checkMaterial(geom, color, fill);
        geom.setQueueBucket(RenderQueue.Bucket.Translucent);        
        geom.setMaterial(mat);
        
        meshRender.render(geom, color, value);
        Mesh mesh = geom.getMesh();
        if (fill && (mesh instanceof AbstractShape2D)) {
           ((AbstractShape2D) mesh).fill(fill);
        }
        return geom;
    }
    
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
    
    @Override
    protected void free() { }
}
