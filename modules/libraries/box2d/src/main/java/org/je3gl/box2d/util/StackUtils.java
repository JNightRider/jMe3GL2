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

import java.util.ArrayList;
import org.je3gl.box2d.debug.data.LineData;
import org.je3gl.box2d.debug.data.PointData;
import org.je3gl.box2d.debug.data.PolygonData;

/**
 *
 * @author wil
 */
public final class StackUtils implements AutoCloseable {
    
    private static final ThreadLocal<StackUtils> TLS = ThreadLocal.withInitial(StackUtils::new);
    
    private static class StackList<E> extends ArrayList<E> {
        
        private int index;

        public StackList() {
        }
        
        public E get() {
            if (index < 0 || index >= size()) {
                return null;
            }
            E value = get(index++);
            if (index >= size()) {
                index = size() - 1;
            }
            return value;
        }
        
        public void pop() {
            index--;
            if (index < 0) {
                index = 0;
            }
        }

        public void reset() {
            index = 0;
        }
    }

    private final StackList<LineData> lineData = new StackList<>();
    private final StackList<PointData> pointData = new StackList<>();
    private final StackList<PolygonData> polygonData = new StackList<>();

    public StackUtils() {
    }
    
    public LineData allocLine() {
        LineData data = lineData.get();
        if (data == null) {
            lineData.add(new LineData());
            return lineData.get();            
        }
        return data;
    }
    
    public PointData allocPoint() {
        PointData data = pointData.get();
        if (data == null) {
            pointData.add(new PointData());
            return pointData.get();            
        }
        return data;
    }
    
    public PolygonData allocPolygon(int size) {
        PolygonData data = polygonData.get();
        if (data == null || data.getLength() < size) {
            polygonData.add(new PolygonData(size));
            return polygonData.get();            
        }
        return data;
    }
    
    @Override
    public void close() {
        lineData.reset();
    }
    
    public static StackUtils get() {
        return TLS.get();
    }
}
