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

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.util.BufferUtils;

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
 * Un <code>Property</code> se encarga de administrar los tipos de datos que
 * puede guardar la clase {@link Properties} al crera un {@link Tile}.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 2.0.0
 */
@SuppressWarnings(value = {"unchecked"})
final class Property implements Savable, Cloneable {
    
    /** Tipo de dato que se este envolviendo. */
    private Type jmeType;
    
    /** Dato primitivo a envolver. */
    private Object object;
    
    /**
     * Constructor predeterminado.
     */
    public Property() {
    }

    /**
     * Construye un {@link Property} con un valor espefecificado
     * a envolver.
     * 
     * @param object 
     *          Valor primitivo a envolver.
     */
    public Property(Object object) {
        this.jmeType = Type.jmeValueOf(object);
        this.object  = object;
    }
    
    /**
     * Siempre que clonamos un {@code JmePrimitive} se hara de una manera profunda,
     * para clonar su valor y tipo de dato.
     * 
     * @throws InternalError Si ocurre un error interno(JVM) al clonar
     *                          el objeto/clase.
     * @return Clon del objeto generado.
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
     * Metodo auxiliar que se encarga de clonar el valor de esta clase, siempre
     * y cuando soporte la clonacion, los de tipo primitivo son una excepcion a
     * esa regla, dado que ellos solo se asignan.
     * 
     * @param obj
     *          Valor a clonar.
     * @param type
     *          El tipo del objeto que se clonara.
     * @return Valor clonado.
     */
    private Object clone0(Object obj, Type type) {
        switch (type) {
            case BitSet: return (BitSet) 
                                ((BitSet) obj).clone();
            case FloatBuffer:  return BufferUtils.clone((FloatBuffer) obj);
            case ShortBuffer:  return BufferUtils.clone((ShortBuffer) obj);            
            case ByteBuffer:   return BufferUtils.clone((ByteBuffer) obj);
            case IntBuffer:    return BufferUtils.clone((IntBuffer) obj);
            default:
                return obj;
        }
    }

    /**
     * Genera un text({@code String}) con el comportamiento de
     * la clase {@code JmePrimitive}.
     * @return Un string como valor.
     */
    @Override
    public String toString() {
        return '[' + "jmeType="  + jmeType 
                   + ", object=" + object + ']';
    }

    // Getters de la clase 'JmePrimitive' que devuelvel el tipo de dato que se
    // esta envolviendo, a si como el dato establecido.
    public Type getJmeType() { return jmeType; }
    public Object getValue()    { return object;  }

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
     * El metodo <code>readCharacter</code> tiene como funcionalidad, leer un
     * objeto de tipo {@code char}.
     * 
     * @param in
     *          Administrador de entradas jme.
     * @return un caracter({@code char}) como valor.
     * 
     * @throws IOException Si al leer los datos en la entrada, genera
     *                      algun erro o excepcion.
     * @throws InternalError Si el valor leido tiene mas de un caracter, se considera
     *                          un error interno, ya que estamos importando un {@code char}.
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
     * El metodo <code>readEnum</code> tiene como funcionalidad, leer un
     * objeto de tipo {@code enum}.
     * 
     * @param in
     *          Administrador de entradas jme.
     * @return Un valor enumerado {@link java.lang.Enum}.
     * 
     * @throws IOException Si al leer los datos en la entrada, genera
     *                      algun erro o excepcion.
     * @throws InternalError Si los valores solicitados para leer un {@code enum}
     *                          no son validos, se lanzara un error interno.
     */
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
     * El metodo <code>readBigNumber</code> tiene como funcionalidad, leer un
     * numero entero o decimal de gran capacidad conocidos como: <pre><code>
     * BigDecimal -> decimales
     * BigInteger -> enteros</code></pre>
     * 
     * @param in
     *          Administrador de entradas jme.
     * @param isDecimal 
     *              {@code true} si se va a leer un <code>BigDecimal</code>, de lo
     *                  contrario {@code false} para un <code>BigInteger</code>.
     * @return Un numero gigante/grande como valor.
     * 
     * @throws IOException Si al leer los datos en la entrada, genera
     *                      algun erro o excepcion.
     * @throws InternalError Si los valores solicitados no son <code>BigDecimal</code> o 
     *                          <code>BigInteger</code>, se lanzara un error interno.
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
     * Metodo encargado de generar un hash para la clase.
     * @return {@code hashCode} de la clase.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.jmeType);
        hash = 43 * hash + Objects.hashCode(this.object);
        return hash;
    }

    /**
     * Metodo encargado de comparar un objeto para luego determinar si son
     * iguales a nivel de contenido o asi mismo.
     * 
     * @param obj
     *          Un objeto para comprobar.
     * @return {@code true} si son iguales, de lo contrario
     *          devolveria {@code false} como valor.
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
