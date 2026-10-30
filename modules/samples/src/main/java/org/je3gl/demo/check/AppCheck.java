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
package org.je3gl.demo.check;

import com.jme3.app.Application;

/**
 * Check the application flags.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class AppCheck {

    /** default time. */
    public static final long DEFAULT_DEAD_TIME = 1000l;

    /**
     * Check whether the application closes automatically after a certain period
     * of time, which is derived from the arguments.
     *
     * @param args {@code String[]}
     * @param app Application
     */
    public static void checkStop(String[] args, Application app) {
        boolean autoStop = false;
        long delay = DEFAULT_DEAD_TIME;

        for (String arg : args) {
            String[] values = arg.split("=");
            if (values.length == 1 && "-stop".equals(values[0])) {
                autoStop = true;
            } else if (values.length == 2 && "-delay".equals(values[0])) {
                try {
                    delay = Long.parseLong(values[1]);
                } catch (NumberFormatException e) {
                    delay = DEFAULT_DEAD_TIME;
                }
            }
        }

        if (autoStop) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) { }
            app.stop();
        }
    }
}
