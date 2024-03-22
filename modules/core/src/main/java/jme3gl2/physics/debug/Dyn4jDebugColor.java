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
 * Clase utilizada para enlistar los colores del depurador de formas físicas.
 * @author wil
 * @version 1.0.0
 * @since 2.5.0
 */
public final class Dyn4jDebugColor {
    
    /** Color utilizado para los cuerpos físicos deshabilitados. */
    public static final ColorRGBA DISABLED = new ColorRGBA(0.376F, 0.490F, 0.545F, 1.0F);
    
    /** Color para formas: sensor. */
    public static final ColorRGBA SENSOR = new ColorRGBA(0.216F, 0.278F, 0.310F, 1.0F);
    
    /** Color utilizada para cuerpos definodo como: bullet. */
    public static final ColorRGBA BULLET = new ColorRGBA(1.0F, 0.09F, 0.267F, 1.0F);
    
    /** Color utilizada para cuerpos definodo como: static. */
    public static final ColorRGBA STATIC = new ColorRGBA(0.271F, 0.153F, 0.627F, 1.0F);
    
    /** Color utilizada para cuerpos definodo como: kinematic. */
    public static final ColorRGBA KINEMATIC = new ColorRGBA(0.98F, 0.463F, 0.824F, 1.0F);
    
    /** Color utilizada para cuerpos definodo como: kinematic. */
    public static final ColorRGBA AT_RESET = new ColorRGBA(0.302F, 0.714F, 0.675F, 1.0F);
    
    /** Color utilzado por cualquier cuerpos físico. */
    //0.267F, 0.541F, 1.0F, 1.0F
    public static final ColorRGBA DEFAULT = ColorRGBA.Blue.clone();
    
    /** Color urilizado para <code>Bounds</code>.*/
    public static final ColorRGBA BOUNDS = new ColorRGBA(0.463F, 1.0F, 0.012F, 1.0F);
    
    /** Constructor perteneciente. */
    private Dyn4jDebugColor() {}
}
