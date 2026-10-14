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
package org.je3gl.box2d;

import com.jme3.app.AppTask;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import org.box2d.jni.b2DebugDraw;
import org.box2d.jni.b2WorldDef;
import org.box2d.jni.b2WorldId;

import static org.box2d.jni.include.Box2d.*;
import static org.box2d.jni.include.Types.*;
import static org.box2d.jni.system.Callbacks.*;
import org.je3gl.box2d.control.PhysicsBody2D;
import org.je3gl.box2d.debug.Drawable;
import org.je3gl.box2d.debug.PhysicsDebugAppState;
import org.je3gl.listener.VoidCallable;

/**
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class PhysicsSpace implements AutoCloseable {

    private final AtomicReference<List<Drawable>> drawables = new AtomicReference<>(new ArrayList<>());
    private final ConcurrentLinkedQueue<AppTask<?>> taskQueue = new ConcurrentLinkedQueue<>();

    private int subStepCount = 4;

    protected b2WorldId worldId;

    protected b2DebugDraw debugDraw;

    protected final Box2dAppState box2dAppState;

    protected PhysicsDebugAppState debugAppState;
    protected AxisType axisType = AxisType.AXIS_XYO;

    public PhysicsSpace(b2WorldDef worldDef, Box2dAppState appState) {
        if (worldDef == null) {
            throw new NullPointerException("b2WorldDef is null");
        }
        box2dAppState = appState;
        worldId = b2CreateWorld(worldDef, b2WorldId.malloc());
        debugDraw = b2DefaultDebugDraw(b2DebugDraw.calloc());
    }

    public void setDebugAppState(PhysicsDebugAppState debugAppState) {
        this.debugAppState = debugAppState;
    }

    public void addDrawable(Drawable drawable) {
        this.drawables.get().add(drawable);
    }

    public void removeDrawable(Drawable drawable) {
        this.drawables.get().remove(drawable);
    }

    public void setSubStepCount(int subStepCount) {
        this.subStepCount = subStepCount;
    }

    public void addBody(PhysicsBody2D body2D) {
        b2CreateBody(worldId, body2D.getBodyDef(), body2D.getBodyId());
        body2D.setPhysicsSpace(this);
    }

    public void removeBody(PhysicsBody2D body2D) {
        try (body2D) {
            b2DestroyBody(body2D.getBodyId());
            body2D.setPhysicsSpace(null);
        }
    }

    public <V> Future<V> enqueue(Callable<V> callable) {
        AppTask<V> task = new AppTask<>(callable);
        taskQueue.add(task);
        return task;
    }

    public Future<Void> enqueue(Runnable callable) {
        return enqueue((VoidCallable) callable::run);
    }

    /**
     * Runs tasks enqueued via {@link #enqueue(Callable)}
     */
    protected void runQueuedTasks() {
        AppTask<?> task;
        while ((task = taskQueue.poll()) != null) {
            if (!task.isCancelled()) {
                task.invoke();
            }
        }
    }

    public void update(float tpf) {
        runQueuedTasks();
        b2World_Step(worldId, tpf, subStepCount);
        if (isEnableDebugger()) {
            b2World_Draw(worldId, debugDraw);
            if (debugAppState.isInitialized() && debugAppState.getDebugProcessor().isInitialized()) {
                for (Drawable drawable : drawables.get()) {
                    drawable.rawDrawing(debugAppState.getDebugProcessor().getDraw());
                }
            }
        }
    }

    public void setAxisType(AxisType axisType) {
        this.axisType = axisType;
    }

    public AxisType getAxisType() {
        return axisType;
    }

    public b2WorldId getWorldId() {
        return worldId;
    }

    public int getSubStepCount() {
        return subStepCount;
    }

    public b2DebugDraw getDebugDraw() {
        return debugDraw;
    }

    protected boolean isEnableDebugger() {
        return debugAppState != null;
    }

    public boolean isParallel() {
        return box2dAppState.isParallel();
    }

    @Override
    public void close() {
        b2DestroyWorld(worldId);
        b2FreeCallbacks();
        debugDraw.close();
        worldId.close();
        debugAppState = null;
    }
}
