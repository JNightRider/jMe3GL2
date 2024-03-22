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
package jme3gl2.renderer;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

/**
 * Un objeto de la clase <code>jMe3GL2Clipping</code> se encarga de gestionar los
 * cortes de la cámara 2D.
 * <p>
 * En esta clase es donde se define el corte minimo y maxima, tanto un atrazo
 * como un adelanto de la cámara respector al objetivo que tiene.
 * </p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT 
 * 
 * @since 2.0.0
 */
public class Jme3GL2Clipping {
    
    /** Recorte minimo. */
    private Vector2f minimum;
    
    /** Recorte maximo. */
    private Vector2f maximum;
    
    /** Punto de atraso o adelanto.
     * <p>
     * Es decir, el desface que tendrá la cámara con respector a su
     * objetivo(si tiene uno establecido).
     * </p>
     */
    private Vector2f offset;

    /**
     * Constructor de la clase <code>Clipping</code> donde se inicializa los
     * objetos de recorte y desface.
     */
    public Jme3GL2Clipping() {
        this.maximum = new Vector2f(0.0F, 0.0F);
        this.maximum = new Vector2f(0.0F, 0.0F);
        this.offset  = new Vector2f(0.0F, 0.0F);
    }
    
    /**
     * Método encargado de calcular la posición de la cámara dentro de los
     * rangos de recortes.
     * 
     * @param x posición del objeto en el eje {@code x}.
     * @param y posición del objeto en el eje {@code y}.
     * @return nueva posición calculada.
     */
    public Vector2f clamp(float x, float y) {
        if (isClipping()) {
            return new Vector2f(FastMath.clamp((x + offset.x), minimum.x, maximum.x), 
                                FastMath.clamp((y + offset.y), minimum.y, maximum.y));
        }
        return new Vector2f(x + offset.x, y + offset.y);
    }

    /**
     * Determina si hay un recorte que aplicar.
     * @return un valor boolean.
     */
    public boolean isClipping() {
        return minimum != null && maximum != null;
    }
    
    /**
     * Establece un nuevo recorte <code>Minima</code>.
     * @param minimum nuevo recorte.
     */
    public void setMinimum(Vector2f minimum) {
        this.minimum = minimum;
    }

    /**
     * Establece un nuevo recorte <code>Maxima</code>.
     * @param maximum nuevo recorte.
     */
    public void setMaximum(Vector2f maximum) {
        this.maximum = maximum;
    }

    /**
     * Método encargado de establecer una nueva desface. Si el valor es
     * <code>null</code>, dicho desfaces será de <code>0</code>.
     * @param offset nuevo punto de esface.
     */
    public void setOffset(Vector2f offset) {
        if (offset == null) {
            this.offset.zero();
        } else {
            this.offset = offset;
        }
    }

    /**
     * Devuelve un valor de recorte-minima.
     * @return recorte.
     */
    public Vector2f getMinimum() {
        return minimum;
    }

     /**
     * Devuelve un valor de recorte-maxima.
     * @return recorte.
     */
    public Vector2f getMaximum() {
        return maximum;
    }

    /**
     * Devuelve el valor del desface.
     * @return desface.
     */
    public Vector2f getOffset() {
        return offset;
    }
}
