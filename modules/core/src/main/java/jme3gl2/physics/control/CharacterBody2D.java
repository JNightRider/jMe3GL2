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

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.listener.ContactListener;
import org.dyn4j.world.listener.ContactListenerAdapter;
import org.dyn4j.world.listener.StepListener;
import org.dyn4j.world.listener.StepListenerAdapter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

import jme3gl2.physics.PhysicsSpace;

/**
 * Class <code>CharacterBody2D</code> is specialized for physical bodies to be
 * controlled by the user. It provides methods for detecting the location of the
 * leather (floor, ceiling and wall).
 * <p>
 * Each object that is instantiated from this class must have a unique
 * identifier that is hosted at <code>UserData</code> that provides
 * <code>dyn4j</code>, to manage physical contacts, determine which flags are
 * activated, etc.
 * </p>
 * <p>
 * If a <code>CharacterBody2D</code> collides or makes contact with another
 * <code>CharacterBody2D</code> and are in the same layer, the collision of
 * physical bodies is deactivated, i.e. they do not collide.
 * </p>
 * <p>
 * To determine whether a physical body is in contact with a
 * <code>CharacterBody2D</code> and that this object is a body that is part of
 * the terrain, it is necessary that this object has as user data the following:
 * </p>
 * <ul>
 * <li><b>FLOOR:</b> A physical body acting as a ground</li>
 * <li><b>ONE_WAY_PLATFORM:</b> A physical body that acts as a platform, which
 * can be passed through</li>
 * </ul>
 * <p>
 * NOTE: Only bodies that are part of the terrain-map of the world can have this
 * user data (repeated).
 * 
 * @author wil
 * @version 1.1.5
 * @since 1.0.0
 */
public class CharacterBody2D extends PhysicsBody2D {
    /** Class logger. */
    private static final Logger LOG = Logger.getLogger(CharacterBody2D.class.getName());

    /**
     * This object can be used as an identifier for a single object of type
     * {@link CharacterBody2D}, avoid using it if possible to avoid problems in
     * the future.
     */
    public static final Object CHARACTER = new Object();
    /** Identifier for physical bodies that are part of the scene map terrain. */
    public static final Object FLOOR = new Object();

    /**
     * Identifier for physical bodies that are part of the terrain (platforms)
     * of the scene map.
     */
    public static final Object ONE_WAY_PLATFORM = new Object();

    /** Acceptable error for contact detection. */
    private static final double ERROR = 0.01;

    /**
     * Object in charge of clearing the flags if this <code>CharacterBody2D</code> 
     * has as identifier:
     * <ul>
     * <li>FLOOR</li>
     * <li>ONE_WAY_PLATFORM</li>
     * </ul>
     */
    protected final StepListener<PhysicsBody2D> physicsBody2DStepListener = new StepListenerAdapter<>() {
        @Override
        public void begin(TimeStep step, PhysicsWorld<PhysicsBody2D, ?> world) {
            boolean isGround = false;
            List<ContactConstraint<PhysicsBody2D>> contacts = world.getContacts(CharacterBody2D.this);
            for (ContactConstraint<PhysicsBody2D> cc : contacts) {
                if (is(cc.getOtherBody(CharacterBody2D.this), FLOOR, ONE_WAY_PLATFORM) && cc.isEnabled()) {
                    isGround = true;
                }
            }

            // Just clean it
            if (!isGround) {
                onGround = onCeiling = onWall = false;
            }
        }
    };

    /**
     * Listener in charge of verifying, administering the collisions/contacts of
     * each body with this <code>CharacterBody2D</code>.
     */
    protected final ContactListener<PhysicsBody2D> physicsBody2DContactListener = new ContactListenerAdapter<>() {
        @Override
        public void collision(ContactCollisionData<PhysicsBody2D> collision) {
            ContactConstraint<PhysicsBody2D> cc = collision.getContactConstraint();

            // Set the other body to unidirectional if necessary
            disableContactForOneWay(cc);

            // Ground condition monitoring
            trackIsOnGround(cc);
        }
    };

    /**
     * <code>true</code> if this physical body is landed on the ground, otherwise 
     * its value is <code>false</code>.
     */
    private boolean onGround;

    /**
     * <code>true</code> if this physical body makes contact with the roof (lower part)
     * of another body, otherwise its value is <code>false</code>.
     */
    private boolean onCeiling;

    /**
     * <code>true</code> if this physical body is in contact with the walls of
     * another body, otherwise its value is <code>false</code>.
     */
    private boolean onWall;

    /**
     * Layer where this physical body belongs, this is used to verify the contacts
     * between bodies. If 2 bodies collide and they are of type <code>CharacterBody2D</code>
     * that are in the same layer, they simply pass by.
     */
    private int layer = 0;

    /**
     * Generate an <code>CharacterBody2D</code> to control a character that is
     * able to determine whether it is on the floor, ceiling or wall.
     */
    public CharacterBody2D() {
    }

    // Getters / Setters
    /**
     * Returns the layer belonging to this physical body.
     * @return int
     */
    public int getLayer() {
        return layer;
    }

    /**
     * Establishes a new layer for this physical body.
     * @param layer int
     */
    public void setLayer(int layer) {
        this.layer = layer;
    }
    
    /**
     * (non-Javadoc)
     * @see jme3gl2.physics.control.PhysicsControl#setPhysicsSpace(jme3gl2.physics.PhysicsSpace) 
     * @param physicsSpace object
     */
    @Override
    public void setPhysicsSpace(PhysicsSpace<PhysicsBody2D> physicsSpace) {
        super.setPhysicsSpace(physicsSpace);
        if (physicsSpace != null) {
            physicsSpace.addStepListener(physicsBody2DStepListener);
            physicsSpace.addContactListener(physicsBody2DContactListener);
        }
    }

    /**
     * (non-Javadoc)
     * @see org.dyn4j.DataContainer#getUserData()
     * @return object
     */
    @Override
    public Object getUserData() {
        Object object = super.getUserData();
        if (object == null) {
            return CHARACTER;
        }
        return object;
    }

    /**
     * Auxiliar method to determine if a body is one of the given types, assuming 
     * the type is stored in the user data.
     *
     * @param body the body
     * @param types the set of types
     * @return boolean
     */
    protected boolean is(PhysicsBody2D body, Object... types) {
        for (Object type : types) {
            if (body.getUserData() == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns <code>true</code> if the given platform should alternate as
     * unidirectional given the position of the character's body.
     *
     * @param character the body of the character
     * @param platform the body of the platform
     * @return boolean
     */
    protected boolean allowOneWayUp(PhysicsBody2D character, PhysicsBody2D platform) {
        AABB wAABB = character.createAABB();
        AABB pAABB = platform.createAABB();
        
        // NOTE: This should change depending on the shape of the platform and its
        // orientation.
        // 
        // One idea might be to store the allowed platform normal in the platform
        // body and compare it with the ContactConstraint normal to see if they
        // point in the same direction.
        // 
        // Another option could be to project both on the normal platform to see
        // where they overlap.        
        return wAABB.getMinY() < pAABB.getMinY();
    }

    /**
     * Disables the restriction if it is between the character and the platform
     * and if the scenario meets the unidirectional condition.
     *
     * @param contactConstraint the restriction
     */
    protected void disableContactForOneWay(ContactConstraint<PhysicsBody2D> contactConstraint) {
        PhysicsBody2D b1 = contactConstraint.getBody1();
        PhysicsBody2D b2 = contactConstraint.getBody2();

        if ((b1 instanceof CharacterBody2D) && (b2 instanceof CharacterBody2D)) {
            if (b1.getUserData() == b2.getUserData()) {
                throw new IllegalStateException("Each CharacterBody2D<?> must have a unique [UserData] to identify itself.");
            }

            int layerB1 = ((CharacterBody2D) b1).getLayer(),
                layetB2 = ((CharacterBody2D) b2).getLayer();

            if (layerB1 == layetB2) {
                contactConstraint.setEnabled(false);
                return;
            }
        }

        if (is(b1, getUserData()) && is(b2, ONE_WAY_PLATFORM)) {
            if (allowOneWayUp(b1, b2) || isActiveButNotHandled()) {
                setHasBeenHandled(true);
                contactConstraint.setEnabled(false);
            }
        } else if (is(b1, ONE_WAY_PLATFORM) && is(b2, getUserData())) {
            if (allowOneWayUp(b2, b1) || isActiveButNotHandled()) {
                setHasBeenHandled(true);
                contactConstraint.setEnabled(false);
            }
        }
    }

    /**
     * Sets the indicators/flags if the given contact constraint is between the
     * character's body and a floor or unidirectional platform.
     *
     * @param contactConstraint the restriction
     */
    protected void trackIsOnGround(ContactConstraint<PhysicsBody2D> contactConstraint) {
        PhysicsBody2D b1 = contactConstraint.getBody1();
        PhysicsBody2D b2 = contactConstraint.getBody2();

        if (is(b1, getUserData())
                && is(b2, FLOOR, ONE_WAY_PLATFORM)
                && contactConstraint.isEnabled()) {
            collision(b1, b2);
        } else if (is(b1, FLOOR, ONE_WAY_PLATFORM)
                && is(b2, getUserData())
                && contactConstraint.isEnabled()) {
            collision(b2, b1);
        }
    }

    /**
     * Determine the flags, if the body enters against a wall, floor or ceiling.
     * @param character the body of the character
     * @param platform the body of the platform
     */
    protected void collision(PhysicsBody2D character, PhysicsBody2D platform) {
        AABB wAABB = character.createAABB();
        AABB pAABB = platform.createAABB();

        if (Double.compare(Math.abs(wAABB.getMinY() - pAABB.getMaxY()), ERROR) < 0) {
            onGround  = true;
            onCeiling = false;
            onWall    = false;
        } else if (Double.compare(Math.abs(pAABB.getMinY() - wAABB.getMaxY()), ERROR) < 0) {
            if (!onGround) {
                onCeiling = true;
                onWall    = false;
            }
        } else {
            if (!onGround) {
                onCeiling = false;
                onWall    = true;
            }
        }
    }

    /** (non-Javadoc )*/
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);        
        OutputCapsule out = ex.getCapsule(this);
        out.write(layer, "layer", 0);
        
        Object user = getUserData();
        if (user == CHARACTER) {
            out.write("CHARACTER", "CharacterBody2D#UserData", null);
        } else if (user == FLOOR) {
            out.write("FLOOR", "CharacterBody2D#UserData", null);
        } else if (user == ONE_WAY_PLATFORM) {
            out.write("ONE_WAY_PLATFORM", "CharacterBody2D#UserData", null);
        }
    }

    /** (non-Javadoc )*/
    @Override
    @SuppressWarnings("unchecked")
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        layer = in.readInt("layer", 0);
        
        String data = in.readString("CharacterBody2D#UserData", null);
        if (data == null) {
            return;
        }
        
        Object obj = null;
        if ("CHARACTER".equals(data)) {
            obj = CHARACTER;
        } else if ("FLOOR".equals(data)) {
            obj = FLOOR;
        } else if ("ONE_WAY_PLATFORM".equals(data)) {
            obj = ONE_WAY_PLATFORM;
        } else {
            LOG.log(Level.WARNING, "Could not load user data");
        }
        setUserData(obj);
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.scene.control.AbstractControl#controlUpdate(float)
     * @param tpf float
     */
    @Override
    protected void controlUpdate(float tpf) {
        applyPhysicsLocation(this);
        applyPhysicsRotation(this);
    }

    /**
     * Release this physical body from the scene as well as from the physical
     * space.
     */
    public void queueFree() {
        synchronized (this) {
            if (spatial.removeFromParent() && physicsSpace != null) {
                physicsSpace.removeBody(CharacterBody2D.this);
                physicsSpace.removeStepListener(physicsBody2DStepListener);
                physicsSpace.removeContactListener(physicsBody2DContactListener);
            }
        }
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.scene.control.AbstractControl#render(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort)
     * @param rm RenderManager
     * @param vp ViewPort
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        /* NO */
    }

    /**
     * Returns the player's status for getting off a platform.
     * @return boolean
     */
    protected boolean isActiveButNotHandled() {
        return false;
    }

    /**
     * Sets the player's status.
     * @param b boolean
     */
    protected void setHasBeenHandled(boolean b) {
    }

    // Flags
    /**
     * Returns the status of the player against the ceiling.
     * @return boolean
     */
    public boolean isOnCeiling() {
        return this.onCeiling;
    }

    /**
     * Returns the state of the player against the floor.
     * @return boolean
     */
    public boolean isOnFloor() {
        return this.onGround;
    }

    /**
     * Returns the state of the player with the walls.
     * @return boolean
     */
    public boolean isOnWall() {
        return this.onWall;
    }
}
