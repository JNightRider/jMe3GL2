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
 * Clase <code>Slice2D</code> encargado de generar una forma de rebanda.
 * @author wil
 * @version 1.0-SNAPSHOT
 * @since 2.5.0
 */
public class Slice2D extends AbstractShape2D {

    /** Contador predeterminado para generar una rebanada.*/
    public static final int COUNT = 24;
    
    /**
     * Constructor de la clase <code>Circle2D</code> donde se establecern los
     * valores para generar un forma de rabanada.
     * 
     * @param count cantidad de poligonos.
     * @param radius radio de la forma.
     * @param theta theta desado.
     * @param deep profundidad.
     */
    public Slice2D(int count, float radius, float theta, float deep) {
        Slice2D.this.updateGeometry(count, radius, theta, deep);
    }

    /**
     * Método encargado de actualizar la geometría de esta malla circular.
     * @param count cantidad de vertices-poligonos.
     * @param radius radio de la forma.
     * @param theta un theta.
     * @param deep profundidad.
     */
    public void updateGeometry(int count, float radius, float theta, float deep) {
        // calcular el incremento angular
        final float pin = theta / (count + 1);
        // asegúrese de que la salida resultante sea un número par de vértices
        final Vector3f[] vertices = new Vector3f[count + 3];

        final float c = FastMath.cos(pin);
        final float s = FastMath.sin(pin);
        float t = 0;

        // inicializar en menos theta
        float x = radius * FastMath.cos(-theta * 0.5f);
        float y = radius * FastMath.sin(-theta * 0.5f);

        // establecer el primer y último punto del arco
        vertices[0] = new Vector3f(x, y, deep);
        vertices[count + 1] = new Vector3f(x, -y, deep);

        for (int i = 1; i < count + 1; i++) {
            //aplicar la matriz de rotación
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
            // agregar un punto.
            vertices[i] = new Vector3f(x, y, deep);
        }

        // terminar añadiendo el origen
        vertices[count + 2] = new Vector3f();
        this.updateGeometry(vertices);
    }
}
