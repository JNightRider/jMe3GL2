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
package org.je3gl.box2d.control;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.box2d.jni.b2BodyDef;
import org.box2d.jni.b2BodyId;
import org.box2d.jni.b2CastResultCallbackFcnI;
import org.box2d.jni.b2Filter;
import org.box2d.jni.b2JointId;
import org.box2d.jni.b2Manifold;
import org.box2d.jni.b2MoverJointDef;
import org.box2d.jni.b2PogoJointDef;
import org.box2d.jni.b2Pos;
import org.box2d.jni.b2PreSolveCallbackFcnI;
import org.box2d.jni.b2QueryFilter;
import org.box2d.jni.b2ShapeId;
import org.box2d.jni.b2TreeStats;
import org.box2d.jni.b2Vec2;
import org.box2d.jni.b2WorldId;
import org.box2d.jni.b2PreContinuousCallbackFcnI;

import org.box2d.jni.system.ArenaAlloc;

import static org.box2d.jni.b2BodyType.*;
import static org.box2d.jni.b2HexColor.*;

import static org.box2d.jni.include.Box2d.*;
import static org.box2d.jni.include.Id.*;
import static org.box2d.jni.include.MathFunctions.*;
import static org.box2d.jni.include.MathTypes.b2Vec2_zero;
import static org.box2d.jni.include.Types.*;
import static org.box2d.jni.libc.LibCFloat.FLT_MAX;
import static org.box2d.jni.system.ArenaAlloc.*;

import org.je3gl.box2d.AxisType;
import org.je3gl.box2d.PhysicsSpace;
import org.je3gl.box2d.control.mover.DynamicMoverCastResult;
import org.je3gl.box2d.util.Converter;
import org.je3gl.box2d.util.MathUtils;
import org.je3gl.box2d.control.mover.Center1Fcn;
import org.je3gl.box2d.control.mover.DynamicMoverProbe;
import org.je3gl.box2d.control.mover.RadiusFcn;
import org.je3gl.box2d.debug.Drawable;
import org.je3gl.box2d.debug.draw.Draw;
import org.je3gl.box2d.listener.SpaceListener;


/**
 *
 * @author wil
 */
public class CharacterBody2D extends PhysicsBody2D implements Drawable {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(CharacterBody2D.class.getName());
    
    public static final long USE_CAST_RESULT = 0x0001L;
    public static final long USE_RESULT = 0x0002L;
    
    protected final List<b2BodyId> contacts = new ArrayList<>();
    
    /**
     * Velocity of the moving body
     */
    protected float jumpSpeed;
    protected float maxSpeed;
    protected float minSpeed;
    protected float stopSpeed;
    protected float accelerate;

    // Fraction of the ground acceleration available in the air.
    protected float airSteer;

    // Ground friction in units of 1/time.
    protected float friction;

    // A character usually wants to fall faster than the rest of the world.
    protected float gravityScale;

    // Force budget the mover joint has for reaching the target velocity. The air value
    // is small so the character keeps its momentum while jumping.
    protected float maxGroundForce;
    protected float maxAirForce;

    protected float pogoRestLength;
    protected float pogoHertz;
    protected float pogoDampingRatio;

    // Pogo force limits as a multiple of the mover weight.
    protected float pogoCompressionScale;
    protected float pogoTensionScale;

    // Enables extra ray casts when the central one misses.
    protected boolean enableSidePogo;

    // Used for the pogo to detect moving platforms.
    protected long platformMaterialId;

    // Surfaces steeper than this are not ground.
    protected float minGroundNormalY;

    protected Center1Fcn center1Fcn;
    protected RadiusFcn radiusFcn;
    
    protected b2Filter filter;

    protected b2BodyId groundId;
    protected b2JointId moverJointId;
    protected b2JointId pogoJointId;

    protected DynamicMoverCastResult castResult;
    protected DynamicMoverCastResult result;

    protected Vector2f velocity;
    protected boolean onGround;
    protected boolean walkable;
    protected boolean jumping;
    protected int jumpTicks;

    // Pogo joint state, carried across the joint being recreated each solve.
    protected float pogoImpulse;
    protected float pogoVelocity;
    protected float pogoLength;

    // Pogo ray cast results for drawing.
    protected DynamicMoverProbe[] probes;
    protected int probeCount;
    protected int selectedProbe;

    public CharacterBody2D() {
        jumpSpeed = 5.0f;
	maxSpeed = 6.0f;
	minSpeed = 0.1f;
	stopSpeed = 3.0f;
	accelerate = 20.0f;
	airSteer = 0.2f;
	friction = 8.0f;
	gravityScale = 1.5f;
	maxGroundForce = 70.0f;
	maxAirForce = 20.0f;
	pogoRestLength = 0.9f;
	pogoHertz = 5.0f;
	pogoDampingRatio = 0.8f;
	pogoCompressionScale = 100.0f;
	pogoTensionScale = 100.0f;
	enableSidePogo = false;
	platformMaterialId = 0;
	minGroundNormalY = 0.7f;

	velocity = new Vector2f();
	onGround = false;
	walkable = false;
	jumping = false;
	jumpTicks = 0;

	pogoImpulse = 0.0f;
	pogoVelocity = 0.0f;
	pogoLength = 0.0f;

        probes = new DynamicMoverProbe[3];
	for (int i = 0; i < probes.length; i++) {
            probes[i] = new DynamicMoverProbe(new Vector3f(), new Vector3f(), 1.0f, false);
	}

	probeCount = 0;
	selectedProbe = 0;

	castResult = new DynamicMoverCastResult();
        result = new DynamicMoverCastResult();
        bodyDef.motionLocks().angularZ(true);
        bodyDef.enableSleep(false);
        addSpaceListener(spaceListener);
    }

    public void addContact(b2BodyId id) {
        if (bodyId == id) {
            throw new IllegalStateException();
        }
        if (id == null) {
            throw new NullPointerException();
        }
        if (contacts.contains(id)) {
            LOGGER.warning(() -> "This body already exists as a possible contact: " + id);
        } else {
            contacts.add(id);
        }
    }

    public void removeContact(b2BodyId id) {
        contacts.remove(id);
    }
    
    private final b2CastResultCallbackFcnI CastCallback = (b2ShapeId shapeId, b2Pos point, b2Vec2 normal, float fraction, long context) -> {
        if (context == USE_CAST_RESULT) {
            castResult.point = Converter.toVector3f(point, getPhysicsSpace().getAxisType());
            castResult.normal = Converter.toVector3f(normal, getPhysicsSpace().getAxisType());
            castResult.bodyId = b2Shape_GetBody(shapeId, b2BodyId.malloc());
            castResult.userMaterialId = b2Shape_GetUserMaterial(shapeId);
            castResult.fraction = fraction;
            castResult.hit = true;
        } else {
            result.point = Converter.toVector3f(point, getPhysicsSpace().getAxisType());
            result.normal = Converter.toVector3f(normal, getPhysicsSpace().getAxisType());
            result.bodyId = b2Shape_GetBody(shapeId, b2BodyId.malloc());
            result.userMaterialId = b2Shape_GetUserMaterial(shapeId);
            result.fraction = fraction;
            result.hit = true;
        }
        return fraction;
    };

    private final b2PreSolveCallbackFcnI PreSolveStatic = (b2ShapeId shapeIdA, b2ShapeId shapeIdB, b2Manifold manifold, long context) -> {
        preSolve(shapeIdA, shapeIdB, manifold);
    };
    
    protected void preSolve( b2ShapeId shapeIdA, b2ShapeId shapeIdB, b2Manifold manifold ) {
        try (ArenaAlloc arena = allocPush()) {
            b2BodyId idA = b2Shape_GetBody(shapeIdA, b2BodyId.calloc(arena));
            b2BodyId idB = b2Shape_GetBody(shapeIdB, b2BodyId.calloc(arena));

            for (b2BodyId id : contacts) {
                if (B2_ID_EQUALS(idA, id) && B2_ID_EQUALS(idB, bodyId)) {
                    if (manifold.normal().y() < 0.0f) {
                        manifold.pointCount(0);
                    }
                    break;
                } else if (B2_ID_EQUALS(idA, bodyId) && B2_ID_EQUALS(idB, id)) {
                    if (manifold.normal().y() > 0.0f) {
                        manifold.pointCount(0);
                    }
                    break;
                }
            }
        }
    }

    private final b2PreContinuousCallbackFcnI PreContinuousStatic = (b2ShapeId shapeIdA, b2ShapeId shapeIdB, b2Pos point, b2Vec2 normal, long context) -> {
        return preContinuous(shapeIdA, shapeIdB, point, normal);
    };

    protected boolean preContinuous( b2ShapeId shapeIdA, b2ShapeId shapeIdB, b2Pos point, b2Vec2 normal ) {
        try (ArenaAlloc arena = allocPush()) {
            b2BodyId idA = b2Shape_GetBody(shapeIdA, b2BodyId.calloc(arena));
            b2BodyId idB = b2Shape_GetBody(shapeIdB, b2BodyId.calloc(arena));

            for (b2BodyId id : contacts) {
                if (B2_ID_EQUALS(idA, id) && B2_ID_EQUALS(idB, bodyId)) {
                    if (normal.y() < 0.0f) {
                        return false;
                    }

                    return true;
                }

                if (B2_ID_EQUALS(idA, bodyId) && B2_ID_EQUALS(idB, id)) {
                    if (normal.y() > 0.0f) {
                        return false;
                    }

                    return true;
                }
            }

            return true;
        }
    }

    private final SpaceListener spaceListener = new SpaceListener() {
        @Override
        public void spaceAttached(PhysicsSpace physicsSpace) {
            CharacterBody2D.this.physicsSpace = physicsSpace;
            try (ArenaAlloc arena = allocPush()) {
                b2BodyDef groundDef = b2DefaultBodyDef(b2BodyDef.calloc(arena));
                groundId = b2CreateBody(physicsSpace.getWorldId(), groundDef, b2BodyId.malloc());

                createMoverJoint(groundId, b2Vec2.calloc(arena).set(maxGroundForce, 0.0f));
            }
            b2World_SetPreSolveCallback(physicsSpace.getWorldId(), PreSolveStatic, PreContinuousStatic, NULL);
            physicsSpace.addDrawable(CharacterBody2D.this);
        }

        @Override
        public void spaceDetached(PhysicsSpace physicsSpace) {
            physicsSpace.removeDrawable(CharacterBody2D.this);
            nb2World_SetPreSolveCallback(physicsSpace.getWorldId().address(), NULL, NULL, NULL);
        }
    };

    protected void createMoverJoint(b2BodyId baseId, b2Vec2 maxForce) {
        try (ArenaAlloc arena = allocPush()) {
            b2MoverJointDef moverDef = b2DefaultMoverJointDef(b2MoverJointDef.calloc(arena));
            moverDef.linearVelocity(Converter.toB2Vec2(velocity, b2Vec2.calloc(arena)));
            moverDef.maxVelocityForce(maxForce);
            moverDef.base().bodyIdA(baseId);
            moverDef.base().bodyIdB(bodyId);
            moverDef.base().collideConnected(true);
            if (moverJointId != null) {
                moverJointId.close();
            }
            moverJointId = b2CreateMoverJoint(physicsSpace.getWorldId(), moverDef, b2JointId.malloc());
        }
    }

    public boolean applyJump() {
        if (onGround == false || walkable == false) {
            return false;
        }

        float surfaceVelocity = 0.0f;
        if (b2Body_IsValid(castResult.bodyId)) {
            try (ArenaAlloc arena = allocPush()) {
                b2Pos point = b2Pos.calloc(arena).set(castResult.point.x, castResult.point.y);
                b2Vec2 v = b2Body_GetWorldPointVelocity(castResult.bodyId, point, b2Vec2.calloc(arena));
                surfaceVelocity = v.y();
            }
        }

        try (ArenaAlloc arena = allocPush()) {
            float mass = b2Body_GetMass(bodyId);
            float vy = b2Body_GetLinearVelocity(bodyId, b2Vec2.calloc(arena)).y();

            // Remove the pogo constraint velocity but add the surface velocity.
            float dv = b2MaxFloat(0.0f, jumpSpeed - vy) + surfaceVelocity;
            b2Body_ApplyLinearImpulseToCenter(bodyId, b2Vec2.calloc(arena).set(0.0f, mass * dv), true);

            // This removes the pogo step down on the next update.
            onGround = false;
            walkable = false;
            jumping = true;
            jumpTicks = 0;
        }
        return true;
    }
    
    public void applyMoveAndUpdate(float timeStep, float throttle) {
        applyMoveAndUpdate(timeStep, throttle, 1.0f);
    }

    public void applyMoveAndUpdate(float timeStep, float throttle, float speedScale) {
        if (filter == null) {
            LOGGER.log(Level.WARNING, "Set a filter to proceed with updating this character's body.");
            return;
        }
        if (moverJointId == null) {
            LOGGER.log(Level.WARNING, "Inicialize b");
            return;
        }
        
        b2WorldId worldId = physicsSpace.getWorldId();
        AxisType axis = physicsSpace.getAxisType();

        try (ArenaAlloc arena = allocPush()) {
            // Reach further while grounded so the spring can find the ground over a step
            float stepDownLength = pogoRestLength;
            float rayLength = onGround ? pogoRestLength + stepDownLength : pogoRestLength;
            b2Vec2 translation = b2Vec2.calloc(arena).set(0.0f, -rayLength);

            b2Pos position = b2Body_GetPosition(bodyId, b2Pos.calloc(arena));
            b2Pos origin = MathUtils.sum(position, center1Fcn.center1(arena), b2Pos.calloc(arena));

            b2QueryFilter queryFilter = b2QueryFilter.calloc(arena)
                    .categoryBits(filter.categoryBits())
                    .maskBits(filter.maskBits());

            castResult.free();
            castResult = new DynamicMoverCastResult();
            b2World_CastRay(worldId, origin, translation, queryFilter, CastCallback, USE_CAST_RESULT, b2TreeStats.calloc(arena));

            probes[0].set(origin, translation, castResult.hit ? castResult.fraction : 1.0f, castResult.hit, axis);
            probeCount = 1;
            selectedProbe = 0;

            b2Vec2 pogoOffset =  b2Vec2_zero;

            if (castResult.hit == false && enableSidePogo) {
                float direction = b2Body_GetLinearVelocity(bodyId, b2Vec2.calloc(arena)).x() < 0.0f ? -1.0f : 1.0f;
                b2Vec2 offsets[] = {
                    b2Vec2.calloc(arena).set(direction * radiusFcn.radius(), 0.0f),
                    b2Vec2.calloc(arena).set(-direction * radiusFcn.radius(), 0.0f)
                };

                float bestDisplacement = FLT_MAX;
                for (b2Vec2 offset : offsets) {
                    result.free();
                    result = new DynamicMoverCastResult();
                    b2Pos sideOrigin = MathUtils.sum(origin, offset, b2Pos.calloc(arena));
                    b2World_CastRay(worldId, sideOrigin, translation, queryFilter, CastCallback, USE_RESULT, b2TreeStats.calloc(arena));

                    probes[probeCount].set(sideOrigin, translation, result.hit ? result.fraction : 1.0f, result.hit, axis);

                    // Use the result that causes the least movement.
                    if (result.hit) {
                        float displacement = b2AbsFloat(result.fraction * rayLength - pogoRestLength);
                        if (displacement < bestDisplacement) {
                            bestDisplacement = displacement;
                            castResult = result;
                            selectedProbe = probeCount;
                            pogoOffset = offset;
                        }
                    }

                    probeCount += 1;
                }
            }

            // Should jumping end?
            if (jumping) {
                jumpTicks += 1;

                // Jump ticks allow time for the jump to leave the ground. Otherwise jumping ends
                // when the character approaches the current ground.
                if (jumpTicks > 2 && castResult.hit && b2Dot(Converter.toB2Vec2(velocity, b2Vec2.calloc(arena)), Converter.toB2Vec2(castResult.normal, b2Vec2.calloc(arena))) <= 0.0f) {
                    jumping = false;
                }
            }

            if (castResult.hit) {
                // The cast can still hit while jumping.
                onGround = jumping == false;

                // Is the ground too steep to walk?
                walkable = castResult.normal.y >= minGroundNormalY;
            } else {
                onGround = false;
                walkable = false;
            }

            // Connect the mover joint to platform. Acts like friction.
            b2BodyId baseId = groundId;
            if (castResult.hit && platformMaterialId != 0 && castResult.userMaterialId == platformMaterialId
                    && b2Body_GetType(castResult.bodyId) == b2_kinematicBody) {
                baseId = castResult.bodyId;
            }

            if (B2_ID_EQUALS(b2Joint_GetBodyA(moverJointId, b2BodyId.calloc(arena)), baseId) == false) {
                b2Vec2 maxForce = b2MoverJoint_GetMaxVelocityForce(moverJointId, b2Vec2.calloc(arena));
                b2DestroyJoint(moverJointId);
                createMoverJoint(baseId, maxForce);
            }

            b2Vec2 velocity0 = MathUtils.diff(
                    b2Body_GetLinearVelocity(bodyId, b2Vec2.calloc(arena)),
                    b2Body_GetLinearVelocity(baseId, b2Vec2.calloc(arena)),
                    b2Vec2.calloc(arena)
            );
            velocity = Converter.toVector2f(velocity0);

            // Friction
            if (onGround) {
                float speed = velocity.length();
                if (speed < minSpeed) {
                    velocity.x = 0.0f;
                } else {
                    // Linear damping above stopSpeed and fixed reduction below stopSpeed
                    float control = speed < stopSpeed ? stopSpeed : speed;

                    // friction has units of 1/time
                    float drop = control * friction * timeStep;
                    float newSpeed = b2MaxFloat(0.0f, speed - drop);
                    velocity.x *= newSpeed / speed;
                }
            }

            // Mover force
            if (onGround) {
                float maxForce = walkable ? maxGroundForce : 0.0f;
                b2MoverJoint_SetMaxVelocityForce(moverJointId, b2Vec2.calloc(arena).set(maxForce, 0.0f));
            } else {
                b2MoverJoint_SetMaxVelocityForce(moverJointId, b2Vec2.calloc(arena).set(maxAirForce, 0.0f));
            }

            float maxSpeed0 = speedScale * maxSpeed;
            b2Vec2 desiredVelocity = b2Vec2.calloc(arena).set(maxSpeed0 * throttle, 0.0f);
            FloatBuffer desiredSpeed = arena.callocFloat(1);
            b2Vec2 desiredDirection = b2GetLengthAndNormalize(desiredSpeed, desiredVelocity, b2Vec2.calloc(arena));

            if (desiredSpeed.get(0) > maxSpeed0) {
                desiredSpeed.put(0, maxSpeed0);
            }

            // Accelerate
            float currentSpeed = b2Dot(Converter.toB2Vec2(velocity, b2Vec2.calloc(arena)), desiredDirection);
            float addSpeed = desiredSpeed.get(0) - currentSpeed;
            if (addSpeed > 0.0f) {
                float steer = walkable ? 1.0f : airSteer;
                float accelSpeed = steer * accelerate * maxSpeed0 * timeStep;
                if (accelSpeed > addSpeed) {
                    accelSpeed = addSpeed;
                }

                velocity.x += accelSpeed * desiredDirection.x();
            }

            // The pogo joint is rebuilt every solve because it may land on a different body
            if (pogoJointId != null && b2Joint_IsValid(pogoJointId)) {
                pogoLength = b2PogoJoint_GetCurrentLength(pogoJointId);
                pogoImpulse = b2PogoJoint_GetImpulse(pogoJointId);
                pogoVelocity = b2PogoJoint_GetCurrentSpeed(pogoJointId);

                b2DestroyJoint(pogoJointId);
                pogoJointId.close();
                pogoJointId = null;
            }

            if (castResult.hit == true) {
                float moverMass = b2Body_GetMass(bodyId);
                float moverWeight = gravityScale * b2Length(b2World_GetGravity(worldId, b2Vec2.calloc(arena))) * moverMass;

                b2PogoJointDef pogoDef = b2DefaultPogoJointDef(b2PogoJointDef.calloc(arena));
                pogoDef.base().localFrameA().p(b2Body_GetLocalPoint(castResult.bodyId, Converter.toB2Pos(castResult.point, b2Pos.calloc(arena)), b2Vec2.calloc(arena)));
                pogoDef.base().localFrameB().p(MathUtils.sum(center1Fcn.center1(arena), pogoOffset, b2Vec2.calloc(arena)));
                pogoDef.normal(Converter.toB2Vec2(castResult.normal, b2Vec2.calloc(arena)));
                pogoDef.base().bodyIdA(castResult.bodyId);
                pogoDef.base().bodyIdB(bodyId);
                pogoDef.base().collideConnected(true);
                pogoDef.restLength(pogoRestLength);
                pogoDef.hertz(pogoHertz);
                pogoDef.dampingRatio(pogoDampingRatio);
                pogoDef.maxCompressionForce(pogoCompressionScale * moverWeight);

                // Warm start from the joint that was just destroyed
                pogoDef.impulse(pogoImpulse);
                pogoDef.velocity(pogoVelocity);

                if (jumping) {
                    // Don't allow the pogo to pull down
                    pogoDef.maxTensionForce(0.0f);
                } else {
                    // The pogo can pull down at a multiple of the gravity force.
                    pogoDef.maxTensionForce(pogoTensionScale * moverWeight);
                }

                pogoJointId = b2CreatePogoJoint(worldId, pogoDef, b2JointId.malloc());
            } else {
                pogoImpulse = 0.0f;
                pogoVelocity = 0.0f;
            }

            b2MoverJoint_SetLinearVelocity(moverJointId, Converter.toB2Vec2(velocity, b2Vec2.calloc(arena)));
        }
    }

    @Override
    public void close() {
        super.close();
        if (groundId != null && B2_IS_NON_NULL(groundId)) {
            b2DestroyBody(groundId);
            groundId.close();
            groundId = null;
        }
        // Joints are implicitly destroyed.
        if (moverJointId != null) {
            b2DestroyJoint(moverJointId);
            moverJointId.close();
            moverJointId = null;
        }
        if (pogoJointId != null) {
            b2DestroyJoint(pogoJointId);
            pogoJointId.close();            
            pogoJointId = null;
        }
    }

    /**
     * The ground probe is a point, a circle, or a segment, all carried by the
     * proxy.
     *
     * @param draw Draw
     * @param norigin Vector3f
     * @param ntranslation Vector3f
     * @param fraction float
     * @param hit boolean
     */
    private void drawPogo(Draw draw, b2Pos origin, b2Vec2 translation, float fraction, boolean hit) {
        try (ArenaAlloc arena = allocPush()) {
            int color = hit ? b2_colorPlum : b2_colorGray;
            b2Vec2 delta = MathUtils.product(fraction, translation, b2Vec2.calloc(arena));

            draw.drawLine(origin, MathUtils.sum(origin, delta, b2Vec2.calloc(arena)), b2_colorGray);
            draw.drawPoint(MathUtils.sum(origin, delta, b2Pos.calloc(arena)), 10.0f, color);
        }
    }

    @Override
    public void draw(Draw draw) {
        try (ArenaAlloc arena = allocPush()) {
            for (int i = 0; i < probeCount; i++) {
                DynamicMoverProbe probe = probes[i];
                boolean selected = probe.hit && i == selectedProbe;
                drawPogo(
                        draw,
                        Converter.toB2Pos(probe.origin, b2Pos.calloc(arena)),
                        Converter.toB2Vec2(probe.translation, b2Vec2.calloc(arena)),
                        probe.fraction,
                        selected
                );
            }

            b2Pos position = b2Body_GetPosition(bodyId, b2Pos.calloc(arena));
            b2Pos vel = b2Pos.calloc(arena).set(position.x().doubleValue() + velocity.x, position.y().doubleValue() + velocity.y);

            int color = onGround ? b2_colorOrange : b2_colorAquamarine;
            draw.drawLine(position, vel, color);
        }
    }

    public void setEnableSidePogo(boolean enableSidePogo) {
        this.enableSidePogo = enableSidePogo;
    }

    public void setPlatformMaterialId(long platformMaterialId) {
        this.platformMaterialId = platformMaterialId;
    }

    public void setJumpSpeed(float jumpSpeed) {
        this.jumpSpeed = jumpSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setMinSpeed(float minSpeed) {
        this.minSpeed = minSpeed;
    }

    public void setStopSpeed(float stopSpeed) {
        this.stopSpeed = stopSpeed;
    }

    public void setAccelerate(float accelerate) {
        this.accelerate = accelerate;
    }

    public void setAirSteer(float airSteer) {
        this.airSteer = airSteer;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    @Override
    public void setGravityScale(float gravityScale) {
        super.setGravityScale(gravityScale);
        this.gravityScale = gravityScale;
    }

    public void setMaxGroundForce(float maxGroundForce) {
        this.maxGroundForce = maxGroundForce;
    }

    public void setMaxAirForce(float maxAirForce) {
        this.maxAirForce = maxAirForce;
    }

    public void setPogoRestLength(float pogoRestLength) {
        this.pogoRestLength = pogoRestLength;
    }

    public void setPogoHertz(float pogoHertz) {
        this.pogoHertz = pogoHertz;
    }

    public void setPogoDampingRatio(float pogoDampingRatio) {
        this.pogoDampingRatio = pogoDampingRatio;
    }

    public void setPogoCompressionScale(float pogoCompressionScale) {
        this.pogoCompressionScale = pogoCompressionScale;
    }

    public void setPogoTensionScale(float pogoTensionScale) {
        this.pogoTensionScale = pogoTensionScale;
    }

    public void setMinGroundNormalY(float minGroundNormalY) {
        this.minGroundNormalY = minGroundNormalY;
    }

    public void setCenter1Fcn(Center1Fcn center1Fcn) {
        this.center1Fcn = center1Fcn;
    }

    public void setRadiusFcn(RadiusFcn radiusFcn) {
        this.radiusFcn = radiusFcn;
    }

    public void setFilter(b2Filter filter) {
        this.filter = filter;
    }

    public void setGroundId(b2BodyId groundId) {
        this.groundId = groundId;
    }

    public void setMoverJointId(b2JointId moverJointId) {
        this.moverJointId = moverJointId;
    }

    public void setPogoJointId(b2JointId pogoJointId) {
        this.pogoJointId = pogoJointId;
    }

    public void setCastResult(DynamicMoverCastResult castResult) {
        this.castResult = castResult;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public void setJumpTicks(int jumpTicks) {
        this.jumpTicks = jumpTicks;
    }

    public void setPogoImpulse(float pogoImpulse) {
        this.pogoImpulse = pogoImpulse;
    }

    public void setPogoVelocity(float pogoVelocity) {
        this.pogoVelocity = pogoVelocity;
    }

    public void setPogoLength(float pogoLength) {
        this.pogoLength = pogoLength;
    }

    public float getJumpSpeed() {
        return jumpSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getMinSpeed() {
        return minSpeed;
    }

    public float getStopSpeed() {
        return stopSpeed;
    }

    public float getAccelerate() {
        return accelerate;
    }

    public float getAirSteer() {
        return airSteer;
    }

    public float getFriction() {
        return friction;
    }

    public float getGravityScale() {
        return gravityScale;
    }

    public float getMaxGroundForce() {
        return maxGroundForce;
    }

    public float getMaxAirForce() {
        return maxAirForce;
    }

    public float getPogoRestLength() {
        return pogoRestLength;
    }

    public float getPogoHertz() {
        return pogoHertz;
    }

    public float getPogoDampingRatio() {
        return pogoDampingRatio;
    }

    public float getPogoCompressionScale() {
        return pogoCompressionScale;
    }

    public float getPogoTensionScale() {
        return pogoTensionScale;
    }

    public float getMinGroundNormalY() {
        return minGroundNormalY;
    }

    public Center1Fcn getCenter1Fcn() {
        return center1Fcn;
    }

    public b2Filter getFilter() {
        return filter;
    }

    public b2BodyId getGroundId() {
        return groundId;
    }

    public b2JointId getMoverJointId() {
        return moverJointId;
    }

    public b2JointId getPogoJointId() {
        return pogoJointId;
    }

    public DynamicMoverCastResult getCastResult() {
        return castResult;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public boolean isJumping() {
        return jumping;
    }

    public int getJumpTicks() {
        return jumpTicks;
    }

    public float getPogoImpulse() {
        return pogoImpulse;
    }

    public float getPogoVelocity() {
        return pogoVelocity;
    }

    public float getPogoLength() {
        return pogoLength;
    }

    public boolean isEnableSidePogo() {
        return enableSidePogo;
    }

    public long getPlatformMaterialId() {
        return platformMaterialId;
    }

    public DynamicMoverProbe[] getProbes() {
        return probes;
    }

    public int getProbeCount() {
        return probeCount;
    }

    public int getSelectedProbe() {
        return selectedProbe;
    }
}
