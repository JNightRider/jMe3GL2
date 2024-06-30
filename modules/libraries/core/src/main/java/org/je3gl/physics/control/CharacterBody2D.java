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

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.scene.Spatial;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.je3gl.physics.PhysicsSpace;
import org.je3gl.listener.CharacterContactListener;
import org.je3gl.listener.OneWayContactDisabler;

import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.listener.ContactListener;
import org.dyn4j.world.listener.ContactListenerAdapter;
import org.dyn4j.world.listener.StepListener;
import org.dyn4j.world.listener.StepListenerAdapter;

/**
 * Class <code>CharacterBody2D</code> in charge of managing the control of 2D 
 * characters where methods are provided to use to manage the different 
 * flags (floor, ceiling and wall).
 * <p>
 * If this object comes into contact (collision) with another object (<code>CharacterBody2D</code>) 
 * of the same type, the flags will not activate (it would not make sense for 
 * 2 characters to activate the flags).
 * 
 * @author wil
 * @version 2.0.0
 * @since 1.0.0
 */
public class CharacterBody2D extends PhysicsBody2D {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(CharacterBody2D.class.getName());    
    /** Key for user data that defines the type of the character (optional). */
    public static final String VK_USER_DATA = "CharacterBody2D#UserData";
    
    /**
     * Enumerated class responsible for defining a type of physical bodies to 
     * trigger certain actions related to a character, this can be used to 
     * determine a platform.
     */
    public static enum Type {
        /**
         * 'Character' type.
         */
        CHARACTER,
        
        /**
         * Of the 'floor' type, it does not directly affect the mechanics of a 
         * character, it is simply an identifier.
         */
        FLOOR,
        
        /**
         * Bodies with this identifier are sensitive to a character passing
         * through them.
         */
        ONE_WAY_PLATFORM;
    }
    
    /** Acceptable error for contact detection. */
    private static final double ERROR = 0.01;
    
    /**
     * Class in charge of managing a flag for the actions of a character when 
     * coming into contact with their environment (other physical bodies).
     * 
     * @since 3.0.0
     */
    protected class BooleanHandler implements Cloneable {
        
        /** If the flag is active. */
        private boolean active;
        /** {@code true} if the active status has been handled. */
	private boolean hasBeenHandled;

        /**
         * Constructor.
         */
        public BooleanHandler() {
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#clone() 
         */
        @Override
        protected BooleanHandler clone() {
            try {
                BooleanHandler clon = (BooleanHandler) 
                                        BooleanHandler.super.clone();
                clon.active = active;
                clon.hasBeenHandled = hasBeenHandled;
                return clon;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        
        /**
         * Activate this handler (flag).
         */
        public void activate() {
            boolean active0 = this.active;
            this.active = true;

            // if the state transitioned from inactive to active
            // flag that it needs to be handled
            if (!active0) {
                this.hasBeenHandled = false;
            }
        }
        
        /**
         * Returns the state of this handler (flag).
         * @return boolean
         */
        public boolean isActiveButNotHandled() {
            if (this.hasBeenHandled) {
                return false;
            }
            return this.active;
        }

        /**
         * Set the state of this handler.
         * @param hasBeenHandled boolean
         */
        public void setHasBeenHandled(boolean hasBeenHandled) {
            this.hasBeenHandled = hasBeenHandled;
        }
    }
    
    /**
     * At the beginning of each step of the world, check whether the body is in
     * contact with one of the bodies on the ground or another.
     */
    private final StepListener<PhysicsBody2D> stepListener = new StepListenerAdapter<>() {
        @Override
        public void begin(TimeStep step, PhysicsWorld<PhysicsBody2D, ?> world) {
            super.begin(step, world);

            boolean isGround = false;
            List<ContactConstraint<PhysicsBody2D>> contacts = world.getContacts(CharacterBody2D.this);
            for (ContactConstraint<PhysicsBody2D> cc : contacts) {
                if (! is(cc.getOtherBody(CharacterBody2D.this)) && cc.isEnabled()) {
                    isGround = true;
                }
            }

            // only clear it
            if (!isGround) {
                onGround  = false;
                onCeiling = false;
                onWall    = false;
            }
        }
    };
    
    /**
     * When processing contacts, we must check whether we are colliding with the
     * one-way platform or another physical body.
     */
    private final ContactListener<PhysicsBody2D> contactListener = new ContactListenerAdapter<PhysicsBody2D>() {
        @Override
        public void collision(ContactCollisionData<PhysicsBody2D> collision) {
            ContactConstraint<PhysicsBody2D> cc = collision.getContactConstraint();

            // set the other body to one-way if necessary
            disableContactForOneWay(cc);

            // track on the on-ground status
            trackIsOnBody(cc);

            super.collision(collision);
        }
    };
    
    /** *  A default listener for interface {@link org.je3gl.listener.CharacterContactListener}. */
    private final CharacterContactListener<CharacterBody2D, PhysicsBody2D> defaultCharacterContactListener = new CharacterContactListener<CharacterBody2D, PhysicsBody2D>() {
        @Override
        public boolean trackIsOnGround(CharacterBody2D character, PhysicsBody2D platform, ContactConstraint<PhysicsBody2D> contactConstraint) {
            Vector2 locA = character.getTransform().getTranslation(),
                    locB = platform.getTransform().getTranslation();

            Vector2 dif = locB.difference(locA);
            if (dif.y < 0) {
                AABB wAABB = character.createAABB();
                AABB pAABB = platform.createAABB();

                if (Double.compare(Math.abs(wAABB.getMinY() - pAABB.getMaxY()), ERROR) < 0) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean trackIsOnCeiling(CharacterBody2D character, PhysicsBody2D platform, ContactConstraint<PhysicsBody2D> contactConstraint) {
            Vector2 locA = character.getTransform().getTranslation(),
                    locB = platform.getTransform().getTranslation();

            Vector2 dif = locB.difference(locA);
            if (dif.y >= 0) {
                AABB wAABB = character.createAABB();
                AABB pAABB = platform.createAABB();

                if (Double.compare(Math.abs(pAABB.getMinY() - wAABB.getMaxY()), ERROR) < 0) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean trackIsOnWall(CharacterBody2D character, PhysicsBody2D platform, ContactConstraint<PhysicsBody2D> contactConstraint) {
            AABB wAABB = character.createAABB();
            AABB pAABB = platform.createAABB();

            if ((Double.compare(Math.abs(wAABB.getMinY() - pAABB.getMaxY()), ERROR) < 0)
                    || (Double.compare(Math.abs(pAABB.getMinY() - wAABB.getMaxY()), ERROR) < 0)) {
                return false;
            }
            return true;
        }};
    
    /** *  A default listener for interface {@link org.je3gl.listener.OneWayContactDisabler}. */
    private final OneWayContactDisabler<PhysicsBody2D> defaultOneWayContactDisabler = (PhysicsBody2D body) -> {
        if (is(body, Type.ONE_WAY_PLATFORM)) {
            return true;
        }
        return false;
    };
    
    /**
     * <code>true</code> if this physical body is landed on the ground, otherwise
     * its value is <code>false</code>.
     */
    private boolean onGround;
    
    /**
     * <code>true</code> if this physical body makes contact with the roof
     * (lower part) of another body, otherwise its value is <code>false</code>.
     */
    private boolean onCeiling;
    
    /**
     * <code>true</code> if this physical body is in contact with the walls of
     * another body, otherwise its value is <code>false</code>.
     */
    private boolean onWall;
    
    /** Object that is responsible for listening to the character's contacts. */
    private CharacterContactListener<CharacterBody2D, PhysicsBody2D> characterContactListener;
    /** Object in charge of disabling physical bodies that are of type {@link Type#ONE_WAY_PLATFORM}. */
    private OneWayContactDisabler<PhysicsBody2D> oneWayContactDisabler;
    /** A handler for this character. */
    private BooleanHandler downHandler;

    /**
     * Generates a new instance of class <code>CharacterBody2D</code> to manage a 
     * physical body as a 2D character.
     */
    public CharacterBody2D() {
        this(null);
    }
    
    /**
     * Generates a new instance of class <code>CharacterBody2D</code> to manage a 
     * physical body as a 2D character.
     * 
     * @param oneWayContactDisabler disable listener
     */
    public CharacterBody2D(OneWayContactDisabler<PhysicsBody2D> oneWayContactDisabler) {
        this(oneWayContactDisabler, null);
    }
    
    /**
     * Generates a new instance of class <code>CharacterBody2D</code> to manage a 
     * physical body as a 2D character.
     * 
     * @param oneWayContactDisabler disable listener
     * @param characterContactListener contact listener
     */
    public CharacterBody2D(OneWayContactDisabler<PhysicsBody2D> oneWayContactDisabler, CharacterContactListener<CharacterBody2D, PhysicsBody2D> characterContactListener) {
        this.downHandler           = new BooleanHandler();
        if (oneWayContactDisabler == null) {
            this.oneWayContactDisabler = defaultOneWayContactDisabler;
        } else {
            this.oneWayContactDisabler = oneWayContactDisabler;
        }
        
        if (characterContactListener == null) {
            this.characterContactListener = defaultCharacterContactListener;
        } else {
            this.characterContactListener = characterContactListener;
        }
    }

    /**
     * Set a new contact listener.
     * @param characterContactListener listener 
     */
    public void setCharacterContactListener(CharacterContactListener<CharacterBody2D, PhysicsBody2D> characterContactListener) {
        this.characterContactListener = characterContactListener;
    }

    /**
     * Set a new disable listener
     * @param oneWayContactDisabler disabler
     */
    public void setOneWayContactDisabler(OneWayContactDisabler<PhysicsBody2D> oneWayContactDisabler) {
        this.oneWayContactDisabler = oneWayContactDisabler;
    }

    /*
     * (non-Javadoc)
     * @see org.jegl.physics.control.PhysicsControl#setPhysicsSpace(org.jegl.physics.PhysicsSpace) 
     */
    @Override
    public void setPhysicsSpace(PhysicsSpace<PhysicsBody2D> physicsSpace) {
        if (this.physicsSpace != null && physicsSpace != null && this.physicsSpace != physicsSpace) {
            throw new IllegalStateException("This body has already been added to a physical space.");
        }
        
        if (physicsSpace == null && this.physicsSpace != null) {
            this.physicsSpace.removeStepListener(stepListener);
            this.physicsSpace.removeContactListener(contactListener);
            fireSpaceListener(physicsSpace, false);
        } else if (physicsSpace != null && this.physicsSpace == null) {
            physicsSpace.addStepListener(stepListener);
            physicsSpace.addContactListener(contactListener);
            fireSpaceListener(physicsSpace, true);
        }
        this.physicsSpace = physicsSpace;
    }
    
    /**
     * Returns the type.
     * @return type
     */
    public Type getBodyType() {
        return Type.CHARACTER;
    }

    /**
     * Method responsible for activating a handler so that this character can pass
     * through physical objects of type {@link Type#ONE_WAY_PLATFORM}
     */
    public void applyDown() {
        downHandler.activate();
    }
    
    /**
     * Auxiliar method to determine if a body is one of the given types,
     * assuming the type is stored in the user data.
     *
     * @param body the body
     * @param types the set of types
     * @return boolean
     */
    protected boolean is(PhysicsBody2D body, Type... types) {
        if (!(body.getUserData() instanceof Type) && (body.getJmeObject() != null && !(body.getJmeObject().getUserData(VK_USER_DATA) instanceof Type)) || (types.length == 0)) {
            return (body instanceof CharacterBody2D);
        }
        for (final Type type : types) {
            if (body.getUserData() != null && body.getUserData() == type) {
                return true;
            } else {
                Spatial sp = body.getJmeObject();
                if (sp != null && sp.getUserData(VK_USER_DATA) != null) {
                    if (sp.getUserData(VK_USER_DATA) == type) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Returns true if the given platform should be toggled as one-way given the
     * position of the character body.
     *
     * @param character the character body
     * @param platform the platform body
     * @return boolean
     */
    protected boolean allowOneWayUp(PhysicsBody2D character, PhysicsBody2D platform) {
        AABB wAABB = character.createAABB();
        AABB pAABB = platform.createAABB();

        // NOTE: this would need to change based on the shape of the platform and it's orientation
        // 
        // one thought might be to store the allowed normal of the platform on the platform body
        // and check that against the ContactConstraint normal to see if they are pointing in the
        // same direction
        //
        // another option might be to project both onto the platform normal to see where they are overlapping
        if (wAABB.getMinY() < pAABB.getMinY()) {
            return true;
        }
        return false;
    }

    /**
     * Disables the constraint if it's between the character and platform and it
     * the scenario meets the condition for one-way.
     *
     * @param contactConstraint the constraint
     */
    protected void disableContactForOneWay(ContactConstraint<PhysicsBody2D> contactConstraint) {        
        PhysicsBody2D b1 = contactConstraint.getBody1();
        PhysicsBody2D b2 = contactConstraint.getBody2();

        if (is(b1) && (! is(b2) && oneWayContactDisabler.isDeactivatable(b2))) {
            if (allowOneWayUp(b1, b2) || downHandler.isActiveButNotHandled()) {
                downHandler.setHasBeenHandled(true);
                contactConstraint.setEnabled(false);
            }
        } else if ((! is(b1) && oneWayContactDisabler.isDeactivatable(b1)) && is(b2)) {
            if (allowOneWayUp(b2, b1) || downHandler.isActiveButNotHandled()) {
                downHandler.setHasBeenHandled(true);
                contactConstraint.setEnabled(false);
            }
        }
    }

    /**
     * Sets the indicators/flags if the given contact constraint is between the
     * character's body and a floor or unidirectional platform.
     *
     * @param contactConstraint the constraint
     */
    protected void trackIsOnBody(ContactConstraint<PhysicsBody2D> contactConstraint) {
        PhysicsBody2D b1 = contactConstraint.getBody1();
        PhysicsBody2D b2 = contactConstraint.getBody2();

        if (! is(b2) && is(b1) && contactConstraint.isEnabled()) {
            onGround  = characterContactListener.trackIsOnGround((CharacterBody2D) b1, b2, contactConstraint);
            onCeiling = characterContactListener.trackIsOnCeiling((CharacterBody2D) b1, b2, contactConstraint);
            onWall    = characterContactListener.trackIsOnWall((CharacterBody2D) b1, b2, contactConstraint);
        } else if (! is(b1) && is(b2) && contactConstraint.isEnabled()) {
            onGround  = characterContactListener.trackIsOnGround((CharacterBody2D) b2, b1, contactConstraint);
            onCeiling = characterContactListener.trackIsOnCeiling((CharacterBody2D) b2, b1, contactConstraint);
            onWall    = characterContactListener.trackIsOnWall((CharacterBody2D) b2, b1, contactConstraint);
        }
    }
    
    /**
     * Returns the state of the player against the floor.
     * @return boolean
     */
    public boolean isOnFloor() {
        return onGround;
    }

    /**
     * Returns the status of the player against the ceiling.
     * @return boolean
     */
    public boolean isOnCeiling() {
        return onCeiling;
    }

    /**
     * Returns the state of the player with the walls.
     * @return boolean
     */
    public boolean isOnWall() {
        return onWall;
    }

    /*
     * (non-Javadoc)
     * @see com.jme3.export.Savable#write(com.jme3.export.JmeExporter) 
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);        
        if (characterContactListener != defaultCharacterContactListener) {
            out.write(characterContactListener, "CharacterContactListener", null);
            LOGGER.log(Level.INFO, "An object of type 'CharacterContactListener<?,?>' has been serialized >> {0}", characterContactListener.getClass().getName());
        }
        if (oneWayContactDisabler != defaultOneWayContactDisabler) {
            out.write(oneWayContactDisabler, "OneWayContactDisabler", null);
            LOGGER.log(Level.INFO, "An object of type 'OneWayContactDisabler<?>' has been serialized >> {0}", oneWayContactDisabler.getClass().getName());
        }
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
        CharacterContactListener<CharacterBody2D, PhysicsBody2D> tmpCharacterContactListener = (CharacterContactListener<CharacterBody2D, PhysicsBody2D>) in.readSavable("CharacterContactListener", null);
        if (tmpCharacterContactListener != null) {
            characterContactListener = tmpCharacterContactListener;
            LOGGER.log(Level.INFO, "A listener of type ''{0}'' has been imported", tmpCharacterContactListener.getClass().getName());
        }
        OneWayContactDisabler<PhysicsBody2D> tmpOneWayContactDisabler = (OneWayContactDisabler<PhysicsBody2D>) in.readSavable("OneWayContactDisabler", null);
        if (tmpCharacterContactListener != null) {
            oneWayContactDisabler = tmpOneWayContactDisabler;
            LOGGER.log(Level.INFO, "A listener of type ''{0}'' has been imported", tmpOneWayContactDisabler.getClass().getName());
        }
    }
}
