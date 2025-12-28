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

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Vector2;

import org.je3gl.physics.control.PhysicsBody2D;
import org.je3gl.physics.joint.PhysicsJoint;
import org.je3gl.scene.debug.Circle2D;
import org.je3gl.scene.debug.Cross;
import org.je3gl.scene.debug.Polygon2D;
import static org.je3gl.physics.util.Converter.*;

/**
 * Class responsible for controlling joint objects in the debugger integrated
 * by jMe3GL2.
 * 
 * @author wil
 * @param <E> body type|joint
 * 
 * @version 1.0.0
 * @since 3.1.0
 */
abstract class Dyn4jJointControl<E extends Joint<PhysicsBody2D>> extends AbstractPhysicsDebugControl<PhysicsBody2D> {
    
    /** Number of segments for circular figures. */
    private static final float CIRCLE_SEGMENT_NUMBER = 10;
    /** Size of the segments of a square. */
    private static final float SQUARE_SEGMENT_SIZE = 0.2f;
    /** Radius of the circles. */
    private static final float CIRCLE_RADIUS       = 0.1f;
    /** size of the cross-shaped segments */
    private static final float CROSS_SEGMENT_SIZE  = 0.1f;

    /**
     * Internal/private class responsible for controlling an articulation of type: <code>any</code>
     * @param <T> body type|joint
     */
    private static class Dyn4jDefJointControl<T extends Joint<PhysicsBody2D>> extends Dyn4jJointControl<T> {

        /**
         * Constructor of class <code>Dyn4jDefJointControl</code>.
         * @param dyn4jDebugAppState debugger
         * @param joint joint
         */
        public Dyn4jDefJointControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, T joint) {
            super(dyn4jDebugAppState, joint);
            Vector2 np1 = Dyn4jDefJointControl.this.getAnchor1();
            Vector2 np2 = Dyn4jDefJointControl.this.getAnchor2();
            
            if (np1 != null) {
                this.anchorA = renderer.renderMesh(Circle2D.class, toVector3fValueOfJME3(np1), CIRCLE_RADIUS, CIRCLE_SEGMENT_NUMBER);
                this.jointNode.attachChild(anchorA);
            }
            
            if (np2 != null) {
                this.anchorB = renderer.renderMesh(Circle2D.class, toVector3fValueOfJME3(np2), CIRCLE_RADIUS, CIRCLE_SEGMENT_NUMBER);
                this.jointNode.attachChild(anchorB);
            }
        }

        /**
         * Return the joint anchor 1
         * @return Vector2
         */
        @Override
        protected Vector2 getAnchor1() {
            return invoke(Vector2.class, "getAnchor1");
        }

        /**
         * Return the joint anchor 1
         * @return  Vector2
         */
        @Override
        protected Vector2 getAnchor2() {
            return invoke(Vector2.class, "getAnchor2");
        }
        
        /**
         * Invoking a built-in method in the joint being debugged through its 
         * class (metaprogramming).
         * 
         * @param <T> return type
         * @param clazzType type class
         * @param name method name
         * @param args arguments of the method
         * @return value
         */
        @SuppressWarnings("unchecked")
        private <T> T invoke(Class<T> clazzType, String name, Object ...args) {
            try {
                Class[] parameterTypes = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    parameterTypes[i] = args[i].getClass();
                }
                
                Method method = joint.getClass().getDeclaredMethod(name, parameterTypes);
                if (method == null) {
                    return null;
                }
                
                method.setAccessible(true);
                Object value = method.invoke(joint, args);
                
                if (clazzType.isAssignableFrom(value.getClass())) {
                    return (T) value;
                }
                return null;
            } catch (IllegalAccessException | IllegalArgumentException | 
                    NoSuchMethodException   | SecurityException | InvocationTargetException e) {
                return null;
            }
        }
    }
    
    /**
     * Internal/private class responsible for controlling an articulation of type: <code>DistanceJoint</code>
     * @param <T> body type|joint
     */
    private static class Dyn4jDistanceJointControl extends Dyn4jJointControl<DistanceJoint<PhysicsBody2D>> {

        /**
         * Constructor of class <code>Dyn4jDistanceJointControl</code>.
         * @param dyn4jDebugAppState debugger
         * @param joint joint
         */
        public Dyn4jDistanceJointControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, DistanceJoint<PhysicsBody2D> joint) {
            super(dyn4jDebugAppState, joint);
            
            Vector3f p1 = toVector3fValueOfJME3(joint.getAnchor1());
            Vector3f p2 = toVector3fValueOfJME3(joint.getAnchor2());
            
            this.line = renderer.renderMesh(Line.class, p1, p2);
            this.lineWidth = 2;
            
            this.anchorA = renderer.renderMesh(Circle2D.class, p1, CIRCLE_RADIUS, CIRCLE_SEGMENT_NUMBER);
            this.anchorB = renderer.renderMesh(Circle2D.class, p1, CIRCLE_RADIUS, CIRCLE_SEGMENT_NUMBER);
            
            this.jointNode.attachChild(line);
            this.jointNode.attachChild(anchorA);
            this.jointNode.attachChild(anchorB);
        }

        /**
         * Return the joint anchor 1
         * @return Vector2
         */
        @Override
        protected Vector2 getAnchor1() {
            return joint.getAnchor1();
        }

        /**
         * Return the joint anchor 2
         * @return Vector2
         */
        @Override
        protected Vector2 getAnchor2() {
            return joint.getAnchor2();
        }
    }
    
    /**
     * Internal/private class responsible for controlling an articulation of type: <code>DistanceJoint</code>
     * @param <T> body type|joint
     */
    private static class Dyn4jRevoluteJointControl extends Dyn4jJointControl<RevoluteJoint<PhysicsBody2D>> {

        /**
         * Constructor of class <code>Dyn4jRevoluteJointControl</code>.
         * @param dyn4jDebugAppState debugger
         * @param joint joint
         */
        public Dyn4jRevoluteJointControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, RevoluteJoint<PhysicsBody2D> joint) {
            super(dyn4jDebugAppState, joint);
            
            Vector3f p1 = toVector3fValueOfJME3(joint.getAnchor1());
            this.anchorA = renderer.renderMesh(Circle2D.class, p1, CIRCLE_RADIUS, CIRCLE_SEGMENT_NUMBER);
            this.jointNode.attachChild(anchorA);
        }

        /**
         * Return the joint anchor 1
         * @return Vector2
         */
        @Override
        protected Vector2 getAnchor1() {
            return joint.getAnchor1();
        }

        /**
         * Return the joint anchor 2
         * @return Vector2
         */
        @Override
        protected Vector2 getAnchor2() {
            return joint.getAnchor2();
        }        
    }
    
    /**
     * Internal/private class responsible for controlling an articulation of type: <code>PrismaticJoint</code>
     * @param <T> body type|joint
     */
    private static class Dyn4jPrismaticJointControl extends Dyn4jJointControl<PrismaticJoint<PhysicsBody2D>> {

        /**
         * Constructor of class <code>Dyn4jPrismaticJointControl</code>.
         * @param dyn4jDebugAppState debugger
         * @param joint joint
         */
        public Dyn4jPrismaticJointControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, PrismaticJoint<PhysicsBody2D> joint) {
            super(dyn4jDebugAppState, joint);
            
            Vector3f p1 = toVector3fValueOfJME3(joint.getAnchor1());
            Vector3f p2 = toVector3fValueOfJME3(joint.getAnchor2());
            
            this.line = renderer.renderMesh(Line.class, p1, p2);
            this.lineWidth = 2;
            
            this.anchorA = renderer.renderMesh(Circle2D.class, p1, CIRCLE_RADIUS, CIRCLE_SEGMENT_NUMBER);
            this.anchorB = renderer.renderMesh(Circle2D.class, p2, CIRCLE_RADIUS, CIRCLE_SEGMENT_NUMBER);
            
            this.jointNode.attachChild(line);
            this.jointNode.attachChild(anchorA);
            this.jointNode.attachChild(anchorB);
        }

        /**
         * Responsible method for managing the materials used by linear models.
         * @param isJointActive boolean
         * @return material
         */
        @Override
        protected Material getLineGeomMaterial(boolean isJointActive) {
            return getMaterial(isJointActive ? ColorRGBA.Brown : ColorRGBA.Pink);
        }

        /**
         * Responsible method for managing the materials used in anchoring models
         * @param isJointActive boolean
         * @return material
         */
        @Override
        protected Material getAnchorGeomMaterial(boolean isJointActive) {
            return getMaterial(isJointActive ? ColorRGBA.Orange : ColorRGBA.Pink);
        }

        /**
         * Return the joint anchor 1
         * @return Vector2
         */
        @Override
        protected Vector2 getAnchor1() {
            return joint.getAnchor1();
        }

        /**
         * Return the joint anchor 2
         * @return Vector2
         */
        @Override
        protected Vector2 getAnchor2() {
            return joint.getAnchor2();
        }
    }
    
    /**
     * Internal/private class responsible for controlling an articulation of type: <code>PulleyJoint</code>
     * @param <T> body type|joint
     */
    private static class Dyn4jPulleyJointControl extends Dyn4jJointControl<PulleyJoint<PhysicsBody2D>> {
        
        /** Model: pulleyLineGeomA */
        protected Geometry pulleyLineGeomA;
        /** Model: pulleyLineGeomB */
        protected Geometry pulleyLineGeomB;
        /** Model: pulleyAnchorGeomA */
        protected Geometry pulleyAnchorGeomA;
        /** Model: pulleyAnchorGeomB */
        protected Geometry pulleyAnchorGeomB;

        /**
         * Constructor of class <code>Dyn4jPulleyJointControl</code>.
         * @param dyn4jDebugAppState debugger
         * @param joint joint
         */
        public Dyn4jPulleyJointControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, PulleyJoint<PhysicsBody2D> joint) {
            super(dyn4jDebugAppState, joint);
            
            Vector3f p1 = toVector3fValueOfJME3(joint.getAnchor1());
            Vector3f p2 = toVector3fValueOfJME3(joint.getPulleyAnchor1());
            Vector3f p3 = toVector3fValueOfJME3(joint.getPulleyAnchor2());
            Vector3f p4 = toVector3fValueOfJME3(joint.getAnchor2());
            

            this.line = renderer.renderMesh(Line.class, p2, p3);            
            this.lineWidth = 1;
            this.jointNode.attachChild(line);
            
            this.pulleyLineGeomA = renderer.renderMesh(Line.class, p1, p2);         
            this.pulleyLineGeomB = renderer.renderMesh(Line.class, p3, p4);
            this.jointNode.attachChild(pulleyLineGeomA);
            this.jointNode.attachChild(pulleyLineGeomB);
            
            this.anchorA = renderer.renderMesh(Circle2D.class, p1, CIRCLE_RADIUS, CIRCLE_SEGMENT_NUMBER);
            this.anchorB = renderer.renderMesh(Circle2D.class, p4, CIRCLE_RADIUS, CIRCLE_SEGMENT_NUMBER);
            this.jointNode.attachChild(anchorA);
            this.jointNode.attachChild(anchorB);
            
            this.pulleyAnchorGeomA = renderer.renderMesh(Cross.class, p2, CROSS_SEGMENT_SIZE, true);
            this.pulleyAnchorGeomB = renderer.renderMesh(Cross.class, p3, CROSS_SEGMENT_SIZE, true);
            this.jointNode.attachChild(pulleyAnchorGeomA);
            this.jointNode.attachChild(pulleyAnchorGeomB);
        }
        
        @Override
        public void update(final float tpf) {
            final PulleyJoint pulleyJoint = (PulleyJoint) this.joint;

            final Vector3f p1 = toVector3fValueOfJME3(pulleyJoint.getAnchor1());
            final Vector3f p2 = toVector3fValueOfJME3(pulleyJoint.getPulleyAnchor1());
            final Vector3f p3 = toVector3fValueOfJME3(pulleyJoint.getPulleyAnchor2());
            final Vector3f p4 = toVector3fValueOfJME3(pulleyJoint.getAnchor2());

            final Material anchorGeomMaterial = getAnchorGeomMaterial(this.joint.isEnabled());
            final Material lineGeomMaterial = getLineGeomMaterial(this.joint.isEnabled());

            this.anchorA.setLocalTranslation(p1);
            this.anchorA.setMaterial(anchorGeomMaterial);

            this.pulleyAnchorGeomA.setLocalTranslation(p2);
            this.pulleyAnchorGeomA.setMaterial(anchorGeomMaterial);

            this.pulleyAnchorGeomB.setLocalTranslation(p3);
            this.pulleyAnchorGeomB.setMaterial(anchorGeomMaterial);

            this.anchorB.setLocalTranslation(p4);
            this.anchorB.setMaterial(anchorGeomMaterial);

            ((Line) this.line.getMesh()).updatePoints(p2, p3);
            this.line.setMaterial(lineGeomMaterial);

            ((Line) this.pulleyLineGeomA.getMesh()).updatePoints(p1, p2);
            this.pulleyLineGeomA.setMaterial(lineGeomMaterial);

            ((Line) this.pulleyLineGeomB.getMesh()).updatePoints(p3, p4);
            this.pulleyLineGeomB.setMaterial(lineGeomMaterial);
        }

        /**
         * Return the joint anchor 1
         * @return Vector2
         */
        @Override
        protected Vector2 getAnchor1() {
            return joint.getAnchor1();
        }

        /**
         * Return the joint anchor 2
         * @return Vector2
         */
        @Override
        protected Vector2 getAnchor2() {
            return joint.getAnchor2();
        }
        
        /**
         * Responsible method for managing the materials used by linear models.
         * @param isJointActive boolean
         * @return material
         */
        @Override
        protected Material getLineGeomMaterial(boolean isJointActive) {
            return getMaterial(isJointActive ? ColorRGBA.Orange : ColorRGBA.Pink);
        }

        /**
         * Responsible method for managing the materials used in anchoring models
         * @param isJointActive boolean
         * @return material
         */
        @Override
        protected Material getAnchorGeomMaterial(boolean isJointActive) {
            return getMaterial(isJointActive ? ColorRGBA.Green : ColorRGBA.Pink);
        }        
    }
    
    /**
     * Internal/private class responsible for controlling an articulation of type: <code>WeldJoint</code>
     * @param <T> body type|joint
     */
    private static class Dyn4jWeldJointControl extends Dyn4jJointControl<WeldJoint<PhysicsBody2D>> {
        
        /**
         * Constructor of class <code>Dyn4jWeldJointControl</code>.
         * @param dyn4jDebugAppState debugger
         * @param joint joint
         */
        public Dyn4jWeldJointControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, WeldJoint<PhysicsBody2D> joint) {
            super(dyn4jDebugAppState, joint);
            
            Vector3f p1 = toVector3fValueOfJME3(joint.getAnchor1());
            this.anchorA = renderer.renderMesh(Cross.class, p1, CROSS_SEGMENT_SIZE, false);
        }

        /**
         * Return the joint anchor 1
         * @return Vector2
         */
        @Override
        protected Vector2 getAnchor1() {
            return joint.getAnchor1();
        }

        /**
         * Return the joint anchor 2
         * @return Vector2
         */
        @Override
        protected Vector2 getAnchor2() {
            return joint.getAnchor2();
        }
    }
    
    /**
     * Internal/private class responsible for controlling an articulation of type: <code>WheelJoint</code>
     * @param <T> body type|joint
     */
    private static class Dyn4jWheelJointControl extends Dyn4jJointControl<WheelJoint<PhysicsBody2D>> {

        /**
         * Constructor of class <code>Dyn4jWheelJointControl</code>.
         * @param dyn4jDebugAppState debugger
         * @param joint joint
         */
        public Dyn4jWheelJointControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, WheelJoint<PhysicsBody2D> joint) {
            super(dyn4jDebugAppState, joint);
            
            Vector3f p1 = toVector3fValueOfJME3(joint.getAnchor1());
            Vector3f p2 = toVector3fValueOfJME3(joint.getAnchor2());

            this.line = renderer.renderMesh(Line.class, p1, p2);
            this.lineWidth = 2;
            this.jointNode.attachChild(line);
            
            this.anchorA = renderer.renderMesh(Polygon2D.class, true, SQUARE_SEGMENT_SIZE, p1); //createSquare("SquareGeom1", p1);
            this.anchorB = renderer.renderMesh(Circle2D.class, p2, CIRCLE_RADIUS, CIRCLE_SEGMENT_NUMBER);
            
            this.jointNode.attachChild(anchorA);
            this.jointNode.attachChild(anchorB);
        }

        /**
         * Return the joint anchor 1
         * @return Vector2
         */
        @Override
        protected Vector2 getAnchor1() {
            return joint.getAnchor1();
        }

        /**
         * Return the joint anchor 2
         * @return Vector2
         */
        @Override
        protected Vector2 getAnchor2() {
            return joint.getAnchor2();
        }
        
        /**
         * Responsible method for managing the materials used by linear models.
         * @param isJointActive boolean
         * @return material
         */
        @Override
        protected Material getLineGeomMaterial(final boolean isJointActive) {
            return getMaterial(isJointActive ? ColorRGBA.Green : ColorRGBA.Pink);
        }

        /**
         * Responsible method for managing the materials used in anchoring models
         * @param isJointActive boolean
         * @return material
         */
        @Override
        protected Material getAnchorGeomMaterial(final boolean isJointActive) {
             return getMaterial(isJointActive ? ColorRGBA.Yellow : ColorRGBA.Pink);
        }

    }
    
    /**
     * Method responsible for creating a new instance of a controller according 
     * to the type of joint.
     * 
     * @param dyn4jDebugAppState debugger
     * @param joint joint
     * @return control
     */
    @SuppressWarnings("unchecked")
    public static Dyn4jJointControl getNewInstance(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, Joint<PhysicsBody2D> joint) {
        PhysicsJoint.Type type = PhysicsJoint.Type.valueOf(joint);        
        switch (type) {
            case PrismaticJoint: return new Dyn4jPrismaticJointControl(dyn4jDebugAppState, (PrismaticJoint<PhysicsBody2D>) joint);
            case DistanceJoint: return new Dyn4jDistanceJointControl(dyn4jDebugAppState, (DistanceJoint<PhysicsBody2D>) joint);
            case RevoluteJoint: return new Dyn4jRevoluteJointControl(dyn4jDebugAppState, (RevoluteJoint<PhysicsBody2D>) joint);            
            case PulleyJoint:   return new Dyn4jPulleyJointControl(dyn4jDebugAppState, (PulleyJoint<PhysicsBody2D>) joint);
            case WeldJoint:     return new Dyn4jWeldJointControl(dyn4jDebugAppState, (WeldJoint<PhysicsBody2D>) joint);
            case WheelJoint:    return new Dyn4jWheelJointControl(dyn4jDebugAppState, (WheelJoint<PhysicsBody2D>) joint);
            case Custom:
                return null;
            default: 
                return new Dyn4jDefJointControl(dyn4jDebugAppState, joint);
        }
    }
    
     /** Renderer for physical shapes. */
    protected Graphics2DRenderer renderer;    
    /* joint */
    protected E joint;
    
    /**
     *A line that connects two points of the joint.
     */
    protected Geometry line;
    /** Line thickness. */
    protected float lineWidth;
    
    /** the first anchor. */
    protected Geometry anchorA;
    /** The second anchor. */
    protected Geometry anchorB;
    /** root node. */
    protected Node jointNode;
    
    /**
     * Constructor of class <code>Dyn4jDebugAppState</code>.
     * @param dyn4jDebugAppState debugger
     * @param joint joint
     */
    public Dyn4jJointControl(Dyn4jDebugAppState<PhysicsBody2D> dyn4jDebugAppState, E joint) {
        super(dyn4jDebugAppState);
        this.joint      = joint;
        this.renderer   = dyn4jDebugAppState.getGraphics2DRenderer();        
        this.jointNode  = new Node("Joints<?>");
        this.lineWidth  = 1;
    }
    
    /**
     * (non-javadoc)
     * @see AbstractPhysicsDebugControl#setSpatial(com.jme3.scene.Spatial) 
     * 
     * @param spatial spatial
     */
    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null && spatial instanceof Node) {
            final Node spatialAsNode = (Node) spatial;
            spatialAsNode.attachChild(this.jointNode);
        } else if (spatial == null && this.spatial != null) {
            final Node spatialAsNode = (Node) this.spatial;
            spatialAsNode.detachChild(this.jointNode);
        }
        super.setSpatial(spatial);
    }

    /**
     * (non-javadoc)
     * @see AbstractPhysicsDebugControl#controlUpdate(float) 
     * 
     * @param f float
     */
    @Override
    protected void controlUpdate(float f) {
        Vector2 np1 = getAnchor1();
        Vector2 np2 = getAnchor2();
        
        final Vector3f p1 = np1 == null ? null : toVector3fValueOfJME3(np1);
        final Vector3f p2 = np2 == null ? null : toVector3fValueOfJME3(np2);
        
        Material mat;
        if (this.line != null) {
            final Mesh mesh = this.line.getMesh();
            if (mesh instanceof Line) {
                ((Line) mesh).updatePoints(p1, p2);
            }
            
            mat = line.getMaterial();
            if (mat == null) {
                mat = getLineGeomMaterial(joint.isEnabled());
                mat.getAdditionalRenderState().setLineWidth(lineWidth);                
                mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
            }
            line.setMaterial(mat);
        }

        if (this.anchorA != null) {
            mat = this.anchorA.getMaterial();
            if (mat == null) {
                this.anchorA.setMaterial(getAnchorGeomMaterial(joint.isEnabled()));
            }
            
            this.anchorA.setLocalTranslation(p1);
        }

        if (this.anchorB != null) {
            mat = this.anchorB.getMaterial();
            if (mat == null) {
                this.anchorB.setMaterial(getAnchorGeomMaterial(joint.isEnabled()));
            }
            this.anchorB.setLocalTranslation(p2);
        }
    }
    
    /**
     * Return the joint anchor 1
     * @return Vector2
     */
    protected abstract Vector2 getAnchor1();
    
    /**
     * Return the joint anchor 2
     * @return Vector2
     */
    protected abstract Vector2 getAnchor2();
    
    /**
     * Responsible method for managing the materials used by linear models.
     * @param isJointActive boolean
     * @return material
     */
    protected Material getLineGeomMaterial(boolean isJointActive) {
        return getMaterial(isJointActive ? ColorRGBA.DarkGray : ColorRGBA.Pink);
    }

    /**
     * Responsible method for managing the materials used in anchoring models
     * @param isJointActive boolean
     * @return material
     */
    protected Material getAnchorGeomMaterial(boolean isJointActive) {
        return getMaterial(isJointActive ? ColorRGBA.Gray : ColorRGBA.Pink);
    }

    /**
     * Create a new material.
     *
     * @param color color rgba
     * @return material
     */
    protected Material getMaterial(ColorRGBA color) {
        return renderer.createMat(color);
    }
}
