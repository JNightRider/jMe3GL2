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
package org.je3gl.jawt;

/**
 * An interface in charge of managing the user's screen to determine the resolutions 
 * of the monitor where the game is run.
 * <p>
 * This feature uses <code>AWT</code> components to determine screen resolutions, so 
 * it only works in a desktop environment (typically Android and IOS run games in full screen).
 * 
 * @author wil
 * @version 1.1.5
 * @since 2.0.0
 */
public interface Jme3GL2DisplaySystem {
    
    /**
     * Returns a series of resolutions that can be used.
     * @return {@link org.je3gl.jawt.JAWTResolution}
     */
    public JAWTResolution[] getResolutions();
    
    /**
     * Returns the resolution of the full screen, this solution will be the screen 
     * where the game is running.
     * 
     * @return {@link org.je3gl.jawt.JAWTResolution}
     */
    public JAWTResolution getFullScreenResolution();
    
    /**
     * Method responsible for determining whether the device supports full screen.
     * @return {@code true} if full screen; otherwise {@code false}
     */
    public boolean isFullScreenSupported();
    
    /**
     * Method responsible for determining whether display changes are supported.
     * @return boolean
     */
    public boolean isDisplayChangeSupported();
}
