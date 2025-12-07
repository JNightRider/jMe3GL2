/*
BSD 3-Clause License

Copyright (c) 2023-2025, Night Rider (Wilson)

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
package org.je3gl.scene.tile;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.je3gl.util.Arg.*;

/**
 * This class manages the physical system of TileMaps, in order to provide access
 * to the physical components used by jMe3GL2 by default.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.1.0
 */
public final class TilePhysicsSystem {
    
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(TilePhysicsSystem.class.getName());
    /** The provider of the physical space. */
    private static TilePhysicsProvider<Object> TILE_PHYSICS_SYSTEM = null;
    
    /** Key to obtain the name of the method where a rectangular shape can be created.  */
    public static final String VKF_CREATE_RECTANGLE = "org.je3gl.CreateRectangle";
    /** Key to obtain the name of the method where a collision form is wrapped. */
    public static final String VKF_WRAP_COLLISION   = "org.je3gl.WrapCollision";
    
    /**
     * Key to obtain the name of the implemented class that supports the physical
     * space for the TileMap.
     */
    public static final String VKF_NEW_INSTANCE     = "org.je3gl.TilePhysicsSystemI";

    /**
     * Method responsible for verifying the physical space provider, where if it
     * does not exist it creates a new instance.
     */
    @SuppressWarnings("unchecked")
    private static void checkTilePhysicsSystemProvider() {
        if (TILE_PHYSICS_SYSTEM == null) {
            String className = System.getProperty(VKF_NEW_INSTANCE);
            try {
                Class<?> clazz = Class.forName(className);
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                
                Object objectInstance = constructor.newInstance();
                if (objectInstance instanceof TilePhysicsProvider) {
                    TILE_PHYSICS_SYSTEM = (TilePhysicsProvider<Object>) objectInstance;
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | 
                    InstantiationException  | NoSuchMethodException  | SecurityException        | InvocationTargetException e) {
                LOGGER.log(Level.WARNING, () -> "Error instantiating the physics manager for TileMap: " + e.getMessage());
            }
        }
    }
    
    /**
     * Destroy the current provider of physical space.
     */
    public static void destroyInstance(){
        TILE_PHYSICS_SYSTEM = null;
    }
    
    /**
     * Create a rectangular shape.
     * 
     * @param w float (width)
     * @param h float (height)
     * @return shape
     */
    public static Object physicsCreateRectangle(double w, double h) {
        checkTilePhysicsSystemProvider();
        String funcName = System.getProperty(VKF_CREATE_RECTANGLE);
        return TILE_PHYSICS_SYSTEM.invoke(funcName, buildArgs(w, h));
    }
    
    /**
     * Wrap a native physics engine shape in a jMe3GL2 manageable object,
     * 
     * @param collision shape
     * @return wrapped shape
     */
    public static Object wrapCollision(Object collision) {
        checkTilePhysicsSystemProvider();
        String funcName = System.getProperty(VKF_WRAP_COLLISION);
        return TILE_PHYSICS_SYSTEM.invoke(funcName, buildArgs(collision));
    }
}
