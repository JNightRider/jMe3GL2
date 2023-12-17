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
package jme3gl2.utilities;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;

import java.util.UUID;

import jme3gl2.physics.collision.AbstractCollisionShape;
import jme3gl2.physics.collision.RectangleCollisionShape;
import jme3gl2.scene.tile.Properties;
import jme3gl2.scene.tile.Tile;
import jme3gl2.scene.tile.TileMap;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;

/**
 * Clase de utilidad para cargar, administrar y modificar <code>TileMap</code>
 * o <code>Tile</code>.
 * @author wil
 * @version 1.0.0
 * @since 2.5.0
 */
public final class TileMapUtilities {
    
    /**
     * Devuelve un UUID para ser utilizado como ID, para los objetos 
     * <code>TileMap</code> y <code>Tile</code>.
     * @return UUID string
     */
    public static final String getStrigRandomUUID() {
        return String.valueOf(UUID.randomUUID());
    }
    
    /**
     * Devuelve un <code>TileMap</code> almacenada con la propiedades inicializadas.
     * 
     * @param path la ruta de la textura que contiene los sprites dentro del classpath
     * @param cs cantidad de imágenes por columna
     * @param rs cantidad de imágenes por fila
     * @param assetManager administrador de los activos
     * @return TileMap
     */
    public static final TileMap getTileMap(String path, int cs, int rs, AssetManager assetManager) {
        return getTileMap(getStrigRandomUUID(), path, cs, rs, assetManager);
    }
    
    /**
     * Devuelve un <code>TileMap</code> almacenada con la propiedades inicializadas.
     * 
     * @param id nombre-identificador del TileMap
     * @param path la ruta de la textura que contiene los sprites dentro del classpath
     * @param cs cantidad de imágenes por columna
     * @param rs cantidad de imágenes por fila
     * @param assetManager administrador de los activos
     * @return TileMap
     */
    public static final TileMap getTileMap(String id, String path, int cs, int rs, AssetManager assetManager) {
        TileMap myMap = new TileMap(assetManager, id);
        Properties properties = new Properties();
        properties.put("Texture", path);
        properties.put("Columns", cs);
        properties.put("Rows", rs);
        myMap.setProperties(properties);
        return myMap;
    }
    
    /**
     * Devuelve un <code>Tile</code> con las propiedades mínimas inicializadas
     * listas para ser gestionadas por un <code>TileMap</code>.
     * 
     * @param cp número de columna (posición: x)
     * @param rp número de fila (posición: y)
     * @param w largo del Tile
     * @param h ancho del Tile
     * @param x posición en escena: <code>x</code>
     * @param y posición en escena: <code>y</code>
     * @param z posición en escena: <code>y</code>
     * @return Tile
     */
    public static Tile getTile(int cp, int rp, float w, float h, float x, float y, float z) {
        return getTile(cp, rp, w, h, x, y, z, null);
    }
    
    /**
     * Devuelve un <code>Tile</code> con las propiedades mínimas inicializadas
     * listas para ser gestionadas por un <code>TileMap</code>.
     * 
     * @param cp número de columna (posición: x)
     * @param rp número de fila (posición: y)
     * @param w largo del Tile
     * @param h ancho del Tile
     * @param x posición en escena: <code>x</code>
     * @param y posición en escena: <code>y</code>
     * @param z posición en escena: <code>y</code>
     * @param coll <code>true</code> para agregar una forma rectangular, de lo
     * contrario <code>false</code>
     * @return Tile
     */
    public static Tile getTile(int cp, int rp, float w, float h, float x, float y, float z, boolean coll) {
        return getTile(cp, rp, w, h, x, y, z, coll ? new RectangleCollisionShape(w, h) : null);
    }
    
    /**
     * Devuelve un <code>Tile</code> con las propiedades mínimas inicializadas
     * listas para ser gestionadas por un <code>TileMap</code>.
     * 
     * @param <T> tipo de colición
     * @param cp número de columna (posición: x)
     * @param rp número de fila (posición: y)
     * @param w largo del Tile
     * @param h ancho del Tile
     * @param x posición en escena: <code>x</code>
     * @param y posición en escena: <code>y</code>
     * @param z posición en escena: <code>y</code>
     * @param acs forma física
     * @return Tile
     */
    public static <T extends Convex> Tile getTile(int cp, int rp, float w, float h, float x, float y, float z, AbstractCollisionShape<T> acs) {
        return getTile(getStrigRandomUUID(), cp, rp, w, h, x, y, z, acs);
    }
    
    /**
     * Devuelve un <code>Tile</code> con las propiedades mínimas inicializadas
     * listas para ser gestionadas por un <code>TileMap</code>.
     * 
     * @param id identificador unico
     * @param cp número de columna (posición: x)
     * @param rp número de fila (posición: y)
     * @param w largo del Tile
     * @param h ancho del Tile
     * @param x posición en escena: <code>x</code>
     * @param y posición en escena: <code>y</code>
     * @param z posición en escena: <code>y</code>
     * @return Tile
     */
    public static Tile getTile(String id, int cp, int rp, float w, float h, float x, float y, float z) {
        return getTile(id, cp, rp, w, h, x, y, z, null);
    }
    
    /**
     * Devuelve un <code>Tile</code> con las propiedades mínimas inicializadas
     * listas para ser gestionadas por un <code>TileMap</code>.
     * 
     * @param id identificador unico
     * @param cp número de columna (posición: x)
     * @param rp número de fila (posición: y)
     * @param w largo del Tile
     * @param h ancho del Tile
     * @param x posición en escena: <code>x</code>
     * @param y posición en escena: <code>y</code>
     * @param z posición en escena: <code>y</code>
     * @param coll <code>true</code> para agregar una forma rectangular, de lo
     * contrario <code>false</code>
     * @return Tile
     */
    public static Tile getTile(String id, int cp, int rp, float w, float h, float x, float y, float z, boolean coll) {
        return getTile(id, cp, rp, w, h, x, y, z, coll ? new RectangleCollisionShape(w, h) : null);
    }
    
    /**
     * Devuelve un <code>Tile</code> con las propiedades mínimas inicializadas
     * listas para ser gestionadas por un <code>TileMap</code>.
     * 
     * @param <T> tipo de colición
     * @param id identificador unico
     * @param cp número de columna (posición: x)
     * @param rp número de fila (posición: y)
     * @param w largo del Tile
     * @param h ancho del Tile
     * @param x posición en escena: <code>x</code>
     * @param y posición en escena: <code>y</code>
     * @param z posición en escena: <code>y</code>
     * @param acs forma física
     * @return Tile
     */
    public static <T extends Convex> Tile getTile(String id, int cp, int rp, float w, float h, float x, float y, float z, AbstractCollisionShape<T> acs) {
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
            properties.put("CollisionShape", acs);
            properties.put("MassType", MassType.INFINITE);
        }        
        tile.setProperties(properties);
        return tile;
    }
}
