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
package jme3gl2.jawt;

import java.awt.*;
import java.util.*;

/**
 * Class in charge of implementing the '<code>Jme3GL2DisplaySystem</code> interface 
 * where the resolution of supported screens is determined with the help of the 
 * <b>Java AWT API</b>
 * 
 * @author wil
 * @version 1.1.1
 * @since 2.0.0
 */
final class Jme3GL2DesktopDisplaySystem implements Jme3GL2DisplaySystem {
    
    /** Device default graphics. */
    private final GraphicsDevice device;    
    /** AWT resolution arrray. */
    private final DisplayMode[] modes;

    /**
     * Constructor.
     */
    public Jme3GL2DesktopDisplaySystem() {
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        modes  = device.getDisplayModes();
        
        // sort the list of resolutions.
        Arrays.sort(modes, new DisplayModeSorter());
    }
    
    /**
     * (non-Javadoc)
     * @see org.jmegl.jawt.Jme3GL2DisplaySystem#getResolutions() 
     * @return object
     */
    @Override
    public JAWTResolution[] getResolutions() {
        return getWindowedResolutions();
    }

    /**
     * (non-Javadoc)
     * @see org.jmegl.jawt.Jme3GL2DisplaySystem#getFullScreenResolution() 
     * @return object
     */
    @Override
    public JAWTResolution getFullScreenResolution() {        
        DisplayMode mode = device.getDisplayMode();
        JAWTResolution resolution = new JAWTResolution(mode.getWidth(), mode.getHeight());
        
        resolution.setBitDepth(mode.getBitDepth());
        resolution.setRefreshRate(mode.getRefreshRate());
        return resolution;
    }

    /**
     * (non-Javadoc)
     * @see org.jmegl.jawt.Jme3GL2DisplaySystem#isFullScreenSupported() 
     * @return boolean
     */
    @Override
    public boolean isFullScreenSupported() {
        return device.isFullScreenSupported();
    }

    /**
     * (non-Javadoc)
     * @see org.jmegl.jawt.Jme3GL2DisplaySystem#isDisplayChangeSupported() 
     * @return boolean
     */
    @Override
    public boolean isDisplayChangeSupported() {
        return device.isDisplayChangeSupported();
    }
    
    /**
     * Returns a list of current screen resolutions.
     * @return resolutions
     */
    private JAWTResolution[] getWindowedResolutions() {
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
    
    /**
     * Returns a list of resolutions greater than the minimum set by the 
     * {@link jme3gl2.jawt.JAWTResolution} class but less than the given limit
     * 
     * @param heightLimit screen height limit
     * @param widthLimit screen width limit
     * @return resolutions
     */
    private JAWTResolution[] getResolutions(int heightLimit, int widthLimit) {        
        final ArrayList<JAWTResolution> resolutions = new ArrayList<>();
        for (final DisplayMode element : this.modes) {
            if (element == null)
                continue;
            
            int height = element.getHeight(),
                width  = element.getWidth();
            
            if (width >= JAWTResolution.MIN_WIDTH && height >= JAWTResolution.MIN_HEIGHT) {
                if (height >= heightLimit) {
                    height = heightLimit;
                }
                if (width >= widthLimit) {
                    width = widthLimit;
                }
                                
                JAWTResolution resolution = new JAWTResolution(width, height);
                resolution.setBitDepth(element.getBitDepth());
                resolution.setRefreshRate(element.getRefreshRate());
                
                if (!resolutions.contains(resolution)) {
                    resolutions.add(resolution);
                }
            }
        }        
        return resolutions.toArray(JAWTResolution[]::new);
    }
    
    /**
     * Comparative class in charge of organizing the list of resolutions where 
     * the {@link java.lang.Comparable} interface is implemented.
     */
    static final class DisplayModeSorter implements Comparator<DisplayMode> {
        
        /**
         * (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object) s
         * 
         * @param a object 'a'
         * @param b object 'b'
         * @return int
         */
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
