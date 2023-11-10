/* Copyright (c) 2009-2023 jMonkeyEngine.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jme3gl2.scene.tile;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;

/**
 * Clase de conveniencia para implementar la interfaz {@link Spritesheet}. 
 * @author wil
 * @version 1.0.1
 * @since 2.0.0
 */
public abstract class SpritesheetAdapter implements Spritesheet {

    /**
     * (non-JavaDoc)
     * @see Spritesheet#render(jMe3GL2.scene.tile.TileMap, jMe3GL2.scene.tile.Tile, com.jme3.asset.AssetManager) 
     * 
     * @param tileMap Tile-Map
     * @param tile Tile
     * @param assetManager Asset-Manager
     * @return Modelo.
     */
    @Override
    public Geometry render(TileMap tileMap, Tile tile, AssetManager assetManager) { return null; }

    /**
     * (non-JavaDoc)
     * @see Spritesheet#update(jMe3GL2.scene.tile.TileMap, jMe3GL2.scene.tile.Tile, com.jme3.asset.AssetManager, com.jme3.scene.Geometry) 
     * 
     * @param tileMap Tile-Map
     * @param tile Tile
     * @param assetManager Asset-Manager
     * @param geom Geometry
     */
    @Override
    public void update(TileMap tileMap, Tile tile, AssetManager assetManager, Geometry geom) { }
}
