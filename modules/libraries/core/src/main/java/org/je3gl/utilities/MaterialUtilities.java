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
package org.je3gl.utilities;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

/**
 * Utility class for loading, managing and modifying materials.
 * @author wil
 * @version 1.0.5
 * @since 2.5.0
 */
public final class MaterialUtilities {
    
    /**
     * Minimum value of the <code>AlphaDiscardThreshold</code> of materials that
     * support brightness.
     */
    public static final float MIN_ALPHA_DISCARD_THRESHOLD = 0.0001F;
    
    /**
     * Returns a <code>Material</code> stored in the classpath given the path.
     * 
     * @param assetManager asset manager
     * @param color material color
     * @return material
     */
    public static final Material getUnshadedColorMaterialFromClassPath(AssetManager assetManager, ColorRGBA color) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return mat;
    }
    
    /**
     * Returns a <code>Material</code> stored in the classpath given the path
     * with an added texture.
     * 
     * @param assetManager asset manager
     * @param path the path within the classpath
     * @return material
     */
    public static final Material getUnshadedMaterialFromClassPath(AssetManager assetManager, String path) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture(new TextureKey(path, false));
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return mat;
    }
    
    /**
     * Returns a <code>Material</code> stored in the classpath given the path
     * with an added texture that shines on it.
     * 
     * @param assetManager asset manager
     * @param path1 the texture path within the classpath
     * @param path2 the texture path for the gloss within the classpath
     * @return material
     */
    public static final Material getUnshadedMaterialSupportsGlowFromClassPath(AssetManager assetManager, String path1, String path2) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture(new TextureKey(path1, false));
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setWrap(Texture.WrapMode.Repeat);
        
        Texture glowtex = assetManager.loadTexture(new TextureKey(path2, false));
        glowtex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        glowtex.setMagFilter(Texture.MagFilter.Nearest);
        glowtex.setWrap(Texture.WrapMode.Repeat);
        
        mat.setTexture("ColorMap", tex);
        mat.setTexture("GlowMap", glowtex);        
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setFloat("AlphaDiscardThreshold", MIN_ALPHA_DISCARD_THRESHOLD);
        return mat;
    }
    
    /**
     * Returns a <code>Material</code> stored in the classpath given the path
     * with an added texture.
     * 
     * @param assetManager asset manager
     * @param path the path within the classpath
     * @return material
     */
    public static final Material getLightingMaterialFromClassPath(AssetManager assetManager, String path) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture tex = assetManager.loadTexture(new TextureKey(path, false));
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setWrap(Texture.WrapMode.Repeat);
        
        mat.setTexture("DiffuseMap", tex);
        mat.setFloat("Shininess", 32f);
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", new ColorRGBA(0, 0, 0, 1));
        mat.setColor("Diffuse", new ColorRGBA(1, 1, 1, 1));
        mat.setColor("Specular", new ColorRGBA(1, 1, 1, 1));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return mat;
    }
    
    /**
     * Returns a <code>Material</code> stored in the classpath given the path
     * with an added texture that shines on it.
     * 
     * @param assetManager asset manager
     * @param path1 the texture path within the classpath
     * @param path2 the texture path for the gloss within the classpath
     * @return material
     */
    public static final Material getLightingMaterialSupportsGlowFromClassPath(AssetManager assetManager, String path1, String path2) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture tex = assetManager.loadTexture(new TextureKey(path1, false));
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setWrap(Texture.WrapMode.Repeat);
        
        Texture glowtex = assetManager.loadTexture(new TextureKey(path2, false));
        glowtex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        glowtex.setMagFilter(Texture.MagFilter.Nearest);
        glowtex.setWrap(Texture.WrapMode.Repeat);
        
        mat.setTexture("DiffuseMap", tex);
        mat.setTexture("GlowMap", glowtex);        
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setFloat("AlphaDiscardThreshold", MIN_ALPHA_DISCARD_THRESHOLD);
        return mat;
    }
}
