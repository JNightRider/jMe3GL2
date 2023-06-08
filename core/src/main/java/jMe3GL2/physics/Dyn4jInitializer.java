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
package jMe3GL2.physics;

import jMe3GL2.physics.control.PhysicsBody2D;

/**
 * Un <code>Dyn4jInitializer</code> es una interfaz encargado de proprocionar 
 * una manera de inicializar nuetros objetos o cuerpos físico estando 
 * inicializado el objeto {@link Dyn4jAppState} para el contro de la física.
 * <p>
 * Esta interzas solo es un auxilizar, dado que esto se puede evitar usando
 * métodos propios que más nos convengan.
 * </p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 2.0.0
 * @param <E> del tipo {@link PhysicsBody2D}
 */
public interface Dyn4jInitializer<E extends PhysicsBody2D> {
    
    /**
     * Método encargado de inicializar los cuerpos, esto sucede cuando la clase
     * {@link Dyn4jAppState} este preparado por parte del motor. Es decir que
     * está inicializado.
     * @param physicsSpace espacio de la física.
     */
    void ready(PhysicsSpace<E> physicsSpace);
}
