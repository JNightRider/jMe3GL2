/* Copyright (c) 2009-2024 jMonkeyEngine.
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
package jme3gl2.physics.control;

import com.jme3.export.*;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import java.io.IOException;
import jme3gl2.physics.PhysicsSpace;
import org.dyn4j.dynamics.Body;

/**
 * An abstract implementation of the {@link jme3gl2.physics.control.PhysicsControl} 
 * interface.
 * <p>
 * An object of the <code>PhysicsBody2D</code> class is the body that the physics 
 * engine uses to give realism to the games. With this control we can manage a 
 * 2D model with or without physics.
 * 
 * @author wil
 * @version 1.5.5
 * @since 1.0.0
 */
public abstract class PhysicsBody2D extends Body implements Control, PhysicsControl<PhysicsBody2D> {
    
    /** Physical space. */
    protected PhysicsSpace<PhysicsBody2D> physicsSpace;
    /** The 2D model. */
    protected Spatial spatial;
    
    /**
     * <code>true</code> if physical control is disabled; otherwise <code>false</code>
     * if active.
     */
    private boolean enablebody= true;
    
    /**
     * Generates a new object of class <code>PhysicsBody2D</code> to generate a 
     * physical body from a 2D model.
     */
    public PhysicsBody2D() { }
    
    /**
     * (non-Javadoc)
     * @see jme3gl2.physics.control.PhysicsControl#setPhysicsSpace(jme3gl2.physics.PhysicsSpace) 
     * @param physicsSpace A {@link jme3gl2.physics.PhysicsSpace} object
     */
    @Override
    public void setPhysicsSpace(PhysicsSpace<PhysicsBody2D> physicsSpace) {
        this.physicsSpace = physicsSpace;
    }

    /**
     * (non-Javadoc)
     * @see jme3gl2.physics.control.PhysicsControl#getPhysicsSpace() 
     * @return A {@link jme3gl2.physics.PhysicsSpace} object
     */
    @Override
    public PhysicsSpace<PhysicsBody2D> getPhysicsSpace() {
        return physicsSpace;
    }

    /**
     * (non-Javadoc)
     * @see jme3gl2.physics.control.PhysicsControl#setEnabledPhysicsControl(boolean) 
     * @param enabled boolean
     */
    @Override
    public void setEnabledPhysicsControl(boolean enabled) {
        this.enablebody = enabled;
    }

    /**
     * (non-Javadoc)
     * @see jme3gl2.physics.control.PhysicsControl#isEnabledPhysicsControl() 
     * @return boolean
     */
    @Override
    public boolean isEnabledPhysicsControl() {
        return this.enablebody;
    }

    /**
     * (non-Javadoc)
     * @param spatial object
     * @return object
     * @deprecated (?,?)
     */
    @Deprecated
    @Override
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.scene.control.Control#setSpatial(com.jme3.scene.Spatial) 
     * @param spatial object
     */
    @Override
    public void setSpatial(Spatial spatial) {
        if (this.spatial != null && spatial != null && spatial != this.spatial) {
            throw new IllegalStateException("This control has already been added to a Spatial");
        }
        this.spatial = spatial;
        this.ready();
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.scene.control.Control#update(float) 
     * @param tpf float
     */
    @Override
    public void update(float tpf) {
        if (!enablebody)
            return;        
        controlUpdate(tpf);
        physicsProcess(tpf);
    }

    /**
     * (non-Javadoc)
     *  @see com.jme3.scene.control.Control#render(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort) 
     * @param rm render-manager
     * @param vp view-port
     */
    @Override
    public void render(RenderManager rm, ViewPort vp) {
        if (!enablebody)
            return;
        controlRender(rm, vp);
    }

    /**
     * Returns the {@link com.jme3.scene.Spatial} assigned to this physical body.
     * @param <T> spatial type
     * @return A {@link com.jme3.scene.Spatial} object
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Spatial> T getJmeObject() {
        return (T) spatial;
    }

    /** Data initialization for this body (optional). */
    protected void ready() {}
    
    /**
     * Updating physical processes (optional).
     * @param delta time per frame (in seconds)
     */
    protected void physicsProcess(float delta) {}
    
    /**
     * To be implemented in subclass.
     * @param tpf time per frame (in seconds)
     */
    protected abstract void controlUpdate(float tpf);

    /**
     * To be implemented in subclass.
     * @param rm the RenderManager rendering the controlled Spatial (not null)
     * @param vp the ViewPort being rendered (not null)
     */
    protected abstract void controlRender(RenderManager rm, ViewPort vp);
    
    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#read(com.jme3.export.JmeImporter) 
     * 
     * @param im {@link com.jme3.export.JmeImporter}
     * @throws IOException throws
     */
    @Override
    public void read(JmeImporter im) throws IOException { }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#write(com.jme3.export.JmeExporter) 
     * 
     * @param ex {@link com.jme3.export.JmeExporter}
     * @throws IOException throws
     */
    @Override
    public void write(JmeExporter ex) throws IOException { }
}
