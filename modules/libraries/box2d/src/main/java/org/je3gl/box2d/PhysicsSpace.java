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

import org.box2d.jni.b2DebugDraw;
import org.box2d.jni.b2WorldDef;
import org.box2d.jni.b2WorldId;

import static org.box2d.jni.include.Box2d.*;
import static org.box2d.jni.include.Types.*;
import static org.box2d.jni.system.Callbacks.*;

import org.je3gl.box2d.control.PhysicsBody2D;
import org.je3gl.box2d.debug.Drawable;
import org.je3gl.box2d.debug.PhysicsDebugAppState;
import org.je3gl.box2d.listener.PhysicsProcess;
import org.je3gl.listener.VoidCallable;

/**
 * Class in charge of managing a physical space that connects {@code box2d-jni}
 * with {@code JME} where physical bodies can be formed.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class PhysicsSpace implements AutoCloseable {

    /**
     * List of drawable elements; these objects are drawn after the Box2D
     * debugger has finished processing its data queue.
     */
    private final List<Drawable> drawables = new ArrayList<>();

    /**
     * List of atomic operations that are executed before each new update of the
     * physical world.
     */
    private final ConcurrentLinkedQueue<AppTask<?>> taskQueue = new ConcurrentLinkedQueue<>();

    /**
     * If the physical space runs in parallel, this list handles invoking
     * updates from the thread in which that physical space is operating; this
     * prevents the Box2D debugger’s synchronization from breaking. Otherwise,
     * it has no special effect and functions like a standard, linear update
     * call.
     *
     * @see ThreadingType
     */
    private final List<PhysicsProcess> physicsProcesses = new ArrayList<>();

    /**
     * The number of sub-steps, increasing the sub-step count can increase
     * accuracy. Usually 4.
     */
    private int subStepCount = 4;

    /**
     * The id that points to the Box2D physical world; this object provides
     * access to physics management.
     */
    protected b2WorldId worldId;

    /**  The object that manages the behavior of the Box2D debugger. */
    protected b2DebugDraw debugDraw;
    /** The state that manages this physical space. */
    protected final Box2dAppState box2dAppState;

    /**
     * A state that manages Box2D debugging; this is where drawable objects are
     * created and rendered.
     */
    protected PhysicsDebugAppState debugAppState;

    /**
     * The axis type is the way in which the positions of physical objects are
     * applied with respect to the three coordinates of JME3's 3D space; changing
     * this axis implies a change in the way objects are controlled at the three
     * points (x, y, z).
     */
    protected AxisType axisType = AxisType.AXIS_XYO;

    /**
     * Generates a new instance of the <code>PhysicsSpace</code> class where to 
     * generate a 2D world provided by {@code Box2d}
     *
     * @param worldDef b2WorldDef
     * @param appState Box2dAppState
     */
    public PhysicsSpace(b2WorldDef worldDef, Box2dAppState appState) {
        if (worldDef == null) {
            throw new NullPointerException("b2WorldDef is null");
        }
        box2dAppState = appState;
        worldId = b2CreateWorld(worldDef, b2WorldId.malloc());
        debugDraw = b2DefaultDebugDraw(b2DebugDraw.calloc());
    }

    /**
     * Add a new process to the list.
     *
     * @param physicsProcess PhysicsProcess
     */
    public void addPhysicsProcess(PhysicsProcess physicsProcess) {
        this.physicsProcesses.add(physicsProcess);
    }

    /**
     * Removes an existing process from the list
     * 
     * @param physicsProcess PhysicsProcess
     */
    public void removePhysicsProcess(PhysicsProcess physicsProcess) {
        this.physicsProcesses.remove(physicsProcess);
    }

    /**
     * Sets a new state for debugger management.
     *
     * <p>
     * Do not use this method unless you have full control over the various
     * phases involved in creating the physical space.
     * </p>
     *
     * @param debugAppState PhysicsDebugAppState
     */
    public void setDebugAppState(PhysicsDebugAppState debugAppState) {
        this.debugAppState = debugAppState;
    }

    /**
     * Add a new drawing to the list
     * 
     * @param drawable Drawable
     */
    public void addDrawable(Drawable drawable) {
        this.drawables.add(drawable);
    }

    /**
     * Removes a drawable from the list
     * 
     * @param drawable Drawable
     */
    public void removeDrawable(Drawable drawable) {
        this.drawables.remove(drawable);
    }

    /**
     * Set the number of sub-steps
     *
     * @param subStepCount int
     */
    public void setSubStepCount(int subStepCount) {
        this.subStepCount = subStepCount;
    }

    /**
     * Add a new body to the physical space.
     *
     * @param body2D PhysicsBody2D
     */
    public void addBody(PhysicsBody2D body2D) {
        b2CreateBody(worldId, body2D.getBodyDef(), body2D.getBodyId());
        body2D.setPhysicsSpace(this);
    }

    /**
     * Remove a physical body from this space.
     *
     * @param body2D PhysicsBody2D
     */
    public void removeBody(PhysicsBody2D body2D) {
        try (body2D) {
            b2DestroyBody(body2D.getBodyId());
            body2D.setPhysicsSpace(null);
        }
    }

    /**
     * Add a new task to the queue, awaiting processing.
     *
     * @param <V> type
     * @param callable task
     *
     * @return the task
     */
    public <V> Future<V> enqueue(Callable<V> callable) {
        AppTask<V> task = new AppTask<>(callable);
        taskQueue.add(task);
        return task;
    }

     /**
     * Add a new task to the queue, awaiting processing.
     *
     * @param callable task
     *
     * @return the task
     */
    public Future<Void> enqueue(Runnable callable) {
        return enqueue((VoidCallable) callable::run);
    }

    /**
     * Runs tasks enqueued via {@link #enqueue(Callable)}
     */
    protected void fireQueuedTasks() {
        AppTask<?> task;
        while ((task = taskQueue.poll()) != null) {
            if (!task.isCancelled()) {
                task.invoke();
            }
        }
    }

    /**
     * Runs all processes.
     *
     * @param tpf float
     */
    protected final void firePhysicsProcess(float tpf) {
        for (final PhysicsProcess process : physicsProcesses) {
            process.invokeProcess(tpf);
        }
    }

    /**
     * Responsible method of updating the physical world.
     *
     * @param tpf float
     */
    public void update(float tpf) {
        if (tpf > 0) {
            // ejecutar las tareas
            fireQueuedTasks();
            firePhysicsProcess(tpf);

            // actualizar y dibujar las físicas
            b2World_Step(worldId, tpf, subStepCount);
            if (debugAppState != null) {
                b2World_Draw(worldId, debugDraw);
                if (debugAppState.isInitialized() && debugAppState.getDebugProcessor().isInitialized()) {
                    for (Drawable drawable : drawables) {
                        drawable.draw(debugAppState.getDebugProcessor().getDraw());
                    }
                }
            }
        }
    }

    /**
     * Set the axis that will be used in the physical space.
     *
     * @param axisType axis type
     */
    public void setAxisType(AxisType axisType) {
        this.axisType = axisType;
    }

    /**
     * Returns the axis used in physical space.
     *
     * @return axis type
     */
    public AxisType getAxisType() {
        return axisType;
    }

    /**
     * Returns the {@code b2WorldId}
     *
     * @return b2WorldId
     */
    public b2WorldId getWorldId() {
        return worldId;
    }

    /**
     * Returns the number of sub-steps
     *
     * @return int
     */
    public int getSubStepCount() {
        return subStepCount;
    }

    /**
     * Returns the {@code b2DebugDraw}
     *
     * @return b2DebugDraw
     */
    public b2DebugDraw getDebugDraw() {
        return debugDraw;
    }

    /**
     * Free native objects and callbacks
     */
    @Override
    public void close() {
        b2DestroyWorld(worldId);
        b2FreeCallbacks();
        debugDraw.close();
        worldId.close();
        debugAppState = null;
    }
}
