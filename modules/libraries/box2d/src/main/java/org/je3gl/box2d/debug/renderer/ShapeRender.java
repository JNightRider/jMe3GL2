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
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.je3gl.box2d.debug.ObjectPool;
import org.je3gl.utilities.ColorUtilities;

/**
 * A manager for rendering immediate-mode shapes in Box2D.
 *
 * @param <T> type
 * @param <POOL> object pool
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public abstract class ShapeRender<T, POOL extends ObjectPool<T>> implements AutoCloseable {
    /** assets - jme3 */
    protected final AssetManager assetManager;
    /** All objects to be drawn temporarily. */
    private final List<T> cache;
    /** ObjectPool */
    private final POOL pool;
 
    /**
     * Constructor
     *
     * @param assetManager AssetManager
     * @param pool ObjectPool
     */
    public ShapeRender(AssetManager assetManager, POOL pool) {
        this.assetManager = assetManager;
        this.cache = Collections.synchronizedList(new ArrayList<>());
        this.pool = pool;
    }

    /**
     * Method in charge of creating the materials to be used by the
     * {@code Spatial} for the debugging of the physical bodies.
     *
     * @param c color of the material
     * @param alpha the alpha component
     * @param fill the material colors the entire surface
     * @return generated material
     */
    protected Material createMat(int c, float alpha, boolean fill) {
        return createMat(ColorUtilities.fromIntRGBA(c, alpha), fill);
    }

    /**
     * Method in charge of creating the materials to be used by the
     * {@code Spatial} for the debugging of the physical bodies.
     *
     * @param color color of the material
     * @param fill the material colors the entire surface
     * @return generated material
     */
    protected Material createMat(ColorRGBA color, boolean fill) {
        final Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        if (fill) {
            mat.getAdditionalRenderState().setWireframe(false);
        } else {
            mat.getAdditionalRenderState().setWireframe(true);
            mat.getAdditionalRenderState().setLineWidth(2);
        }
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setColor("Color", color);
        return mat;
    }

    /**
     * Release any additional objects that may appear.
     */
    protected abstract void free();

    /**
     * Get a new instance of the object group.
     *
     * @return Object
     */
    protected T pool() {
        T value = pool.takePush();
        cache.add(value);
        return value;
    }

    /*(non-Javadoc)
     */
    @Override
    public void close() {
        for (int i = 0; i < cache.size(); i++) {
            T value = cache.get(i);
            pool.takePop(value);
            cache.remove(i);
            i--;
        }
        free();
    }
}
