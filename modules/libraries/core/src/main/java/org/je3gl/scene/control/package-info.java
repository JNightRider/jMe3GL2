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

/**
 * Package responsible for managing animation control for 2D models, this package 
 * offers 3 animated controls that can be used to meet the demands of a full 2D game.
 * <p>
 * List of currently supported animated controls:
 * <ul>
 * <li><b>AnimatedSprite2D</b>: Animated control where you can generate an animation 
 * through various textures.
 * </li>
 * <li><b>AnimatedSingleSprite2D</b>: Animated control responsible for generating 
 * an animation by going through the list of images that contain the texture.
 * </li>
 * <li><b>AnimatedRibbonBoxSprite2D</b>:A composite animated control, this control 
 * combines <b>AnimatedSprite2D</b> with <b>AnimatedSingleSprite2D</b> generating 
 * complex animations that require multiple textures, each of which provides 
 * an animation.
 * </li>
 * </ul>
 * 
 * @author wil
 * @version 2.0.0
 * @since 1.5.0
 */
package org.je3gl.scene.control;
