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
package jme3gl2.physics.debug.control;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import jme3gl2.physics.control.PhysicsBody2D;
import jme3gl2.physics.debug.Graphics2DRenderer;
import jme3gl2.util.Converter;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.Bounds;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

/**
 * Clase <code>BoundsDebugControl</code> encargado de gestionar un depurador
 * para los límites del mundo físico definido por un <code>AxisAlignedBounds</code>.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT 
 * 
 * @since 2.5.0
 * @param <E> tipo-cuerpo
 */
public class BoundsDebugControl<E extends PhysicsBody2D> extends AbstractControl {

    /** Renderizador. */
    protected final Graphics2DRenderer renderer;
    
    /** Mundo físico. */
    protected final World<E> world;
    
    /** Nodo depuración. */
    protected Node boundsNode;

    /**
     * Constructor de la clase <code>BoundsDebugControl</code>.
     * @param world mundo físico.
     * @param renderer renderizador.
     */
    public BoundsDebugControl(World<E> world, Graphics2DRenderer renderer) {
        this.renderer = renderer;
        this.world = world;
    }

    /**
     * (non-JavaDoc)
     * @see com.jme3.scene.control.AbstractControl#setSpatial(com.jme3.scene.Spatial) 
     * 
     * @param spatial spatial.
     */
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        renderBounds();
    }

    /**
     * Método encargdo de verificar si existe un <code>Bounds</code> para el
     * mundo físico actual. De se asi se fenerar una forma para ella.
     * <p>
     * Nota: Solo es compatible con la forma <code>xisAlignedBounds</code>.
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
     * (non-JavaDoc)
     * @see com.jme3.scene.control.AbstractControl#controlUpdate(float) 
     * @param tpf float
     */
    @Override
    protected void controlUpdate(float tpf) {
        renderBounds();        
        
        final Bounds bounds = world.getBounds();
        if (this.boundsNode != null && (bounds instanceof AxisAlignedBounds)) {
            final Vector2 trans = world.getBounds().getTranslation();
            final float posX = Converter.toFloat(trans.x);
            final float posY = Converter.toFloat(trans.y);
            this.boundsNode.setLocalTranslation(posX, posY, this.boundsNode.getLocalTranslation().z);
        }
    }

    /**
     * (non-JavaDoc)
     * @see com.jme3.scene.control.AbstractControl#controlRender(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort) 
     * @param rm render-manager
     * @param vp view-port
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        /* NADA. */
    }
}
