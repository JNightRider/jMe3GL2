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
package jme3gl2.physics.debug;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.HashMap;
import java.util.Map;

import jme3gl2.physics.control.PhysicsBody2D;
import org.dyn4j.dynamics.BodyFixture;

/**
 * Class <code>PhysicsDebugControl</code> in charge of controlling the physical
 * forms of a body in space.
 * @author wil
 * @version 1.5.0
 * @since 2.5.0
 */
class Dyn4jPhysicsControl extends AbstractPhysicsDebugControl<PhysicsBody2D> {

    /** body. */
    private final PhysicsBody2D body;    
    /** Renderer for physical shapes. */
    protected Graphics2DRenderer renderer;
    
    /** Node map for all physical forms of a body. */
    protected Map<BodyFixture, Node> shapes = new HashMap<>();    
    /** Parent node containing the physical forms. */
    protected Node spatialAsNode = null;
    
    /**
     * Control de la clase <code>PhysicsDebugControl</code> donde se inicializa
     * las formas físicos contenido en los cerpos físicos-
     * 
     * @param dyn4jDebugAppState debugger
     * @param body physical body
     */
    public Dyn4jPhysicsControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, PhysicsBody2D body) {
        super(dyn4jDebugAppState);
        this.body     = body;
        this.renderer = dyn4jDebugAppState.getGraphics2DRenderer();
        for (final BodyFixture bf : body.getFixtures()) {
            processRender(bf);
        }
    }
    
    /**
     * Method for establishing the <code>Spatial</code> to control, it will
     * normally be a <code>Node</code> to which the geometries of the debugged
     * physical bodies will be added.
     * <p>
     * Only bodies that are available in the stack are added.
     * 
     * @param spatial <code>Spatial</code> to control
     */
    @Override
    public void setSpatial(final Spatial spatial) {
        if (spatial != null && spatial instanceof Node) {
            this.spatialAsNode = (Node) spatial;
            for (final Node node : this.shapes.values()) {
                this.spatialAsNode.attachChild(node);
            }
        } else if (spatial == null && this.spatial != null) {
            for (final Node node : this.shapes.values()) {
                this.spatialAsNode.detachChild(node);
            }
        }
        super.setSpatial(spatial);
    }

    /**
     * Method in charge of updating the list of nodes for the physical body
     * shapes.
     * <p>
     * If there is a change or a new shape, the parent node is notified to
     * discard its children and then add the new geometry stack.
     * 
     * @param tpf time per frames per second
     */
    @Override
    protected void controlUpdate(float tpf) {
        final Map<BodyFixture, Node> oldMap = shapes;
        shapes = new HashMap<>();
        
        for (final BodyFixture fixture : body.getFixtures()) {
            if (oldMap.containsKey(fixture)) {
                shapes.put(fixture, oldMap.get(fixture));
                oldMap.remove(fixture);
            } else {
                processRender(fixture);
            }
        }
                
        // Set material according to body condition
        for (final Map.Entry<?, Node> entry : oldMap.entrySet()) {
            entry.getValue().removeFromParent();
        }
        applyPhysicsLocation(body);
        applyPhysicsRotation(body);
    }
    
    /**
     * Method in charge of processing the rendering of physical bodies, i.e. their shape.
     * @param shape physical form
     * @return graphical form
     */
    private Node processRender(BodyFixture bf) {
        final Node node = renderer.render(bf, body, null);        
        this.shapes.put(bf, node);
        return node;
    }
}
