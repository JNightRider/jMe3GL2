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
package jme3gl2.scene.shape;

import com.jme3.export.*;
import com.jme3.math.*;

import java.io.*;

/**
 * An object of class <code>Transform</code> is responsible for managing the 
 * transformations of the vertices as well as the texture coordinates of a 
 * mesh ({@link jme3gl2.scene.shape.Sprite}) that uses a 2D model.
 * 
 * @author wil
 * @version 1.5.5
 * @since 1.0.0
 */
public final class Transform implements Cloneable, Savable {
    
    /**
     * A <code>FlipType</code> is an internal class in charge of listing the
     * different types of changes that the {@link jme3gl2.scene.shape.Sprite}
     * mesh can make.
     *
     * @version 1.0.1
     */
    public static enum FlipType {

        /**
         * Flip horizontally, making the object face another direction
         * (left/right).
         */
        Flip_H,
        /**
         * Flip vertically, making the object face another direction (up/down).
         */
        Flip_V,
        /**
         * When you want to flip horizontally and vertically at the same time,
         * you can use this option.
         */
        Flip_HV,
        /**
         * The model does not undergo any change in orientation.
         */
        NonFlip;
    }
    
    /**
     * Vector responsible for storing the dimensions of the 2D mesh
     * {@link jme3gl2.scene.shape.Sprite}.
     */
    Vector2f size           = new Vector2f(0.0F, 0.0F);    
    /** Vector in charge of storing the columns and files of the mesh. */
    Vector2f columnsAndRows = new Vector2f(1.0F, 1.0F);    
    /** Vector responsible for storing positions. */
    Vector2f position       = new Vector2f(0.0F, 0.0F);    
    /** Vector responsible for storing the scale value. */
    Vector2f scale          = new Vector2f(1.0F, 1.0F);
    
    /**
     * Flip type.
     * @see FlipType
     */
    FlipType flipType = FlipType.NonFlip;

    /**
     * Serialization only. Do not use.
     */
    public Transform() {        
    }
    
    /**
     * Creates an instance of a new <code>Transform</code> object. Set the 
     * dimensions of the mesh vertices.
     * 
     * @param width the desired width
     * @param height the desired height
     */
    public Transform(float width, float height) {
        this(width, height, 1, 1, 0, 0);
    }
    
    /**
     * Creates an instance of a new <code>Transform</code> object. Set default 
     * values ​​for the transformation.
     * 
     * @param width the desired width
     * @param height the desired height
     * @param columns mesh columns (used to determine position)
     * @param rows mesh rows (used to determine position)
     * @param col_position column position to use (rendering)
     * @param row_position row position to use (rendering)
     */
    public Transform(float width, float height, int columns, int rows, int col_position, int row_position) {
        this.size.set(width, height);                
        this.columnsAndRows.set(columns, rows);
        this.position.set(col_position, row_position);
    }
    
    /**
     * (non-Javadoc)
     * @see java.lang.Object#clone() 
     * @return this
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
     * Method responsible for generating the size of the mesh (vertices).
     * @return Vertices for a 2D mesh
     */
    public Vector3f[] getVertices() {
        Vector3f[] vertices = new Vector3f[4];
        // if necessary it is scaled to a new size
        float width  = size.getX() * scale.getX(), 
              height = size.getY() * scale.getY();
        
        // positions of vertices in space
        vertices[0] = new Vector3f(-width * 0.5F, -height * 0.5F, 0.0F);
        vertices[1] = new Vector3f( width * 0.5F, -height * 0.5F, 0.0F);
        vertices[2] = new Vector3f(-width * 0.5F,  height * 0.5F, 0.0F);
        vertices[3] = new Vector3f (width * 0.5F,  height * 0.5F, 0.0F);
        return vertices;
    }
    
    /**
     * Method responsible for generating the texture coordinates.
     * @return Texture coordinates
     */
    public Vector2f[] getTextureCoordinates() {
        Vector2f[] texCoord = new Vector2f[4];
        float rows    = columnsAndRows.y,
              columns = columnsAndRows.x;
            
        float colPosition = position.x,
              rowPosition = position.y;
        
        float uvSpacing = FastMath.FLT_EPSILON;/*0.001F*/   
        float colSize = 1.0F / columns;
        float rowSize = 1.0F / rows;
        
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
     * Returns the type of flip.
     * @return type of flip
     */
    public FlipType getFlipType() {
        return flipType;
    }
    
    /**
     * Returns the current scale.
     * @return A {@link com.jme3.math.Vector2f} object
     */
    public Vector2f getScale() {
        return scale.clone();
    }
    
    /**
     * Returns the width of the transformation.
     * @return float
     */
    public float getWidth() {
        return size.x;
    }
    
    /**
     * Returns the height of the transformation.
     * @return float
     */
    public float getHeight() {
        return size.y;
    }
    
    /**
     * Returns the number of rows.
     * @return int
     */
    public int getRows() {
        return (int) columnsAndRows.y;
    }
    
    /**
     * Returns the number of columns.
     * @return int
     */
    public int getColumns() {
        return (int) columnsAndRows.x;
    }
    
    /**
     * Returns the position of the column.
     * @return Posición fila.
     */
    public int getColPosition() { 
        return (int) position.x;
    }
    
    /**
     * Returns the position of the row.
     * @return int
     */
    public int getRowPosition() {
        return (int) position.y;
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#write(com.jme3.export.JmeExporter) 
     * 
     * @param ex {@link com.jme3.export.JmeExporter}
     * @throws IOException throws
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
     * (non-Javadoc)
     * @see com.jme3.export.Savable#read(com.jme3.export.JmeImporter) 
     * 
     * @param im {@link com.jme3.export.JmeImporter}
     * @throws IOException hrows
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        size  = (Vector2f) in.readSavable("size", new Vector2f(0, 0));
        scale = (Vector2f) in.readSavable("scale", new Vector2f(1.0F, 1.0F));
        columnsAndRows = (Vector2f) in.readSavable("columnsAndRows", new Vector2f(1, 1));
        position = (Vector2f) in.readSavable("position", new Vector2f(0, 0));
        flipType = in.readEnum("flipType", FlipType.class, FlipType.NonFlip);
    }
}
