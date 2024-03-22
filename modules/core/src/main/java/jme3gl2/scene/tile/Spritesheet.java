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
 * Un <code>Spritesheet</code> es una interfaz encargado de gestionar la creación
 * de modelos 2D para un {@link TileMap}.
 * <p>
 * Cada {@link Tile} que se agrega al nodo escena cuenta con una lista de 
 * propiedades con que personalizar el modelo 2D que se genere.
 * </p>
 * 
 * @author wil
 * @version 1.0.1
 * 
 * @since 2.0.0
 */
public interface Spritesheet {
    
    /**
     * Método encargado de crear un modelo en una geometría para agregarlo en
     * una escena. Cada {@link TileMap} que se genere utiliza este método para
     * crear su nodos hijos.
     * 
     * @param tileMap nodo padre de mapas.
     * @param tile información del azulejo a crear.
     * @param assetManager admnistrador de recursos
     * @return modelo 2d.
     */
    public Geometry render(TileMap tileMap, Tile tile, AssetManager assetManager);
    
    /**
     * Método encargado de actualizar un modelo con nuevas propiedades.
     * @param tileMap nodo padre de mapas.
     * @param tile información del azulejo a crear.
     * @param assetManager admnistrador de recursos
     * @param geom geometrya a modificar.
     */
    public void update(TileMap tileMap, Tile tile, AssetManager assetManager, Geometry geom);
}
