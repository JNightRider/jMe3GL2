/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.j3gl.box2d.debug;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.control.BillboardControl;
import com.jme3.texture.Texture;
import org.je3gl.scene.debug.custom.DebugGraphics;

/**
 * Class responsible for implementing the {@link org.je3gl.scene.debug.custom.DebugGraphics} interface
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class Box2dDebugGraphics implements DebugGraphics {
    /** Resource manager <code>JME</code>. */
    private final AssetManager assetManager;

    /**
     * Constructor.
     * @param assetManager object
     */
    public Box2dDebugGraphics(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
 
    /* (non-Javadoc)
     * @see org.je3gl.scene.debug.custom.DebugGraphics#getBitmapFont(java.lang.String) 
     */
    @Override
    public BitmapFont getBitmapFont(String name) {
        if (name != null && name.startsWith("path://")) {
            return assetManager.loadFont(name.substring(7, name.length()));
        }
        return assetManager.loadFont("Interface/Fonts/Console.fnt");
    }

    /* (non-Javadoc)
     * @see org.je3gl.scene.debug.custom.DebugGraphics#createBitmapText(com.jme3.font.BitmapFont, java.lang.String) 
     */
    @Override
    public BitmapText createBitmapText(BitmapFont font, String value) {
        BitmapText text = font.createLabel(value);
        font.getPage(0).getTextureParam("ColorMap").getTextureValue().setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
        font.getPage(0).getTextureParam("ColorMap").getTextureValue().setMagFilter(Texture.MagFilter.Bilinear);
        text.setSize(2f);
        text.scale(0.12f);
        return text;
    }

    /*(non-Javadoc)
     * @see org.je3gl.scene.debug.custom.DebugGraphics#getColor(java.lang.String) 
     */
    @Override
    public ColorRGBA getColor(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
