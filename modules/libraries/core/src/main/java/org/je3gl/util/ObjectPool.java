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
package org.je3gl.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.je3gl.util.pool.PoolHandler;

/**
 * An abstract class that manages a group of objects to control instances, 
 * eliminating constant creation and destruction.
 * 
 * <p>
 * This encourages the reuse of objects that can cause memory exhaustion.
 * </p>
 * 
 * @param <T> Object
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class ObjectPool<T> {
    /** Default time for group objects. */
    public static final long DEAD_TIME = 50000L;

    /**
     * A maximum time for an object to be reused, unless that object is still
     * valid and reusable.
     */
    private long deadTime;

    /** Map of objects that are locked, i.e., that are in use. */
    private Map<T, Long> lock;
    /** Map of objects that are unlocked, that is, available for use. */
    private Map<T, Long> unlock;

    private PoolHandler<T> poolHandler;

    /**
     * Constructor of the class <code>ObjectPool</code>.
     * @param poolHandler
     */
    public ObjectPool(PoolHandler<T> poolHandler) {
        this(DEAD_TIME, poolHandler);
    }

    /**
     * Constructor of the class <code>ObjectPool</code>.
     *
     * @param deadTime long|time
     * @param poolHandler
     */
    public ObjectPool(long deadTime, PoolHandler<T> poolHandler) {
        this.poolHandler = poolHandler;
        this.deadTime = deadTime;
        this.lock = new HashMap<>();
        this.unlock = new HashMap<>();
    }

    /**
     * Method responsible for creating an object with a specific size, if
     * applicable.
     *
     * @param args
     * @return object
     */
    private T create(Object... args) {
        return poolHandler.create(Arg.buildArgs(args));
    }

    /**
     * Method responsible for verifying if the object being searched for is
     * reusable.
     *
     * @param o object
     * @param args
     * @return boolean
     */
    private boolean validate(T o, Object... args) {
        return poolHandler.validate(o, Arg.buildArgs(args));
    }

    /**
     * Method responsible for destroying and freeing memory.
     *
     * @param o object
     */
    private void dead(T o) {
        poolHandler.destroy(o);
    }

    /**
     * Method that searches for an object in the group that is optimally
     * reusable; if it is not reusable, a new object with the desired capacity
     * is created, destroying the old one.
     * <p>
     * The returned object is locked, so it cannot be used by another object
     * that requires it.
     * </p>
     *
     * @param args
     * @return object
     */
    public synchronized T takePush(Object... args) {
        long now = System.currentTimeMillis();
        T t;
        if (!unlock.isEmpty()) {
            Iterator<T> e = unlock.keySet().iterator();
            while (e.hasNext()) {
                t = e.next();
                if ((now - unlock.get(t)) > deadTime) {
                    e.remove();
                    dead(t);
                    t = null;
                } else {
                    if (validate(t, args)) {
                        e.remove();
                        lock.put(t, now);
                        return t;
                    } else {
                        e.remove();
                        dead(t);
                        t = null;
                    }
                }
            }
        }
        t = create(args);
        lock.put(t, now);
        return t;
    }

    /**
     * Leave the object free so that another class can reuse it.
     *
     * @param t object
     */
    public synchronized void takePop(T t) {
        lock.remove(t);
        unlock.put(t, System.currentTimeMillis());
    }
}
