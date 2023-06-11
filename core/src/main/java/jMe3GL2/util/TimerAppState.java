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
package jMe3GL2.util;

import com.jme3.app.state.AbstractAppState;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Un <code>TimerAppState</code> se encarga de administrar todos los
 * {@link Timer} que se ejecutan en el juego.
 * 
 * <p>
 * En terminos más simples, es el encargado de actualizarlas, verificar cuando
 * se hace una pausa o reanudarlas.</p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 *
 * @since 1.3.0
 */
public final 
class TimerAppState extends AbstractAppState {

    /** Logger de la clase. */
    private static final Logger LOG = Logger.getLogger(TimerAppState.class.getName());

    /**
     * Mapa de los temporizadores.
     */
    private final Map<String, EntryTimer> timerMap
                        = new IdentityHashMap<>();

    /** demora predeterminado. */
    private float defaultDelay = 0.5F;
    
    /**
     * Devuelv la demora predeterminada.
     * @return demora.
     */
    public float getDefaultDelay() {
        return defaultDelay;
    }

    /**
     * Establece una nueva demora que se utiliza como valor predeterminado
     * para todos los <code>Timer</code> sin una demora establecido.
     * @param defaultDelay nueva demora predeterminada.
     */
    public void setDefaultDelay(float defaultDelay) {
        this.defaultDelay = defaultDelay;
    }
    
    /**
     * Método encargado de establecer un estador a un {@link Timer} en 
     * especifico.
     * @param name nombre-clave del temporizador.
     * @param state estado que tomará.
     * @return {@code true} si se aplico los cambios, de lo contrario {@code false}.
     */
    public boolean setState(String name, TimerState state) {
        EntryTimer entryTimer = this.timerMap.get(name);
        if ( entryTimer == null ) {
            LOG.log(Level.WARNING, "[ {0} ] Nonexistent timer.", name);
            return false;
        }
        
        Timer timer = entryTimer.getTimer();
        switch (state) {
            case Pause:
                timer.pause(true);
                break;
            case Resume:
                timer.pause(false);
                break;
            case Start:
                timer.start();
                break;
            case Stop:
                timer.stop();
                break;
            default:
                throw new AssertionError();
        }
        return true;
    }
    
    /**
     * Agrega un nuevo temporizador. En donde utilizara el retardo 
     * predeterminado.
     * 
     * @param name nombre clave-temporizador.
     * @param timer temporizador.
     * @return el mimo {@link Timer}.
     */
    public Timer attachTimer(String name, Timer timer) {
        return this.attachTimer(name, timer, defaultDelay);
    }
    
    /**
     * Agrega un nuevo temporizador.
     * @param name nombre clave-temporizador.
     * @param timer temporizador.
     * @param delay retardo de este temporizador.
     * @return el mimo {@link Timer}.
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
     * Elimina un temporizador del administrador.
     * @param name nombre clave-temporizador.
     * @return el temporizador eliminado si existe, de lo contrario devolvera
     *          un valor {@code null}.
     */
    public Timer detachTimer(String name) {
        EntryTimer entryTimer = this.timerMap.remove(name);
        if ( entryTimer != null ) {
            return entryTimer.getTimer();
        }
        return null;
    }
    
    /**
     * Busca un temporizador a travez de su nombre clave.
     * @param name nombre clave-temporizador.
     * @return el {@link Timer}.
     */
    public Timer getTimer(String name) {
        EntryTimer entryTimer = this.timerMap.get(name);
        if ( entryTimer == null ) {
            return null;
        }
        return entryTimer.getTimer();
    }
    
    /**
     * Agrega una nueva tarea a un temporizador especifico.
     * @see Timer#addTask(jMe3GL2.util.TimerTask) 
     * 
     * @param name nombre clave-temporizador.
     * @param task tarera-temporizador.
     * @return {@code true} si se a agregado de manera correcta la tarea, de
     *          lo contrario {@code false}.
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
     * Elimina una nueva tarea a un temporizador especifico.
     * @see Timer#removeTask(jMe3GL2.util.TimerTask)  
     * 
     * @param name nombre clave-temporizador.
     * @param task tarera-temporizador.
     * @return {@code true} si se a eliminado de manera correcta la tarea, de
     *          lo contrario {@code false}.
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
     * (non-JavaDoc)
     * @see AbstractAppState#update(float) 
     * @param tpf {@code float}
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
     * (non-JavaDoc)
     * @see AbstractAppState#setEnabled(boolean) 
     * @param enabled {@code boolean}
     */
    @Override
    public void setEnabled(boolean enabled) {
        for (final Map.Entry<String, EntryTimer> entry : this.timerMap.entrySet()) {
            EntryTimer timer = entry.getValue();
            if (timer == null) {
                continue;
            }
            
            timer.getTimer().pause(!enabled);
        }
        super.setEnabled(enabled);
    }
    
    /**
     * Clase interna encargado de almacenar en memoria el temporizador, así como
     * su retardo.
     */
    class EntryTimer {
        
        /** retardo-temporizador. */
        private final float delay;
        
        /** temporizador. */
        private final Timer timer;

        /**
         * Constructor predeterminado de la clase <code>EntryTimer</code>.
         * @param delay retardo-temporizador.
         * @param timer temporizador.
         */
        public EntryTimer(float delay, Timer timer) {
            this.delay = delay;
            this.timer = timer;
        }
        
        /**
         * Se encarga de validar los datos.
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
         * Devuelve el retardo.
         * @return {@code float}
         */
        public float getDelay() {
            return delay;
        }

        /**
         * Devuelve el temporizador.
         * @return {@link Timer}
         */
        public Timer getTimer() {
            return timer;
        }
    }
}
