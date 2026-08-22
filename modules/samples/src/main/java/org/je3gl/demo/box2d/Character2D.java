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
package org.je3gl.demo.box2d;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;
import java.nio.FloatBuffer;
import java.util.logging.Logger;
import org.je3gl.box2d.Box2dAppState;
import org.je3gl.box2d.ThreadingType;
import org.je3gl.renderer.Camera2DAppSate;
import org.je3gl.renderer.UnitComparator;
import org.je3gl.utilities.ColorUtilities;

import org.box2d.jni.*;
import org.box2d.jni.system.*;

import static org.box2d.jni.b2BodyType.*;
import static org.box2d.jni.b2HexColor.*;

import static org.box2d.jni.include.Box2d.*;
import static org.box2d.jni.include.Collision.*;
import static org.box2d.jni.include.MathFunctions.*;
import static org.box2d.jni.include.Types.*;
import static org.box2d.jni.system.ArenaAlloc.*;
import static org.box2d.jni.system.MemoryUtil.*;
import org.je3gl.box2d.collision.CastResult;
import org.je3gl.box2d.control.CharacterBody2D;
import static org.je3gl.box2d.control.CharacterBody2D.CollisionBits.*;
import static org.je3gl.box2d.control.CharacterBody2D.PogoShape.*;
import org.je3gl.box2d.debug.PhysicsDebugAppState;
import org.je3gl.box2d.debug.PhysicsDebugSceneProcessor;
import org.je3gl.box2d.debug.batch.BatchSnapshot;
import org.je3gl.box2d.util.ParsePath;
import org.je3gl.plugins.input.BooleanStateKeyboardInputHandler;
import org.je3gl.plugins.input.InputHandlerAppState;
import org.je3gl.plugins.input.Key;
import org.je3gl.scene.control.AnimatedSprite2D;
import org.je3gl.scene.shape.Sprite;

/**
 * Class where a small platform game is exemplified and how it can be controlled 
 * using the jMe3GL2 library.
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class Character2D extends SimpleApplication  {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(Character2D.class.getName());
    
    /**
     * The main method; uses zero arguments in args array
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Character2D app = new Character2D();
        AppSettings settings = new AppSettings(true);
        settings.setGammaCorrection(false);

        app.setSettings(settings);
        app.start();
    }

    /** Object in charge of managing the 'A' key */
    private static final BooleanStateKeyboardInputHandler VK_LEFT = new BooleanStateKeyboardInputHandler(new Key(KeyInput.KEY_A, "left"));
    /** Object in charge of managing the 'D' key */
    private static final BooleanStateKeyboardInputHandler VK_RIGHT = new BooleanStateKeyboardInputHandler(new Key(KeyInput.KEY_D, "right"));
    /** Object in charge of managing the 'SPACE' key */
    private static final BooleanStateKeyboardInputHandler VK_JUMP = new BooleanStateKeyboardInputHandler(new Key(KeyInput.KEY_SPACE, "jump"));

    /**
     * Control of the character (player) in the scene.
     */
    private static class Player extends CharacterBody2D {
        
        public static final int 
                StaticBit   = 0x0001,
                MoverBit    = 0x0002,
                DynamicBit  = 0x0004,
                DebrisBit   = 0x0008,
                AllBits     = ~0;

        private final int planeCapacity = 8;
	private final Vector2f elevatorBase = new Vector2f( 112.0f, 10.0f );
	private final float elevatorAmplitude = 4.0f;

	float jumpSpeed = 10.0f;
	float maxSpeed = 3.0f;
	float minSpeed = 0.1f;
	float stopSpeed = 3.0f;
	float accelerate = 10.0f;
	float airSteer = 0.2f;
	float friction = 8.0f;
	float gravity = 30.0f;
	float pogoHertz = 5.0f;
	float pogoDampingRatio = 0.8f;

	PogoShape pogoShape = PogoSegment;
	Vector2f position = new Vector2f();
	Vector2f velocity;
	b2Capsule capsule;
	b2BodyId elevatorId;
	b2ShapeId ballId;
	b2CollisionPlane.Buffer planes = b2CollisionPlane.calloc(planeCapacity);
	int planeCount;
	int totalIterations;
	float pogoVelocity;
	float time;
	boolean onGround;
	boolean jumpReleased;
	boolean lockCamera;
        
        private CastResult castResult = new CastResult();
        
        public Player() {
        }

        private final b2CastResultFcnI CastCallback = (shapeId, point, normal, fraction, context) -> {
            castResult.setPoint(new Vector2f(point.x().floatValue(), point.y().floatValue()));
            castResult.setNormal(new Vector2f(normal.x(), normal.y()));
            castResult.setBodyId(b2Shape_GetBody( shapeId, b2BodyId.malloc() ));
            castResult.setFraction(fraction);
            castResult.setHit(true);
            return fraction;
        };
        
        private final b2PlaneResultFcnI PlaneResultFcn = (shapeId, planeResult, context) -> {
            if (planeCount < planeCapacity) {
                assert( b2IsValidPlane( (b2Plane) planeResult.plane() ) );
                float maxPush = Float.MAX_VALUE;
                boolean clipVelocity = true;
                
                try (ArenaAlloc arena = allocPush()) {
                    b2CollisionPlane plane = b2CollisionPlane.calloc(arena);
                    plane.plane((b2Plane) planeResult.plane());
                    plane.pushLimit(maxPush);
                    plane.push(0.0f);
                    plane.clipVelocity(clipVelocity);
                    planes.put(planeCount, plane);
                    planeCount++;
                }
            }
            return true;
        };

        public void setCapsule(b2Capsule capsule) {
            this.capsule = capsule;
        }
        
        @Override
        protected void ready() {
            // Mover position is center of the capsule.
            position = new Vector2f( 2.0f, 8.0f );
            velocity = new Vector2f( 0.0f, 0.0f );
                
            totalIterations = 0;
            pogoVelocity = 0.0f;
            onGround = false;
            jumpReleased = true;
            lockCamera = true;
            planeCount = 0;
            time = 0.0f;
        }

        @Override
        protected void physicsProcess(float delta) {
            float throttle = 0.0f;
            Sprite sprite = (Sprite) ((Geometry) spatial).getMesh();
            
            if ( VK_LEFT.isActive() ) {
                throttle -= 1.0f;
                sprite.flipH(true);
            }

            if ( VK_RIGHT.isActive() ) {
                throttle += 1.0f;
                sprite.flipH(false);
            }

            if (VK_JUMP.isActiveButNotHandled()) {
                VK_JUMP.setHasBeenHandled(true);
                if ( onGround == true && jumpReleased ) {
                    velocity.y = jumpSpeed;
                    onGround = false;
                    jumpReleased = false;
                }
            }  else {
                    jumpReleased = true;
            }

            SolveMove(delta, throttle);
            applyAnimation();
        }
        
        void SolveMove( float timeStep, float throttle ) {
            // Friction
            float speed = velocity.length();
            if ( speed < minSpeed )
            {
                velocity.x = 0.0f;
                velocity.y = 0.0f;
            }
            else if ( onGround )
            {
                // Linear damping above stopSpeed and fixed reduction below stopSpeed
                float control = speed < stopSpeed ? stopSpeed : speed;

                // friction has units of 1/time
                float drop = control * friction * timeStep;
                float newSpeed = b2MaxFloat( 0.0f, speed - drop );
                velocity.multLocal(newSpeed / speed);
            }
            
            
            Vector2f desiredVelocity = new Vector2f( maxSpeed * throttle, 0.0f );
            float desiredSpeed = desiredVelocity.length();
            Vector2f desiredDirection;
            
            if (desiredSpeed < FastMath.FLT_EPSILON) {
                desiredDirection = new Vector2f(0f, 0f);
            } else {
                desiredDirection = desiredVelocity.normalize();
            }
            
            if ( desiredSpeed > maxSpeed )
            {
                desiredSpeed = maxSpeed;
            }

            if ( onGround )
            {
                velocity.y = 0.0f;
            }
            
            // Accelerate
            float currentSpeed = velocity.dot(desiredDirection);
            float addSpeed = desiredSpeed - currentSpeed;
            if ( addSpeed > 0.0f )
            {
                float steer = onGround ? 1.0f : airSteer;
                float accelSpeed = steer * accelerate * maxSpeed * timeStep;
                if ( accelSpeed > addSpeed )
                {
                    accelSpeed = addSpeed;
                }

                velocity.addLocal(desiredDirection.mult(accelSpeed));
            }

            velocity.y -= gravity * timeStep;
            
            try (ArenaAlloc arena = allocPush()) {
                float pogoRestLength = 2.0f * capsule.radius();
		float rayLength = pogoRestLength + capsule.radius();
		b2Circle circle = b2Circle.calloc(arena).set( b2Vec2_zero, 0.5f * capsule.radius() );
		b2Vec2 segmentOffset = b2Vec2.calloc(arena).set( 0.75f * capsule.radius(), 0.0f );
                
                b2Segment segment = b2Segment.calloc(arena)
                        .point2(segmentOffset)
                        .point1(segmentOffset.neg());
                
                b2ShapeProxy proxy = b2ShapeProxy.calloc(arena);
		b2Vec2 translation = b2Vec2.calloc(arena);
		b2QueryFilter pogoFilter = b2QueryFilter.calloc(arena).categoryBits(MoverBit).maskBits(StaticBit | DynamicBit);
                castResult.reset();
		
                if ( pogoShape == PogoPoint )
		{
                    nb2MakeProxy( b2Vec2_zero.address(), 1, 0.0f, proxy.address() );
                    translation.set( 0.0f, -rayLength );
		}
		else if ( pogoShape == PogoCircle )
		{
                    nb2MakeProxy( b2Vec2_zero.address(), 1, circle.radius(), proxy.address() );
                    translation.set( 0.0f, -rayLength + circle.radius() );
		}
		else
		{
                    nb2MakeProxy( segment.point1().address(), 2, 0.0f, proxy.address() );
                    translation.set( 0.0f, -rayLength );
		}
                
                Vector2f norigin = position.add(new Vector2f(capsule.center1().x(), capsule.center1().y()));
                b2Pos origin = b2Pos.calloc(arena).set(norigin.x, norigin.y);
                b2World_CastShape( physicsSpace.getWorldId(), origin, proxy, translation, pogoFilter, CastCallback, NULL, b2TreeStats.calloc(arena) );
                
                // Avoid snapping to ground if still going up
		if ( onGround == false )
                {
                    onGround = castResult.isHit() && velocity.y <= 0.01f;
                }
                else
                {
                    onGround = castResult.isHit();
                }
                
		if ( castResult.isHit() == false )
		{
                    pogoVelocity = 0.0f;
		}
		else
		{
                    float pogoCurrentLength = castResult.getFraction() * rayLength;

                    float offset = pogoCurrentLength - pogoRestLength;
                    pogoVelocity = b2SpringDamper( pogoHertz, pogoDampingRatio, offset, pogoVelocity, timeStep );

                    b2Pos point = b2Pos.calloc(arena).set(castResult.getPoint().x, castResult.getPoint().y);
                    b2Body_ApplyForce( castResult.getBodyId(), b2Vec2.calloc(arena).set( 0.0f, -50.0f ), point, true );
		}
                
                b2Pos target = b2Pos.calloc(arena)
                        .x(position.x + timeStep * velocity.x + timeStep * pogoVelocity * 0.0f)
                        .y(position.y + timeStep * velocity.y + timeStep * pogoVelocity * 1.0f);
                
                // Mover overlap filter
		b2QueryFilter collideFilter = b2QueryFilter.calloc(arena).categoryBits( MoverBit ).maskBits( StaticBit | DynamicBit | MoverBit );

		// Movers don't sweep against other movers, allows for soft collision
		b2QueryFilter castFilter = b2QueryFilter.calloc(arena).categoryBits( MoverBit ).maskBits( StaticBit | DynamicBit );
                
                totalIterations = 0;
		float tolerance = 0.01f;
                
                for ( int iteration = 0; iteration < 5; ++iteration )
		{
                    planeCount = 0;

                    b2Capsule mover = capsule;

                    b2World_CollideMover( physicsSpace.getWorldId(), b2Pos.calloc(arena).set(position.x, position.y), mover, collideFilter, PlaneResultFcn, NULL );
                    
                    b2PlaneSolverResult result = b2PlaneSolverResult.calloc(arena);
                    nb2SolvePlanes( 
                            b2Vec2.calloc(arena).set(target.x().floatValue() - position.x, target.y().floatValue() - position.y).address(),
                            planes.address(),
                            planeCount,
                            result.address()
                    );

                    totalIterations += result.iterationCount();

                    float fraction = b2World_CastMover( physicsSpace.getWorldId(), b2Pos.calloc(arena).set(position.x, position.y), mover, result.translation(), castFilter );

                    b2Vec2 delta = b2Vec2.calloc(arena).set(fraction, fraction).mult(result.translation());                    
                    position.addLocal(delta.x(), delta.y());

                    if ( b2LengthSquared( delta ) < tolerance * tolerance )
                    {
                        break;
                    }
		}

                b2Vec2 m_velocity = b2Vec2.calloc(arena);
                nb2ClipVector( b2Vec2.calloc(arena).set(velocity.x, velocity.y).address(), planes.address(), planeCount, m_velocity.address() );
                velocity.set(m_velocity.x(), m_velocity.y());
            }
        }

        @Override
        public void flushProjectedDraw(PhysicsDebugSceneProcessor physicsProcessor) {
            int color = onGround ? b2_colorOrange : b2_colorAquamarine;
            
            try (ArenaAlloc arena = allocPush()) {
                b2Pos p1 = b2Pos.calloc(arena).set(position.x + capsule.center1().x(), position.y + capsule.center1().y());
		b2Pos p2 = b2Pos.calloc(arena).set(position.x + capsule.center2().x(), position.y + capsule.center2().y());
                
                b2Vec2 d = b2SubPos(p1, p2, b2Vec2.calloc(arena));
                float length = b2Length(d);
                if (length < 0.001f) {
                    LOGGER.warning("sample app: capsule too short!");
                    return;
                }

                b2Vec2 axis = b2Vec2.calloc(arena).set(d.x() / length, d.y() / length);
                b2Transform transform = b2Transform.calloc(arena);

                transform.p(b2Lerp(b2ToVec2(p1, b2Vec2.calloc(arena)), b2ToVec2(p2, b2Vec2.calloc(arena)), 0.5f, b2Vec2.calloc(arena)));
                transform.q().c(axis.x());
                transform.q().s(axis.y());

                BatchSnapshot snapshot = physicsProcessor.getSnapshot().get();
                snapshot.drawCapsule(transform.p().x(), transform.p().y(), b2Rot_GetAngle(transform.q()), capsule.radius(), length, color);
            }
        }

        /**
         * Depending on the player's status, an animation will be activated.
         */
        private void applyAnimation() {
            if (onGround) {
                if (Math.abs(velocity.x) > 0) {
                    spatial.getControl(AnimatedSprite2D.class).playAnimation("walk", 10);
                } else {
                    spatial.getControl(AnimatedSprite2D.class).playAnimation("idle", 10);
                }
            } else {
                spatial.getControl(AnimatedSprite2D.class).playAnimation("jump", 10);
            }
        }

        @Override
        public float getRotation() {
            return 0.0f;
        }

        @Override
        public Vector3f getPosition() {
            tmpWorldPosition.set(position.x, position.y, 0f);
            return tmpWorldPosition;
        }
    }

    /*(non-Javadoc)
     */
    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);
        viewPort.setBackgroundColor(
            new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f)
        );

        Camera2DAppSate camera2DAppSate = new Camera2DAppSate(1f);
        camera2DAppSate.setUnitComparator(Vector3f.UNIT_Z, UnitComparator.UType.World, RenderQueue.Bucket.Translucent, RenderQueue.Bucket.Transparent);
        stateManager.attach(camera2DAppSate);

        Box2dAppState box2d = new Box2dAppState(ThreadingType.PARALLEL);
        box2d.setDebugEnabled(true);
        stateManager.attach(box2d);

        InputHandlerAppState inputHandlerAppState = new InputHandlerAppState();
        stateManager.attach(inputHandlerAppState);

        inputHandlerAppState.addInputHandler(VK_LEFT).install();
        inputHandlerAppState.addInputHandler(VK_RIGHT).install();
        inputHandlerAppState.addInputHandler(VK_JUMP).install();

        prepareGround();
        prepareCharacter();
    }

    /**
     * Prepare the character (2D model) and animations.
     */
    @SuppressWarnings("unchecked")
    private void prepareCharacter() {
        Box2dAppState box2dAppState = stateManager.getState(Box2dAppState.class);
        Camera2DAppSate camera2DAppSate = stateManager.getState(Camera2DAppSate.class);
        b2WorldId worldId = box2dAppState.getPhysicsSpace().getWorldId();
        
        Spatial player = assetManager.loadModel("Models/Rabbit.j3o");
        player.getControl(AnimatedSprite2D.class).playAnimation("walk", 10);
        rootNode.attachChild(player);
        
        Player body2D = new Player();
        body2D.setType(b2_kinematicBody);
        body2D.setGravityScale(2f);
        body2D.setPosition(new Vector2f(0, 10));
        box2dAppState.getPhysicsSpace().addBody(body2D);

        {
            b2Capsule capsule = b2Capsule.calloc();
            capsule.radius(0.25f);

            try(ArenaAlloc alloc = allocPush()) {
                capsule.center1(b2Vec2.calloc(alloc).set(0, 0));
                capsule.center2(b2Vec2.calloc(alloc).set(0, 0.25f));
            }
            body2D.setCapsule(capsule);
            player.addControl(body2D);
            camera2DAppSate.setTarget(player);
        }
        
        {
            b2BodyDef bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
            bodyDef.type(b2_dynamicBody);
            bodyDef.position(b2Pos.malloc().set( 7.0f, 7.0f ));
            b2BodyId bodyId = b2CreateBody( worldId, bodyDef, b2BodyId.malloc() );

            b2ShapeDef shapeDef = b2DefaultShapeDef(b2ShapeDef.malloc());
            shapeDef.filter(b2Filter.malloc().categoryBits(Player.DebrisBit).maskBits(Player.AllBits).groupIndex(0) );
            shapeDef.material().restitution(0.7f);
            shapeDef.material().rollingResistance(0.2f);

            b2Circle circle = b2Circle.malloc().set( b2Vec2_zero, 0.3f );
            b2ShapeId m_ballId = b2CreateCircleShape( bodyId, shapeDef, circle, b2ShapeId.malloc() );
        }
    }

    private void prepareGround() {
        Box2dAppState box2dAppState = stateManager.getState(Box2dAppState.class);
        b2WorldId worldId = box2dAppState.getPhysicsSpace().getWorldId();
        
        b2BodyId groundId1;
        {
            b2BodyDef bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
            bodyDef.position(b2Pos.malloc().set( 0.0f, 0.0f ));
            groundId1 = b2CreateBody( worldId, bodyDef, b2BodyId.malloc() );

            String path =
                    """
                    M 2.6458333,201.08333 H 293.68751 v -47.625 h -2.64584 l -10.58333,7.9375 -13.22916,7.9375 -13.24648,5.29167 
                    -31.73269,7.9375 -21.16667,2.64583 -23.8125,10.58333 H 142.875 v -5.29167 h -5.29166 v 5.29167 H 119.0625 v 
                    -2.64583 h -2.64583 v -2.64584 h -2.64584 v -2.64583 H 111.125 v -2.64583 H 84.666668 v -2.64583 h -5.291666 v 
                    -2.64584 h -5.291667 v -2.64583 H 68.791668 V 174.625 h -5.291666 v -2.64584 H 52.916669 L 39.6875,177.27083 H 
                    34.395833 L 23.8125,185.20833 H 15.875 L 5.2916669,187.85416 V 153.45833 H 2.6458333 v 47.625
                    """;

            b2Vec2.Buffer points = b2Vec2.malloc(64);

            b2Vec2 offset = b2Vec2.malloc().set( -50.0f, -200.0f );
            float scale = 0.2f;

            int count = ParsePath.parse( path, offset, points, 64, scale, false );

            b2ChainDef chainDef = b2DefaultChainDef(b2ChainDef.malloc());
            chainDef.points(points);
            chainDef.count(count);
            chainDef.isLoop(true);

            b2CreateChain( groundId1, chainDef, b2ChainId.malloc() );
        }

        b2BodyId groundId2;
        {
            b2BodyDef bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
            bodyDef.position(b2Pos.malloc().set( 98.0f, 0.0f ));
            groundId2 = b2CreateBody( worldId, bodyDef, b2BodyId.malloc() );

            String path = """
                    M 2.6458333,201.08333 H 293.68751 l 0,-23.8125 h -23.8125 l 21.16667,21.16667 h -23.8125 l -39.68751,-13.22917 
                    -26.45833,7.9375 -23.8125,2.64583 h -13.22917 l -0.0575,2.64584 h -5.29166 v -2.64583 l -7.86855,-1e-5 
                    -0.0114,-2.64583 h -2.64583 l -2.64583,2.64584 h -7.9375 l -2.64584,2.64583 -2.58891,-2.64584 h -13.28609 v 
                    -2.64583 h -2.64583 v -2.64584 l -5.29167,1e-5 v -2.64583 h -2.64583 v -2.64583 l -5.29167,-1e-5 v -2.64583 h 
                    -2.64583 v -2.64584 h -5.291667 v -2.64583 H 92.60417 V 174.625 h -5.291667 v -2.64584 l -34.395835,1e-5 
                    -7.9375,-2.64584 -7.9375,-2.64583 -5.291667,-5.29167 H 21.166667 L 13.229167,158.75 5.2916668,153.45833 H 
                    2.6458334 l -10e-8,47.625
                    """;

            b2Vec2.Buffer points = b2Vec2.malloc(64);

            b2Vec2 offset = b2Vec2.malloc().set( 0.0f, -200.0f );
            float scale = 0.2f;

            int count = ParsePath.parse(path, offset, points, 64, scale, false );

            b2ChainDef chainDef = b2DefaultChainDef(b2ChainDef.malloc());
            chainDef.points(points);
            chainDef.count(count);
            chainDef.isLoop(true);

            b2CreateChain( groundId2, chainDef, b2ChainId.malloc() );
        }

        {
            b2Polygon box = b2MakeBox( 0.5f, 0.125f, b2Polygon.malloc() );

            b2ShapeDef shapeDef = b2DefaultShapeDef(b2ShapeDef.malloc());

            b2RevoluteJointDef jointDef = b2DefaultRevoluteJointDef(b2RevoluteJointDef.malloc());
            jointDef.maxMotorTorque(10.0f);
            jointDef.enableMotor(true);
            jointDef.hertz(3.0f);
            jointDef.dampingRatio(0.8f);
            jointDef.enableSpring(true);

            float xBase = 48.7f;
            float yBase = 9.2f;
            int count = 50;
            b2BodyId prevBodyId = groundId1;
            for ( int i = 0; i < count; ++i )
            {
                b2BodyDef bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
                bodyDef.type(b2_dynamicBody);
                bodyDef.position(b2Pos.malloc().set( xBase + 0.5f + 1.0f * i, yBase ));
                bodyDef.angularDamping(0.2f);
                b2BodyId bodyId = b2CreateBody( worldId, bodyDef, b2BodyId.malloc() );
                b2CreatePolygonShape( bodyId, shapeDef, box, b2ShapeId.malloc() );

                b2Pos pivot = b2Pos.malloc().set( xBase + 1.0f * i, yBase );
                jointDef.base().bodyIdA(prevBodyId);
                jointDef.base().bodyIdB(bodyId);
                jointDef.base().localFrameA().p( b2Body_GetLocalPoint( jointDef.base().bodyIdA(), pivot, b2Vec2.malloc() ) );
                jointDef.base().localFrameB().p( b2Body_GetLocalPoint( jointDef.base().bodyIdB(), pivot, b2Vec2.malloc() ) );
                b2CreateRevoluteJoint( worldId, jointDef, b2JointId.malloc() );

                prevBodyId = bodyId;
            }

            b2Pos pivot = b2Pos.malloc().set( xBase + 1.0f * count, yBase );
            jointDef.base().bodyIdA(prevBodyId);
            jointDef.base().bodyIdB(groundId2);
            jointDef.base().localFrameA().p(b2Body_GetLocalPoint( jointDef.base().bodyIdA(), pivot, b2Vec2.malloc() ));
            jointDef.base().localFrameB().p(b2Body_GetLocalPoint( jointDef.base().bodyIdB(), pivot, b2Vec2.malloc() ));
            b2CreateRevoluteJoint( worldId, jointDef, b2JointId.malloc() );
        }
    }
}
