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
package jMe3GL2.physics.collision;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import java.io.IOException;
import org.dyn4j.geometry.HalfEllipse;

/**
 * Implementación de un Elipse {@code Convex} {@code Shape}
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 1.0.0
 */
public class HalfEllipseCollisionShape extends AbstractCollisionShape<HalfEllipse> {
    
    /** El ancho {@code HalfEllipse} */
    private double width;
    
    /** El largo {@code HalfEllipse} */
    private double height;

    /**
     * Constructor predeterminado.
     */
    public HalfEllipseCollisionShape() {
    }

    /**
     * Genere un nuevo objeto <code>HalfEllipseCollisionShape</code>.
     * @param width ancho-medio;elipse
     * @param height largo-medio;elipse
     */
    public HalfEllipseCollisionShape(double width, double height) {
        this.width = width;
        this.height = height;
        HalfEllipseCollisionShape.this.createCollisionShape();
    }
    
    /**
     * Devuelve el ancho.
     * @return ancho.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Devuelve el largo.
     * @return largo.
     */
    public double getHeight() {
        return height;
    }
    
    /**
     * (non-JavaDoc)
     * @see AbstractCollisionShape#createCollisionShape() 
     * @return this.
     */
    @Override
    public AbstractCollisionShape<HalfEllipse> createCollisionShape() {
        this.collisionShape = new HalfEllipse(width, height);
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
        out.write(width, "width", 0);
        out.write(height, "height", 0);
    }

    /**
     * (non-JavaDoc)
     * @param im jme-importer
     * @throws IOException io-exception
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        width  = in.readDouble("width", width);
        height = in.readDouble("height", height);
        createCollisionShape();
    }
}
