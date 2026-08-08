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
package org.je3gl.box2d.debug.renderer;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;
import org.je3gl.scene.debug.custom.DebugGraphics;

/**
 *
 * @author wil
 */
public class StringRender extends ShapeRender {
    
    /** Text debugger. */
    private BitmapText bitmapText;

    private final List<StringData> list = new ArrayList<>();
    private final StringData.Pool dataPool = new StringData.Pool();
    
    public StringRender(AssetManager assetManager, ViewPort viewPort, DebugGraphics graphics) {
        super(assetManager, "String");        
        BitmapFont font = graphics.getBitmapFont(null);
        this.bitmapText = graphics.createBitmapText(font, "");
    }

    public void addDrawString(float x, float y, ColorRGBA color, String value) {
        synchronized (lock) {
            StringData data = dataPool.takePush();
            data.setPosition(x, y);
            data.setRGBA(color);
            data.setValue(value);

            if (list.contains(data)) {
                dataPool.takePop(data);
            } else {
                list.add(data);
            }
        }
    }
    
    @Override
    public void flushDraw(RenderManager renderManager, boolean solid) {
        synchronized (lock) {
            for (int i = 0; i < list.size(); i++) {
                StringData data = list.get(i);
                
//                bitmapText.setText(data.getValue());
                bitmapText.setColor(data.getRGBA());
                bitmapText.setSize(0.25f);
                
                bitmapText.updateLogicalState(0);
                bitmapText.render(renderManager, ColorRGBA.Blue);
                for (Spatial child : bitmapText.getChildren()) {
                    if (child instanceof Geometry page) {
                        page.setLocalTranslation(data.getPosition());
                        page.updateGeometricState();
                        page.updateModelBound();
                        
                        renderManager.renderGeometry(page);
                    }
                }
                
                dataPool.takePop(data);
                list.remove(i);
                i--;
            }
        }
    }
}
