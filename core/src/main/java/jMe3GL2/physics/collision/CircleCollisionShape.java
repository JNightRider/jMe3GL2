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
import org.dyn4j.geometry.Circle;

/**
 * Implementación de un Circulo {@code Convex} {@code Shape}
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 1.0.0
 */
public class CircleCollisionShape extends AbstractCollisionShape<Circle> {
    
    /** Radios del {@code Circle}. */
    private double radius;

    /**
     * Constructor predeterminado.
     */
    public CircleCollisionShape() {
    }

    /**
     * Genere un nuevo objeto <code>CircleCollisionShape</code>.
     * @param radius radio-circulo.
     */
    public CircleCollisionShape(double radius) {
        this.radius = radius;
        CircleCollisionShape.this.createCollisionShape();
    }

    /**
     * Devuelve el radio de la circunferencia.
     * @return radio.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * (non-JavaDoc)
     * @see AbstractCollisionShape#createCollisionShape() 
     * @return this.
     */
    @Override
    public AbstractCollisionShape<Circle> createCollisionShape() {
        this.collisionShape = new Circle(radius);
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
        out.write(radius, "radius", 0);
    }

    /**
     * (non-JavaDoc)
     * @param im jme-importer
     * @throws IOException io-exception
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        radius = in.readDouble("radius", radius);
        createCollisionShape();
    }
}
