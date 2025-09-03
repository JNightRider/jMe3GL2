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
import com.jme3.util.clone.Cloner;

import java.io.IOException;
import org.je3gl.scene.shape.Sprite;

/**
 * A class in charge of managing the frames of the animated control 
 * {@link org.je3gl.scene.control.AnimatedSingleSprite2D}, in this class the 
 * properties of the animation are defined.
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class SingleAnimation2D extends AbstractAnimation2D<SingleAnimation2D> implements Animation2D, Savable {
    
    /** Animated frame index. */
    private int index;

    /**
     * Constructor for internal use only
     */
    protected SingleAnimation2D() {
        this(-1);
    }

    /**
     * Generate a <code>SingleAnimation2D</code> object and initialize its parameters.
     * @param index int
     */
    public SingleAnimation2D(int index) {
        this(index, null, null, Type.Nothing);
    }
    
    /**
     * Generate a <code>SingleAnimation2D</code> object and initialize its parameters.
     * @param index int
     * @param sprite the model mesh
     * @param type scale type
     */
    public SingleAnimation2D(int index, Sprite sprite, Type type) {
        this(index, sprite.getNativeWidth(), sprite.getNativeHeight(), type);
    }
    
    /**
     * Generate a <code>SingleAnimation2D</code> object and initialize its parameters.
     * @param index int
     * @param nw mesh width
     * @param nh mesh height
     * @param type scale type
     */
    public SingleAnimation2D(int index, Float nw, Float nh, Type type) {
        super(nw, nh, type);
        this.index = index;
    }

    /* (non-Javadoc)
     * @see com.jme3.util.clone.JmeCloneable#cloneFields(com.jme3.util.clone.Cloner, java.lang.Object) 
     */
    @Override
    public void cloneFields(Cloner cloner, Object original) {
    }

    /**
     * Returns the index of the animated frame.
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
        out.write(index, "Index", 0);
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
        index = in.readInt("Index", index);
    }
}
