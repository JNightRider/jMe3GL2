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
package jme3gl2.physics.debug;

import com.jme3.math.ColorRGBA;

/**
 * Clase enumerada <code>Color</code> encargado de gestionar los colores que
 * se utiliza para depurar los cuerpos físicos.
 * <p>
 * Cada color porprocionado por esta clase enumerada, define características
 * unicas según sea el caso.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT 
 * 
 * @since 2.5.0
 */
public enum Color {
    
    /** Color para cuerpos dormidos. */
    BLUE(ColorRGBA.Blue),

    /** Color para cuerpos definidos como <code>Bullet</code>. */
    RED(ColorRGBA.Red),

    /** Color definir los límites del espacio físico. */
    GREEN(ColorRGBA.Green),

    /** Color para cuerpos definidos como <code>Kinematic</code>. */
    YELLOW(ColorRGBA.Yellow),

    /** Color para cuerpos activos. */
    MAGENTA(ColorRGBA.Magenta),

    /** Color para uso general. */
    PINK(ColorRGBA.Pink),

    /** Color para cuerpos definidos como <code>Static</code>. */
    ORANGE(ColorRGBA.Orange),

    /** Color para cuerpos deshabilitados. */
    GRAY(ColorRGBA.Gray);
    
    /** Color RGBA utilizado por <code>JME</code>.*/
    private ColorRGBA color;
    
    /**
     * {@code true} de manera predeterminada, esto se utiliza para crear el
     * material de los {@code Spatial} para la depuración de los cuerpos
     * físicos.
     */
    private boolean wireFrame;

    /**
     * Constructor de la clase enumerada <code>Color</code> donde se establecen
     * las características un color.
     * 
     * @param color color-RGBA
     */
    private Color(ColorRGBA color) {
        this(color, true);
    }
    
    /**
     * Constructor de la clase enumerada <code>Color</code> donde se establecen
     * las características un color.
     * 
     * @param color color-RGBA
     * @param wireFrame un booleano.
     */
    private Color(ColorRGBA color, boolean wireFrame) {
        this.color = color;
        this.wireFrame = wireFrame;
    }
    
    /**
     * Devuelve el color RGBA utilizado por <code>JME</code>.
     * @return color-rgba
     */
    public ColorRGBA getColor() {
        return color;
    }

    /**
     * Devuelve un valor lógico para la creación de los materiales.
     * @return boolean.
     */
    public boolean isWireFrame() {
        return wireFrame;
    }

    /**
     * Método encargado de generar una cadena con la información del color.
     * @return información de este color.
     */
    @Override
    public String toString() {
        return "[ GL ] >> " + "color=" + color + ", wireFrame=" + wireFrame + '.';
    }
}
