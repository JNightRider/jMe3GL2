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
import jme3gl2.geometry.Jme3GL2Geometry;
import jme3gl2.util.Converter;

/**
 * Clase <code>Circle2D</code> encargado de generar una forma de circulo.
 * @author wil
 * @version 1.0-SNAPSHOT
 * @since 2.5.0
 */
public class Circle2D extends AbstractShape2D {
    
    /** Contador predeterminado para generar un circulo. */
    public static final int COUNT = 24;
    
    /**
     * Constructor de la clase <code>Circle2D</code> donde se establecern los
     * valores para generar un forma circular.
     * 
     * @param count cantidad de poligonos.
     * @param radius radio del circulo.
     * @param theta theta desado.
     * @param deep profundidad.
     */
    public Circle2D(int count, float radius, float theta, float deep) {
        Circle2D.this.updateGeometry(count, radius, theta, deep);
    }

    /**
     * Método encargado de actualizar la geometría de esta malla circular.
     * @param count cantidad de vertices-poligonos.
     * @param radius radio del circulo.
     * @param theta un theta para el circulo.
     * @param deep profundidad.
     */
    public void updateGeometry(int count, float radius, float theta, float deep) {
        // calcular el incremento angular
        final float pin = Converter.toFloat(Jme3GL2Geometry.TWO_PI / count);
        // make sure the resulting output is an even number of vertices
        final Vector3f[] vertices = new Vector3f[count];

        final float c = FastMath.cos(pin);
        final float s = FastMath.sin(pin);
        float t = 0;

        float x = radius;
        float y = 0;
        // inicializar en theta si es necesario
        if (theta != 0) {
            x = radius * FastMath.cos(theta);
            y = radius * FastMath.sin(theta);
        }

        for (int i = 0; i < count; i++) {
            vertices[i] = new Vector3f(x, y, deep);

            //aplicar la matriz de rotación
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }
        updateGeometry(vertices);
    }
}
