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
package org.jegl.jawt;

import com.jme3.export.*;
import java.io.*;

/**
 * Class responsible for encapsulating data from the different available screen
 * resolutions.
 * 
 * @author wil
 * @version 1.5.0
 * @since 2.0.0
 */
public final class JAWTResolution implements Cloneable, Savable {
    /** Test resolution for a window game. */
    public static final JAWTResolution TEST_RESOLUTION = new JAWTResolution(1024, 576);
    
    /** Minimum value that the width of the game screen can have. */
    public static final int MIN_WIDTH  = 640;
    /** Minimum value that the height can have on the game screen. */
    public static final int MIN_HEIGHT = 480;
    
    /** Screen width. */
    private int width;    
    /** Screen height. */
    private int height;
    
    /** Bit depth. */
    private int bitDepth;    
    /** Refresh-rate.  */
    private int refreshRate;

    /**
     * Constructor for internal use only to create this object.
     */
    protected JAWTResolution() {
        this(MIN_WIDTH, MIN_HEIGHT);
    }
    
    /**
     * Generate a new object of class <code>WTResolution</code> where the screen 
     * size is initialized.
     * 
     * @param width screen width
     * @param height screen height
     */
    public JAWTResolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Method responsible for cloning this object.
     * @return this
     */
    @Override
    public JAWTResolution clone() {
        try {
            JAWTResolution clon = (JAWTResolution) super.clone();
            clon.width         = width;
            clon.height        = height;
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    
    /**
     * (non-Javadoc)
     * @see java.lang.Object#toString() 
     * @return string
     */
    @Override
    public String toString() {
        return "Resolution [" + "width=" + width + ", height=" + height + ']';
    }

    /**
     * Returns the width of the resolution.
     * @return int
     */
    public int getWidth() {
        return width;  
    }
    
    /**
     * Returns the height of the resolution.
     * @return int
     */
    public int getHeight() { 
        return height; 
    }

    /**
     * Returns the bit depth.
     * @return int
     */
    public int getBitDepth() {
        return bitDepth;
    }

    /**
     * Returns the refresh rate.
     * @return int
     */
    public int getRefreshRate() {
        return refreshRate;
    }

    /**
     * Set a new width for the resolution.
     * @param width int
     */
    public void setWidth(int width) {
        if (width < MIN_WIDTH)
            throw new IllegalArgumentException("Width=[" + width);
        
        this.width = width;
    }
    
    /**
     * Set a new height for the resolution.
     * @param height  int
     */
    public void setHeight(int height) {
        if (height < MIN_WIDTH)
            throw new IllegalArgumentException("Height=[" + height);
        
        this.height = height;
    }

    /**
     * Sets a new value for the bit depth.
     * @param bitDepth int
     */
    public void setBitDepth(int bitDepth) {
        this.bitDepth = bitDepth;
    }

    /**
     * Set a new refresh rate.
     * @param refreshRate int
     */
    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
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
        out.write(width, "width", MIN_WIDTH);
        out.write(height, "height", MIN_HEIGHT);
        out.write(bitDepth, "bitDepth", 0);
        out.write(refreshRate, "refreshRate", 0);
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
        width  = in.readInt("width", MIN_WIDTH);
        height = in.readInt("height", MIN_HEIGHT);
        bitDepth    = in.readInt("bitDepth", bitDepth);
        refreshRate = in.readInt("refreshRate", refreshRate);
    }

    /**
     * (non-Javadoc)
     * @see java.lang.Object#hashCode() 
     * @return int
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.width;
        hash = 73 * hash + this.height;
        hash = 73 * hash + this.bitDepth;
        hash = 73 * hash + this.refreshRate;
        return hash;
    }

    /**
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object) 
     * 
     * @param obj the other object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JAWTResolution other = (JAWTResolution) obj;
        if (this.width != other.width) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        if (this.bitDepth != other.bitDepth) {
            return false;
        }
        return this.refreshRate == other.refreshRate;
    }
}
