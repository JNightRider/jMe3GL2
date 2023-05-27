/* Copyright (c) 2009-2023 jMonkeyEngine.
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
package jMe3GL2.scene.tile;

/**
 * Un <code>Type</code> se encarga de enumerar los diferentes tipos de
 * datos que soporta {@link Properties}
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 1.5.0
 */
enum Type {
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Integer}
     */
    Int,
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Float}
     */
    Float, 
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Boolean}
     */
    Boolean, 
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.String}
     */
    String,
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Long}
     */
    Long,
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Double}
     */
    Double, 
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Short}
     */
    Short, 
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Byte}
     */
    Byte,
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Enum}
     */
    Enum,
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Character}
     */
    Character,
    
    /**
     * Tipo de dato: 
     *          {@link java.math.BigDecimal}
     */
    BigDecimal,
    
    /**
     * Tipo de dato: 
     *          {@link java.math.BigInteger
     */
    BigInteger,
    
    /**
     * Tipo de dato: 
     *          {@link java.util.BitSet}
     */
    BitSet,
    
    /**
     * Tipo de dato: 
     *          {@link java.nio.FloatBuffer}
     */
    FloatBuffer,
    
    /**
     * Tipo de dato: 
     *          {@link java.nio.IntBuffer}
     */
    IntBuffer,
    
    /**
     * Tipo de dato: 
     *          {@link java.nio.ByteBuffer}
     */
    ByteBuffer,
    
    /**
     * Tipo de dato: 
     *          {@link java.nio.ShortBuffer}
     */
    ShortBuffer,
    
    /**
     * Tipo de dato: 
     *          {@code  com.jme3.export.Savable
     */
    Savable;
    
    /**
     * Evalua un objeto para determinar su tipo de dato.
     * @param o Objeto a evaluer.
     * @return Tipo de dato.
     * @throws IllegalArgumentException Si el objeto que se evalua, no esta
     *                                  soportado o es <code>NULL</code>
     */
    public static Type jmeValueOf(Object o) throws IllegalArgumentException {
        if (o == null)
            throw new NullPointerException("Object is Null.");
            
        if (o instanceof java
                        .lang
                        .Character) {
            return Character;
        } else if (o instanceof java
                                .lang
                                .Integer) {
            return Int;
        } else if (o instanceof java
                                .lang
                                .Float) {
            return Float;
        } else if (o instanceof java
                                .lang
                                .Boolean) {
            return Boolean;
        } else if (o instanceof java
                                .lang
                                .String) {
            return String;
        } else if (o instanceof java
                                .lang
                                .Long) {
            return Long;
        } else if (o instanceof java
                                .lang
                                .Double) {
            return Double;
        } else if (o instanceof java
                                .lang
                                .Short) {
            return Short;
        } else if (o instanceof java
                                .lang
                                .Byte) {
            return Byte;
        } else if (o instanceof java
                                .lang
                                .Enum) {
            return Enum;
        } else if (o instanceof java
                                .math
                                .BigInteger) {
            return BigInteger;
        } else if (o instanceof java
                                .math
                                .BigDecimal) {
            return BigDecimal;
        } else if (o instanceof java
                                .util
                                .BitSet) {
            return BitSet;
        } else if (o instanceof java
                                .nio
                                .FloatBuffer) {
            return FloatBuffer;
        } else if (o instanceof java
                                .nio
                                .IntBuffer) {
            return IntBuffer;
        } else if (o instanceof java
                                .nio
                                .ByteBuffer) {
            return ByteBuffer;
        } else if (o instanceof java
                                .nio
                                .ShortBuffer) {
            return ShortBuffer;
        } else if (o instanceof com
                                .jme3
                                .export
                                .Savable) {
            return Savable;
        }
        throw new IllegalArgumentException("Object " + o.getClass().getName() + " type not supported.");
    }
}
