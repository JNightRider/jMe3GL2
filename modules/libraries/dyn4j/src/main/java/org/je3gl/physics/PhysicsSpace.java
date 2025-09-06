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
package org.je3gl.physics;

import com.jme3.scene.Spatial;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.je3gl.physics.control.PhysicsBody2D;
import org.je3gl.physics.control.PhysicsControl;
import org.je3gl.physics.joint.PhysicsJoint;

import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.world.CollisionWorld;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.World;

/**
 * Class in charge of managing a physical space that connects <b>dyn4j</b> with 
 * <b>JME3</b> where physical bodies can be formed.
 * @param <E> of type {@link org.je3gl.physics.control.PhysicsBody2D}
 * @author wil
 * @version 2.0.0
 * @since 1.0.0
 */
public class PhysicsSpace<E extends PhysicsBody2D> extends World<E> {

    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(PhysicsSpace.class.getName());    
    /** physics default speed. */
    private static final float DEFAULT_SPEED = 1.0F;
    
    /**
     * The speed with which updates to the physical world are applied (<b>dyn4j</b>).
     */
    protected float speed = DEFAULT_SPEED;
    
    /**
     * Generates a new instance of the <code>PhysicsSpace</code> class where to 
     * generate a 2D world provided by <b>dyn4j</b>.
     * 
     * @param initialCapacity initial capacity (bodies in the world)
     * @param initialJointCapacity initial joint capability
     * @param bounds limit of the world.
     */
    public PhysicsSpace(final Integer initialCapacity, final Integer initialJointCapacity, final Bounds bounds) {
        super(initialCapacity == null ? CollisionWorld.DEFAULT_INITIAL_BODY_CAPACITY : initialCapacity, 
              initialJointCapacity == null ? PhysicsWorld.DEFAULT_INITIAL_JOINT_CAPACITY : initialJointCapacity);
        if ( bounds != null ) {
            this.setBounds(bounds);
        }
    }
    
    /**
     * Adds a new physical object to the world (physical space)
     * @param body the body
     */
    public void addPhysicsBody(Object body) {
        this.addPhysicsBody(body, false);
    }

    /**
     * Adds a new physical object to the world (physical space)
     * @param body the body
     * @param notifyFirst <code>true</code> if the body is added to the physical
     * space first and this physical space is notified (set) to the control 
     * later, and <code>false</code> if the opposite is true.
     */
    @SuppressWarnings("unchecked")
    public void addPhysicsBody(Object body, boolean notifyFirst) {
        PhysicsBody2D body2D = null;
        if (body instanceof Spatial) {
            body2D = ((Spatial) body).getControl(PhysicsBody2D.class);
        } else if (body instanceof PhysicsBody2D) {
            body2D = (PhysicsBody2D) body;
        }
        
        if (body2D == null) {
            LOGGER.log(Level.WARNING, "Object [{0}] is not a physical body supported by Dyn4j", body);
        } else {
            this.addBody((E) body2D, notifyFirst);
        }
    }
    
    /**
     * Remove a physical body from this space.
     * @param body the body
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public boolean removePhysicsBody(Object body) {
        PhysicsBody2D body2D = null;
        if (body instanceof Spatial) {
            body2D = ((Spatial) body).getControl(PhysicsBody2D.class);
        } else if (body instanceof PhysicsBody2D) {
            body2D = (PhysicsBody2D) body;
        }
        
        if (body2D == null) {
            LOGGER.log(Level.WARNING, "Object [{0}] is not a physical body supported by Dyn4j", body);
            return false;
        } else {
            return this.removeBody((E) body2D);
        }
    }
    
    /**
     * (non-Javadoc)
     * @see org.dyn4j.world.AbstractCollisionWorld#addBody(org.dyn4j.collision.CollisionBody)
     * @param body body
     */
    @Override
    public void addBody(E body) {
        addBody(body, false);
    }
    
    /**
     * Add a new body to the physical space.
     * @param body body
     * @param notifyFirst <code>true</code> if the body is added to the physical
     * space first and this physical space is notified (set) to the control 
     * later, and <code>false</code> if the opposite is true.
     * 
     * @see org.dyn4j.world.AbstractPhysicsWorld#addBody(org.dyn4j.dynamics.PhysicsBody) 
     * @see org.je3gl.physics.control.PhysicsControl#setPhysicsSpace(org.je3gl.physics.PhysicsSpace) 
     * 
     * @since 3.0.0
     */
    @SuppressWarnings("unchecked")
    public void addBody(E body, boolean notifyFirst) {
        if (notifyFirst) {
            super.addBody(body);
            if (body instanceof PhysicsControl) {
                ((PhysicsControl)  body).setPhysicsSpace(this);
            }
        } else {
            if (body instanceof PhysicsControl) {
                ((PhysicsControl)  body).setPhysicsSpace(this);
            }
            super.addBody(body);
        }
    }

    /**
     * (non-Javadoc)
     * @see org.dyn4j.world.PhysicsWorld#removeBody(org.dyn4j.dynamics.PhysicsBody, boolean)
     * @param body body
     * @param notify boolean
     * @return boolean
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean removeBody(E body, boolean notify) {
        boolean b = super.removeBody(body, notify);
        if ( b && (body instanceof PhysicsControl) ) {
            ((PhysicsControl) body).setPhysicsSpace(null);
        }
        return b;
    }
    
    /* (non-Javadoc)
     * @see org.je3gl.physics.PhysicsSpace#addJoint(org.dyn4j.dynamics.joint.Joint) 
     */
    @SuppressWarnings("unchecked")
    public void addPhysicsJoint(Object joint) {
        Joint<E> myJoint = null;
        if (joint instanceof Joint) {
            myJoint = (Joint<E>) joint;
        } else if (joint instanceof PhysicsJoint) {
            myJoint = ((PhysicsJoint<E, ?>) joint).getJoint();
        }
        
        if (myJoint == null) {
            LOGGER.log(Level.WARNING, "Unsupported joint: {0}", joint);
        } else {
            this.addJoint(myJoint);
        }
    }
    
    /* (non-Javadoc)
     * @see org.je3gl.physics.PhysicsSpace#removeJoint(org.dyn4j.dynamics.joint.Joint) 
     */
    @SuppressWarnings("unchecked")
    public boolean removePhysicsJoint(Object joint) {
        Joint<E> myJoint = null;
        if (joint instanceof Joint) {
            myJoint = (Joint<E>) joint;
        } else if (joint instanceof PhysicsJoint) {
            myJoint = ((PhysicsJoint<E, ?>) joint).getJoint();
        }
        
        if (myJoint == null) {
            LOGGER.log(Level.WARNING, "Unsupported joint: {0}", joint);
            return false;
        } else {
            return this.removeJoint(myJoint);
        }
    }
    
    /**
     * Responsible method of updating the physical world.
     * @param elapsedTime float
     */
    public void updateFixed(final float elapsedTime) {
        this.update(elapsedTime);
    }
    
    /**
     * Set a new speed for the physical world
     * @param speed float
     */
    public void setSpeed(final float speed) {
        this.speed = speed;
        this.getSettings().setMinimumAtRestTime(Settings.DEFAULT_STEP_FREQUENCY * speed);
    }
    
    /**
     * Returns the current speed of the world.
     * @return float
     */
    public float getSpeed() {
        return this.speed;
    }
}
