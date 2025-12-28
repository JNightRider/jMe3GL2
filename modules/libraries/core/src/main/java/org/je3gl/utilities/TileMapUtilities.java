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
package org.je3gl.utilities;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;

import java.util.UUID;

import org.je3gl.scene.tile.Properties;
import org.je3gl.scene.tile.Tile;
import org.je3gl.scene.tile.TileMap;
import org.je3gl.scene.tile.TilePhysicsSystem;
import org.je3gl.scene.tile.Tilesheet;

/**
 * Utility class for loading, managing and modifying <code>TileMap</code> or
 * <code>Tile</code>.
 * 
 * @author wil
 * @version 1.0.5
 * @since 2.5.0
 */
public final class TileMapUtilities {

    /**
     * Returns a UUID to be used as ID, for objects <code>TileMap</code> and
     * <code>Tile</code>.
     * 
     * @return UUID string
     */
    public static final String gl2GetStrigRandomUUID() {
        return String.valueOf(UUID.randomUUID());
    }
    
    /**
     * Returns a <code>TileMap</code> stored with the initialized properties.
     * 
     * @param path the path of the texture containing the sprites inside the classpath
     * @param cs number of images per column
     * @param rs number of images per row
     * @param tilesheet the {@code Tilesheet}
     * @param assetManager asset manager
     * @return TileMap
     */
    public static final TileMap gl2GetTileMap(String path, int cs, int rs, Tilesheet tilesheet, AssetManager assetManager) {
        return gl2GetTileMap(gl2GetStrigRandomUUID(), path, cs, rs, tilesheet, assetManager);
    }
    
    /**
     * Returns a <code>TileMap</code> stored with the initialized properties.
     * 
     * @param id TileMap identifier name
     * @param path the path of the texture containing the sprites inside the classpath
     * @param cs number of images per column
     * @param rs number of images per row
     * @param tilesheet the {@code Tilesheet}
     * @param assetManager asset manager
     * @return TileMap
     */
    public static final TileMap gl2GetTileMap(String id, String path, int cs, int rs, Tilesheet tilesheet, AssetManager assetManager) {
        TileMap myMap = new TileMap(assetManager, id, tilesheet);
        Properties properties = new Properties();
        properties.put("Texture", path);
        properties.put("Columns", cs);
        properties.put("Rows", rs);
        myMap.setProperties(properties);
        return myMap;
    }
    
    /**
     * Returns a <code>Tile</code> with the minimum properties initialized ready
     * to be managed by a <code>TileMap</code>.
     * 
     * @param cp column number (position: x)
     * @param rp row number (position: y)
     * @param w Tile width
     * @param h Tile height
     * @param x position on the scene: <code>x</code>
     * @param y position on the scene: <code>y</code>
     * @param z position on the scene: <code>y</code>
     * @return Tile
     */
    public static Tile gl2GetTile(int cp, int rp, float w, float h, float x, float y, float z) {
        return gl2GetTile(cp, rp, w, h, x, y, z, null);
    }
    
    /**
     * Returns a <code>Tile</code> with the minimum properties initialized ready
     * to be managed by a <code>TileMap</code>.
     * 
     * @param cp column number (position: x)
     * @param rp row number (position: y)
     * @param w Tile width
     * @param h Tile height
     * @param x position on the scene: <code>x</code>
     * @param y position on the scene: <code>y</code>
     * @param z position on the scene: <code>y</code>
     * @param coll <code>true</code> to add a rectangular shape, otherwise <code>false</code>
     * @return Tile
     */
    public static Tile gl2GetTile(int cp, int rp, float w, float h, float x, float y, float z, boolean coll) {
        return gl2GetTile(cp, rp, w, h, x, y, z, coll ? TilePhysicsSystem.physicsCreateRectangle(w, h) : null);
    }
    
    /**
     * Returns a <code>Tile</code> with the minimum properties initialized ready
     * to be managed by a <code>TileMap</code>.
     * 
     * @param <T> type of collision
     * @param cp column number (position: x)
     * @param rp row number (position: y)
     * @param w Tile width
     * @param h Tile height
     * @param x position on the scene: <code>x</code>
     * @param y position on the scene: <code>y</code>
     * @param z position on the scene: <code>y</code>
     * @param acs physical shape
     * @return Tile
     */
    public static <T> Tile gl2GetTile(int cp, int rp, float w, float h, float x, float y, float z, T acs) {
        return gl2GetTile(gl2GetStrigRandomUUID(), cp, rp, w, h, x, y, z, acs);
    }
    
    /**
     * Returns a <code>Tile</code> with the minimum properties initialized ready
     * to be managed by a <code>TileMap</code>.
     * 
     * @param id unique identifier
     * @param cp column number (position: x)
     * @param rp row number (position: y)
     * @param w Tile width
     * @param h Tile height
     * @param x position on the scene: <code>x</code>
     * @param y position on the scene: <code>y</code>
     * @param z position on the scene: <code>y</code>
     * @return Tile
     */
    public static Tile gl2GetTile(String id, int cp, int rp, float w, float h, float x, float y, float z) {
        return gl2GetTile(id, cp, rp, w, h, x, y, z, null);
    }
    
    /**
     * Returns a <code>Tile</code> with the minimum properties initialized ready
     * to be managed by a <code>TileMap</code>.
     * 
     * @param id unique identifier
     * @param cp column number (position: x)
     * @param rp row number (position: y)
     * @param w Tile width
     * @param h Tile height
     * @param x position on the scene: <code>x</code>
     * @param y position on the scene: <code>y</code>
     * @param z position on the scene: <code>y</code>
     * @param coll <code>true</code> to add a rectangular shape, otherwise <code>false</code>
     * @return Tile
     */
    public static Tile gl2GetTile(String id, int cp, int rp, float w, float h, float x, float y, float z, boolean coll) {
        return gl2GetTile(id, cp, rp, w, h, x, y, z, coll ? TilePhysicsSystem.physicsCreateRectangle(w, h) : null);
    }
    
    /**
     * Returns a <code>Tile</code> with the minimum properties initialized ready
     * to be managed by a <code>TileMap</code>.
     * 
     * @param <T> type of collision
     * @param id unique identifier
     * @param cp column number (position: x)
     * @param rp row number (position: y)
     * @param w Tile width
     * @param h Tile height
     * @param x position on the scene: <code>x</code>
     * @param y position on the scene: <code>y</code>
     * @param z position on the scene: <code>y</code>
     * @param acs physical shape
     * @return Tile
     */
    public static <T> Tile gl2GetTile(String id, int cp, int rp, float w, float h, float x, float y, float z, T acs) {
        Tile tile = new Tile();        
        Properties properties = new Properties();
        properties.put("Id", id);
        properties.put("Row", rp);
        properties.put("Column", cp);
        properties.put("Width", w);
        properties.put("Height", h);
        properties.put("Translation", new Vector3f(x, y, z));        
        if (acs != null) {
            properties.put("PhysicsBody", true);
            properties.put("CollisionShape", TilePhysicsSystem.wrapCollision(acs));
            properties.put("MassType", "INFINITE");
        }        
        tile.setProperties(properties);
        return tile;
    }
}
