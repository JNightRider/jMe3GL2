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
package org.je3gl.physics.control;

import com.jme3.export.*;
import com.jme3.util.clone.Cloner;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.je3gl.physics.PhysicsSpace;
import org.je3gl.physics.joint.PhysicsJoint;

import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Vector2;

/**
 * Class in charge of managing 3 physical bodies where they interact with each 
 * other to form a 2D vehicle. It has a rear wheel + front wheel, the third object 
 * is the complete body of the vehicle.
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class Vehicle2D extends PhysicsBody2D {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(Vehicle2D.class.getName());

    /** rear wheel. */
    protected PhysicsBody2D rearWheel;
    /** front wheel.*/
    protected PhysicsBody2D frontWheel;
    
    /** Joint used for the rear wheel. */
    private PhysicsJoint<PhysicsBody2D, WheelJoint<PhysicsBody2D>> rearWheelPhysicsJoint;
    /** Joint used for the front wheel. */
    private PhysicsJoint<PhysicsBody2D, WheelJoint<PhysicsBody2D>> frontWheelPhysicsJoint;

    //primitive
    
    /** Vehicle speed. */
    protected double speed;
    /** Maximum speed that this vehicle can have. */
    protected double maxSpeed = 20.0;
    /** Acceleration level to move the vehicle. */
    protected double acceleration = 0.1;
    /** Speed ​​to decelerate the vehicle (stop it). */
    protected double decceleration = 0.5;
    
    /**
     * Constructor.
     */
    protected Vehicle2D() {
    }

    /**
     * Generate a new vehicle through class <code>Vehicle2D</code> where you specify 
     * the 2 wheels it will have, given the width between the axles as well as the
     * distances to the vehicle.
     * 
     * @param rearWheel rear wheel
     * @param frontWheel front wheel
     */
    public Vehicle2D(PhysicsBody2D rearWheel, PhysicsBody2D frontWheel) {
        this(rearWheel, frontWheel, rearWheel.getWorldCenter(), frontWheel.getWorldCenter(), new Vector2(0.0, -1.0));
    }
    
    /**
     * Generate a new vehicle through class <code>Vehicle2D</code> where you specify 
     * the 2 wheels it will have, given the width between the axles as well as the
     * distances to the vehicle.
     * 
     * @param rearWheel rear wheel
     * @param frontWheel front wheel
     * @param anchor the anchor point for the 2 wheels
     * @param axis the axis of allowed motion
     */
    public Vehicle2D(PhysicsBody2D rearWheel, PhysicsBody2D frontWheel, Vector2 anchor, Vector2 axis) {
        this(rearWheel, frontWheel, anchor, anchor, axis);
    }
    
    /**
     * Generate a new vehicle through class <code>Vehicle2D</code> where you specify 
     * the 2 wheels it will have, given the width between the axles as well as the
     * distances to the vehicle.
     * 
     * @param rearWheel rear wheel
     * @param frontWheel front wheel
     * @param anchorRearWheel the anchor point for the rear wheel
     * @param anchorRrontWheel the anchor point for the front wheel
     * @param axis the axis of allowed motion
     */
    public Vehicle2D(PhysicsBody2D rearWheel, PhysicsBody2D frontWheel, Vector2 anchorRearWheel, Vector2 anchorRrontWheel, Vector2 axis) {
        this.rearWheel  = rearWheel;   // Rear Wheel
        this.frontWheel = frontWheel;  // Front Wheel
        
        // Rear Motor
        WheelJoint<PhysicsBody2D> rearWheelJoint = new WheelJoint<>(this, rearWheel, anchorRearWheel.copy(), axis.copy());
        rearWheelJoint.setMotorEnabled(true);
        rearWheelJoint.setMotorSpeed(0.0);
        rearWheelJoint.setMaximumMotorTorqueEnabled(true);
        rearWheelJoint.setMaximumMotorTorque(1000.0);
        rearWheelPhysicsJoint = new PhysicsJoint<>(rearWheelJoint);

        // Front Motor
        WheelJoint<PhysicsBody2D> frontWheelJoint = new WheelJoint<>(this, frontWheel, anchorRrontWheel.copy(), axis.copy());
        frontWheelJoint.setMotorEnabled(true);
        frontWheelJoint.setMotorSpeed(0.0);
        frontWheelJoint.setMaximumMotorTorqueEnabled(true);
        frontWheelJoint.setMaximumMotorTorque(1000.0);
        frontWheelPhysicsJoint = new PhysicsJoint<>(frontWheelJoint);
    }

    /**
     * Set a new speed.
     * @param maxSpeed double
     */
    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * Set a new value for acceleration.
     * @param acceleration double
     */
    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * Set a new value for deceleration.
     * @param decceleration double
     */
    public void setDecceleration(double decceleration) {
        this.decceleration = decceleration;
    }

    /**
     * Activates or deactivates the motors (joints) of this vehicle.
     * @param enabled boolean
     */
    public void setMotorEnabled(boolean enabled) {
        rearWheelPhysicsJoint.getJoint().setMotorEnabled(enabled);
        frontWheelPhysicsJoint.getJoint().setMotorEnabled(enabled);
    }
    
    /**
     * Sets the maximum state of the motors (Joints)
     * @param enabled boolean
     */
    public void setMaximumMotorTorqueEnabled(boolean enabled) {
        rearWheelPhysicsJoint.getJoint().setMaximumMotorTorqueEnabled(enabled);
        frontWheelPhysicsJoint.getJoint().setMaximumMotorTorqueEnabled(enabled);
    }
    
    /**
     * Sets the minimum state of the motors (Joints)
     * @param torque double
     */
    public void setMaximumMotorTorque(double torque) {
        rearWheelPhysicsJoint.getJoint().setMaximumMotorTorque(torque);
        frontWheelPhysicsJoint.getJoint().setMaximumMotorTorque(torque);
    }
    
    /* (non-Javadoc)
     * @see org.je3gl.physics.control.PhysicsControl#setPhysicsSpace(org.je3gl.physics.PhysicsSpace) 
     */
    @Override
    public void setPhysicsSpace(PhysicsSpace<PhysicsBody2D> physicsSpace) {
        if (physicsSpace == getPhysicsSpace()) {
            return;
        }
        
        if ((physicsSpace == null && getPhysicsSpace() != null) && (rearWheel != null && frontWheel != null)) {
            getPhysicsSpace().removeBody(rearWheel);
            getPhysicsSpace().removeBody(frontWheel);
            
            getPhysicsSpace().removePhysicsJoint(rearWheelPhysicsJoint);
            getPhysicsSpace().removePhysicsJoint(frontWheelPhysicsJoint);
        } else if ((physicsSpace != null && getPhysicsSpace() == null) && (rearWheel != null && frontWheel != null)) {
            physicsSpace.addBody(rearWheel);
            physicsSpace.addBody(frontWheel);
                        
            physicsSpace.addPhysicsJoint(rearWheelPhysicsJoint);
            physicsSpace.addPhysicsJoint(frontWheelPhysicsJoint);
        }        
        super.setPhysicsSpace(physicsSpace);
    }
    
    /* non-Javadoc)
     * @see com.jme3.util.clone.JmeCloneable#cloneFields(com.jme3.util.clone.Cloner, java.lang.Object) 
     */
    @Override
    public void cloneFields(Cloner cloner, Object object) {
        super.cloneFields(cloner, object);
        Vehicle2D original = (Vehicle2D) object;
        
        rearWheel  = cloner.clone(original.rearWheel);
        frontWheel = cloner.clone(original.frontWheel);        
        rearWheelPhysicsJoint  = cloner.clone(original.rearWheelPhysicsJoint);
        frontWheelPhysicsJoint = cloner.clone(original.frontWheelPhysicsJoint);
        
        speed    = original.speed;
        maxSpeed = original.maxSpeed;
        acceleration  = original.acceleration;
        decceleration = original.decceleration;
    }
    
    /**
     * Move vehicle forward
     */
    public void forward() {
        if (speed < 0.0) {
            speed = speed + (acceleration * 2.0);
        } else {
            speed = speed + acceleration;
        }
        if (speed > maxSpeed) {
            speed = maxSpeed;
        }
    }
    
    /**
     * Move vehicle backward
     */
    public void reverse() {
        if (speed > 0) {
            speed = speed - (acceleration * 2.0);
        } else {
            speed = speed - acceleration;
        }

        if (speed < -maxSpeed) {
            speed = -maxSpeed;
        }
    }
    
    /**
     * Stop the vehicle.
     */
    public void brake() {
        if (speed > 0.0) {
            reverse();
        } else if (speed < 0.0) {
            forward();
        } else {
            speed = 0.0;
        }
    }
    
    /* (non-Javadoc)
     * @see com.jme3.scene.control.AbstractControl#controlUpdate(float) 
     */
    @Override
    protected void controlUpdate(float tpf) {
        super.controlUpdate(tpf);
        if (rearWheelPhysicsJoint != null) {
            rearWheelPhysicsJoint.getJoint().setMotorSpeed(this.speed);
        }
        if (frontWheelPhysicsJoint != null) {
            frontWheelPhysicsJoint.getJoint().setMotorSpeed(this.speed);
        }
    }
    
    /**
     * Returns the current vehicle speed
     * @return double
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Returns the maximum speed of the vehicle
     * @return double
     */
    public double getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Returns acceleration speed
     * @return double
     */
    public double getAcceleration() {
        return acceleration;
    }

    /**
     * Returns the deceleration speed
     * @return double
     */
    public double getDecceleration() {
        return decceleration;
    }
    
    /**
     * Returns the rear wheel (body)
     * @return body
     */
    public PhysicsBody2D getRearWheel() {
        return rearWheel;
    }

    /**
     * Returns the front wheel (body)
     * @return body
     */
    public PhysicsBody2D getFrontWheel() {
        return frontWheel;
    }

    /**
     * Returns the rear wheel joint.
     * @return joint
     */
    public WheelJoint<PhysicsBody2D> getRearWheelJoint() {
        return rearWheelPhysicsJoint.getJoint();
    }

    /**
     * Returns the front wheel joint.
     * @return joint
     */
    public WheelJoint<PhysicsBody2D> getFrontWheelJoint() {
        return frontWheelPhysicsJoint.getJoint();
    }

    /*
     * (non-Javadoc)
     * @see com.jme3.export.Savable#write(com.jme3.export.JmeExporter) 
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        out.write(rearWheel, "RearWheel", null);
        out.write(frontWheel, "FrontWheel", null);
        
        out.write(rearWheelPhysicsJoint, "RearWheelPhysicsJoint", null);
        out.write(frontWheelPhysicsJoint, "FrontWheelPhysicsJoint", null);
        
        out.write(speed, "Speed", 0.0);
        out.write(maxSpeed, "MaxSpeed", 20.0);
        out.write(acceleration , "Acceleration ", 0.1);
        out.write(decceleration, "Decceleration", 0.5);
    }

    /*
     * (non-Javadoc)
     * @see com.jme3.export.Savable#read(com.jme3.export.JmeImporter) 
     */
    @Override
    @SuppressWarnings("unchecked")
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        
        rearWheel  = (PhysicsBody2D) in.readSavable("RearWheel", null);
        frontWheel = (PhysicsBody2D) in.readSavable("FrontWheel", null);
        if (rearWheel == null || frontWheel == null) {
            LOGGER.log(Level.SEVERE, "The wheel bodies of the vehicle were not found.");
        }
        
        rearWheelPhysicsJoint  = (PhysicsJoint<PhysicsBody2D, WheelJoint<PhysicsBody2D>>) in.readSavable("RearWheelPhysicsJoint", null);
        frontWheelPhysicsJoint = (PhysicsJoint<PhysicsBody2D, WheelJoint<PhysicsBody2D>>) in.readSavable("FrontWheelPhysicsJoint", null);
        if (rearWheelPhysicsJoint == null || frontWheelPhysicsJoint == null) {
            LOGGER.log(Level.SEVERE, "Could not import joints WheelJoin<?>");
            return;
        }
        
        rearWheelPhysicsJoint.getJoint().setMotorEnabled(true);
        rearWheelPhysicsJoint.getJoint().setMotorSpeed(0.0);
        rearWheelPhysicsJoint.getJoint().setMaximumMotorTorqueEnabled(true);
        rearWheelPhysicsJoint.getJoint().setMaximumMotorTorque(1000.0);

        frontWheelPhysicsJoint.getJoint().setMotorEnabled(true);
        frontWheelPhysicsJoint.getJoint().setMotorSpeed(0.0);
        frontWheelPhysicsJoint.getJoint().setMaximumMotorTorqueEnabled(true);
        frontWheelPhysicsJoint.getJoint().setMaximumMotorTorque(1000.0);
        
        speed    = in.readDouble("Speed", 0.0);
        maxSpeed = in.readDouble("MaxSpeed", 20.0);
        acceleration  = in.readDouble("Acceleration", 0.1);
        decceleration = in.readDouble("Decceleration", 0.5);
    }
}
