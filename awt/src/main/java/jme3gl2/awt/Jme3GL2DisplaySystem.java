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
package jme3gl2.awt;

/**
 * Clase encargado de gestionar la resolución de pantalla que utilizara
 * el juego. En caso de que se este ejecutando en un PC se debe obtener
 * las dimensiones de dciha pantalla con la ayuda de las bibliotecas {@code AWT}.
 * 
 * De lo contrario se debe buscar la forma de obtener dichas resoluciones.
 * 
 * @author wil
 * @version 1.1-SNAPSHOT
 * 
 * @since 2.0.0
 */
public interface Jme3GL2DisplaySystem {
    
    /**
     * Devuelve un arreglo de resoluciones a utilizar.
     * 
     * @return {@link AWTResolution}.
     */
    public AWTResolution[] getResolutions();
    
    /**
     * Genera la resolucion de la pantalla completa, dicha resolucion será
     * de la pantalla en donde se está corriendo el juego.
     * 
     * @return {@link AWTResolution}.
     */
    public AWTResolution getFullScreenResolution();
    
    /**
     * Método encargado de determinar si la pantalla completa es soportada 
     * por el equipo.
     * @return {@code true} si es pantalla completa, de lo contrario
     *          será {@code false}.
     */
    public boolean isFullScreenSupported();
    
    /**
     * Método encargado de determinar si se admite cambios de visualización.
     * @return estado.
     */
    public boolean isDisplayChangeSupported();
}
