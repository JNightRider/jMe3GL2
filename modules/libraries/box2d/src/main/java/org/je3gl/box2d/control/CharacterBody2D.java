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

import org.box2d.jni.b2BodyId;
import org.box2d.jni.b2CastResultFcnI;
import org.box2d.jni.b2Filter;
import org.box2d.jni.b2JointId;
import org.box2d.jni.b2PogoJointDef;
import org.box2d.jni.b2Pos;
import org.box2d.jni.b2QueryFilter;
import org.box2d.jni.b2ShapeId;
import org.box2d.jni.b2TreeStats;
import org.box2d.jni.b2Vec2;
import org.box2d.jni.b2WorldId;

import org.box2d.jni.system.ArenaAlloc;

import static org.box2d.jni.include.Box2d.*;
import static org.box2d.jni.include.Id.*;
import static org.box2d.jni.include.MathFunctions.*;
import static org.box2d.jni.include.Types.*;
import static org.box2d.jni.system.ArenaAlloc.*;
import static org.box2d.jni.system.Pointer.NULL;

import org.je3gl.box2d.control.mover.DynamicMoverCastResult;
import org.je3gl.box2d.control.mover.ShapeMover;
import org.je3gl.box2d.util.Converter;
import org.je3gl.box2d.util.MathUtils;


/**
 *
 * @author wil
 */
public class CharacterBody2D extends PhysicsBody2D {
    
    /**
     * Velocity of the moving body
     */
    protected float
            jumpSpeed,
            maxSpeed,
            minSpeed,
            stopSpeed,
            accelerate;

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

    // Surfaces steeper than this are not ground.
    protected float minGroundNormalY;

    protected ShapeMover<?> shape;
    protected b2Filter filter;

    protected b2WorldId worldId;
    protected b2BodyId moverId;
    protected b2JointId moverJointId;
    protected b2JointId pogoJointId;

    protected DynamicMoverCastResult castResult;

    protected Vector2f velocity;
    protected boolean onGround;
    protected boolean walkable;
    protected boolean jumping;
    protected int jumpTicks;

    // Pogo joint state, carried across the joint being recreated each solve.
    protected float pogoImpulse;
    protected float pogoVelocity;
    protected float pogoLength;

    // Ground probe from the last solve, kept for debug drawing. The probe ends at
    // the origin plus the fraction of the translation.
    protected Vector3f pogoOrigin;
    protected Vector3f pogoTranslation;
    protected float pogoFraction;
    protected boolean pogoHit;
    
    private final b2CastResultFcnI CastCallback = (b2ShapeId shapeId, b2Pos point, b2Vec2 normal, float fraction, long context) -> {
        castResult.point = Converter.toVector3f(point, getPhysicsSpace().getAxisType());
        castResult.normal = Converter.toVector3f(normal, getPhysicsSpace().getAxisType());
        castResult.bodyId = b2Shape_GetBody(shapeId, b2BodyId.malloc());
        castResult.fraction = fraction;
        castResult.hit = true;
        return fraction;
    };

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
	minGroundNormalY = 0.7f;

	filter = b2DefaultFilter(b2Filter.malloc());

	velocity = new Vector2f();
	onGround = false;
	walkable = false;
	jumping = false;
	jumpTicks = 0;

	pogoImpulse = 0.0f;
	pogoVelocity = 0.0f;
	pogoLength = 0.0f;

	pogoOrigin = new Vector3f();
	pogoTranslation = new Vector3f();
	pogoFraction = 1.0f;
	pogoHit = false;

	castResult = new DynamicMoverCastResult();
    }

    public boolean jump() {
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
            float mass = b2Body_GetMass(moverId);
            float vy = b2Body_GetLinearVelocity(moverId, b2Vec2.calloc(arena)).y();

            // Remove the pogo constraint velocity but add the surface velocity.
            float dv = b2MaxFloat(0.0f, jumpSpeed - vy) + surfaceVelocity;
            b2Body_ApplyLinearImpulseToCenter(moverId, b2Vec2.calloc(arena).set(0.0f, mass * dv), true);

            // This removes the pogo step down on the next update.
            onGround = false;
            walkable = false;
            jumping = true;
            jumpTicks = 0;
        }
        return true;
    }
    
    public void update(float timeStep, float throttle) {
        try (ArenaAlloc arena = allocPush()) {

            // Reach further while grounded so the spring can find the ground over a step
            float stepDownLength = pogoRestLength;
            float rayLength = onGround ? pogoRestLength + stepDownLength : pogoRestLength;
            b2Vec2 translation = b2Vec2.calloc(arena).set(0.0f, -rayLength);

            b2Pos position = b2Body_GetPosition(moverId, b2Pos.calloc(arena));
            b2Pos origin = MathUtils.sum(position, shape.center1(), b2Pos.calloc(arena));

            b2QueryFilter queryFilter = b2QueryFilter.calloc(arena)
                    .categoryBits(filter.categoryBits())
                    .maskBits(filter.maskBits());

            castResult.reset();
            b2World_CastRay(worldId, origin, translation, queryFilter, CastCallback, NULL, b2TreeStats.calloc(arena));

            pogoOrigin = Converter.toVector3f(origin, getPhysicsSpace().getAxisType());
            pogoTranslation = Converter.toVector3f(translation, getPhysicsSpace().getAxisType());
            pogoFraction = castResult.hit ? castResult.fraction : 1.0f;
            pogoHit = castResult.hit;

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

            velocity = Converter.toVector2f(b2Body_GetLinearVelocity(moverId, b2Vec2.calloc(arena)));

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

            b2Vec2 desiredVelocity = b2Vec2.calloc(arena).set(maxSpeed * throttle, 0.0f);
            FloatBuffer desiredSpeed = arena.callocFloat(1);
            b2Vec2 desiredDirection = b2GetLengthAndNormalize(desiredSpeed, desiredVelocity, b2Vec2.calloc(arena));

            if (desiredSpeed.get(0) > maxSpeed) {
                desiredSpeed.put(0, maxSpeed);
            }

            // Accelerate
            float currentSpeed = b2Dot(Converter.toB2Vec2(velocity, b2Vec2.calloc(arena)), desiredDirection);
            float addSpeed = desiredSpeed.get(0) - currentSpeed;
            if (addSpeed > 0.0f) {
                float steer = walkable ? 1.0f : airSteer;
                float accelSpeed = steer * accelerate * maxSpeed * timeStep;
                if (accelSpeed > addSpeed) {
                    accelSpeed = addSpeed;
                }

                velocity.x += accelSpeed * desiredDirection.x();
            }

            // The pogo joint is rebuilt every solve because it may land on a different body
            if (pogoJointId != null && b2Joint_IsValid(pogoJointId)) {
                pogoLength = b2PogoJoint_GetLength(pogoJointId);
                pogoImpulse = b2PogoJoint_GetImpulse(pogoJointId);
                pogoVelocity = b2PogoJoint_GetVelocity(pogoJointId);

                b2DestroyJoint(pogoJointId);
                pogoJointId.close();
                pogoJointId = null;
            }

            if (castResult.hit == true) {
                float moverMass = b2Body_GetMass(moverId);
                float moverWeight = gravityScale * b2Length(b2World_GetGravity(worldId, b2Vec2.calloc(arena))) * moverMass;

                b2PogoJointDef pogoDef = b2DefaultPogoJointDef(b2PogoJointDef.calloc(arena));
                pogoDef.base().localFrameA().p(b2Body_GetLocalPoint(castResult.bodyId, Converter.toB2Pos(castResult.point, b2Pos.calloc(arena)), b2Vec2.calloc(arena)));
                pogoDef.base().localFrameB().p(shape.center1());
                pogoDef.normal(Converter.toB2Vec2(castResult.normal, b2Vec2.calloc(arena)));
                pogoDef.base().bodyIdA(castResult.bodyId);
                pogoDef.base().bodyIdB(moverId);
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
        if (moverId != null && B2_IS_NON_NULL(moverId)) {
            b2DestroyBody(moverId);
        }
        moverId = null;

        // Joints are implicitly destroyed.
        moverJointId = null;
        pogoJointId = null;
    }
}
