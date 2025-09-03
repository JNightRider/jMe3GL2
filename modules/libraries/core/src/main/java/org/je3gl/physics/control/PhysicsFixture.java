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
package org.je3gl.physics.control;

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

import org.je3gl.physics.collision.CollisionShape;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.Filter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;

/**
 * Class in charge of encapsulating a <code>BodyFixture</code> in order to export 
 * and import it.
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class PhysicsFixture implements Savable, Cloneable {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(PhysicsFixture.class.getName());
    
    /**
     * An enumeration class where the different types of filters for physical
     * bodies are listed.
     */
    public static enum PhysicsFilter {
        
        /**  Default type (not exported). */
        DEFAULT,
        
        /**
         * A type {@link org.dyn4j.collision.CategoryFilter} filter.
         */
        CATEGOTY,
        
        /**
         * Custom filter (normally not supported by default).
         */
        CUSTOM;
        
        /**
         * Method responsible for determining the type of a given filter.
         * @param f the filter
         * @return type
         */
        public static PhysicsFilter valuOf(Filter f) {
            if (f == Filter.DEFAULT_FILTER) {
                return DEFAULT;
            } else if (f instanceof CategoryFilter) {
                return CATEGOTY;
            }
            return CUSTOM;
        }
    }
    
    /** An {@link org.dyn4j.dynamics.BodyFixture} of the physical body*/
    private BodyFixture bf;
    /** Physical filter type. */
    private PhysicsFilter type;

    /**
     * Constructor.
     */
    protected PhysicsFixture() {
    }

    /**
     * Generate an object of class <code>hysicsFixture</code>.
     * @param bf body-fixture
     */
    public PhysicsFixture(BodyFixture bf) {
        this.type = PhysicsFilter.valuOf(bf.getFilter());
        this.bf = bf;
    }
    
    /**
     * (non-Javadoc)
     * @see java.lang.Object#clone() 
     * @return this
     */
    @Override
    @SuppressWarnings("unchecked")
    public PhysicsFixture clone() {
        try {
            // To 'clone' this object, we must first encapsulate the body shape 
            // into an CollisionShape<?> where the copy will be made.
            PhysicsFixture clon = (PhysicsFixture) super.clone();            
            CollisionShape<?> temp = new CollisionShape<>(bf.getShape());
            
            clon.type = type;
            clon.bf   = new BodyFixture(temp.clone().getShape());
            
            // Whether it is possible to clone user data (except null values)
            Object userData = bf.getUserData();
            if ((userData instanceof JmeCloneable) || (userData instanceof Cloneable)) {
                Cloner cloner = new Cloner();
                userData = cloner.clone(userData);
            } else {
                if (userData != null) {
                    LOGGER.log(Level.INFO, "Could NOT clone {0} (probably a primitive type)", userData.getClass().getName());
                }
            }
            
            clon.bf.setUserData(userData);
            if (clon.type == PhysicsFilter.CATEGOTY) {
                CategoryFilter cf = (CategoryFilter) bf.getFilter();                
                clon.bf.setFilter(new CategoryFilter(cf.getCategory(), cf.getMask()));
            } else if (clon.type == PhysicsFilter.DEFAULT) {
                // nothing
            } else if (clon.type == PhysicsFilter.CUSTOM) {
                LOGGER.log(Level.WARNING, "The class {0} is not known.", bf.getFilter().getClass().getName());
            }
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    
    /**
     * Returns body shape.
     * @return convex
     */
    public Convex getShape() {
        return bf.getShape();
    }

    /**
     * Returns the encapsulated <code>BodyFixture</code>
     * @return body-fixture
     */
    public BodyFixture getFixture() {
        return bf;
    }

    /**
     * Returns the type of filter used.
     * @return type-filter
     */
    public PhysicsFilter getPhysicsFilter() {
        return type;
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#write(com.jme3.export.JmeExporter) 
     * 
     * @param ex {@link com.jme3.export.JmeExporter}
     * @throws IOException throws
     */
    @Override
    @SuppressWarnings("unchecked")
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);        
        Object userObject = bf.getUserData();
        byte userType = -1;
        if (userObject != null) {
            try {
                userType = UserData.getObjectType(userObject);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "[ UserData ] :Unsupported type: {0}", userObject.getClass().getName());
            }
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
        
        out.write(type, "TypeFilter", PhysicsFilter.CUSTOM);
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
        
        type = in.readEnum("TypeFilter", PhysicsFilter.class, PhysicsFilter.CUSTOM);
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
