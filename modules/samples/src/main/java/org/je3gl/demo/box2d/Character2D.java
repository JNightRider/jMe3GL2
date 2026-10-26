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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;

import java.util.logging.Logger;

import org.je3gl.box2d.Box2dAppState;
import org.je3gl.box2d.ThreadingType;
import org.je3gl.renderer.Camera2DAppSate;
import org.je3gl.renderer.UnitComparator;

import org.box2d.jni.*;
import org.box2d.jni.system.*;

import static org.box2d.jni.b2BodyType.*;
import static org.box2d.jni.include.Box2d.*;
import static org.box2d.jni.include.Collision.*;
import static org.box2d.jni.include.Id.*;
import static org.box2d.jni.include.MathTypes.*;
import static org.box2d.jni.include.Types.*;
import static org.box2d.jni.libc.LibCMath.*;
import static org.box2d.jni.system.ArenaAlloc.*;

import org.je3gl.box2d.control.CharacterBody2D;
import org.je3gl.box2d.control.KinematicBody2D;
import org.je3gl.box2d.control.mover.MoverBounds;
import org.je3gl.box2d.util.MathUtils;
import org.je3gl.box2d.util.ParsePath;
import org.je3gl.jawt.JAWTResolution;
import org.je3gl.jawt.Jme3GL2DefaultDisplaySystem;
import org.je3gl.jawt.Jme3GL2DisplaySystem;
import org.je3gl.plugins.input.BooleanStateKeyboardInputHandler;
import org.je3gl.plugins.input.InputHandlerAppState;
import org.je3gl.plugins.input.Key;
import org.je3gl.scene.control.AnimatedSprite2D;
import org.je3gl.scene.shape.Sprite;

import static org.je3gl.utilities.MaterialUtilities.*;

/**
 * Class where a small platform game is exemplified and how it can be controlled 
 * using the jMe3GL2 library.
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class Character2D extends SimpleApplication  {

    /**
     * {@code True} if in full-screen mode; otherwise, the application will run
     * in windowed mode.
     */
    private static final boolean FULL_SCREEN = false;

    /**
     * The main method; uses zero arguments in args array
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Character2D app = new Character2D();
        AppSettings settings = new AppSettings(true);
        settings.setGammaCorrection(false);

        // Get the resolution of the user's current screen.
        Jme3GL2DisplaySystem displaySystem = Jme3GL2DefaultDisplaySystem.getDisplaySystem();
        JAWTResolution resolution = FULL_SCREEN 
                ? displaySystem.getFullScreenResolution()
                : JAWTResolution.TEST_RESOLUTION;

        settings.setFullscreen(displaySystem.isFullScreenSupported() && FULL_SCREEN);
        settings.setResolution(resolution.getWidth(), resolution.getHeight());

        app.setSettings(settings);
        app.start();
    }

    /** Object in charge of managing the 'A' key */
    private static final BooleanStateKeyboardInputHandler VK_LEFT = new BooleanStateKeyboardInputHandler(new Key(KeyInput.KEY_A, "left"));
    /** Object in charge of managing the 'D' key */
    private static final BooleanStateKeyboardInputHandler VK_RIGHT = new BooleanStateKeyboardInputHandler(new Key(KeyInput.KEY_D, "right"));
    /** Object in charge of managing the 'SPACE' key */
    private static final BooleanStateKeyboardInputHandler VK_JUMP = new BooleanStateKeyboardInputHandler(new Key(KeyInput.KEY_SPACE, "jump"));

    /** The base of the {@link Elevator} */
    private static final b2Vec2 elevatorBase = b2Vec2.malloc().set(112.0f, 10.0f);
    /** elevator-amplitude */
    private static final float elevatorAmplitude = 4.0f;

    /**
     * Object masks (filters) for distinguishing one physical body from another.
     */
    public static final long
                StaticBit   = 0x0001,
                MoverBit    = 0x0002,
                DynamicBit  = 0x0004,
                AllBits     = ~0;
 
    /**
     * A platform that floats.
     */
    private static class Elevator extends KinematicBody2D {
        /**  accumulated time. */
        private float time;

        /** Constructor. */
        public Elevator() {
        }

        /**
         * Update.
         *
         * @param delta float
         */
        @Override
        protected void physicsProcess(float delta) {
            if (B2_IS_NON_NULL(bodyId)) {
                try (ArenaAlloc arena = allocPush()) {
                    b2Pos point = b2Pos.calloc(arena)
                            .x(elevatorBase.x())
                            .y(elevatorAmplitude * cosf(1.0f * time + B2_PI) + elevatorBase.y().doubleValue());
                    boolean wake = true;
                    b2Body_SetTargetTransform(bodyId, b2WorldTransform.malloc().set(point, b2Rot_identity), delta, wake);
                }
            }
            time += delta;
        }
    }

    /**
     * Control of the character (player) in the scene.
     */
    private static class Player extends CharacterBody2D {
        /** Constructor. */
        public Player() {
        }

        /**
         * (non-Javadoc)
         *
         * @param delta float
         */
        @Override
        protected void physicsProcess(float delta) {
            applyAnimation();
        }

        /**
         * (non-Javadoc)
         *
         * @param delta float
         */
        @Override
        protected void physicsParallel(float delta) {
            applyControls(delta);
        }

        /**
         * Apply controls to activate different player actions.
         *
         * @param delta float
         */
        private void applyControls(float delta) {
            Sprite sprite = (Sprite) ((Geometry) spatial).getMesh();

            float throttle = 0.0f;
            if (VK_LEFT.isActive()) {
                throttle -= 1.0f;
                sprite.flipH(true);
            }

            if (VK_RIGHT.isActive()) {
                throttle += 1.0f;
                sprite.flipH(false);
            }

            if (VK_JUMP.isActiveButNotHandled()) {
                VK_JUMP.setHasBeenHandled(true);
                applyJump();
            }
            applyMoveAndUpdate(delta, throttle);
        }

        /**
         * Depending on the player's status, an animation will be activated.
         */
        private void applyAnimation() {
            if (isOnGround()) {
                if (VK_LEFT.isActive() || VK_RIGHT.isActive()) {
                    spatial.getControl(AnimatedSprite2D.class).playAnimation("walk", 10);
                } else {
                    spatial.getControl(AnimatedSprite2D.class).playAnimation("idle", 10);
                }
            } else {
                spatial.getControl(AnimatedSprite2D.class).playAnimation("jump", 10);
            }
        }
    }

    /** The player. */
    private Player body2D;
    /** A mobile platform. */
    private Elevator elevator;

    /*(non-Javadoc)
     */
    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);
        viewPort.setBackgroundColor(
                new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f)
        );

        Camera2DAppSate camera2DAppSate = new Camera2DAppSate(2f);
        camera2DAppSate.setUnitComparator(Vector3f.UNIT_Z, UnitComparator.UType.World, RenderQueue.Bucket.Translucent, RenderQueue.Bucket.Transparent);
        stateManager.attach(camera2DAppSate);

        Box2dAppState box2d = new Box2dAppState(ThreadingType.PARALLEL);
        box2d.setDebugEnabled(true);
        box2d.getDrawSettings()
                .drawAnchorA(true)
                .drawBodyNames(true)
                .drawBounds(true)
                .drawChainNormals(true)
                .drawContactFeatures(true)
                .drawContactNormals(true)
                .drawContacts(true)
                .drawFrictionForces(true)
                .drawGraphColors(true)
                .drawIslands(true)
                .drawJointExtras(true)
                .drawJoints(true)
                .drawMass(true)
                .drawShapes(true);
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
    private void prepareCharacter() {
        Box2dAppState box2dAppState = stateManager.getState(Box2dAppState.class);
        Camera2DAppSate camera2DAppSate = stateManager.getState(Camera2DAppSate.class);

        Spatial player = assetManager.loadModel("Models/Rabbit.j3o");
        player.getControl(AnimatedSprite2D.class).playAnimation("walk", 10);
        ((Sprite) ((Geometry) player).getMesh()).applyScale(2.5f);
        rootNode.attachChild(player);
        
        body2D = new Player();
        body2D.addContact(elevator.getBodyId());
        box2dAppState.getPhysicsSpace().addBody(body2D);

        body2D.setType(b2_dynamicBody);
        body2D.setPosition(new Vector2f(0, 8));
        {
            b2Capsule capsule = b2Capsule.calloc();
            capsule.radius(0.5f);

            try (ArenaAlloc alloc = allocPush()) {
                capsule.center1(b2Vec2.calloc(alloc).set(0, -0.4f));
                capsule.center2(b2Vec2.calloc(alloc).set(0, 0.4f));
            }

            b2Filter filter = b2Filter.malloc().set(MoverBit, StaticBit | DynamicBit, 0);

            b2ShapeDef shapeDef = b2DefaultShapeDef(b2ShapeDef.malloc());
            shapeDef.density(1.0f);
            shapeDef.material().friction(0.0f);
            shapeDef.enablePreSolveEvents(true);
            shapeDef.filter(filter);

            body2D.setFilter(filter);
            body2D.setMoverBounds(new MoverBounds(capsule.center1(), capsule.radius()));
            body2D.setPlatformMaterialId(1);
            body2D.addCapsuleShape(shapeDef, capsule);
            player.addControl(body2D);
            camera2DAppSate.setTarget(player);
        }
    }

    /**
     * Prepare a simple terrain.
     */
    private void prepareGround() {
        Box2dAppState box2dAppState = stateManager.getState(Box2dAppState.class);
        b2WorldId worldId = box2dAppState.getPhysicsSpace().getWorldId();

        b2BodyId groundId1;
        {
            b2BodyDef bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
            try (ArenaAlloc arena = allocPush()) {
                bodyDef.name(arena.nUTF("svg"));
            }
            bodyDef.position(b2Pos.malloc().set( 0.0f, 0.0f ));
            groundId1 = b2CreateBody( worldId, bodyDef, b2BodyId.malloc() );

            String path ="""
                    M -34.395834,201.08333 H 293.68751 v -47.625 h -2.64584 l -10.58333,7.9375 -13.22916,7.9375 -13.24648,5.29167 
                    -31.73269,7.9375 -21.16667,2.64583 -23.8125,13.22919 -15.875,2e-5 0,-6.61461 -17.19792,2e-5 v 2.64582 h 
                    -17.19791 v -1.32292 h -3.96875 v -1.32292 h -3.96875 v -1.32292 h -3.96875 v -1.32291 h -3.96875 v -1.32292 h  
                    -3.96875 v -1.32292 h -3.96875 v -1.32291 h -3.968754 v -1.32292 h -3.96875 v -1.32292 h -3.968751 v -1.32291 h  
                    -3.96875 v -1.32292 h -3.96875 v -1.32292 h -3.96875 v -1.32291 h -3.96875 v -1.32292 h -3.96875 v -1.32292 h 
                    -3.968751 v -1.32291 h -3.96875 l -2e-6,-1.32294 H 52.916669 L 39.6875,177.27083 h -5.291667 l 
                    -7.937499,5.29167 H 15.875001 l -47.625002,-50.27083 v -26.45834 h -2.645834 l 10e-7,95.25 
                    """;

            b2Vec2.Buffer points = b2Vec2.malloc(64);

            b2Vec2 offset = b2Vec2.malloc().set( -50.0f, -200.0f );
            float scale = 0.2f;

            int count = ParsePath.parse(path, offset, points, 64, scale, false );

            b2ChainDef chainDef = b2DefaultChainDef(b2ChainDef.malloc());
            chainDef.points(points);
            chainDef.pointCount(count);
            chainDef.isLoop(true);

            b2CreateChain( groundId1, chainDef, b2ChainId.malloc() );
        }

        b2BodyId groundId2;
        {
            b2BodyDef bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
            bodyDef.position(b2Pos.malloc().set( 98.0f, 0.0f ));
            groundId2 = b2CreateBody( worldId, bodyDef, b2BodyId.malloc() );

            String path = """
                    M 2.6458333,201.08333 H 399.52085 v -23.8125 h -23.8125 l 21.16667,18.52084 -232.83335,-1e-5 -0.0575,2.64584 h 
                    -5.29166 v -2.64583 l -7.86855,-1e-5 -0.0114,-2.64583 h -2.64583 l -2.64583,2.64584 h -7.9375 l 
                    -2.64584,2.64583 -2.58891,-2.64584 -10.64031,2e-5 v -2.64583 l -7.9375,0 v -2.64584 l -7.9375,0 v -2.64583 l 
                    -7.9375,0 v -2.64583 l -7.9375,0 v -2.64583 l -7.937501,-1e-5 v -2.64584 l -7.9375,1e-5 v -2.64583 l 
                    -7.937501,-2e-5 V 174.625 l -7.937501,1e-5 v -2.64584 l -5.291669,0 -7.9375,-2.64584 -7.9375,-2.64583 
                    -5.291667,-5.29167 H 21.166667 L 13.229167,158.75 5.2916668,153.45833 H 2.6458334 l -10e-8,47.625
                    """;

            b2Vec2.Buffer points = b2Vec2.malloc(64);

            b2Vec2 offset = b2Vec2.malloc().set( 0.0f, -200.0f );
            float scale = 0.2f;

            int count = ParsePath.parse(path, offset, points, 64, scale, false );

            b2ChainDef chainDef = b2DefaultChainDef(b2ChainDef.malloc());
            chainDef.points(points);
            chainDef.pointCount(count);
            chainDef.isLoop(true);

            b2CreateChain( groundId2, chainDef, b2ChainId.malloc() );
        }

        {
            b2Polygon box = b2MakeBox( 0.55f, 0.125f, b2Polygon.malloc() );
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
                jointDef.base().localFrameA().p(b2Body_GetLocalPoint( jointDef.base().bodyIdA(), pivot, b2Vec2.malloc() ));
                jointDef.base().localFrameB().p(b2Body_GetLocalPoint( jointDef.base().bodyIdB(), pivot, b2Vec2.malloc() ));
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

        for ( int i = 0; i < 5; ++i )
        {
            b2BodyDef bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
            bodyDef.type(b2_dynamicBody);
            bodyDef.position(b2Pos.malloc().set( 12.0f, 7.0f + 0.5f * i ));
            b2BodyId bodyId = b2CreateBody( worldId, bodyDef, b2BodyId.malloc() );

            b2ShapeDef shapeDef = b2DefaultShapeDef(b2ShapeDef.malloc());
            shapeDef.filter(b2Filter.malloc().set( DynamicBit, AllBits, 0 ));
            shapeDef.material().restitution(0.7f);
            shapeDef.material().rollingResistance(0.2f);

            b2Circle circle = b2Circle.malloc().set( b2Vec2_zero, 0.25f );
            b2CreateCircleShape( bodyId, shapeDef, circle, b2ShapeId.malloc() );
        }

        {
            Geometry geom = new Geometry("Elevator", new Sprite(4, 0.1f));
            geom.setMaterial(getUnshadedColorMaterialFromClassPath(assetManager, ColorRGBA.randomColor()));
            rootNode.attachChild(geom);
            
            elevator = new Elevator();
            elevator.setPosition(new Vector2f(elevatorBase.x(), elevatorBase.y() - elevatorAmplitude));
            box2dAppState.getPhysicsSpace()
                         .addBody(elevator);

            b2ShapeDef shapeDef = b2DefaultShapeDef(b2ShapeDef.malloc());
            shapeDef.filter(b2Filter.malloc().set( DynamicBit, AllBits, 0 ));

            b2Polygon box = b2MakeBox( 2.0f, 0.1f, b2Polygon.malloc() );
            elevator.addPolygonShape(shapeDef, box);
            geom.addControl(elevator);
        }

        {
            b2BodyDef bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
            bodyDef.type(b2_dynamicBody);
            bodyDef.position().x(140.0f);
            float a = 0.25f;
            b2Polygon square = b2MakeSquare(a, b2Polygon.malloc());
            b2ShapeDef shapeDef = b2DefaultShapeDef(b2ShapeDef.malloc());
            shapeDef.filter(b2Filter.malloc().set(DynamicBit, AllBits, 0));
            for (int i = 0; i < 10; ++i) {
                bodyDef.position().y((2.0f * i + 1.0f) * a + 1.0f);
                b2BodyId bodyId = b2CreateBody(worldId, bodyDef, b2BodyId.malloc());
                b2CreatePolygonShape(bodyId, shapeDef, square, b2ShapeId.malloc());
            }
        }

        {
            b2BodyDef bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
            bodyDef.position(b2Pos.malloc().set(160.0f, 5.0f));
            bodyDef.type(b2_dynamicBody);
            b2BodyId body = b2CreateBody(worldId, bodyDef, b2BodyId.malloc());

            b2Polygon box = b2MakeBox(0.1f, 4.0f, b2Polygon.malloc());
            b2ShapeDef shapeDef = b2DefaultShapeDef(b2ShapeDef.malloc());
            shapeDef.density(1.0f);
            b2CreatePolygonShape(body, shapeDef, box, b2ShapeId.malloc());

            b2Pos pivot = MathUtils.sum(bodyDef.position(), b2Vec2.malloc().set(0.0f, 4.0f), b2Pos.malloc());
            b2RevoluteJointDef jointDef = b2DefaultRevoluteJointDef(b2RevoluteJointDef.malloc());
            jointDef.base().bodyIdA(groundId2);
            jointDef.base().bodyIdB(body);
            jointDef.base().localFrameA().p(b2Body_GetLocalPoint(jointDef.base().bodyIdA(), pivot, b2Vec2.malloc()));
            jointDef.base().localFrameB().p(b2Body_GetLocalPoint(jointDef.base().bodyIdB(), pivot, b2Vec2.malloc()));
            jointDef.enableMotor(true);
            jointDef.motorSpeed(1.0f);
            jointDef.maxMotorTorque(500.0f);

            b2CreateRevoluteJoint(worldId, jointDef, b2JointId.malloc());
        }
    }
}
