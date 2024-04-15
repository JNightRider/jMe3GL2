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
package org.jegl.physics.debug;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import org.jegl.physics.PhysicsSpace;
import org.jegl.physics.control.PhysicsBody2D;
import org.jegl.util.Converter;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.Bounds;
import org.dyn4j.geometry.Vector2;

/**
 * Class <code>BoundsDebugControl</code> to manage a debugger for the boundaries
 * of the physical world as defined by a <code>AxisAlignedBounds</code>.
 * @param <E> body type
 * 
 * @author wil
 * @version 1.0.5
 * @since 2.5.0
 */
final class BoundsDebugControl<E extends PhysicsBody2D> extends AbstractPhysicsDebugControl<E> {

    /** Renderer. */
    protected final Graphics2DRenderer renderer;    
    /** Physical world. */
    protected final PhysicsSpace<E> world;    
    /** Debugging node. */
    protected Node boundsNode;

    /**
     * Class constructor <code>BoundsDebugControl</code>.
     * @param dyn4jDebugAppState debugger
     * @param renderer renderer.
     */
    public BoundsDebugControl(Dyn4jDebugAppState<E> dyn4jDebugAppState, Graphics2DRenderer renderer) {
        super(dyn4jDebugAppState);
        this.world    = dyn4jDebugAppState.getPhysicsSpace();
        this.renderer = renderer;
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.scene.control.AbstractControl#setSpatial(com.jme3.scene.Spatial) 
     * @param spatial spatial
     */
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        renderBounds();
    }

    /**
     * Method for verifying if there is a <code>Bounds</code> for the actual
     * physical world. If so, a form can be generated for it.
     * <p>
     * Note: Only compatible with the form <code>AxisAlignedBounds</code>.
     */
    protected void renderBounds() {
        final Bounds bounds = world.getBounds();
        if (bounds != null && (bounds instanceof AxisAlignedBounds)) {
            if (this.boundsNode == null && !((Node) spatial).hasChild(this.boundsNode)) {
                AxisAlignedBounds alignedBounds = (AxisAlignedBounds) bounds;
                double width  = alignedBounds.getWidth(),
                       height = alignedBounds.getHeight();

                this.boundsNode = renderer.renderBounds(true, new Vector2[]{
                    new Vector2(-width * 0.5, -height * 0.5),
                    new Vector2( width * 0.5, -height * 0.5),
                    new Vector2( width * 0.5,  height * 0.5),
                    new Vector2(-width * 0.5,  height * 0.5)
                });
                ((Node) spatial).attachChild(this.boundsNode);
            }
        } else {
            if (this.boundsNode != null) {
                this.boundsNode.removeFromParent();
                this.boundsNode = null;
            }
        }
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.scene.control.AbstractControl#controlUpdate(float) 
     * @param tpf float
     */
    @Override
    protected void controlUpdate(float tpf) {
        renderBounds();        
        
        final Bounds bounds = world.getBounds();
        if (this.boundsNode != null && (bounds instanceof AxisAlignedBounds)) {
            final Vector2 trans = world.getBounds().getTranslation();
            final float posX = Converter.toFloatValue(trans.x);
            final float posY = Converter.toFloatValue(trans.y);
            this.boundsNode.setLocalTranslation(posX, posY, this.boundsNode.getLocalTranslation().z);
        }
    }
}
