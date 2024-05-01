/* Copyright (c) 2009-2024 jMonkeyEngine.
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
package org.je3gl.scene.tile;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.GeometryGroupNode;
import com.jme3.scene.Spatial;

import org.je3gl.physics.PhysicsSpace;
import org.je3gl.physics.control.PhysicsBody2D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An object of the class <code>TileMap</code> is a node that inherits from the
 * class <code>GeometryGroupNode</code> which is a set of geometries.
 * <p>
 * With a <code>TileMap</code> we can be able to use an image that integrates
 * all our resources into it, then add them to our scene.
 * <p>
 * This class asks for the number of tiles horizontally and vertically that
 * contain our image. That is to say, how many icons/tiles our image map contains.
 * <p>
 * One way to find out is to know the exact size of each tile in pixels. Dividing
 * the width of the image with the size of the tiles we get the number of columns,
 * and with the length we get the files.
 * <pre><code>
 * <b>EXAMPLE:</b>
 * 
 * If you have an image that measures:
 * width:  1152 pixels
 * height: 1280 pixels
 * 
 * And that each tile measures:
 * 128px x 128px.
 * </code></pre>
 * <p>
 * We can say that we have 9 columns and 10 rows given that if we divide the
 * total width with the width of each tile:
 * <pre><code>
 * 1152/128 = 9  # columns
 * 1280/128 = 10 # rows
 * </code></pre>
 * <p>
 * <b>NOTE</b>: Of course, this calculation only applies if the tiles are next to each
 * other or symmetrically distributed.
 * <p>
 * To add the tiles to our scene node, just use the following method:
 * <code>addTile()</code>.
 * 
 * @author wil
 * @version 1.8.5
 * @since 1.5.0
 */
public class TileMap extends GeometryGroupNode {
    /** Class logger. */
    private static final Logger LOG = Logger.getLogger(TileMap.class.getName());
    
    /**
     * Internal class in charge of managing the rules when adding or removing a
     * {@code Tile} from the tile map.
     */
    class TileRule {
        /** Tile geometry. */
        Geometry model;        
        /** Tile model. */
        Tile tileModel;

        /**
         * Default constructor of the class <code>TileRule</code>.
         * 
         * @param model model to manage
         * @param tileModel model tiles
         */
        public TileRule(Geometry model, Tile tileModel) {
            this.model     = model;
            this.tileModel = tileModel;
        }
    }

    /** Resource manager. */
    private final AssetManager assetManager;    
    /** Tile manager. */
    private Tilesheet tilesHeet;    
    /** Scene map properties. */
    private Properties properties;    
    /** List of tiles. */
    private final ArrayList<TileRule> tiles;
    
    /**
     * <code>true</code> if the tile list is checked when deleting a child node
     * of this map, otherwise it will be <code>false</code>.
     */
    boolean checkRemove = true;
    
    /**
     * Generate a <code>TileMap</code> using this constructor.
     * 
     * @param assetManager JME3 manager
     * @param name name for this scene map
     */
    public TileMap(AssetManager assetManager, String name) {
        super(name);
        this.assetManager = assetManager;        
        this.tilesHeet  = Jme3GLDefTilesheet.getInstance();
        this.properties = new Properties();
        this.tiles      = new ArrayList<>();
    }

    /**
     * Sets up a new {@link Tilesheet} for this data map.
     * 
     * @param tilesHeet new {@link Tilesheet}
     */
    public void setTilesHeet(Tilesheet tilesHeet) {
        if (tilesHeet == null) {
            throw new IllegalArgumentException("A renderer is necessary for 'Tiles'");
        }
        this.tilesHeet = tilesHeet;
    }

    /**
     * Returns the resource manager.
     * 
     * @return resource manager
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * Returns the tile manager.
     * 
     * @return manager
     */
    public Tilesheet getTilesHeet() {
        return tilesHeet;
    }

    /**
     * Sets the properties of this map node.
     * <p>
     * <b>NOTE:</b> Before adding new {@link Tile} to this scene map, set the
     * properties since these changes will not apply to existing children.
     * 
     * @param properties new properties
     */
    public void setProperties(Properties properties) {
        this.properties = properties == null ? new Properties() : properties;
    }

    /**
     * Method in charge of establishing the physical space.
     * 
     * @param physicsSpace physical space
     */
    public void setPhysicsSpace(PhysicsSpace<PhysicsBody2D> physicsSpace) {
        tilesHeet.getSpritesheetPhysics().setPhysicsSpace(physicsSpace);
    }
    
    /**
     * Adds a new {@link Tile} to this tile node.
     * 
     * @param c tile column
     * @param r tile row
     * @param properties properties
     */
    public void addTile(int c, int r, Properties properties) {
        final Tile tile = new Tile();
        properties.put("Column", c);
        properties.put("Row", r);
        
        tile.setProperties(properties);
        addTile(tile);
    }
    
    /**
     * Adds a new {@link Tile} to this tile node.
     * 
     * @param tile new data to be added.
     */
    public void addTile(final Tile tile) {
        addTile(tile, true);
    }
    
    /**
     * Adds a new {@link Tile} to this tile node.
     * <p>
     * <b>NOTE:</b> Use this method only if you have full control of the tile
     * handling, otherwise do not touch the tiles.
     * 
     * @param tile new data to be added
     * @param verifID {@code true} if you wish to have a unique identifier,
     * otherwise {@code false}
     */
    protected void addTile(final Tile tile, boolean verifID) {
        boolean exists = false;
        for (final TileRule rule : this.tiles) {
            Tile element = rule.tileModel;
            if (element == null) {
                continue;
            }            
            //if (element.equals(tile)) {
            //    exists = true;
            //    break;
            //}            
            if (verifID && (element.getId().equals(tile.getId()))) {
                LOG.log(Level.WARNING, "[{0}] existing tile.", tile.getId());
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
     * Set new properties to a {@link Tile} with the id of it.
     * 
     * @param id tile identifier
     * @param properties new properties
     */
    public void setTile(String id, Properties properties) {
        setTile(id, new Tile() {{
            setProperties(properties.put("Id", id));
        }});
    }
    
    /**
     * Set a new {@link Tile} with the id of it.
     * 
     * @param id tile identifier
     * @param tile new tile
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
     * Method for establishing new properties to a tile.
     * <p>
     * To locate this {@link Tile} its identifier is used {@code id}.
     * 
     * @param id unique identifier
     * @param p new properties
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
     * Removes a scene tile.
     * 
     * @param id tile identifier
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
     * Returns a tile iterator.
     * 
     * @return iterator
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
     * Returns the properties of this tile map.
     * 
     * @return properties
     */
    public Properties getProperties() {
        return properties;
    }
    
    /* (non-Javadoc) */
    @Override
    public void onTransformChange(Geometry geom) {
        tilesHeet.getSpritesheetPhysics().onTransformChange(geom);
    }
    /* (non-Javadoc) */
    @Override
    public void onMaterialChange(Geometry geom) {
        tilesHeet.getSpritesheetPhysics().onMaterialChange(geom);
    }
    /* (non-Javadoc) */
    @Override
    public void onMeshChange(Geometry geom) {
        tilesHeet.getSpritesheetPhysics().onMeshChange(geom);
    }
    /* (non-Javadoc) */
    @Override
    public void onGeometryUnassociated(Geometry geom) {
        tilesHeet.getSpritesheetPhysics().onTileUnassociated(geom);
    }
    
    /*
     * (non-Javadoc)
     * @see GeometryGroupNode#detachChildAt(int) 
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

    /*
     * (non-Javadoc)
     * @see GeometryGroupNode#attachChildAt(com.jme3.scene.Spatial, int) 
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
