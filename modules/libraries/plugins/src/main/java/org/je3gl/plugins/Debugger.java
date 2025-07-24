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
package org.je3gl.plugins;

import java.io.PrintStream;

/**
 * A small debugger that can be enabled or disabled as follows:<pre><code>
 * System.getProperty("jMe3GL2.Debug", "true")
 * ...
 * System.getProperty("jMe3GL2.Debug", "false")
 * </code></pre>
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public final class Debugger {
    
    /**Object responsible for printing the message via console (screen). */
    public static final PrintStream DEBUG_STREAM = getDebugStream();
    
    /** Flag - state */
    private final static Boolean DEBUG;
    /** Prefix that is added to messages. */
    private final static String PREFIX;
    // init
    static {
        DEBUG = Boolean.valueOf(System.getProperty("jMe3GL2.Debug", "false"));
        PREFIX = "jMe3GL2";
    }
    
    /**
     * Returns a default output.
     * @return stream
     */
    private static PrintStream getDebugStream() {
        return System.out;
    }
    
    /**
     * Print a message.
     * @param msg message
     */
    public static void apiGLLog(CharSequence msg) {
        if (DEBUG) {
            DEBUG_STREAM.print("[" + PREFIX + "]" + msg + "\n");
        }
    }
    
    /**
     *Print a message with a format.
     * @param msg message
     */
    public static void apiGLLogMore(CharSequence msg) {
        if (DEBUG) {
            DEBUG_STREAM.print("\t" + msg + "\n");
        }
    }
}
