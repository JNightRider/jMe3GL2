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

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.je3gl.scene.debug.custom.DebugGraphics;

/**
 *
 * @author wil
 */
public class StringRender extends ShapeRender {
    
    /** Text debugger. */
    private Map<String, BitmapText> map = new HashMap<>();
    private List<Spatial> tmp = new ArrayList<>();
    private Node guiNode;

    private final List<StringData> list = new ArrayList<>();
    private final StringData.Pool dataPool = new StringData.Pool();
    
    public StringRender(Application app) {
        super(app.getAssetManager(), "String");
        if (app instanceof SimpleApplication) {
            guiNode = ((SimpleApplication) app).getGuiNode();
        }
    }

    public void addDrawString(float x, float y, ColorRGBA color, String value) {
        synchronized (lock) {
            if (guiNode == null) {
                return;
            }

            StringData data = dataPool.takePush();
            data.setPosition(x, y);
            data.setRGBA(color);
            data.setValue(value);

//            if (list.contains(data)) {
//                dataPool.takePop(data);
//            } else {
//                list.add(data);
//            }
        }
    }

    @Override
    public void flushDraw(RenderManager renderManager, boolean solid) {
        synchronized (lock) {
            for (int i = 0; i < list.size(); i++) {
                StringData data = list.get(i);
                BitmapText txt = map.get(data.getValue());                
                if (txt == null) {
                    txt = new BitmapText(assetManager.loadFont("jMe3GL2/Fonts/ProggyClean.fnt"));
                    txt.setText(data.getValue());
                    guiNode.attachChild(txt);
                }
                txt.setColor(data.getRGBA());
                txt.setLocalTranslation(data.getPosition());
                
                
                dataPool.takePop(data);
                list.remove(i);
                i--;
            }
        }
    }
}
