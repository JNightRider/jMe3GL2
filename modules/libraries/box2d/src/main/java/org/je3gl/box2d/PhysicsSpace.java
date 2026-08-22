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
package org.je3gl.box2d;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.box2d.jni.b2DebugDraw;
import org.box2d.jni.b2WorldDef;
import org.box2d.jni.b2WorldId;

import static org.box2d.jni.include.Box2d.*;
import static org.box2d.jni.include.Types.*;
import static org.box2d.jni.system.Callbacks.*;
import org.je3gl.box2d.control.PhysicsBody2D;
import org.je3gl.box2d.debug.PhysicsDebugAppState;

/**
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class PhysicsSpace implements AutoCloseable {
    
    private final AtomicBoolean enableDebugger = new AtomicBoolean(false);
    private final AtomicReference<List<PhysicsBody2D>> references = new AtomicReference<>(new ArrayList<>());
    
    private int subStepCount = 4;
    
    protected b2WorldId worldId;
    
    protected b2DebugDraw debugDraw;
    
    protected PhysicsDebugAppState debugAppState;
    protected AxisType axisType = AxisType.AXIS_XYO;

    public PhysicsSpace(b2WorldDef worldDef) {
        if (worldDef == null) {
            throw new NullPointerException("b2WorldDef is null");
        }
        worldId = b2CreateWorld(worldDef, b2WorldId.malloc());
        debugDraw = b2DefaultDebugDraw(b2DebugDraw.calloc());
    }

    public void setDebugAppState(PhysicsDebugAppState debugAppState) {
        this.debugAppState = debugAppState;
    }

    public void setEnableDebugger(boolean enabled) {
        this.enableDebugger.set(enabled);
    }
    
    public void setSubStepCount(int subStepCount) {
        this.subStepCount = subStepCount;
    }
    
    public void addBody(PhysicsBody2D body2D) {
        if (body2D.isProjected()) {
            references.get().add(body2D);
        } else {
            b2CreateBody(worldId, body2D.getBodyDef(), body2D.getBodyId());
        }
        body2D.setPhysicsSpace(this);
    }
    
    public void removeBody(PhysicsBody2D body2D) {
        if (body2D.isProjected()) {
            references.get().remove(body2D);
        } else {
            b2DestroyBody(body2D.getBodyId());
        }
        body2D.setPhysicsSpace(null);
    }

    public void update(float tpf) {
        b2World_Step(worldId, tpf, subStepCount);
        if (enableDebugger.get()) {
            b2World_Draw(worldId, debugDraw);

            if (debugAppState != null && debugAppState.isInitialized()) {
                synchronized (debugAppState.getDebugProcessor().getLock()) {
                    for (PhysicsBody2D body2D : references.get()) {
                        if (body2D == null) {
                            continue;
                        }

                        body2D.flushProjectedDraw(debugAppState.getDebugProcessor());
                    }
                }
            }
        }
    }

    public void setAxisType(AxisType axisType) {
        this.axisType = axisType;
    }
    
    public AxisType getAxisType() {
        return axisType;
    }

    public b2WorldId getWorldId() {
        return worldId;
    }

    public int getSubStepCount() {
        return subStepCount;
    }

    public b2DebugDraw getDebugDraw() {
        return debugDraw;
    }
    
    @Override
    public void close() {
        b2DestroyWorld(worldId);
        b2FreeCallbacks();
        debugDraw.close();
        worldId.close();
        references.get().clear();
        debugAppState = null;
    }
}
