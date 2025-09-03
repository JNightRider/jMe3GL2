/*
BSD 3-Clause License

Copyright (c) 2023-2025, Night Rider (Wilson)

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.je3gl.demo.jawt;

import org.je3gl.jawt.JAWTResolution;
import org.je3gl.jawt.Jme3GL2DisplaySystem;
import org.je3gl.jawt.Jme3GL2DefaultDisplaySystem;
import static org.je3gl.plugins.Debugger.*;

/**
 * Test class for screen resolutions in a desktop environment.
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class ScreenResolution {
    
    /**
     * The main method; uses zero arguments in args array
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("jMe3GL2.Debug", "true");

        Jme3GL2DisplaySystem displaySystem = Jme3GL2DefaultDisplaySystem.getDisplaySystem();
        JAWTResolution resolution = displaySystem.getFullScreenResolution();
        
        apiGLLog(" List of available resolutions: ");
        for (final JAWTResolution element : displaySystem.getResolutions()) {
            apiGLLogMore("* " + (element.getWidth() + "x" + element.getHeight() + "x" +
                                (element.getBitDepth() > 0 ? element.getBitDepth() + "bpp": "[Multi depth]")
                                + "@" + (element.getRefreshRate() > 0 ? element.getRefreshRate() + "Hz" : "[Unknown refresh rate]")));
        }

        apiGLLog(" Full screen resolution: ");
        apiGLLogMore(" * " + resolution.toString());
        
        apiGLLog(" Support:");
        apiGLLogMore(" * Full Screen Supported    = " + displaySystem.isFullScreenSupported());
        apiGLLogMore(" * Display Change Supported = " + displaySystem.isDisplayChangeSupported());
    }
}
