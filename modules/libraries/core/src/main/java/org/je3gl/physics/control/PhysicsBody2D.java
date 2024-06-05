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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.UserData;
import com.jme3.scene.control.Control;
import com.jme3.util.TempVars;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dyn4j.Epsilon;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

import org.je3gl.physics.PhysicsSpace;
import org.je3gl.util.Converter;
import org.je3gl.utilities.TransformUtilities;

/**
 * An abstract implementation of the {@link org.je3gl.physics.control.PhysicsControl} 
 * interface.
 * <p>
 * An object of the <code>PhysicsBody2D</code> class is the body that the physics 
 * engine uses to give realism to the games. With this control we can manage a 
 * 2D model with or without physics.
 * 
 * @author wil
 * @version 1.5.5
 * @since 1.0.0
 */
public abstract class PhysicsBody2D extends Body implements Control, Cloneable, Savable, PhysicsControl<PhysicsBody2D> {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(PhysicsBody2D.class.getName());
    
    /** Physical space. */
    protected PhysicsSpace<PhysicsBody2D> physicsSpace;
    /** The 2D model. */
    protected Spatial spatial;
    
    /**
     * true &rarr; physics-space coordinates match local transform, false &rarr;
     * physics-space coordinates match world transform
     */
    private boolean localPhysics = false;
        
    /**
     * Generates a new object of class <code>PhysicsBody2D</code> to generate a 
     * physical body from a 2D model.
     */
    public PhysicsBody2D() { }

    public CollisionBody<BodyFixture> addFixture(PhysicsFixture fixture) {
        return this.addFixture(fixture.getFixture());
    }
    
    public boolean removeFixture(PhysicsFixture fixture) {
        return this.removeFixture(fixture.getFixture());
    }
    
    @Override
    public PhysicsBody2D clone() {
        return clone(false);
    }
    
    public PhysicsBody2D clone(boolean cloneForce) {
        try {
            final Cloner cloner      = new Cloner();            
            final PhysicsBody2D clon = (PhysicsBody2D) super.clone();
            
            clon.spatial = cloner.clone(spatial);
            clon.physicsSpace = physicsSpace;

            Object myuserObject = getUserData();
            if ((myuserObject instanceof JmeCloneable) || (myuserObject instanceof Cloneable)) {
                myuserObject = cloner.clone(myuserObject);
            }

            clon.setUserData(myuserObject);
            clon.removeAllFixtures();

            int fSize = this.getFixtureCount();
            for (int j = 0; j < fSize; j++) {
                BodyFixture bf  = this.getFixture(j);
                clon.addFixture(new PhysicsFixture(bf).clone());
            }
            
            // set the transform
            if (Math.abs(getTransform().getRotationAngle()) > Epsilon.E) {
                clon.getTransform().setRotation(getTransform().getRotationAngle());
            }
            if (!getTransform().getTranslation().isZero()) {
                clon.getTransform().setTranslation(getTransform().getTranslation().copy());
            }
        
            // set velocity
            if (!getLinearVelocity().isZero()) {
                clon.setLinearVelocity(getLinearVelocity().copy());
            }
            if (Math.abs(getAngularVelocity()) > Epsilon.E) {
                clon.setAngularVelocity(getAngularVelocity());
            }
            
            // set state properties
            if (!isEnabled()) {
                clon.setEnabled(true);
            } // by default the body is active
            if (isAtRest()) {
                clon.setAtRest(true);
            } // by default the body is awake
            if (!isAtRestDetectionEnabled()) {
                clon.setAtRestDetectionEnabled(false);
            } // by default auto sleeping is true
            if (isBullet()) {
                clon.setBullet(true);
            } // by default the body is not a bullet
            
            // set damping
            if (getLinearDamping() != Body.DEFAULT_LINEAR_DAMPING) {
                clon.setLinearDamping(getLinearDamping());
            }
            if (getAngularDamping() != Body.DEFAULT_ANGULAR_DAMPING) {
                clon.setAngularDamping(getAngularDamping());
            }
            // set gravity scale
            if (getGravityScale() != 1.0) {
                clon.setGravityScale(getGravityScale());
            }
            
            // set mass properties last
            Mass myMass = getMass();
            Mass iMass = new Mass(myMass.getCenter().copy(), myMass.getMass(), myMass.getInertia());
            iMass.setType(myMass.getType());   
            
            // new mass
            clon.setMass(iMass);
            
            if (cloneForce) {
                // set force/torque accumulators
                if (!getAccumulatedForce().isZero()) {
                    clon.applyForce(getAccumulatedForce().copy());
                }
                if (Math.abs(getAccumulatedTorque()) > Epsilon.E) {
                    applyTorque(getAccumulatedTorque());
                }
            }
       
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    protected Vector3f getSpatialTranslation() {
        Vector3f result;
         if (localPhysics) {
            result = spatial.getLocalTranslation(); // alias
        } else {
            result = spatial.getWorldTranslation(); // alias
        }
        return result;
    }
    
    protected Quaternion getSpatialRotation() {
        Quaternion result; 
        if (localPhysics) {
            result = spatial.getLocalRotation(); // alias
        } else {
            result = spatial.getWorldRotation(); // alias
        }

        return result;
    }
    
    public boolean isLocalPhysics() {
        return localPhysics;
    }

    public void setLocalPhysics(boolean localPhysics) {
        this.localPhysics = localPhysics;
    }

    @Override
    public void applyPhysicsRotation(PhysicsBody2D physicBody) {
        if (!isEnabled() && spatial == null)
            return;
        
        final Transform trans = physicBody.getTransform();
        final float rotation = Converter.toFloatValue(trans.getRotationAngle());

        final TempVars tempVars = TempVars.get();
        final Quaternion tmpInverseWorldRotation = new Quaternion(),
                         physicsOrientation = tempVars.quat1;
        
        Vector3f localLocation = spatial.getLocalTranslation();
        Quaternion localRotationQuat = spatial.getLocalRotation(); // alias
        if (!localPhysics && spatial.getParent() != null) {
            tmpInverseWorldRotation
                    .set(spatial.getParent().getWorldRotation())
                    .inverseLocal();
            TransformUtilities.rotate(
                        tmpInverseWorldRotation, localLocation, localLocation);
            localRotationQuat.set(physicsOrientation.fromAngleAxis(rotation, Vector3f.UNIT_Z));
            tmpInverseWorldRotation
                    .set(spatial.getParent().getWorldRotation())
                    .inverseLocal()
                    .mult(localRotationQuat, localRotationQuat);

            spatial.setLocalRotation(localRotationQuat);
        } else {
            physicsOrientation.fromAngleAxis(rotation, Vector3f.UNIT_Z);
            getJmeObject().setLocalRotation(physicsOrientation);
        }
        tempVars.release();
    }

    @Override
    public void applyPhysicsLocation(PhysicsBody2D physicBody) {
        if (!isEnabled() && spatial == null)
            return;
        
        final Transform trans = physicBody.getTransform();

        final float posX = Converter.toFloatValue(trans.getTranslationX());
        final float posY = Converter.toFloatValue(trans.getTranslationY());

        Vector3f physicsLocation = new Vector3f(posX, posY, spatial.getLocalTranslation().z);
        if (! localPhysics && spatial.getParent() != null) {
            Vector3f localLocation = spatial.getLocalTranslation();
            localLocation
                    .set(physicsLocation)
                    .subtractLocal(
                            spatial.getParent()
                                    .getWorldTranslation());
            localLocation.divideLocal(
                    spatial.getParent().getWorldScale());

            spatial.setLocalTranslation(localLocation);
        } else {
            spatial.setLocalTranslation(posX, posY, spatial.getLocalTranslation().z);
        }
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
    
    /**
     * (non-Javadoc)
     * @see jme3gl2.physics.control.PhysicsControl#setPhysicsSpace(jme3gl2.physics.PhysicsSpace) 
     * @param physicsSpace A {@link org.je3gl.physics.PhysicsSpace} object
     */
    @Override
    public void setPhysicsSpace(PhysicsSpace<PhysicsBody2D> physicsSpace) {
        if (this.physicsSpace != null && physicsSpace != null && this.physicsSpace != physicsSpace) {
            throw new IllegalStateException("This body has already been added to a physical space.");
        }
        this.physicsSpace = physicsSpace;
    }
    
    /**
     * (non-Javadoc)
     * @see jme3gl2.physics.control.PhysicsControl#getPhysicsSpace() 
     * @return A {@link org.je3gl.physics.PhysicsSpace} object
     */
    @Override
    public PhysicsSpace<PhysicsBody2D> getPhysicsSpace() {
        return physicsSpace;
    }

    /**
     * (non-Javadoc)
     * @param spatial object
     * @return object
     * @deprecated (?,?)
     */
    @Override
    @Deprecated
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.scene.control.Control#setSpatial(com.jme3.scene.Spatial) 
     * @param spatial object
     */
    @Override
    public void setSpatial(Spatial spatial) {
        if (this.spatial != null && spatial != null && spatial != this.spatial) {
            throw new IllegalStateException("This control has already been added to a Spatial.");
        }
        this.spatial = spatial;
        
        this.applyPhysicsLocation(this);
        this.applyPhysicsRotation(this);
        this.ready();
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.scene.control.Control#update(float) 
     * @param tpf float
     */
    @Override
    public void update(float tpf) {
        if (!isEnabled())
            return;        
        controlUpdate(tpf);
        physicsProcess(tpf);
    }

    /**
     * (non-Javadoc)
     *  @see com.jme3.scene.control.Control#render(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort) 
     * @param rm render-manager
     * @param vp view-port
     */
    @Override
    public void render(RenderManager rm, ViewPort vp) {
        if (!isEnabled())
            return;
        controlRender(rm, vp);
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

    /** Data initialization for this body (optional). */
    protected void ready() {}
    
    /**
     * Updating physical processes (optional).
     * @param delta time per frame (in seconds)
     */
    protected void physicsProcess(float delta) {}
    
    /**
     * To be implemented in subclass.
     * @param tpf time per frame (in seconds)
     */
    protected void controlUpdate(float tpf) {
        applyPhysicsLocation(this);
        applyPhysicsRotation(this);
    }

    /**
     * To be implemented in subclass.
     * @param rm the RenderManager rendering the controlled Spatial (not null)
     * @param vp the ViewPort being rendered (not null)
     */
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#read(com.jme3.export.JmeImporter) 
     * 
     * @param im {@link com.jme3.export.JmeImporter}
     * @throws IOException throws
     */
    @Override
    public void read(JmeImporter im) throws IOException { 
        InputCapsule in = im.getCapsule(this);
        UserData userObject = (UserData) in.readSavable("UserData", null);
        if (userObject != null) {
            setUserData(userObject.getValue());
        }
        
        int fSize = in.readInt("BodyFixture#fSize", 0);
        for (int j = 0; j < fSize; j++) {
            PhysicsFixture pf = (PhysicsFixture) in.readSavable("BodyFixture#" + String.valueOf(j), null);
            addFixture(pf.getFixture());
        }
        
        rotate(in.readDouble("RotationAngle", 0));
        translate(Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Translation", new Vector2f())));
        setLinearVelocity(Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("LinearVelocity", new Vector2f())));
        setAngularVelocity(in.readDouble("AngularVelocity", 0));
        
        setEnabled(in.readBoolean("Enabled", true));
        setAtRest(in.readBoolean("AtRest", false));
        setAtRestDetectionEnabled(in.readBoolean("AtRestDetectionEnabled", true));
        setBullet(in.readBoolean("Bullet", false));
        
        setLinearDamping(in.readDouble("LinearDamping", Body.DEFAULT_LINEAR_DAMPING));
        setAngularDamping(in.readDouble("AngularDamping", Body.DEFAULT_ANGULAR_DAMPING));
        setGravityScale(in.readDouble("GravityScale", 1.0));
        
        Vector2 mCenter = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Center", new Vector2f(0.0F, 0.0F)));
        Mass myMass     = new Mass(mCenter, in.readDouble("Mass", 0.0), in.readDouble("Inertia", 0.0));
        myMass.setType(in.readEnum("MassType", MassType.class, MassType.INFINITE));
        
        setMass(myMass);        
        applyForce(Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("AccumulatedForce", new Vector2f(0.0F, 0.0F))));
        applyTorque(in.readDouble("AccumulatedTorque", 0));
        
        spatial    = (Spatial) in.readSavable("Spatial", null);        
        if (spatial == null) {
            LOGGER.log(Level.SEVERE, "There is NO 'Spatial' to represent this physical body");
        }
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#write(com.jme3.export.JmeExporter) 
     * 
     * @param ex {@link com.jme3.export.JmeExporter}
     * @throws IOException throws
     */
    @Override
    @SuppressWarnings("unchecked")
    public void write(JmeExporter ex) throws IOException { 
        OutputCapsule out = ex.getCapsule(this);      
        Object userObject = this.getUserData();
        byte userType = -1;
        if (userObject != null) {
            try {
                userType = UserData.getObjectType(userObject);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "[ UserData ] :Unsupported type: {0}", userObject.getClass().getName());
            }
        }
        
        if (userType != -1) {
            out.write(new UserData(userType, userObject), "UserData", null);
        }
        
        int fSize = this.getFixtureCount();
        out.write(fSize, "BodyFixture#fSize", 0);
        
        for (int j = 0; j < fSize; j++) {
            BodyFixture bf  = this.getFixture(j);            
            out.write(new PhysicsFixture(bf), "BodyFixture#" + String.valueOf(j), null);
        }
        
        // set the transform
        if (Math.abs(getTransform().getRotationAngle()) > Epsilon.E) {
            out.write(getTransform().getRotationAngle(), "RotationAngle", 0);
        }
        if (!getTransform().getTranslation().isZero()) {
            out.write(Converter.toVector2fValueOfJME3(getTransform().getTranslation()), "Translation", null);
        }
        // set velocity
        if (!getLinearVelocity().isZero()) {
            out.write(Converter.toVector2fValueOfJME3(getLinearVelocity()), "LinearVelocity", null);
        }
        if (Math.abs(getAngularVelocity()) > Epsilon.E) {
            out.write(getAngularVelocity(), "AngularVelocity", 0);
        }
        // set state properties
        if (!isEnabled()) {
            out.write(isEnabled(), "Enabled", true);
        } // by default the body is active
        if (isAtRest()) {
            out.write(isAtRest(), "AtRest", false);
        } // by default the body is awake
        if (!isAtRestDetectionEnabled()) {
            out.write(isAtRestDetectionEnabled(), "AtRestDetectionEnabled", true);
        } // by default auto sleeping is true
        if (isBullet()) {
            out.write(isBullet(), "Bullet", false);
        } // by default the body is not a bullet
        // set damping
        if (getLinearDamping() != Body.DEFAULT_LINEAR_DAMPING) {
            out.write(getLinearDamping(), "LinearDamping", 0);
        }
        if (getAngularDamping() != Body.DEFAULT_ANGULAR_DAMPING) {
            out.write(getAngularDamping(), "AngularDamping", 0);
        }
        // set gravity scale
        if (getGravityScale() != 1.0) {
            out.write(getGravityScale(), "GravityScale", 0);
        }
        
        // set mass properties last
        Mass myMass = getMass();
        out.write(Converter.toVector2fValueOfJME3(myMass.getCenter()), "Center", null);
        out.write(myMass.getMass(), "Mass", 0);
        out.write(myMass.getInertia(), "Inertia", 0);
        out.write(myMass.getType(), "MassType", MassType.INFINITE);
        
        // set force/torque accumulators
        if (!getAccumulatedForce().isZero()) {
            out.write(Converter.toVector2fValueOfJME3(getAccumulatedForce()), "AccumulatedForce", null);
        }
        if (Math.abs(getAccumulatedTorque()) > Epsilon.E) {
            out.write(getAccumulatedTorque(), "AccumulatedTorque", 0);
        }
        
        // jme3
        out.write(spatial, "Spatial", null);
    }
}
