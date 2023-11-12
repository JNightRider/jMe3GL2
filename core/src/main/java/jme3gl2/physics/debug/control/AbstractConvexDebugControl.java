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

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.jme3.util.TempVars;

import jme3gl2.util.Converter;

import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Slice;
import org.dyn4j.geometry.Vector2;

/**
 * Clase <code>AbstractConvexDebugControl</code> encargado de controlar la 
 * posición y rotación de una forma física.
 * <p>
 * <b>Dyn4j</b> tiene la libertad de trasladar una forma <code>Convex</code>
 * a un punto específico de un cuerpo, dado que es relativo al cuerpo físico se
 * tiene que controlar por aparte.
 * <p>
 * Las siguientes formas, sus posiciones y rotación son peculiares por ende
 * tienen una clase dedicada a obtener dichas coordenadas:
 * <ul>
 * <li>Capsule</li>
 * <li>Circle</li>
 * <li>Ellipse</li>
 * <li>HalfEllipse</li>
 * <li>Slice</li>
 * </ul>
 * Por otro lado un forma <code>Polygon</code> o que implemente la interfaz
 * <code>Wound</code> no tiene la necesidad de tener un control dado que sus
 * puntos-vértices se modifican al trasladarlos o rotarlos en su forma.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT 
 * 
 * @since 2.5.0
 * @param <E> tipo-cuerpo
 */
public abstract class AbstractConvexDebugControl<E extends Convex> extends AbstractControl  {

    /**
     * Clase interna <code>CapsuleDebugControl</code> encargado de gestionar las
     * coordenadas de la forma <code>Capsule</code>.
     */
    public static final class CapsuleDebugControl extends AbstractConvexDebugControl<Capsule> {
        
        /**
         * Constructor de la clase <code>CapsuleDebugControl</code>.
         * @param shape forma física.
         */
        public CapsuleDebugControl(Capsule shape) {
            super(shape);
        }
        
        /**
         * (non-JavaDoc)
         * @see AbstractConvexDebugControl#getRotationAngle() 
         * 
         * @return double
         */
        @Override
        double getRotationAngle() {
            return this.shape.getRotationAngle();
        }
        
        /**
         * (non-JavaDoc)
         * @see AbstractConvexDebugControl#getLocaTranslation() 
         * 
         * @return double
         */
        @Override
        Vector2 getLocaTranslation() {
            return this.shape.getCenter();
        }        
    }

    /**
     * Clase interna <code>CircleDebugControl</code> encargado de gestionar las
     * coordenadas de la forma <code>Circle</code>.
     */
    public static final class CircleDebugControl extends AbstractConvexDebugControl<Circle> {
        
        /**
         * Constructor de la clase <code>CircleDebugControl</code>.
         * @param shape forma física.
         */
        public CircleDebugControl(Circle shape) {
            super(shape);
        }
        
        /**
         * (non-JavaDoc)
         * @see AbstractConvexDebugControl#getRotationAngle() 
         * 
         * @return double
         */
        @Override
        double getRotationAngle() {
            return 0;
        }
        
        /**
         * (non-JavaDoc)
         * @see AbstractConvexDebugControl#getLocaTranslation() 
         * 
         * @return double
         */
        @Override
        Vector2 getLocaTranslation() {
            return shape.getCenter();
        }        
    }
    
    /**
     * Clase interna <code>EllipseDebugControl</code> encargado de gestionar las
     * coordenadas de la forma <code>Ellipse</code>.
     */
    public static final class EllipseDebugControl extends AbstractConvexDebugControl<Ellipse> {
        
        /**
         * Constructor de la clase <code>EllipseDebugControl</code>.
         * @param shape forma física.
         */
        public EllipseDebugControl(Ellipse shape) {
            super(shape);
        }
        
        /**
         * (non-JavaDoc)
         * @see AbstractConvexDebugControl#getRotationAngle() 
         * 
         * @return double
         */
        @Override
        double getRotationAngle() {
            return shape.getRotationAngle();
        }
        
        /**
         * (non-JavaDoc)
         * @see AbstractConvexDebugControl#getLocaTranslation() 
         * 
         * @return double
         */
        @Override
        Vector2 getLocaTranslation() {
            return shape.getCenter();
        }        
    }
    
    /**
     * Clase interna <code>HalfEllipseDebugControl</code> encargado de gestionar las
     * coordenadas de la forma <code>HalfEllipse</code>.
     */
    public static final class HalfEllipseDebugControl extends AbstractConvexDebugControl<HalfEllipse> {
        
        /**
         * Constructor de la clase <code>HalfEllipseDebugControl</code>.
         * @param shape forma física.
         */
        public HalfEllipseDebugControl(HalfEllipse shape) {
            super(shape);
        }
        
        /**
         * (non-JavaDoc)
         * @see AbstractConvexDebugControl#getRotationAngle() 
         * 
         * @return double
         */
        @Override
        double getRotationAngle() {
            return shape.getRotationAngle();
        }

        /**
         * (non-JavaDoc)
         * @see AbstractConvexDebugControl#getLocaTranslation() 
         * 
         * @return double
         */
        @Override
        Vector2 getLocaTranslation() {
            return shape.getEllipseCenter();
        }        
    }
    
    /**
     * Clase interna <code>SliceDebugControl</code> encargado de gestionar las
     * coordenadas de la forma <code>Slice</code>.
     */
    public static final class SliceDebugControl extends AbstractConvexDebugControl<Slice> {
        /**
         * Constructor de la clase <code>SliceDebugControl</code>.
         * @param shape forma física.
         */        
        public SliceDebugControl(Slice shape) {
            super(shape);
        }
        
        /**
         * (non-JavaDoc)
         * @see AbstractConvexDebugControl#getRotationAngle() 
         * 
         * @return double
         */
        @Override
        double getRotationAngle() {
            return shape.getRotationAngle();
        }
        
        /**
         * (non-JavaDoc)
         * @see AbstractConvexDebugControl#getLocaTranslation() 
         * 
         * @return double
         */
        @Override
        Vector2 getLocaTranslation() {
            return shape.getCircleCenter();
        }        
    }
    
    /** Forma física del objeto. */
    final E shape;

    /**
     * Constructor privato de la clase <code>AbstractConvexDebugControl</code>.
     * @param shape forma física.
     */
    private AbstractConvexDebugControl(E shape) {
        this.shape    = shape;
    }
    
    /**
     * (non-JavaDoc)
     * @see com.jme3.scene.control.AbstractControl#controlUpdate(float) 
     * 
     * @param tpf float
     */
    @Override
    protected void controlUpdate(float tpf) {
        applyPhysicsLocation();
        applyPhysicsRotation();
    }

    /**
     * Aplica la rotación que tien la forma al la geometría.
     */
    void applyPhysicsRotation() {
        double rotation = getRotationAngle();
        final TempVars tempVars = TempVars.get();
        final Quaternion quaternion = tempVars.quat1;

        quaternion.fromAngleAxis(Converter.toFloat(rotation), new Vector3f(0.0F, 0.0F, 1.0F));
        this.spatial.setLocalRotation(quaternion);

        tempVars.release();
    }
    
    /**
     * Método encargado de devolver el angulo de rotación de la forma.
     * @return angulo.
     */
    abstract double getRotationAngle();
    
    /**
     * Mértodo encargado de devolver un desface(posición local) de la forma.
     * @return posición.
     */
    abstract Vector2 getLocaTranslation();
    
    /**
     * Método encargado de aplicar una traslación físico.
     */
    void applyPhysicsLocation() {
        final Vector2 center = getLocaTranslation();
        if (center == null)
            return;

        final float posX = Converter.toFloat(center.x);
        final float posY = Converter.toFloat(center.y);
        
        spatial.setLocalTranslation(posX, posY, spatial.getLocalTranslation().z);
    }
    
    /**
     * (non-JavaDoc)
     * @see com.jme3.scene.control.AbstractControl#controlRender(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort) 
     * 
     * @param rm render-manager
     * @param vp view-port
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
}
