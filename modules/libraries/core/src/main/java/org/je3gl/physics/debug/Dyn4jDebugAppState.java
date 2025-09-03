/*
BSD 3-Clause License

Copyright (c) 2023-2025, Night Rider (Wilson)

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
package org.je3gl.physics.debug;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import org.je3gl.physics.PhysicsSpace;
import org.je3gl.physics.control.PhysicsBody2D;
import org.je3gl.scene.debug.custom.DebugGraphics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class <code>Dyn4JDebugAppState</code> responsible for managing a state for
 * the debugging of the physical forms of the bodies that are added to the world
 * of <b>Dyn4j</b>.
 * @param <E> body types
 * 
 * @author wil
 * @version 1.5.0
 * @since 2.5.0
 */
public class Dyn4jDebugAppState<E extends PhysicsBody2D> extends BaseAppState {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(Dyn4jDebugAppState.class.getName());
    
    /** Main application <code>JME</code>. */
    protected Application application;    
    /** Resource manager <code>JME</code>. */
    protected AssetManager assetManager;
    
    // Graphic part.
    /** Physical space of the bodies. */
    private final PhysicsSpace<E> physicsSpace;
    
    /**
     * All objects/bodies for the physical shapes to be added in this node out
     * of scene so that it does not interfere with the main root node.
     */
    private Node debugNode;    
    /** Rendering manager. */
    private Graphics2DRenderer renderer; // Debugger
    
    // Physical bodies and joints.    
    /** Map of physical bodies. */
    protected Map<E, Spatial> bodies = new HashMap<>();
    
    /** Debugger view. */
    protected ViewPort viewPort;
    /** <code>JME3</code> renderer. */
    protected RenderManager rm;

    /**
     * Class constructor <code>Dyn4JDebugAppState</code> where it asks for the
     * physical space to manage the shapes of the bodies.
     * 
     * @param physicsSpace physical space
     */
    public Dyn4jDebugAppState(PhysicsSpace<E> physicsSpace) {
        this.physicsSpace = physicsSpace;
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application) 
     * @param app application
     */
    @Override
    @SuppressWarnings("unchecked")
    public void initialize(Application app) {
        rm            = app.getRenderManager();
        assetManager = app.getAssetManager();
        application  = app;
        
        // Initialize the debug scene
        renderer  = new Graphics2DRenderer((Dyn4jDebugAppState<PhysicsBody2D>) this);
        debugNode = new Node("Debug Node");
        
        viewPort  = rm.createMainView("Physics Debug Overlay", app.getCamera());
        viewPort.setClearFlags(false, true, false);
        
        setDebugGraphics(new Dyn4jDebugGraphics(app.getAssetManager()));
        BoundsDebugControl bdc = new BoundsDebugControl<>(this, renderer);
        bdc.setEnabled(false);
        
        debugNode.setCullHint(Spatial.CullHint.Never);
        debugNode.addControl(bdc);
    }
    
    /**
     * Sets debug graphics (color manager).
     * @param graphics object
     */
    public void setDebugGraphics(DebugGraphics graphics) {
        if (!isInitialized()) {
            LOGGER.log(Level.WARNING, "Initialize debugging first to set a color palette");
            return;
        }
        renderer.setDebugGraphics(graphics);
        renderer.printInformation();
    }

    /** (non-Javadoc) */
    @Override
    protected void onEnable() {
        if (viewPort != null) {
            debugNode.getControl(BoundsDebugControl.class).setEnabled(true);
            viewPort.attachScene(debugNode);
        }
    }
    /**(non-Javadoc) */
    @Override
    protected void onDisable() {
        if (viewPort != null) {
            debugNode.getControl(BoundsDebugControl.class).setEnabled(false);
            viewPort.detachScene(debugNode);
        }
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#cleanup() 
     * @param app application
     */
    @Override
    protected void cleanup(Application app) {
        debugNode.detachAllChildren();
        rm.removeMainView(viewPort);
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#update(float) 
     * @param tpf float
     */
    @Override
    public void update(float tpf) {
        // Update all object links
        updateBodies();
        
        // Update debug root node
        debugNode.updateLogicalState(tpf);
        debugNode.updateGeometricState();
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.app.state.AbstractAppState#render(com.jme3.renderer.RenderManager) 
     * @param rm render
     */
    @Override
    public void render(RenderManager rm) {
        if (this.viewPort != null) {
            rm.renderScene(this.debugNode, this.viewPort);
        }
    }
    
    /**
     * Method in charge of updating the physical bodies.
     */
    @SuppressWarnings("unchecked")
    private void updateBodies() {
        final Map<E, Spatial> oldBodies = this.bodies;        
        this.bodies = new HashMap<>();
        
        final Collection<E> currentBodies 
                = this.physicsSpace.getBodies();

        // Create new body map
        for (final E body : currentBodies) {            
            if (oldBodies.containsKey(body)) {

                // Copy existing Spatial
                final Spatial spatial = oldBodies.get(body);
                this.bodies.put(body, spatial);
                oldBodies.remove(body);
            } else {
                LOGGER.log(Level.FINE, "**Create new debug PhysicsBody2D**");
                
                // Create a new Spatial
                final Node node = new Node(body.toString());
                node.addControl(new Dyn4jPhysicsControl((Dyn4jDebugAppState<PhysicsBody2D>) this, body));
                
                this.bodies.put(body, node);
                this.debugNode.attachChild(node);
            }
        }
        for (final Spatial spatial : oldBodies.values()) {
            spatial.removeFromParent();
        }
    }
    
    /**
     * Return physical space.
     * @return object
     */
    public PhysicsSpace<E> getPhysicsSpace() {
        return physicsSpace;
    }
    
    /**
     * Returns the 2D graphics renderer.
     * @return object
     */
    public Graphics2DRenderer getGraphics2DRenderer() {
        return renderer;
    }
}
