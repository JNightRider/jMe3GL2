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
package org.jegl.renderer.effect;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

/**
 * Class in charge of managing an effect where the camera follows a target.
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class GLXFollowing extends AbstractGLXEffect {
    
    /** Object/Target. */
    private Spatial target;
    /** Target position. */
    private Vector3f translation;
    
    /** offset. */
    private Vector2f offset;

    /**
     * Generate a new instance of this class <code>GLXFollowing</code>.
     * @param followInterpolationAmount interpolation speed (amount)
     */
    public GLXFollowing(float followInterpolationAmount) {
        this(null, followInterpolationAmount);
    }
    
    /**
     * Generate a new instance of this class <code>GLXFollowing</code>.
     * @param target object/target
     * @param followInterpolationAmount interpolation speed (amount)
     */
    public GLXFollowing(Spatial target, float followInterpolationAmount) {
        this.target = target;
        this.interpolationAmount = followInterpolationAmount;
        this.offset = new Vector2f();
    }

    /**
     * Returns the camera target.
     * @return target
     */
    public Spatial getTarget() {
        return target;
    }

    /**
     * Method in charge of establishing a new offset. If the value is
     * <code>null</code>, such a offset will be of <code>0</code>.
     * 
     * @param offset new offset point
     */
    public void setOffset(Vector2f offset) {
        if (offset == null)
            offset = new Vector2f();
        this.offset = offset;
    }
    
    /**
     * Method in charge of establishing a target to be followed by the camera in
     * the game scene.
     * 
     * @param target camera target
     */
    public void setTarget(Spatial target) {
        if (translation == null && target != null) {
            translation = target.getLocalTranslation();
        }
        this.target = target;
    }
    
    /**
     * Returns the value of the offset.
     * @return offset
     */
    public Vector2f getOffset() {
        return offset;
    }
    
    /*
     * (non-Javadoc)
     * @see jme3gl2.renderer.effect.AbstractGLXEffect#effectUpdate(float) 
     */
    @Override
    protected void effectUpdate(float tpf) {
        Camera cam   = camera.getCamera();
        Vector3f loc = cam.getLocation();
        
        Vector3f location = getTranslation();
        if (offset != null && (offset.x > FastMath.FLT_EPSILON || offset.y > FastMath.FLT_EPSILON)) {
            GLXClipping glxc = camera.getEffect(GLXClipping.class);
            if (glxc != null) {
                Vector2f clip = glxc.clipping(location, offset);
                location.x = clip.x;
                location.y = clip.y;
            } else {
                location.x += offset.x;
                location.y += offset.y;
            }
        }
        
        if (interpolationAmount > 0) {
            cam.setLocation(loc.interpolateLocal(location, interpolationAmount * tpf).setZ(loc.z));
        } else {
            cam.setLocation(location.setZ(loc.z));
        }
    }
    
    /**
     * Responsible method of managing the position (translation) of the target.
     * @return vectoe3f
     */
    private Vector3f getTranslation() {
        if (target == null) {
            if (translation == null) {
                return new Vector3f();
            }
            return translation.clone();
        }
        return target.getLocalTranslation().clone();
    }
}
