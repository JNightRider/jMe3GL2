/* Copyright (c) 2009-2024 jMonkeyEngine.
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
package org.jegl.utilities;

import com.jme3.math.ColorRGBA;
        
/**
 * Utility class for loading, managing and modifying RGBA colors.
 * 
 * @author wil
 * @version 1.0.5
 * @since 2.5.0
 */
public final class ColorUtilities {    
    /** Scale factor. */
    private static final float FACTOR = 0.7F;
    
    /**
     * Private constructor.
     */
    private ColorUtilities() {
    }
    
    /**
     * Returns a darker color than the current color.
     * 
     * @param color color
     * @return dark color
     */
    public static final ColorRGBA darker(ColorRGBA color) {
        return new ColorRGBA(Math.max((color.getRed()   * 255.0F) * FACTOR, 0) / 255.0F,
                             Math.max((color.getGreen() * 255.0F) * FACTOR, 0) / 255.0F,
                             Math.max((color.getBlue()  * 255.0F) * FACTOR, 0) / 255.0F,
                             color.getAlpha());
    }
    
    /**
     * Returns a lighter color than the current color.
     * 
     * @param color color
     * @return light color
     */
    public static final ColorRGBA brighter(ColorRGBA color) {
        float r = color.getRed()   * 255.0F;
        float g = color.getGreen() * 255.0F;
        float b = color.getBlue()  * 255.0F;
        float alpha = color.getAlpha();
        
        float i = (1.0f/(1.0f-FACTOR));
        if ( r == 0 && g == 0 && b == 0) {
            return new ColorRGBA(i, i, i, alpha);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;
        
        return new ColorRGBA(Math.min(r/FACTOR, 255.0F) / 255.0F,
                             Math.min(g/FACTOR, 255.0F) / 255.0F,
                             Math.min(b/FACTOR, 255.0F) / 255.0F,
                             alpha);
    }
}
