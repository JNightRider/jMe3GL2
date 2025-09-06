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
package org.je3gl.physics.scene.tile;

import org.dyn4j.geometry.Convex;

import org.je3gl.physics.collision.CollisionShape;
import org.je3gl.scene.tile.TilePhysicsSystemI;
import org.je3gl.util.Arg;
import static org.je3gl.scene.tile.TilePhysicsSystem.*;
import static org.je3gl.utilities.GeometryUtilities.*;

/**
 *
 * @author wil
 */
public final class Dyn4jTilePhysicsSystem implements TilePhysicsSystemI<Object> {
    
    private static final String VF_CREATE_RECTANGLE   = "CreateRectangle";
    private static final String VF_WRAP_COLLISION     = "WrapCollision";
    
    public static void initialize() {
        System.setProperty(VKF_CREATE_RECTANGLE, VF_CREATE_RECTANGLE);
        System.setProperty(VKF_WRAP_COLLISION, VF_WRAP_COLLISION);
        System.setProperty(VKF_NEW_INSTANCE, Dyn4jTilePhysicsSystem.class.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(String name, Arg<?>... args) {
        if (name == null) {
            return null;
        }        
        return switch (name) {
            case VF_CREATE_RECTANGLE -> dyn4jCreateRectangle(args[0].getDouble(), args[1].getDouble());
            case VF_WRAP_COLLISION -> new CollisionShape((Convex) args[0].getSource());
            default -> null;
        };
    }
}
