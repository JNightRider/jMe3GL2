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
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.je3gl.box2d.scene.tile.Box2dTilePhysicsSystem;

import org.box2d.jni.*;

import static org.box2d.jni.include.Base.*;
import static org.box2d.jni.include.Types.*;
import static org.box2d.jni.libc.LibCStdlib.*;
import org.box2d.jni.system.Sys;
import org.je3gl.box2d.debug.Box2dDebug;

/**
 * An object (an instance) of the class <code>Box2dAppState</code> is a state
 * that is responsible for managing the physics engine provided by box2d, it
 * integrates this engine with JME3 to give realism to 2D games with it.
 * <p>
 * Note that the dyn4j engine is independent of jme3; therefore, you must have
 * knowledge of how to handle both.
 * </p>
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class Box2dAppState extends AbstractAppState {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(Box2dAppState.class.getName());
    static {
        Sys.PIPELINE_STREAM.set(Box2dPipelineStream.class.getName());
    }
    
    /** JME3 Application (Game). */
    protected Application app = null;    
    /** States Manager. */
    protected AppStateManager stateManager = null;
    /** The initial values ​​of the 2D world. */
    protected b2WorldDef worldDef;

    /**
     * The physical space of bodies.
     */
    protected PhysicsSpace physicsSpace = null;
    /**
     * time interval between frames (in seconds) from the most recent update
     */
    protected float tpf = 0;
    /**
     * simulation speed multiplier (0&rarr;paused)
     */
    private float speed = 1f;

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
    final private Callable<Boolean> parallelPhysicsUpdate = new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            physicsSpace.update(isEnabled() ? tpf * speed : 0f);
            return true;
        }
    };
    
    //--------------------------------------------------------------------------
    //                              Debugger
    //-------------------------------------------------------------------------- 
    /**
     * Debugger settings.
     */
    private DrawSettings drawSettings;
    
    /**
     * SceneProcessor to manage the debug visualization, or null if none
     */
    private Box2dDebug box2dDebug;

    /**
     * <code>true</code> to enable the purification state of physical bodies and
     * joints in physical space; otherwise it is <code>false</code> to disable.
     */
    protected boolean debug = false;

    //--------------------------------------------------------------------------
    //                              Axis
    //--------------------------------------------------------------------------

    /**
     * The axis type is the way in which the positions of physical objects are
     * applied with respect to the three coordinates of JME3's 3D space;
     * changing this axis implies a change in the way objects are controlled at
     * the three points (x, y, z).
     */
    protected AxisType axisType = AxisType.getDefault();

    /**
     * Generate a new instance of the <code>Box2dAppState</code> class to create
     * a physics engine that can handle all physical bodies in a 2D world or
     * scene realistically via <b>box2d</b>.
     */
    public Box2dAppState() {
        this(b2DefaultWorldDef(b2WorldDef.malloc()), new DrawSettings(), ThreadingType.SEQUENTIAL);
    }

    /**
     * Generate a new instance of the <code>Box2dAppState</code> class to create
     * a physics engine that can handle all physical bodies in a 2D world or
     * scene realistically via <b>box2d</b>.
     *
     * @param threadingType physics engine integration type (thread)
     */
    public Box2dAppState(ThreadingType threadingType) {
        this(b2DefaultWorldDef(b2WorldDef.malloc()), new DrawSettings(), threadingType);
    }

    /**
     * Generate a new instance of the <code>Box2dAppState</code> class to create
     * a physics engine that can handle all physical bodies in a 2D world or
     * scene realistically via <b>box2d</b>.
     *
     * @param worldDef the initial values ​​of the 2D world.
     * @param drawSettings debugger/draw settings.
     * @param threadingType physics engine integration type (thread)
     */
    public Box2dAppState(b2WorldDef worldDef, DrawSettings drawSettings, ThreadingType threadingType) {
        this.threadingType = threadingType;
        this.drawSettings = drawSettings;
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

    /**
     * A callback function for: b2AllocFcn
     */
    protected final b2AllocFcnI allocFcn = (size, alignment) -> naligned_alloc(alignment, size);

    /**
     * A callback function for: b2FreeFcn
     */
    protected final b2FreeFcnI freeFcn = (mem, size) -> naligned_free(mem);

    /**
     * A callback function for: b2AssertFcn
     */
    protected final b2AssertFcnI assertFcn = (condition, fileName, lineNumber) -> {
        LOGGER.log(Level.SEVERE, "{0}, {1}, line {2}", new Object[]{
            condition, fileName, lineNumber
        });
        return 1;
    };

    /**
     * A callback function for: b2LogFcn
     */
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
        }

        Box2dTilePhysicsSystem.initialize();
        this.box2dDebug = new Box2dDebug(this);
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

        Callable<Boolean> call = () -> {
            physicsSpace = new PhysicsSpace(worldDef);
            return true;
        };

        try {
            this.executor.submit(call).get();
        } catch (final InterruptedException | ExecutionException ex) {
            Logger.getLogger(Box2dAppState.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /*(non-Javadoc)
     */
    @Override
    public void update(float tpf) {
        if (!isEnabled()) {
            return;
        }
        this.tpf = tpf;

        if (initialized) {
            ViewPort viewPort = this.app.getViewPort();
            if (debug && !viewPort.getProcessors().contains(box2dDebug)) {
                // Start debug visualization.
                this.app.getViewPort().addProcessor(box2dDebug);
                this.physicsSpace.setDebugDraw(box2dDebug.getDebugDraw());
            } else if (!debug && viewPort.getProcessors().contains(box2dDebug)) {
                // Stop debug visualization.
                this.app.getViewPort().removeProcessor(box2dDebug);
                this.physicsSpace.setDebugDraw(null);
            }
        }
    }
        
    /* (non-Javadoc)
     */
    @Override
    public void render(final RenderManager rm) {
        switch (threadingType) {
            case PARALLEL -> executor.submit(parallelPhysicsUpdate);
            case SEQUENTIAL -> {
                final float timeStep = isEnabled() ? this.tpf * speed: 0;
                this.physicsSpace.update(timeStep);
            }
            default -> { }
        }
    }

    /**
     * Alter the physics simulation speed.
     *
     * @param speed the desired speedup factor (&ge;0, default=1, 0&rarr;paused)
     */
    public void setSpeed(float speed) {
        if (speed < 0) {
            throw new IllegalStateException("Speed ​​cannot be negative.");
        }
        this.speed = speed;
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
     * Return the physics-simulation speed.
     *
     * @return the speedup factor (&ge;0, default=1, 0&rarr;paused)
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Returns the debugger settings (drawings).
     *
     * @return DrawSettings
     */
    public DrawSettings getDrawSettings() {
        return drawSettings;
    }

    /**
     * Returns the physics space.
     *
     * @return PhysicsSpace
     */
    public PhysicsSpace getPhysicsSpace() {
        return this.physicsSpace;
    }

    public Application getApplication() {
        return app;
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
