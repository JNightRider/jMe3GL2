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
package jMe3GL2.util;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

/**
 * Clase encargado de proporcionar métodos de utilidad para la creación
 * de materiales y texturas 2D.
 * 
 * @version 1.0-SNAPSHOT
 * @since 1.0.0
 */
public final 
class jMe3GL2Utils {
    
    /**
     * Constructor privado de la clase.
     */
    private jMe3GL2Utils() {}
    
    /**
     * Método local para reutilizar la carga de una textura como material
     * sin sombrear.
     * 
     * @param assetManager administrador de recursos {@code jme3}.
     * @param texture nombre de la textura a usar.
     * @return material generado.
     */
    public static Material loadMaterial(AssetManager assetManager, String texture) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture(new TextureKey(texture, false));
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return mat;
    }

    /**
     * Método local en donde genera un material con un color.
     * 
     * @param assetManager administrador de recursos {@code jme3}.
     * @param colorRGBA color del material.
     * @return material generado.
     */
    public static Material loadMaterial(AssetManager assetManager, ColorRGBA colorRGBA) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", colorRGBA);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return mat;
    }

    /**
     * Carga una textura sin sombrear.
     * 
     * @param assetManager administrador de recursos {@code jme3}.
     * @param file nombre de la textura a cargar.
     * @return textura cargado.
     */
    public static Texture loadTexture(AssetManager assetManager, String file) {
        Texture tex = assetManager.loadTexture(new TextureKey(file, false));
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setWrap(Texture.WrapMode.Repeat);
        return tex;
    }
}
