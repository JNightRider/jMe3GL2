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

import java.util.List;
import java.util.logging.Logger;
import jme3gl2.physics.PhysicsSpace;
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
 *
 * @author wil
 * @version 2.0.0
 * @since 1.0.0
 */
public class CharacterBody2D extends PhysicsBody2D {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(CharacterBody2D.class.getName());
    /** Acceptable error for contact detection. */
    private static final double ERROR = 0.01;
    
    protected class BooleanHandler {
        
        private boolean active;
	private boolean hasBeenHandled;

        public BooleanHandler() {
        }
        
        public void activate() {
            boolean active0 = this.active;
            this.active = true;

            // if the state transitioned from inactive to active
            // flag that it needs to be handled
            if (!active0) {
                this.hasBeenHandled = false;
            }
        }
        
        public boolean isActiveButNotHandled() {
            if (this.hasBeenHandled) {
                return false;
            }
            return this.active;
        }

        public void setHasBeenHandled(boolean hasBeenHandled) {
            this.hasBeenHandled = hasBeenHandled;
        }
    }
    
    // at the beginning of each world step, check if the body is in
    // contact with any of the floor bodies
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
    
    // when contacts are processed, we need to check if we're colliding with either
    // the one-way platform or the ground
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
        
    private boolean onGround;
    private boolean onCeiling;
    private boolean onWall;
    
    private CharacterContactListener<CharacterBody2D, PhysicsBody2D> characterContactListener;
    private OneWayContactDisabler<PhysicsBody2D> oneWayContactDisabler;
    private BooleanHandler downHandler;

    public CharacterBody2D() {
        this(null);
    }
    
    public CharacterBody2D(OneWayContactDisabler<PhysicsBody2D> oneWayContactDisabler) {
        this(oneWayContactDisabler, null);
    }
    
    public CharacterBody2D(OneWayContactDisabler<PhysicsBody2D> oneWayContactDisabler, CharacterContactListener<CharacterBody2D, PhysicsBody2D> characterContactListener) {
        this.downHandler           = new BooleanHandler();
        if (oneWayContactDisabler == null) {
            this.oneWayContactDisabler = (PhysicsBody2D body) -> {
                return true;
            };
        } else {
            this.oneWayContactDisabler = oneWayContactDisabler;
        }
        
        if (characterContactListener == null) {
            this.characterContactListener = new CharacterContactListener<CharacterBody2D, PhysicsBody2D>() {
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
                            onCeiling = true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean trackIsOnWall(CharacterBody2D character, PhysicsBody2D platform, ContactConstraint<PhysicsBody2D> contactConstraint) {
                    AABB wAABB = character.createAABB();
                    AABB pAABB = platform.createAABB();                    
                    return Double.compare(Math.abs(pAABB.getMinY() - wAABB.getMaxY()), ERROR) > 0 && 
                           Double.compare(Math.abs(wAABB.getMinY() - pAABB.getMaxY()), ERROR) > 0;
                }
            };
        } else {
            this.characterContactListener = characterContactListener;
        }
    }

    @Override
    public void setPhysicsSpace(PhysicsSpace<PhysicsBody2D> physicsSpace) {
        if (this.physicsSpace != null && physicsSpace != null && this.physicsSpace != physicsSpace) {
            throw new IllegalStateException("This body has already been added to a physical space.");
        }
        
        if (physicsSpace == null && this.physicsSpace != null) {
            this.physicsSpace.removeStepListener(stepListener);
            this.physicsSpace.removeContactListener(contactListener);
        } else if (physicsSpace != null && this.physicsSpace == null) {
            physicsSpace.addStepListener(stepListener);
            physicsSpace.addContactListener(contactListener);
        }
        this.physicsSpace = physicsSpace;
    }
    
    public void applyDown() {
        downHandler.activate();
    }
    
    protected boolean is(PhysicsBody2D body) {
        return (body instanceof CharacterBody2D);
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

    public boolean isOnGround() {
        return onGround;
    }

    public boolean isOnCeiling() {
        return onCeiling;
    }

    public boolean isOnWall() {
        return onWall;
    }
}
