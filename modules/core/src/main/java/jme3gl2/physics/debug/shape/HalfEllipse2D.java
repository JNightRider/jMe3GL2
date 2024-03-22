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
package jme3gl2.physics.debug.shape;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * Clase <code>HalfEllipse2D</code> encargado de generar una forma eliptica.
 * @author wil
 * @version 1.0-SNAPSHOT 
 * @since 2.5.0
 */
public class HalfEllipse2D extends AbstractShape2D {
    
    /** Cantidad miníma de poligonos para una elipse decente. */
    public static final int COUNT = 15;

    /**
     * Constructor de la clase <code>HalfEllipse</code> para generar una forma de
     * eliptica en '2D' con una profundidad establecida para toda/o los vértices.
     * 
     * @param count cantidad de vértices-poligonos a usar.
     * @param width laergo de la elipse.
     * @param height ancho de la elipse.
     * @param deep profundidad.
     */
    public HalfEllipse2D(int count, float width, float height, float deep) {
        HalfEllipse2D.this.updateGeometry(count, width, height, deep);
    }

    /**
     * Método encargado de actualizar las geometrías de esta malla.
     * @param count cantidad de vértices-poligonos a usar.
     * @param width laergo de la elipse.
     * @param height ancho de la elipse.
     * @param deep profundidad.
     */
    public void updateGeometry(int count, float width, float height, float deep) {
        final float a = width * 0.5f;
        final float b = height * 0.5f;

        // calcular el incremento angular
        final float inc = FastMath.PI / (count + 1);
        // asegúrese de que la salida resultante sea un número par de vértices
        final Vector3f[] vertices = new Vector3f[count + 2];

        // establecer los vértices inicial y final
        vertices[0] = new Vector3f(a, 0, deep);
        vertices[count + 1] = new Vector3f(-a, 0, deep);

        // Usar las ecuaciones paramétricas:
        // x = a * cos(t)
        // y = b * sin(t)
        for (int i = 1; i < count + 1; i++) {
            final float t = inc * i;
            // Dado que el lado inferior de la elipse es el mismo que el 
            // lado superior, solo que con una y negada, ahorremos algo de 
            // tiempo creando el lado inferior al mismo tiempo.
            final float x = a * FastMath.cos(t);
            final float y = b * FastMath.sin(t);
            vertices[i] = new Vector3f(x, y, deep);
        }
        this.updateGeometry(vertices);
    }
}
