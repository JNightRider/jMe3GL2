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

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.box2d.jni.*;
import org.box2d.jni.system.*;

import static org.box2d.jni.include.Base.*;
import static org.box2d.jni.include.Types.*;
import static org.box2d.jni.libc.LibCStdlib.*;
import static org.box2d.jni.system.ArenaAlloc.*;
import org.je3gl.box2d.debug.Box2dDebugAppState;
import org.je3gl.box2d.scene.tile.Box2dTilePhysicsSystem;

/**
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class Box2dAppState extends AbstractAppState {
    /** Wait time in microseconds. */
    private static final long TIME_STEP_IN_MICROSECONDS = (long) ((1.0f / 60.0f) * 1000L); 
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(Box2dAppState.class.getName());
    
    /** JME3 Application (Game). */
    protected Application app = null;    
    /** States Manager. */
    protected AppStateManager stateManager = null;
    
    protected b2WorldDef worldDef;
    
    /**The physical space of bodies. */
    protected PhysicsSpace physicsSpace = null;
    /** <code>TPF</code> since last update call; in seconds. */
    protected float tpf = 0;    
    /**accumulated <code>TPF</code>. */
    protected float tpfSum = 0;

    //--------------------------------------------------------------------------
    //                       Multithreaded fields
    //--------------------------------------------------------------------------
    /** Type of thread on which the physics engine runs. */
    protected ThreadingType threadingType = null;
    
    /**
     * When the engine is running in parallel, an executor is used to safely update 
     * the physical engine to avoid problems with JME3 threads.
     */
    protected ScheduledThreadPoolExecutor executor;
    
    /**
     * When running the update thread; An executable is used to update the parallel
     * physics engine.
     */
    private final Runnable parallelPhysicsUpdate = () -> {
        if (!isEnabled()) {
            return;
        }
        
        // physics engine update
        Box2dAppState.this.physicsSpace.update(
            Box2dAppState.this.tpfSum
        );
        Box2dAppState.this.tpfSum = 0.0F;
    };
    
    //--------------------------------------------------------------------------
    //                              Debugger
    //-------------------------------------------------------------------------- 
    /**
     * State in charge of managing a debugger for all physical bodies that manage
     * the physical space (world).
     */
    protected Box2dDebugAppState box2dDebugAppState;
     
    /**
     * <code>true</code> to enable the purification state of physical bodies and
     * joints in physical space; otherwise it is <code>false</code> to disable.
     */
    protected boolean debug;
    
    //--------------------------------------------------------------------------
    //                              Axis
    //--------------------------------------------------------------------------
    /**
     * The axis type is the way in which the positions of physical objects are
     * applied with respect to the three coordinates of JME3's 3D space; changing
     * this axis implies a change in the way objects are controlled at the three
     * points (x, y, z).
     */
    protected AxisType axisType = AxisType.getDefault();
    

    public Box2dAppState() {
        this(b2DefaultWorldDef(b2WorldDef.malloc()), ThreadingType.SEQUENTIAL);
    }
    
    public Box2dAppState(ThreadingType threadingType) {
        this(b2DefaultWorldDef(b2WorldDef.malloc()), threadingType);
    }

    public Box2dAppState(b2WorldDef worldDef, ThreadingType threadingType) {
        this.threadingType = threadingType;
        this.worldDef = worldDef;
        startPhysics();
    }

    /*(non-Javadoc)
     */
    @Override
    public void initialize(final AppStateManager stateManager, final Application app) {
        this.app = app;
        this.stateManager = stateManager;

        // Start physics-related objects.
        startPhysics();

        super.initialize(stateManager, app);
    }
    
    protected final b2AllocFcnI allocFcn = (size, alignment) -> naligned_alloc(alignment, size);
    
    protected final b2FreeFcnI freeFcn = (mem, size) -> naligned_free(mem);
    
    protected final b2AssertFcnI assertFcn = (condition, fileName, lineNumber) -> {
        LOGGER.log(Level.SEVERE, "{0}, {1}, line {2}", new Object[]{
            condition, fileName, lineNumber
        });
        return 1;
    };
    
    protected final b2LogFcnI logFcn = (message) -> {
        LOGGER.log(Level.WARNING, message);
    };
    
    /**
     * Initialize physics for physical bodies.
     */
    private void startPhysics() {
        if (this.initialized) {
            return;
        }

        b2SetAllocator(allocFcn, freeFcn);
        b2SetAssertFcn(assertFcn);
        b2SetLogFcn(logFcn);
        
        if (this.threadingType == ThreadingType.PARALLEL) {
            startPhysicsOnExecutor();
        } else {
            this.physicsSpace = new PhysicsSpace(worldDef);
            this.box2dDebugAppState = new Box2dDebugAppState(physicsSpace);
            this.box2dDebugAppState.setEnabled(false);
        }

        Box2dTilePhysicsSystem.initialize();
        this.initialized = true;
    }

    /**
     * Initializes the physics engine to run in parallel with JME3 safely.
     */
    private void startPhysicsOnExecutor() {
        if (this.executor != null) {
            this.executor.shutdown();
        }
        this.executor = new ScheduledThreadPoolExecutor(1);

        @SuppressWarnings("unchecked")
        final Callable<Boolean> call = () -> {
            Box2dAppState.this.physicsSpace = new PhysicsSpace(Box2dAppState.this.worldDef);
            Box2dAppState.this.box2dDebugAppState = new Box2dDebugAppState(physicsSpace);
            return true;
        };

        try {
            this.executor.submit(call).get();
        } catch (final InterruptedException | ExecutionException ex) {
            Logger.getLogger(Box2dAppState.class.getName()).log(Level.SEVERE, null, ex);
        }

        schedulePhysicsCalculationTask();
    }
    
    /**
     * Method responsible for configuring the physical engine update task, this 
     * is only valid if the engine runs in parallel.
     */
    private void schedulePhysicsCalculationTask() {
        if (this.executor != null) {
            this.executor.scheduleAtFixedRate(this.parallelPhysicsUpdate, 0L, TIME_STEP_IN_MICROSECONDS,
                    TimeUnit.MICROSECONDS);
        }
    }
    
    /*(non-Javadoc)
     */
    @Override
    public void update(final float tpf) {
        if (!isEnabled()) {
            return;
        }
        if (box2dDebugAppState != null && !stateManager.hasState(box2dDebugAppState)) {
            stateManager.attach(box2dDebugAppState);
        }
        this.tpf = tpf;
        this.tpfSum += tpf;
    }
        
    /* (non-Javadoc)
     */
    @Override
    public void render(final RenderManager rm) {
        if (null == threadingType) {
            /* (non-Code). */
        } else switch (threadingType) {
            case PARALLEL:
                executor.submit(parallelPhysicsUpdate);
                break;
            case SEQUENTIAL:
                final float timeStep = isEnabled() ? this.tpf * this.physicsSpace.getMaximumLinearSpeed(): 0;
                this.physicsSpace.update(timeStep);
                break;
            default:
                break;
        }
    }

    /* (non-Javadoc)
     */
    @Override
    public void setEnabled(final boolean enabled) {
        if (enabled) {
            schedulePhysicsCalculationTask();

        } else if (this.executor != null) {
            this.executor.remove(this.parallelPhysicsUpdate);
        }
        super.setEnabled(enabled);
    }
    /**
     * Method responsible for cleaning the state of physics.
     * <p>
     * <b>WARNING</b>: Once this method is executed (remove it from the state
     * manager) the physical space will be invalidated so it will be unusable.
     */
    @Override
    public void cleanup() {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
        physicsSpace.close();
        physicsSpace = null;
        super.cleanup();
    }

    /**
     * Returns the physics space.
     *
     * @return PhysicsSpace
     */
    public PhysicsSpace getPhysicsSpace() {
        return this.physicsSpace;
    }

    /**
     * Method responsible for returning the state of the debugger.
     *
     * @return <code>true</code> if enabled; otherwise it will return
     * <code>false</code> if disabled
     */
    public boolean isDebugEnabled() {
        return debug;
    }

    /**
     * Method responsible for activating or deactivating the physical body
     * debugger.
     *
     * @param debug <code>true</code> to enable state; otherwise
     * <code>false</code> to disable it
     */
    public void setDebugEnabled(boolean debug) {
        this.debug = debug;
    }

    /**
     * Set the axis that will be used in the physical space.
     *
     * @param axisType axis type
     */
    public void setAxisType(AxisType axisType) {
        if (axisType == null) {
            throw new NullPointerException("The axis type cannot be null, choose a type");
        }

        if (physicsSpace != null) {
            physicsSpace.setAxisType(axisType);
        }
        this.axisType = axisType;
    }

    /**
     * Returns the axis used in physical space.
     *
     * @return axis type
     */
    public AxisType getAxisType() {
        AxisType localAxis = physicsSpace.getAxisType();
        if (localAxis != axisType) {
            LOGGER.log(Level.WARNING, "Forced change for axis type: before [{0}], after [{1}]", new Object[]{localAxis, axisType});
            physicsSpace.setAxisType(axisType);
        }
        return axisType;
    }
}
