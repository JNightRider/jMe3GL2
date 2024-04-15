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
package org.jegl.demo.core;

import com.jme3.app.SimpleApplication;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture;

import org.jegl.scene.control.AnimatedRibbonBoxSprite2D;
import org.jegl.scene.control.AnimatedSingleSprite2D;
import org.jegl.scene.control.AnimatedSprite2D;
import org.jegl.scene.control.RibbonBoxAnimation2D;
import org.jegl.scene.shape.Sprite;
import static org.jegl.utilities.MaterialUtilities.*;
import static org.jegl.utilities.TextureUtilities.*;

/**
 * Test class where the 3 methods to generate animations are exemplified.
 * @author wil
 * @version 1.0.0
 * @since 3.0.0
 */
public class Animations2D extends SimpleApplication {
    
    /**
     * The main method; uses zero arguments in args array
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Animations2D app = new Animations2D();
        app.start();
    }

    /*
     * (non-Javadoc)
     * @see com.jme3.app.SimpleApplication#simpleInitApp() 
     */
    @Override
    public void simpleInitApp() {
        //----------------------------------------------------------------------
        //                   A separate texture animation.
        //----------------------------------------------------------------------
        Geometry player = new Geometry("AnimatedSprite2D", new Sprite(1.0F, 1.0F));
        player.setMaterial(getUnshadedMaterialFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"));
        player.setQueueBucket(RenderQueue.Bucket.Transparent);
        player.move(-2, 0, 0);
        
        // Generate the animation...
        AnimatedSprite2D animatedSprite2D = new AnimatedSprite2D();
        animatedSprite2D.addAnimation("idle", new Texture[] {
            getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"),
            getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0042.png")
        });
        animatedSprite2D.addAnimation("walk", new Texture[] {
            getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"),
            getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0041.png")
        });
        
        player.addControl(animatedSprite2D);
        animatedSprite2D.playAnimation("walk", 10);
        rootNode.attachChild(player);
        
        //----------------------------------------------------------------------
        //                 An animation integrated into an image.
        //----------------------------------------------------------------------
        Geometry bee = new Geometry("", new Sprite(1.0F, 1.0F, 10, 6, 1, 5));        
        Material mat   = getLightingMaterialFromClassPath(assetManager, "Textures/Tilemap_Packed_Rabbit.png");
        
        mat.setColor("Ambient", ColorRGBA.White);
        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.Orange);
        mat.setFloat("Shininess", 64f);
        
        bee.setMaterial(mat);
        bee.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        // Generate the animation...
        AnimatedSingleSprite2D animatedSingleSprite2D = new AnimatedSingleSprite2D(true);
        animatedSingleSprite2D.addAnimation("idle", new int[] {51});
        animatedSingleSprite2D.addAnimation("fly", new int[] {51, 52});

        bee.addControl(animatedSingleSprite2D);
        animatedSingleSprite2D.playAnimation("fly", 8);
        rootNode.attachChild(bee);
        
        // Must add a light to make the lit object visible!
        PointLight aLight = new PointLight();
        aLight.setRadius(1.5F);
        
        aLight.setPosition(new Vector3f(0F, 0, 0.1F));
        aLight.setColor(ColorRGBA.White);
        rootNode.addLight(aLight);
        
        //----------------------------------------------------------------------
        //              A complex and complete animation (combinational).
        //----------------------------------------------------------------------
        Geometry complex = new Geometry("AnimatedRibbonBoxSprite2D", new Sprite(1.0F, 1.0F));
        complex.setMaterial(getUnshadedMaterialFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"));
        complex.setQueueBucket(RenderQueue.Bucket.Transparent);
        complex.move(2, 0, 0);
        
        // Generate the animation...
        AnimatedRibbonBoxSprite2D animatedRibbonBoxSprite2D = new AnimatedRibbonBoxSprite2D(false);
        animatedRibbonBoxSprite2D.addAnimation("start", new RibbonBoxAnimation2D[] {
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"), new int[] {0}, 1, 1),
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0042.png"), new int[] {0}, 1, 1),
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"), new int[] {0}, 1, 1),
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0042.png"), new int[] {0}, 1, 1),
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"), new int[] {0}, 1, 1),
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0042.png"), new int[] {0}, 1, 1),
            
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Tilemap_Packed_Rabbit.png"), new int[] {
               45, 46, 45, 46, 45, 46, 45, 46, 45, 46, 45, 46,
            }, 10, 6),
            
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"), new int[] {0}, 1, 1),
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0042.png"), new int[] {0}, 1, 1),
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"), new int[] {0}, 1, 1),
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0042.png"), new int[] {0}, 1, 1),
            
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"), new int[] {0}, 1, 1),
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0041.png"), new int[] {0}, 1, 1),
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"), new int[] {0}, 1, 1),
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0041.png"), new int[] {0}, 1, 1),
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0040.png"), new int[] {0}, 1, 1),
            new RibbonBoxAnimation2D(getTextureFromClassPath(assetManager, "Textures/Rabbit/tile_0041.png"), new int[] {0}, 1, 1)
        });        
        
        complex.addControl(animatedRibbonBoxSprite2D);
        animatedRibbonBoxSprite2D.playAnimation("start", 15);
        
        rootNode.attachChild(complex);
    }
}
