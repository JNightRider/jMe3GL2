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
package org.je3gl.box2d.debug.draw;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.List;

import org.box2d.jni.b2Pos;
import org.box2d.jni.system.MemoryUtil;

import org.je3gl.box2d.util.pool.BitmapTextHandler;
import org.je3gl.scene.debug.custom.DebugGraphics;
import org.je3gl.util.ObjectPool;
import org.je3gl.utilities.ColorUtilities;

/**
 *
 * @author wil
 */
public class BatchText {

    private static final class Data {

        final Vector3f position;
        final ColorRGBA color;
        final String text;

        private Data(Vector3f position, ColorRGBA color, String text) {
            this.position = position;
            this.color = color;
            this.text = text;
        }

        static Data create(b2Pos p, long s, int color) {
            return new Data(
                    new Vector3f(p.x().floatValue(), p.y().floatValue(), 0.0f),
                    ColorUtilities.fromIntRGBA(color, 1.0f),
                    MemoryUtil.memUTF(s)
            );
        }
    }

    private final List<Data> dataList = new ArrayList<>();

    private DebugGraphics graphics;

    private final ObjectPool<BitmapText> textPool = new ObjectPool<>(new BitmapTextHandler());
    private List<BitmapText> textList = new ArrayList<>();

    public BatchText() {
    }

    public void setGraphics(DebugGraphics graphics) {
        this.graphics = graphics;
    }

    public void addData(b2Pos p, long s, int color) {
        dataList.add(Data.create(p, s, color));
    }

    public void draw(Node rootNode, Camera camera, int width, int height) {
        List<BitmapText> cache = textList;
        textList = new ArrayList<>();

        for (Data data : dataList) {
            Vector3f pos = camera.getScreenCoordinates(data.position);

            if ((pos.x >= 0 && pos.x <= width) && (pos.y >= 0 && pos.y <= height)) {

                BitmapText text;
                if (!cache.isEmpty()) {
                    text = cache.get(0);
                    rootNode.attachChild(text);

                    cache.remove(0);
                    textList.add(text);
                } else {
                    text = textPool.takePush(graphics);
                    textList.add(text);
                    rootNode.attachChild(text);
                }
                text.setText(data.text);
                text.setSize(16.5f);
                text.setColor(data.color);
                text.setLocalTranslation(
                        pos.x - text.getLineWidth() / 2f,
                        pos.y + text.getLineHeight(),
                        pos.z
                );
            }
        }
        for (BitmapText text : cache) {
            text.removeFromParent();
            textPool.takePop(text);
        }
    }

    public void clear() {
        dataList.clear();
    }
}
