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
package jMe3GL2.scene.control;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Un objeto de la clase <code>RibbonBox</code> se encarga de gestionar la
 * textura y los cuadros de la animación por medio del control 
 * {@link RibbonBoxAnimationSprite}.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 *
 * @since 2.0.0
 */
public class RibbonBox implements Savable, Cloneable {

    /** Logger de la clase. */
    private static final Logger LOG = Logger.getLogger(RibbonBox.class.getName());

    /** Vector encargado de almacenar las columnas y files de la malla. */
    private Vector2f columnsAndRows 
            = new Vector2f(1.0F, 1.0F);
    
    /** Textura de esta cinta. */
    private Texture texture;
    
    /** Cuadro de animación. */
    private int[] frames;

    /**
     * {@code true} si esta cinta se está utilizando par la animación
     * de un modelo 2D, de lo contrario {@code false}.
     */
    private boolean inAction;
    
    /**
     * Constructor predeterminado.
     */
    protected RibbonBox() {
        this(null, new int[0], 0, 0);
    }
    
    /**
     * Intancie un nuevo objeto de la clase <code>RibbonBox</code> para generar
     * una cita de animación.
     * 
     * @param texture textura animada.
     * @param frames cuadro de animación.
     * @param columns número de columnas para la nueva cinta.
     * @param rows número de filas para la nueva cinta.
     */
    public RibbonBox(Texture texture, int[] frames, int columns, int rows) {
        this.columnsAndRows.set(columns, rows);
        this.texture = texture;
        this.frames = frames;
    }

    /**
     * Método encargado de generar un clon de este objeto.
     * @see Object#clone() 
     * @return Clon objeto.
     */
    @Override
    public RibbonBox clone() {
        try {
            RibbonBox clon = (RibbonBox) super.clone();
            clon.texture = texture != null ? texture.clone() : null;
            clon.frames  = frames  != null ? frames.clone()  : null;
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    /**
     * Establece el estado de acción de esta cinta.
     * @param inAction estado.
     */
    void setInAction(boolean inAction) {
        this.inAction = inAction;
    }

    /**
     * Establece una nueva textura animada.
     * @param texture nueva textura.
     */
    public void setTexture(Texture texture) {
        if (inAction) {
            LOG.log(Level.WARNING, "This animation is in use.");
        } else {
            this.texture = texture;
        }
    }

    /**
     * Establece un nuevo areglo de cuadros para la animación.
     * @param frames nuevos cuadros.
     */
    public void setFrames(int[] frames) {
        if (inAction) {
            LOG.log(Level.WARNING, "This animation is in use.");
        } else {
            this.frames = frames;
        }
    }
    
    /**
     * Devuelve la textura actual.
     * @return textura.
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Devuelve los cuadro de la animación actual.
     * @return cuadro animación.
     */
    public int[] getFrames() {
        return frames;
    }

    /**
     * Método encargado de gestionar el estado de está cinta.
     * @see RibbonBox#inAction
     * 
     * @return boolean.
     */
    public boolean inAction() {
        return inAction;
    }
    
    /**
     * Devuelve el número de columnas.
     * @return Columnas.
     */
    public int getColumns() {
        return (int) columnsAndRows.x;
    }
    
    /**
     * Devuelve el número de filas.
     * @return Filas.
     */
    public int getRows() {
        return (int) columnsAndRows.y;
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
        out.write(frames, "frames", null);
        out.write(columnsAndRows , "columnsAndRows ", null);
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
        columnsAndRows  = (Vector2f) in.readSavable("columnsAndRows ", columnsAndRows);
        texture = (Texture) in.readSavable("texture", texture);
        frames  = in.readIntArray("frames", frames);
    }
}
