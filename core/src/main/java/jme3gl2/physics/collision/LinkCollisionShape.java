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
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Vector2;

/**
 * Implementación de un Link {@code Convex} {@code Shape}
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 1.0.0
 */
public class LinkCollisionShape extends AbstractCollisionShape<Link> {
    
    /** Primer punto. */
    private Vector2 point1;
    
    /** Punto final.*/
    private Vector2 point2;

    /**
     * Constructor predeterminado.
     */
    public LinkCollisionShape() {
    }

    /**
     * Genere un nuevo objeto <code>LinkCollisionShape</code>.
     * @param point1 punto-1
     * @param point2 punto-2
     */
    public LinkCollisionShape(Vector2 point1, Vector2 point2) {
        this.point1 = point1;
        this.point2 = point2;
        LinkCollisionShape.this.createCollisionShape();
    }

    /**
     * Devuelve el primer punto.
     * @return punto-1
     */
    public Vector2 getPoint1() {
        return point1;
    }

    /**
     * Devueleve el segundo punto.
     * @return punto-2
     */
    public Vector2 getPoint2() {
        return point2;
    }
    
    /**
     * (non-JavaDoc)
     * @see AbstractCollisionShape#createCollisionShape() 
     * @return this.
     */
    @Override
    public AbstractCollisionShape<Link> createCollisionShape() {
        this.collisionShape = new Link(point1, point2);
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
        out.write(point1.x, "p1X", 0);
        out.write(point1.y, "p1Y", 0);
        out.write(point2.x, "p2X", 0);
        out.write(point2.y, "p2Y", 0);
    }

    /**
     * (non-JavaDoc)
     * @param im jme-importer
     * @throws IOException io-exception
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        point1 = new Vector2(in.readDouble("p1X", 0), in.readDouble("p1Y", 0));
        point2 = new Vector2(in.readDouble("p2X", 0), in.readDouble("p2Y", 0));
        createCollisionShape();
    }
}
