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
package org.j3gl.box2d;

import com.jme3.app.state.AbstractAppState;
import com.jme3.math.Vector2f;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.box2d.jni.*;
import org.box2d.jni.system.*;

import static org.box2d.jni.b2BodyType.*;

import static org.box2d.jni.include.Base.*;
import static org.box2d.jni.include.Box2d.*;
import static org.box2d.jni.include.Collision.*;
import static org.box2d.jni.include.MathFunctions.*;
import static org.box2d.jni.include.Id.*;
import static org.box2d.jni.include.Types.*;
import org.box2d.jni.libc.LibCStdlib;
import static org.box2d.jni.libc.LibCStdlib.*;
import static org.box2d.jni.system.ArenaAlloc.*;
import static org.box2d.jni.system.Callbacks.*;

/**
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class Box2dAppState extends AbstractAppState {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(Box2dAppState.class.getName());
    /** Physical space configurations. */
    private Settings settings;
    
    /**The physical space of bodies. */
    protected PhysicsSpace physicsSpace = null;   

    //--------------------------------------------------------------------------
    //                       Multithreaded fields
    //--------------------------------------------------------------------------
    /** Type of thread on which the physics engine runs. */
    protected ThreadingType threadingType = null;

    public Box2dAppState() {
        this.settings = new Settings();
        startPhysics();
    }
    
    /**
     * Initialize physics for physical bodies.
     */
    private void startPhysics() {
        if (this.initialized) {
            return;
        }

        b2SetAllocator(
            (size, alignment) -> naligned_alloc(alignment, size),
            (mem, size) -> naligned_free(mem)
        );

        b2SetAssertFcn((condition, fileName, lineNumber) -> {
            LOGGER.log(Level.SEVERE, "{0}, {1}, line {2}", new Object[] {
                condition, fileName, lineNumber
            });
            return 1;
        });
        
        b2SetLogFcn((message) -> {
            Debug.apiPrint(message);
        });
        
        try (ArenaAlloc arena = allocPush()) {
            b2WorldDef worldDef = b2DefaultWorldDef(b2WorldDef.calloc(arena));
            
            Vector2f gravity = settings.getGravity();
            worldDef.gravity(b2Vec2.calloc(arena).set(gravity.x, gravity.y));
            
            physicsSpace = new PhysicsSpace(worldDef);
        }
        
        this.initialized = true;
    }

    @Override
    public void update(float tpf) {
        physicsSpace.update(tpf);
    }

    @Override
    public void cleanup() {
        physicsSpace.close();
        super.cleanup();
    }

    public PhysicsSpace getPhysicsSpace() {
        return physicsSpace;
    }
}
