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
package org.je3gl.scene.tile;

/**
 * A <code>Type</code> is responsible for listing the different types of data
 * that supports {@link Properties}.
 * 
 * @author wil
 * @version 1.0.5
 * @since 2.0.0
 */
@Deprecated(since = "3.1.0")
enum Type {
    
    /**
     * Data type: 
     *          {@link java.lang.Integer}
     */
    Int,
    
    /**
     * Data type: 
     *          {@link java.lang.Float}
     */
    Float, 
    
    /**
     * Data type: 
     *          {@link java.lang.Boolean}
     */
    Boolean, 
    
    /**
     * Data type: 
     *          {@link java.lang.String}
     */
    String,
    
    /**
     * Data type: 
     *          {@link java.lang.Long}
     */
    Long,
    
    /**
     * Data type: 
     *          {@link java.lang.Double}
     */
    Double, 
    
    /**
     * Data type: 
     *          {@link java.lang.Short}
     */
    Short, 
    
    /**
     * Data type: 
     *          {@link java.lang.Byte}
     */
    Byte,
    
    /**
     * Data type: 
     *          {@link java.lang.Enum}
     */
    Enum,
    
    /**
     * Data type: 
     *          {@link java.lang.Character}
     */
    Character,
    
    /**
     * Data type: 
     *          {@link java.math.BigDecimal}
     */
    BigDecimal,
    
    /**
     * Data type: 
     *          {@link java.math.BigInteger
     */
    BigInteger,
    
    /**
     * Data type: 
     *          {@link java.util.BitSet}
     */
    BitSet,
    
    /**
     * Data type: 
     *          {@link java.nio.FloatBuffer}
     */
    FloatBuffer,
    
    /**
     * Data type: 
     *          {@link java.nio.IntBuffer}
     */
    IntBuffer,
    
    /**
     * Data type: 
     *          {@link java.nio.ByteBuffer}
     */
    ByteBuffer,
    
    /**
     * Data type: 
     *          {@link java.nio.ShortBuffer}
     */
    ShortBuffer,
    
    /**
     * Data type: 
     *          {@code  com.jme3.export.Savable
     */
    Savable;
    
    /**
     * Evaluates an object to determine its data type.
     * 
     * @param o object to be evaluated
     * @return data type
     * @throws IllegalArgumentException if the object to be evaluated is not
     *                                  supported or is <code>NULL</code>.
     */
    public static Type jmeValueOf(Object o) throws IllegalArgumentException {
        if (o == null)
            throw new NullPointerException("Object is Null.");
            
        if (o instanceof java.lang.Character) {
            return Character;
        } else if (o instanceof java.lang.Integer) {
            return Int;
        } else if (o instanceof java.lang.Float) {
            return Float;
        } else if (o instanceof java.lang.Boolean) {
            return Boolean;
        } else if (o instanceof java.lang.String) {
            return String;
        } else if (o instanceof java.lang.Long) {
            return Long;
        } else if (o instanceof java.lang.Double) {
            return Double;
        } else if (o instanceof java.lang.Short) {
            return Short;
        } else if (o instanceof java.lang.Byte) {
            return Byte;
        } else if (o instanceof java.lang.Enum) {
            return Enum;
        } else if (o instanceof java.math.BigInteger) {
            return BigInteger;
        } else if (o instanceof java.math.BigDecimal) {
            return BigDecimal;
        } else if (o instanceof java.util.BitSet) {
            return BitSet;
        } else if (o instanceof java.nio.FloatBuffer) {
            return FloatBuffer;
        } else if (o instanceof java.nio.IntBuffer) {
            return IntBuffer;
        } else if (o instanceof java.nio.ByteBuffer) {
            return ByteBuffer;
        } else if (o instanceof java.nio.ShortBuffer) {
            return ShortBuffer;
        } else if (o instanceof com.jme3.export.Savable) {
            return Savable;
        }
        throw new IllegalArgumentException("Object " + o.getClass().getName() + " type not supported.");
    }
}
