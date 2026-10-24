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
package org.je3gl.box2d.control.mover;

import com.jme3.math.Vector2f;
import org.box2d.jni.b2Vec2;
import org.box2d.jni.system.ArenaAlloc;

/**
 * Class responsible for managing the information required by a
 * {@code CharacterBody2D} to determine its physical shape and move it.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class MoverBounds {
    /** the {@link Center1Fcn}. */
    private Center1Fcn center1Fcn;
    /** the {@link RadiusFcn} */
    private RadiusFcn radiusFcn;

    /**
     * The center of the body's physical form that is to be moved
     */
    private Vector2f center1;

    /**
     * The radius that the body occupies in physical space
     */
    private float radius;

    /**
     * Constructor
     *
     * @param center1Fcn Center1Fcn
     * @param radiusFcn RadiusFcn
     */
    public MoverBounds(Center1Fcn center1Fcn, RadiusFcn radiusFcn) {
        this.center1Fcn = center1Fcn;
        this.radiusFcn = radiusFcn;
    }

    /**
     * Constructor.
     *
     * @param center1 b2Vec2
     * @param radius float
     */
    public MoverBounds(b2Vec2 center1, float radius) {
        this.center1 = new Vector2f(center1.x(), center1.y());
        this.radius = radius;
    }

    /**
     * Returns the center point
     *
     * @param arena ArenaAlloc
     * @return b2Vec2
     */
    public b2Vec2 center1(ArenaAlloc arena) {
        if (center1Fcn == null) {
            return b2Vec2.calloc(arena).set(center1.x, center1.y);
        }
        return center1Fcn.center1(arena);
    }

    /**
     * Return the radius
     *
     * @return radius
     */
    public float radius() {
        if (radiusFcn == null) {
            return radius;
        }
        return radiusFcn.radius();
    }
}
