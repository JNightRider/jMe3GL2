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

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.scene.UserData;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jme3gl2.physics.collision.CollisionShape;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.Filter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;

/**
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class PhysicsFixture implements Savable, Cloneable {

    private static final Logger LOGGER = Logger.getLogger(PhysicsFixture.class.getName());
    
    public static enum Type{
        DEFAULT,
        
        CATEGOTY,
        
        CUSTOM;
        
        public static Type valuOf(Filter f) {
            if (f == Filter.DEFAULT_FILTER) {
                return DEFAULT;
            } else if (f instanceof CategoryFilter) {
                return CATEGOTY;
            }
            return CUSTOM;
        }
    }
    
    private BodyFixture bf;
    private Type type;

    protected PhysicsFixture() {
    }

    public PhysicsFixture(BodyFixture bf) {
        this.type = Type.valuOf(bf.getFilter());
        this.bf = bf;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public PhysicsFixture clone() {
        try {
            PhysicsFixture clon = (PhysicsFixture) super.clone();            
            CollisionShape<?> temp = new CollisionShape<>(bf.getShape());
            
            clon.type = type;
            clon.bf   = new BodyFixture(temp.clone().getShape());
            
            Object userData = bf.getUserData();
            if ((userData instanceof JmeCloneable) || (userData instanceof Cloneable)) {
                Cloner cloner = new Cloner();
                userData = cloner.clone(userData);
            } else {
                LOGGER.log(Level.INFO, "Could NOT clone {0} (probably a primitive type)", userData != null ? userData.getClass().getName() : null);
            }
            
            clon.bf.setUserData(userData);
            if (clon.type == Type.CATEGOTY) {
                CategoryFilter cf = (CategoryFilter) bf.getFilter();                
                clon.bf.setFilter(new CategoryFilter(cf.getCategory(), cf.getMask()));
            } else if (clon.type == Type.DEFAULT) {
                // nothing
            } else if (clon.type == Type.CUSTOM) {
                LOGGER.log(Level.WARNING, "The class {0} is not known.", bf.getFilter().getClass().getName());
            }
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public Convex getShape() {
        return bf.getShape();
    }

    public BodyFixture getFixture() {
        return bf;
    }

    public Type getTypeFilter() {
        return type;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);        
        Object userObject = bf.getUserData();
        byte userType = -1;
        try {
            userType = UserData.getObjectType(userObject);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unsupported type: {0}", userObject == null ? null : userObject.getClass().getName());
        }
        
        if (userType != -1) {
            out.write(new UserData(userType, userObject), "UserData", null);
        }
        
        out.write(new CollisionShape<>(bf.getShape()), "Shape", null);
        
        if (bf.isSensor()) {
            out.write(bf.isSensor(), "Sensor", false);
        } // by default fixtures are not sensors
        if (bf.getDensity() != BodyFixture.DEFAULT_DENSITY) {
            out.write(bf.getDensity(), "Density", 0);
        }
        if (bf.getFriction() != BodyFixture.DEFAULT_FRICTION) {
            out.write(bf.getFriction(), "Friction", 0);
        }
        if (bf.getRestitution() != BodyFixture.DEFAULT_RESTITUTION) {
            out.write(bf.getRestitution(), "Restitution", 0);
        }
        if (bf.getRestitutionVelocity() != BodyFixture.DEFAULT_RESTITUTION_VELOCITY) {
            out.write(bf.getRestitutionVelocity(), "RestitutionVelocity", 0);
        }
        
        out.write(type, "TypeFilter", Type.CUSTOM);
        switch (type) {
            case DEFAULT:
                // output nothing
                break;
            case CATEGOTY:
                CategoryFilter cf = (CategoryFilter) bf.getFilter();
                out.write(cf.getCategory(), "CategoryFilter#Category", 0);
                out.write(cf.getMask(), "CategoryFilter#Mask", 0);
                break;
            case CUSTOM:
                LOGGER.log(Level.WARNING, "The class {0} is not known.", bf.getFilter().getClass().getName());
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        
        UserData userData = (UserData) in.readSavable("UserData", null);
        Object userObject = null;
        if (userData != null) {
            userObject  = userData.getValue();
        }
        
        CollisionShape<?> shape = (CollisionShape<?>) in.readSavable("Shape", null);
        
        bf = new BodyFixture(shape.getShape());
        bf.setUserData(userObject);
        
        bf.setSensor(in.readBoolean("Sensor", false));
        bf.setDensity(in.readDouble("Density", BodyFixture.DEFAULT_DENSITY));
        bf.setFriction(in.readDouble("Friction", BodyFixture.DEFAULT_FRICTION));
        bf.setRestitution(in.readDouble("Restitution", BodyFixture.DEFAULT_RESTITUTION));
        bf.setRestitutionVelocity(in.readDouble("RestitutionVelocity", BodyFixture.DEFAULT_RESTITUTION_VELOCITY));
        
        type = in.readEnum("TypeFilter", Type.class, Type.CUSTOM);
        switch (type) {
            case DEFAULT:
                // input nothing
                break;
            case CATEGOTY:
                long category = in.readLong("CategoryFilter#Category", 0),
                     mask     = in.readLong("CategoryFilter#Mask", 0);
                
                CategoryFilter cf = new CategoryFilter(category, mask);
                bf.setFilter(cf);
                break;
            case CUSTOM:
                LOGGER.log(Level.WARNING, "The class {0} is not known.", bf.getFilter().getClass().getName());
                break;
            default:
                throw new AssertionError();
        }
    }
}
