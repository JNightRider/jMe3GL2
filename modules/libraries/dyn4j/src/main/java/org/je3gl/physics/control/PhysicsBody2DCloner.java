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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to be able to clone dyn4j physical bodies, this class provides 
 * methods to create a new instance of a class used by physical bodies.
 * 
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
final class PhysicsBody2DCloner {
    
    /**
     * Method responsible for creating a new instance from a class ({@link java.lang.Class}).
     * @param <T> object type
     * @param clazz object class (original)
     * @return new instance
     */
    @SuppressWarnings("unchecked")
    public static <T extends PhysicsBody2D> T clone(Class<T> clazz) {
        try {
            return clazz.cast(newInstranceNoArgConstructor(clazz.getName()).newInstance());
        } catch (InvocationTargetException | InstantiationException e) {
            Logger.getLogger(PhysicsBody2DCloner.class.getName()).log(
                    Level.SEVERE, "Could not access constructor of class ''{0}" + "''! \n"
                    + "Some types need to have the BinaryImporter set up in a special way. Please double-check the setup.", clazz);
        } catch (IllegalAccessException e) {
            Logger.getLogger(PhysicsBody2DCloner.class.getName()).log(
                    Level.SEVERE, "{0} \n"
                    + "Some types need to have the BinaryImporter set up in a special way. Please double-check the setup.", e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PhysicsBody2DCloner.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new RuntimeException("The class " + clazz + " does not support constructors without arguments.");
    }
    
    /**
     * Use reflection to gain access to the no-arg constructor of the named
     * class.
     *
     * @return the pre-existing constructor (not null)
     */
    @SuppressWarnings("unchecked")
    private static Constructor newInstranceNoArgConstructor(String className) throws ClassNotFoundException, InstantiationException {
        Class<?> clazz = Class.forName(className);
        Constructor result;
        try {
            result = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new InstantiationException("Loading requires a no-arg constructor, but class " + className + " lacks one.");
        }
        return result;
    }
}
