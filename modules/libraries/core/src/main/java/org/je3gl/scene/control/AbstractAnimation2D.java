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
import com.jme3.math.Vector2f;
import com.jme3.util.clone.JmeCloneable;
import java.io.IOException;

/**
 * Abstract class for all animations (frames)
 * @param <T> a {@code jme3gl2.scene.control.AbstractAnimation2D} type
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public abstract class AbstractAnimation2D<T extends AbstractAnimation2D<T>> implements Animation2D, Savable, Cloneable, JmeCloneable {
    
    /* size (mesh). **/
    private Float width,  // <- mesh width (Sprite)
                  height; // <- mesh height (Sprite)
    
    /**
     * Scaling type.
     * @see Type
     */
    private Type type;
    
    /**
     * Generate a new instance of class <code>AbstractAnimation2D</code>,
     * @param width mesh width (Sprite)
     * @param height mesh height (Sprite)
     * @param type scale type
     */
    public AbstractAnimation2D(Float width, Float height, Type type) {
        if (type != null && Type.Nothing != type) {
            if (width == null || height == null) {
                throw new IllegalArgumentException("It is necessary to have the dimensions to apply the animation scale.");
            }
        }        
        this.width  = width;
        this.height = height;
        this.type   = type;
    }

    /**
     * Returns the scale type of the animation
     * @return type
     */
    public Type getType() {
        return type;
    }
    
    /**
     * Method responsible for returning the size of the new animation, said size
     * is relative to a given reference to determine the corresponding scale.
     * 
     * @param ref reference
     * @return vector2f
     */
    public Vector2f getSize(Vector2f ref) {
        float max, min;
        float mm_dim;
        if (null == type || Type.Nothing == type) {
            return null;
        } else switch (type) {
            case GLXH:
                max = ref.y;
                mm_dim = getNativeHeight();
                return new Vector2f((ref.x / max) *  mm_dim, mm_dim);
            case GLXW:
                max = ref.x;
                mm_dim = getNativeWidth();
                return new Vector2f(mm_dim, (ref.y / max) * mm_dim);
            case Max:
                max = ref.y;
                min = ref.x;
                if (max >= min) {
                    mm_dim = getNativeHeight();
                    return new Vector2f((min / max) * mm_dim, mm_dim);
                } else {
                    mm_dim = getNativeWidth();
                    return new Vector2f(mm_dim, (max / min) * mm_dim);
                }
            case Min:
                max = ref.y;
                min = ref.x;
                if (max >= min) {
                    mm_dim = getNativeWidth();
                    return new Vector2f(mm_dim, (max / min) * mm_dim);
                } else {
                    mm_dim = getNativeHeight();
                    return new Vector2f((min / max) * mm_dim, mm_dim);
                }
            case Custom:
                return new Vector2f(getNativeWidth(), getNativeHeight());
            default:
                throw new AssertionError();
        }
    }

    /* (non-Javadoc)
     * @see com.jme3.util.clone.JmeCloneable#jmeClone() 
     */
    @Override
    public Object jmeClone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Error cloning this object: " + this.getClass(), e);
        }
    }
    
    /**
     * Set a new size for the animation mesh
     * @param width mesh width (Sprite)
     * @param height mesh height (Sprite)
     * @param type scale type
     * @return this
     */
    @SuppressWarnings("unchecked")
    public T size(Float width, Float height, Type type) {
        this.width  = width;
        this.height = height;
        this.type   = type;
        return (T) this;
    }
    
    /**
     * Set a new size for the animation mesh
     * @param width mesh width (Sprite)
     * @param height mesh height (Sprite)
     * @return this
     */
    @SuppressWarnings("unchecked")
    public T size(Float width, Float height) {
        return this.size(width, height, Type.Custom);
    }
    
    /**
     * Returns the width, <code>null</code> by default.
     * @return float|Float
     */
    public Float getNativeWidth() {
        return width;
    }

    /**
     * Returns the height, <code>null</code> by default.
     * @return float|Float
     */
    public Float getNativeHeight() {
        return height;
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
        if (width != null) {
            out.write(width, "Width", -1);
        }
        if (height != null) {
            out.write(height, "Heigh", -1);
        }
        out.write(type, "Type", null);
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
        float nw = in.readFloat("Width", -1);
        if (nw > -1) {
            width = nw;
        }        
        float nh = in.readFloat("Heigh", -1);
        if (nh > -1) {
            height = nh;
        }
        type = in.readEnum("Type", Type.class, Type.Nothing);
    }
}
