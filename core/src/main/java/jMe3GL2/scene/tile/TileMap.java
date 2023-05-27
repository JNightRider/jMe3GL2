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
package jMe3GL2.scene.tile;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.GeometryGroupNode;
import com.jme3.scene.Spatial;

import jMe3GL2.physics.PhysicsSpace;
import jMe3GL2.physics.control.PhysicsBody2D;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Un objeto de la clase <code>TileMap</code> es un nodo que hereda de la clase
 * <code>GeometryGroupNode</code> que es un conjunto de geometrías.
 * <p>
 * Con un <code>TileMap</code> podemos ser capaz de usar una imagen que integra
 * todo nuestros recursos en ella, luego ir agregando los necesario a nuestro
 * escena.
 * </p>
 * <p>
 * Esta clase pide la cantidad en la horizontal y vertical de los azulejos que
 * contienen nuetro imagen. Es decir, cuantos iconos/azujeos contiene nuestro
 * image map.
 * 
 * <p>
 * Una manera de averiguarlo, es sabiendo el tamaño exata de cada azulejo en
 * piexele. Dividiendo el ancho de la imagen con el tamaño de los azulejos
 * obtenemos la cantidad de columna, y con el largo las files.
 * <pre><code>
 * <b>EJEMPLO:</b>
 * 
 * float width = 20, height = 20;   // ancho y largo de la imagen.
 * float quadrant = 16;             // cada azulejo que integra la imagen mide
 *                                  // 16px * 16px
 * 
 * // Esto nos daria un valor de 20px * 20px, es decir
 * // que tenemos 20 azulejos en la horizontal y 20 en
 * // la vertical.
 * int cols = width  * quadrant,
 *     rows = height * quadrant;
 * </code></pre>
 * </p>
 * </p>
 * <p>
 * Para agregar los azulejos a nuetro nodo escena, solo se tiene que utilizar el
 * siguiente método: <code>addTile()</code>.
 * </p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * @since 1.5.0
 */
public class TileMap extends GeometryGroupNode {

    /** Administrador de recursos. */
    private AssetManager assetManager;
    
    /**
     * Administrador de azulejos.
     */
    private TilesHeet tilesHeet;
    
    /** Propiedades de mapa secena. */
    private Properties properties;
    
    /** Lista de azulejos. */
    private final ArrayList<Tile> tiles;
    
    /**
     * Genere un <code>TileMap</code> utilizando este constructor.
     * @param assetManager administrador-jme3.
     * @param name nombre para este mapa de escena.
     */
    public TileMap(AssetManager assetManager, String name) {
        super(name);
        this.assetManager = assetManager;        
        this.tilesHeet  = new jMe3GLDefTilesHeet();
        this.properties = new Properties();
        this.tiles      = new ArrayList<>();
    }

    /**
     * Establece un {@link TilesHeet} nuevo para este mapa de datos.
     * @param tilesHeet nuevo {@link TilesHeet}.
     */
    public void setTilesHeet(TilesHeet tilesHeet) {
        this.tilesHeet = tilesHeet;
    }

    /**
     * Establece las propiedades de este nodo de mapas.
     * <p>
     * <b>NOTA:</b> Antes de agregar nuevos {@link Tile} al este mapa de escena,
     * establesca las propiedades dado que estos cambios no se aplicaran con lo
     * hijos ya existentes.</p>
     * @param properties nuevas propiedades.
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Método encargado de establecer el espacio físico.
     * @param physicsSpace espacio físico.
     */
    public void setPhysicsSpace(PhysicsSpace<PhysicsBody2D> physicsSpace) {
        tilesHeet.getTileSpace().setPhysicsSpace(physicsSpace);
    }
    
    /**
     * Agrega un nuevo {@link Tile} a este nodo de azulejos.
     * @param c columna del azulejo.
     * @param r fila azulejo.
     * @param properties propiedades.
     */
    public void addTile(int c, int r, Properties properties) {
        final Tile tile = new Tile();
        properties.setProperty("Column", c);
        properties.setProperty("Row", r);
        
        tile.setProperties(properties);
        addTile(tile);
    }
    
    /**
     * Agrega un nuevo {@link Tile} a este nodo de azulejos.
     * @param tile nuvo dato a agregar.
     */
    public void addTile(final Tile tile) {
        if (!tiles.contains(tile)) {
            for (final Tile element : this.tiles) {
                if (element == null)
                    continue;
                
                if (element.getId().equals(tile.getId())) {
                    throw new IllegalArgumentException("[" + tile.getId() + "] existing tile.");
                }
            }
            
            tiles.add(tile);
            attachChild(tilesHeet.getTileModel().tileModel(this, tile, assetManager));
        }
    }
    
    /**
     * Establece nuevas propiedades a un {@link Tile} con el id de ello.
     * @param id identificador del azulejo.
     * @param properties nuevas propiedades.
     */
    public void setTile(String id, Properties properties) {
        setTile(id, new Tile() {{
            setProperties(properties.setProperty("Id", id));
        }});
    }
    
    /**
     * Establece un {@link Tile} nuevo con el id de ello.
     * @param id identificador del azulejo.
     * @param tile nuevo azulejo.
     */
    public void setTile(String id, Tile tile) {
        if (!id.equals(tile.getId())) {
            throw new IllegalArgumentException("Id tile.");
        }
        
        for (int i = 0; i < tiles.size(); i++) {
            Tile element = tiles.get(i);
            if (element == null)
                continue;
            
            if (element.getId().equals(id)) {                
                tiles.set(i, tile);
                tilesHeet.getTileModel().updateModel(this, tile, assetManager, (Geometry) getChild(id));
                break;
            }
        }
    }
    
    /**
     * Elimina un azulejo de escena.
     * @param id identificador del azulejo.
     */
    public void removeTile(String id) {
        for (int i = 0; i < tiles.size(); i++) {
            Tile element = tiles.get(i);
            if (element == null)
                continue;
            
            if (element.getId().equals(id)) {
                tiles.remove(i);
                detachChildNamed(id);
                break;
            }
        }
    }

    /**
     * Devueleve un iterador de azulejos.
     * @return iterador.
     */
    public Iterator<Tile> iteratorTile() {
        return tiles.iterator();
    }
    
    /**
     * Devuelve las propiedades de este mapa de azulejos.
     * @return propiedades.
     */
    public Properties getProperties() {
        return properties;
    }
    
    /**
     * Devuele la fila total de azulejos de la imagen mapeado.
     * @return filas.
     */
    public int getRows() {
        return  properties.getProperty("Rows", 0);
    }
    
    /**
     * Devuele el número total de columnas de la imagen mapeado.
     * @return columnas.
     */
    public int getColumns() {
        return properties.getProperty("Columns", 0);
    }
    
    /**
     * (non-JavaDoc)
     * @param geom geometry
     */
    @Override
    public void onTransformChange(Geometry geom) {
        tilesHeet.getTileSpace().onTransformChange(geom);
    }

    /**
     * (non-JavaDoc)
     * @param geom geometry
     */
    @Override
    public void onMaterialChange(Geometry geom) {
        tilesHeet.getTileSpace().onMaterialChange(geom);
    }

    /**
     * (non-JavaDoc)
     * @param geom gemetry
     */
    @Override
    public void onMeshChange(Geometry geom) {
        tilesHeet.getTileSpace().onMeshChange(geom);
    }

    /**
     * (non-JavaDoc)
     * @param geom geometry
     */
    @Override
    public void onGeometryUnassociated(Geometry geom) {
        tilesHeet.getTileSpace().onTileUnassociated(geom);
    }
    
    /**
     * (non-JavaDoc)
     * @see GeometryGroupNode#detachChildAt(int) 
     * 
     * @param index int.
     * @return Spatial.
     */
    @Override
    public Spatial detachChildAt(int index) {
        Spatial child = super.detachChildAt(index);
        if ( child instanceof Geometry ) {
            tilesHeet.getTileSpace().onDetachTile((Geometry) child);
        }
        return child;
    }

    /**
     * (non-JavaDoc)
     * @see GeometryGroupNode#attachChildAt(com.jme3.scene.Spatial, int) 
     * 
     * @param child Spatial.
     * @param index int.
     * @return int.
     */
    @Override
    public int attachChildAt(Spatial child, int index) {
        int i =  super.attachChildAt(child, index);
        if ( child instanceof Geometry ) {
            tilesHeet.getTileSpace().onAttachTile((Geometry) child);
        }
        return i;
    }
}
