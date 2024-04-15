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
package org.jegl.utilities;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.texture.Texture;

/**
 * Utility class for loading, managing and modifying textures.
 * @author wil
 * @version 1.0.5
 * @since 2.5.0
 */
public final class TextureUtilities {
    
    /**
     * Returns a texture stored in the classpath given the path.
     *
     * @param assetManager asset manager
     * @param path the path within the classpath
     * @return texture
     */
    public static final Texture getTextureFromClassPath(AssetManager assetManager, String path){
        Texture tex = assetManager.loadTexture(new TextureKey(path, false));
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setWrap(Texture.WrapMode.Repeat);
        return tex;
    }
    
    /**
     * Returns an array of textures stored in the classpath given the path
     * 
     * @param assetManager asset manager
     * @param paths the path within the classpath
     * @return texture array
     */
    public static final Texture[] getArrayTextureFromClassPath(AssetManager assetManager, String ...paths) {
        if (paths == null) {
            throw new NullPointerException("The paths are not valid: null");
        }
        Texture[] texts = new Texture[paths.length];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = getTextureFromClassPath(assetManager, paths[i]);
        }
        return texts;
    }
}
