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
package org.je3gl.utilities;

/**
 * Basic C-style string formatting and scanning. The format strings can contain
 * %d, %f and %s codes.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public final class StringUtilities {

    /**
     * Method responsible for verifying the different formats supported by the format.
     *
     * @param t tormad/command
     * @param c character
     * @param i index
     *
     * @return boolean
     */
    private static boolean accept(char t, char c, int i) {
        switch (t) {
            case 'd' -> {
                return "0123456789".indexOf(c) >= 0 || i == 0 && c == '-';
            }
            case 'f' -> {
                return "-0123456789.+Ee".indexOf(c) >= 0;
            }
            case 's' -> {
                return Character.isLetterOrDigit(c);
            }
            default -> throw new RuntimeException("Unknown format code: " + t);
        }
    }
    
    /**
     * Returns scanned values, or throws exception if anything wrong.
     *
     * @param fmt format specification
     * @param str string to scan
     * @param ans scanned values
     *
     * @return int
     */
    @SuppressWarnings("unchecked")
    public static int scanf(String fmt, String str, Object[] ans) {
        int s = 0, idx = 0;
        int ns = str.length();
        int n = fmt.length();
        for (int i = 0; i < n; i++) {
            char c = fmt.charAt(i);
            if (c == '%') {
                char t = fmt.charAt(++i);
                if (t == '%') {
                    c = t;
                } else {
                    int s0 = s;
                    while ((s == s0 || s < ns) && accept(t, str.charAt(s), s - s0)) {
                        s++;
                    }
                    String sub = str.substring(s0, s);
                    switch (t) {
                        case 'd' -> ans[idx++] = Integer.valueOf(sub);
                        case 'f' -> ans[idx++] = Float.valueOf(sub);
                        default -> ans[idx++] = (sub);
                    }
                    continue;
                }
            }
            if (str.charAt(s++) != c) {
                throw new RuntimeException();
            }
            if (idx >= ans.length) {
                break;
            }
        }
        //if (s < ns) {
        //    throw new RuntimeException("Unmatched characters at end of string");
        //}
        return idx;
    }
}
