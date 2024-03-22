/* Copyright (c) 2009-2023 jMonkeyEngine.
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
package jme3gl2.physics.collision;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import java.io.IOException;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;

/**
 * Implementación de un Polygon {@code Convex} {@code Shape}
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 1.0.0
 */
public class PolygonCollisionShape extends AbstractCollisionShape<Polygon> {
    
    /** vértices-poligono. */
    private Vector2[] vertices;

    /**
     * Constructor predeterminado.
     */
    public PolygonCollisionShape() {
    }

    /**
     * Genere un nuevo objeto <code>EllipseCollisionShape</code>.
     * @param vertices vértices-poligono
     */
    public PolygonCollisionShape(Vector2... vertices) {
        this.vertices = vertices;
        PolygonCollisionShape.this.createCollisionShape();
    }

    /**
     * Devuelve los vértices.
     * @return vértices-poligono.
     */
    public Vector2[] getVertices() {
        return vertices;
    }
    
    /**
     * (non-JavaDoc)
     * @see AbstractCollisionShape#createCollisionShape() 
     * @return this.
     */
    @Override
    public AbstractCollisionShape<Polygon> createCollisionShape() {
        this.collisionShape = new Polygon(vertices);
        return this;
    }

    /**
     * (non-JavaDoc)
     * @param ex jme-exporter
     * @throws IOException io-exception
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        if (vertices != null) {
            int lenght = vertices.length;
            out.write(lenght, "lenght", 0);
            
            for (int i = 0; i < lenght; i++) {
                out.write(vertices[i].x, "x" + i, 0);
                out.write(vertices[i].y, "y" + i, 0);
            }
        }
    }

    /**
     * (non-JavaDoc)
     * @param im jme-importer
     * @throws IOException io-exception
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        int lenght = in.readInt("lenght", 0);
        vertices = new Vector2[lenght];        
        for (int i = 0; i < lenght; i++) {
            vertices[i] = new Vector2(in.readDouble("x" + i, 0), 
                                      in.readDouble("y" + i, 0));
        }
        createCollisionShape();
    }
}
