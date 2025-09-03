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
package org.je3gl.demo.core;

import com.jme3.app.SimpleApplication;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;

import org.je3gl.scene.shape.Sprite;
import org.je3gl.utilities.MaterialUtilities;

/**
 * Test class for 2D models (Sprite).
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class Sprite2D extends SimpleApplication {

    /**
     * The main method; uses zero arguments in args array
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Sprite2D app = new Sprite2D();
        app.start();
    }
    
    /* (non-Javadoc)
     * @see com.jme3.app.SimpleApplication#simpleInitApp() 
     */
    @Override
    public void simpleInitApp() {        
        Sprite mesh    = new Sprite(1.0F, 1.0F);        
        Geometry model = new Geometry("Model", mesh);
        
        model.setMaterial(MaterialUtilities.getUnshadedMaterialFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"));
        model.setQueueBucket(RenderQueue.Bucket.Transparent);        
        rootNode.attachChild(model);
        
        // Generates the texture on an integrated image set.
        Sprite friendMesh    = new Sprite(1.0F, 1.0F, 10, 6, 5, 4, true, false);
        Geometry friendModel = new Geometry("friend", friendMesh);
        
        Material mat = MaterialUtilities.getLightingMaterialFromClassPath(assetManager, "Textures/Tilemap_Packed_Rabbit.png");
        mat.setColor("Ambient", ColorRGBA.White);
        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.Orange);
        mat.setFloat("Shininess", 64f);
        
        friendModel.setMaterial(mat);
        friendModel.setQueueBucket(RenderQueue.Bucket.Transparent);
        friendModel.move(2, 0, 0);        
        rootNode.attachChild(friendModel);
        
        // Must add a light to make the lit object visible!
        PointLight aLight = new PointLight();
        aLight.setRadius(1.5F);
        
        aLight.setPosition(new Vector3f(2.25F, 0, 0.1F));
        aLight.setColor(ColorRGBA.White);
        rootNode.addLight(aLight);
    }
}
