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
import java.util.logging.Logger;

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
 * contienen nuestro imagen. Es decir, cuantos iconos/azujeos contiene nuestro
 * image-map.
 * 
 * <p>
 * Una manera de averiguarlo, es sabiendo el tamaño exacta de cada azulejo en
 * piexele. Dividiendo el ancho de la imagen con el tamaño de los azulejos
 * obtenemos la cantidad de columna, y con el largo las files.
 * <pre><code>
 * <b>EJEMPLO:</b>
 * 
 * Si se tiene una imagen que mida:
 * ancho: 1152 píxeles
 * largo: 1280 píxeles
 * 
 * y que cada azulejos miden:
 * 128px x 128px.
 * 
 * Podemos decir que tenemos 9 columnas y 10 filas dado que si se divide
 * el ancho total con el ancho que cada azulejo: 
 * 
 * 1152/128 = 9  # columnas
 * 1280/128 = 10 #filas
 * 
 * NOTA: Claro que este calculo solo se aplica si los azulejos estan pegados 
 * uno a la par del otro o distribuidos de manera simétrica.
 * </code></pre>
 * <p>
 * Para agregar los azulejos a nuetro nodo escena, solo se tiene que utilizar el
 * siguiente método: <code>addTile()</code>.
 * </p>
 * 
 * @author wil
 * @version 1.8-SNAPSHOT
 * @since 1.5.0
 */
public class TileMap extends GeometryGroupNode {

    /** Looger de la clase. */
    private static final Logger LOG = Logger.getLogger(TileMap.class.getName());
    
    /**
     * Clase interna encargado de gestionar las reglas al agregar o eliminar
     * un {@code Tile} del mapa de azulejos.
     */
    class TileRule {
        // modelos del azulejo.
        Geometry model;
        
        // azulejo-modelo.
        Tile tileModel;

        /**
         * Constructor predeterminado de la clase <code>TileRule</code>.
         * @param model mode a gestionar.
         * @param tileModel azulejos del modelo.
         */
        public TileRule(Geometry model, Tile tileModel) {
            this.model     = model;
            this.tileModel = tileModel;
        }
    }

    /** Administrador de recursos. */
    private final AssetManager assetManager;
    
    /**
     * Administrador de azulejos.
     */
    private Tilesheet tilesHeet;
    
    /** Propiedades de mapa secena. */
    private Properties properties;
    
    /** Lista de azulejos. */
    private final ArrayList<TileRule> tiles;
    
    /**
     * <code>true</code> si se verifica la lista de azulejos al eliminar un
     * nodo hijo de este mapa, de lo contrario será <code>false</code>.
     */
    boolean checkRemove = true;
    
    /**
     * Genere un <code>TileMap</code> utilizando este constructor.
     * @param assetManager administrador-jme3.
     * @param name nombre para este mapa de escena.
     */
    public TileMap(AssetManager assetManager, String name) {
        super(name);
        this.assetManager = assetManager;        
        this.tilesHeet  = jMe3GLDefTilesheet.getInstance();
        this.properties = new Properties();
        this.tiles      = new ArrayList<>();
    }

    /**
     * Establece un {@link Tilesheet} nuevo para este mapa de datos.
     * @param tilesHeet nuevo {@link Tilesheet}.
     */
    public void setTilesHeet(Tilesheet tilesHeet) {
        this.tilesHeet = tilesHeet;
    }

    /**
     * Devuelve el administrdor de recursos.
     * @return admin-recuros
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * Devuelve el gestor de azulejos.
     * @return gestor.
     */
    public Tilesheet getTilesHeet() {
        return tilesHeet;
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
        tilesHeet.getSpritesheetPhysics().setPhysicsSpace(physicsSpace);
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
        addTile(tile, true);
    }
    
    /**
     * Agrega un nuevo {@link Tile} a este nodo de azulejos.
     * <p>
     * <b>NOTA:</b> Utilizar este método unicamente si se tiene el control
     * total del manejo de azulejos, de lo contrario no los toque</p>
     * 
     * @param tile nuvo dato a agregar.
     * @param verifID {@code true} si se desea tener un identiricador unico,
     *                  de lo contrario {@code false}.
     */
    protected void addTile(final Tile tile, boolean verifID) {
        boolean exists = false;
        for (final TileRule rule : this.tiles) {
            Tile element = rule.tileModel;
            if (element == null) {
                continue;
            }            
            if (element.equals(tile)) {
                exists = true;
                break;
            }            
            if (verifID && (element.getId().equals(tile.getId()))) {
                throw new IllegalArgumentException("[" + tile.getId() + "] existing tile.");
            }
        }

        if (!exists) {
            final Geometry model = tilesHeet.getSpritesheet()
                                            .render(this, tile, assetManager);            
            tiles.add(new TileRule(model, tile));
            attachChild(model);
        } else {
            LOG.warning("It was not possible to add the tile because there is already an identical one on the map.");
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
            TileRule rule = tiles.get(i);
            Tile element  = rule.tileModel;
            if (element == null)
                continue;
            
            if (element.getId().equals(id)) {
                rule.tileModel = tile;
                tilesHeet.getSpritesheet().update(this, tile, assetManager, (Geometry) getChild(id));
                break;
            }
        }
    }
    
    /**
     * Método encargado de establecer nuevas propiedades a un azulejo.
     * <p>
     * Para localizar dicho {@link Tile} se utiliza su identificador {@code id}.
     * </p>
     * 
     * @param id identificador unico.
     * @param p nuevas propiedades.
     */
    public void setTileProperties(String id, Properties p) {
        for (int i = 0; i < tiles.size(); i++) {
            TileRule rule = tiles.get(i);
            Tile element  = rule.tileModel;
            if (element == null)
                continue;
            
            if (element.getId().equals(id)) {                
                element.setProperties(p);
                tilesHeet.getSpritesheet().update(this, element, assetManager, (Geometry) getChild(id));
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
            TileRule rule = tiles.get(i);
            Tile element  = rule.tileModel;
            if (element == null)
                continue;
            
            if (element.getId().equals(id)) {
                tiles.remove(i);
                checkRemove = false;
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
        final Iterator<TileRule> it = this.tiles.iterator();
        return new Iterator<Tile>() {
            @Override public boolean hasNext() {
                return it.hasNext();
            }
            @Override public Tile next() {
                return it.next().tileModel;
            }
        };
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
        tilesHeet.getSpritesheetPhysics().onTransformChange(geom);
    }

    /**
     * (non-JavaDoc)
     * @param geom geometry
     */
    @Override
    public void onMaterialChange(Geometry geom) {
        tilesHeet.getSpritesheetPhysics().onMaterialChange(geom);
    }

    /**
     * (non-JavaDoc)
     * @param geom gemetry
     */
    @Override
    public void onMeshChange(Geometry geom) {
        tilesHeet.getSpritesheetPhysics().onMeshChange(geom);
    }

    /**
     * (non-JavaDoc)
     * @param geom geometry
     */
    @Override
    public void onGeometryUnassociated(Geometry geom) {
        tilesHeet.getSpritesheetPhysics().onTileUnassociated(geom);
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
            tilesHeet.getSpritesheetPhysics()
                     .onDetachTile((Geometry) child);            
            if (checkRemove) {
                for (int i = 0; i < tiles.size(); i++) {
                    if (tiles.get(i).model == child) {
                        this.tiles.remove(index);
                        break;
                    }
                }
            }
        }
        checkRemove = true;
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
            tilesHeet.getSpritesheetPhysics().onAttachTile((Geometry) child);
        }
        return i;
    }
}
