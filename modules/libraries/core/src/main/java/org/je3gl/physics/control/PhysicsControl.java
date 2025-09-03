/*
BSD 3-Clause License

Copyright (c) 2023-2025, Night Rider (Wilson)

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.je3gl.physics.control;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.util.TempVars;

import org.je3gl.physics.PhysicsSpace;
import org.je3gl.util.Converter;

import org.dyn4j.geometry.Transform;

/**
 * An Interface in charge of managing the control of the physical bodies used by 
 * the dyn4j engine, this interface is in charge of applying physics to the
 * <code>Spatial</code> of JME3.
 * @param <E> of type {@link org.je3gl.physics.control.PhysicsBody2D}
 * @author wil
 * @version 1.5.5
 * @since 1.0.0
 */
public interface PhysicsControl<E extends PhysicsBody2D> extends Control {
    
    /**
     * Returns the <code>Spatia</code> associated with the physical body.
     * @param <T> type <b>Spatial</b>
     * @return object
     */
    public <T extends Spatial> T getJmeObject();
    
    /**
     * Method responsible for applying physical rotation to the <code>Spatial</code>
     * associated with the body.
     * @param physicBody body
     */
    @Deprecated(since = "3.0.0")
    default void applyPhysicsRotation(final E physicBody) {
        final Transform trans = physicBody.getTransform();
        final float rotation = Converter.toFloatValue(trans.getRotationAngle());

        final TempVars tempVars = TempVars.get();
        final Quaternion quaternion = tempVars.quat1;
        
        quaternion.fromAngleAxis(rotation, new Vector3f(0.0F, 0.0F, 1.0F));
        getJmeObject().setLocalRotation(quaternion);
        tempVars.release();
    }
    
    /**
     * Method in charge of applying the physical position to the <code>Spatial</code>
     * associated with the body.
     * @param physicBody body
     */
    @Deprecated(since = "3.0.0")
    default void applyPhysicsLocation(final E physicBody) {
        final Transform trans = physicBody.getTransform();

        final float posX = Converter.toFloatValue(trans.getTranslationX());
        final float posY = Converter.toFloatValue(trans.getTranslationY());

        final Spatial spatial = getJmeObject();
        spatial.setLocalTranslation(posX, posY, spatial.getLocalTranslation().z);
    }
    
    /**
     * Establishes the physical space to which this physical control belongs.
     * @param physicsSpace a physical space
     */
    public void setPhysicsSpace(PhysicsSpace<E> physicsSpace);
    
    /**
     * Returns the physical space to which this physical control belongs.
     * @return a physical space
     */
    public PhysicsSpace<E> getPhysicsSpace();
    
    /**
     * Method in charge of enabling or disabling the control.
     * @param enabled control status
     */
    public void setEnabledPhysicsControl(boolean enabled);
    
    /**
     * Returns the current status of this control.
     * @return control status
     */
    public boolean isEnabledPhysicsControl();
}
