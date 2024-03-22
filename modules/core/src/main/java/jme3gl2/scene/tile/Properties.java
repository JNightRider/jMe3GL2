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
 * Un <code>Properties</code> se encarga de guardar las propiedades de
 * un {@link Tile}.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 2.0.0
 */
@SuppressWarnings(value = {"unchecked"})
public class Properties implements Savable, Cloneable {

    /** Loggre de la clase. */
    private static final Logger LOGGER = Logger.getLogger(Properties.class.getName());
    
    /** Mapa de propiedades. */
    private HashMap<String, Savable> properties;
    
    /** Version que tiene la clase {@code Properties} actualemente. */
    public static final int SAVABLE_VERSION = 2;
    
    /**
     * Constructor predeterminado.
     */
    public Properties() {
        this.properties = new HashMap<>();
    }
    
    /**
     * Obtiene el objeto de valor asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return El objeto asociado con la clave.
     * @throws RuntimeException
     *             si no se encuentra la clave.
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
     * Obtenga el valor de enum asociado con una clave.
     *
     * @param <E>
     *            Tipoe enumerado.
     * @param key
     *           Clave string.
     * @return El valor de enumeración asociado con la clave.
     * @throws RuntimeException
     *             si no se encuentra la clave o si el valor no se puede convertir
     *             a una enumeración.
     */
    public <E extends Enum<E>> E getEnum(String key) throws RuntimeException {
        E val = optEnum(key);
        if(val==null) {
            // JmeException realmente debería tomar un argumento arrojable.
            // Si lo hiciera, lo volvería a implementar con Enum.valueOf
            // método y coloque cualquier excepción lanzada en JmeException
            throw wrongValueFormatException(key, "enum of type " + quote("null"), opt(key), null);
        }
        return val;
    }

    /**
     * Obtiene el valor boolean asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return Valor verdadero o real.
     * @throws RuntimeException
     *             si el valor no es un booleano o la cadena "true" o
     *             "false".
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
     * Obtenga el valor BigInteger asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return El valor numérico.
     * @throws RuntimeException
     *             si no se encuentra la clave o si el valor no puede
     *             ser convertido a BigInteger.
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
     * Obtenga el valor BigDecimal asociado con una clave. Si el valor es flotante o
     * double, el constructor {@link BigDecimal#BigDecimal(double)}
     * sera usado. Consulte las notas sobre el constructor para problemas de conversión que pueden
     * aumentar.
     *
     * @param key
     *            Clave string.
     * @return El valor numérico.
     * @throws RuntimeException
     *             si no se encuentra la clave o si el valor
     *             no se puede convertir a BigDecimal.
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
     * Obtenga el valor BitSet asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return Un objeto como valor.
     * @throws RuntimeException
     *             si no se encuentra la clave o si el valor no puede
     *             ser convertido a BitSet.
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
     * Obtiene un FloatBuffer asociada con una clave.
     *
     * @param key
     *            Clave string.
     * @return Una buffer que es el valor.
     * @throws RuntimeException
     *             si no hay un valor buffer para la clave.
     */
    public FloatBuffer getFloatBuffer(String key) throws RuntimeException {
        final Object object = this.get(key);
        if (object instanceof FloatBuffer) {
            return (FloatBuffer) object;
        }
        throw wrongValueFormatException(key, "FloatBuffer", object, null);
    }
    
    /**
     * Obtiene un IntBuffer asociada con una clave.
     *
     * @param key
     *            Clave string.
     * @return Una buffer que es el valor.
     * @throws RuntimeException
     *             si no hay un valor buffer para la clave.
     */
    public IntBuffer getIntBuffer(String key) throws RuntimeException {
        final Object object = this.get(key);
        if (object instanceof IntBuffer) {
            return (IntBuffer) object;
        }
        throw wrongValueFormatException(key, "IntBuffer", object, null);
    }
    
    /**
     * Obtiene un ByteBuffer asociada con una clave.
     *
     * @param key
     *            Clave string.
     * @return Una buffer que es el valor.
     * @throws RuntimeException
     *             si no hay un valor buffer para la clave.
     */
    public ByteBuffer getByteBuffer(String key) throws RuntimeException {
        final Object object = this.get(key);
        if (object instanceof ByteBuffer) {
            return (ByteBuffer) object;
        }
        throw wrongValueFormatException(key, "ByteBuffer", object, null);
    }
    
    /**
     * Obtiene un ShortBuffer asociada con una clave.
     *
     * @param key
     *            Clave string.
     * @return Una buffer que es el valor.
     * @throws RuntimeException
     *             si no hay un valor buffer para la clave.
     */
    public ShortBuffer getShortBuffer(String key) throws RuntimeException {
        final Object object = this.get(key);
        if (object instanceof ShortBuffer) {
            return (ShortBuffer) object;
        }
        throw wrongValueFormatException(key, "ShortBuffer", object, null);
    }
    
    /**
     * Obtiene el valor double asociado a una clave.
     *
     * @param key
     *            Clave string.
     * @return Valor numerico.
     * @throws RuntimeException
     *             si no se encuentra la clave o si el valor no es un Número
     *              objeto y no se puede convertir en un número.
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
     * Obtiene el valor float asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return El valor numerico.
     * @throws RuntimeException
     *             si no se encuentra la clave o si el valor no es un Número
     *             objeto y no se puede convertir en un número.
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
     * Obtenga el valor Numbers asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return El valor numerico.
     * @throws RuntimeException
     *             si no se encuentra la clave o si el valor no es un Número
     *             objeto y no se puede convertir en un número.
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
     * Obtiene el valor int asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return Valor Integer.
     * @throws RuntimeException
     *             si no se encuentra la clave o si el valor no se puede convertir
     *             a un número entero.
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
     * Obtiene el valor long asociado con una clave.
     *
     * @param key
     *            Un string como clave.
     * @return Un long como valor.
     * @throws RuntimeException
     *             si no se encuentra la clave o si el valor no se puede convertir
     *              a un long.
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
     * Obtiene el valor short asociado con una clave.
     *
     * @param key
     *            Un string como clave.
     * @return Un short como valor.
     * @throws RuntimeException
     *             si no se encuentra la clave o si el valor no se puede convertir
     *              a un short.
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
     * Obtiene el valor byte asociado con una clave.
     *
     * @param key
     *            Un string como clave.
     * @return Un byte como valor.
     * @throws RuntimeException
     *             si no se encuentra la clave o si el valor no se puede convertir
     *              a un byte.
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
     * Obtiene el valor char asociado con una clave.
     *
     * @param key
     *            Un string como clave.
     * @return Un char como valor.
     * @throws RuntimeException
     *             si no se encuentra la clave o si el valor no se puede convertir
     *              a un char.
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
     * Obtiene un String asociada con una clave.
     *
     * @param key
     *            Clave string.
     * @return Una cadena que es el valor.
     * @throws RuntimeException
     *             si no hay un valor de cadena para la clave.
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
     * Obtiene un Savable asociada con una clave.
     *
     * @param <E>
     *          Tipo savable.
     * @param key
     *            Clave string.
     * @return Un objeto Savable como valor.
     * @throws RuntimeException
     *             si no hay un valor savable para la clave.
     */
    public <E extends Savable> E getSavable(String key) throws RuntimeException {
        Object object = this.get(key);
        if (object instanceof Savable) {
            return (E) object;
        }
        throw wrongValueFormatException(key, "savable", object, null);
    }
    
    /**
     * Determine si JmeProperties contiene una clave específica.
     *
     * @param key
     *            Una cadena clave.
     * @return true si la clave existe en JmeProperties.
     */
    public boolean has(String key) {
        return this.properties.containsKey(key);
    }

    /**
     * Determine si el valor asociado con la clave es <code>null</code> o si no hay
     * valor.
     *
     * @param key
     *            Una cadena clave.
     * @return true si no hay ningún valor asociado con la clave o si el valor
     *          es el objeto JmeProperties.NULL o JmeNull.
     */
    public boolean isNull(String key) {
        final Object object = this.opt(key);
        return object == null;
    }
    
    /**
     * Obtenga una enumeración de las claves del JmeProperties. La modificación de este conjunto de claves también
     * modificar el JmeProperties. Utilizar con precaución.
     *
     * @see Set#iterator()
     *
     * @return Un iterador de las claves.
     */
    public Iterator<String> keys() {
        return this.keySet().iterator();
    }

    /**
     * Obtenga una enumeración de las claves del JmeProperties. La modificación de este conjunto de claves también
     * modificar el JmeProperties. Utilizar con precaución.
     *
     * @see Map#keySet()
     *
     * @return Un juego de llaves.
     */
    public Set<String> keySet() {
        return this.properties.keySet();
    }

    /**
     * Obtenga un conjunto de entradas de JmeProperties. Estos son valores brutos y es posible que no
     * coincide con lo que devuelven las funciones get* y opt* de JmeProperties. modificando
     * el EntrySet devuelto o los objetos Entry contenidos en él modificarán el
     * respaldando JmeProperties. Esto no devuelve un clon o una vista de solo lectura.
     *
     * Utilizar con precaución.
     *
     * @see Map#entrySet()
     *
     * @return Un conjunto de entrada
     */
    protected Set<Entry<String, Savable>> entrySet() {
        return this.properties.entrySet();
    }

    /**
     * Obtenga el número de claves almacenadas en JmeProperties.
     *
     * @return El número de claves en el JmeProperties.
     */
    public int length() {
        return this.properties.size();
    }

    /**
     * Elimina todos los elementos de este JmeProperties.
     * JmeProperties estará vacío después de que regrese esta llamada.
     */
    public void clear() {
        this.properties.clear();
    }

    /**
     * Compruebe si JmeProperties está vacío.
     *
     * @return true si el JmeProperties está vacío, de lo contrario false.
     */
    public boolean isEmpty() {
        return this.properties.isEmpty();
    }
    
    /**
     * Produce una cadena a partir de un Número.
     *
     * @param number
     *            Un numero.
     * @return Uan cadena.
     * @throws RuntimeException
     *             Si n es un número no finito.
     */
    protected static String numberToString(Number number) throws RuntimeException {
        if (number == null) {
            throw new RuntimeException("Null pointer");
        }
        testValidity(number);

        // Elimina los ceros finales y el punto decimal, si es posible.

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
     * Obtenga un valor opcional asociado con una clave.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor, o nulo si no hay valor.
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
     * Obtenga el valor de enumeración asociado con una clave.
     *
     * @param <E>
     *            Tipo enum.
     * @param key
     *            Una cadena clave.
     * @return El valor enumerado asociado con la clave o nulo si no se encuentra
     */
    public <E extends Enum<E>> E optEnum(String key) {
        return this.optEnum(key, null);
    }
    
    /**
     * Obtenga el valor de enumeración asociado con una clave.
     *
     * @param <E>
     *            Tipo enum.
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            El valor predeterminado en caso de que no se encuentre el valor
     * @return El valor de enumeración asociado con la clave o defaultValue
     *          si el valor no se encuentra o no se puede asignar a <code>clazz</code>
     */
    public <E extends Enum<E>> E optEnum(String key, E defaultValue) {
        final Object val = this.opt(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Enum) {
            // Lo acabamos de comprobar!
            @SuppressWarnings("unchecked")
            E myE = (E) val;
            return myE;
        }
        return defaultValue;
    }
    
    /**
     * Obtenga un booleano opcional asociado con una clave. Devuelve false si
     * no existe tal clave, o si el valor no es Boolean.TRUE o la cadena "true".
     *
     * @param key
     *            Una cadena clave.
     * @return Valo real o verdadero.
     */
    public boolean optBoolean(String key) {
        return this.optBoolean(key, false);
    }
    
    /**
     * Obtenga un boolean opcional asociado con una clave. devuelve el
     * defaultValue si no existe tal clave, o si no es un boolean o la
     * Cadena "ture" o "falsoe" (no distingue entre mayúsculas y minúsculas).
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Valo real o verdadero.
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
            // usaremos get de todos modos porque hace conversión de cadenas.
            return this.getBoolean(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga un BigDecimal opcional asociado con una clave, o null si
     * no existe tal clave o si su valor no es un número. Si el valor es un
     * string, se intentará evaluarlo como un número. si el valor
     * es flotante o doble, entonces {@link BigDecimal#BigDecimal(double)}
     * Se utilizará el constructor. Ver notas sobre el constructor para la conversión
     * de problemas que puedan surgir.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public BigDecimal optBigDecimal(String key) {
        return this.optBigDecimal(key, null);
    }
    
    /**
     * Obtenga un BigDecimal opcional asociado con una clave, o el valor predeterminado si
     * no existe tal clave o si su valor no es un número. Si el valor es un
     * string, se intentará evaluarlo como un número. si el valor
     * es flotante o doble, entonces {@link BigDecimal#BigDecimal(double)}
     * Se utilizará el constructor. Ver notas sobre el constructor para la conversión
     * de problemas que puedan surgir.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminadoo.
     * @return Un objeto que es el valor.
     */
    public BigDecimal optBigDecimal(String key, BigDecimal defaultValue) {
        Object val = this.opt(key);
        return objectToBigDecimal(val, defaultValue);
    }
    
    /**
     * @param val 
     *          valor para convertir
     * @param defaultValue 
     *                  el valor predeterminado que se devuelve es que la conversión no funciona o es nula.
     * @return Conversión BigDecimal del valor original, o el valor predeterminado si no se puede
     *          para convertir.
     */
    static BigDecimal objectToBigDecimal(Object val, BigDecimal defaultValue) {
        return objectToBigDecimal(val, defaultValue, true);
    }
    
    /**
     * @param val valor para convertir
     * @param defaultValue el valor predeterminado que se devuelve es que la conversión no funciona o es nula.
     * @param exact Cuando <code>true</code>, los valores {@link Double} y {@link Float} se convertirán exactamente.
     *              Cuando <code>false</code>, se convertirán a valores {@link String} antes de convertirse a {@link BigDecimal}.
     * @return Conversión BigDecimal del valor original, o el valor predeterminado si no se puede
     *          para convertir.
     */
    static BigDecimal objectToBigDecimal(Object val, BigDecimal defaultValue, boolean exact) {
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
            }else {
                // usar el constructor de cadenas para mantener valores "agradables" para double y float,
                // el constructor double traducirá los douuble a valores "exactos" en lugar de los probables
                // representación prevista
                return new BigDecimal(val.toString());
            }
        }
        if (val instanceof Long || val instanceof Integer
                || val instanceof Short || val instanceof Byte){
            return new BigDecimal(((Number) val).longValue());
        }
        // no verifique si es una cadena en caso de subclases de números no verificadas
        try {
            return new BigDecimal(String.valueOf(val));
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga un BigInteger opcional asociado con una clave, o null si
     * no existe tal clave o si su valor no es un número. Si el valor es un
     * string, se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public BigInteger optBigInteger(String key) {
        return this.optBigInteger(key, null);
    }
    
    /**
     * Obtenga un BigInteger opcional asociado con una clave, o el valor predeterminado si
     * no existe tal clave o si su valor no es un número. Si el valor es un
     * string, se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            valor predeterminado.
     * @return Un objeto que es el valor.
     */
    public BigInteger optBigInteger(String key, BigInteger defaultValue) {
        Object val = this.opt(key);
        return objectToBigInteger(val, defaultValue);
    }

    
    /**
     * @param val valor para convertir
     * @param defaultValue el valor predeterminado que se devuelve es que la conversión no funciona o es nula.
     * @return Conversión BigInteger del valor original, o el valor predeterminado si no se puede
     *          para convertir.
     */
    static BigInteger objectToBigInteger(Object val, BigInteger defaultValue) {
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
        // no verifique si es una cadena en caso de subclases de números no verificadas
        try {
            // las otras funciones opt manejan conversiones implícitas, es decir
            // jo.put("doble",1.1d);
            // jo.optInt("doble"); -- devolverá 1, no un error
            // esta conversión a BigDecimal y luego a BigInteger es para mantener
            // ese tipo de soporte de conversión que puede truncar el decimal.
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
     * Obtenga un doble opcional asociado con una clave, o NaN si no existe tal
     * clave o si su valor no es un número. Si el valor es una cadena, un intento
     * se hará para evaluarlo como un número.
     *
     * @param key
     *            Una cadena que es la clave..
     * @return Un objeto que es el valor.
     */
    public double optDouble(String key) {
        return this.optDouble(key, Double.NaN);
    }

    /**
     * Obtenga un double opcional asociado con una clave, o el valor predeterminado si
     * no existe tal clave o si su valor no es un número. Si el valor es un
     * string, se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predetermiando.
     * @return Un objeto que es el valor.
     */
    public double optDouble(String key, double defaultValue) {
        Number val = this.optNumber(key);
        if (val == null) {
            return defaultValue;
        }
        final double doubleValue = val.doubleValue();
        // if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
        // return defaultValue;
        // }
        return doubleValue;
    }
    
    /**
     * Obtenga el valor float opcional asociado con una clave. Sse devuelve 
     * Nan si la clave no existe, o si el valor no es un
     * número y no se puede convertir a un número.
     *
     * @param key
     *           Una cande clave.
     * @return El valor.
     */
    public float optFloat(String key) {
        return this.optFloat(key, Float.NaN);
    }

    /**
     * Obtenga el valor float opcional asociado con una clave. El valor predeterminado
     * se devuelve si la clave no existe, o si el valor no es un
     * número y no se puede convertir a un número.
     *
     * @param key
     *            Una cande clave
     * @param defaultValue
     *            El valor predetreminado.
     * @return El valor.
     */
    public float optFloat(String key, float defaultValue) {
        Number val = this.optNumber(key);
        if (val == null) {
            return defaultValue;
        }
        final float floatValue = val.floatValue();
        // if (Float.isNaN(floatValue) || Float.isInfinite(floatValue)) {
        // return defaultValue;
        // }
        return floatValue;
    }
    
    /**
     * Obtenga un valor int opcional asociado con una clave, o cero si no hay
     * tal clave o si el valor no es un número. Si el valor es una cadena, un
     * se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public int optInt(String key) {
        return this.optInt(key, 0);
    }

    /**
     * Obtenga un valor int opcional asociado con una clave, o el valor predeterminado si existe
     * no existe tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            valor predeterminado.
     * @return Un objeto que es el valor.
     */
    public int optInt(String key, int defaultValue) {
        final Number val = this.optNumber(key, null);
        if (val == null) {
            return defaultValue;
        }
        return val.intValue();
    }
    
    /**
     * Obtenga un valor long opcional asociado con una clave, o cero si no hay
     * tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public long optLong(String key) {
        return this.optLong(key, 0L);
    }

    /**
     * Obtenga un valor long opcional asociado con una clave, o el valor predeterminado si existe
     * no existe tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un objeto que es el valor.
     */
    public long optLong(String key, long defaultValue) {
        final Number val = this.optNumber(key, null);
        if (val == null) {
            return defaultValue;
        }

        return val.longValue();
    }
    
    /**
     * Obtenga un valor short opcional asociado con una clave, o cero si no hay
     * tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public short optShort(String key) {
        return this.optShort(key, (short)0);
    }

    /**
     * Obtenga un valor short opcional asociado con una clave, o el valor predeterminado si existe
     * no existe tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un objeto que es el valor.
     */
    public short optShort(String key, short defaultValue) {
        final Number val = this.optNumber(key, null);
        if (val == null) {
            return defaultValue;
        }

        return val.shortValue();
    }
    
    /**
     * Obtenga un valor byte opcional asociado con una clave, o cero bytes si no hay
     * tal clave o si el valor no es un byte o número. Si el valor es una cadena,
     * se intentará evaluarlo como un Byte.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public byte optByte(String key) {
        return this.optByte(key, (byte)0);
    }

    /**
     * Obtenga un valor byte opcional asociado con una clave, o el valor predeterminado si no hay
     * tal clave o si el valor no es un byte o número. Si el valor es una cadena,
     * se intentará evaluarlo como un Byte.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un objeto que es el valor.
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
            // usaremos get de todos modos porque hace conversión de cadenas.
            return this.getByte(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga un valor char opcional asociado con una clave, o un valor nulo si no hay
     * tal clave o si el valor no es un char. Si el valor es una cadena,
     * se intentará evaluarlo como un char.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public char optChar(String key) {
        return this.optChar(key, '\u0000');
    }

    /**
     * Obtenga un valor char opcional asociado con una clave, o el valor predeterminado si no hay
     * tal clave o si el valor no es un char. Si el valor es una cadena,
     * se intentará evaluarlo como un char
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un objeto que es el valor.
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
            // usaremos get de todos modos porque hace conversión de cadenas.
            return this.getChar(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga un valor {@link Number} opcional asociado con una clave, o <code>null</code>
     * si no existe tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número ({@link BigDecimal}). Este método
     * se usaría en los casos en que no se desee la coerción de tipo del valor numérico.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public Number optNumber(String key) {
        return this.optNumber(key, null);
    }

    /**
     * Obtenga un valor {@link Number} opcional asociado con una clave, o el valor predeterminado si
     * no existe tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número. Este método
     * se usaría en los casos en que no se desee la coerción de tipo del valor numérico.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un objeto que es el valor.
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
     * Obtenga una cadena opcional asociada con una clave. Devuelve una cadena vacía.
     * si no existe tal clave. Si el valor no es una cadena y no es nulo,
     * luego se convierte en una cadena.
     *
     * @param key
     *            Una cadena clave.
     * @return Una cadena que es el valor.
     */
    public String optString(String key) {
        return this.optString(key, "");
    }

    /**
     * Obtenga una cadena opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una cadena que es el valor.
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
     * Obtenga un BitSet opcional asociada con una clave. Devuelve <code>null</code>.
     * Si no existe tal clave. O si el valor no es un objeto {@link BitSet}.
     *
     * @param key
     *            Una cadena clave.
     * @return Una BitSet que es el valor.
     */
    public BitSet optBitSet(String key) {
        return this.optBitSet(key, null);
    }
    
    /**
     * Obtenga un BitSet opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una BitSet que es el valor.
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

            // usaremos get ya que hace una
            // conversion.
            return this.getBitSet(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga un FloatBuffer opcional asociada con una clave. Devuelve <code>null</code>.
     * Si no existe tal clave. O si el valor no es un objeto {@link FloatBuffer}.
     *
     * @param key
     *            Una cadena clave.
     * @return Una buffer que es el valor.
     */
    public FloatBuffer optFloatBuffer(String key) {
        return this.optFloatBuffer(key, null);
    }
    
    /**
     * Obtenga un FloatBuffer opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una buffer que es el valor.
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
     * Obtenga un IntBuffer opcional asociada con una clave. Devuelve <code>null</code>.
     * Si no existe tal clave. O si el valor no es un objeto {@link IntBuffer}.
     *
     * @param key
     *            Una cadena clave.
     * @return Una buffer que es el valor.
     */
    public IntBuffer optIntBuffer(String key) {
        return this.optIntBuffer(key, null);
    }
    
    /**
     * Obtenga un IntBuffer opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una buffer que es el valor.
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
     * Obtenga un ByteBuffer opcional asociada con una clave. Devuelve <code>null</code>.
     * Si no existe tal clave. O si el valor no es un objeto {@link ByteBuffer}.
     *
     * @param key
     *            Una cadena clave.
     * @return Una buffer que es el valor.
     */
    public ByteBuffer optByteBuffer(String key) {
        return this.optByteBuffer(key, null);
    }
    
    /**
     * Obtenga un ByteBuffer opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una buffer que es el valor.
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
     * Obtenga un ShortBuffer opcional asociada con una clave. Devuelve <code>null</code>.
     * Si no existe tal clave. O si el valor no es un objeto {@link ShortBuffer}.
     *
     * @param key
     *            Una cadena clave.
     * @return Una buffer que es el valor.
     */
    public ShortBuffer optShortBuffer(String key) {
        return this.optShortBuffer(key, null);
    }
    
    /**
     * Obtenga un ShortBuffer opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una buffer que es el valor.
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
     * Obtenga una savable opcional asociada con una clave.Devuelve el nulo <code>null</code>
     *  si no existe tal clave.
     * 
     * @param <E>
     *          Tipo savable.
     * @param key
     *            Una cadena clave.
     * @return Una cadena que es el valor.
     */
    public <E extends Savable> E optSavable(String key) {
        return this.optSavable(key, null);
    }
    
    /**
     * Obtenga una savable opcional asociada con una clave.Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param <E>
     *          Tipo savable
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un savable que es el valor.
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
     * Método encargado de generar un clon de este objeto.
     * @return clon objeto.
     */
    @Override
    public Properties clone() {
        try {
            // Clonamos el objeto JmeProperties
            Properties clon = (Properties) 
                               super.clone();
            
            // Objeto clonaro.
            final Cloner cloner = new Cloner();
            
            // Ya que la clonacion solo afecta de manera superficial
            // los objeto, tambine clonaremos el Map junto con sus
            // datos que soportan la clonacion.
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
                        
                        // ¡Clonamos el objeto!.
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
            
            // Devolvemos el objeto clonado junto con
            // su propiedades.
            return clon;
        } catch (CloneNotSupportedException e) {
            // Si da un erro o por alguna razon JmeProperties
            // no soporto la clonacion.
            throw new InternalError(e);
        }
    }
    
    /**
     * Envuelva un objeto, si es necesario. Si el objeto es <code>null</code>, devuelve NULL
     * objeto. Si es una matriz o una colección, envuélvala en un JmeArray. Si esto es
     * un mapa, envuélvalo en un JmeProperties. Si es una propiedad estándar (Double,
     * String, etc.) entonces ya está envuelto. En caso contrario, si procede
     * uno de los paquetes java, convertirlo en una cadena. Y si no es así, prueba
     * para envolverlo en un JmeProperties. Si el ajuste falla, se devuelve nulo.
     *
     * @param object
     *            El objeto a envolver
     * @return El valor envuelto
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
     * Genera el codigo hahs de la clase u objeto {@link Properties}.
     * 
     * @return codigo hahs del objeto.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.properties);
        return hash;
    }
    
    /**
     * Un objeto {@link Properties} es igual a sí mismo.
     *
     * @param obj
     *            Un objeto para probar la nulidad.
     * @return true si el parámetro del objeto es el objeto JmeProperties o
     *         el mismo.
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
     * Coloque un par clave/boolean en el JmeProperties
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un boolean que es el valor.
     * @return this.
     * @throws RuntimeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public Properties put(String key, boolean value) throws RuntimeException{
        return this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
    }
    
    /**
     * Coloque un par clave/double en el JmeProperties.
     *
     * @param key
     *            Una cadena como clave.
     * @param value
     *            Un double como valor-
     * @return this.
     * @throws RuntimeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la lcave es <code>null</code>.
     */
    public Properties put(String key, double value) throws RuntimeException {
        return this.put(key, Double.valueOf(value));
    }
    
    /**
     * Coloque un par clave/float en el JmeProperties.
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un float como valor
     * @return this.
     * @throws RuntimeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public Properties put(String key, float value) throws RuntimeException {
        return this.put(key, Float.valueOf(value));
    }
    
    /**
     * Coloque un par clave/int en el JmeProperties.
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un int como valor.
     * @return this.
     * @throws RuntimeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public Properties put(String key, int value) throws RuntimeException{
        return this.put(key, Integer.valueOf(value));
    }
    
    /**
     * Coloque un par clave/long en el JmeProperties
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un long como valor..
     * @return this.
     * @throws RuntimeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public Properties put(String key, long value) throws RuntimeException {
        return this.put(key, Long.valueOf(value));
    }
    
    /**
     * Coloque un par clave/byte en el JmeProperties
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un byte como valor..
     * @return this.
     * @throws RuntimeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public Properties put(String key, byte value) throws RuntimeException {
        return this.put(key, Byte.valueOf(value));
    }
    
    /**
     * Coloque un par clave/char en el JmeProperties
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un char como valor..
     * @return this.
     * @throws RuntimeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public Properties put(String key, char value) throws RuntimeException {
        return this.put(key, Character.valueOf(value));
    }
    
    /**
     * Coloque un par clave/short en el JmeProperties
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un short como valor..
     * @return this.
     * @throws RuntimeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public Properties put(String key, short value) throws RuntimeException {
        return this.put(key, Short.valueOf(value));
    }
    
    /**
     * Coloque un par clave/valor en el JmeProperties. Si el valor es <code>null</code>, entonces el
     * La clave se eliminará del JmeProperties si está presente.
     *
     * @param key
     *            Un cadena como clave..
     * @param value
     *            Un objeto que es el valor. debe ser de uno de estos
     *            tipos: boolean, double, int, JmeArray, JmeProperties, long,
     *            String, o el objeto JmeNull.
     * @return this.
     * @throws RuntimeException
     *             Si el valor es un número no finito o el valor no est
     *              soportado.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
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

    private static boolean numberIsFinite(Number n) {
        if (n instanceof Double && (((Double) n).isInfinite() || ((Double) n).isNaN())) {
            return false;
        } else if (n instanceof Float && (((Float) n).isInfinite() || ((Float) n).isNaN())) {
            return false;
        }
        return true;
    }
    
     /**
     * Comprueba si el valor debe probarse como un decimal. No prueba si hay dígitos reales.
     *
     * @param val 
     *          valor a probar
     * @return verdadero si la cadena es "-0" o si contiene '.', 'e' o 'E', falso en caso contrario.
     */
    protected static boolean isDecimalNotation(final String val) {
        return val.indexOf('.') > -1 || val.indexOf('e') > -1
                || val.indexOf('E') > -1 || "-0".equals(val);
    }
    
   /**
     * Convierte una cadena en un número usando el tipo más estrecho posible. Posible
     * Los resultados de esta función son BigDecimal, Double, BigInteger, Long e Integer.
     * Cuando se devuelve un Doble, siempre debe ser un Doble válido y no NaN o +-infinito.
     *
     * @param val 
     *          valor para convertir
     * @return Representación numérica del valor.
     * @throws NumberFormatException 
     *                  lanzado si el valor no es un número válido. La persona
     *                  que llama debe captar esto y envolverlo en una {@link RuntimeException} si corresponde.
     */
    protected static Number stringToNumber(final String val) throws NumberFormatException {
        char initial = val.charAt(0);
        if ((initial >= '0' && initial <= '9') || initial == '-') {
            // representación decimal
            if (isDecimalNotation(val)) {
                // Usar un BigDecimal todo el tiempo para mantener el original
                // representación. BigDecimal no admite -0.0, asegúrese de que
                // mantener eso forzando un decimal.
                try {
                    BigDecimal bd = new BigDecimal(val);
                    if(initial == '-' && BigDecimal.ZERO.compareTo(bd)==0) {
                        return -0.0d;
                    }
                    return bd;
                } catch (NumberFormatException retryAsDouble) {
                    // esto es para soportar "Hex Floats" como este: 0x1.0P-1074
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
            // bloquea elementos como 00 01, etc. Los analizadores de números de Java los tratan como octales.
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
            // representación de enteros.
            // Esto reducirá cualquier valor a la representación de objeto razonable más pequeña
            // (Integer, Long o BigInteger)

            // Conversión descendente de BigInteger: Usamos una comparación de longitud de bits similar a
            // BigInteger#intValueExact usa. Aumenta GC, pero los objetos se mantienen
            // solo lo que necesitan. es decir, menos sobrecarga de tiempo de ejecución si el valor es
            // larga vida.
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
     * Lanza una excepción si el objeto es un NaN o un número infinito.
     *
     * @param o
     *            El objeto a probar.
     * @throws RuntimeException
     *             Si o es un número no finito.
     */
    protected static void testValidity(Object o) throws RuntimeException {
        if (o instanceof Number && !numberIsFinite((Number) o)) {
            throw new RuntimeException("JmeProperties does not allow non-finite numbers.");
        }
    }
    
    /**
     * Método encargado de determinar si existe una propiedad.
     * @param key nombre clave.
     * @return estado.
     * @deprecated utilizar {@code has()}
     */
    @Deprecated(since = "2.5.0")
    public boolean contains(String key) {
        return this.properties.containsKey(key);
    }
    
    /**
     * Eliminar un nombre y su valor, si está presente.
     *
     * @param key
     *            El nombre que se va a eliminar.
     * @return El valor que se asoció con el nombre, o nulo si no
     *         hubo valor.
     */
    public Object remove(String key) {
        return this.properties.remove(key);
    }
    
    /**
     * Establece una nueva propiedad.
     * @param <T> tipo de dato.
     * @param key nombre clave.
     * @param value valor a guardar.
     * @return este.
     */
    @Deprecated(since = "2.5.0")
    public <T extends Object> Properties setProperty(String key, T value) {
        this.properties.put(key, new Property(value));
        return this;
    }

    /**
     * Método encargado de buscar y devolver una propiedad.
     * @param <T> tipo de dato.
     * @param key nombre clave.
     * @param defaultVal valor predeterminado.
     * @return valor buscado.
     */
    @Deprecated(since = "2.5.0")
    public <T extends Object> T getProperty(String key, T defaultVal) {
        Property userData = (Property) this.properties.get(key);
        if (userData == null) {
            return defaultVal;
        }
        return (T) userData.getValue();
    }
    
    /**
     * Método encargado de buscar y devolver una propiedad.
     * @param <T> tipo de dato.
     * @param key nombre clave.
     * @return valor buscado.
     */
    @Deprecated(since = "2.5.0")
    public <T extends Object> T getProperty(String key) {
        return getProperty(key, null);
    }
    
    /**
     * Método encargado de gestionar los nombres claves de las propiedades.
     * @return nombres claves.
     */
    public Iterator<String> propertyNames() {
        return this.properties.keySet().iterator();
    }
    
    /**
     * (non-JavaDoc).
     * 
     * @param ex JmeExporter.
     * @see Savable#write(com.jme3.export.JmeExporter) 
     * 
     * @throws IOException Excepción.
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.writeStringSavableMap(properties, "Properties", null);
    }

    /**
     * (non-JavaDoc).
     * 
     * @param im JmeImporter
     * @see Savable#read(com.jme3.export.JmeImporter) 
     * 
     * @throws IOException Excepción.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);        
        properties = (HashMap<String, Savable>) in.readStringSavableMap("Properties", properties);
    }
    
    /**
     * Produce una cadena entre comillas dobles con secuencias de barra invertida en todos los
     * lugares correctos. Se insertará una barra invertida dentro de &lt;/, produciendo
     * &lt;\/, lo que permite que el texto de propieades se entregue en HTML. En texto de propiedades, una
     * cadena no puede contener un carácter de control o una comilla sin escape o
     * barra invertida.
     *
     * @param string
     *            Una cadena
     * @return Una cadena con el formato correcto para la inserción en un texto JmeProperties.
     */
    protected static String quote(String string) {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            try {
                return quote(string, sw).toString();
            } catch (IOException ignored) {
                // nunca sucedera - estamos escribiendo a un escritor de cadenas
                return "";
            }
        }
    }

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
     * Cree una nueva JmeException en un formato comun para conversiones incorrectas.
     * @param key nombre de la llave
     * @param valueType el tipo de valor al que se obliga
     * @param cause causa opcional de la falla de coerción
     * @return JmeException que se puede tirar.
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
        // no intente hacer cadenas de colecciones o tipos de objetos conocidos que podrian ser grandes.
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
