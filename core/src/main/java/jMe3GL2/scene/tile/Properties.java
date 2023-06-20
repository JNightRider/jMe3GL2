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
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Un <code>Properties</code> se encarga de guardar las propiedades de
 * un {@link Tile}.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 2.0.0
 */
@SuppressWarnings(value = {"unchecked"})
public class Properties implements Savable, Cloneable {
    
    /** Mapa de propiedades. */
    private HashMap<String, Property> properties;
    
    /**
     * Constructor predeterminado.
     */
    public Properties() {
        this.properties = new HashMap<>();
    }
    
    /**
     * Método encargado de generar un clon de este objeto.
     * @return clon objeto.
     */
    @Override
    public Properties clone() {
        try {
            Properties clon = (Properties) super.clone();
            clon.properties = (HashMap<String, Property>) properties.clone();
            for (final Map.Entry<String, Property> entry : clon.properties.entrySet()) {
                if (entry == null) 
                    continue;
                
                clon.properties.put(entry.getKey(), entry.getValue().clone());
            }
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    
    /**
     * Método encargado de determinar si existe una propiedad.
     * @param key nombre clave.
     * @return estado.
     */
    public boolean contains(String key) {
        return this.properties.containsKey(key);
    }
    
    /**
     * Elimina un datos.
     * @param key nombre clave.
     */
    public void remove(String key) {
        this.properties.remove(key);
    }
    
    /**
     * Establece una nueva propiedad.
     * @param <T> tipo de dato.
     * @param key nombre clave.
     * @param value valor a guardar.
     * @return este.
     */
    public <T extends Object> Properties setProperty(String key, T value) {
        this.properties.put(key, new Property(value));
        return this;
    }

    /**
     * Método encargado de buscar y devolver una propiedad.
     * @param <T> tipo de dato.
     * @param key nombre clave.
     * @param defaultVal valor predeterminado.
     * @return valor buscado.
     */
    public <T extends Object> T getProperty(String key, T defaultVal) {
        Property userData = this.properties.get(key);
        if (userData == null) {
            return defaultVal;
        }
        return (T) userData.getValue();
    }
    
    /**
     * Método encargado de buscar y devolver una propiedad.
     * @param <T> tipo de dato.
     * @param key nombre clave.
     * @return valor buscado.
     */
    public <T extends Object> T getProperty(String key) {
        return getProperty(key, null);
    }
    
    /**
     * Método encargado de gestionar los nombres claves de las propiedades.
     * @return nombres claves.
     */
    public Iterator<String> propertyNames() {
        return this.properties.keySet().iterator();
    }
    
    /**
     * Devuelve la cantidad de propiedades existentes.
     * @return cantidad.
     */
    public int size() {
        return this.properties.size();
    }
    
    /**
     * Devuelve {@code false} si no existe datos, de lo contrario {@code true}.
     * @return boolean.
     */
    public boolean isEmpty() {
        return this.properties.isEmpty();
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
        out.writeStringSavableMap(properties, "Properties", null);
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
        properties = (HashMap<String, Property>) in.readStringSavableMap("Properties", properties);
    }
}
