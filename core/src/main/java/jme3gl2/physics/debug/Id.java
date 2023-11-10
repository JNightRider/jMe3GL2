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
package jme3gl2.physics.debug;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Clase encargado de gestionar un identificador unico para los cuerpos físicos
 * que se depuran en tiempo real.
 *
 * @author wil
 * @version 1.0-SNAPSHOT
 *
 * @since 2.5.0
 */
public final class Id {
    
    /** Identificadores. */
    private static final Id IDS;

    /*
        Bloque de codigo estatico que inicializa el objeto de gestionamiento
    de los identificadores. 
    */
    static {
         IDS = new Id();
     }
    
    /**
     * Método encargado de devolver la instancie del gestor de identificadores.
     * @return instancie de la clase <code>Id</code>.
     */
    public static Id getInstance() {
        return Id.IDS;
    }
    
    /**
     * Mapa para los identificadores físicos.
     */
    private final Map<Object, UUID> mapUUID
            = new HashMap<>();

    /**
     * Constructor de la clase <code>Ids</code>.
     */
    public Id() {
    }

    /**
     * (non-JavaDoc)
     *
     * @see java.lang.String#toString()
     * @return java.lang.String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("** IDS **")
                .append('\n');
        for (final Map.Entry<Object, UUID> entry : this.mapUUID.entrySet()) {
            sb.append(entry.getValue());
            sb.append(" : ")
              .append(entry.getKey())
              .append('\n');
        }

        sb.append("### FIN ###");
        return String.valueOf(sb);
    }
    
    /**
     * Método encargado de devolver o generar un nuevo id para un objeto
     * especifico dada.
     * 
     * @param obj objeto-id.
     * @return clave generada-buscada.
     */
    public String getIdString(Object obj) {
        return String.valueOf(getId(obj));
    }
    
    /**
     * Método encargado de devolver o generar un nuevo id para un objeto
     * especifico dada.
     * 
     * @param obj objeto-id.
     * @return clave generada-buscada.
     */
    public UUID getId(Object obj) {
        return getUUUID(obj, true);
    }
    
    /**
     * Método encargado de liminar todo los id que no aparecen en la nueva lista.
     * @param uuids lista contenido.
     * @return {@code true} si hay cambios en los ids.
     */
    public boolean retain(Collection<UUID> uuids) {
        return this.mapUUID.values().retainAll(uuids);
    }
    
    /**
     * Elimina los <b>ID</b> si uso o no requeridos.
     * @param objs lista de objeto si uso.
     */
    public void clear(Object... objs) {
        for (final Object element : objs) {
            this.mapUUID.remove(element);
        }
    }
    
    /**
     * Destrute todo los ids.
     */
    public void destroy() {
        this.mapUUID.clear();
    }
    
    /**
     * Método encargado de buscar una id en la lista o bien crearla si no
     * es existente.
     * 
     * @param obj objeto clave del id.
     * @param autoCreate {@code true} si se desea auto-crear un nuevo id, de
     * lo contrario {@code false}.
     * @return objeto id a devolver.
     */
    private UUID getUUUID(Object obj, boolean autoCreate) {
        /*final*/UUID id = this.mapUUID.get(obj);
        if (id == null && autoCreate) {
            id = UUID.randomUUID();
            this.mapUUID.put(obj, id);
        }
        return id;
    }
}
