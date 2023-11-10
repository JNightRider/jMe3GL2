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
package jme3gl2.awt;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

import java.io.IOException;

/**
 * Gestiona la resolución de la pantalla del juego.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 2.0.0
 */
public final 
class AWTResolution implements Cloneable, Savable {
    
    /**
     * Valor minimo que puede tener el largo de la
     * pantalla de juego.
     */
    public static final int MIN_WIDTH  = 1024;
    
    /**
     * Valor minimo que puede tener el nacho de la
     * pantalla de juego.
     */
    public static final int MIN_HEIGHT = 576;
    
    /** Largo de la pantalla. */
    private int width;
    
    /** Ancho de la pantalla. */
    private int height;
    
    /** Profundidad de bits. */
    private int bitDepth;
    
    /** frecuencia de actualización. */
    private int refreshRate;

    /**
     * Constructor de la clase para uso interno.
     */
    protected AWTResolution() {
        this(MIN_WIDTH, MIN_HEIGHT);
    }

    /**
     * Genera un nuevo objeto <code>AWTResolution</code> para establecer una
     * nueva resolucion de pantalla.
     * 
     * @param width Valor del largo de la pantalla de juego.
     * @param height Valor del ancho de la pantalla.
     */
    public AWTResolution(int width, int height) {
        this.width  = width;
        this.height = height;
    }

    /**
     * Método encargado de clonar a este objeto.
     * @return clon objeto.
     */
    @Override
    public AWTResolution clone() {
        try {
            AWTResolution clon = (AWTResolution) 
                            super.clone();
            clon.width  = width;
            clon.height = height;
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    
    /**
     * (non-JavaDoc)
     * @see Object#toString() 
     * @return string
     */
    @Override
    public String toString() {
        return "Resolution{" + "width=" + width + ", height=" + height + '}';
    }

    /**
     * Devuelve el ancho de la resolución.
     * @return ancho,
     */
    public int getWidth()  { 
        return width; 
    }
    
    /**
     * Devuelve el largo de la resolución.
     * @return largo.
     */
    public int getHeight() { 
        return height; 
    }

    /**
     * Devuelve la profundidad de bits.
     * @return frecuencia.
     */
    public int getBitDepth() {
        return bitDepth;
    }

    /**
     * Devuelve la frecuencia de actualización.
     * @return frecuencia.
     */
    public int getRefreshRate() {
        return refreshRate;
    }

    /**
     * Establece un nuevo ancho para la resolución.
     * @param width nuevo ancho.
     */
    public void setWidth(int width) {
        if (width < MIN_WIDTH)
            throw new IllegalArgumentException("Width=[" + width);
        
        this.width = width;
    }
    
    /**
     * Establece un nuevo largo para la resolución.
     * @param height nuevo largo.
     */
    public void setHeight(int height) {
        if (height < MIN_WIDTH)
            throw new IllegalArgumentException("Height=[" + height);
        
        this.height = height;
    }

    /**
     * Establece un nuevo valor para la profundidad de bits.
     * @param bitDepth porfundiad de bits.
     */
    public void setBitDepth(int bitDepth) {
        this.bitDepth = bitDepth;
    }

    /**
     * Establece una nueva frecuencia de actualización.
     * @param refreshRate nueva frecuencia.
     */
    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
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
        out.write(width, "width", MIN_WIDTH);
        out.write(height, "height", MIN_HEIGHT);
        out.write(bitDepth, "bitDepth", 0);
        out.write(refreshRate, "refreshRate", 0);
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
        width  = in.readInt("width", MIN_WIDTH);
        height = in.readInt("height", MIN_HEIGHT);
        bitDepth    = in.readInt("bitDepth", bitDepth);
        refreshRate = in.readInt("refreshRate", refreshRate);
    }

    /**
     * Genera un codigo hash.
     * @see Object#hashCode() 
     * @return número hash.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.width;
        hash = 19 * hash + this.height;
        hash = 19 * hash + this.bitDepth;
        hash = 19 * hash + this.refreshRate;
        return hash;
    }

    /**
     * Compara un objeto para determinar su igualdad con este objeto.
     * @see Object#equals(java.lang.Object)  
     * @param obj objeto a comparar.
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
        final AWTResolution other = (AWTResolution) obj;
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