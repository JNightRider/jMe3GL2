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
package jme3gl2.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Un objeto de la clase <code>Timer</code> se puede utilizar como temporizador
 * secuencial. Es decir que se actualiza junto con las escenas del juego.
 * <p>
 * Para cativar o realizar una/varias tareas, se utiliza la interfaz
 * <code>TimerTask</code>.</p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 *
 * @since 1.2.0
 */
@SuppressWarnings(value = {"unchecked"})
public class Timer {
    
    /** Tiempo maximo del temporizador. */
    private float maxTime = 10f;
    
    /** Contador. */
    private float counter = 0;
    
    /**
     * {@code true} si el temporizador esta detenida, es decir que dejara de
     * contar, de lo contrario {@code false}.
     */
    private boolean paused = true;
    
    /**
     * Lista de tareas a ejecutar.
     */
    private final List<TimerTask> tasks 
                        = new ArrayList<>();
    
    /**
     * Genera un objeto <code>Timer</code> que actuara como un temporizador.
     * @param maxTime tiempo maximo (se toma los fps del juego).
     */
    public Timer(float maxTime) {
        this.maxTime = maxTime;
        counter = 0;        
    }
    
    /**
     * Agrega una nueva tarea.
     * @param task tarea nueva.
     * @return estado.
     */
    public boolean addTask(TimerTask task) {
        return this.tasks.add(task);
    }
    
    /**
     * Agrega una nueva tarea.
     * @param task tarea nueva.
     * @return {@link Timer} de lo contrario <code>null</code> si sucede un
     *          erro al agregarlo.
     */
    public Timer attachTask(TimerTask task) {
        if (this.addTask(task)) {
            return this;
        }
        return null;
    }
     /**
      * Método encargado de agregar varias tareas para este temporizador.
      * @param tasks nuevas tareas.
      * @return {@link Timer} de lo contrario <code>null</code> si sucede un
     *          erro al agregarlos.
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
     * Elimina una tarea.
     * @param task tarea a eliminar.
     * @return estado.
     */
    public boolean removeTask(TimerTask task) {
        return this.tasks.remove(task);
    }
    
    /**
     * Elimina una tarea.
     * @param task tarea a eliminar.
     * @return este temporizador.
     */
    public Timer detachTask(TimerTask task) {
        if (this.removeTask(task)) {
            return this;
        }
        return null;
    }
    
   /**
     * Elimina el primer {@link TimerTask} que es una instancia de subclase 
     * de la clase especificada.
     * 
     * @param <T> el tipo deseado de {@link TimerTask}
     * @param taskClass clase del tipo deseado.
     * @return este temporizador.
     */
    public <T extends TimerTask> Timer detachTask(Class<T> taskClass) {
        if (this.detachTask(this.getTask(taskClass)) != null) {
            return this;
        }
        return null;
    }
    
    /**
     * Devuelve un {@link TimerTask} según su índice o posición en la lista de 
     * de tareas de este temporizador.
     * @param index índice.
     * @return tarera.
     */
    public TimerTask getTask(int index) {
        return this.tasks.get(index);
    }
    
    /**
     * Devuelve el primer {@link TimerTask} que es una instancia de subclase 
     * de la clase especificada.
     * 
     * @param <T> el tipo deseado de {@link TimerTask}
     * @param taskClass clase del tipo deseado.
     * @return Primer estado adjunto que es una instancia de {@code taskClass}.
     */
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
     * Limpia las tareas.
     */
    public void clearTask() {
        tasks.clear();
    }
    
    /**
     * Inicia el temporizador.
     */
    public void start() {
        counter = 0;
        paused = false;
    }
    
    /**
     * Método encargado de deterner la ejecuación de este temporizador.
     * @param pause estado del temporizador.
     */
    public void pause(boolean pause) {
        paused = pause;
    }
    
    /**
     * Resetea este temporizador.
     */
    public void reset() {
        paused = false;
        counter = 0;
    }
    
    /**
     * Detiene por completo el temporizador.
     */
    public void stop() {
        paused = true;
        counter = 0;
    }
    
    /**
     * Método encargado de actualizar este temporizado. Ten encuenta que se
     * actualiza de manera secuencial al motor {@code jme3}.
     * 
     * @param tpf fps del juego.
     * @param speed velocidad.
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
                    task.doTask();
                }
            }
        }        
    }
    
    /**
     * Determina si a finalizado ó alcanzado el tiempo maximo esperado.
     * @return un valor lógico.
     */
    public boolean finished() {
        if (paused) {
            return false;
        }
        return counter >= maxTime;
    }
    
    /**
     * Método encargado de gestionar el estado de este temporizador.
     * @return {@code true} si esta en ejecución, de lo contrario devolvería
     *          {@code false} si esta detenido o muero.
     */
    public boolean isRun() {
        return !paused;
    }
    
    /**
     * Hace que finalize el temporizador de manera forzada.
     */
    public void forceFinished() {
        counter = maxTime;
    }
    
    /**
     * Método encargado de devolver el estado.
     * @return estado.
     */
    public boolean isPaused() {
        return paused;
    }
    
    /**
     * Establece un nuevo tiempo maximo.
     * <p>
     * <b>NOTA:</b> Se debe tener en cuenta que al establecer un nuevo tiempo, 
     * se reiniciará automáticamente el temporizador.</p>
     * 
     * @param maxTime tiempo maximo.
     */
    public void setMaxTime(float maxTime) {
        this.maxTime = maxTime;
        reset();
    }
    
    /**
     * Devuelve la diferencia entre el tiempo transcurrido y el maximo.
     * @return tiempo restante.
     */
    public float getTimeLeft() {
        return (maxTime - counter);
    }
    
    /**
     * Método que se puede utiliza para hacer un adelanto o un atraso de tiempo.
     * @param counter contador de tiempo.
     */
    public void setCounterTo(float counter) {
        this.counter = counter;
    }    

    /**
     * Devuelve el contador actual.
     * @return contador.
     */
    public float getCounter() {
        return counter;
    }

    /**
     * Devuelve el tiempo maximo actual.
     * @return tiempo maximo.
     */
    public float getMaxTime() {
        return maxTime;
    }
}
