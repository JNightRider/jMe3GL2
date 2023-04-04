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
package jMe3GL2.physics.collision;

import com.jme3.export.Savable;
import org.dyn4j.geometry.Convex;

/**
 * Un objeto de la clase <code>AbstractCollisionShape</code> se encarga de
 * crear una forma de colisón para los cuerpos físicos.
 * <p>
 * Extienda de esta clase para crear una forma, es decir la colisión del
 * cuerpo rigido.</p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 1.0.0
 * @param <E> tipo de colisión {@code Convex}.
 */
public abstract class AbstractCollisionShape<E extends Convex> implements Savable {
    
    /** forma física. */
    protected E collisionShape;

    /**
     * Instancie un nuevo <code>AbstractCollisionShape</code>.
     */
    public AbstractCollisionShape() { }
    
    /**
     * Método encargado de crear la forma física.
     * @return {@link AbstractCollisionShape}.
     */
    public abstract AbstractCollisionShape<E> createCollisionShape();

    /**
     * Devuelve la forma física.
     * @return forma física del cuerpo.
     */
    public E getCollisionShape() {
        return collisionShape;
    }
}
