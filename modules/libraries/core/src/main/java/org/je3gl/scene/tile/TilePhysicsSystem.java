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
 *
 * @author wil
 */
public final class TilePhysicsSystem {
    
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(TilePhysicsSystem.class.getName());    
    private static TilePhysicsSystemI<Object> TILE_PHYSICS_SYSTEM = null;
    
    public static final String VKF_CREATE_RECTANGLE = "org.je3gl.CreateRectangle";
    public static final String VKF_WRAP_COLLISION   = "org.je3gl.WrapCollision";
    public static final String VKF_NEW_INSTANCE     = "org.je3gl.TilePhysicsSystemI";

    @SuppressWarnings("unchecked")
    private static void checkTilePhysicsSystemI() {
        if (TILE_PHYSICS_SYSTEM == null) {
            String className = System.getProperty(VKF_NEW_INSTANCE);
            try {
                Class<?> clazz = Class.forName(className);
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                
                Object objectInstance = constructor.newInstance();
                if (objectInstance instanceof TilePhysicsSystemI) {
                    TILE_PHYSICS_SYSTEM = (TilePhysicsSystemI<Object>) objectInstance;
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | 
                    InstantiationException  | NoSuchMethodException  | SecurityException        | InvocationTargetException e) {
                LOGGER.log(Level.WARNING, () -> "Error al instanciar el gestor de física para los TileMap: " + e.getMessage());
            }
        }
    }
    
    public static Object physicsCreateRectangle(double w, double h) {
        checkTilePhysicsSystemI();
        String funcName = System.getProperty(VKF_CREATE_RECTANGLE);
        return TILE_PHYSICS_SYSTEM.invoke(funcName, buildArgs(w, h));
    }
    
    public static Object wrapCollision(Object collision) {
        checkTilePhysicsSystemI();
        String funcName = System.getProperty(VKF_WRAP_COLLISION);
        return TILE_PHYSICS_SYSTEM.invoke(funcName, buildArgs(collision));
    }
}
