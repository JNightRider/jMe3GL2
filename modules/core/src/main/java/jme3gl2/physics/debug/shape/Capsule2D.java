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
 * Clase <code>Capsule2D</code> encargado de generar una forma de cápsula.
 * @author wil
 * @version 1.0-SNAPSHOT 
 * @since 2.5.0
 */
public class Capsule2D extends AbstractShape2D {
    
    /** Contador predeterminado para los bordes-semi circulos de la cápsula. */
    public static final int COUNT = 12;

    /**
     * Constructor de la clase <code>GLCapsule</code> donde se genera la forma
     * de una cápsula.
     * @param count cantidad de 'pines' para los border circulares, entre mayor
     *              sea la cantidad se mejorara el semicirculo. Tenga cuidado ya
     *              entre mayor sea el número más vértices se utilizan.
     * @param width largo de la forma.
     * @param height ancho de la forma.
     * @param deep profundidad en el mundo 3D.
     */
    public Capsule2D(int count, float width, float height, float deep) {
        Capsule2D.this.updateGeometry(count, width, height, deep);
    }

    /**
     * Método encargado de actualizar las geometrías de esta malla.
     * @param count cantidad de 'pines' para los border circulares, entre mayor
     *              sea la cantidad se mejorara el semisirculo. Tenga cuidado ya
     *              entre mayor sea el número más vértices se utilizan.
     * @param width largo de la forma.
     * @param height ancho de la forma.
     * @param deep profundidad en el mundo 3D.
     */
    public void updateGeometry(int count, float width, float height, float deep) {
        // calcular el incremento angular
        final float pin = FastMath.PI / (count + 1);
        // 4 verts rectas más 2 * medias verts circulares
        final Vector3f[] vertices = new Vector3f[4 + 2 * count];

        final float c = FastMath.cos(pin);
        final float s = FastMath.sin(pin);
        float t = 0;

        // get the major and minor axes
        float major = width;
        float minor = height;
        boolean vertical = false;
        if (width < height) {
            major = height;
            minor = width;
            vertical = true;
        }

        // obtener el radio del eje menor
        float radius = minor * 0.5f;

        // calcular las compensaciones x/y
        float offset = major * 0.5f - radius;
        float ox = 0;
        float oy = 0;
        if (vertical) {
            // alineado con la y
            oy = offset;
        } else {
            // alineado con la x
            ox = offset;
        }

        int n = 0;

        // Tapón derecho
        float ao = vertical ? 0 : FastMath.PI * 0.5f;
        float x = radius * FastMath.cos(pin - ao);
        float y = radius * FastMath.sin(pin - ao);
        for (int i = 0; i < count; i++) {
            vertices[n++] = new Vector3f(x + ox, y + oy, deep);

            //aplicar la matriz de rotación
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }

        // agregar vértices superior/izquierdo
        if (vertical) {
            vertices[n++] = new Vector3f(-radius, oy, deep);
            vertices[n++] = new Vector3f(-radius, -oy, deep);
        } else {
            vertices[n++] = new Vector3f(ox, radius, deep);
            vertices[n++] = new Vector3f(-ox, radius, deep);
        }

        // tapa izquierda
        ao = vertical ? FastMath.PI : FastMath.PI * 0.5f;
        x = radius * FastMath.cos(pin + ao);
        y = radius * FastMath.sin(pin + ao);
        for (int i = 0; i < count; i++) {
            vertices[n++] = new Vector3f(x - ox, y - oy, deep);

            //aplicar la matriz de rotación
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }

        // agregar vértices inferior/derecho
        if (vertical) {
            vertices[n++] = new Vector3f(radius, -oy, deep);
            vertices[n++] = new Vector3f(radius, oy, deep);
        } else {
            vertices[n++] = new Vector3f(-ox, -radius, deep);
            vertices[n++] = new Vector3f(ox, -radius, deep);
        }
        
        updateGeometry(vertices);
    }
}
