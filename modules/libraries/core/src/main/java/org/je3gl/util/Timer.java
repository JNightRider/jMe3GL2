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

import java.util.ArrayList;
import java.util.List;

/**
 * An object of class <code>Timer</code> can be used as a sequential timer. That
 * is, it is updated along with the game scenes.
 * <p>
 * To activate or perform one or more tasks, use the interface {@link org.je3gl.util.TimerTask}.
 * 
 * @author wil
 * @version 1.0.5
 * @since 1.2.0
 */
public class Timer {
    
    /** Maximum timer time. */
    private float maxTime = 10.0F;    
    /** Counter. */
    private float counter = 0.0F;
    
    /**
     * {@code true} if the timer is stopped, i.e., it will stop counting, 
     * otherwise {@code false}.
     */
    private boolean paused = true;    
    /** List of tasks to be executed. */
    private final List<TimerTask> tasks = new ArrayList<>();
    
    /**
     * Generates a <code>Timer</code> object that will act as a timer.
     * @param maxTime maximum time (the fps of the game is taken)
     */
    public Timer(float maxTime) {
        this.maxTime = maxTime;
        this.counter = 0;        
    }
    
    /**
     * Adds a new task.
     * @param task new task
     * @return boolean
     */
    public boolean addTask(TimerTask task) {
        return this.tasks.add(task);
    }
    
    /**
     * Adds a new task.
     * @param task new task
     * @return {@link org.je3gl.util.Timer} otherwise <code>null</code> if an error occurs when
     * adding it
     */
    public Timer attachTask(TimerTask task) {
        if (this.addTask(task)) {
            return this;
        }
        return null;
    }
    
    /**
     * Method in charge of adding several tasks for this timer.
     * @param tasks new tasks
     * @return {@link org.je3gl.util.Timer}otherwise <code>null</code> if an error occurs
     * when adding them
     */
    public Timer attachAllTask(TimerTask... tasks) {
        if (tasks == null)
            return null;
        
        for (final TimerTask element : tasks) {
            if (element == null)
                continue;
            
            if (attachTask(element) == null) {
                return null;
            }
        }
        return this;
    }
    
    /**
     * Deletes a task
     * @param task task to be eliminated
     * @return boolean
     */
    public boolean removeTask(TimerTask task) {
        return this.tasks.remove(task);
    }
    
    /**
     * Deletes a task.
     * @param task task to be eliminated
     * @return this
     */
    public Timer detachTask(TimerTask task) {
        if (this.removeTask(task)) {
            return this;
        }
        return null;
    }
    
    /**
     * Deletes the first {@link org.je3gl.util.TimerTask} which is a subclass instance of the
     * specified class.
     * 
     * @param <T> the desired type of {@link org.je3gl.util.TimerTask}
     * @param taskClass class of the desired type
     * @return this
     */
    @SuppressWarnings("unchecked")
    public <T extends TimerTask> Timer detachTask(Class<T> taskClass) {
        if (this.detachTask(this.getTask(taskClass)) != null) {
            return this;
        }
        return null;
    }
    
    /**
     * Returns a {@link org.je3gl.util.TimerTask} according to its index or position in the
     * task list of this timer.
     * 
     * @param index index
     * @return task
     */
    public TimerTask getTask(int index) {
        return this.tasks.get(index);
    }
    
    /**
     * Returns the first {@link org.je3gl.util.TimerTask} which is a subclass instance of the
     * specified class.
     * 
     * @param <T> the desired type of {@link org.je3gl.util.TimerTask}
     * @param taskClass class of the desired type.
     * @return first state attached which is an instance of {@code taskClass}.
     */
    @SuppressWarnings("unchecked")
    public <T extends TimerTask> T getTask(Class<T> taskClass) {
        synchronized (tasks) {
            for (TimerTask task : tasks) {
                if (taskClass.isAssignableFrom(task.getClass())) {
                    return (T) task;
                }
            }
        }
        return null;
    }
    
    /**
     * Cleans tasks.
     */
    public void clearTask() {
        tasks.clear();
    }
    
    /**
     * Starts the timer.
     */
    public void start() {
        counter = 0;
        paused = false;
    }
    
    /**
     * Method in charge of stopping the execution of this timer.
     * @param pause boolean
     */
    public void pause(boolean pause) {
        paused = pause;
    }
    
    /**
     * Reset this timer.
     */
    public void reset() {
        paused = false;
        counter = 0;
    }
    
    /**
     * Stops the timer completely.
     */
    public void stop() {
        paused = true;
        counter = 0;
    }
    
    /**
     * Method in charge of updating this timer. Note that it is updated sequentially 
     * to the engine {@code JME3}.
     * 
     * @param tpf fps of the game
     * @param speed speed
     */
    public void update(float tpf, float speed) {        
        if (!paused) {
            if (counter < maxTime) {
                counter += (tpf*speed);
            }
            if ( finished() ) {
                for (final TimerTask task : this.tasks) {
                    if (task == null) {
                        continue;
                    }
                    task.doTask(this);
                }
            }
        }
    }
    
    /**
     * (non-Javadoc)
     * @return boolean
     * @deprecated Use a {@link jme3gl2.util.TimerTask} to determine if your status
     */
    @Deprecated(since = "3.0.0")
    public boolean finished() {
        if (paused) {
            return false;
        }
        return counter >= maxTime;
    }
    
    /**
     * Method in charge of managing the status of this timer.
     * @return <code>true</code> if it is running, otherwise it will return
     * <code>alse</code> if it's stopped or dead
     */
    public boolean isRun() {
        return !paused;
    }
    
    /**
     * Forcibly terminates the timer.
     */
    public void forceFinished() {
        counter = maxTime;
    }
    
    /**
     * Method in charge of returning the status.
     * @return boolean
     */
    public boolean isPaused() {
        return paused;
    }
    
    /**
     * Set a new maximum time.
     * <p>
     * <b>NOTE:</b> It should be noted that setting a new time will automatically
     * reset the timer.
     * 
     * @param maxTime maximum time
     */
    public void setMaxTime(float maxTime) {
        this.maxTime = maxTime;
        reset();
    }
    
    /**
     * Returns the difference between the elapsed time and the maximum time.
     * @return remaining time
     */
    public float getTimeLeft() {
        return (maxTime - counter);
    }
    
    /**
     * Method that can be used to make an advance or a delay of time.
     * @param counter time counter
     */
    public void setCounterTo(float counter) {
        this.counter = counter;
    }    

    /**
     * Returns the current counter.
     * @return float
     */
    public float getCounter() {
        return counter;
    }

    /**
     * Returns the current maximum time.
     * @return float
     */
    public float getMaxTime() {
        return maxTime;
    }
}
