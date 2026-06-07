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
package org.je3gl.math;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 * A container that represents a 2D rectangle with floating values.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public final class Rect {
    /** The X coordinate of the lower-left corner of the {@code Rect}. */
    private float x;
    /** The Y coordinate of the lower-left corner of the {@code Rect}. */
    private float y;

    /** The width of the {@code Rect}. */
    private float width;
    /** The height of the {@code Rect}. */
    private float height;

    /**
     * Constructs a new {@code Rectangle} whose upper-left corner is at
     * (0,&nbsp;0) in the coordinate space, and whose width and height are both
     * zero.
     */
    public Rect() {
    }

    /**
     * Constructs a new {@code Rect} whose upper-left corner is specified as
     * {@code (x,y)} and whose width and height are specified by the arguments
     * of the same name.
     *
     * @param x the specified X coordinate
     * @param y the specified Y coordinate
     * @param width the width of the {@code Rect}
     * @param height the height of the {@code Rect}
     * @since 1.0
     */
    public Rect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the value of the {@code x} attribute
     *
     * @param x float
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Sets the value of the {@code y} attribute
     *
     * @param y float
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Sets the value of the {@code width} attribute
     *
     * @param width float
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Sets the value of the {@code height} attribute
     *
     * @param height float
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Returns the value of the attribute {@code x}
     *
     * @return float
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the value of the attribute {@code y}
     *
     * @return float
     */
    public float getY() {
        return y;
    }

    /**
     * Returns the value of the attribute {@code width}
     *
     * @return float
     */
    public float getWidth() {
        return width;
    }

    /**
     * Returns the value of the attribute {@code height}
     *
     * @return float
     */
    public float getHeight() {
        return height;
    }

    /**
     * Method responsible for calculating the camera position within the
     * clipping ranges.
     *
     * @param loc position
     * @param offset offset
     * @return new calculated position.
     */
    public Vector2f clipping(Vector3f loc, Vector2f offset) {
        return new Vector2f(FastMath.clamp((loc.x + offset.x), x, width),
                            FastMath.clamp((loc.y + offset.y), y, height));
    }

    /**
     * Returns a {@code String} representing this {@code Rect} and its values.
     *
     * @return a {@code String} representing this {@code Rect} object's
     * coordinate and size values.
     */
    @Override
    public String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
    }
}
