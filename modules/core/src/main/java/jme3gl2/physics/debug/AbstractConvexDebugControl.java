/* Copyright (c) 2009-2024 jMonkeyEngine.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jme3gl2.physics.debug;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;

import jme3gl2.physics.control.PhysicsBody2D;
import jme3gl2.scene.debug.custom.DebugGraphics;
import static jme3gl2.physics.debug.Dyn4jDebugGraphics.*;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Slice;
import org.dyn4j.geometry.Vector2;

/**
 * Class <code>AbstractConvexDebugControl</code> in charge of controlling the
 * position and rotation of a physical form.
 * <p>
 * <b>Dyn4j</b> has the freedom to transfer a form <code>Convex</code>
 * to a specific point of a body, since it is relative to the physical body it
 * has to be controlled separately.
 * <p>
 * The following shapes, their positions and rotations are peculiar and therefore
 * have a class dedicated to obtain these coordinates:
 * <ul>
 * <li>Capsule</li>
 * <li>Circle</li>
 * <li>Ellipse</li>
 * <li>HalfEllipse</li>
 * <li>Slice</li>
 * </ul>
 * <p>
 * On the other hand, a form <code>Polygon</code> or that implements the interface
 * <code>Wound</code> has no need to have a control since its points/vertices are
 * modified by moving or rotating them in its shape.
 * @param <E> body type
 * 
 * @author wil
 * @version 1.5.5
 * @since 2.5.0
 */
public abstract class AbstractConvexDebugControl<E extends Convex> extends AbstractPhysicsDebugControl<PhysicsBody2D>  {
    
    /**
     * Internal class <code>RectangleDebugControl</code> responsible for managing
     * the coordinates of the form <code>Rectangle</code>.
     */
    static final class RectangleDebugControl extends AbstractConvexDebugControl<Rectangle> {
        /**
         * Class constructor of <code>RectangleDebugControl</code>.
         * @param dyn4jDebugAppState debugger
         * @param shape physical form
         * @param body physical body
         */        
        RectangleDebugControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, BodyFixture shape, PhysicsBody2D body) {
            super(dyn4jDebugAppState, shape, body);
        }
        
        /* (non-Javadoc) */
        @Override
        protected double getRotationAngle() { return getShape().getRotationAngle(); }        
        /* (non-Javadoc) */
        @Override
        protected Vector2 getLocaTranslation() { return getShape().getCenter(); }        
    }

    /**
     * Internal class <code>CapsuleDebugControl</code> responsible for managing
     * the coordinates of the form <code>Capsule</code>.
     */
    static final class CapsuleDebugControl extends AbstractConvexDebugControl<Capsule> {
        
        /**
         * Class constructor of <code>CapsuleDebugControl</code>.
         * @param dyn4jDebugAppState debugger
         * @param shape physical form
         * @param body physical body
         */
        CapsuleDebugControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, BodyFixture shape, PhysicsBody2D body) {
            super(dyn4jDebugAppState, shape, body);
        }
        
        /* (non-Javadoc) */
        @Override
        protected double getRotationAngle() { return this.getShape().getRotationAngle(); }        
        /* (non-Javadoc) */
        @Override
        protected Vector2 getLocaTranslation() { return this.getShape().getCenter(); }        
    }

    /**
     * Internal class <code>CircleDebugControl</code> responsible for managing
     * the coordinates of the form <code>Circle</code>.
     */
    static final class CircleDebugControl extends AbstractConvexDebugControl<Circle> {
        
        /**
         * Class constructor of <code>CircleDebugControl</code>.
         * @param dyn4jDebugAppState debugger
         * @param shape physical form
         * @param body physical body
         */
        CircleDebugControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, BodyFixture shape, PhysicsBody2D body) {
            super(dyn4jDebugAppState, shape, body);
        }
        
        /* (non-JavaDoc) */
        @Override
        protected double getRotationAngle() { return 0; }        
        /* (non-JavaDoc) */
        @Override
        protected Vector2 getLocaTranslation() { return getShape().getCenter(); }        
    }
    
    /**
     * Internal class <code>EllipseDebugControl</code> responsible for managing
     * the coordinates of the form <code>Ellipse</code>.
     */
    static final class EllipseDebugControl extends AbstractConvexDebugControl<Ellipse> {
        
        /**
         * Class constructor of <code>EllipseDebugControl</code>.
         * @param dyn4jDebugAppState debugger
         * @param shape physical form
         * @param body physical body
         */
        EllipseDebugControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, BodyFixture shape, PhysicsBody2D body) {
            super(dyn4jDebugAppState, shape, body);
        }
        
        /* (non-Javadoc) */
        @Override
        protected double getRotationAngle() { return getShape().getRotationAngle(); }        
        /* (non-Javadoc) */
        @Override
        protected Vector2 getLocaTranslation() { return getShape().getCenter(); }        
    }
    
    /**
     * Internal class <code>HalfEllipseDebugControl</code> responsible for
     * managing the coordinates of the form <code>HalfEllipse</code>.
     */
    static final class HalfEllipseDebugControl extends AbstractConvexDebugControl<HalfEllipse> {
        
        /**
         * Class constructor of <code>HalfEllipseDebugControl</code>.
         * @param dyn4jDebugAppState debugger
         * @param shape physical form
         * @param body physical body
         */
        HalfEllipseDebugControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, BodyFixture shape, PhysicsBody2D body) {
            super(dyn4jDebugAppState, shape, body);
        }
        
        /* (non-Javadoc) */
        @Override
        protected double getRotationAngle() { return getShape().getRotationAngle(); }
        /* (non-Javadoc) */
        @Override
        protected Vector2 getLocaTranslation() {  return getShape().getEllipseCenter(); }        
    }
    
    /**
     * Internal class for any convex shape.
     */
    static final class ConvexDebugControl extends AbstractConvexDebugControl<Convex> {

        /** Default position. */
        private final Vector2 v = new Vector2();
        
        /**
         * Class constructor.
         * @param dyn4jDebugAppState debugger
         * @param shape physical form
         * @param body physical body
         */
        ConvexDebugControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, BodyFixture shape, PhysicsBody2D body) {
            super(dyn4jDebugAppState, shape, body);
        }

        /* (non-Javadoc) */
        @Override
        protected double getRotationAngle() { return 0; }
        /* non-Javadoc) */
        @Override
        protected Vector2 getLocaTranslation() { return v; }
    }
    
    /**
     * Internal class <code>SliceDebugControl</code> responsible for managing
     * the coordinates of the form <code>Slice</code>.
     */
    static final class SliceDebugControl extends AbstractConvexDebugControl<Slice> {
        /**
         * Class constructor of <code>SliceDebugControl</code>.
         * @param dyn4jDebugAppState debugger
         * @param shape physical form
         * @param body physical body
         */        
        SliceDebugControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, BodyFixture shape, PhysicsBody2D body) {
            super(dyn4jDebugAppState, shape, body);
        }
        
        /* (non-Javadoc) */
        @Override
        protected double getRotationAngle() { return getShape().getRotationAngle(); }        
        /* (non-Javadoc) */
        @Override
        protected Vector2 getLocaTranslation() { return getShape().getCircleCenter(); }        
    }
    
    /** Physical form of the object. */
    final BodyFixture fixture;
    /** Physical bodies. */
    final PhysicsBody2D body2D;
    
    /**
     * Private class constructor <code>AbstractConvexDebugControl</code>.
     * @param dyn4jDebugAppState debugger
     * @param fixture physical form
     * @param body2D physical body
     */
    private AbstractConvexDebugControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, BodyFixture fixture, PhysicsBody2D body2D) {
        super(dyn4jDebugAppState);
        this.fixture = fixture;
        this.body2D = body2D;
    }
    
    /**
     * Returns physical shape.
     * @return Convex
     */
    @SuppressWarnings("unchecked")
    public E getShape() {
        return (E) fixture.getShape();
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.scene.control.AbstractControl#controlUpdate(float) 
     * @param tpf float
     */
    @Override
    protected void controlUpdate(float tpf) {
        applyPhysicsLocation(body2D);
        applyPhysicsRotation(body2D);
        renderMaterial();
    }
    
    /**
     * Renders the object; manages the geometry material.
     */
    void renderMaterial() {
        DebugGraphics dg = dyn4jDebugAppState.getGraphics2DRenderer().getDebugGraphics();
        ColorRGBA color/*= ColorRGBA.White*/;
        
        if (!(body2D.isEnabled())) {
            color = dg.getColor(GL_DEBUG_DISABLED);
        } else {
            if (fixture.isSensor()) {
                color = dg.getColor(GL_DEBUG_SENSOR);
            } else {
                if (body2D.isBullet()) {
                    color = dg.getColor(GL_DEBUG_BULLET);
                } else if (body2D.isStatic()) {
                    color = dg.getColor(GL_DEBUG_STATIC);
                } else if (body2D.isKinematic()) {
                    color = dg.getColor(GL_DEBUG_KINEMATIC);
                } else if (body2D.isAtRest()) {
                    color = dg.getColor(GL_DEBUG_AT_RESET);
                } else {
                    color = dg.getColor(GL_DEBUG_DEFAULT);
                }
            }
        }
        
        Material mat = ((Geometry) spatial).getMaterial();   
        mat.setColor("Color", color);
    }
    
    /**
     * Method in charge of returning the rotation angle of the shape.
     * @return angle
     */
    protected abstract double getRotationAngle();
    
    /**
     * Method in charge of returning the local position of the form.
     * @return position
     */
    protected abstract Vector2 getLocaTranslation();
}
