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
package jMe3GL2.renderer;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;

import jMe3GL2.scene.TilesHeet;

/**
 * Interfaz <code>TileMapRender</code> encargado de renderizar las geometrías
 * de un {@link jMe3GL2.scene.TileMap}.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 1.5.0
 */
public interface TileMapRender {
    
    /**
     * Método encargado de crera la geometría del {@link jMe3GL2.scene.TileMap}
     * con las características personalizadas.
     * 
     * @param tile azulejo.
     * @param tilesHeet administrador azulejo.
     * @param assetManager administrador de recursos <code>jme</code>.
     * @return una nueva {@code Geometry}.
     */
    public Geometry render(TilesHeet.Tile tile, TilesHeet tilesHeet, AssetManager assetManager);
    
    /**
     * Método encargado de actualizar los datos de un {@code Geometry}.
     * @param geom geometría a gestionar.
     * @param tile azulejo.
     * @param tilesHeet administrador azulejo.
     * @param assetManager administrador de recursos <code>jme</code>.
     */
    public void update(Geometry geom, TilesHeet.Tile tile, TilesHeet tilesHeet, AssetManager assetManager);
}
