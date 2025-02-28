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
package org.je3gl.scene.shape;

import com.jme3.export.*;
import com.jme3.math.Vector2f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

import java.io.IOException;

/**
 * An object of the <code>Sprite</code> class is responsible for managing the mesh 
 * used by the 2D models. This mesh provides what is needed to model a 
 * <b><a href="https://jmonkeyengine.org/">JME3</a></b> object in a 2D environment.
 * <p>
 * A <code>Sprite</code> uses a percentage scale, meaning that the dimensions of 
 * this mesh are in the range of 0 to 1 (0% - 100%) which must be taken into account
 * when generating a 2D model so that it does not deform in the process.
 * <p>
 * <b>Example</b>:
 * If a texture has the following dimensions
 * <pre>{@code 
 * Width : 100px;
 * Height:  50px;
 * }</pre>
 * We have to take as a reference a measurement of 100% of the dimensions to be 
 * able to define and thus avoid deforming the texture of the geometry.
 * <br>
 * In this case we will take the width, with this data referenced the dimensions 
 * of this mesh will be:<br>
 * <pre>{@code 
 * Width : 1.0F; // <- Equivalent to 100%.
 * Height: 0.5F; // <- Equivalent to 50%
 * }</pre>
 * This analysis is equivalent to saying that the width is 100%, while the height 
 * is 50% of the width. If the data had been taken the other way around it would 
 * be that now the width is 50% of the height, which is 100%.
 * 
 * @author wil
 * @version 2.0.0
 * @since 1.0.0
 */
public class Sprite extends Mesh implements Cloneable {
    
    /**
     * Object in charge of managing the transformation of the mesh, that is, its
     * vertices as well as the coordinates of the texture of this mesh.
     */
    private Transform transform;
    
    /**
     * {@code true} if you want to flip the mesh texture horizontally; otherwise 
     * {@code false} if you want to return the texture to its original state.
     */
    private boolean flipH;
    
    /**
     * {@code true} if you want to flip the mesh texture vertically; otherwise 
     * {@code false} if you want to return the texture to its original state.
     */
    private boolean flipV;

    /**
     * Serialization only. Do not use.
     */
    protected Sprite() {
    }
    
    /**
     * Generate a new instance of the class <code>Sprite</code>' where the data 
     * of this mesh is initialized (2D Sprite).
     * 
     * @param w mesh width (typically in the range 0.0 to 1.0)
     * @param h mesh height (typically in the range of 0.0 to 1.0)
     */
    public Sprite(float w, float h) {
        this(w, h, 1, 1, 0, 0);
    }
    
    /**
     * Generate a new instance of the class <code>Sprite</code>' where the data 
     * of this mesh is initialized (2D Sprite).
     * 
     * @param w mesh width (typically in the range 0.0 to 1.0)
     * @param h mesh height (typically in the range of 0.0 to 1.0)
     * @param c the number of columns is the amount of horizontal fragmentation 
     *          that will be performed on the mesh texture
     * @param r the number of rows is the amount of vertical fragmentation that 
     *          will be performed on the mesh texture
     * @param cp an integer that specifies the position (column) of the fragmentation 
     *           to be used in the horizontal (mesh texture)
     * @param rp an integer that specifies the position (row) of the fragmentation 
     *           to use in the vertical (mesh texture)
     */
    public Sprite(float w, float h, int c, int r, int cp, int rp) {
        this(w, h, c, r, cp, rp, false, false);
    }
    
    /**
     * Generate a new instance of the class <code>Sprite</code>' where the data 
     * of this mesh is initialized (2D Sprite).
     * 
     * @param w mesh width (typically in the range 0.0 to 1.0)
     * @param h mesh height (typically in the range of 0.0 to 1.0)
     * @param c the number of columns is the amount of horizontal fragmentation 
     *          that will be performed on the mesh texture
     * @param r the number of rows is the amount of vertical fragmentation that 
     *          will be performed on the mesh texture
     * @param cp an integer that specifies the position (column) of the fragmentation 
     *           to be used in the horizontal (mesh texture)
     * @param rp an integer that specifies the position (row) of the fragmentation 
     *           to use in the vertical (mesh texture)
     * @param fph <code>true</code> if you want to flip the mesh horizontally; otherwise <code>false</code>
     * @param fpv <code>true</code> if you want to flip the mesh vertically; otherwise <code>false</code>
     */
    public Sprite(float w, float h, int c, int r, int cp, int rp, boolean fph, boolean fpv) {
        this.transform = new Transform(w, h, c, r, cp, rp);
        this.flipH     = fph;
        this.flipV     = fpv;
        this.initializeMesh();
    }
    
    /**
     * Method responsible for initializing and/or configuring the mesh.
     */
    private void initializeMesh() {
        transform.flipType = valueOfFlipType(flipH, flipV);
        
        // we define the order in which the mesh should be built.
        short[] indexes = {2, 0, 1, 
                           1, 3, 2};

        // buffer configuration
        setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(transform.getVertices()));
        setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(transform.getTextureCoordinates()));
        setBuffer(VertexBuffer.Type.Normal,   3, new float[] {
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1});
        
        setBuffer(VertexBuffer.Type.Index, 1, BufferUtils.createShortBuffer(indexes));
        updateBound();
    }
    
    /**
     * Method responsible for verifying the type of flip ({@link jme3gl2.scene.shape.Transform.FlipType}) 
     * that the <code>Sprite</code> mesh transformer ({@link org.je3gl.scene.shape.Transform}) will apply.
     * 
     * @param fph <code>true</code> if you want to flip the mesh horizontally; otherwise <code>false</code>
     * @param fpv <code>true</code> if you want to flip the mesh vertically; otherwise <code>false</code
     * @return An <code>enum</code> object
     */
    private static Transform.FlipType valueOfFlipType(boolean fph, boolean fpv) {
        if ( fph && fpv ) {
            return Transform.FlipType.Flip_HV;
        } else if ( fph ) {
            return Transform.FlipType.Flip_H;
        } else if ( fpv ) {
            return Transform.FlipType.Flip_V;
        } else {
            return Transform.FlipType.NonFlip;
        }
    }
    
    /**
     * Method responsible for changing the fragmentation positions (column and row)
     * previously assigned through an index.
     * <p>
     * Allows you to loop through all the fragmentation (rows and columns) of this 
     * mesh (texture) in a linear fashion.
     * @param index int
     */
    public void showIndex(int index) {
        applyCoords(index % transform.getColumns(), index / transform.getColumns());
    }
    
    /**
     * Apply a new size for this mesh.
     * @param nw new width
     * @param nh new height
     */
    public void applySize(float nw, float nh) {
        if (transform.size.x != nw || transform.size.y != nh) {
            transform.size.set(nw, nh);
            
            setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(transform.getVertices()));
            updateBound();
        }
    }
    
    /**
      *Apply new positions for the fragmentation of this mesh (texture).
     * @param cp new position on the horizontal (column)
     * @param rp new position in the vertical (row)
     */
    public void applyCoords(int cp, int rp) {
        applyCoords(transform.getColumns(), transform.getRows(), cp, rp);
    }
    
    /**
     * Apply new coordinates for the fragmentation of this mesh (texture).
     * @param c new number of columns
     * @param r new number of rows
     * @param cp new position on the horizontal (column)
     * @param rp new position in the vertical (row)
     */
    public void applyCoords(int c, int r, int cp, int rp) {
        if (c < 1 || r < 1) {
            throw new IllegalArgumentException("A Sprite with negative values ​​cannot exist.");
        }
        
        byte changes = 0;
        if (transform.columnsAndRows.x != c || transform.columnsAndRows.y != r) {
            transform.columnsAndRows.set(c, r);
            changes++;
        }
        if (transform.position.x != cp || transform.position.y != rp) {
            transform.position.set(cp, rp);
            changes++;
        }
        if (changes > 0) {
            setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(transform.getVertices()));
            updateTextureCoords();
            updateBound();
        }
    }
    
    /**
     * Method responsible for forcing an update on the coordinates of this mesh (texture).
     * <p>
     * <b>WARNING</b>: Do not abuse this method as it is not controlled (may have 
     * unnecessary rendering costs).
     */
    public void updateTextureCoords() {
        transform.flipType = valueOfFlipType(flipH, flipV);
        setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(transform.getTextureCoordinates()));
    }
    
    /**
     * Apply a controlled update to the coordinates of this mesh (texture).
     * @param cp new position on the horizontal (column)
     * @param rp new position in the vertical (row)
     */
    public void applyTextureCoords(int cp, int rp) {
        if (transform.position.x != cp || transform.position.y != rp) {
            transform.position.set(cp, rp);
            updateTextureCoords();
        }
    }
    
    /**
     * Flip the mesh horizontally.
     * @param flipH <code>true</code> if flipped horizontally; otherwise <code>false</code>
     */
    public void flipH(boolean flipH) {
        if (this.flipH != flipH) {
            this.flipH = flipH;
            this.updateTextureCoords();
        }
    }
    
    /**
     * Flip the mesh vertically.
     * @param flipV <code>true</code> if flipped vertically; otherwise <code>false</code>
     */
    public void flipV(boolean flipV) {
        if (this.flipV != flipV) {
            this.flipV = flipV;
            this.updateTextureCoords();
        }
    }
    
    /**
     * Apply a uniform scale.
     * @param scale value
     */
    public void applyScale(float scale) {
        applyScale(scale, scale);
    }
    
    /**
     * Applies a specific scale for this mesh.
     * @param sx scale in x (width)
     * @param sy scale in y (height)
     */
    public void applyScale(float sx, float sy) {
        if (transform.scale.x != sx || transform.scale.y != sy) {
            transform.scale.set(sx, sy);
            
            // new vertices are established for the mesh.
            setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(transform.getVertices()));
            updateBound();
        }
    }
    
    /* (non-Javadoc)
     * @see com.jme3.export.Savable#read(com.jme3.export.JmeImporter)
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        transform = (Transform) in.readSavable("transform", new Transform());
        flipH     = in.readBoolean("flipH", Boolean.FALSE);
        flipV     = in.readBoolean("flipV", Boolean.FALSE);
        initializeMesh();
    }
    
    /* (non-Javadoc)
     * @see com.jme3.export.Savable#write(com.jme3.export.JmeExporter) 
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);        
        OutputCapsule out = ex.getCapsule(this);
        out.write(transform, "transform", null);
        out.write(flipH, "flipH", Boolean.FALSE);
        out.write(flipV, "flipV", Boolean.FALSE);
    }
    
    /* (non-Javadoc)
     * @see com.jme3.scene.Mesh#jmeClone() 
     */
    @Override
    public Sprite jmeClone() {
        return (Sprite) super.jmeClone();
    }
    
    /* (non-Javadoc)
     * @see com.jme3.scene.Mesh#deepClone() 
     */
    @Override
    public Sprite deepClone() {
        Sprite clon = (Sprite) super.deepClone();
        clon.transform = transform.clone();
        clon.flipH = flipH;
        clon.flipV = flipV;
        return clon;
    }
    
    /* (non-Javadoc)
     * @see com.jme3.scene.Mesh#clone() 
     */
    @Override
    public Sprite clone() {
        Sprite clon = (Sprite) super.clone();
        clon.transform = transform.clone();
        clon.flipH = flipH;
        clon.flipV = flipV;
        return clon;
    }
    
    /**
     *  Returns the type of flip in the horizontal.
     * @return boolean
     */
    public boolean isFlipH() {
        return flipH;
    }
    
    /**
     * Returns the type of flip in the vertical.
     * @return boolean
     */
    public boolean isFlipV() {
        return flipV;
    }

    /**
     * Returns the transformer of this mesh.
     * @return object
     */
    public Transform getTransform() {
        return transform;
    }
    
    /**
     * Returns the native width of this mesh (unscaled).
     * @return float
     */
    public float getNativeWidth() {
        return transform.getWidth();
    }
    
    /**
     * Returns the native height of this mesh (unscaled).
     * @return float
     */
    public float getNativeHeight() {
        return transform.getHeight();
    }
    
    /**
     * Returns the current width.
     * @return float
     */
    public float getWidth() {
        return transform.getWidth() * transform.scale.x;
    }
    
    /**
     * Returns the current height.
     * @return float
     */
    public float getHeight() {
        return transform.getHeight() * transform.scale.y;
    }
    
    /**
     * Returns the scale of this mesh.
     * @return vector2f
     */
    public Vector2f getScale() {
        return transform.getScale();
    }
}
