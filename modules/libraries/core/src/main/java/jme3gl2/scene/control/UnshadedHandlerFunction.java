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
package jme3gl2.scene.control;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.logging.Level;
import java.util.logging.Logger;
import jme3gl2.scene.shape.Sprite;

/**
 * Class <code>UnshadedHandlerFunction</code> that implements the {@link jme3gl2.scene.control.AnimatedMaterialsHandlerFunction} 
 * interface to manage the geometries of 2D models that have a material of type <code>"Common/MatDefs/Misc/Unshaded.j3md"</code>.
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 * @param <O> the type of model
 * @param <A> the type of animation
 * @param <E> the type of animated control
 */
public class UnshadedHandlerFunction<O extends Spatial, A extends Animation2D, E extends AbstractAnimation2DControl<O, A, E>> implements AnimatedMaterialsHandlerFunction<O, A, E> {

    /** Class logger. */
    private static final Logger LOG = Logger.getLogger(UnshadedHandlerFunction.class.getName());

    /**
     * (non-Javadoc)
     * @see jme3gl2.scene.control.AnimatedMaterialsHandlerFunction#applyAnimation2DControl(com.jme3.scene.Spatial, jme3gl2.scene.control.Animation2D, jme3gl2.scene.control.AbstractAnimation2DControl) 
     * @param model object
     * @param animation object
     * @param control object
     */
    @Override
    public void applyAnimation2DControl(O model, A animation, E control) {        
        if (model instanceof Geometry) {
            applyAnimation2DGeometry((Geometry) model, animation, control);
        } else {
            applyAnimation2DNode((Node) model, animation, control);
        }
    }
    
    /**
     * Recursively search for the geometries that make up the Model Node.
     * @param node the model node
     * @param animation the animation (frame)
     * @param control the animated control
     */
    protected  void applyAnimation2DNode(Node node, A animation, E control) { 
        for (final Spatial spatial : node.getChildren()) {
            if (spatial instanceof Node) {
                applyAnimation2DNode((Node) spatial, animation, control);
            } else {
                applyAnimation2DGeometry((Geometry) spatial, animation, control);
            }
        }
    }
    
    /**
     * Applies the animated frame to the given geometry (2D model).
     * @param geom the 2D model
     * @param animation the animation (frame)
     * @param control the animated control
     */
    protected void applyAnimation2DGeometry(Geometry geom, A animation, E control) {
        Material mat = geom.getMaterial();
        Mesh mesh;
        switch (control.getType()) {
            case Sprite:
                reshape(geom.getMesh(), animation);
                mat.setTexture("ColorMap", ((SpriteAnimation2D) animation).getTexture());
                break;
            case Single:
                mesh = geom.getMesh();
                if (mesh instanceof Sprite) {
                    reshape(mesh, animation);
                    ((Sprite) mesh).showIndex(((SingleAnimation2D) animation).getIndex());
                }
                break;
            case RibbonBox:
                RibbonBoxAnimation2D rbad = (RibbonBoxAnimation2D) animation;                
                mesh = geom.getMesh();
                
                mat.setTexture("ColorMap", rbad.getTexture());
                if (mesh instanceof Sprite) {
                    Sprite sprite = (Sprite) mesh;
                    
                    reshape(mesh, animation);
                    sprite.applyCoords(rbad.getColumns(), rbad.getRows(), sprite.getTransform().getColPosition(), sprite.getTransform().getRowPosition());
                    sprite.showIndex(rbad.getFrame());
                }
                break;
            case Custom:
                LOG.log(Level.WARNING, "The type '{0}' is not supported.", control.getType());
                break;
            default:
                throw new AssertionError();
        }
    }
    
    /**
     * If possible, change the mesh size.
     * @param mesh mesh (Sprite)
     * @param animation the animated control
     */
    protected void reshape(Mesh mesh, A animation) {
        if ((animation instanceof AbstractAnimation2D<?>) && (mesh instanceof Sprite)) {
            float sw = ((Sprite) mesh).getNativeWidth(), 
                  sh = ((Sprite) mesh).getNativeHeight();
            
            Integer nw = ((AbstractAnimation2D<?>) animation).getWidth(), 
                    nh = ((AbstractAnimation2D<?>) animation).getHeight();
            if ((nw != null && sw != nw) && (nh != null && sh != nh)) {
                ((Sprite) mesh).applySize(nw, nh);
            }
        }
    }
}
