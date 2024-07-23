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
package org.je3gl.util;

import com.jme3.app.state.AbstractAppState;
import java.util.ArrayList;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An <code>TimerAppState</code> is responsible for managing all {@link Timer}
 * running in the game.
 * <p>
 * In simpler terms, it is in charge of updating them, checking when they are
 * paused or resuming them.
 * 
 * @author wil
 * @version 1.5.0
 * @since 1.3.0
 */
public final class TimerAppState extends AbstractAppState {
    /** Class logger. */
    private static final Logger LOG = Logger.getLogger(TimerAppState.class.getName());

    /**
     * An object of class <code>State</code> is responsible for enumerating
     * the 3 possible states of a {@link org.je3gl.util.Timer}:
     * <div>
     * <ul>
     * <li><b>Start:</b> initializing.</li>
     * <li><b>Pause:</b> momentary pause.</li>
     * <li><b>Stop:</b> dead status.</li>
     * </ul>
     * </div>
     *
     * @author wil
     * @version 1.0.5
     * @since 2.0.0
     */
    public static enum State {
        /**
         * Initialization state of the timer {@link org.je3gl.util.Timer}, this calls the
         * method <code>start()</code>.
         */
        Start,
        /**
         * Temporary pause or stop state of the timer {@link org.je3gl.util.Timer}, this calls
         * the method <code>pause(boolean pause)</code>.
         */
        Pause,
        /**
         * It does the opposite of {@code Pause}, i.e. it resumes the processes
         * as long as it has not died.
         */
        Resume,
        /**
         * Completely stops the timer processes.
         */
        Stop;
    }
    
    /** Map of the timers. */
    private final Map<String, EntryTimer> timerMap = new IdentityHashMap<>();
    
    /**
     * Temporary list containing all paused timers that were previously executed 
     * when the <code>setEnabled()</code> method was called.
     */
    final List<Timer> tempTimer = new ArrayList<>();
    /** Default delay. */
    private float defaultDelay = 0.5F;
    
    /**
     * Returns the default delay.
     * @return delay
     */
    public float getDefaultDelay() {
        return defaultDelay;
    }

    /**
     * Sets a new delay that is used as default value for all {@link org.je3gl.util.Timer}
     * without a delay set.
     * 
     * @param defaultDelay new default delay
     */
    public void setDefaultDelay(float defaultDelay) {
        this.defaultDelay = defaultDelay;
    }
    
    /**
     * Method in charge of setting a timer to a specific {@link org.je3gl.util.Timer}.
     * @param name timer codename
     * @param state status that it will take
     * @return <code>true</code> if the changes have been applied, otherwise <code>false</code>
     */
    public boolean setState(String name, State state) {
        return setState(name, state, true);
    }
    
    /**
     * Method in charge of setting a timer to a specific {@link org.je3gl.util.Timer}.
     * @param name timer codename
     * @param state status that it will take
     * @param force <code>true</code> if you need to force the action; otherwise 
     *              <code>false</code> if you want to apply the action only once (controlled).
     * @return <code>true</code> if the changes have been applied, otherwise <code>false</code>
     */
    public boolean setState(String name, State state, boolean force) {
        EntryTimer entryTimer = this.timerMap.get(name);
        if ( entryTimer == null ) {
            LOG.log(Level.WARNING, "[ {0} ] Nonexistent timer.", name);
            return false;
        }
        
        if (! isEnabled()) {
            LOG.log(Level.WARNING, "The state is stopped, enable it first to start any timer.");
            return false;
        }
        
        Timer timer = entryTimer.getTimer();
        switch (state) {
            case Pause:
                if (force) {
                    timer.pause(true);
                } else {
                    if (!timer.isPaused()) {
                        timer.pause(true);
                    }
                }
                break;
            case Resume:
                if (force) {
                    timer.pause(false);
                } else {
                    if (timer.isPaused()) {
                        timer.pause(false);
                    }
                }
                break;
            case Start:
                if (force) {
                    timer.start();
                } else {
                    if (!timer.isRun()) {
                        timer.start();
                    }
                }
                break;
            case Stop:
                if (force) {
                    timer.stop();
                } else {
                    if (timer.isRun()) {
                        timer.stop();
                    }
                }
                break;
            default:
                throw new AssertionError();
        }
        return true;
    }
    
    /**
     * Adds a new timer. Where it will use the default delay.
     * @param name timer codename
     * @param timer timer
     * @return the same {@link org.je3gl.util.Timer}
     */
    public Timer attachTimer(String name, Timer timer) {
        return this.attachTimer(name, timer, defaultDelay);
    }
    
    /**
     * Adds a new timer
     * @param name timer codename
     * @param timer timer
     * @param delay delay of this timer
     * @return the same {@link org.je3gl.util.Timer}
     */
    public Timer attachTimer(String name, Timer timer, float delay) {
        if (this.timerMap.containsKey(name)) {
            LOG.log(Level.WARNING, "[ {0} ] Timer exist.", name);
            return null;
        }
        EntryTimer entryTimer = new EntryTimer(delay, timer);
        entryTimer.validate();
        this.timerMap.put(name, entryTimer);
        return timer;
    }

    /**
     * Deletes a timer from the manager.
     * @param name timer codename
     * @return the deleted timer if it exists, otherwise it will return a value
     * of <code>null</code>
     */
    public Timer detachTimer(String name) {
        EntryTimer entryTimer = this.timerMap.remove(name);
        if ( entryTimer != null ) {
            Timer tm = entryTimer.getTimer();
            tm.setAppState(null);
            return tm;
        }
        return null;
    }
    
    /**
     * Search for a timer by its codename.
     * @param name timer codename
     * @return the {@link org.je3gl.util.Timer}
     */
    public Timer getTimer(String name) {
        EntryTimer entryTimer = this.timerMap.get(name);
        if ( entryTimer == null ) {
            return null;
        }
        return entryTimer.getTimer();
    }
    
    /**
     * Adds a new task to a specific timer.
     * @see org.je3gl.util.Timer#addTask(org.je3gl.util.TimerTask) 
     * @param name timer codename
     * @param task task timer
     * @return <code>true</code> if the task has been successfully added, otherwise <code>false</code>
     */
    public boolean addTask(String name, TimerTask task) {
        EntryTimer entryTimer = this.timerMap.get(name);
        if ( entryTimer == null ) {
            LOG.log(Level.WARNING, "[ {0} ] Nonexistent timer.", name);
            return false;
        }
        return entryTimer.getTimer().addTask(task);
    }
    
    /**
     * Deletes a task at a specific timer.
     * @see org.je3gl.util.Timer#removeTask(org.je3gl.util.TimerTask)  
     * @param name timer codename
     * @param task task timer
     * @return <code>true</code> if the task has been deleted correctly, otherwise <code>false</code>
     */
    public boolean removeTask(String name, TimerTask task) {
        EntryTimer entryTimer = this.timerMap.get(name);
        if ( entryTimer == null ) {
            LOG.log(Level.WARNING, "[ {0} ] Nonexistent timer.", name);
            return false;
        }
        return entryTimer.getTimer().removeTask(task);
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#update(float) 
     * @param tpf float
     */
    @Override
    public void update(float tpf) {
        for (final Map.Entry<String, EntryTimer> entry : this.timerMap.entrySet()) {
            EntryTimer timer = entry.getValue();
            if (timer == null) {
                continue;
            }
            
            timer.getTimer().update(tpf, timer.getDelay());
        }
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#setEnabled(boolean) 
     * @param enabled boolean
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            for (int i = 0; i < tempTimer.size(); i++) {
                Timer etm = tempTimer.get(i);
                etm.setAppState(null);
                etm.pause(false);
                                
                tempTimer.remove(i);
                i--;
            }
        } else {
            for (final Map.Entry<String, EntryTimer> entry : this.timerMap.entrySet()) {
                EntryTimer timer = entry.getValue();
                if (timer == null) {
                    continue;
                }

                Timer tm = timer.getTimer();
                if (tm.isRun()) {
                    tm.pause(true);
                    tm.setAppState(this);
                    tempTimer.add(tm);
                }
            }
        }
        super.setEnabled(enabled);
    }
    
    /**
     * Method in charge of deleting all the timers registered in this time
     * manager/administrator.
     */
    public void detachAllTimer() {
        for (final Map.Entry<?, EntryTimer> entry : timerMap.entrySet()) {
            Timer timer = entry.getValue().getTimer();
            timer.setAppState(null);
        }
        timerMap.clear();
    }
    
    /**
     * Internal class in charge of storing the timer and its delay in memory.
     */
    class EntryTimer {
        
        /** Timer delay. */
        private final float delay;        
        /** Timer. */
        private final Timer timer;

        /**
         * Default constructor of the class <code>EntryTimer</code>.
         * @param delay timer delay
         * @param timer timer
         */
        public EntryTimer(float delay, Timer timer) {
            this.delay = delay;
            this.timer = timer;
        }
        
        /**
         * It is responsible for validating the data.
         */
        public void validate() {
            if (timer == null) {
                throw new NullPointerException("The timer is null.");
            }
            if (delay < 0) {
                throw new IllegalArgumentException("[" + delay + "] Invalid delay.");
            }
        }

        /**
         * Returns the delay.
         * @return float
         */
        public float getDelay() {
            return delay;
        }

        /**
         * Returns the timer.
         * @return object
         */
        public Timer getTimer() {
            return timer;
        }
    }
}
