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

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.util.TempVars;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.box2d.jni.b2BodyDef;
import org.box2d.jni.b2BodyId;
import org.box2d.jni.b2BodyType;
import org.box2d.jni.b2Capsule;
import org.box2d.jni.b2Circle;
import org.box2d.jni.b2Polygon;
import org.box2d.jni.b2Pos;
import org.box2d.jni.b2Rot;
import org.box2d.jni.b2ShapeDef;
import org.box2d.jni.b2ShapeId;

import org.box2d.jni.system.ArenaAlloc;

import static org.box2d.jni.include.Box2d.*;
import static org.box2d.jni.include.Id.*;
import static org.box2d.jni.include.MathFunctions.*;
import static org.box2d.jni.include.Types.*;
import static org.box2d.jni.system.ArenaAlloc.*;

import org.je3gl.box2d.AxisType;
import org.je3gl.box2d.PhysicsSpace;
import org.je3gl.box2d.listener.SpaceListener;
import org.je3gl.box2d.util.Converter;
import org.je3gl.utilities.TransformUtilities;

/**
 * An abstract implementation of the {@link PhysicsControl} interface.
 * <p>
 * An object of the <code>PhysicsBody2D</code> class is the body that the
 * physics engine uses to give realism to the games. With this control we can
 * manage a 2D model with or without physics.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public abstract class PhysicsBody2D extends AbstractControl implements PhysicsControl, AutoCloseable {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(PhysicsBody2D.class.getName());
    /** List of physical space listeners. */
    private final List<SpaceListener> spaceListeners = new ArrayList<>();
    
    /**
     * temporary storage during calculations 'Quaternion'
     */
    private final Quaternion tmpInverseWorldRotation = new Quaternion();
    /**
     * temporary storage during calculations 'Vector3f'
     */
    private final Vector3f tmpWorldPosition = new Vector3f();
    
    /** Physical space. */
    protected PhysicsSpace physicsSpace;
    
    /**
     * A flag that is responsible for managing the states of the bodies, so it 
     * is only used to indicate if the bodies have been initialized correctly
     * to call method <code>postReady(void)</code>.
     */
    private boolean initialized;
    
    /**
     * true &rarr; physics-space coordinates match local transform, false &rarr;
     * physics-space coordinates match world transform
     */
    private boolean localPhysics = false;
    /** the body id */
    protected b2BodyId bodyId;

    /**
     * This refers to the definition of the body prior to its creation in
     * physical space; these properties are used only when the body has not yet
     * been initialized or created.
     */
    protected b2BodyDef bodyDef;

    /**
     * Generates a new object of class <code>PhysicsBody2D</code> to generate a 
     * physical body from a 2D model.
     *
     */
    public PhysicsBody2D() {
        this.bodyDef = b2DefaultBodyDef(b2BodyDef.malloc());
        this.bodyId  = b2BodyId.malloc();
        this.bodyId.clear();
    }

    public b2ShapeId addPolygonShape(b2ShapeDef shapeDef, b2Polygon shape) {
        if (isValid()) {
            return b2CreatePolygonShape(bodyId, shapeDef, shape, b2ShapeId.malloc());
        }
        LOGGER.log(Level.WARNING, "First, add the body to the physical space to create the physical shape.");
        return null;
    }

    public b2ShapeId addCircleShape(b2ShapeDef shapeDef, b2Circle shape) {
        if (isValid()) {
            return b2CreateCircleShape(bodyId, shapeDef, shape, b2ShapeId.malloc());
        }
        LOGGER.log(Level.WARNING, "First, add the body to the physical space to create the physical shape.");
        return null;
    }
    
    public b2ShapeId addCapsuleShape(b2ShapeDef shapeDef, b2Capsule shape) {
        if (isValid()) {
            return b2CreateCapsuleShape(bodyId, shapeDef, shape, b2ShapeId.calloc());
        }
        LOGGER.log(Level.WARNING, "First, add the body to the physical space to create the physical shape.");
        return null;
    }
    
    public void setType(b2BodyType bodyType) {
        if (isValid()) {
            b2Body_SetType(bodyId, bodyType);
        } else {
            bodyDef.type(bodyType);
        }
    }
    
    public void setPosition(Vector2f position) {
        try(ArenaAlloc alloc = allocPush()) {
            b2Pos pos = Converter.toB2Pos(position, b2Pos.calloc(alloc));
            if (isValid()) {
                b2Body_SetTransform(bodyId, pos, b2Body_GetRotation(bodyId, b2Rot.calloc(alloc)));
            } else {
                bodyDef.position(pos);
            }
        }
    }
    
    public void setGravityScale(float scale) {
        if (isValid()) {
            b2Body_SetGravityScale(bodyId, scale);
        } else {
            bodyDef.gravityScale(scale);
        }
    }
    
    /**
     * Check if the body is valid.
     *
     * @return boolean
     */
    public boolean isValid() {
        if (bodyId == null) {
            return false;
        }
        return B2_IS_NON_NULL(bodyId);
    }

    /**
     * Returns body rotation.
     *
     * @return float
     */
    public float getRotation() {
        try (ArenaAlloc alloc = allocPush()) {
            if (isValid()) {
                b2Rot rot = b2Body_GetRotation(bodyId, b2Rot.calloc(alloc));
                return b2Rot_GetAngle(rot);
            }
            return b2Rot_GetAngle(bodyDef.rotation());
        }
    }

    /**
     * Devuelve la posisción del cuerpo
     *
     * @return Vector3f
     */
    public Vector3f getPosition() {
        AxisType axisType = physicsSpace == null
                ? AxisType.AXIS_XYO : physicsSpace.getAxisType();

        try (ArenaAlloc alloc = allocPush()) {
            if (isValid()) {
                b2Pos position = b2Body_GetPosition(bodyId, b2Pos.calloc(alloc));
                return Converter.toVector3f(position, axisType, tmpWorldPosition);
            }
            return Converter.toVector3f(bodyDef.position(), axisType, tmpWorldPosition);
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString() 
     */
    @Override
    public String toString() {
        return "(" + String.valueOf(spatial) + ") " + bodyId;
    }

    /**
     * Add a new listener for the physical space.
     *
     * @param listener a listener
     */
    public void addSpaceListener(SpaceListener listener) {
        this.spaceListeners.add(listener);
    }

    /**
     * Delete a listener for physical space.
     *
     * @param listener the listener
     * @return boolean
     */
    public boolean removeSpaceListener(SpaceListener listener) {
        return this.spaceListeners.remove(listener);
    }

    /**
     * Method responsible for activating listeners, where it will notify the
     * state of this body (in physical space)
     *
     * @param physicsSpace the current physical space
     * @param attach Flag indicating whether the body is being removed or added
     * (from physical space).
     */
    protected final void fireSpaceListener(PhysicsSpace physicsSpace, boolean attach) {
        for (int i = 0; i < this.spaceListeners.size(); i++) {
            SpaceListener sl = this.spaceListeners.get(i);
            if (sl != null) {
                if (attach) {
                    sl.spaceAttached(physicsSpace);
                } else {
                    sl.spaceDetached(physicsSpace);
                }
            }
        }
    }

    /**
     * Returns the translation of the spatial.
     * @return Vector3f
     */
    protected Vector3f getSpatialTranslation() {
        Vector3f result;
         if (localPhysics) {
            result = spatial.getLocalTranslation(); // alias
        } else {
            result = spatial.getWorldTranslation(); // alias
        }
        return result;
    }
    
    /**
     * Returns spatial rotation
     * @return Quaternion
     */
    protected Quaternion getSpatialRotation() {
        Quaternion result; 
        if (localPhysics) {
            result = spatial.getLocalRotation(); // alias
        } else {
            result = spatial.getWorldRotation(); // alias
        }

        return result;
    }

    /**
     * Returns the physics state, <code>true</code> if the physics is applied
     * with local coordinates; otherwise <code>false</code> if world coordinates
     * are used.
     *
     * @return boolean
     */
    public boolean isLocalPhysics() {
        return localPhysics;
    }

    /**
     * Set the behavior.
     *
     * @param localPhysics <code>true</code> if the physics is applied with
     * local coordinates; otherwise <code>false</code> if world coordinates are
     * used.
     */
    public void setLocalPhysics(boolean localPhysics) {
        this.localPhysics = localPhysics;
    }

    /**
     * Method responsible for applying the transformation of physical control to
     * the body (2D model).
     * 
     * @param physicsLocation physical control position
     * @param physicsOrientation physical control rotation
     */
    protected void applyPhysicsTransform(Vector3f physicsLocation, Quaternion physicsOrientation) {
        if (isEnabled() && spatial != null) {
            Vector3f localLocation = spatial.getLocalTranslation();
            Quaternion localRotationQuat = spatial.getLocalRotation();
                        
            if (!localPhysics && spatial.getParent() != null) {
                localLocation
                        .set(physicsLocation)
                        .subtractLocal(
                                spatial.getParent()
                                        .getWorldTranslation());
                localLocation.divideLocal(
                        spatial.getParent().getWorldScale());
                tmpInverseWorldRotation
                        .set(spatial.getParent().getWorldRotation())
                        .inverseLocal();
                TransformUtilities.rotate(
                        tmpInverseWorldRotation, localLocation, localLocation);
                localRotationQuat.set(physicsOrientation);
                tmpInverseWorldRotation
                        .set(spatial.getParent().getWorldRotation())
                        .inverseLocal()
                        .mult(localRotationQuat, localRotationQuat);

                spatial.setLocalTranslation(localLocation);
                spatial.setLocalRotation(localRotationQuat);
            } else {
                spatial.setLocalTranslation(physicsLocation);
                spatial.setLocalRotation(physicsOrientation);
            }
        }
    }

    /**
     * Method responsible for applying coordinates based on information from the
     * physical body, this includes rotation and location.
     */
    private void checkAppliedPhysicalTransformation() {
        AxisType axisType = physicsSpace == null
                ? null : physicsSpace.getAxisType();

        TempVars temp = TempVars.get();
        if (axisType == null) {
            axisType = AxisType.getDefault();
        }

        Quaternion rotation = temp.quat1;
        rotation.fromAngleAxis(
                getRotation() * axisType.getMultiplier(),
                axisType.getUnit()
        );

        Vector3f locDeep = spatial.getLocalTranslation().mult(axisType.getUnit());
        Vector3f location = getPosition();
        location.add(locDeep);

        applyPhysicsTransform(location, rotation);
        temp.release();
    }

    /**
     * Release this physical body from the scene as well as from the physical
     * space.
     */
    public void queueFree() {
        if (spatial.removeFromParent()) {
            physicsSpace.removeBody(this);
        }
    }
    
    /*(non-Javadoc) 
     */
    @Override
    public void setPhysicsSpace(PhysicsSpace physicsSpace) {
        if (this.physicsSpace != null && physicsSpace != null && this.physicsSpace != physicsSpace) {
            throw new IllegalStateException("This body has already been added to a physical space.");
        }
        if (physicsSpace == null && this.physicsSpace != null) {
            fireSpaceListener(this.physicsSpace, false);
            bodyId.clear();
        } else {
            fireSpaceListener(physicsSpace, true);
        }
        this.physicsSpace = physicsSpace;
    }

    /* (non-Javadoc)
     */
    @Override
    public PhysicsSpace getPhysicsSpace() {
        return physicsSpace;
    }

    /* (non-Javadoc)
     * @deprecated (?,?)
     */
    @Override
    @Deprecated
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /* (non-Javadoc)
     */
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        checkAppliedPhysicalTransformation();
        this.ready();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (!initialized) {
            initialized = true;
            this.postReady();
        }
        
        checkAppliedPhysicalTransformation();
        physicsProcess(tpf);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // nothing
    }
    
    /**
     * Returns the {@link com.jme3.scene.Spatial} assigned to this physical body.
     * @param <T> spatial type
     * @return A {@link com.jme3.scene.Spatial} object
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Spatial> T getJmeObject() {
        return (T) spatial;
    }
    
    /**
     * It is used to initialize data when it is certain that the body is in sync
     * with the model.
     */
    protected void postReady() {};

    /** Data initialization for this body (optional). */
    protected void ready() {}
    
    /**
     * Updating physical processes (optional).
     * @param delta time per frame (in seconds)
     */
    protected void physicsProcess(float delta) {}

    /**
     * Returns the body id.
     *
     * @return b2BodyId
     */
    public b2BodyId getBodyId() {
        return bodyId;
    }

    /**
     * Returns the body definition.
     *
     * @return b2BodyDef
     */
    public b2BodyDef getBodyDef() {
        return bodyDef;
    }

    /* (non-Javadoc)
     */
    @Override
    public void close() {
        if (bodyId != null) {
            bodyId.close();
            bodyId = null;
        }
        if (bodyDef != null) {
            bodyDef.close();
            bodyId = null;
        }
    }
}
