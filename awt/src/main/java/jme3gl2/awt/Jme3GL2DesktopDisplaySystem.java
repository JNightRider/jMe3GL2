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
package jme3gl2.awt;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Configuracion de pantalla para PC.
 * 
 * @author wil
 * @version 1.1-SNAPSHOT
 * 
 * @since 2.0.0
 */
final class Jme3GL2DesktopDisplaySystem implements Jme3GL2DisplaySystem {

    private final GraphicsDevice device;
    private final DisplayMode[] modes;

    public Jme3GL2DesktopDisplaySystem() {
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        modes  = device.getDisplayModes();
        Arrays.sort(modes, new DisplayModeSorter());
    }
        
    @Override
    public AWTResolution[] getResolutions() {
        return getWindowedResolutions();
    }

    @Override
    public AWTResolution getFullScreenResolution() {
        //AWTResolution resolution = new AWTResolution();
        //
        //final AWTResolution[] resAll;
        //resAll = getWindowedResolutions();
        //        
        //for (final AWTResolution res : resAll) {
        //    if (resolution.getWidth() < res.getWidth() 
        //            && resolution.getHeight() < res.getHeight()) {
        //        resolution = res.clone();
        //    }
        //}
        
        DisplayMode mode = device.getDisplayMode();
        AWTResolution resolution = new AWTResolution(mode.getWidth(), mode.getHeight());
        
        resolution.setBitDepth(mode.getBitDepth());
        resolution.setRefreshRate(mode.getRefreshRate());
        return resolution;
    }

    @Override
    public boolean isFullScreenSupported() {
        return device.isFullScreenSupported();
    }

    @Override
    public boolean isDisplayChangeSupported() {
        return device.isDisplayChangeSupported();
    }
    
    private Insets getTempInsets() {
        return new Insets(0, 0, 0, 0);
    }
    
    private AWTResolution[] getWindowedResolutions() {
        int maxWidth  = 0,
            maxHeight = 0;
        
        for (final DisplayMode element : this.modes) {
            if ( element == null )
                continue;
            
            if (maxWidth < element.getWidth()) {
                maxWidth = element.getWidth();
            }
            
            if (maxHeight < element.getHeight()) {
                maxHeight = element.getHeight();
            }
        }
        
        return getResolutions(maxHeight, maxWidth);
    }
    
    private AWTResolution[] getResolutions(int heightLimit, int widthLimit) {
        Insets insets = getTempInsets();
        heightLimit -= insets.top + insets.bottom;
        widthLimit -= insets.left + insets.right;
        
        final ArrayList<AWTResolution> resolutions = new ArrayList<>();
        for (final DisplayMode element : this.modes) {
            if (element == null)
                continue;
            
            int height = element.getHeight(),
                width  = element.getWidth();
            
            if (width >= AWTResolution.MIN_WIDTH && height >= AWTResolution.MIN_HEIGHT) {
                if (height >= heightLimit) {
                    height = heightLimit;
                }
                if (width >= widthLimit) {
                    width = widthLimit;
                }
                                
                AWTResolution resolution = new AWTResolution(width, height);
                resolution.setBitDepth(element.getBitDepth());
                resolution.setRefreshRate(element.getRefreshRate());
                
                if (!resolutions.contains(resolution)) {
                    resolutions.add(resolution);
                }
            }
        }
        
        return resolutions.toArray(AWTResolution[]::new);
    }
    
    // Clase comparado.
    private static final 
    class DisplayModeSorter implements Comparator<DisplayMode> {
        @Override
        public int compare(DisplayMode a, DisplayMode b) {
            // Width
            if (a.getWidth() != b.getWidth()) {
                return (a.getWidth() > b.getWidth()) ? 1 : -1;
            }
            // Height
            if (a.getHeight() != b.getHeight()) {
                return (a.getHeight() > b.getHeight()) ? 1 : -1;
            }
            // Bit depth
            if (a.getBitDepth() != b.getBitDepth()) {
                return (a.getBitDepth() > b.getBitDepth()) ? 1 : -1;
            }
            // Refresh rate
            if (a.getRefreshRate() != b.getRefreshRate()) {
                return (a.getRefreshRate() > b.getRefreshRate()) ? 1 : -1;
            }
            // All fields are equal
            return 0;
        }
    }
}
