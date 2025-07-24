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
package org.je3gl.scene.tile;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;

/**
 * An <code>Spritesheet</code> is an interface responsible for managing the
 * creation of 2D models for a {@link TileMap}.
 * <p>
 * Each {@link Tile} added to the scene node has a list of properties to customize
 * the generated 2D model.
 * 
 * @author wil
 * @version 1.0.5
 * @since 2.0.0
 */
public interface Spritesheet {
    
    /**
     * Method in charge of creating a model in a geometry to add it in a scene.
     * Each {@link TileMap} that is generated uses this method to create its
     * child nodes.
     * 
     * @param tileMap parent map node
     * @param tile information of the tile to be created
     * @param assetManager resource manager
     * @return 2D model
     */
    public Geometry render(TileMap tileMap, Tile tile, AssetManager assetManager);
    
    /**
     * Method in charge of updating a model with new properties.
     * 
     * @param tileMap parent map node
     * @param tile information of the tile to be created
     * @param assetManager resource manager
     * @param geom geometry to be modified
     */
    public void update(TileMap tileMap, Tile tile, AssetManager assetManager, Geometry geom);
}
