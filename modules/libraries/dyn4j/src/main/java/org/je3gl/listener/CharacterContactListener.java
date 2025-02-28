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
package org.je3gl.listener;

import org.je3gl.physics.control.CharacterBody2D;
import org.je3gl.physics.control.PhysicsBody2D;
import org.je3gl.util.Serializable;
import org.dyn4j.dynamics.contact.ContactConstraint;

/**
 * Interface in charge of managing a listener for the contacts that a 
 * character ({@link org.je3gl.physics.control.CharacterBody2D}) makes in physical space.
 * 
 * @param <A> type {@link org.je3gl.physics.control.CharacterBody2D}
 * @param <B> type {@link org.je3gl.physics.control.PhysicsBody2D}
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public interface CharacterContactListener<A extends CharacterBody2D, B extends PhysicsBody2D> extends Serializable {
    
    /**
     * Determines if the character is making contact with the ground|floor.
     * 
     * @param character the character
     * @param platform the contact platform
     * @param contactConstraint contact-constraint
     * @return boolean
     */
    boolean trackIsOnGround(A character, B platform, ContactConstraint<B> contactConstraint);
    
    /**
     * Determines if the character is making contact with the ceiling.
     * 
     * @param character the character
     * @param platform the contact platform
     * @param contactConstraint contact-constraint
     * @return boolean
     */
    boolean trackIsOnCeiling(A character, B platform, ContactConstraint<B> contactConstraint);
    
    /**
     * Determines if the character is making contact with a wall
     * 
     * @param character the character
     * @param platform the contact platform
     * @param contactConstraint contact-constraint
     * @return boolean
     */
    boolean trackIsOnWall(A character, B platform, ContactConstraint<B> contactConstraint);
}
