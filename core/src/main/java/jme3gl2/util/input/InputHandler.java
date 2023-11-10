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
package jme3gl2.util.input;

import com.jme3.input.InputManager;
import java.util.List;

/**
 * Cualquier clase que implemente la interfaz <code>InputHandler</code> tendra
 * acceso a gestionar las entradas de datos del usuario.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * @since 2.0.0
 */
public interface InputHandler {
    
    /**
     * Método encargado de inicializar las entradas, es decir que establece
     * el administrador de las entradas.
     * @param im administrador-entradas.
     */
    public void initialize(InputManager im);
    
    /**
     * Determina si se ha inicializado el gestor de las entradas.
     * @return estado.
     */
    public boolean isInitialized();

    /**
     * Método encargado de instalar las entradas.
     */
    public void install();

    /**
     * Método encargado de desinstalar las entradas.
     */
    public void uninstall();

    /**
     * Determina el estado de las entradas.
     * @return estado lógico.
     */
    public boolean isEnabled();

    /**
     * Método encargado de establecer el estado de las entradas.
     * @param flag {@code true} si se habilita las entradas, de lo contrario
     *              {@code false} si se deshabilita.
     */
    public void setEnabled(boolean flag);

    /**
     * Método encargado de gestionar las entradas para determina su estado.
     * @return {@code true} si se a activado la entrada, {@code false} si no
     *          a habido cambios.
     */
    public boolean isActive();

    /**
     * Gestiona una lista de dependencia de comportamineto.
     * @return lista de dependencias.
     */
    public List<InputHandler> getDependentBehaviors();

    /**
     * Encargado de devolver el estado de las dependencia de esta entrada.
     * @return estado.
     */
    public boolean isDependentBehaviorActive();

    /**
     * Método encargado de determinar si las dependencias son aditivas o no.
     * @return estado.
     */
    public boolean isDependentBehaviorsAdditive();

    /**
     * Establece si las dependencias de esta entrada son aditivas.
     * @param flag estado.
     */
    public void setDependentBehaviorsAdditive(boolean flag);
}
