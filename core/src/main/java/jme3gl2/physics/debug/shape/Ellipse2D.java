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
 * Clase <code>Ellipse2D</code> encargado de generar una forma de elipse.
 * @author wil
 * @version 1.0-SNAPSHOT 
 * @since 2.5.0
 */
public class Ellipse2D extends AbstractShape2D {
    
    /** Cantidad miníma de poligonos para una elipse decente. */
    public static final int COUNT = 30;

    /**
     * Constructor de la clase <code>Ellipse2D</code> para generar una forma de
     * elipce en '2D' con una profundidad establecida para toda/o los vértices.
     * 
     * @param count cantidad de vértices-poligonos a usar.
     * @param width laergo de la elipse.
     * @param height ancho de la elipse.
     * @param deep profundidad.
     */
    public Ellipse2D(int count, float width, float height, float deep) {
        Ellipse2D.this.updateGeometry(count, width, height, deep);
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

        final int n2 = count / 2;
        // calcular el incremento angular
        final float pin2 = FastMath.PI / n2;
        // asegúrese de que la salida resultante sea un número par de vértices
        final Vector3f[] vertices = new Vector3f[n2 * 2];

        // Usar las ecuaciones paramétricas:
        // x = a * cos(t)
        // y = b * sin(t)
        int j = 0;
        for (int i = 0; i < n2 + 1; i++) {
            final float t = pin2 * i;
            // Dado que el lado inferior de la elipse es el mismo que el lado 
            // superior, solo que con una y negada, ahorremos algo de tiempo 
            // creando el lado inferior al mismo tiempo.
            final float x = a * FastMath.cos(t);
            final float y = b * FastMath.sin(t);
            if (i > 0) {
                vertices[vertices.length - j] = new Vector3f(x, -y, deep);
            }
            vertices[j++] = new Vector3f(x, y, deep);
        }
        this.updateGeometry(vertices);
    }
}
