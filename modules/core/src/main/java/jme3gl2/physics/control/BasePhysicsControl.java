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
package jme3gl2.physics.control;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.util.TempVars;

import jme3gl2.util.Converter;

import org.dyn4j.geometry.Transform;

/**
 * Interfaz <code>BasePhysicsControl</code> encargado de proprocionar métodos
 * base para el control de cuerpos físicos. Su trabajo consiste en aplicar los
 * cambios de estdos de un cuerpo, es decir su posición el mundos y/o rotación.
 * <p>
 * De manera predeterminada, se implementa un forma de aplicar la traslación y
 * rotación de los cuerpos físico a un <code>Spatial</code>, estos métodos son
 * <code>applyPhysicsRotation</code> y <code>applyPhysicsLocation</code>.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * @param <E> tipo-cuerpo
 * 
 * @since 2.5.0
 */
public interface BasePhysicsControl<E extends PhysicsBody2D> extends Control {
    
    /**
     * Devuelve el <code>Spatial</code> asignado a este cuerpo físico.
     * @param <T> tipo-spatial
     * @return <code>Spatial</code> JME.
     */
    public <T extends Spatial> T getJmeObject();
    
    /**
     * Método encargado de aplicar una rotación física.
     * @param physicBody cuerpo físico.
     */
    default void applyPhysicsRotation(final E physicBody) {
        final Transform trans = physicBody.getTransform();

        final float rotation = Converter.toFloat(trans.getRotationAngle());

        final TempVars tempVars = TempVars.get();
        final Quaternion quaternion = tempVars.quat1;
        quaternion.fromAngleAxis(rotation, new Vector3f(0.0F, 0.0F, 1.0F));

        getJmeObject().setLocalRotation(quaternion);

        tempVars.release();
    }

    /**
     * Método encargado de aplicar una traslación físico.
     * @param physicBody cuerpo físico.
     */
    default void applyPhysicsLocation(final E physicBody) {
        final Transform trans = physicBody.getTransform();

        final float posX = Converter.toFloat(trans.getTranslationX());
        final float posY = Converter.toFloat(trans.getTranslationY());

        final Spatial spatial = getJmeObject();
        spatial.setLocalTranslation(posX, posY, spatial.getLocalTranslation().z);
    }
}
