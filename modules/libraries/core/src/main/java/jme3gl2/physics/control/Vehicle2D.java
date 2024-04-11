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
package jme3gl2.physics.control;

import com.jme3.export.*;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

import java.io.IOException;

import jme3gl2.physics.PhysicsSpace;
import jme3gl2.physics.joint.PhysicsJoint;

import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class Vehicle2D extends PhysicsBody2D {

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
        this(rearWheel, frontWheel, new Vector2(-25.0, -3.0), new Vector2(0.0, -1.0));
    }
    
    public Vehicle2D(PhysicsBody2D rearWheel, PhysicsBody2D frontWheel, Vector2 anchor, Vector2 axis) {
        this.rearWheel  = rearWheel;   // Rear Wheel
        this.frontWheel = frontWheel;  // Front Wheel
        
        // Rear Motor
        WheelJoint<PhysicsBody2D> rearWheelJoint = new WheelJoint<>(this, rearWheel, anchor.copy(), axis.copy());
        rearWheelJoint.setMotorEnabled(true);
        rearWheelJoint.setMotorSpeed(0.0);
        rearWheelJoint.setMaximumMotorTorqueEnabled(true);
        rearWheelJoint.setMaximumMotorTorque(1000.0);
        rearWheelPhysicsJoint = new PhysicsJoint<>(rearWheelJoint);

        // Front Motor
        WheelJoint<PhysicsBody2D> frontWheelJoint = new WheelJoint<>(this, frontWheel, anchor.copy(), axis.copy());
        frontWheelJoint.setMotorEnabled(true);
        frontWheelJoint.setMotorSpeed(0.0);
        frontWheelJoint.setMaximumMotorTorqueEnabled(true);
        frontWheelJoint.setMaximumMotorTorque(1000.0);
        frontWheelPhysicsJoint = new PhysicsJoint<>(frontWheelJoint);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
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
            
            getPhysicsSpace().removeJoint(rearWheelPhysicsJoint);
            getPhysicsSpace().removeJoint(frontWheelPhysicsJoint);
        } else if ((physicsSpace != null && getPhysicsSpace() == null) && (rearWheel != null && frontWheel != null)) {
            physicsSpace.addBody(rearWheel);
            physicsSpace.addBody(frontWheel);
            
            physicsSpace.addJoint(rearWheelPhysicsJoint);
            physicsSpace.addJoint(frontWheelPhysicsJoint);
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
    protected void controlRender(RenderManager rm, ViewPort vp) { }

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
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
    }
}
