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

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.util.BufferUtils;
import com.jme3.util.clone.Cloner;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.BitSet;
import java.util.Objects;

/**
 * An <code>Property</code> is responsible for managing the types of data that
 * can be stored by the class {@link Properties} by creating a {@link Tile}.
 * 
 * @author wil
 * @version 1.0.5
 * @since 2.0.0
 */
final class Property implements Savable, Cloneable {
    
    /** Type of data being wrapped. */
    private Type jmeType;    
    /** Primitive data to be wrapped. */
    private Object object;
    
    /**
     * Default constructor.
     */
    public Property() {
    }

    /**
     * Constructs a {@link Property} with a specified value to wrap.
     * 
     * @param object primitive value to be wrapped
     */
    public Property(Object object) throws RuntimeException {
        this.jmeType = Type.jmeValueOf(object);
        this.object  = object;
    }
    
    /**
     * Whenever we clone a {@code JmePrimitive} it will be done in a deep way,
     * to clone its value and data type
     * 
     * @throws InternalError if an internal(JVM) error occurs when cloning the
     *          object/class
     * @return clone of the generated object
     */
    @Override
    public Property clone() {
        try {
            Property clon = (Property) super.clone();            
            clon.jmeType  = jmeType;
            clon.object   = clone0(object, jmeType);
            
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    
    /**
     * Auxiliary method that is in charge of cloning the value of this class, as
     * long as it supports the cloning, the primitive ones are an exception to
     * that rule, since they are only assigned.
     * 
     * @param obj value to be cloned
     * @param type the type of the object to be cloned
     * @return cloned value
     */
    private Object clone0(Object obj, Type type) {
        switch (type) {
            case BitSet:       return (BitSet) ((BitSet) obj).clone();
            case FloatBuffer:  return BufferUtils.clone((FloatBuffer) obj);
            case ShortBuffer:  return BufferUtils.clone((ShortBuffer) obj);            
            case ByteBuffer:   return BufferUtils.clone((ByteBuffer) obj);
            case IntBuffer:    return BufferUtils.clone((IntBuffer) obj);
            case Savable:
                final Cloner cloner = new Cloner();
                if (obj instanceof Cloneable) {
                    return cloner.clone(obj);
                }
                return obj;
            default:
                return obj;
        }
    }

    /**
     * Generates a text ({@code String}) with the behavior of class
     * {@code JmePrimitive}.
     * 
     * @return a string as value.
     */
    @Override
    public String toString() {
        return '[' + "jmeType="  + jmeType 
                   + ", object=" + object + ']';
    }

    // Getters of the 'JmePrimitive' class that return the type of data being
    // wrapped, as well as the data set.
    
    /**
     * Gets the type of data being wrapped.
     * 
     * @return type of data
     */
    public Type getJmeType() {
        return jmeType;
    }
    
    /**
     * Gets the wrapped data.
     * 
     * @return the wrapped data
     */
    public Object getValue() {
        return object;
    }

    /**
     * (non-JavaDoc).
     * @param ex JmeExporter
     * @throws IOException exception
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        final OutputCapsule out = ex.getCapsule(this);
        
        out.write(jmeType, "JmeType", null);
        
        switch(jmeType) {
            case Boolean:
                boolean bool = (boolean) object;
                out.write(bool, "boolVal", false);
                break;
            case Byte:
                byte bit = (byte) object;
                out.write(bit, "bitVal", (byte)0);
                break;
            case Character:
                char car = (char) object;
                out.write(String.valueOf(car), "charVal", null);
                break;
            case Double:
                double d = (double) object;
                out.write(d, "doubleVal", Double.NaN);
                break;
            case Enum:
                Enum en = (Enum) object;
                Class className = en.getDeclaringClass();
                
                out.write(className.getName(), "className", null);
                out.write(en, "enumVal", (Enum)null);
                break;
            case Float:
                float f = (float) object;
                out.write(f, "floatVal", Float.NaN);
                break;
            case Int:
                int in = (int) object;
                out.write(in, "intVal", 0);
                break;
            case Long:
                long l = (long) object;
                out.write(l, "longVal", 0L);
                break;
            case Short:
                short s = (short) object;
                out.write(s, "shortVal", (short) 0);
                break;
            case String:
                String stl = (String) object;
                out.write(stl, "stringVal", null);
                break;
            case BigDecimal:
                BigDecimal decimal = (BigDecimal) object;
                BigInteger toint   = decimal.unscaledValue();
                
                out.write(decimal.scale(), "scaledValue", 0);
                out.write(toint.toByteArray(), "bigDecimal", null);                
                break;
            case BigInteger:
                BigInteger integer = (BigInteger) object;
                out.write(integer.toByteArray(), "bigInteger", null);
                break;
            case BitSet:
                BitSet bitSet = (BitSet) object;
                out.write(bitSet, "bitSet", null);
                break;
            case ByteBuffer:
                ByteBuffer byteBuffer = (ByteBuffer) object;
                out.write(byteBuffer, "byteBuffer", null);
                break;
            case FloatBuffer:
                FloatBuffer floatBuffer = (FloatBuffer) object;
                out.write(floatBuffer, "floatBuffer", null);
                break;
            case IntBuffer:
                IntBuffer intBuffer = (IntBuffer) object;
                out.write(intBuffer, "intBuffer", null);
                break;
            case ShortBuffer:
                ShortBuffer shortBuffer = (ShortBuffer) object;
                out.write(shortBuffer, "shortBuffer", null);
                break;
            case Savable:
                Savable savable = (Savable) object;
                out.write(savable, "savableVal", null);
                break;
            default:
                throw new AssertionError();
        }
    }

    /**
     * (non-JavaDoc).
     * @param im JmeImporter
     * @throws IOException exception.
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        final InputCapsule in = im.getCapsule(this);
        
        jmeType = in.readEnum("JmeType", Type.class, null);
        
        switch(jmeType) {
            case Boolean:
                object = in.readBoolean("boolVal", false);
                break;
            case Byte:
                object = in.readByte("bitVal", (byte) 0);
                break;
            case Character:
                object = readCharacter(in);
                break;
            case Double:
                object = in.readDouble("doubleVal", Double.NaN);
                break;
            case Enum:
                object = readEnum(in);
                break;
            case Float:
                object = in.readFloat("floatVal", Float.NaN);
                break;
            case Int:
                object = in.readInt("intVal", 0);
                break;
            case Long:
                object = in.readLong("longVal", 0L);
                break;
            case Short:
                object = in.readShort("shortVal", (short) 0);
                break;
            case String:
                object = in.readString("stringVal", null);
                break;
            case BigDecimal:                
                object = readBigNumber(in, true);
                break;
            case BigInteger:
                object = readBigNumber(in, false);
                break;
            case BitSet:
                object = in.readBitSet("bitSet", null);
                break;
            case ByteBuffer:
                object = in.readByteBuffer("byteBuffer", null);
                break;
            case FloatBuffer:
                object = in.readFloatBuffer("floatBuffer", null);
                break;
            case IntBuffer:
                object = in.readIntBuffer("intBuffer", null);
                break;
            case ShortBuffer:
                object = in.readShortBuffer("shortBuffer", null);
                break;
            case Savable:
                object = in.readSavable("savableVal", null);
                break;
            default:
                throw new AssertionError();
            }
    }
    
    /**
     * The <code>readCharacter</code> method has the functionality to read an
     * object of type {@code char}.
     * 
     * @param in JME ticket manager
     * @return a character ({@code char}) as value
     * 
     * @throws IOException if, when reading the data in the input, it generates
     *                      an error or exception
     * @throws InternalError if the value read has more than one character, it
     *                       is considered an internal error, because we are 
     *                      importing a {@code char}
     */
    private char readCharacter(InputCapsule in) throws IOException {
        final String valueOf = in.readString("charVal", null);
        
        if (valueOf == null 
                || valueOf.isEmpty()) {
            return '\u0000';
        }
        
        if (valueOf.length() == 1) {
            return valueOf.charAt(0);
        }
        throw new InternalError("Character.");
    }
    
    /**
     * The <code>readEnum</code> method has the functionality to read an object
     * of type {@code enum}.
     * 
     * @param in JME ticket manager
     * @return an enumerated value {@link java.lang.Enum}
     * 
     * @throws IOException if, when reading the data in the input, it generates
     *                      an error or exception
     * @throws InternalError if the values requested to read an {@code enum} are
     *                      not valid, an internal error will be thrown
     */
    @SuppressWarnings("unchecked")
    private Enum<?> readEnum(InputCapsule in) throws IOException {
        try {
            String className = in.readString("className", null);
            Class forName    = Class.forName(className);
            
            return in.readEnum("enumVal", forName, null);
        } catch (ClassNotFoundException e) {
            throw new InternalError(e);
        }
    }
    
    
    /**
     * The <code>readBigNumber</code> method has as functionality, read an
     * integer or decimal number of large capacity known as:
     * <pre><code>
     * BigDecimal --- decimals
     * BigInteger --- integers
     * </code></pre>
     * 
     * @param in JME ticket manager
     * @param isDecimal {@code true} if you are going to read a <code>BigDecimal</code>,
     *                  otherwise {@code false} to a <code>BigInteger</code>
     * @return a giant/large number as a value
     * 
     * @throws IOException if, when reading the data in the input, it generates
     *                      an error or exception
     * @throws InternalError if the requested values are not <code>BigDecimal</code>
     *          or <code>BigInteger</code>, an internal error will be thrown
     */
    private Number readBigNumber(InputCapsule in, boolean isDecimal) throws IOException {
        if (isDecimal) {
            byte[] decimalBits = in.readByteArray("bigDecimal", null);
            int scaleBits      = in.readInt("scaledValue", 0);
            
            if (decimalBits == null)
                throw new InternalError("BigDecimal.");
            
            BigInteger bigInt = new BigInteger(decimalBits);
            return new BigDecimal(bigInt, scaleBits, MathContext.UNLIMITED);
        } else {
            byte[] intBits = in.readByteArray("bigInteger", null);
            
            if (intBits == null)
                throw new InternalError("BigInteger.");
            
            return new BigInteger(intBits);
        }
    }

    /**
     * Method in charge of generating a hash for the class.
     * 
     * @return {@code hashCode} of the class
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.jmeType);
        hash = 43 * hash + Objects.hashCode(this.object);
        return hash;
    }

    /**
     * Method in charge of comparing an object to then determine if they are
     * equal at the content level or not.
     * 
     * @param obj an object to check
     * @return {@code true} if they are the same, returns {@code false} as value
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Property other = (Property) obj;
        if (this.jmeType != other.jmeType) {
            return false;
        }
        return Objects.equals(this.object, other.object);
    }
}
