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
package jme3gl2.util;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture;

import jme3gl2.physics.collision.RectangleCollisionShape;
import jme3gl2.scene.tile.Properties;
import jme3gl2.scene.tile.Tile;
import jme3gl2.scene.tile.TileMap;

import java.util.UUID;

import org.dyn4j.geometry.MassType;

/**
 * Clase encargado de proporcionar métodos de utilidad para la creación
 * de materiales y texturas 2D.
 * 
 * @version 1.6-SNAPSHOT
 * @since 1.0.0
 */
public final 
class Jme3GL2Utils {
    
    /**
     * Constructor privado de la clase.
     */
    private Jme3GL2Utils() {}
    
    /**
     * Método encargado de generar un identificador a travez de 
     * <code>UUID</code> que ofrece <b>Java</b>.
     * @return nuevo id generada.
     */
    private static String nextId() {
        return String.valueOf(UUID.randomUUID());
    }
    
    /**
     * Genera un {@link TileMap} con las propiedades mas importante y utilizadas
     * por el objeto {@link jme3gl2.scene.tile.TileMap} predeterminado que
     * utiliza {@link TileMap} al instanciarlo.
     * @param tilemap nombre o ruta de la textura que contiene los azulejos.
     * @param rows número maximo de filas.
     * @param columns número maximo de columnas.
     * @param assetManager administrador de recursoso jme.
     * @return Un mapa de azulejos generado.
     */
    public static TileMap jme3gl2TileMap(String tilemap, int rows, int columns, AssetManager assetManager) {
        return jme3gl2TileMap(nextId(), tilemap, rows, columns, assetManager);
    }
    
    /**
     * Genera un {@link TileMap} con las propiedades mas importante y utilizadas
     * por el objeto {@link jme3gl2.scene.tile.TileMap} predeterminado que
     * utiliza {@link TileMap} al instanciarlo.
     * @param id identificador 'unico'.
     * @param tilemap nombre o ruta de la textura que contiene los azulejos.
     * @param rows número maximo de filas.
     * @param columns número maximo de columnas.
     * @param assetManager administrador de recursoso jme.
     * @return Un mapa de azulejos generado.
     */
    public static TileMap jme3gl2TileMap(String id, String tilemap, int rows, int columns, AssetManager assetManager) {
        TileMap myMap = new TileMap(assetManager, id);
        Properties properties = new Properties();
        properties.setProperty("Texture", tilemap);
        properties.setProperty("Rows", rows);
        properties.setProperty("Columns", columns);
        myMap.setProperties(properties);
        return myMap;
    }
    
    /**
     * Método auxiliar en donde se prepara un objeto de la clase {@link Tile}
     * con las propiedades del azulejo, utilizadas por el 
     * {@link jme3gl2.scene.tile.Tilesheet} predeterminado de la clase
     * {@link jme3gl2.scene.tile.TileMap}.
     * 
     * @param id ideintificador unico del azulejo.
     * @param colPos columna de la textura.
     * @param rowPos fila de la textura.
     * @param x posición en el eje {@code x} en escena.
     * @param y posición en el eje {@code y} en escena.
     * @return azulejo generado.
     */
    public static Tile jme3gl2Tile(String id, int colPos, int rowPos, float x, float y) {
        return jme3gl2Tile(id, colPos, rowPos, x, y, false);
    }
    
    /**
     * Método auxiliar en donde se prepara un objeto de la clase {@link Tile}
     * con las propiedades del azulejo, utilizadas por el 
     * {@link jme3gl2.scene.tile.Tilesheet} predeterminado de la clase
     * {@link jme3gl2.scene.tile.TileMap}.
     * 
     * @param id ideintificador unico del azulejo.
     * @param colPos columna de la textura.
     * @param rowPos fila de la textura.
     * @param x posición en el eje {@code x} en escena.
     * @param y posición en el eje {@code y} en escena.
     * @param collide {@code true} si se desea color un cuerpo físico con forma
     *                  de un rectamgulo de acuerdo a las dimensiones dadas.
     * @return azulejo generado.
     */
    public static Tile jme3gl2Tile(String id, int colPos, int rowPos, float x, float y, boolean collide) {
        return jme3gl2Tile(id, colPos, rowPos, 1.0F, 1.0F, x, y, 0.0F, collide);
    }
    
    /**
     * Método auxiliar en donde se prepara un objeto de la clase {@link Tile}
     * con las propiedades del azulejo, utilizadas por el 
     * {@link jme3gl2.scene.tile.Tilesheet} predeterminado de la clase
     * {@link jme3gl2.scene.tile.TileMap}.
     * 
     * @param id ideintificador unico del azulejo.
     * @param colPos columna de la textura.
     * @param rowPos fila de la textura.
     * @param width ancho del modelo.
     * @param height largo del modelo.
     * @param x posición en el eje {@code x} en escena.
     * @param y posición en el eje {@code y} en escena.
     * @param z posición en el eje {@code xz} en escena(esta coordena se utiliza
     *          para tener una perspectiva de alejanida o cercania en 2D).
     * @param collide {@code true} si se desea color un cuerpo físico con forma
     *                  de un rectamgulo de acuerdo a las dimensiones dadas.
     * @return azulejo generado.
     */
    public static Tile jme3gl2Tile(String id, int colPos, int rowPos, float width, float height, float x, float y, float z, boolean collide) {
        Tile tile = new Tile();        
        Properties properties = new Properties();
        properties.setProperty("Id", id);
        properties.setProperty("Row", rowPos);
        properties.setProperty("Column", colPos);
        properties.setProperty("Width", width);
        properties.setProperty("Height", height);
        properties.setProperty("Translation", new Vector3f(x, y, z));        
        if ( collide ) {
            properties.setProperty("RigidBody2D", true);
            properties.setProperty("CollisionShape", new RectangleCollisionShape(width, height));
            properties.setProperty("MassType", MassType.INFINITE);
        }        
        tile.setProperties(properties);
        return tile;
    }
    
    /**
     * Método local para reutilizar la carga de una textura como material
     * sin sombrear.
     * 
     * @param assetManager administrador de recursos {@code jme3}.
     * @param texture nombre de la textura a usar.
     * @return material generado.
     */
    public static Material loadMaterial(AssetManager assetManager, String texture) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture(new TextureKey(texture, false));
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return mat;
    }
    
    /**
     * Método local para reutilizar la carga de una textura con un material que
     * requiera una fuente de luz.
     * 
     * @param assetManager administrador de recursos {@code jme3}.
     * @param texture nombre de la textura a usar.
     * @return material generado.
     */
    public static Material loadMaterialLight(AssetManager assetManager, String texture) {        
        Texture tex = assetManager.loadTexture(new TextureKey(texture, false));
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setWrap(Texture.WrapMode.Repeat);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", tex);
        mat.setFloat("Shininess", 32f);
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", new ColorRGBA(0, 0, 0, 1));
        mat.setColor("Diffuse", new ColorRGBA(1, 1, 1, 1));
        mat.setColor("Specular", new ColorRGBA(1, 1, 1, 1));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return mat;
    }

    /**
     * Método local en donde genera un material con un color.
     * 
     * @param assetManager administrador de recursos {@code jme3}.
     * @param colorRGBA color del material.
     * @return material generado.
     */
    public static Material loadMaterial(AssetManager assetManager, ColorRGBA colorRGBA) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", colorRGBA);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return mat;
    }

    /**
     * Carga una textura sin sombrear.
     * 
     * @param assetManager administrador de recursos {@code jme3}.
     * @param file nombre de la textura a cargar.
     * @return textura cargado.
     */
    public static Texture loadTexture(AssetManager assetManager, String file) {
        Texture tex = assetManager.loadTexture(new TextureKey(file, false));
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setWrap(Texture.WrapMode.Repeat);
        return tex;
    }
}
