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
package jme3gl2.scene.tile;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An <code>Properties</code> is responsible for storing the properties of a
 * {@link jme3gl2.scene.tile.Tile}.
 * 
 * @author wil
 * @version 1.0.1
 * @since 2.0.0
 */
@SuppressWarnings(value = {"unchecked"})
public class Properties implements Savable, Cloneable {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(Properties.class.getName());
    
    /** Map of properties. */
    private HashMap<String, Savable> properties;    
    /** Version that has the class {@code Properties} currently. */
    public static final int SAVABLE_VERSION = 2;
    
    /**
     * Default constructor.
     */
    public Properties() {
        this.properties = new HashMap<>();
    }
    
    /**
     * Gets the value object associated with a key.
     *
     * @param key string key
     * @return the object associated with the key
     * 
     * @throws RuntimeException if the key is not found
     */
    public Object get(String key) throws RuntimeException {
        if (key == null) {
            throw new RuntimeException("Null key.");
        }
        Object object = this.opt(key);
        if (object == null) {
            throw new RuntimeException("JmeProperties[" + quote(key) + "] not found.");
        }
        return object;
    }
    
    /**
     * Gets the enum value associated with a key.
     *
     * @param <E> enumerated type
     * @param key string key
     * @return the enumeration value associated with the key
     * 
     * @throws RuntimeException if the key is not found or if the value cannot
     *          be converted to an enumeration
     */
    public <E extends Enum<E>> E getEnum(String key) throws RuntimeException {
        E val = optEnum(key);
        if(val==null) {
            /*
            JmeException should really take a throwable argument.
            If it did, I would re-implement it with Enum.valueOf method and
            place any exception thrown in JmeException.
            */
            throw wrongValueFormatException(key, "enum of type " + quote("null"), opt(key), null);
        }
        return val;
    }

    /**
     * Gets the boolean value associated with a key.
     *
     * @param key string key
     * @return true or false value
     * 
     * @throws RuntimeException if the value is not a boolean or the string
     *          "true" or "false"
     */
    public boolean getBoolean(String key) throws RuntimeException {
        Object object = this.get(key);
        if (object.equals(Boolean.FALSE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("false"))) {
            return false;
        } else if (object.equals(Boolean.TRUE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("true"))) {
            return true;
        }
        throw wrongValueFormatException(key, "Boolean", object, null);
    }
    
    /**
     * Gets the BigInteger value associated with a key.
     *
     * @param key string key
     * @return the numerical value
     * 
     * @throws RuntimeException if the key is not found or if the value cannot
     *          be converted to BigInteger
     */
    public BigInteger getBigInteger(String key) throws RuntimeException {
        Object object = this.get(key);
        BigInteger ret = objectToBigInteger(object, null);
        if (ret != null) {
            return ret;
        }        
        throw wrongValueFormatException(key, "BigInteger", object, null);
    }
    
    /**
     * Gets the BigDecimal value associated with a key. If the value is a float
     * or double, the constructor {@link BigDecimal#BigDecimal(double)} will be
     * used. See the notes on the constructor for conversion problems that may
     * increase.
     *
     * @param key string key
     * @return the numerical value
     * 
     * @throws RuntimeException if the key is not found or if the value cannot
     *          be converted to BigDecimal
     */
    public BigDecimal getBigDecimal(String key) throws RuntimeException {
        Object object = this.get(key);
        BigDecimal ret = objectToBigDecimal(object, null);
        if (ret != null) {
            return ret;
        }
        throw wrongValueFormatException(key, "BigDecimal", object, null);
    }
    
    /**
     * Gets the BitSet value associated with a key.
     *
     * @param key string key
     * @return an object as a value
     * 
     * @throws RuntimeException if the key is not found or if the value cannot
     *          be converted to BitSet
     */
    public BitSet getBitSet(String key) throws RuntimeException {
        final Object object = this.get(key);
        if (object 
                instanceof BitSet) {
            return (BitSet) object;
        } else if (object 
                        instanceof ByteBuffer) {
            return BitSet.valueOf((ByteBuffer)
                                    object);
        }
        
        throw wrongValueFormatException(key, "BitSet", object, null);
    }
    
    /**
     * Gets a FloatBuffer associated with a key.
     *
     * @param key string key
     * @return a buffer which is the value
     * 
     * @throws RuntimeException if there is no buffer value for the key
     */
    public FloatBuffer getFloatBuffer(String key) throws RuntimeException {
        final Object object = this.get(key);
        if (object instanceof FloatBuffer) {
            return (FloatBuffer) object;
        }
        throw wrongValueFormatException(key, "FloatBuffer", object, null);
    }
    
    /**
     * Gets an IntBuffer associated with a key.
     *
     * @param key string key
     * @return a buffer which is the value
     * 
     * @throws RuntimeException if there is no buffer value for the key
     */
    public IntBuffer getIntBuffer(String key) throws RuntimeException {
        final Object object = this.get(key);
        if (object instanceof IntBuffer) {
            return (IntBuffer) object;
        }
        throw wrongValueFormatException(key, "IntBuffer", object, null);
    }
    
    /**
     * Gets a ByteBuffer associated with a key.
     *
     * @param key string key
     * @return a buffer which is the value
     * 
     * @throws RuntimeException if there is no buffer value for the key
     */
    public ByteBuffer getByteBuffer(String key) throws RuntimeException {
        final Object object = this.get(key);
        if (object instanceof ByteBuffer) {
            return (ByteBuffer) object;
        }
        throw wrongValueFormatException(key, "ByteBuffer", object, null);
    }
    
    /**
     * Gets a ShortBuffer associated with a key.
     *
     * @param key string key
     * @return a buffer which is the value
     * 
     * @throws RuntimeException if there is no buffer value for the key
     */
    public ShortBuffer getShortBuffer(String key) throws RuntimeException {
        final Object object = this.get(key);
        if (object instanceof ShortBuffer) {
            return (ShortBuffer) object;
        }
        throw wrongValueFormatException(key, "ShortBuffer", object, null);
    }
    
    /**
     * Gets the double value associated with a key.
     *
     * @param key string key
     * @return numerical value
     * 
     * @throws RuntimeException if the key is not found or if the value is not
     *          an object number and cannot be converted to a number.
     */
    public double getDouble(String key) throws RuntimeException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "double", object, e);
        }
    }
    
    /**
     * Gets the float value associated with a key.
     *
     * @param key string key
     * @return the numerical value
     * 
     * @throws RuntimeException if the key is not found or if the value is not
     *          an object number and cannot be converted to a number
     */
    public float getFloat(String key) throws RuntimeException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).floatValue();
        }
        try {
            return Float.parseFloat(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "float", object, e);
        }
    }
    
    /**
     * Gets the Number value associated with a key.
     *
     * @param key string key
     * @return the numerical value
     * 
     * @throws RuntimeException if the key is not found or if the value is not
     *          an object number and cannot be converted to a number
     */
    public Number getNumber(String key) throws RuntimeException {
        Object object = this.get(key);
        try {
            if (object instanceof Number) {
                return (Number)object;
            }
            return stringToNumber(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "number", object, e);
        }
    }
    
    /**
     * Gets the int value associated with a key.
     *
     * @param key string key
     * @return integer value
     * 
     * @throws RuntimeException if the key is not found or if the value cannot
     *          be converted to an integer
     */
    public int getInt(String key) throws RuntimeException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "int", object, e);
        }
    }
    
    /**
     * Gets the long value associated with a key.
     *
     * @param key string key
     * @return long value
     * 
     * @throws RuntimeException if the key is not found or if the value cannot
     *          be converted to a long
     */
    public long getLong(String key) throws RuntimeException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "long", object, e);
        }
    }
    
    /**
     * Gets the short value associated with a key.
     *
     * @param key string key
     * @return short value
     * 
     * @throws RuntimeException if the key is not found or if the value cannot
     * b        e converted to a short
     */
    public short getShort(String key) throws RuntimeException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).shortValue();
        }
        try {
            return Short.parseShort(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "short", object, e);
        }
    }
    
    /**
     * Gets the byte value associated with a key.
     *
     * @param key string key
     * @return byte value
     * 
     * @throws RuntimeException if the key is not found or if the value cannot
     *          be converted to a byte
     */
    public byte getByte(String key) throws RuntimeException {
        final Object object = this.get(key);
        if(object instanceof Byte) {
            return (byte) object;
        } else {
            if (object instanceof Number) {
                return ((Number) object).byteValue();
            }
        }
        try {
            return Byte.parseByte(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "byte", object, e);
        }
    }
    
    /**
     * Gets the char value associated with a key.
     *
     * @param key string key
     * @return char value
     * @throws RuntimeException if the key is not found or if the value cannot
     *          be converted to a char
     */
    public char getChar(String key) throws RuntimeException {
        final Object object = this.get(key);
        if (object instanceof Character) {
            return (char) object;
        } else {
            if (object instanceof String) {
                if (!((String) object).isEmpty() 
                        && ((String) object).length() == 1) {
                    return ((String) object).charAt(0);
                }
            }
        }
        
        throw wrongValueFormatException(key, "char", object, null);
    }
    
    /**
     * Gets a String associated with a key.
     *
     * @param key string key
     * @return a String that is the value
     * @throws RuntimeException if there is no string value for the key
     */
    public String getString(String key) throws RuntimeException {
        Object object = this.get(key);
        if (object 
                instanceof String) {
            return (String) object;
        }
        throw wrongValueFormatException(key, "string", object, null);
    }
    
    /**
     * Gets a Savable associated with a key.
     *
     * @param <E> savable type
     * @param key string key
     * @return a Savable object as a value
     * @throws RuntimeException if there is no Savable value for the key
     */
    public <E extends Savable> E getSavable(String key) throws RuntimeException {
        Object object = this.get(key);
        if (object instanceof Savable) {
            return (E) object;
        }
        throw wrongValueFormatException(key, "savable", object, null);
    }
    
    /**
     * Determine if JmeProperties contains a specific key.
     *
     * @param key string key
     * @return true if the key exists in JmeProperties
     */
    public boolean has(String key) {
        return this.properties.containsKey(key);
    }

    /**
     * Determine if the value associated with the key is <code>null</code> or
     * if there is no value.
     *
     * @param key string key
     * @return true if there is no value associated with the key or if the value
     *          is the object JmeProperties.NULL or JmeNull
     */
    public boolean isNull(String key) {
        final Object object = this.opt(key);
        return object == null;
    }
    
    /**
     * Gets an enumeration of the keys of the JmeProperties. Modifying this set
     * of keys also modifies the JmeProperties. Use with caution.
     *
     * @see Set#iterator()
     * @return a key iterator
     */
    public Iterator<String> keys() {
        return this.keySet().iterator();
    }

    /**
     * Gets an enumeration of the keys of the JmeProperties. Modifying this set
     * of keys also modifies the JmeProperties. Use with caution.
     *
     * @see Map#keySet()
     * @return a set of keys
     */
    public Set<String> keySet() {
        return this.properties.keySet();
    }

    /**
     * Gets a set of entries from JmeProperties. These are raw values and may
     * not match what is returned by the get* and opt* functions of JmeProperties.
     * Modifying the returned EntrySet or the Entry objects contained in it will
     * modify the JmeProperties. This does not return a clone or a read-only view.
     * Use with caution.
     *
     * @see Map#entrySet()
     * @return an input set
     */
    protected Set<Entry<String, Savable>> entrySet() {
        return this.properties.entrySet();
    }

    /**
     * Gets the number of keys stored in JmeProperties.
     * @return the number of keys in the JmeProperties
     */
    public int length() {
        return this.properties.size();
    }

    /**
     * Removes all elements of this JmeProperties.
     * JmeProperties will be empty after this call returns.
     */
    public void clear() {
        this.properties.clear();
    }

    /**
     * Checks if JmeProperties is empty.
     * @return true if the JmeProperties is empty, otherwise false
     */
    public boolean isEmpty() {
        return this.properties.isEmpty();
    }
    
    /**
     * Produces a string from a number
     *
     * @param number a number
     * @return a string
     * @throws RuntimeException if it's not a non-finite number
     */
    protected static String numberToString(Number number) throws RuntimeException {
        if (number == null) {
            throw new RuntimeException("Null pointer");
        }
        testValidity(number);

        // Remove trailing zeros and decimal point, if possible

        String string = number.toString();
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0
                && string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    /**
     * Gets an optional value associated with a key
     *
     * @param key string key
     * @return an object that is the value, or null if there is no value
     */
    public Object opt(String key) {
        if (key == null)
            return null;
        
        final Object object = this.properties.get(key);
        if (object instanceof Property) {
            return ((Property) object).getValue();
        } else {
            if (object == null) {
                return null;
            }
        }        
        return object;
    }
    
    /**
     * Gets the enumeration value associated with a key
     *
     * @param <E> enum type
     * @param key string key
     * @return the enumerated value associated with the key or null if not found
     */
    public <E extends Enum<E>> E optEnum(String key) {
        return this.optEnum(key, null);
    }
    
    /**
     * Gets the enumeration value associated with a key.
     *
     * @param <E> enum type
     * @param key string key
     * @param defaultValue the default value in case the value is not found
     * 
     * @return the enumeration value associated with the key or defaultValue if
     *          the value is not found or cannot be assigned to <code>class</code>
     */
    public <E extends Enum<E>> E optEnum(String key, E defaultValue) {
        final Object val = this.opt(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Enum) {
            // We just checked it out!
            @SuppressWarnings("unchecked")
            E myE = (E) val;
            return myE;
        }
        return defaultValue;
    }
    
    /**
     * Gets an optional boolean associated with a key. Returns false if no such
     * key exists, or if the value is not Boolean.
     *
     * @param key string key
     * @return true or false value
     */
    public boolean optBoolean(String key) {
        return this.optBoolean(key, false);
    }
    
    /**
     * Gets an optional boolean associated with a key. Returns the default value
     * if no such key exists, or if it is not a boolean or the String "true" or
     * "false" (case insensitive).
     *
     * @param key string key
     * @param defaultValue default value
     * @return true or false value
     */
    public boolean optBoolean(String key, boolean defaultValue) {
        Object val = this.opt(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Boolean){
            return ((boolean) val);
        }
        try {
            // We will use get anyway because it does string conversion
            return this.getBoolean(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Gets an optional BigDecimal associated with a key, or null if no such key
     * exists or if its value is not a number. If the value is a string, an
     * attempt will be made to evaluate it as a number. If the value is a float
     * or double, then the constructor {@link BigDecimal#BigDecimal(double)}
     * will be used. See notes on the constructor for conversion problems that
     * may arise.
     *
     * @param key string key
     * @return an object that is the value
     */
    public BigDecimal optBigDecimal(String key) {
        return this.optBigDecimal(key, null);
    }
    
    /**
     * Gets an optional BigDecimal associated with a key, or the default value
     * if no such key exists or if its value is not a number. If the value is a
     * string, an attempt will be made to evaluate it as a number. If the value
     * is a float or double, then the constructor {@link BigDecimal#BigDecimal(double)}
     * will be used. See notes on the constructor for conversion problems that
     * may arise.
     *
     * @param key string key
     * @param defaultValue default value
     * @return an object that is the value
     */
    public BigDecimal optBigDecimal(String key, BigDecimal defaultValue) {
        Object val = this.opt(key);
        return objectToBigDecimal(val, defaultValue);
    }
    
    /**
     * Converts an object to BigDecimal.
     * 
     * @param val value to convert
     * @param defaultValue the default value that is returned when conversion
     * does not work or is null
     * @return BigDecimal conversion of the original value, or the default value
     *          if it cannot be converted
     */
    protected static BigDecimal objectToBigDecimal(Object val, BigDecimal defaultValue) {
        return objectToBigDecimal(val, defaultValue, true);
    }
    
    /**
     * Converts an object to BigDecimal.
     * 
     * @param val value to convert
     * @param defaultValue the default value that is returned when conversio
     * does not work or is null
     * @param exact When <code>true</code>, the values {@link Double} and {@link Float}
     * will be converted exactly. When <code>false</code>, will be converted to
     * {@link String} value before converting to {@link BigDecimal}
     * 
     * @return BigDecimal conversion of the original value, or the default value
     *          if it cannot be converted
     */
    protected static BigDecimal objectToBigDecimal(Object val, BigDecimal defaultValue, boolean exact) {
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof BigDecimal){
            return (BigDecimal) val;
        }
        if (val instanceof BigInteger){
            return new BigDecimal((BigInteger) val);
        }
        if (val instanceof Double || val instanceof Float){
            if (!numberIsFinite((Number)val)) {
                return defaultValue;
            }
            if (exact) {
                return new BigDecimal(((Number)val).doubleValue());
            } else {
                
                // Using the string constructor to hold "nice" values for double
                // and float, the double constructor will translate the doubles to
                // "exact" values instead of the likely intended representation.
                return new BigDecimal(val.toString());
            }
        }
        if (val instanceof Long || val instanceof Integer
                || val instanceof Short || val instanceof Byte){
            return new BigDecimal(((Number) val).longValue());
        }
        // Do not check if it is a string in case of unchecked subclasses of numbers.
        try {
            return new BigDecimal(String.valueOf(val));
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * Gets an optional BigInteger associated with a key, or null if no such key
     * exists or if its value is not a number. If the value is a string, an
     * attempt will be made to evaluate it as a number.
     *
     * @param key string key
     * @return an object that is the value
     */
    public BigInteger optBigInteger(String key) {
        return this.optBigInteger(key, null);
    }
    
    /**
     * Gets an optional BigInteger associated with a key, or the default value
     * if no such key exists or if its value is not a number. If the value is a
     * string, an attempt will be made to evaluate it as a number.
     *
     * @param key string key
     * @param defaultValue default value
     * @return an object that is the value
     */
    public BigInteger optBigInteger(String key, BigInteger defaultValue) {
        Object val = this.opt(key);
        return objectToBigInteger(val, defaultValue);
    }

    
    /**
     * Converts an object to BigDecimal.
     * 
     * @param val value to convert
     * @param defaultValue the default value that is returned when the conversion
     * does not work or is null
     * 
     * @return BigInteger conversion of the original value, or the default value
     *                     if it cannot be converted
     */
    protected static BigInteger objectToBigInteger(Object val, BigInteger defaultValue) {
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof BigInteger){
            return (BigInteger) val;
        }
        if (val instanceof BigDecimal){
            return ((BigDecimal) val).toBigInteger();
        }
        if (val instanceof Double || val instanceof Float){
            if (!numberIsFinite((Number)val)) {
                return defaultValue;
            }
            return new BigDecimal(((Number) val).doubleValue()).toBigInteger();
        }
        if (val instanceof Long || val instanceof Integer
                || val instanceof Short || val instanceof Byte){
            return BigInteger.valueOf(((Number) val).longValue());
        }
        // Do not check if it is a string in case of unchecked subclasses of numbers
        try {
            final String valStr = String.valueOf(val);
            if(isDecimalNotation(valStr)) {
                return new BigDecimal(valStr).toBigInteger();
            }
            return new BigInteger(valStr);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * Gets an optional double associated with a key, or NaN if no such key
     * exists or if its value is not a number. If the value is a string, an
     * attempt will be made to evaluate it as a number.
     *
     * @param key string key
     * @return the value
     */
    public double optDouble(String key) {
        return this.optDouble(key, Double.NaN);
    }

    /**
     * Gets an optional double associated with a key, or the default value if no
     * such key exists or if its value is not a number. If the value is a string,
     * an attempt will be made to evaluate it as a number.
     *
     * @param key string key
     * @param defaultValue default value
     * @return the value
     */
    public double optDouble(String key, double defaultValue) {
        Number val = this.optNumber(key);
        if (val == null) {
            return defaultValue;
        }
        final double doubleValue = val.doubleValue();
        return doubleValue;
    }
    
    /**
     * Gets the optional float value associated with a key. Nan is returned if
     * the key does not exist, or if the value is not a number and cannot be
     * converted to a number.
     *
     * @param key string key
     * @return the value
     */
    public float optFloat(String key) {
        return this.optFloat(key, Float.NaN);
    }

    /**
     * Gets the optional float value associated with a key. The default value is
     * returned if the key does not exist, or if the value is not a number and
     * cannot be converted to a number.
     *
     * @param key string key
     * @param defaultValue default value
     * @return the value
     */
    public float optFloat(String key, float defaultValue) {
        Number val = this.optNumber(key);
        if (val == null) {
            return defaultValue;
        }
        final float floatValue = val.floatValue();
        return floatValue;
    }
    
    /**
     * Gets an optional int value associated with a key, or zero if there is no
     * such key or if the value is not a number. If the value is a string, a
     * will attempt to evaluate it as a number.
     *
     * @param key string key
     * @return the value
     */
    public int optInt(String key) {
        return this.optInt(key, 0);
    }

    /**
     * Gets an optional int value associated with a key, or the default value if
     * there is no such key or if the value is not a number. If the value is a
     * string, an attempt will be made to evaluate it as a number.
     *
     * @param key string key
     * @param defaultValue default value
     * @return the value
     */
    public int optInt(String key, int defaultValue) {
        final Number val = this.optNumber(key, null);
        if (val == null) {
            return defaultValue;
        }
        return val.intValue();
    }
    
    /**
     * Gets an optional long value associated with a key, or zero if there is no
     * such key or if the value is not a number. If the value is a string, an
     * attempt will be made to evaluate it as a number.
     *
     * @param key string key
     * @return the value
     */
    public long optLong(String key) {
        return this.optLong(key, 0L);
    }

    /**
     * Gets an optional long value associated with a key, or the default value
     * if there is no such key or if the value is not a number. If the value is
     * a string, an attempt will be made to evaluate it as a number.
     *
     * @param key string key
     * @param defaultValue default value
     * @return the value
     */
    public long optLong(String key, long defaultValue) {
        final Number val = this.optNumber(key, null);
        if (val == null) {
            return defaultValue;
        }

        return val.longValue();
    }
    
    /**
     * Gets an optional short value associated with a key, or zero if there is no
     * such key or if the value is not a number. If the value is a string, an
     * attempt will be made to evaluate it as a number.
     *
     * @param key string key
     * @return the value
     */
    public short optShort(String key) {
        return this.optShort(key, (short)0);
    }

    /**
     * Gets an optional short value associated with a key, or the default value
     * if there is no such key or if the value is not a number. If the value is
     * a string, an attempt will be made to evaluate it as a number.
     *
     * @param key string key
     * @param defaultValue default value
     * @return the value
     */
    public short optShort(String key, short defaultValue) {
        final Number val = this.optNumber(key, null);
        if (val == null) {
            return defaultValue;
        }

        return val.shortValue();
    }
    
    /**
     * Gets an optional byte value associated with a key, or zero bytes if there
     * is no such key or if the value is not a byte or number. If the value is a
     * string, an attempt will be made to evaluate it as a byte.
     *
     * @param key string key
     * @return the value
     */
    public byte optByte(String key) {
        return this.optByte(key, (byte)0);
    }

    /**
     * Gets an optional byte value associated with a key, or the default value
     * if there is no such key or if the value is not a byte or number. If the
     * value is a string, an attempt will be made to evaluate it as a byte.
     *
     * @param key string key
     * @param defaultValue default value
     * @return the value
     */
    public byte optByte(String key, byte defaultValue) {
        final Object val = this.opt(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Byte) {
            return (byte) val;
        }
        try {
            // We will use get anyway because it does string conversion.
            return this.getByte(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Gets an optional char value associated with a key, or a null value if
     * there is no such key or if the value is not a char. If the value is a
     * string, an attempt will be made to evaluate it as a char.
     *
     * @param key string key
     * @return the value
     */
    public char optChar(String key) {
        return this.optChar(key, '\u0000');
    }

    /**
     * Gets an optional char value associated with a key, or the default value
     * if there is no such key or if the value is not a char. If the value is a
     * string, an attempt will be made to evaluate it as a char.
     *
     * @param key string key
     * @param defaultValue default value
     * @return the value
     */
    public char optChar(String key, char defaultValue) {
        final Object val = this.opt(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Character) {
            return (char) val;
        }
        try {
            // We will use get anyway because it does string conversion.
            return this.getChar(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Gets a optional {@link Number} value associated with a key, or <code>null</code>
     * if no such key exists or if the value is not a number. If the value is a
     * string, an attempt will be made to evaluate it as a ({@link BigDecimal})
     * number. This method would be used in cases where type coercion of the
     * numeric value is not desired.
     *
     * @param key string key
     * @return the value
     */
    public Number optNumber(String key) {
        return this.optNumber(key, null);
    }

    /**
     * Gets a optional {@link Number} value associated with a key, or the default
     * value if no such key exists or if the value is not a number. If the value
     * is a string, an attempt will be made to evaluate it as a number. This
     * method would be used in cases where type coercion of the numeric value is
     * not desired.
     *
     * @param key string key
     * @param defaultValue default value
     * @return the value
     */
    public Number optNumber(String key, Number defaultValue) {
        Object val = this.opt(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Number){
            return (Number) val;
        }

        try {
            return stringToNumber(String.valueOf(val));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Gets an optional string associated with a key. Returns an empty string if
     * no such key exists. If the value is not a string and is not null, then it
     * is converted to a string.
     *
     * @param key string key
     * @return the value
     */
    public String optString(String key) {
        return this.optString(key, "");
    }

    /**
     * Gets an optional string associated with a key. Returns the default value
     * if no such key exists.
     *
     * @param key string key
     * @param defaultValue default value
     * @return the value
     */
    public String optString(String key, String defaultValue) {
        Object object = this.opt(key);
        if (object == null) {
            return defaultValue;
        } else if (object 
                    instanceof String) {
            return (String) object;
        }        
        return String.valueOf(object);
    }
    
    /**
     * Gets an optional BitSet associated with a key. Returns <code>null</code>
     * if there is no such key, or if the value is not an object {@link BitSet}.
     *
     * @param key string key
     * @return the value
     */
    public BitSet optBitSet(String key) {
        return this.optBitSet(key, null);
    }
    
    /**
     * Gets an optional BitSet associated with a key. Returns the default value
     * if no such key exists.
     *
     * @param key string key
     * @param defaultValue default value
     * @return the value
     */
    public BitSet optBitSet(String key, BitSet defaultValue) {
        try {
            final Object object = this.opt(key);
            if (object == null) {
                return defaultValue;
            }            
            if (object instanceof BitSet) {
                return (BitSet) object;
            }

            // We will use get since it does a conversion
            return this.getBitSet(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Gets an optional FloatBuffer associated with a key. Returns <code>null</code>
     * if there is no such key, or if the value is not an object {@link FloatBuffer}.
     *
     * @param key string key
     * @return a buffer which is the value
     */
    public FloatBuffer optFloatBuffer(String key) {
        return this.optFloatBuffer(key, null);
    }
    
    /**
     * Gets an optional FloatBuffer associated with a key. Returns the default
     * value if no such key exists.
     *
     * @param key string key
     * @param defaultValue default value
     * @return a buffer which is the value
     */
    public FloatBuffer optFloatBuffer(String key, FloatBuffer defaultValue) {
        final Object object = this.opt(key);
        if (object == null) {
            return defaultValue;
        } else {
            if (object instanceof FloatBuffer) {
                return (FloatBuffer) object;
            }
        }
        return defaultValue;
    }
    
    /**
     * Gets an optional IntBuffer associated with a key. Returns <code>null</code>
     * if there is no such key, or if the value is not an object {@link IntBuffer}.
     *
     * @param key string key
     * @return a buffer which is the value
     */
    public IntBuffer optIntBuffer(String key) {
        return this.optIntBuffer(key, null);
    }
    
    /**
     * Gets an optional IntBuffer associated with a key. Returns the default
     * value if no such key exists.
     *
     * @param key string key
     * @param defaultValue default value
     * @return a buffer which is the value
     */
    public IntBuffer optIntBuffer(String key, IntBuffer defaultValue) {
        final Object object = this.opt(key);
        if (object == null) {
            return defaultValue;
        } else {
            if (object instanceof IntBuffer) {
                return (IntBuffer) object;
            }
        }
        return defaultValue;
    }
    
    /**
     * Gets an optional ByteBuffer associated with a key. Returns <code>null</code>
     * if there is no such key, or if the value is not an object {@link ByteBuffer}.
     *
     * @param key string key
     * @return a buffer which is the value
     */
    public ByteBuffer optByteBuffer(String key) {
        return this.optByteBuffer(key, null);
    }
    
    /**
     * Gets an optional ByteBuffer associated with a key. Returns the default
     * value if no such key exists.
     *
     * @param key string key
     * @param defaultValue default value
     * @return a buffer which is the value
     */
    public ByteBuffer optByteBuffer(String key, ByteBuffer defaultValue) {
        final Object object = this.opt(key);
        if (object == null) {
            return defaultValue;
        } else {
            if (object instanceof ByteBuffer) {
                return (ByteBuffer) object;
            }
        }
        return defaultValue;
    }
    
    /**
     * Gets an optional ShortBuffer associated with a key. Returns <code>null</code>
     * if there is no such key, or if the value is not an object {@link ShortBuffer}.
     *
     * @param key string key
     * @return a buffer which is the value
     */
    public ShortBuffer optShortBuffer(String key) {
        return this.optShortBuffer(key, null);
    }
    
    /**
     * Gets an optional ShortBuffer associated with a key. Returns the default
     * value if no such key exists.
     *
     * @param key string key
     * @param defaultValue default value
     * @return a buffer which is the value
     */
    public ShortBuffer optShortBuffer(String key, ShortBuffer defaultValue) {
        final Object object = this.opt(key);
        if (object == null) {
            return defaultValue;
        } else {
            if (object instanceof ShortBuffer) {
                return (ShortBuffer) object;
            }
        }
        return defaultValue;
    }
        
    /**
     * Gets an optional savable associated with a key. Returns <code>null</code>
     * if no such key exists.
     * 
     * @param <E> savable type
     * @param key string key
     * @return the savable object
     */
    public <E extends Savable> E optSavable(String key) {
        return this.optSavable(key, null);
    }
    
    /**
     * Gets an optional savable associated with a key. Returns the default value
     * if no such key exists.
     *
     * @param <E> savable type
     * @param key string key
     * @param defaultValue default value
     * @return the savable object
     */
    @SuppressWarnings("unchecked")
    public <E extends Savable> E optSavable(String key, E defaultValue) {
        final Object object = this.opt(key);
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof Savable) {
            return (E) object;
        }
        return defaultValue;
    }
    
    /**
     * Method in charge of generating a clone of this object.
     * @return clone object.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Properties clone() {
        try {
            // Clone the object JmeProperties
            Properties clon = (Properties) 
                               super.clone();
            
            // Cloned object
            final Cloner cloner = new Cloner();
            
            // Since cloning only superficially affects the objects, we will 
            // also clone the Map along with its data that supports cloning.
            clon.properties = (HashMap<String, Savable>) properties.clone();
                        
            for (final Entry<?, ?> entry : clon.properties.entrySet()) {
                if (entry.getKey() == null) {
                    throw new NullPointerException("Null key.");
                }
                
                final Object oldvalue = entry.getValue();
                if (oldvalue == null)
                    continue;
                
                Object newvalue;
                if (oldvalue instanceof Property) {
                    newvalue = ((Property) oldvalue).clone();
                } else if (oldvalue instanceof Savable) {
                    if (oldvalue instanceof JmeCloneable ||
                        oldvalue instanceof Cloneable ||
                        cloner.isCloned(oldvalue)) {
                        
                        // We cloned the object!
                        newvalue = cloner.clone(oldvalue);
                    } else {
                        newvalue = oldvalue;                        
                        LOGGER.log(Level.WARNING,"The object[key={0}, object={1}] does not support cloning.", new Object[] {
                                                                                        quote(String.valueOf(entry.getKey())),
                                                                                        quote(oldvalue.getClass().getName())});
                    }
                } else {
                    throw new UnsupportedOperationException("Object [" + oldvalue.getClass().getName() + "] not supported.");
                }
                
                clon.properties.put(String.valueOf(entry.getKey()), wrap(newvalue));
            }
            
            // We return the cloned object together with its properties
            return clon;
        } catch (CloneNotSupportedException e) {
            // If it gives an error or for some reason Jm Properties does not 
            // support cloning.
            throw new InternalError(e);
        }
    }
    
    /**
     * Wrap an object, if necessary. If the object is <code>null</code>, returns
     * a NULL object. If this is an array or a collection, wrap it in a JmeArray.
     * If this is a map, wrap it in a JmeProperties. If it is a standard property
     * (Double, String, etc.) then it is already wrapped. Otherwise, if it comes
     * from one of the java packages, convert it to a string. And if not, try to
     * wrap it in a JmeProperties. If the settings fails, null is returned.
     *
     * @param object the object to be wrapped
     * @return the value wrapped
     */
    protected static Savable wrap(Object object) {
        try {
            if (object == null) {
                return null;
            }
            
            if (object instanceof Savable) {
                return (Savable) object;
            } else {                
               Type jmeType = Type.jmeValueOf(object);                    
               if (jmeType != null)
                   return new Property(object);
               
               throw new IllegalStateException();
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * Generates the hash code of the class of an object {@link Properties}.
     * @return object hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.properties);
        return hash;
    }
    
    /**
     * An object {@link Properties} is equal to itself.
     *
     * @param obj an object to prove nullity
     * @return true if the object parameter is the Properties object or the same
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
        final Properties other = (Properties) obj;
        return Objects.equals(this.properties, other.properties);
    }
    
    /**
     * Place a key/boolean pair in the JmeProperties.
     *
     * @param key string key
     * @param value a boolean as value
     * @return this
     * 
     * @throws RuntimeException 
     *          if the value is a non-finite number
     * @throws NullPointerException 
     *          if the key is <code>null</code>
     */
    public Properties put(String key, boolean value) throws RuntimeException{
        return this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
    }
    
    /**
     * Place a key/double pair in the JmeProperties.
     *
     * @param key string key
     * @param value a double as value
     * @return this
     * 
     * @throws RuntimeException 
     *          if the value is a non-finite number
     * @throws NullPointerException 
     *          if the key is <code>null</code>
     */
    public Properties put(String key, double value) throws RuntimeException {
        return this.put(key, Double.valueOf(value));
    }
    
    /**
     * Place a key/float pair in the JmeProperties.
     *
     * @param key string key
     * @param value a float as value
     * @return this
     * 
     * @throws RuntimeException 
     *          if the value is a non-finite number.
     * @throws NullPointerException 
     *          if the key is <code>null</code>
     */
    public Properties put(String key, float value) throws RuntimeException {
        return this.put(key, Float.valueOf(value));
    }
    
    /**
     * Place a key/int pair in the JmeProperties.
     *
     * @param key string key
     * @param value a int as value
     * @return this
     * 
     * @throws RuntimeException 
     *          if the value is a non-finite number
     * @throws NullPointerException 
     *          if the key is <code>null</code>
     */
    public Properties put(String key, int value) throws RuntimeException {
        return this.put(key, Integer.valueOf(value));
    }
    
    /**
     * Place a key/long pair in the JmeProperties.
     *
     * @param key string key
     * @param value a long as value
     * @return this
     * 
     * @throws RuntimeException 
     *          if the value is a non-finite number
     * @throws NullPointerException 
     *          if the key is <code>null</code>
     */
    public Properties put(String key, long value) throws RuntimeException {
        return this.put(key, Long.valueOf(value));
    }
    
    /**
     * Place a key/byte pair in the JmeProperties.
     *
     * @param key string key
     * @param value a byte as value
     * @return this
     * 
     * @throws RuntimeException 
     *              if the value is a non-finite number
     * @throws NullPointerException 
     *              if the key is <code>null</code>
     */
    public Properties put(String key, byte value) throws RuntimeException {
        return this.put(key, Byte.valueOf(value));
    }
    
    /**
     * Place a key/char pair in the JmeProperties.
     *
     * @param key string key
     * @param value a char as value
     * @return this
     * 
     * @throws RuntimeException 
     *              if the value is a non-finite number.
     * @throws NullPointerException 
     *              if the key is <code>null</code>
     */
    public Properties put(String key, char value) throws RuntimeException {
        return this.put(key, Character.valueOf(value));
    }
    
    /**
     * Place a key/short pair in the JmeProperties.
     *
     * @param key string key
     * @param value a short as value
     * @return this
     * 
     * @throws RuntimeException 
     *          if the value is a non-finite number
     * @throws NullPointerException 
     *          if the key is <code>null</code>
     */
    public Properties put(String key, short value) throws RuntimeException {
        return this.put(key, Short.valueOf(value));
    }
    
    /**
     * Place a key/value pair in the JmeProperties. If the value is <code>null</code>,
     * then the key will be removed from the JmeProperties if present.
     *
     * @param key string key
     * @param value an object that is the value. It must be of one of these types:
     * boolean, double, int, JmeArray, JmeProperties, long, String, or the JmeNull object
     * @return this
     * 
     * @throws RuntimeException if the value is a non-finite number or the value
     *                          is not supported
     * @throws NullPointerException if the key is <code>null</code>
     */
    public Properties put(String key, Object value) throws RuntimeException {
        if (key == null) {
            throw new NullPointerException("Null key.");
        }
        if (value != null) {
            if (value instanceof Savable) {
                this.properties.put(key, (Savable) value);
            } else {
                testValidity(value);
                this.properties.put(key, new Property(value));
            }
        } else {
            this.remove(key);
        }
        return this;
    }

    /**
     * Checks if a numeric value is a finite number.
     * 
     * @param n the value to check
     * @return true if it's a finite number, false otherwise
     */
    private static boolean numberIsFinite(Number n) {
        if (n instanceof Double && (((Double) n).isInfinite() || ((Double) n).isNaN())) {
            return false;
        } else if (n instanceof Float && (((Float) n).isInfinite() || ((Float) n).isNaN())) {
            return false;
        }
        return true;
    }
    
     /**
     * Tests whether the value should be tested as a decimal. It does not test
     * for real digits.
     *
     * @param val value to be tested
     * @return true if the string is "-0" or if it contains '.', 'e' or 'E',
     * false otherwise
     */
    protected static boolean isDecimalNotation(final String val) {
        return val.indexOf('.') > -1 || val.indexOf('e') > -1
                || val.indexOf('E') > -1 || "-0".equals(val);
    }
    
   /**
     * Converts a string to a number using the narrowest possible type. Possible
     * results of this function are BigDecimal, Double, BigInteger, Long and
     * Integer. When a Double is returned, it must always be a valid Double and
     * not NaN or +-infinite.
     *
     * @param val value to convert
     * @return numerical representation of the value
     * @throws NumberFormatException thrown if the value is not a valid number
     * The caller should catch this and wrap it in a {@link RuntimeException}
     * if applicabl
     */
    protected static Number stringToNumber(final String val) throws NumberFormatException {
        char initial = val.charAt(0);
        if ((initial >= '0' && initial <= '9') || initial == '-') {
            // Decimal representation
            if (isDecimalNotation(val)) {
                // Use a BigDecimal all the time to keep the original representation.
                // BigDecimal does not support -0.0, make sure you maintain that by
                // forcing a decimal.
                try {
                    BigDecimal bd = new BigDecimal(val);
                    if(initial == '-' && BigDecimal.ZERO.compareTo(bd)==0) {
                        return -0.0d;
                    }
                    return bd;
                } catch (NumberFormatException retryAsDouble) {
                    // This is to support "Hex Floats" like this one: 0x1.0P-1074
                    try {
                        Double d = Double.valueOf(val);
                        if(d.isNaN() || d.isInfinite()) {
                            throw new NumberFormatException("val ["+val+"] is not a valid number.");
                        }
                        return d;
                    } catch (NumberFormatException ignore) {
                        throw new NumberFormatException("val ["+val+"] is not a valid number.");
                    }
                }
            }
            // Blocks items such as 00 01, etc. Java number parsers treat them as octal.
            if(initial == '0' && val.length() > 1) {
                char at1 = val.charAt(1);
                if(at1 >= '0' && at1 <= '9') {
                    throw new NumberFormatException("val ["+val+"] is not a valid number.");
                }
            } else if (initial == '-' && val.length() > 2) {
                char at1 = val.charAt(1);
                char at2 = val.charAt(2);
                if(at1 == '0' && at2 >= '0' && at2 <= '9') {
                    throw new NumberFormatException("val ["+val+"] is not a valid number.");
                }
            }
            
            // Integer representation.
            // This will reduce any value to the smallest reasonable object representation.
            // (Integer, Long or BigInteger).
            // 
            // BigInteger downconversion: We use a bit length comparison similar to
            // BigInteger#intValueExact uses. It increases GC, but objects are kept
            // only what they need. i.e. less runtime overhead if the value is long-lived.
            
            BigInteger bi = new BigInteger(val);
            if(bi.bitLength() <= 31){
                return bi.intValue();
            }
            if(bi.bitLength() <= 63){
                return bi.longValue();
            }
            return bi;
        }
        throw new NumberFormatException("val ["+val+"] is not a valid number.");
    }
    
    /**
     * Throws an exception if the object is a NaN or an infinite number.
     *
     * @param o the object to be tested.
     * @throws RuntimeException if it is a non-finite number.
     */
    protected static void testValidity(Object o) throws RuntimeException {
        if (o instanceof Number && !numberIsFinite((Number) o)) {
            throw new RuntimeException("JmeProperties does not allow non-finite numbers.");
        }
    }
    
    /**
     * Delete a name and its value, if present.
     *
     * @param key the name to be deleted
     * @return the value that was associated with the name, or null if there was
     * no value
     */
    public Object remove(String key) {
        return this.properties.remove(key);
    }
    
    /**
     * Sets a new property.
     * 
     * @param <T> data type
     * @param key key name
     * @param value value to be saved
     * @return this.
     */
    @Deprecated(since = "2.5.0")
    public <T extends Object> Properties setProperty(String key, T value) {
        this.properties.put(key, new Property(value));
        return this;
    }
    
    /**
     * Method in charge of managing the key names of the properties.
     * 
     * @return key names.
     */
    public Iterator<String> propertyNames() {
        return this.properties.keySet().iterator();
    }
    
    /**
     * (non-Javadoc).
     * 
     * @param ex JmeExporter
     * @see Savable#write(com.jme3.export.JmeExporter) 
     * 
     * @throws IOException exception.
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.writeStringSavableMap(properties, "Properties", null);
    }

    /**
     * (non-JavaDdc).
     * 
     * @param im JmeImporter
     * @see Savable#read(com.jme3.export.JmeImporter) 
     * 
     * @throws IOException exception.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);        
        properties = (HashMap<String, Savable>) in.readStringSavableMap("Properties", properties);
    }
    
    /**
     * Produces a string enclosed in double quotes with backslash sequences in
     * all the correct places. A backslash will be inserted inside &lt;/, producing
     * &lt;\/, which allows the property text to be delivered in HTML. In property
     * text, a string cannot contain a control character or a quotation mark without
     * an escape or backslash.
     *
     * @param string the text
     * @return a correctly formatted string for insertion into a JmeProperties text
     */
    protected static String quote(String string) {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            try {
                return quote(string, sw).toString();
            } catch (IOException ignored) {
                // It will never happen - we are writing to a string writer.
                return "";
            }
        }
    }

    /**
     * Produces a string enclosed in double quotes with backslash sequences in
     * all the correct places using a custom {@link Writer} object. A backslash
     * will be inserted inside &lt;/, producing &lt;\/, which allows the property
     * text to be delivered in HTML. In property text, a string cannot contain a
     * control character or a quotation mark without an escape or backslash.
     * 
     * @param string the text
     * @param w the custom Writer object
     * @return a correctly formatted string inside a Writer object
     * @throws IOException exception
     */
    protected static Writer quote(String string, Writer w) throws IOException {
        if (string == null || string.isEmpty()) {
            w.write("\"\"");
            return w;
        }

        char b;
        char c = 0;
        String hhhh;
        int i;
        int len = string.length();

        w.write('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                w.write('\\');
                w.write(c);
                break;
            case '/':
                if (b == '<') {
                    w.write('\\');
                }
                w.write(c);
                break;
            case '\b':
                w.write("\\b");
                break;
            case '\t':
                w.write("\\t");
                break;
            case '\n':
                w.write("\\n");
                break;
            case '\f':
                w.write("\\f");
                break;
            case '\r':
                w.write("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                        || (c >= '\u2000' && c < '\u2100')) {
                    w.write("\\u");
                    hhhh = Integer.toHexString(c);
                    w.write("0000", 0, 4 - hhhh.length());
                    w.write(hhhh);
                } else {
                    w.write(c);
                }
            }
        }
        w.write('"');
        return w;
    }
    
    /**
     * Creates a new JmeException in a common format for incorrect conversions.
     * 
     * @param key name of the key
     * @param valueType the type of value to which it is obligated
     * @param cause causa opcional de la falla de coerción
     * @return JmeException that can be thrown out
     */
    private static RuntimeException wrongValueFormatException(
            String key,
            String valueType,
            Object value,
            Throwable cause) {
        if(value == null) {

            return new RuntimeException(
                    "JmeProperties[" + quote(key) + "] is not a " + valueType + " (null)."
                    , cause);
        }
        // Do not attempt to make chains of collections or known object types that could be large
        if(value instanceof Map || value instanceof Iterable || value instanceof Properties) {
            return new RuntimeException(
                    "JmeProperties[" + quote(key) + "] is not a " + valueType + " (" + value.getClass() + ")."
                    , cause);
        }
        return new RuntimeException(
                "JmeProperties[" + quote(key) + "] is not a " + valueType + " (" + value.getClass() + " : " + value + ")."
                , cause);
    }
}
