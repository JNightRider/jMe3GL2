/* Copyright (c) 2009-2023 jMonkeyEngine.
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

import jme3gl2.physics.control.PhysicsBody2D;
import jme3gl2.physics.debug.Dyn4JDebugAppState;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.Settings;

/**
 * Un <code>Dyn4jAppState</code> es el estado encargado de gestionar el motor<br>
 * de física <code>Dyn4j</code>.
 * <p>
 * Tenga en cuenta que el motor {@code dyn4j} es independiente de {@code jme3},
 * por lo cual debe tener conociminetos para manejos de ambos. </p>
 * 
 * @author wil
 * @version 1.0.5-SNAPSHOT
 * 
 * @since 1.0.0
 * @param <E> el tipo {@code PhysicsBody}.
 */
@SuppressWarnings(value = {"unchecked"})
public class Dyn4jAppState<E extends PhysicsBody2D> extends AbstractAppState {

    /** Tiempo de espera en microsefundos. */
    private static final long TIME_STEP_IN_MICROSECONDS = (long) (Settings.DEFAULT_STEP_FREQUENCY * 1000L);
    
    /**
     * Consulte {@link Application} para obtener más información.
     */
    protected Application app = null;
    
    /** Administrador de estados. */
    protected AppStateManager stateManager = null;
    
    /** Capacidad inicial de cuerpo del mundo. */
    protected Integer initialCapacity = null;
    
    /** Capacidad inicial para las articulaciones. */
    protected Integer initialJointCapacity = null;
    
    /** Límite del mundo.
     * <p>
     * Tenga encuenta que si se establece un límite, el motor de física al
     * alcanzar dicho límite dejara dehabilitado el mundo.</p>
     */
    protected Bounds bounds = null;
    
    /**
     * Consulte {@link PhysicsSpace} para obtener más información.
     */
    protected PhysicsSpace<E> physicsSpace = null;
    
    /** fps para el motor. */
    protected float tpf = 0;
    
    /** fps acumulado para el motor. */
    protected float tpfSum = 0;
    
    // Campos MultiTreading //    
    /** Consulte {@link ThreadingType} para obtener más información. */
    protected ThreadingType threadingType = null;
    
    /** (non-javadoc) */
    protected ScheduledThreadPoolExecutor executor;
    /** (non-javadoc) */
     private final Runnable parallelPhysicsUpdate = () -> {
         if (!isEnabled()) {
             return;
         }
         
         Dyn4jAppState.this.physicsSpace.updateFixed(Dyn4jAppState.this.tpfSum);
         Dyn4jAppState.this.tpfSum = 0;
    };
    
     // Depurador.
     /** Estado-depuración.*/
     protected Dyn4JDebugAppState<E> debugAppState;
     
     /**
      * <code>true</code> si se activa el estado depurador para los cuerpos
      * físico y articulaciones del espacio-físico, de lo contrario
      * <code>false</code> para desactivarlo.
      */
     protected boolean debug;
     
    /**
     * Instancia un nuevo objeto <code>Dyn4jAppState</code> con los valores
     * predeterminados.
     */
    public Dyn4jAppState() {
        this(null, null, ThreadingType.PARALLEL);
    }

    /**
     * Instancia un nuevo objeto <code>Dyn4jAppState</code> e inicializelo con
     * los valores predeterminados.
     * 
     * @param bounds Límite del mundo.
     */
    public Dyn4jAppState(final Bounds bounds) {
        this(null, null, bounds, ThreadingType.PARALLEL);
    }

    /**
     * Instancia un nuevo objeto <code>Dyn4jAppState</code> e inicializelo con
     * los valores predeterminados.
     * 
     * @param initialCapacity capacidad inicial (cuerpos en el mundo).
     * @param initialJointCapacity capacidad inicial de las articulaciones.
     */
    public Dyn4jAppState(final Integer initialCapacity, final Integer initialJointCapacity) {
        this(initialCapacity, initialJointCapacity,null, ThreadingType.PARALLEL);
    }

    /**
     * Instancia un nuevo objeto <code>Dyn4jAppState</code> e inicializelo con
     * los valores predeterminados.
     * 
     * @param initialCapacity capacidad inicial (cuerpos en el mundo).
     * @param initialJointCapacity capacidad inicial de las articulaciones.
     * @param bounds Límite del mundo.
     */
    public Dyn4jAppState(final Integer initialCapacity, final Integer initialJointCapacity, final Bounds bounds) {
        this(initialCapacity, initialJointCapacity, bounds, ThreadingType.PARALLEL);
    }

    /**
     * Instancia un nuevo objeto <code>Dyn4jAppState</code> e inicializelo con
     * los valores predeterminados.
     * 
     * @param threadingType Tipo del integración del motor.
     */
    public Dyn4jAppState(final ThreadingType threadingType) {
        this(null, null, threadingType);
    }

    /**
     * Instancia un nuevo objeto <code>Dyn4jAppState</code> e inicializelo con
     * los valores predeterminados.
     * 
     * @param bounds Límite del mundo.
     * @param threadingType Tipo del integración del motor.
     */
    public Dyn4jAppState(final Bounds bounds, final ThreadingType threadingType) {
        this(null, null, bounds, threadingType);
    }

    /**
     * Instancia un nuevo objeto <code>Dyn4jAppState</code> e inicializelo con
     * los valores predeterminados.
     * 
     * @param initialCapacity capacidad inicial (cuerpos en el mundo).
     * @param initialJointCapacity capacidad inicial de las articulaciones.
     * @param threadingType Tipo del integración del motor.
     */
    public Dyn4jAppState(final Integer initialCapacity, final Integer initialJointCapacity, final ThreadingType threadingType) {
        this(initialCapacity, initialJointCapacity, null, threadingType);
    }

    /**
     * Instancia un nuevo objeto <code>Dyn4jAppState</code> e inicializelo con
     * los valores predeterminados.
     * 
     * @param initialCapacity capacidad inicial (cuerpos en el mundo).
     * @param initialJointCapacity capacidad inicial de las articulaciones.
     * @param bounds Límite del mundo.
     * @param threadingType Tipo del integración del motor.
     */
    public Dyn4jAppState(final Integer initialCapacity, final Integer initialJointCapacity, final Bounds bounds, final ThreadingType threadingType) {
        this.threadingType = threadingType;
        this.initialCapacity = initialCapacity;
        this.bounds = bounds;
    }

    /**
     * (non-JavaDoc).
     * 
     * @param stateManager AppStateManager
     * @param app Application
     * @see AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application) 
     */
    @Override
    public void initialize(final AppStateManager stateManager, final Application app) {
        this.app = app;
        this.stateManager = stateManager;

        // Iniciar objetos relacionados con la física.
        startPhysics();

        super.initialize(stateManager, app);
    }

    /**
     * Inicializa las física.
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
     * Inicializa la física de manera que se ejecute en paralelo con el motor
     * {@code jme3}, es decir con nuestros 'modelos 2D'.
     */
    private void startPhysicsOnExecutor() {
        if (this.executor != null) {
            this.executor.shutdown();
        }
        this.executor = new ScheduledThreadPoolExecutor(1);

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
     * (non-JavaDoc).
     */
    private void schedulePhysicsCalculationTask() {
        if (this.executor != null) {
            this.executor.scheduleAtFixedRate(this.parallelPhysicsUpdate, 0l, TIME_STEP_IN_MICROSECONDS,
                    TimeUnit.MICROSECONDS);
        }
    }

    /**
     * (non-JavaDoc).
     * @param stateManager AppStateManager
     * @see AbstractAppState#stateAttached(com.jme3.app.state.AppStateManager) 
     */
    @Override
    public void stateAttached(final AppStateManager stateManager) {
        // iniciar objetos relacionados con la física 
        // si appState no está inicializado.
        if (!this.initialized) {
            startPhysics();
        }
        
        // Comprobar si el modo depurador está habilitado 
        // e iniciar el estado depurador.
        if (this.debug) {
            this.stateManager = stateManager;
            prepareDebugger(true);
        }

        super.stateAttached(stateManager);
    }
    
    /**
     * (non-JavaDoc).
     * @param tpf float
     * @see AbstractAppState#update(float) 
     */
    @Override
    public void update(final float tpf) {
        if (!isEnabled()) {
            return;
        }
        
        if (this.debug && this.debugAppState == null && this.physicsSpace != null) {
            prepareDebugger(true);
        } else if (!this.debug && this.debugAppState != null) {
            destroyDebugger();
        }
        
        this.tpf = tpf;
        this.tpfSum += tpf;
    }
    
    /**
     * (non-JavaDoc).
     * @param rm RenderManager
     * @see AbstractAppState#render(com.jme3.renderer.RenderManager) 
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
     * (non-JavaDoc).
     * @param enabled boolean
     * @see AbstractAppState#setEnabled(boolean) 
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
     * Método encargado de limpiar el estado de la física.
     */
    @Override
    public void cleanup() {
        destroyDebugger();
        if (this.executor != null) {
            this.executor.shutdown();
            this.executor = null;
        }

        this.physicsSpace.destroy();

        super.cleanup();
    }

    /**
     * Devuelve esl espacio de la física.
     * @return motor de la física.
     */
    public PhysicsSpace<E> getPhysicsSpace() {
        return this.physicsSpace;
    }

    /**
     * Método encargado de devolver el estado del depurador.
     * @return <code>true</code> si esta habilitado, de lo contrario devolverá
     * <code>false</code> si se encuentra deshabilitado.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Método encargado de activar o desactivar el depurador de los cuerpos 
     * físicos.
     * 
     * @param debug <code>true</code> para habilitar el estado, de lo contrario
     * <code>false</code> para deshabilitar.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    /**
     * Método encargado de preparar el depurador para los cuerpos físicos.
     * @param attach <code>true</code> si se desea agregar al administrador de
     * estados, de lo contrario <code>false</code>.
     */
    protected void prepareDebugger(boolean attach) {
        if (this.debugAppState == null) {
            this.debugAppState = new Dyn4JDebugAppState<>(this.physicsSpace);
            
            if (attach) {
                this.stateManager.attach(this.debugAppState);
            }
        }
    }
    
    /**
     * Método encargado de estruir el estado depurador.
     */
    protected void destroyDebugger() {
        if (this.debugAppState != null) {
            this.stateManager.detach(this.debugAppState);
            this.debugAppState = null;
        }
    }
}
