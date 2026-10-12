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
package org.je3gl.box2d.util.pool;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import org.je3gl.scene.debug.custom.DebugGraphics;
import org.je3gl.util.Arg;
import org.je3gl.util.Checks;
import org.je3gl.util.pool.PoolHandler;

/**
 *
 * @author wil
 */
public class BitmapTextHandler implements PoolHandler<BitmapText> {

    public BitmapTextHandler() {
    }

    @Override
    public BitmapText create(Arg... args) {
        Checks.checkArgs(1, args);
        DebugGraphics graphics = (DebugGraphics) args[0].getSource();
        BitmapFont font = graphics.getBitmapFont("path://jMe3GL2/Fonts/ProggyClean.fnt");
        return graphics.createBitmapText(font, null);
    }

    @Override
    public boolean validate(BitmapText o, Arg... args) {
        if (o == null) {
            return false;
        }
        return o.getParent() == null;
    }

    @Override
    public void destroy(BitmapText o) {
        o.removeFromParent();
    }    
}
