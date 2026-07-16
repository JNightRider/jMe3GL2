 /*
BSD 3-Clause License

Copyright (c) 2023-2026, Night Rider (Wilson)

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
package org.je3gl.box2d.util;

import org.box2d.jni.b2Vec2;
import org.box2d.jni.system.ArenaAlloc;

import static org.box2d.jni.system.ArenaAlloc.*;
import static org.je3gl.utilities.StringUtilities.*;

/**
 * Parse an SVG path element with only straight lines
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public final class ParsePath {

    /**
     * Parse an SVG path element with only straight lines.
     *
     * @param svgPath source
     * @param offset offset (b2Vec2)
     * @param points list
     * @param capacity int
     * @param scale float
     * @param reverseOrder boolean
     *
     * @return point count
     */
    public static int parse( String svgPath, b2Vec2 offset, b2Vec2.Buffer points, int capacity, float scale, boolean reverseOrder ) {
	int pointCount = 0;
	b2Vec2 currentPoint = b2Vec2.malloc();
        int ptr = 0;
        char command = svgPath.charAt(ptr);

       
        int length = svgPath.length();
        while (ptr < length) {
            char car = svgPath.charAt(ptr);
            if ( !Character.isDigit( car ) && car != '-' )
            {
                // note: command can be implicitly repeated
                command = car;

                if ( command == 'M' || command == 'L' || command == 'H' || command == 'V' || command == 'm' || command == 'l' ||
                         command == 'h' || command == 'v' )
                {
                    ptr += 2; // Skip the command character and space
                    car = svgPath.charAt(ptr);
                }

                if ( command == 'z' )
                {
                    break;
                }
            }

            assert ( Character.isDigit(car) || car == '-' );

            
            String sub = svgPath.substring(ptr, length);
            switch (command) {
                case 'M', 'L' -> {
                    Float[] xy = new Float[2];
                    if ( scanf("%f,%f", sub, xy) == 2 ) {
                        currentPoint.x(xy[0]);
                        currentPoint.y(xy[1]);
                    } else {
                        assert (false);
                    }
                }
                case 'H' -> {
                    Float[] x = new Float[1];
                    if ( scanf("%f", sub, x) == 1 ) {
                        currentPoint.x(x[0]);
                    } else {
                        assert (false);
                    }
                }
                case 'V' -> {
                    Float[] y = new Float[1];
                    if ( scanf("%f", sub, y) == 1 ) {
                        currentPoint.y(y[0]);
                    } else {
                        assert (false);
                    }
                }
                case 'm', 'l' -> {
                    Float[] xy0 = new Float[2];
                    if ( scanf("%f,%f", sub, xy0) == 2 ) {
                        currentPoint.x(currentPoint.x() + xy0[0]);
                        currentPoint.y(currentPoint.y() + xy0[1]);
                    } else {
                        assert (false);
                    }
                }
                case 'h' -> {
                    Float[] x0 = new Float[1];
                    if ( scanf("%f", sub, x0) == 1 ) {
                        currentPoint.x(currentPoint.x() + x0[0]);
                    } else {
                        assert (false);
                    }
                }
                case 'v' -> {
                    Float[] y0 = new Float[1];
                    if ( scanf("%f", sub, y0) == 1 ) {
                        currentPoint.y(currentPoint.y() + y0[0]);
                    } else {
                        assert (false);
                    }
                }
                default -> {
                    assert (false);
                }
            }

            try (ArenaAlloc alloc = allocPush()) {
                points.put(pointCount, b2Vec2.calloc(alloc).set(scale * ( currentPoint.x() + offset.x() ), -scale * ( currentPoint.y() + offset.y() )));
            }
            pointCount += 1;
            if ( pointCount == capacity )
            {
                break;
            }
            
            // Move to the next space or end of string            
            while ( car != '\0' && !Character.isSpaceChar( car ) )
            {
                ptr++;
                if (ptr < length) {
                    car = svgPath.charAt(ptr);
                } else {
                    break;
                }
            }

            // Skip contiguous spaces
            while ( Character.isSpaceChar( car ) )
            {
                ptr++;
                if (ptr < length) {
                    car = svgPath.charAt(ptr);
                } else {
                    break;
                }
            }
        }
	return pointCount;
    }
}
