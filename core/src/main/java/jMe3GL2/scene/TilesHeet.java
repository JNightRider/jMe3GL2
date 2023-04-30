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

import com.jme3.asset.TextureKey;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Un objeto de la clase <code>TilesHeet</code> se encarga de gestionar los
 * datos de un {@link TileMap}.
 * <p>
 * Almacena una lista de {@link Tile} con los datos en el mapa:
 * <ul>
 * <li>Posición del tile.</li>
 * <li>Localización que tomará el sprite</li>
 * <li>Un identificador unico.</li>
 * </ul>
 * </p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 1.5.0
 */
@SuppressWarnings(value = {"unchecked"})
public class TilesHeet implements Savable, Cloneable {
    
    /**
     * Lista {@link Tile} con los datos de cada uno.
     */
    private ArrayList<Tile> tiles 
            = new ArrayList<>();
    
    /** Clave de la textura a utilizar. */
    private TextureKey texture;
    
    /** Cuadrantes de la textura. */
    private Flot quadrant;
    
    /** Número total de filas y columnas de la textura. */
    private Int quadrantSize;

    /**
     * Constructor predeterminado.
     */
    public TilesHeet() {
        this(null, 0, 0, 0, 0);
    }

    /**
     * Instancie un nuevo objeto de la clase <code>TilesHeet</code> con los
     * datos predeterminados de este azulejo.
     * 
     * @param texture clave textura.
     * @param w ancho de los cuadrantes
     * @param h largo de los cuadrantes
     * @param wt columna de los cuadrantes(total)
     * @param ht fila de los cuadrantes(total)
     */
    public TilesHeet(TextureKey texture, float w, float h, int wt, int ht) {
        this.quadrantSize = new Int(wt, ht);
        this.quadrant = new Flot(w, h);
        this.texture  = texture;
    }
        
    /**
     * Método encargado de clonar esta clase y sus propiedades.
     * @return Clon generado.
     */
    @Override
    public TilesHeet clone() {
        try {
            TilesHeet clon = (TilesHeet) super.clone();
            clon.texture   = (TextureKey) texture.clone();
            clon.quadrant     = quadrant.clone();
            clon.quadrantSize = quadrantSize.clone();
            
            clon.tiles = (ArrayList<Tile>) tiles.clone();
            for (int i = 0; i < clon.tiles.size(); i++) {
                clon.tiles.set(i, clon.tiles.get(i).clone());
            }
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    
    /**
     * Método encargado de agregar un nuevo {@link Tile}.
     * 
     * @param id id clave del nuevo tile.
     * @param col columna al que pertenece.
     * @param row fila al que pertenece.
     * @param x posición {@code y} en el mapa.
     * @param y posición {@code x} en el mapa.
     * @return {@link Tile}.
     */
    Tile addTile(String id, int col, int row, float x, float y) {
        Tile tile = validate(id);
        if (tile != null) {
            throw new IllegalArgumentException("[" + id + "] existing tile.");
        }
        
        tile = new Tile(id);
        tile.setPosition(x, y);
        tile.setLocation(col, row);        
        this.tiles.add(tile);
        return tile;
    }
    
    /**
     * Limina un tile de la lista.
     * @param id id clave.
     * @return 
     */
    Tile removeTile(String id) {
        Tile tile = validate(id);
        if (tile != null) {
            this.tiles.remove(tile);
            return tile;
        }
        return null;
    }
    
    /**
     * Actualiza los datos de un tile registrado.Si no se localiza se agregará
     * a la lista como un nuevo {@link Tile}.
     * 
     * @param id id clave del nuevo tile.
     * @param col columna al que pertenece.
     * @param row fila al que pertenece.
     * @param x posición {@code y} en el mapa.
     * @param y posición {@code x} en el mapa.
     * @return {@link Tile}.
     */
    Tile setTile(String id, int col, int row, float x, float y) {
        Tile tile = validate(id);
        if (tile == null) {
            return addTile(id, col, row, x, y);
        } else {
            tile.setPosition(x, y);
            tile.setLocation(col, row);
            return tile;
        }        
    }

    /**
     * Devuelve un {@code Iterator} con los tiles registrados.
     * @return iterador.
     */
    public Iterator<Tile> getTiles() {
        return tiles.iterator();
    }

    /**
     * Devuelve la clave de la textura.
     * @return clave textura.
     */
    public TextureKey getTexture() {
        return texture;
    }

    /**
     * Largo y ancho total de todos los cuadrantes.
     * @return dimensiones.
     */
    public Flot getQuadrant() {
        return quadrant.clone();
    }

    /**
     * Devuelve el número de filas y columnas para los alzulejos.
     * @return cantidad de cudrantes.
     */
    public Int getQuadrantSize() {
        return quadrantSize.clone();
    }
    
    /**
     * Método encargado de validar si existe un {@link Tile} a través de su
     * id clave. De lo contrario devolveria <code>null</code>.
     * 
     * @param id id clave.
     * @return {@link Tile} gestionado.
     */
    public Tile validate(String id) {
        for (final Tile element : this.tiles) {
            if (element == null) {
                continue;
            }
            if (element.getId().equals(id)) {
                return element;
            }
        }
        return null;
    }
    
    /**
     * (non-JavaDoc).
     * 
     * @param ex JmeExporter.
     * @see Savable#write(com.jme3.export.JmeExporter) 
     * 
     * @throws IOException Excepción.
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(texture, "texture", null);
        out.write(quadrant, "quadrant", null);
        out.write(quadrantSize, "quadrantSize", null);
        out.writeSavableArrayList(tiles, "tiles", null);
    }

    /**
     * (non-JavaDoc).
     * 
     * @param im JmeImporter
     * @see Savable#read(com.jme3.export.JmeImporter) 
     * 
     * @throws IOException Excepción.
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);        
        texture = (TextureKey) in.readSavable("texture", texture);
        quadrant = (Flot) in.readSavable("quadrant", quadrant);
        quadrantSize = (Int) in.readSavable("quadrantSize", quadrantSize);
        tiles = in.readSavableArrayList("tiles", tiles);
    }
    
    /**
     * Clase interna encargado de gestionar los datos de un azulejo, es decir
     * la posición y locación de su textura.
     */
    public static final 
    class Tile implements Savable, Cloneable {
        
        /** Identificador unico para este azulejos. */
        String id;
        
        /** Posición del azulejo en escena. */
        Flot position;
        
        /** Locación del azulejos en su conjunto. */
        Int location;

        /**
         * Constructor predeterminado.
         */
        protected Tile() {
        }

        /**
         * Genera un nuevo <code>Tile</code> con un {@code Id} unico.
         * @param id clave azulejo.
         */
        public Tile(String id) {
            this.position = new Flot();
            this.location = new Int();
            this.id = id;
        }
        
        /**
         * (non-JavaDoc)
         * @see Object#clone() 
         * @return {@code this}.
         */
        @Override
        public Tile clone() {
            try {
                Tile clon = (Tile) super.clone();
                clon.id   = id;
                clon.position = position.clone();
                clon.location = location.clone();
                return clon;
            } catch (CloneNotSupportedException e) {
                throw new InternalError(e);
            }
        }
        
        /**
         * Devuelve el id.
         * @return string.
         */
        public String getId() {
            return id;
        }

        /**
         * Devuelve la posición.
         * @return Flot.
         */
        public Flot getPosition() {
            return position.clone();
        }

        /**
         * Devuelve la licalización.
         * @return Int.
         */
        public Int getLocation() {
            return location.clone();
        }

        /**
         * Establece una nueva localización del azulejo.
         * @param c columna que tomará.
         * @param r fila que tomaŕa.
         */
        public void setLocation(int c, int r) {
            this.location.set(c, r);
        }
        
        /**
         * Establece una nueva posición.
         * @param x posición en el eje {@code x}.
         * @param y posición en el eje {@code y}.
         */
        public void setPosition(float x, float y) {
            this.position.set(x, y);
        }
        
        /**
         * (non-JavaDoc).
         * 
         * @param ex JmeExporter.
         * @see Savable#write(com.jme3.export.JmeExporter) 
         * 
         * @throws IOException Excepción.
         */
        @Override
        public void write(JmeExporter ex) throws IOException {
            OutputCapsule out = ex.getCapsule(this);
            out.write(id, "id", null);
            out.write(position, "position", null);
            out.write(location, "location", null);
        }

        /**
         * (non-JavaDoc).
         * 
         * @param im JmeImporter
         * @see Savable#read(com.jme3.export.JmeImporter) 
         * 
         * @throws IOException Excepción.
         */
        @Override
        public void read(JmeImporter im) throws IOException {
            InputCapsule in = im.getCapsule(this);
            id = in.readString("id", null);
            position = (Flot) in.readSavable("position", position);
            location = (Int)  in.readSavable("location", location);
        }
    }
    
    /**
     * Calse abstracta encargado de gestionar dos datos del mismo tipo para
     * que luego sean guardados/expotados por la interfaz {@code Savable}.
     * @param <T> de tipo {@code Number}.
     */
    static abstract class Data<T extends Number> implements Savable, Cloneable {
        
        /** Dato a. */
        protected T a;
        
        /** Dato b. */
        protected T b;

        /**
         * Constructor predeterminado.
         */
        public Data() { }

        /**
         * Intancie un nuevo objeto de la clase <code>Data</code>.
         * @param a dato a.
         * @param b dato b.
         */
        public Data(T a, T b) {
            this.a = a;
            this.b = b;
        }
        
        /**
         * (non-JavaDoc)
         * @see Object#clone() 
         * @return {@code this}.
         */
        @Override
        public Data<T> clone() {
            try {
                Data<T> clon = (Data<T>) super.clone();
                return clon;
            } catch (CloneNotSupportedException e) {
                throw new InternalError(e);
            }
        }
        
        /**
         * Establece nuevos valores para {@code a} y {@code b}.
         * @param a nuevo valor para 'a'.
         * @param b nuevo valor para 'b'.
         */
        public void set(T a, T b) {
            this.a = a;
            this.b = b;
        }
        
        /**
         * Establece un nuevo valor para {@code a}.
         * @param a nuevo valor para 'a'.
         */
        public void setA(T a) {
            this.a = a;
        }

        /**
         * Establece un nuevo valor para {@code b}.
         * @param b nuevo valor para 'b'.
         */
        public void setB(T b) {
            this.b = b;
        }

        /**
         * Devuelve el valor de a.
         * @return valor a.
         */
        public T getA() {
            return a;
        }

        /**
         * Devuelve el valor de b.
         * @return valor b.
         */
        public T getB() {
            return b;
        }        
    }
    
    /**
     * Clase encargado de definir el tipo de datos a utiliza por la clase
     * abstracta {@link Data}.
     * <p>
     * Tipo de datos definida: <b>Float.</b>
     * </p>
     */
    public static final class Flot extends Data<Float> {

        /**
         * (non-JavaDoc).
         * @see Data#Data() 
         */
        public Flot() {
        }

        /**
         * (non-JavaDoc)
         * @see Data#Data(java.lang.Number, java.lang.Number) 
         * @param a valor a.
         * @param b valor b.
         */
        public Flot(float a, float b) {
            super(a, b);
        }
        
        /**
         * (non-JavaDoc)
         * @see Object#clone() 
         * @return {@code this}.
         */
        @Override
        public Flot clone() {
            Flot clon = (Flot) super.clone();
            clon.a = a;
            clon.b = b;
            return clon;
        }
        
        /**
         * (non-JavaDoc).
         * 
         * @param ex JmeExporter.
         * @see Savable#write(com.jme3.export.JmeExporter) 
         * 
         * @throws IOException Excepción.
         */
        @Override
        public void write(JmeExporter ex) throws IOException {
            OutputCapsule out = ex.getCapsule(this);
            out.write(a, "A", 0.0F);
            out.write(b, "B", 0.0F);
        }

        /**
         * (non-JavaDoc).
         * 
         * @param im JmeImporter
         * @see Savable#read(com.jme3.export.JmeImporter) 
         * 
         * @throws IOException Excepción.
         */
        @Override
        public void read(JmeImporter im) throws IOException {
            InputCapsule in = im.getCapsule(this);
            a = in.readFloat("A", 0.0F);
            b = in.readFloat("B", 0.0F);
        }
    }
    
    /**
     * Clase encargado de definir el tipo de datos a utiliza por la clase
     * abstracta {@link Data}.
     * <p>
     * Tipo de datos definida: <b>Integer.</b>
     * </p>
     */
    public static final class Int extends Data<Integer> {

        /**
         * (non-JavaDoc).
         * @see Data#Data() 
         */
        public Int() {
        }

        /**
         * (non-JavaDoc)
         * @see Data#Data(java.lang.Number, java.lang.Number) 
         * @param a valor a.
         * @param b valor b.
         */
        public Int(int a, int b) {
            super(a, b);
        }

        /**
         * (non-JavaDoc)
         * @see Object#clone() 
         * @return {@code this}.
         */
        @Override
        public Int clone() {
            Int clon = (Int) super.clone();
            clon.a = a;
            clon.b = b;
            return clon;
        }
        
        /**
         * (non-JavaDoc).
         * 
         * @param ex JmeExporter.
         * @see Savable#write(com.jme3.export.JmeExporter) 
         * 
         * @throws IOException Excepción.
         */
        @Override
        public void write(JmeExporter ex) throws IOException {
            OutputCapsule out = ex.getCapsule(this);
            out.write(a, "A", 0);
            out.write(b, "B", 0);
        }

        /**
         * (non-JavaDoc).
         * 
         * @param im JmeImporter
         * @see Savable#read(com.jme3.export.JmeImporter) 
         * 
         * @throws IOException Excepción.
         */
        @Override
        public void read(JmeImporter im) throws IOException {
            InputCapsule in = im.getCapsule(this);
            a = in.readInt("A", 0);
            b = in.readInt("B", 0);
        }
    }
}
