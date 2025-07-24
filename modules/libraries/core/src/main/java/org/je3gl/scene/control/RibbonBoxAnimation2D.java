/*
BSD 3-Clause License

Copyright (c) 2023-2025, Night Rider (Wilson)

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.je3gl.scene.control;

import com.jme3.export.*;
import com.jme3.texture.Texture;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

import java.io.IOException;
import org.je3gl.scene.shape.Sprite;

/**
 * A class in charge of managing the frames of the animated control 
 * {@link org.je3gl.scene.control.AnimatedRibbonBoxSprite2D}, in this class the 
 * properties of the animation are defined.
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class RibbonBoxAnimation2D extends AbstractAnimation2D<RibbonBoxAnimation2D> implements Animation2D, Savable, Cloneable, JmeCloneable {

    /** Animated texture. */
    private Texture texture;
    
    /**
     * Number of rows and columns that the animated texture contains.
     */
    private int cols,   // <- columnas - horizontal
                rows;   // <- rows - vertical
    
    /** Frame set. */
    private int[] frames;
    
    /**
     * current index of the animation, this is handled by the animation control 
     * which determines the frame rate of the animation.
     */
    int index;

    /**
     * Constructor for internal use only
     */
    protected RibbonBoxAnimation2D() {
        this(null, new int[0], 0, 0);
    }

    /**
     * Generates a new object of class <code>RibbonBoxAnimation2D</code> where the
     * properties of the animated frame are prepared.
     * 
     * @param texture animated texture
     * @param frames frame set
     * @param cs n-columns
     * @param rs n-rows
     */
    public RibbonBoxAnimation2D(Texture texture, int[] frames, int cs, int rs) {
        this(texture, frames, cs, rs, null, null, Type.Nothing);
    }
    /**
     * Generates a new object of class <code>RibbonBoxAnimation2D</code> where the
     * properties of the animated frame are prepared.
     * 
     * @param texture animated texture
     * @param frames frame set
     * @param cs n-columns
     * @param rs n-rows
     * @param sprite the model mesh
     * @param type scale type
     */
    public RibbonBoxAnimation2D(Texture texture, int[] frames, int cs, int rs, Sprite sprite, Type type) {
        this(texture, frames, cs, rs, sprite.getNativeWidth(), sprite.getNativeHeight(), type);
    }
    
    
    /**
     * Generates a new object of class <code>RibbonBoxAnimation2D</code> where the
     * properties of the animated frame are prepared.
     * 
     * @param texture animated texture
     * @param frames frame set
     * @param cs n-columns
     * @param rs n-rows
     * @param nw mesh width
     * @param nh mesh height
     * @param type scale type
     */
    public RibbonBoxAnimation2D(Texture texture, int[] frames, int cs, int rs, Float nw, Float nh, Type type) {
        super(nw, nh, type);
        this.texture = texture;
        this.frames  = frames;
        this.cols = cs;
        this.rows = rs;
    }

    /* (non-Javadoc)
     * @see com.jme3.util.clone.JmeCloneable#cloneFields(com.jme3.util.clone.Cloner, java.lang.Object) 
     */
    @Override
    public void cloneFields(Cloner cloner, Object original) {
        texture = cloner.clone(texture);
        frames = cloner.clone(frames);
    }

    /**
     * Returns the current frame of the animation.
     * @return int
     */
    public int getFrame() {
        if (frames == null) {
            return 0;
        }
        return frames[index];
    }
    
    /**
     * Returns the list of frames.
     * @return array of integers
     */
    public int[] getFrames() {
        return frames;
    }
    
    /**
     * Returns the number of columns that the texture contains (animated frames).
     * @return int
     */
    public int getColumns() {
        return cols;
    }
    
    /**
     * Returns the number of rows that the texture contains (animated frames).
     * @return int
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the animated texture.
     * @return texture
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Returns the current index (frames).
     * @return int
     */
    public int getIndex() {
        return index;
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
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        out.write(texture, "Texture", null);
        out.write(cols, "Cols", 0);
        out.write(rows, "Rows", 0);
        out.write(frames, "Frames", null);
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
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        texture = (Texture) in.readSavable("Texture", texture);
        frames  = in.readIntArray("Frames", new int[0]);
        cols  = in.readInt("Cols", cols);
        rows  = in.readInt("Rows", rows);
        index = 0;
    }
}
