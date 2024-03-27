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
package jme3gl2.physics.joint;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector2f;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jme3gl2.physics.control.PhysicsBody2D;
import jme3gl2.util.Converter;

import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.dynamics.joint.PinJoint;
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Vector2;

/**
 * @param <T>
 * @param <E>
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class PhysicsJoint<T extends PhysicsBody2D, E extends Joint<T>> implements Savable {

    private static final Logger LOGGER = Logger.getLogger(PhysicsJoint.class.getName());

    public static enum Type {
        AngleJoint,        
        DistanceJoint,        
        FrictionJoint,
        PinJoint,
        PrismaticJoint,
        PulleyJoint,
        RevoluteJoint,
        WeldJoint,
        WheelJoint,
        MotorJoint,
        Custom;
        
        public static Type valueOf(Joint<?> joint) {
            return null;
        }
    }
    
    private Type type;
    private E joint;

    protected PhysicsJoint() {
    }

    public PhysicsJoint(E joint) {
        this.type  = Type.valueOf(joint);
        this.joint = joint;
    }

    public Type getType() {
        return type;
    }

    public E getJoint() {
        return joint;
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#write(com.jme3.export.JmeExporter) 
     * 
     * @param ex {@link com.jme3.export.JmeExporter}
     * @throws IOException throws
     */
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(type, "type", null);
        
        switch (type) {
            case AngleJoint:
                AngleJoint<T> aj = (AngleJoint<T>) joint;                
                T ajBody1 = aj.getBody1();
                T ajBody2 = aj.getBody2();
                
                out.write(ajBody1, "Body1", null);
                out.write(ajBody2, "Body2", null);
                
                out.write(aj.getLowerLimit(), "LowerLimit", 0.0);
                out.write(aj.getUpperLimit(), "UpperLimit", 0.0);
                
                out.write(aj.isLimitsEnabled(), "LimitsEnabled", false);
                out.write(aj.getLimitsReferenceAngle(), "LimitsReferenceAngle", 0);
                
                out.write(aj.getRatio(), "Ratio", 0);
                break;
            case DistanceJoint:
                DistanceJoint<T> dj = (DistanceJoint<T>) joint;
                
                T djBody1 = dj.getBody1();
                T djBody2 = dj.getBody2();
                
                Vector2 djAnchor1 = dj.getAnchor1(),
                        djAnchor2 = dj.getAnchor2();
                
                out.write(djBody1, "Body1", null);
                out.write(djBody2, "Body2", null);
                
                out.write(Converter.toVector2fValueOfJME3(djAnchor1), "Anchor1", null);
                out.write(Converter.toVector2fValueOfJME3(djAnchor2), "Anchor2", null);
                
                out.write(dj.getSpringFrequency(), "SpringFrequency", 0);
                out.write(dj.getSpringDampingRatio(), "SpringDampingRatio", 0);
                out.write(dj.getRestDistance(), "RestDistance", 0);
                break;
            case FrictionJoint:
                FrictionJoint<T> fj = (FrictionJoint<T>) joint;
                
                T fjBody1 = fj.getBody1();
                T fjBody2 = fj.getBody2();
                
                Vector2 fjAnchor = fj.getAnchor1();
                
                out.write(fjBody1, "Body1", null);
                out.write(fjBody2, "Body2", null);
                
                out.write(Converter.toVector2fValueOfJME3(fjAnchor), "Anchor", null);
                out.write(fj.getMaximumForce(), "MaximumForce", 0);
                out.write(fj.getMaximumTorque(), "MaximumTorque", 0);
                break;
            case PinJoint:
                PinJoint<T> mj = (PinJoint<T>) joint;
                
                T mjBody         = mj.getBody();
                Vector2 mjAnchor = mj.getAnchor();
                
                out.write(mjBody, "Body", null);                
                out.write(Converter.toVector2fValueOfJME3(mjAnchor), "Anchor", null);
                
                out.write(mj.getSpringFrequency(), "SpringFrequency", 0);
                out.write(mj.getSpringDampingRatio(), "SpringDampingRatio", 0);
                out.write(mj.getMaximumSpringForce(), "MaximumSpringForce", 0);
                out.write(Converter.toVector2fValueOfJME3(mj.getTarget()), "Target", null);
                break;
            case PrismaticJoint:
                PrismaticJoint<T> pj = (PrismaticJoint<T>) joint;
                
                T pjBody1 = pj.getBody1();
                T pjBody2 = pj.getBody2();
                
                Vector2 pjAnchor = pj.getAnchor1(),
                        pjAxis   = pj.getAxis();
                
                out.write(pjBody1, "Body1", null);
                out.write(pjBody2, "Body2", null);
                
                out.write(Converter.toVector2fValueOfJME3(pjAnchor), "Anchor", null);
                out.write(Converter.toVector2fValueOfJME3(pjAxis), "Axis", null);
                
                out.write(pj.isLowerLimitEnabled(), "LowerLimitEnabled", false);
                out.write(pj.isUpperLimitEnabled(), "UpperLimitEnabled", false);
                
                out.write(pj.getLowerLimit(), "LowerLimit", 0);
                out.write(pj.getUpperLimit(), "UpperLimit", 0);
                
                out.write(pj.getReferenceAngle(), "ReferenceAngle", 0);
                out.write(pj.isMotorEnabled(), "MotorEnabled", false);
                out.write(pj.getMotorSpeed(), "MotorSpeed", 0);
                out.write(pj.getMaximumMotorForce(), "MaximumMotorForce", 0);
                break;
            case PulleyJoint:
                PulleyJoint<T> pyj = (PulleyJoint<T>) joint;
                
                T pyjBody1 = pyj.getBody1();
                T pyjBody2 = pyj.getBody2();
                
                Vector2 pulleyAnchor1 = pyj.getPulleyAnchor1(),
                        pulleyAnchor2 = pyj.getPulleyAnchor2();
                
                Vector2 pyjAnchor1 = pyj.getAnchor1(),
                        pyjAnchor2 = pyj.getAnchor2();
                
                out.write(pyjBody1, "Body1", null);
                out.write(pyjBody2, "Body2", null);
                
                out.write(Converter.toVector2fValueOfJME3(pulleyAnchor1), "PulleyAnchor1", null);
                out.write(Converter.toVector2fValueOfJME3(pulleyAnchor2), "PulleyAnchor2", null);
                
                out.write(Converter.toVector2fValueOfJME3(pyjAnchor1), "Anchor1", null);
                out.write(Converter.toVector2fValueOfJME3(pyjAnchor2), "Anchor2", null);
                
                out.write(pyj.getRatio(), "Ratio", 0);
                break;
            case RevoluteJoint:
                RevoluteJoint<T> rj = (RevoluteJoint<T>) joint;
                T rjBody1 = rj.getBody1();
                T rjBody2 = rj.getBody2();
                
                Vector2 rjAnchor = rj.getAnchor1();
                
                out.write(rjBody1, "Body1", null);
                out.write(rjBody2, "Body2", null);
                
                out.write(Converter.toVector2fValueOfJME3(rjAnchor), "Anchor", null);
                
                out.write(rj.isLimitsEnabled(), "LimitsEnabled", false);
                out.write(rj.getLowerLimit(), "LowerLimit", 0);
                out.write(rj.getUpperLimit(), "UpperLimit", 0);
                out.write(rj.getLimitsReferenceAngle(), "LimitsReferenceAngle", 0);
                out.write(rj.isMotorEnabled(), "MotorEnabled", false);
                out.write(rj.getMotorSpeed(), "MotorSpeed", 0);
                break;
            case WeldJoint:
                WeldJoint<T> wj = (WeldJoint<T>) joint;
                
                T wjBody1 = wj.getBody1();
                T wjBody2 = wj.getBody2();
                
                Vector2 wjAnchor = wj.getAnchor1();
                
                out.write(wjBody1, "Body1", null);
                out.write(wjBody2, "Body2", null);
                
                out.write(Converter.toVector2fValueOfJME3(wjAnchor), "Anchor", null);
                
                out.write(wj.getSpringFrequency(), "SpringFrequency", 0);
                out.write(wj.getSpringDampingRatio(), "SpringDampingRatio", 0);
                out.write(wj.getLimitsReferenceAngle(), "LimitsReferenceAngle", 0);
                break;
            case WheelJoint:
                WheelJoint<T> whj = (WheelJoint<T>) joint;
                
                T whjBody1 = whj.getBody1();
                T whjBody2 = whj.getBody2();
                
                Vector2 whjAnchor = whj.getAnchor1(),
                        whjAxis   = whj.getAxis();
                
                out.write(whjBody1, "Body1", null);
                out.write(whjBody2, "Body2", null);
                
                out.write(Converter.toVector2fValueOfJME3(whjAnchor), "Anchor", null);
                out.write(Converter.toVector2fValueOfJME3(whjAxis), "Axis", null);
                
                out.write(whj.getSpringFrequency(), "SpringFrequency", 0);
                out.write(whj.getSpringDampingRatio(), "SpringDampingRatio", 0);
                out.write(whj.isMotorEnabled(), "MotorEnabled", false);
                out.write(whj.getMotorSpeed(), "MotorSpeed", 0);
                out.write(whj.getMaximumMotorTorque(), "MaximumMotorTorque", 0);
                break;
            case MotorJoint:
                MotorJoint<T> mtj = (MotorJoint<T>) joint;
                T mtjBody1 = mtj.getBody1();
                T mtjBody2 = mtj.getBody2();
                
                out.write(mtjBody1, "Body1", null);
                out.write(mtjBody2, "Body2", null);
                
                out.write(Converter.toVector2fValueOfJME3(mtj.getLinearTarget()), "LinearTarget", null);
                out.write(mtj.getAngularTarget(), "AngularTarget", 0);
                out.write(mtj.getCorrectionFactor(), "CorrectionFactor", 0);
                out.write(mtj.getMaximumForce(), "MaximumForce", 0);
                out.write(mtj.getMaximumTorque(), "MaximumTorque", 0);
                break;
            case Custom:
                LOGGER.log(Level.WARNING, "Unknown joint class: {0}", joint.getClass().getName());
                break;
            default:
                throw new AssertionError();
        }        
        if (type != Type.Custom) {
            out.write(joint.isCollisionAllowed(), "CollisionAllowed", false);
        }
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#read(com.jme3.export.JmeImporter) 
     * 
     * @param im {@link com.jme3.export.JmeImporter}
     * @throws IOException throws
     */
    @Override
    @SuppressWarnings("unchecked")
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);        
        type            = in.readEnum("type", Type.class, null);
        
        if (type == null) {
            throw new NullPointerException("Error reading joint type");
        }
        
        switch (type) {
            case AngleJoint:        
                T ajBody1 = (T) in.readSavable("Body1", null);
                T ajBody2 = (T) in.readSavable("Body2", null);
                
                AngleJoint<T> aj = new AngleJoint<>(ajBody1, ajBody2);
                aj.setLimits(in.readDouble("LowerLimit", 0), in.readDouble("UpperLimit", 0));
                aj.setLimitsEnabled(in.readBoolean("LimitsEnabled", false));
                aj.setLimitsReferenceAngle(in.readDouble("LimitsReferenceAngle", 0));
                aj.setRatio(in.readDouble("Ratio", 0));
                
                joint = (E) aj;
                break;
            case DistanceJoint:
                T djBody1 = (T) in.readSavable("Body1", null);
                T djBody2 = (T) in.readSavable("Body2", null);
                
                Vector2 djAnchor1 = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Anchor1", new Vector2f())),
                        djAnchor2 = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Anchor2", new Vector2f()));
                
                DistanceJoint<T> dj = new DistanceJoint<>(djBody1, djBody2, djAnchor1, djAnchor2);
                dj.setSpringFrequency(in.readDouble("SpringFrequency", 0));
                dj.setSpringDampingRatio(in.readDouble("SpringDampingRatio", 0));
                dj.setRestDistance(in.readDouble("RestDistance", 0));
                
                joint = (E) dj;
                break;
            case FrictionJoint:
                T fjBody1 = (T) in.readSavable("Body1", null);
                T fjBody2 = (T) in.readSavable("Body2", null);
                
                Vector2 fjAnchor = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Anchor", new Vector2f()));
                
                FrictionJoint<T> fj = new FrictionJoint<>(fjBody1, fjBody2, fjAnchor);
                fj.setMaximumForce(in.readDouble("MaximumForce", 0));
                fj.setMaximumTorque(in.readDouble("MaximumTorque", 0));
                
                joint = (E) fj;
                break;
            case PinJoint:
                 T mjBody         = (T) in.readSavable("Body", null);
                 Vector2 mjAnchor = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Anchor", new Vector2f()));
                 
                 PinJoint<T> mj = new PinJoint<>(mjBody, mjAnchor);
                 mj.setSpringFrequency(in.readDouble("SpringFrequency", 0));
                 mj.setSpringDampingRatio(in.readDouble("SpringDampingRatio", 0));
                 mj.setMaximumSpringForce(in.readDouble("MaximumSpringForce", 0));
                 mj.setTarget(Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Target", new Vector2f())));
                 
                 joint = (E) mj;
                break;
            case PrismaticJoint:
                T pjBody1 = (T) in.readSavable("Body1", null);
                T pjBody2 = (T) in.readSavable("Body2", null);
                
                Vector2 pjAnchor = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Anchor", new Vector2f())),
                        pjAxis   = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Axis", new Vector2f()));
                
                PrismaticJoint<T> pj = new PrismaticJoint<>(pjBody1, pjBody2, pjAnchor, pjAxis);
                pj.setLowerLimitEnabled(in.readBoolean("LowerLimitEnabled", false));
                pj.setUpperLimitEnabled(in.readBoolean("UpperLimitEnabled", false));
                pj.setLimits(in.readDouble("LowerLimi", 0), in.readDouble("UpperLimit", 0));
                pj.setReferenceAngle(in.readDouble("ReferenceAngle", 0));
                pj.setMotorEnabled(in.readBoolean("MotorEnabled", false));
                pj.setMotorSpeed(in.readDouble("MotorSpeed", 0));
                pj.setMaximumMotorForce(in.readDouble("MaximumMotorForce", 0));
                
                joint = (E) pj;
                break;
            case PulleyJoint:
                T pyjBody1 = (T) in.readSavable("Body1", null);
                T pyjBody2 = (T) in.readSavable("Body2", null);
                
                Vector2 pulleyAnchor1 = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("PulleyAnchor1", new Vector2f())),
                        pulleyAnchor2 = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("PulleyAnchor2", new Vector2f()));
                
                Vector2 pyjAnchor1 = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Anchor1", new Vector2f())),
                        pyjAnchor2 = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Anchor2", new Vector2f()));
                
                PulleyJoint<T> pyj = new PulleyJoint<>(pyjBody1, pyjBody2, pulleyAnchor1, pulleyAnchor2, pyjAnchor1, pyjAnchor2);
                pyj.setRatio(in.readDouble("Ratio", 0));
                
                joint = (E) pyj;
                break;
            case RevoluteJoint:
                T rjBody1 = (T) in.readSavable("Body1", null);
                T rjBody2 = (T) in.readSavable("Body2", null);
                
                Vector2 rjAnchor = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Anchor", new Vector2f()));
                
                RevoluteJoint<T> rj = new RevoluteJoint<>(rjBody1, rjBody2, rjAnchor);
                rj.setLimitsEnabled(in.readBoolean("LimitsEnabled", false));
                rj.setLimits(in.readDouble("LowerLimit", 0), in.readDouble("UpperLimit", 0));
                rj.setLimitsReferenceAngle(in.readDouble("LimitsReferenceAngle", 0));
                rj.setMotorEnabled(in.readBoolean("MotorEnabled", false));
                rj.setMotorSpeed(in.readDouble("MotorSpeed", 0));
                
                joint = (E) rj;
                break;
            case WeldJoint:
                T wjBody1 = (T) in.readSavable("Body1", null);
                T wjBody2 = (T) in.readSavable("Body2", null);
                
                Vector2 wjAnchor = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Anchor", new Vector2f()));
                
                WeldJoint<T> wj = new WeldJoint<>(wjBody1, wjBody2, wjAnchor);
                wj.setSpringFrequency(in.readDouble("SpringFrequency", 0));
                wj.setSpringDampingRatio(in.readDouble("SpringDampingRatio", 0));
                wj.setLimitsReferenceAngle(in.readDouble("LimitsReferenceAngle", 0));
                
                joint = (E) wj;
                break;
            case WheelJoint:
                T whjBody1 = (T) in.readSavable("Body1", null);
                T whjBody2 = (T) in.readSavable("Body2", null);
                
                Vector2 whjAnchor = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Anchor", new Vector2f())),
                        whjAxis   = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Axis", new Vector2f()));
                
                WheelJoint<T> whj = new WheelJoint<>(whjBody1, whjBody2, whjAnchor, whjAxis);
                whj.setSpringFrequency(in.readDouble("SpringFrequency", 0));
                whj.setSpringDampingRatio(in.readDouble("SpringDampingRatio", 0));
                whj.setMotorEnabled(in.readBoolean("MotorEnabled", false));
                whj.setMotorSpeed(in.readDouble("MotorSpeed", 0));
                whj.setMaximumMotorTorque(in.readDouble("MaximumMotorTorque", 0));
                
                joint = (E) whj;
                break;
            case MotorJoint:
                T mtjBody1 = (T) in.readSavable("Body1", null);
                T mtjBody2 = (T) in.readSavable("Body2", null);
                
                MotorJoint<T> mtj = new MotorJoint<>(mtjBody1, mtjBody2);
                mtj.setLinearTarget(Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("LinearTarget", new Vector2f())));
                mtj.setAngularTarget(in.readDouble("AngularTarget", 0));
                mtj.setCorrectionFactor(in.readDouble("CorrectionFactor", 0));
                mtj.setMaximumForce(in.readDouble("MaximumForce", 0));
                mtj.setMaximumTorque(in.readDouble("MaximumTorque", 0));
                
                joint = (E) mtj;
                break;
            case Custom:
                LOGGER.log(Level.WARNING, "Unknown joint class: {0}", joint.getClass().getName());
                break;
            default:
                throw new AssertionError();
        }
        
        if (type != Type.Custom) {
            joint.setCollisionAllowed(in.readBoolean("CollisionAllowed", false));
        }
    }
}
