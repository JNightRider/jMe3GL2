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

import org.box2d.jni.b2BodyId;
import org.box2d.jni.b2WorldDef;
import org.box2d.jni.b2WorldId;

import static org.box2d.jni.include.Box2d.*;
import org.j3gl.box2d.control.PhysicsBody2D;

/**
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class PhysicsSpace implements AutoCloseable {
    
    protected b2WorldId worldId;

    public PhysicsSpace(b2WorldDef worldDef) {
        worldId = b2CreateWorld(worldDef, b2WorldId.malloc());
    }
    
    public void addBody(PhysicsBody2D body2D) {
        b2CreateBody(worldId, body2D.getBodyDef(), body2D.getBodyId());
    }
    
    public void removeBody(b2BodyId bodyId) {
        b2DestroyBody(bodyId);
    }

    public void update(float tpf) {
        float timeStep = 1.0f / 60.0f;
        int subStepCount = 4;
        b2World_Step(worldId, timeStep, subStepCount);
    }
    
    public AxisType getAxisType() {
        return AxisType.AXIS_XYO;
    }

    public b2WorldId getWorldId() {
        return worldId;
    }
    
    @Override
    public void close() {
        b2DestroyWorld(worldId);
        worldId.close();
    }
}
