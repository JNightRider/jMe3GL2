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
package org.je3gl.plugins;

import com.jme3.app.Application;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.export.Savable;
import com.jme3.export.binary.BinaryImporter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.je3gl.plugins.Debugger.*;

/**
 * A small asset loader.
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class J2OLoader implements AssetLoader {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(J2OLoader.class.getName());
    
    /** Initialization flag. */
    private static boolean J2O_INIT = false;
    
    /**
     *Register the loader of the assets with extension: ".j2o" | ".J2O"
     * @param app application
     */
    public static void initialize(Application app) {
        if (!J2OLoader.J2O_INIT) {
            J2OLoader.J2O_INIT = true;
            app.getAssetManager().registerLoader(J2OLoader.class, "j2o", "J2O");
            LOGGER.log(Level.INFO, "Registered J2OLoader: {0}", J2OLoader.class);
        } else {
            LOGGER.log(Level.WARNING, "Asset Loader {0} is now up and running (registered)", J2OLoader.class);
        }
    }

    /* (non-Javadoc)
     * @see com.jme3.asset.AssetLoader#load(com.jme3.asset.AssetInfo) 
     */
    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        AssetKey<?> key = assetInfo.getKey();        
        if ("j2o".equals(key.getExtension())  || "J2O".equals(key.getExtension())) {
            BinaryImporter importer = BinaryImporter.getInstance();
            importer.setAssetManager(assetInfo.getManager());
            
            try {             
                Savable obj = importer.load(assetInfo.openStream());
                return obj;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        apiGLLog("Extension " + key.getExtension() + " is not supported");
        return null;
    }
}
