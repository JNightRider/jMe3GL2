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
package jme3gl2.physics.debug;

import com.jme3.math.ColorRGBA;

/**
 * Class used to enlist the colors of the physical shape debugger.
 * @author wil
 * @version 1.0.0
 * @since 2.5.0
 */
public final class Dyn4jDebugColor {
    
    /** Color used for disabled physical bodies. */
    public static final ColorRGBA DISABLED = new ColorRGBA(0.2F, 0.2F, 0.2F, 1.0F);
    
    /** Color used for bodies defined as: sensor. */
    public static final ColorRGBA SENSOR = new ColorRGBA(0.5F, 0.5F, 0.5F, 1.0F);
    
    /** Color used for bodies defined as: bullet. */
    public static final ColorRGBA BULLET = new ColorRGBA(1.0F, 0.09F, 0.267F, 1.0F);
    
    /** Color used for bodies defined as: static. */
    public static final ColorRGBA STATIC = new ColorRGBA(0.0F, 0.0F, 1.0F, 1.0F);
    
    /** Color used for bodies defined as: kinematic. */
    public static final ColorRGBA KINEMATIC = new ColorRGBA(0.0F, 1.0F, 1.0F, 1.0F);
    
    /** Color used for resetting objects. */
    public static final ColorRGBA AT_RESET = new ColorRGBA(251.0F / 255.0F, 130.0F / 255.0F, 0.0F, 1.0F);
    
    /** Color used by any physical body. */
    // 0.267F, 0.541F, 1.0F, 1.0F
    public static final ColorRGBA DEFAULT = new ColorRGBA(1.0F, 0.0F, 1.0F, 1.0F);
    
    /** Color used for <code>Bounds</code>. */
    public static final ColorRGBA BOUNDS = new ColorRGBA(0.502F, 0.0F, 0.502F, 1.0F);
    
    /** Constructor owned. */
    private Dyn4jDebugColor() {
    }
}
