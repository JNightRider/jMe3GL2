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
package org.je3gl.box2d.debug.batch;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;

/**
 *
 * @author wil
 */
public class BatchSnapshot {

    private final BatchLine batchLine;
    private final BatchPoint batchPoint;
    private final BatchLine batchBounds;
    private final BatchCircle batchCircle;
    private final BatchLine batchTransform;
    private final BatchPolygon batchPolygon;
    private final BatchCircle batchSolidCircle;
    private final BatchCapsule batchSolidCapsule;
    private final BatchPolygon batchSolidPolygon;

    public BatchSnapshot(AssetManager assetManager) {
        batchLine = new BatchLine(assetManager);
        batchPoint = new BatchPoint(assetManager);
        batchBounds = new BatchLine(assetManager);
        batchCircle = new BatchCircle(assetManager);
        batchTransform = new BatchLine(assetManager);
        batchPolygon = new BatchPolygon(assetManager);
        batchSolidCircle = new BatchCircle(assetManager);
        batchSolidCapsule = new BatchCapsule(assetManager);
        batchSolidPolygon = new BatchPolygon(assetManager);
    }

    public void drawLine(float x0, float y0, float x1, float y1, int color) {
        batchLine.addLine(x0, y0, x1, y1, color);
    }

    public void drawPoint(float x, float y, float size, int rgba) {
        batchPoint.addPoint(x, y, size, rgba);
    }

    public void drawCircle(float x, float y, float radius, int color) {
        batchCircle.addCircle(x, y, radius, 0, color, false);
    }

    public void drawSolidCircle(float x, float y, float radius, float angle, int color) {
        batchSolidCircle.addCircle(x, y, radius, angle, color, true);
    }

    public void drawCapsule(float x, float y, float angle, float radius, float length, int rgba) {
        batchSolidCapsule.addCapsule(x, y, angle, radius, length, rgba, true);
    }

    public void drawPolygon(float x, float y, float angle, int color, Vector3f[] vertices) {
        batchPolygon.addPolygon(x, y, angle, color, vertices, false);
    }

    public void drawSolidPolygon(float x, float y, float angle, int color, Vector3f[] vertices) {
        batchSolidPolygon.addPolygon(x, y, angle, color, vertices, true);
    }

    public void drawLineTransform(float x0, float y0, float x1, float y1, int color) {
        batchTransform.addLine(x0, y0, x1, y1, color);
    }

    public void drawLineBounds(float x0, float y0, float x1, float y1, int color) {
        batchBounds.addLine(x0, y0, x1, y1, color);
    }

    public void flushDraw(RenderManager renderManager) {
        batchLine.flushLines(renderManager);
        batchPoint.flushPoints(renderManager);
        batchBounds.flushLines(renderManager);
        batchCircle.flushCircle(renderManager);
        batchPolygon.flushPolygon(renderManager);
        batchTransform.flushLines(renderManager);
        batchSolidCircle.flushCircle(renderManager);
        batchSolidCapsule.flushCapsule(renderManager);
        batchSolidPolygon.flushPolygon(renderManager);
    }

    public void clear() {
        batchLine.clear();
        batchPoint.clear();
        batchBounds.clear();
        batchCircle.clear();
        batchPolygon.clear();
        batchTransform.clear();
        batchSolidCircle.clear();
        batchSolidCapsule.clear();
        batchSolidPolygon.clear();
    }
}
