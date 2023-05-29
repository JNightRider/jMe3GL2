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
package jMe3GL2.scene.tile;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import java.io.IOException;

/**
 * Un objeto de la clase <code>Tile</code> se encarga de gestionar los datos
 * de un azulejo en escena.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT 
 * @since 1.5.0
 */
public class Tile implements Savable, Cloneable {
    
    /** Propiedades de este objeto. */
    private Properties properties;

    /**
     * Constructor predeterminado de la clase <code>Tile</code>.
     */
    public Tile() {
        this.properties = new Properties();
    }
    
    /**
     * Devuelve el id unico para este {@link Tile}.
     * @return id.
     */
    public String getId() {
        return properties.getProperty("Id");
    }    
    
    /**
     * Devuelve un vector con las coordenadas de traslación.
     * @return posición.
     */
    public Vector3f getTranslation() {
        return properties.getProperty("Translation", new Vector3f());
    }
    
    /**
     * Devuelve la fila para la textura a utiliza en el mapa de escena.
     * @return fila.
     */
    public int getRow() {
        return properties.getProperty("Row", 0);
    }    
    
    /**
     * Devuelve la columna de la textura a utiliza en el mapa de escena.
     * @return columna.
     */
    public int getColumn() {
        return properties.getProperty("Column", 0);
    }

    /**
     * Devuelve las propiedades.
     * @return propiedades
     */
    public Properties getProperties() {
        return properties;
    }
    
    /**
     * Establece una nueva propiedad.
     * @param properties propiedad nueva.
     */
    public void setProperties(Properties properties) {
        if (properties == null) {
            throw new NullPointerException("Properties is null");
        }
        this.properties = properties;
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
        out.write(properties, "properties", null);
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
        properties = (Properties) in.readSavable("properties", properties);
    }
}
