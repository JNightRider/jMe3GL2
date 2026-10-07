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
package org.je3gl.box2d.debug;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.je3gl.box2d.debug.batch.BatchSnapshot;
import org.je3gl.box2d.debug.batch.TextData;

/**
 *
 * @author wil
 */
public class PhysicsDebugSceneProcessor implements SceneProcessor {

    private final Object lock = new Object();

    private final AtomicReference<BatchSnapshot> snapshot = new AtomicReference<>(null);
    private final AtomicBoolean enabled = new AtomicBoolean(true);
    private final List<TextData> listText = new ArrayList<>();
    private RenderManager renderManager;

    private boolean initialized;

    public PhysicsDebugSceneProcessor(AssetManager assetManager) {
        snapshot.set(new BatchSnapshot(assetManager));
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public void setSnapshot(BatchSnapshot value) {
        synchronized (lock) {
            BatchSnapshot sb = snapshot.getAndSet(value);
            if (sb != null) {
                sb.clear();
            }
        }
    }

    public void addDrawString(float x, float y, int color, String value) {
        TextData data = new TextData();
        data.setColor(color);
        data.setPosition(new Vector3f(x, y, 0f));
        data.setText(value);
        listText.add(data);
    }
    
    public void clearDraw() {
        snapshot.get().clear();
        listText.clear();
    }
    
    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        renderManager = rm;
        initialized = true;
    }

    @Override
    public void reshape(ViewPort vp, int w, int h) {

    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void preFrame(float tpf) {

    }

    @Override
    public void postQueue(RenderQueue rq) {
        synchronized (lock) {
            BatchSnapshot sb = snapshot.get();
            if (sb != null && enabled.get()) {
                sb.flushDraw(renderManager);
            }
        }
    }

    @Override
    public void postFrame(FrameBuffer out) {
    }

    @Override
    public void cleanup() {
        synchronized (lock) {
            initialized = false;
            listText.clear();
            snapshot.get().clear();
        }
    }

    public List<TextData> getListText() {
        return listText;
    }

    public AtomicReference<BatchSnapshot> getSnapshot() {
        return snapshot;
    }

    public Object getLock() {
        return lock;
    }

    @Override
    public void setProfiler(AppProfiler profiler) {

    }
}
