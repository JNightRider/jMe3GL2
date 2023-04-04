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
package jMe3GL2.renderer;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Una <code>TargetCamera</code> es una clase encargada de gestionar las posiciones
 * del objetivo a seguir por la cámara 2D.
 *
 * @param <E> tipo de objetivo.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 1.0
 */
public class TargetCamera<E extends Spatial> {

    /** Coordinates of the last target. */
    private Vector3f translation 
                     = new Vector3f();
    
    /**  Objetivo de la cámara.  */
    private E target = null;

    /**
     * Constructor predeterminado.
     */
    public TargetCamera() {
    }
    
    /**
     * Genera un objeti de la camara utilizando la 
     * clase <code>TargetCamera</code>.
     * 
     * @param target Objetivo.
     */
    public TargetCamera(E target) {
        this.target = target;
    }

    /**
     * Limpia el objetivo.
     */
    public void clear() {
        this.target = null;
        this.translation.set(0, 0, 0);
    }

    /**
     * Establece un nuevo objeto a seguir.
     * @param target nuevo objetivo.
     */
    public void setTarget(E target) {
        if (target == null) {
            if (this.target != null) {
                this.translation = this.target.getLocalTranslation().clone();
            }
        }
        this.target = target;
    }

    /**
     * Devuele el objetivo actual de la cámara.
     * @return objetivo cámara.
     */
    public E getTarget() {
        return target;
    }
    
    /**
     * Método encargado de devolver la posicion del objetio o del ultimo
     * si es que tuvo.
     * @return posicion del objetivo.
     */
    public Vector3f getTranslation() {
        if (target == null) {
            return translation;
        }
        return target.getLocalTranslation();
    }
}
