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
package org.je3gl.physics.debug;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;

import org.je3gl.scene.debug.custom.DebugGraphics;
import static org.je3gl.physics.debug.Dyn4jDebugColor.*;

/**
 * Class responsible for implementing the {@link org.je3gl.scene.debug.custom.DebugGraphics} interface
 * @author wil
 * @version 1.0.0
 * @since 3.0.03
 */
public class Dyn4jDebugGraphics implements DebugGraphics {
    
    //--------------------------------------------------------------------------
    //                        Constants - Dyn4jDebugGraphics
    //--------------------------------------------------------------------------
    /** Code name for debug color: DISABLED. */
    public static final String GL_DEBUG_DISABLED  = "DISABLED";
    /** Code name for debug color: SENSOR. */
    public static final String GL_DEBUG_SENSOR    = "SENSOR";
    /** Code name for debug color: BULLET. */
    public static final String GL_DEBUG_BULLET    = "BULLET";
    /** Code name for debug color: STATIC. */
    public static final String GL_DEBUG_STATIC    = "STATIC";
    /** Code name for debug color: KINEMATIC. */
    public static final String GL_DEBUG_KINEMATIC = "KINEMATIC";
    /** Code name for debug color: AT_RESET. */
    public static final String GL_DEBUG_AT_RESET  = "AT_RESET";
    /** Code name for debug color: DEFAULT. */
    public static final String GL_DEBUG_DEFAULT   = "DEFAULT";
    /** Code name for debug color: BOUNDS. */
    public static final String GL_DEBUG_BOUNDS    = "BOUNDS";
    
    //--------------------------------------------------------------------------
    //                        Class - Dyn4jDebugGraphics
    //--------------------------------------------------------------------------
    /** Resource manager <code>JME</code>. */
    private final AssetManager assetManager;

    /**
     * Constructor.
     * @param assetManager object
     */
    public Dyn4jDebugGraphics(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
    
    /*(non-Javadoc)
     * @see org.je3gl.scene.debug.custom.DebugGraphics#getColor(java.lang.String) 
     */
    @Override
    public ColorRGBA getColor(String name) {
        switch (name) {
            case GL_DEBUG_AT_RESET: return AT_RESET;
            case GL_DEBUG_BULLET:   return BULLET;
            case GL_DEBUG_DEFAULT:   return DEFAULT;
            case GL_DEBUG_DISABLED:  return DISABLED;
            case GL_DEBUG_KINEMATIC: return KINEMATIC;
            case GL_DEBUG_SENSOR: return SENSOR;
            case GL_DEBUG_STATIC: return STATIC;
            case GL_DEBUG_BOUNDS: return BOUNDS;
            default:
                throw new AssertionError();
        }
    }

    /* (non-Javadoc)
     * @see org.je3gl.scene.debug.custom.DebugGraphics#getBitmapFont(java.lang.String) 
     */
    @Override
    public BitmapFont getBitmapFont(String name) {
        if (name.startsWith("path//")) {
            return assetManager.loadFont(name.substring(6, name.length()));
        }
        return assetManager.loadFont("Interface/Fonts/Default.fnt");
    }

    /* (non-Javadoc)
     * @see org.je3gl.scene.debug.custom.DebugGraphics#createBitmapText(com.jme3.font.BitmapFont, java.lang.String) 
     */
    @Override
    public BitmapText createBitmapText(BitmapFont font, String value) {
        BitmapText text = font.createLabel(value);
        text.setSize(0.1F);
        return text;
    }
}
