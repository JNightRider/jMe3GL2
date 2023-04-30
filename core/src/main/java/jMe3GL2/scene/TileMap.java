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
package jMe3GL2.scene;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.scene.Geometry;
import com.jme3.scene.GeometryGroupNode;
import com.jme3.scene.Spatial;

import jMe3GL2.physics.PhysicsSpace;
import jMe3GL2.physics.control.PhysicsBody2D;
import jMe3GL2.renderer.DefaultTileMapRender;
import jMe3GL2.renderer.TileMapRender;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Un objeto de la clase <code>TileMap</code> es un nodo que hereda de la clase
 * <code>GeometryGroupNode</code> que es un conjunto de geometrías.
 * <p>
 * Con un <code>TileMap</code> podemos ser capaz de usar una imagen que integra
 * todo nuestros recursos en ella, luego ir agregando los necesario a nuetro
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
 * siguiente método: <code>addTile()</code> en donde se pide la posicion del
 * recurso(columna y fila) en la imagen general, con la posición del la nueva
 * geometría.
 * </p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 1.5.0
 */
public class TileMap extends GeometryGroupNode {

    /** Logger de la clase. */
    private static final Logger LOG = Logger.getLogger(TileMap.class.getName());
    
    /**
     * Nombre clave del dato de usuario para establecer una forma física o
     * no de cada geometría que se agregen en el nodo {@link TileMap}.
     */
    public static final String COLLISION = "collision";

    /**
     * Administrador de recursos <code>jMonkeyEngine</code>.
     */
    private final AssetManager assetManager;
    
    /** Administrador de azulejos. */
    private TileMapManager mapManager;
    
    /** Administrador del renderizado de los azulejos. */
    private TileMapRender mapRender;
    
    /**
     * Objeto encargado de almacear la información de los azulejos que se
     * agregen a este nodo mapa.
     */
    private TilesHeet tilesHeet;
    
    /**
     * Intancie un nuevo objeto de la clase <code>TileMap</code> en donde se
     * especifica los datos de este mapa de recursos.
     * 
     * @param width ancho de los {@link jMe3GL2.scene.shape.Sprite}.
     * @param height largo de los {@link jMe3GL2.scene.shape.Sprite}.
     * @param columns columnas de la imagen.
     * @param rows filas de la image.
     * @param assetManager administrador de recursos.
     * @param physicsSpace espacio físico.
     * @param texture nombre de la textura a utilizar.
     */
    public TileMap(float width, float height, int columns, int rows, AssetManager assetManager, PhysicsSpace<PhysicsBody2D> physicsSpace, String texture) {
        this(width, height, columns, rows, assetManager, physicsSpace, new TextureKey(texture, false));
    }

    /**
     * Intancie un nuevo objeto de la clase <code>TileMap</code> en donde se
     * especifica los datos de este mapa de recursos.
     * 
     * @param width ancho de los {@link jMe3GL2.scene.shape.Sprite}.
     * @param height largo de los {@link jMe3GL2.scene.shape.Sprite}.
     * @param columns columnas de la imagen.
     * @param rows filas de la image.
     * @param assetManager administrador de recursos.
     * @param physicsSpace espacio físico.
     * @param tk clave de la textura a utilizar.
     */
    private TileMap(float width, float height, int columns, int rows, AssetManager assetManager, PhysicsSpace<PhysicsBody2D> physicsSpace, TextureKey tk) {
        this.tilesHeet  = new TilesHeet(tk, width, height, columns, rows);
        this.mapManager = new DefaultTileMapManager(physicsSpace);
        this.mapRender  = new DefaultTileMapRender();
        this.assetManager = assetManager;
    }
    
    /**
     * Método encargado de construir el mapa de azulejos, ten encuenta que se
     * eliminaran todos los nodos agregador.
     * @param th lista de azulejos.
     */
    public void buildTileMap(TilesHeet th) {
        detachAllChildren();
        
        Iterator<TilesHeet.Tile> it = th.getTiles();
        while (it.hasNext()) {
            TilesHeet.Tile next = it.next();
            Geometry geom = mapRender.render(next, tilesHeet, assetManager);
            attachChild(geom);
        }
        
        this.tilesHeet = th;
        LOG.log(Level.INFO, "Tiles loaded by class [{0}].", th.getClass());
    }

    /**
     * Método encargado de agregar un nuevo azulejo a este nodo mapa.
     * @param id clave unio del azulejo.
     * @param col columna de la localizacuón.
     * @param row fila de la localizacuón.
     * @param x posicion en el munod (eje {@code x}).
     * @param y posicion en el munod (eje {@code y}).
     */
    public void addTile(String id, int col, int row, float x, float y) {
        addTile(id, col, row, x, y, true);
    }
    
    /**
     * Método encargado de agregar un nuevo azulejo a este nodo mapa.
     * @param id clave unio del azulejo.
     * @param col columna de la localizacuón.
     * @param row fila de la localizacuón.
     * @param x posicion en el munod (eje {@code x}).
     * @param y posicion en el munod (eje {@code y}).
     * @param collision {@code true} si se desea agregar un cuerpo físico al
     *                  azulejo, de lo contrario {@code false}.
     */
    public void addTile(String id, int col, int row, float x, float y, boolean collision) {
        Geometry geom = mapRender.render(tilesHeet.addTile(id, col, row, x, y), tilesHeet, assetManager);
        geom.setUserData(COLLISION, collision);
        attachChild(geom);
    }
    
    /**
     * Método encargado de establecer un azulejos, no hay que confundirlo con el
     * método {@code addTile()}.
     * <p>
     * Si el {@code id} no existe en el mapa, se generara uno nuevo. De lo
     * contrario se actualizarn los datos.
     * </p>.
     * 
     * @param id clave unio del azulejo.
     * @param col columna de la localizacuón.
     * @param row fila de la localizacuón.
     * @param x posicion en el munod (eje {@code x}).
     * @param y posicion en el munod (eje {@code y}).
     */
    public void setTile(String id, int col, int row, float x, float y) {
        setTile(id, col, row, x, y, true);
    }
    
    /**
     * Método encargado de establecer un azulejos, no hay que confundirlo con el
     * método {@code addTile()}.
     * <p>
     * Si el {@code id} no existe en el mapa, se generara uno nuevo. De lo
     * contrario se actualizarn los datos.
     * </p>.
     * 
     * @param id clave unio del azulejo.
     * @param col columna de la localizacuón.
     * @param row fila de la localizacuón.
     * @param x posicion en el munod (eje {@code x}).
     * @param y posicion en el munod (eje {@code y}).
     * @param collision {@code true} si se desea agregar un cuerpo físico al
     *                  azulejo, de lo contrario {@code false}.
     */
    public void setTile(String id, int col, int row, float x, float y, boolean collision) {
        Spatial child = getChild(id);
        if (child != null) {
            if (child instanceof Geometry) {
                child.setUserData(COLLISION, collision);
                
                mapRender.update((Geometry) child, tilesHeet.setTile(id, col, row, x, y), tilesHeet, assetManager);
                mapManager.onTransformChange((Geometry) child, this);
            }
        } else {
            addTile(id, col, row, x, y, collision);
        }
    }
    
    /**
     * Elimina un azulejo del mapa.
     * @param id clave azulejo.
     */
    public void removeTile(String id) {
        if (tilesHeet.removeTile(id) != null) {
            detachChildNamed(id);
        }
    }

    /**
     * Devuelve el objeto encargado de renderizar los azulejos.
     * @return objeto de renderuzado.
     */
    public TileMapRender getMapRender() {
        return mapRender;
    }
    
    /**
     * Devuelve un objeto que contien toda la información de los azulejos
     * que utiliza este mapa.
     * <p>
     * <h1>NOTA:</h1>
     * <b>No agregue ó elimine azulejos a través de este objeto</b></p>
     * @return {@link TilesHeet}.
     */
    public TilesHeet getTilesHeet() {
        return tilesHeet;
    }

    /**
     * Establece un nuevo administrador.
     * @param mapManager administrador azulejos.
     */
    public void setMapManager(TileMapManager mapManager) {
        this.mapManager = mapManager;
    }

    /**
     * Establece un nuevo objeto de renderizado.
     * @param mapRender renderizador azulejos.
     */
    public void setMapRender(TileMapRender mapRender) {
        this.mapRender = mapRender;
    }
    
    /**
     * (non-JavaDoc)
     * @return boolean.
     */
    private boolean isMapManager() {
        return this.mapManager != null;
    }
    
    /**
     * (non-JavaDoc)
     * @see GeometryGroupNode#onTransformChange(com.jme3.scene.Geometry) 
     * @param geom {@code Geometry}
     */
    @Override
    public void onTransformChange(Geometry geom) {
        if (isMapManager()) {
            mapManager.onTransformChange(geom, this);
        }
    }

    /**
     * (non-JavaDoc)
     * @see GeometryGroupNode#onMaterialChange(com.jme3.scene.Geometry) 
     * @param geom {@code Geometry}
     */
    @Override
    public void onMaterialChange(Geometry geom) {
        if (isMapManager()) {
            mapManager.onMaterialChange(geom, this);
        }
    }

    /**
     * (non-JavaDoc)
     * @see GeometryGroupNode#onMeshChange(com.jme3.scene.Geometry) 
     * @param geom {@code Geometry}
     */
    @Override
    public void onMeshChange(Geometry geom) {
        if (isMapManager()) {
            mapManager.onMeshChange(geom, this);
        }
    }

    /**
     * (non-JavaDoc)
     * @see GeometryGroupNode#onGeometryUnassociated(com.jme3.scene.Geometry) 
     * @param geom {@code Geometry}
     */
    @Override
    public void onGeometryUnassociated(Geometry geom) {
        if (isMapManager()) {
            mapManager.onGeometryUnassociated(geom, this);
        }
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
        if (isMapManager() && (child instanceof Geometry)) {
            mapManager.onDetachChild((Geometry) child);
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
        if (isMapManager() && (child instanceof Geometry)) {
            mapManager.onAttachChild((Geometry) child);
        }
        return i;
    }
}
