/* Copyright (c) 2009-2024 jMonkeyEngine.
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
package org.jegl.physics.debug;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.jme3.util.TempVars;

import org.jegl.physics.control.PhysicsBody2D;
import org.jegl.util.Converter;

import org.dyn4j.geometry.Transform;

/**
 * Abstract class in charge of managing the <code>Spatial</code> controls of the
 * physical bodies of the debugger.
 * @param <E> of type {@link org.jegl.physics.control.PhysicsBody2D}
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public abstract class AbstractPhysicsDebugControl<E extends PhysicsBody2D> extends AbstractControl {

    /** Debug status. */
    protected Dyn4jDebugAppState<E> dyn4jDebugAppState;

    /**
     * Constructor of class <code>AbstractPhysicsDebugControl</code>.
     * @param dyn4jDebugAppState Debug status
     */
    public AbstractPhysicsDebugControl(Dyn4jDebugAppState<E> dyn4jDebugAppState) {
        this.dyn4jDebugAppState = dyn4jDebugAppState;
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.scene.control.AbstractControl#controlRender(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort) 
     *
     * @param rm the render manager (unused)
     * @param vp the view port to render (unused)
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // do nothing
    }
    
    /**
     * Method responsible for applying physical rotation to the <code>Spatial</code>
     * associated with the body.
     * @param physicBody body
     */
    protected void applyPhysicsRotation(final E physicBody) {
        final Transform trans = physicBody.getTransform();
        final float rotation = Converter.toFloatValue(trans.getRotationAngle());

        final TempVars tempVars = TempVars.get();
        final Quaternion quaternion = tempVars.quat1;
        
        quaternion.fromAngleAxis(rotation, new Vector3f(0.0F, 0.0F, 1.0F));
        spatial.setLocalRotation(quaternion);
        tempVars.release();
    }
    
    /**
     * Method in charge of applying the physical position to the <code>Spatial</code>
     * associated with the body.
     * @param physicBody body
     */
    protected void applyPhysicsLocation(final E physicBody) {
        final Transform trans = physicBody.getTransform();
        final float posX = Converter.toFloatValue(trans.getTranslationX());
        final float posY = Converter.toFloatValue(trans.getTranslationY());
        
        spatial.setLocalTranslation(posX, posY, spatial.getLocalTranslation().z);
    }
}
