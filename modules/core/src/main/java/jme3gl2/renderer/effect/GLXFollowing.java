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
package jme3gl2.renderer.effect;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

/**
 *
 * @author wil
 */
public class GLXFollowing extends AbstractGLXEffect {
    
    private Spatial target;
    private Vector3f translation;
    
    private Vector2f offset;
    private float followInterpolationAmount;

    public GLXFollowing(float followInterpolationAmount) {
        this(null, followInterpolationAmount);
    }
    
    public GLXFollowing(Spatial target, float followInterpolationAmount) {
        this.target = target;
        this.followInterpolationAmount = followInterpolationAmount;
        this.offset = new Vector2f();
    }

    public Spatial getTarget() {
        return target;
    }

    public void setOffset(Vector2f offset) {
        if (offset == null)
            offset = new Vector2f();
        this.offset = offset;
    }
    
    public void setTarget(Spatial target) {
        if (translation == null && target != null) {
            translation = target.getLocalTranslation();
        }
        this.target = target;
    }

    public void setFollowInterpolationAmount(float followInterpolationAmount) {
        this.followInterpolationAmount = followInterpolationAmount;
    }

    public Vector2f getOffset() {
        return offset;
    }

    public float getFollowInterpolationAmount() {
        return followInterpolationAmount;
    }
    
    @Override
    protected void effectUpdate(float tpf) {
        Camera cam   = camera.getCamera();
        Vector3f loc = cam.getLocation();
        
        Vector3f location = getTranslation();
        GLXClipping glxc = camera.getEffect(GLXClipping.class);
        if (glxc != null) {
            Vector2f clip = glxc.clipping(location, offset);
            location.x = clip.x;
            location.y = clip.y;
        } else {
            location.x += offset.x;
            location.y += offset.y;
        }
        
        if (followInterpolationAmount > 0) {
            cam.setLocation(loc.interpolateLocal(location, followInterpolationAmount * tpf).setZ(loc.z));
        } else {
            cam.setLocation(location.setZ(loc.z));
        }
    }
    
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
