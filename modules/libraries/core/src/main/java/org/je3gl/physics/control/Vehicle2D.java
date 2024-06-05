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
package org.je3gl.physics.control;

import com.jme3.export.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.je3gl.physics.PhysicsSpace;
import org.je3gl.physics.joint.PhysicsJoint;

import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class Vehicle2D extends PhysicsBody2D implements Cloneable {

    private static final Logger LOGGER = Logger.getLogger(Vehicle2D.class.getName());

    protected PhysicsBody2D rearWheel;
    protected PhysicsBody2D frontWheel;
    
    private PhysicsJoint<PhysicsBody2D, WheelJoint<PhysicsBody2D>> rearWheelPhysicsJoint;
    private PhysicsJoint<PhysicsBody2D, WheelJoint<PhysicsBody2D>> frontWheelPhysicsJoint;

    protected double speed;
    protected double maxSpeed = 20.0;
    protected double acceleration = 0.1;
    protected double decceleration = 0.5;
    
    protected Vehicle2D() {
    }

    public Vehicle2D(PhysicsBody2D rearWheel, PhysicsBody2D frontWheel) {
        this(rearWheel, frontWheel, rearWheel.getWorldCenter(), frontWheel.getWorldCenter(), new Vector2(0.0, -1.0));
    }
    
    public Vehicle2D(PhysicsBody2D rearWheel, PhysicsBody2D frontWheel, Vector2 anchor, Vector2 axis) {
        this(rearWheel, frontWheel, anchor, anchor, axis);
    }
    
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

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public void setDecceleration(double decceleration) {
        this.decceleration = decceleration;
    }

    public void setMotorEnabled(boolean enabled) {
        rearWheelPhysicsJoint.getJoint().setMotorEnabled(enabled);
        frontWheelPhysicsJoint.getJoint().setMotorEnabled(enabled);
    }
    
    public void setMaximumMotorTorqueEnabled(boolean enabled) {
        rearWheelPhysicsJoint.getJoint().setMaximumMotorTorqueEnabled(enabled);
        frontWheelPhysicsJoint.getJoint().setMaximumMotorTorqueEnabled(enabled);
    }
    
    public void setMaximumMotorTorque(double torque) {
        rearWheelPhysicsJoint.getJoint().setMaximumMotorTorque(torque);
        frontWheelPhysicsJoint.getJoint().setMaximumMotorTorque(torque);
    }
    
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
    
    public void brake() {
        if (speed > 0.0) {
            reverse();
        } else if (speed < 0.0) {
            forward();
        } else {
            speed = 0.0;
        }
    }
    
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

    @Override
    public Vehicle2D clone(boolean cloneForce) {
        Vehicle2D clon = (Vehicle2D) super.clone(cloneForce);
        clon.rearWheelPhysicsJoint = rearWheelPhysicsJoint.clone();
        clon.rearWheel = clon.rearWheelPhysicsJoint.getJoint().getBody2();
        
        clon.frontWheelPhysicsJoint = frontWheelPhysicsJoint.clone();
        clon.frontWheel = clon.frontWheelPhysicsJoint.getJoint().getBody2();
        
        clon.speed    = speed;
        clon.maxSpeed = maxSpeed;
        
        clon.acceleration  = acceleration;
        clon.decceleration = decceleration;
        return clon;
    }

    @Override
    public Vehicle2D clone() {
        return this.clone(false);
    }

    public double getSpeed() {
        return speed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getDecceleration() {
        return decceleration;
    }

    public PhysicsBody2D getRearWheel() {
        return rearWheel;
    }

    public PhysicsBody2D getFrontWheel() {
        return frontWheel;
    }

    public WheelJoint<PhysicsBody2D> getRearWheelJoint() {
        return rearWheelPhysicsJoint.getJoint();
    }

    public WheelJoint<PhysicsBody2D> getFrontWheelJoint() {
        return frontWheelPhysicsJoint.getJoint();
    }

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
