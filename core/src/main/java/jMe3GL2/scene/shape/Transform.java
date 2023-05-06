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
package jMe3GL2.scene.shape;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import java.io.IOException;

/**
 * Un objeto <code>Transform</code> es el encargado de gestionar la 
 * transformación de la malla <code>Sprite</code> para 'modelos 2D'.
 * <p>
 * Con esta clase se puede manipular las vértices de la malla, así como las 
 * coordenadas de ello para que se pueda voltear.</p>
 * <p>
 * <b>NOTA:</b> No cambie directamente las propiedades de su malla con esta
 * clase, utilice los métodos proprocionados por la malla<code>Sprite</code></p>.
 * 
 * @author wil
 * @version 1.5-SNAPSHOT
 * 
 * @since 1.0.0
 */
public final 
class Transform implements Savable, Cloneable {
    
    /**
     * Un {@code FlipType} es una clase interna encargada de la enumeración de
     * los diferentes tipos de volteados que la malla {@code Sprite} puede 
     * hacer.
     */
    public static enum FlipType {
        
        /**
         * Voltea horizontalmente, haciendo que el objeto mire hacia el
         * otra forma (izquierda/derecha).
         */
        Flip_H, 
        
        /**
         * Voltea verticalmente, haciendo que el objeto mire hacia el
         * otra forma (arriba/abajo).
         */
        Flip_V, 
        
        /**
         * Cuando desee voltear horizontal y verticalmente al mismo
         * tiempo, puede utilizar esta opción.
         */
        Flip_HV, 
        
        /**
         * Deje las coordenadas de textura del objeto de malla como están.
         */
        NonFlip;
    }
    
    /**
     * Vector encargado de almacenar las dimensiones de la malla {@code Sprite}
     * en 2D.
     */
    private Vector2f size 
            = new Vector2f(0.0F, 0.0F);
    
    /** Vector encargado de almacenar las columnas y files de la malla. */
    private Vector2f columnsAndRows 
            = new Vector2f(1.0F, 1.0F);
    
    /** Vector encargado de almacenar las posiciones. */
    private Vector2f position 
            = new Vector2f(0.0F, 0.0F);
    
    /** Vector encargado de almacenar el valor de escala. */
    private Vector2f scale 
            = new Vector2f(1.0F, 1.0F);
    
    /**
     * Tipo de volteo.
     * 
     * @see FlipType
     */
    private FlipType flipType 
                = FlipType.NonFlip;

    /**
     * Solo serialización. No utilice.
     */
    public Transform() {        
    }
    
    /**
     * Instancia un nuevo objeto <code>Transform</code>. Establezca las 
     * dimensiones que tendrá los vértices de la malla.
     * 
     * @param width el ancho deseado.
     * @param height la altura deseada.
     */
    public Transform(float width, float height) {
        this(width, height, 1, 1, 0, 0);
    }
    
    /**
     * Instancia un nuevo objeto <code>Transform</code>. Establezca los valores
     * predeterminados de la transformación.
     * 
     * @param width el ancho deseado.
     * @param height la altura deseada.
     * @param columns número de columnas deseada. 
     * @param rows número de filas deseada.
     * @param colPosition posición de columna.
     * @param rowPosition posición de fila.
     */
    public Transform(float width, float height, int columns, int rows, int colPosition, int rowPosition) {
        this.size.set(width, height);                
        this.columnsAndRows.set(columns, rows);
        this.position.set(colPosition, rowPosition);
    }
    
    /**
     * (non-JavaDoc.)
     * @see Object#clone() 
     * @return Clon.
     */
    @Override
    public Transform clone() {
        try {
            Transform clon = (Transform) super.clone();
            clon.columnsAndRows = columnsAndRows.clone();
            clon.position       = position.clone();
            clon.size           = size.clone();
            clon.scale          = scale.clone();
            clon.flipType       = flipType;
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    
    /**
     * Método encargado de generar las posiciones de los vértices en el espacio. 
     * @return Vértices para una malla 2D.
     */
    public Vector3f[] getVertices() {
        Vector3f[] vertices = new Vector3f[4];
        
        /*
         * Establecemos los valore del ancho y la altura de la malla, luego
         * se escala con los valores respectivos (x, y).
        */
        float width  = size.getX() * scale.getX(), 
              height = size.getY() * scale.getY();
        
        // Posiciones de los vértices en el espacio
        vertices[0] = new Vector3f(-width * 0.5f, -height * 0.5f, 0f);
        vertices[1] = new Vector3f(width * 0.5f, -height * 0.5f, 0f);
        vertices[2] = new Vector3f(-width * 0.5f, height * 0.5f, 0f);
        vertices[3] = new Vector3f(width * 0.5f, height * 0.5f, 0f);
        return vertices;
    }
    
    /**
     * Método encargado de generar las coordenadas de la textura.
     * @return Coordenadas textura.
     */
    public Vector2f[] getTextureCoordinates() {
        Vector2f[] texCoord = new Vector2f[4];
        int rows    = (int) columnsAndRows.y,
            columns = (int) columnsAndRows.x; 
            
        int colPosition = (int) position.x, 
            rowPosition = (int) position.y;
        
        float uvSpacing = 0.001f;        
        float colSize = 1f / (float) columns;
        float rowSize = 1f / (float) rows;
        
        if ( null == flipType 
                || FlipType.NonFlip == flipType ) {
            texCoord[0] = new Vector2f(colSize * colPosition + uvSpacing,           rowSize * rowPosition + rowSize - uvSpacing);
            texCoord[1] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + rowSize - uvSpacing);
            texCoord[2] = new Vector2f(colSize * colPosition + uvSpacing,           rowSize * rowPosition + uvSpacing);
            texCoord[3] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + uvSpacing);
        } else switch (flipType) {
            case Flip_H:
                texCoord[0] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + rowSize - uvSpacing);
                texCoord[1] = new Vector2f(colSize * colPosition + uvSpacing,           rowSize * rowPosition + rowSize - uvSpacing);
                texCoord[2] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + uvSpacing);
                texCoord[3] = new Vector2f(colSize * colPosition + uvSpacing,           rowSize * rowPosition + uvSpacing);
                break;
            case Flip_V:                
                texCoord[0] = new Vector2f(colSize * colPosition + uvSpacing,           rowSize * rowPosition + uvSpacing);
                texCoord[1] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + uvSpacing);
                texCoord[2] = new Vector2f(colSize * colPosition + uvSpacing,           rowSize * rowPosition + rowSize - uvSpacing);
                texCoord[3] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + rowSize - uvSpacing);
                break;
            case Flip_HV:
                texCoord[0] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + uvSpacing);
                texCoord[1] = new Vector2f(colSize * colPosition + uvSpacing,           rowSize * rowPosition + uvSpacing);
                texCoord[2] = new Vector2f(colSize * colPosition + colSize - uvSpacing, rowSize * rowPosition + rowSize - uvSpacing);
                texCoord[3] = new Vector2f(colSize * colPosition + uvSpacing,           rowSize * rowPosition + rowSize - uvSpacing);
                break;
            default:
                break;
        }
        return texCoord;
    }

    /**
     * Establece una nueva escala para el ancho y largo de la malla.
     * @param scale nueva escala.
     */
    public void setScale(Vector2f scale) {
        if (scale == null) {
            throw new NullPointerException("Null scale vector.");
        }
        this.scale = scale;
    }

    /**
     * Establece un nuevo <code>FlipType</code> para la malla.
     * @param flipType Un nuevo tipo de volteo.
     */
    public void setFlipType(FlipType flipType) {
        this.flipType = flipType;
    }

    /**
     * (non-JavaDoc).
     * 
     * @param columns Integer.
     * @param rows Integer.
     * @see Transform#position
     */
    void setPosition(int colPosition, int rowPosition) {
        this.position.set(colPosition, rowPosition);
    }
    
    /**
     * (non-JavaDoc).
     * @param width Float.
     * @param height Float.
     * @see Transform#size
     */
    void setSize(float width, float height) {
        this.size.set(width, height);
    }
    
    /**
     * (non-JavaDoc)
     * @param columns Float.
     * @param rows float
     * @see Transform#columnsAndRows
     */
    void setCoords(int columns, int rows) {
        this.columnsAndRows.set(columns, rows);
    }
    
    /**
     * Devuelve la escala actual.
     * @return Vector escala.
     */
    public Vector2f getScale() {
        return scale;
    }
    
    /**
     * Devuelve el tipo de volteado.
     * @return Tipo de volteado.
     */
    public FlipType getFlipType() {
        return flipType;
    }
    
    /**
     * Devuelve el ancho de la transformación. 
     * @return Ancho.
     */
    public float getWidth() {
        return size.x;
    }
    
    /**
     * Devuelve el largo de la transformación. 
     * @return Largo.
     */
    public float getHeight() {
        return size.y;
    }
    
    /**
     * Devuelve el número de filas.
     * @return Filas.
     */
    public int getRows() {
        return (int) columnsAndRows.y;
    }
    
    /**
     * Devuelve el número de columnas.
     * @return Columnas.
     */
    public int getColumns() {
        return (int) columnsAndRows.x;
    }
    
    /**
     * Devuelve la posición de las filas.
     * @return Posición fila.
     */
    public int getColPosition() { 
        return (int) position.x;
    }
    
    /**
     * Devuelve la posición de las columnas.
     * @return Posición columna.
     */
    public int getRowPosition() {
        return (int) position.y;
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
        out.write(size, "size", new Vector2f(0, 0));
        out.write(scale, "scale", new Vector2f(1.0F, 1.0F));
        out.write(columnsAndRows, "columnsAndRows", new Vector2f(1, 1));
        out.write(position, "position", new Vector2f(0, 0));
        out.write(flipType, "flipType", null);
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
        size = (Vector2f) in.readSavable("size", new Vector2f(0, 0));
        scale = (Vector2f) in.readSavable("scale", new Vector2f(1.0F, 1.0F));
        columnsAndRows = (Vector2f) in.readSavable("columnsAndRows", new Vector2f(1, 1));
        position = (Vector2f) in.readSavable("position", new Vector2f(0, 0));
        flipType = in.readEnum("flipType", FlipType.class, FlipType.NonFlip);
    }
}
