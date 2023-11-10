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
package jme3gl2.util;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Vector3;

/**
 * Clase <code>Converter</code> se encarga de proprocionar métodos de converción
 * entre vectores y/o números(float &harr; double).
 * 
 * @author wil
 * @version 1.2-SNAPSHOT
 * 
 * @since 1.0.0
 */
public final 
class Converter {
    
    /**
     * Constructor privado de la clase.
     */
    private Converter() {}
    
    /**
     * Método encargado de convertir un arreglo de <code>Vector3</code> a un
     * nuevo arreglo de <code>Vector3f</code>.
     * @param vertices arreglo de vectores a convertir.
     * @return nuevo arreglo.
     */
    public static Vector3f[] toArrayVector3f(final Vector3[] vertices) {
        final Vector3f[] vectors = new Vector3f[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            vectors[i] = toVector3f(vertices[i]);
        }
        return vectors;
    }
    
    /**
     * Método encargado de convertir un arreglo de <code>Vector2</code> a un
     * nuevo arreglo de <code>Vector3f</code>.
     * @param vertices arreglo de vectores a convertir.
     * @return nuevo arreglo.
     */
    public static Vector3f[] toArrayVector3f(final Vector2[] vertices) {
        final Vector3f[] vectors = new Vector3f[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            vectors[i] = toVector3f(vertices[i]);
        }
        return vectors;
    }
    
    /**
     * Convierte un vector {@code Vector2} en {@code Vector2f}.
     * @param v Vector a convertir.
     * @return Vector nuevo.
     */
    public static Vector2f toVector2f(Vector2 v) {
        float x = Double.valueOf(v.x).floatValue(),
              y = Double.valueOf(v.y).floatValue();
        return new Vector2f(x, y);
    }
    
    /**
     * Convierte un vector {@code Vector3} en {@code Vector3f}.
     * @param v Vector a convertir.
     * @return Vector nuevo.
     */
    public static Vector3f toVector3f(Vector3 v) {
        float x = Double.valueOf(v.x).floatValue(),
              y = Double.valueOf(v.y).floatValue(),
              z = Double.valueOf(v.z).floatValue();
        return new Vector3f(x, y, z);
    }
    
    /**
     * Convierte un vector {@code Vector2} en {@code Vector3f}.
     * @param v Vector a convertir.
     * @return Vector nuevo.
     */
    public static Vector3f toVector3f(Vector2 v) {
        float x = Double.valueOf(v.x).floatValue(),
              y = Double.valueOf(v.y).floatValue();
        return new Vector3f(x, y, 0.0F);
    }
    
    /**
     * Convierte un vector {@code Vector2f} en {@code Vector2}.
     * @param v Vector a convertir.
     * @return Vector nuevo.
     */
    public static Vector2 toVector2(Vector2f v) {
        return new Vector2(v.x, v.y);
    }
    
    /**
     * Convierte un vector {@code Vector3f} en {@code Vector2}.
     * @param v Vector a convertir.
     * @return Vector nuevo.
     */
    public static Vector2 toVector2(Vector3f v) {
        return new Vector2(v.x, v.y);
    }
    
    /**
     * Convierte un vector {@code Vector3f} en {@code Vector3}.
     * @param v Vector a convertir.
     * @return Vector nuevo.
     */
    public static Vector3 toVector3(Vector3f v) {
        return new Vector3(v.x, v.y, v.z);
    }
    
    /**
     * Convierte un numero {@code double} a {@code float}.
     * @param d número a convertir.
     * @return nuevo número.
     */
    public static float toFloat(double d) {
        return Double.valueOf(d).floatValue();
    }
    
    /**
     * Convierte un numero {@code float} a {@code double}.
     * @param f número a convertir.
     * @return nuevo número.
     */
    public static double toDouble(float f) {
        return Float.valueOf(f).doubleValue();
    }
}
