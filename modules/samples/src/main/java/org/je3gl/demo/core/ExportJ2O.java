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
package org.je3gl.demo.core;

import com.jme3.app.SimpleApplication;
import com.jme3.export.Savable;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture;
import java.io.File;
import java.io.IOException;
import org.je3gl.scene.control.AnimatedSprite2D;
import org.je3gl.scene.shape.Sprite;
import static org.je3gl.utilities.MaterialUtilities.*;
import static org.je3gl.utilities.TextureUtilities.*;

/**
 *
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class ExportJ2O extends SimpleApplication {
    
    private static final String ROOT_PATH ;    
    static {
        ROOT_PATH = System.getProperty("user.dir") + "/src/main/resources/Models/";
    }
    
    public static void main(String[] args) {
        ExportJ2O app = new ExportJ2O();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Geometry player = new Geometry("AnimatedSprite2D", new Sprite(1.0F, 1.0F));
        player.setMaterial(getUnshadedMaterialFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"));
        player.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        // Generate the animation...
        AnimatedSprite2D animatedSprite2D = new AnimatedSprite2D();
        animatedSprite2D.addAnimation("idle", new Texture[] {
            getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png")
        });
        animatedSprite2D.addAnimation("walk", new Texture[] {
            getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"),
            getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0041.png")
        });
        animatedSprite2D.addAnimation("jump", new Texture[] {
            getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0041.png")
        });
        
        player.addControl(animatedSprite2D);
        animatedSprite2D.playAnimation("walk", 10);
        rootNode.attachChild(player);
        
        export(player, "Rabbit.j2o");
    }
    
    private static void export(Savable obj, String name) {
        try {
            BinaryExporter exporter = BinaryExporter.getInstance();
            exporter.save(obj, new File(ROOT_PATH, name));
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
