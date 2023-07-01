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

import com.jme3.scene.Geometry;

import jMe3GL2.physics.PhysicsSpace;
import jMe3GL2.physics.control.PhysicsBody2D;

/**
 * Interfaz <code>SpritesheetPhysics</code> es el encargado de administrar los
 * cambios de un {@link TileMap}.
 * <p>
 * Con esta interfaz podemos saber y administrar cada cambio que hagan las
 * geometrías del nodo padre.</p>
 * 
 * <p>
 * Hay que tener en cuenta que el {@link TileMap} hereda de 
 * <code>GeometryGroupNode</code> por lo que la mayoría de los eventos de esta
 * interfaz corresponden a la implementación abstracta de los métodos del
 * padre de la clase {@link TileMap}</p>
 * 
 * @author wil
 * @version 1.0.1
 * 
 * @since 2.0.0
 */
public interface SpritesheetPhysics {
    
    /**
     * Método encargado de establecer el espacio físico de los cuerpos
     * rigidos del mapa de azulejos.
     * @param physicsSpace espacio-físico.
     */
    public void setPhysicsSpace(PhysicsSpace<PhysicsBody2D> physicsSpace);
    
    /**
     * Se activa cuando de elimina una geometría del nodo padre.
     * @param geom geometría liminada.
     */
    public void onDetachTile(Geometry geom);
    
    /**
     * Se activa cuando una neva geometría es agregada al nodo padre.
     * @param geom nueva geometría;
     */
    public void onAttachTile(Geometry geom);
    
    /**
     * Se activa la geometría ya no esta asociada con el nodo padre.
     * @param geom geometría
     */
    public void onTileUnassociated(Geometry geom);
    
    /**
     * Se activa cuando se aplica un cambio a una geometría.
     * @param geom geometría
     */
    public void onTransformChange(Geometry geom);
    
    /**
     * Se activa cuando se aplica un cambio en el materia de una geometría.
     * @param geom geometría
     */
    public void onMaterialChange(Geometry geom);
    
    /**
     * Se activa cuando se cambia la malla o el nivel de una geometría.
     * @param geom geometría
     */
    public void onMeshChange(Geometry geom);
}
