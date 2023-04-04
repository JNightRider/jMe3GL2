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
package jMe3GL2.physics.control;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import jMe3GL2.physics.PhysicsSpace;
import java.io.IOException;

import org.dyn4j.dynamics.Body;

/**
 * Una implementación abstracta de la interfaz {@code PhysicsControl}.
 * <p>
 * Un objeto de la clase <code>AbstractBody</code> se puede utilizar como
 * el cuerpo físico de un objeto así como el mismo control de ese modelo</p>.
 * <p>
 * Para utilizarlo, extienda de esta clase <code>AbstractBody</code> o bien
 * extender de una clase que ya implemente la lógica de control.</p>
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 *
 * @since 1.0.0
 */
public abstract class AbstractBody extends Body implements PhysicsControl, Control {
    
    /** Espacio físico. */
    protected PhysicsSpace physicsSpace;
    
    /**
     * {@code true} si el control físico esta deshabilitado, de lo contrario
     * {@code false} si esta en operación.
     */
    protected boolean enabledPhysics = true;

    /** modelo 2D. */
    protected Spatial spatial;
    
    ///** Cuerpo físico. */
    //protected Body body;

    /**
     * Instancie un nuevo objeto utilizadon el contructor de esta clase
     * <code>AbstractBody</code> con los valores predeterminados.
     */
    public AbstractBody() { }
    
    /**
     * (non-JavaDoc)
     * @see PhysicsControl#setPhysicsSpace(jMe3GL2.physics.PhysicsSpace) 
     * @param physicsSpace {@link PhysicsSpace}
     */
    @Override
    public void setPhysicsSpace(PhysicsSpace physicsSpace) {
        this.physicsSpace = physicsSpace;
    }

    /**
     * (non-JavaDoc)
     * @see PhysicsControl#getPhysicsSpace() 
     * @return {@link PhysicsSpace}
     */
    @Override
    public PhysicsSpace getPhysicsSpace() {
        return physicsSpace;
    }

    /**
     * (non-JavaDoc)
     * @see PhysicsControl#getBody() 
     * @return {@code org.dyn4j.dynamics.Body}
     */
    @Override
    public AbstractBody getBody() {
        return this;
    }

    /**
     * (non-JavaDoc)
     * @param enabled boolean
     * @see PhysicsControl#setEnabledPhysicsControl(boolean) 
     */
    @Override
    public void setEnabledPhysicsControl(boolean enabled) {
        this.enabledPhysics = enabled;
    }

    /**
     * (non-JavaDoc)
     * @see PhysicsControl#isEnabledPhysicsControl() 
     * @return boolean
     */
    @Override
    public boolean isEnabledPhysicsControl() {
        return this.enabledPhysics;
    }

    /**
     * (non-JavaDoc)
     * @param spatial {@code Spatial}
     * @return {@code Control}
     * @deprecated (non-javadoc)
     */
    @Deprecated
    @Override
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * (non-JavaDoc)
     * @param spatial {@code Spatial}
     * @see Control#setSpatial(com.jme3.scene.Spatial) 
     */
    @Override
    public void setSpatial(Spatial spatial) {
        if (this.spatial != null && spatial != null && spatial != this.spatial) {
            throw new IllegalStateException("This control has already been added to a Spatial");
        }
        this.spatial = spatial;
        this.setUserData(spatial);
    }

    /**
     * (non-JavaDoc)
     * @param tpf float
     * @see Control#update(float) 
     */
    @Override
    public void update(float tpf) {
        if (!enabledPhysics)
            return;
        
        controlUpdate(tpf);
    }

    /**
     * (non-JavaDoc)
     * @param rm render-manager
     * @param vp view-port
     * @see Control#render(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort) 
     */
    @Override
    public void render(RenderManager rm, ViewPort vp) {
        if (!enabledPhysics)
            return;

        controlRender(rm, vp);
    }

    /**
     * Para ser implementado en la subclase.
     *
     * @param tpf tiempo por cuadro (en segundos)
     */
    protected abstract void controlUpdate(float tpf);

    /**
     * Para ser implementado en la subclase.
     *
     * @param rm el RenderManager representando el Spatial controlado (no nulo)
     * @param vp el ViewPort que se representa (no nulo)
     */
    protected abstract void controlRender(RenderManager rm, ViewPort vp);
    
    /**
     * (non-JavaDoc)
     * @param ex jme-exporter
     * @see Control#write(com.jme3.export.JmeExporter) 
     * @throws IOException io-expetion
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * (non-JavaDoc)
     * @param im jme-importer
     * @see Control#read(com.jme3.export.JmeImporter) 
     * @throws IOException io-exception
     */
    @Override
    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
}
