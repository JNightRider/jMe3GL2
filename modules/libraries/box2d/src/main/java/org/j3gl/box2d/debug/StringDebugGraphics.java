/*
BSD 3-Clause License

Copyright (c) 2023-2026, Night Rider (Wilson)

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
package org.j3gl.box2d.debug;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import org.je3gl.scene.debug.custom.DebugGraphics;

/**
 * Class responsible for implementing the {@link org.je3gl.scene.debug.custom.DebugGraphics} interface
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class StringDebugGraphics implements DebugGraphics {
    /** Resource manager <code>JME</code>. */
    private final AssetManager assetManager;

    /**
     * Constructor.
     * @param assetManager object
     */
    public StringDebugGraphics(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
 
    /* (non-Javadoc)
     * @see org.je3gl.scene.debug.custom.DebugGraphics#getBitmapFont(java.lang.String) 
     */
    @Override
    public BitmapFont getBitmapFont(String name) {
        if (name != null && name.startsWith("path://")) {
            return assetManager.loadFont(name.substring(7, name.length()));
        }
        return assetManager.loadFont("Interface/Fonts/Console.fnt");
    }

    /* (non-Javadoc)
     * @see org.je3gl.scene.debug.custom.DebugGraphics#createBitmapText(com.jme3.font.BitmapFont, java.lang.String) 
     */
    @Override
    public BitmapText createBitmapText(BitmapFont font, String value) {
        BitmapText text = font.createLabel(value);
        text.setSize(0.35f);
        return text;
    }

    /*(non-Javadoc)
     * @see org.je3gl.scene.debug.custom.DebugGraphics#getColor(java.lang.String) 
     */
    @Override
    public ColorRGBA getColor(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
