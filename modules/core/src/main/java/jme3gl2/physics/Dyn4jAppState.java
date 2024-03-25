/* Copyright (c) 2009-2024 jMonkeyEngine.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jme3gl2.physics;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import jme3gl2.physics.control.PhysicsBody2D;
import jme3gl2.physics.debug.Dyn4jDebugAppState;

import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.Settings;

/**
 * An object (an instance) of the class <code>Dyn4jAppState</code> is a state that 
 * is responsible for managing the physics engine provided by dyn4j, it integrates 
 * this engine with JME3 to give realism to 2D games with it.
 * <p>
 * Note that the dyn4j engine is independent of jme3; therefore, you must have
 * knowledge of how to handle both.
 * @param <E> of type {@link jme3gl2.physics.control.PhysicsBody2D}
 * @author wil
 * @version 1.5.0
 * @since 1.0.0
 */
public class Dyn4jAppState<E extends PhysicsBody2D> extends AbstractAppState {

    /** Wait time in microseconds. */
    private static final long TIME_STEP_IN_MICROSECONDS = (long) (Settings.DEFAULT_STEP_FREQUENCY * 1000L);    
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(Dyn4jAppState.class.getName());
    
    /** JME3 Application (Game). */
    protected Application app = null;    
    /** States Manager. */
    protected AppStateManager stateManager = null;
    
    /**
     * Initial capacity that the physical space will have (it is just an initial 
     * space reserved in memory), null if none is specified, to which the one 
     * established by <b>dyn4j</b> will be assigned.
     */
    protected Integer initialCapacity = null;
    
    /** 
     * Initial capacity of the joints that the physical space will have (it is 
     * only an initial space reserved in memory), null if none is specified, to
     * which the one established by <b>dyn4j</b> will be assigned.
     */
    protected Integer initialJointCapacity = null;
    
    /** 
     * Limit of the world.
     * <p>
     * Please note that if a limit is set, the physics engine will disable the world 
     * upon reaching the limit
     */
    protected Bounds bounds = null;
    
    /**The physical space of bodies. */
    protected PhysicsSpace<E> physicsSpace = null;    
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
        Dyn4jAppState.this.physicsSpace.updateFixed(Dyn4jAppState.this.tpfSum);
        Dyn4jAppState.this.tpfSum = 0.0F;
    };
    
    //--------------------------------------------------------------------------
    //                              Debugger
    //-------------------------------------------------------------------------- 
    /**
     * State in charge of managing a debugger for all physical bodies that manage
     * the physical space (world).
     */
    protected Dyn4jDebugAppState<E> dyn4jDebugAppState;
     
    /**
     * <code>true</code> to enable the purification state of physical bodies and
     * joints in physical space; otherwise it is <code>false</code> to disable.
     */
    protected boolean debug;
     
    /**
     * Generate a new instance of the <code>Dyn4jAppState</code> class to create 
     * a physics engine that can handle all physical bodies in a 2D world or scene 
     * realistically via <b>dyn4j</b>.
     */
    public Dyn4jAppState() {
        this(null, null, ThreadingType.PARALLEL);
    }
    
    /**
     * Generate a new instance of the <code>Dyn4jAppState</code> class to create 
     * a physics engine that can handle all physical bodies in a 2D world or scene 
     * realistically via <b>dyn4j</b>.
     * 
     * @param bounds Llmit of the world
     */
    public Dyn4jAppState(final Bounds bounds) {
        this(null, null, bounds, ThreadingType.PARALLEL);
    }
    
    /**
     * Generate a new instance of the <code>Dyn4jAppState</code> class to create 
     * a physics engine that can handle all physical bodies in a 2D world or scene 
     * realistically via <b>dyn4j</b>.
     * 
     * @param initialCapacity initial capacity (bodies in the world)
     * @param initialJointCapacity initial joint capacity
     */
    public Dyn4jAppState(final Integer initialCapacity, final Integer initialJointCapacity) {
        this(initialCapacity, initialJointCapacity,null, ThreadingType.PARALLEL);
    }
    
    /**
     * Generate a new instance of the <code>Dyn4jAppState</code> class to create 
     * a physics engine that can handle all physical bodies in a 2D world or scene 
     * realistically via <b>dyn4j</b>.
     * 
     * @param initialCapacity initial capacity (bodies in the world)
     * @param initialJointCapacity initial joint capacity
     * @param bounds Llmit of the world
     */
    public Dyn4jAppState(final Integer initialCapacity, final Integer initialJointCapacity, final Bounds bounds) {
        this(initialCapacity, initialJointCapacity, bounds, ThreadingType.PARALLEL);
    }
    
    /**
     * Generate a new instance of the <code>Dyn4jAppState</code> class to create 
     * a physics engine that can handle all physical bodies in a 2D world or scene 
     * realistically via <b>dyn4j</b>.
     * 
     * @param threadingType physics engine integration type (thread)
     */
    public Dyn4jAppState(final ThreadingType threadingType) {
        this(null, null, threadingType);
    }
    
    /**
     * Generate a new instance of the <code>Dyn4jAppState</code> class to create 
     * a physics engine that can handle all physical bodies in a 2D world or scene 
     * realistically via <b>dyn4j</b>.
     * 
     * @param bounds Llmit of the world
     * @param threadingType physics engine integration type (thread)
     */
    public Dyn4jAppState(final Bounds bounds, final ThreadingType threadingType) {
        this(null, null, bounds, threadingType);
    }
    
    /**
     * Generate a new instance of the <code>Dyn4jAppState</code> class to create 
     * a physics engine that can handle all physical bodies in a 2D world or scene 
     * realistically via <b>dyn4j</b>.
     * 
     * @param initialCapacity initial capacity (bodies in the world)
     * @param initialJointCapacity initial joint capacity
     * @param threadingType physics engine integration type (thread)
     */
    public Dyn4jAppState(final Integer initialCapacity, final Integer initialJointCapacity, final ThreadingType threadingType) {
        this(initialCapacity, initialJointCapacity, null, threadingType);
    }

    /**
     * Generate a new instance of the <code>Dyn4jAppState</code> class to create 
     * a physics engine that can handle all physical bodies in a 2D world or scene 
     * realistically via <b>dyn4j</b>.
     * 
     * @param initialCapacity initial capacity (bodies in the world)
     * @param initialJointCapacity initial joint capacity
     * @param bounds Llmit of the world
     * @param threadingType physics engine integration type (thread)
     */
    public Dyn4jAppState(final Integer initialCapacity, final Integer initialJointCapacity, final Bounds bounds, final ThreadingType threadingType) {
        this.threadingType = threadingType;
        this.initialCapacity = initialCapacity;
        this.bounds = bounds;
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application) 
     * @param stateManager object
     * @param app object
     */
    @Override
    public void initialize(final AppStateManager stateManager, final Application app) {
        this.app = app;
        this.stateManager = stateManager;

        // Start physics-related objects.
        startPhysics();

        super.initialize(stateManager, app);
        printInformation();
    }

    /**
     * Initialize physics for physical bodies.
     */
    private void startPhysics() {
        if (this.initialized) {
            return;
        }

        if (this.threadingType == ThreadingType.PARALLEL) {
            startPhysicsOnExecutor();
        } else {
            this.physicsSpace = new PhysicsSpace<>(initialCapacity, initialJointCapacity, bounds);
        }

        this.initialized = true;
    }
    
    /**
     * Prints information about the physics engine that was configured on the screen.
     */
    protected void printInformation() {
        StringBuilder buff = new StringBuilder();
        buff.append("[jMe3GL2] :Physical engine initialized with the following properties")
             .append('\n');
        buff.append(" *  Threading Type: ").append(threadingType)
            .append('\n');
        buff.append(" *  Initial Capacity: ").append(initialCapacity)
            .append('\n');
        buff.append(" *  Initial Joint Capacity: ").append(initialJointCapacity)
            .append('\n');
        buff.append(" *  Bounds: ").append(bounds)
            .append('\n');
        buff.append(" *  Debugger Enabled: ").append(debug)
            .append('\n');
        LOGGER.log(Level.INFO, String.valueOf(buff));
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
            Dyn4jAppState.this.physicsSpace = new PhysicsSpace(Dyn4jAppState.this.initialCapacity, Dyn4jAppState.this.initialJointCapacity,
                    Dyn4jAppState.this.bounds);
            return true;
        };

        try {
            this.executor.submit(call).get();
        } catch (final InterruptedException | ExecutionException ex) {
            Logger.getLogger(Dyn4jAppState.class.getName()).log(Level.SEVERE, null, ex);
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

    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#stateAttached(com.jme3.app.state.AppStateManager) 
     * @param stateManager object
     */
    @Override
    public void stateAttached(final AppStateManager stateManager) {
        // initializes physics-related objects if AppState is not initialized.
        if (!this.initialized) {
            startPhysics();
        }
        
        // Check if debugger mode is enabled and start the debugger state.
        if (this.debug) {
            this.stateManager = stateManager;
            prepareDebugger(true);
        }

        super.stateAttached(stateManager);
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#update(float) 
     * @param tpf float
     */
    @Override
    public void update(final float tpf) {
        if (!isEnabled()) {
            return;
        }
        
        if (this.debug && this.dyn4jDebugAppState == null && this.physicsSpace != null) {
            prepareDebugger(true);
        } else if (!this.debug && this.dyn4jDebugAppState != null) {
            destroyDebugger();
        }
        
        this.tpf = tpf;
        this.tpfSum += tpf;
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#render(com.jme3.renderer.RenderManager) 
     * @param rm object
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
                final float timeStep = isEnabled() ? this.tpf * this.physicsSpace.getSpeed() : 0;
                this.physicsSpace.updateFixed(timeStep);
                break;
            default:
                break;
        }
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#setEnabled(boolean) 
     * @param enabled boolean
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
     * <b>WARNING</b>: Once this method is executed (remove it from the state manager)
     * the physical space will be invalidated so it will be unusable.
     */
    @Override
    public void cleanup() {
        destroyDebugger();
        if (this.executor != null) {
            this.executor.shutdown();
            this.executor = null;
        }

        this.physicsSpace = null;

        super.cleanup();
    }

    /**
     * Returns the physics space.
     * @return A {@link jme3gl2.physics.PhysicsSpace} object
     */
    public PhysicsSpace<E> getPhysicsSpace() {
        return this.physicsSpace;
    }

    /**
     * Method responsible for returning the state of the debugger.
     * @return <code>true</code> if enabled; otherwise it will return 
     * <code>false</code> if disabled
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Method responsible for activating or deactivating the physical body debugger.
     * @param debug <code>true</code> to enable state; otherwise <code>false</code>
     * to disable it
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    /**
     * Responsible method of preparing the physical debugger.
     * @param attach <code>true</code> if you want to add it to the state manager;
     * otherwise <code>false</code>
     */
    protected void prepareDebugger(boolean attach) {
        if (this.dyn4jDebugAppState == null) {
            this.dyn4jDebugAppState = new Dyn4jDebugAppState<>(this.physicsSpace);
            
            if (attach) {
                this.stateManager.attach(this.dyn4jDebugAppState);
            }
        }
    }
    
    /**
     * Method responsible for destroying the debugger state.
     */
    protected void destroyDebugger() {
        if (this.dyn4jDebugAppState != null) {
            this.stateManager.detach(this.dyn4jDebugAppState);
            this.dyn4jDebugAppState = null;
        }
    }
}
