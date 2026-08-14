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
package org.je3gl.box2d.debug.renderer;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Line;
import com.jme3.util.TempVars;
import org.je3gl.scene.debug.AbstractShape2D;

import org.je3gl.scene.debug.Capsule2D;
import org.je3gl.scene.debug.Circle2D;
import org.je3gl.scene.debug.Point2D;
import org.je3gl.scene.debug.Polygon2D;

/**
 * An interface responsible for managing how the geometric mesh is rendered,
 * based on properties provided by the physics engine.
 *
 * @param <ATTR> rendering value (value)
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
@FunctionalInterface
public interface MeshRender<ATTR> {

    static void checkColorMat(Geometry geom, ColorRGBA color, boolean solid) {
        Material mat = geom.getMaterial();
        mat.getAdditionalRenderState().setDepthTest(false);
        if (solid) {
            mat.getAdditionalRenderState().setWireframe(false);
            color.setAlpha(0.2f);
        } else {
            mat.getAdditionalRenderState().setWireframe(true);
            mat.getAdditionalRenderState().setLineWidth(2);
            color.setAlpha(1f);
        }
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        Mesh mesh = geom.getMesh();
        if (mesh != null && (mesh instanceof AbstractShape2D)) {
            ((AbstractShape2D) mesh).fill(solid);
        }
    }

    /** Polygon2D */
    MeshRender<Vector3f[]> POLYGON = (geom, color, value, solid) -> {
        Mesh mesh = geom.getMesh();
        if (!(mesh instanceof Polygon2D)) {
            mesh = new Polygon2D(value);
        } else {
            ((Polygon2D) mesh).updateGeometry(value);
        }
        geom.setMesh(mesh);
        checkColorMat(geom, color, solid);
    };

    /** Circle2D */
    MeshRender<Float> CIRCLE = (geom, color, value, solid) -> {
        
        Mesh mesh = geom.getMesh();
        if (!(mesh instanceof Circle2D)) {
            mesh = new Circle2D(Circle2D.COUNT, value, 0);
        } else {
            ((Circle2D) mesh).updateGeometry(Circle2D.COUNT, value, 0);
        }
        geom.setMesh(mesh);
        checkColorMat(geom, color, solid);
    };

    /** Capsule2D */
    MeshRender<Vector2f> CAPSULE = (geom, color, value, solid) -> {
        Mesh mesh = geom.getMesh();
        if (!(mesh instanceof Capsule2D)) {
            mesh = new Capsule2D(Capsule2D.COUNT, value.x, value.y);
        } else {
            ((Capsule2D) mesh).updateGeometry(Capsule2D.COUNT, value.x, value.y);
        }
        geom.setMesh(mesh);
        checkColorMat(geom, color, solid);
    };

    /** Line */
    MeshRender<Vector3f[]> LINE = (geom, color, value, solid) -> {
        Mesh mesh = geom.getMesh();
        if (!(mesh instanceof Line)) {
            mesh = new Line(value[0], value[1]);
        } else {
            ((Line) mesh).updatePoints(value[0], value[1]);
        }
        geom.setMesh(mesh);
        checkColorMat(geom, color, solid);
    };

    /** Point2D */
    MeshRender<Vector4f> POINT = (geom, color, value, solid) -> {
        TempVars vars = TempVars.get();
        Vector3f vec3 = vars.vect1;
        vec3.set(value.x, value.y, value.z);

        Mesh mesh = geom.getMesh();
//        if (!(mesh instanceof Point2D)) {
//            mesh = new Point2D(vec3);
//        } else {
//            ((Point2D) mesh).updateGeometry(vec3);
//        }
        geom.setMesh(mesh);

        Material mat = geom.getMaterial();
        mat.setFloat("PointSize", value.w);
        checkColorMat(geom, color, solid);
        vars.release();
    };

    /**
     * Method responsible for rendering the geometry's shape.
     *
     * @param geom Geometry
     * @param color int
     * @param value Object
     * @param solid boolean
     */
    void render(Geometry geom, ColorRGBA color, ATTR value, boolean solid);
}
