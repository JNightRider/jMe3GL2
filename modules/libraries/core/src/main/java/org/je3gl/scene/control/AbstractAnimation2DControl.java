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
package org.je3gl.scene.control;

import com.jme3.export.*;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import org.je3gl.listener.AnimationChangeListener;
import org.je3gl.listener.AnimationTimeChangeListener;
import org.je3gl.listener.AnimationEvent;

import java.io.IOException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class in charge of managing the animation system for a 2D model that 
 * is implemented with <b>jMe3GL2</b>, this class supports binary export to save it as
 * a file for later use or loading.
 * <p>
 * To apply a 2D animation (animated sprite change) you have to manage the model 
 * material (as it exists in several 'custom types') differently, this is achieved 
 * through the {@link org.je3gl.scene.control.AnimatedMaterialsHandlerFunction} interface 
 * which provides the necessary methods to perform said action.
 * <p>
 * <b>jMe3GL2</b> supports two types of animated managers by default:
 * <ul>
 * <li>
 * <b>{@link org.je3gl.scene.control.UnshadedHandlerFunction}</b>: For materials of type "Common/MatDefs/Misc/Unshaded.j3md"
 * </li>
 * <li>
 * <b>{@link org.je3gl.scene.control.LightingHandlerFunction}</b>: For materials of type "Common/MatDefs/Light/Lighting.j3md"
 * </li>
 * </ul>
 * @author wil
 * @version 2.0.0
 * @since 1.0.0
 * @param <O> the type of model
 * @param <A> the type of animation
 * @param <E> the type of animated control
 */
public abstract class AbstractAnimation2DControl<O extends Spatial, A extends Animation2D, E extends AbstractAnimation2DControl<O, A, E>> extends AbstractControl implements Savable {

    /** Class logger. */
    private static final Logger LOG = Logger.getLogger(AbstractAnimation2DControl.class.getName());
    
    /** Version of this class (when exported and imported). */
    public static final int SAVABLE_VERSION = 1;
    
    /**
     * Animated control type, used to identify whether the control is one of those
     * supported by <b>jMe3GL2</b> or one customized by the user.
     */
    public static enum Type {
        /**
         * Animated control type that references {@link org.je3gl.scene.control.AnimatedSprite2D}.
         */
        Sprite,
        /**
         * Animated control type that references {@link org.je3gl.scene.control.AnimatedSingleSprite2D}.
         */
        Single,
        /**
         * Animated control type that references {@link org.je3gl.scene.control.AnimatedRibbonBoxSprite2D}.
         */
        RibbonBox,
        /**
         * Animated control type that references a custom animated control.
         */
        Custom;
    }
    
    /**
     * Object in charge of managing the animations, that is, applying them to the
     * 2D model that is being managed in this control.
     */
    protected AnimatedMaterialsHandlerFunction<O, A, E> handlerFunction;
    
    /** List of listeners responsible for notifying the change of an animation (sprite). */
    protected final List<AnimationChangeListener<O, A, E>> changeListeners;
    
    /** 
     * List of listeners responsible for reporting the progress of changing an 
     * animation (sprite). 
     */
    protected final List<AnimationTimeChangeListener<O, A, E>> timeChangeListeners;
    
    /** Map where all the animations of the 2D model are recorded. */
    protected final Map<String, A[]> animations;

    /** Current animation name. */
    protected String currentNameAnimation2D;
    /** Current animation. */
    protected A[] currentAnimation2D;
    
    /**
     * Current index of the animation, this index is used to change the "sprite" 
     * (may be another action depending on the animated control)
     */
    protected int animation2DIndex;
    
    /**
     * {@code true} if the animation is infinite (loop); otherwise its value will 
     * be {@code false} and the animation will only run once unless it is started again.
     */
    private boolean animation2Dloop;
    
    /** Current progress (time) to trigger the next frame of the active animation. */
    protected float elapsedeTime;
    /** Speed ​​at which animation frames are applied. */
    protected float animation2DSpeed;
    
    /** 
     * The duration of the animation (maximum), this factor depends on the set speed level. 
     * @see jme3gl2.scene.control.AbstractAnimation2DControl#animation2DSpeed
     */
    protected float animationFrameTime;

    /**
     * Default constructor of class <code>AbstractAnimation2DControl</code>.
     */
    public AbstractAnimation2DControl() {
        this(false);
    }
    
    /**
     * Generate a new instance of the <code>AbstractAnimation2DControl</code> class 
     * where you can specify the type of animation renderer to use from among those
     * supported by default by jMe3GL2.
     * <p>
     * Supported list
     * <ul>
     * <li>{@link org.je3gl.scene.control.UnshadedHandlerFunction}</li>
     * <li>{@link org.je3gl.scene.control.LightingHandlerFunction}</li>
     * </ul>
     * 
     * @param illuminated <code>true</code> if {@link org.je3gl.scene.control.LightingHandlerFunction} 
     * representation is used; otherwise <code>false</code> if {@link org.je3gl.scene.control.UnshadedHandlerFunction} is used.
     */
    public AbstractAnimation2DControl(boolean illuminated) {
        this(illuminated ? new LightingHandlerFunction<>() : new UnshadedHandlerFunction<>());
    }
    
    /**
     * Generate a new instance of <code>AbstractAnimation2DControl</code> where 
     * a custom animation handler can be specified.
     * 
     * @param handlerFunction A custom {@link org.je3gl.scene.control.AnimatedMaterialsHandlerFunction} object
     */
    public AbstractAnimation2DControl(AnimatedMaterialsHandlerFunction<O, A, E> handlerFunction) {
        if (handlerFunction == null) {
            throw new IllegalArgumentException("There needs to be a control function for animations.");
        }        
        this.animations          = new HashMap<>(1);
        this.changeListeners     = new ArrayList<>(0);
        this.timeChangeListeners = new ArrayList<>(0);
        this.handlerFunction  = handlerFunction;
        this.animation2Dloop  = true;
        this.animation2DSpeed = 60F;
    }
    
    /**
     * Returns the name of the current animation, <code>null</code> if none are active.
     * @return string
     */
    public String getCurrentAnimationName() {
        return currentNameAnimation2D;
    }

    /**
     * Returns a <code>Set&#60;&#62;</code> with the names of the registered animations.
     * @return object
     */
    public Set<String> getAnimations() {
        return animations.keySet();
    }
    
    /**
     * Returns <code>true</code> if the animation is infinite; otherwise<code>false</code>.
     * @return boolean
     */
    public boolean isAnimationLoop() {
        return animation2Dloop;
    }

    /**
     * Sets the state of this animation control.
     * @param animation2Dloop boolean
     */
    public void setAnimationLoop(boolean animation2Dloop) {
        this.animation2Dloop = animation2Dloop;
    }

    /**
     * Returns the speed of the animation.
     * @return float
     */
    public float getAnimationSpeed() {
        return animation2DSpeed;
    }

    /**
     * Set a new speed for this animation.
     * @param animation2DSpeed float
     */
    public void setAnimationSpeed(float animation2DSpeed) {
        if (animation2DSpeed <= 0) {
            throw new IllegalArgumentException("Invalid speed.");
        }
        this.animation2DSpeed = animation2DSpeed;
    }
    
    /**
     * Helper method to get the number of animations per frame.
     * @return int
     */
    protected final int getQuantity() {
        return currentAnimation2D != null ? currentAnimation2D.length: 0;
    }
    
    /**
     * Helper method to get a frame of the current animation.
     * @param index int
     * @return object
     */
    protected final A getCurrentAnimation(int index) {
        if (currentAnimation2D != null) {
            return currentAnimation2D[index];
        }
        return null;
    }

    /**
     * Returns the type of the animation control.
     * @see jme3gl2.scene.control.AbstractAnimation2DControl.Type
     * @return Type
     */
    public abstract Type getType();
    
    /**
     * (non-Javadoc)
     * @see com.jme3.scene.control.AbstractControl#controlRender(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort) 
     * @param rm RenderManager
     * @param vp ViewPort
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
    
    /**
     * Add a new {@link org.je3gl.listener.AnimationChangeListener} listener.
     * @param changeListener listener
     */
    public void addAnimationChangeListener(AnimationChangeListener<O, A, E> changeListener) {
        this.changeListeners.add(changeListener);
    }
    
    /**
     * Removes a previously registered listener of type {@link org.je3gl.listener.AnimationChangeListener}.
     * @param changeListener listener
     */
    public void removeAnimationChangeListener(AnimationChangeListener<O, A, E> changeListener) {
        this.changeListeners.remove(changeListener);
    }
    
    /**
     * Add a new {@link org.je3gl.listener.AnimationTimeChangeListener} listener.
     * @param timeChangeListener listener
     */
    public void addAnimationTimeChangeListener(AnimationTimeChangeListener<O, A, E> timeChangeListener) {
        this.timeChangeListeners.add(timeChangeListener);
    }
    
    /**
     * Removes a previously registered listener of type {@link org.je3gl.listener.AnimationTimeChangeListener}.
     * @param timeChangeListener listener
     */
    public void removeAnimationTimeChangeListener(AnimationTimeChangeListener<O, A, E> timeChangeListener) {
        this.timeChangeListeners.remove(timeChangeListener);
    }
    
    /**
     * Activate all {@link org.je3gl.listener.AnimationChangeListener} listeners.
     * @param post <code>true</code> if after animation; otherwise <code>false</code> if before animation (change)
     * @param animIdx current animation index
     * @param idx current frame index
     * @param frame current frame
     */
    @SuppressWarnings("unchecked")
    protected final void fireAnimation2DChangeListener(boolean post, int animIdx, int idx, int frame) {
        this.changeListeners.stream().filter(Objects::nonNull).forEachOrdered(listener -> {
            AnimationEvent<O, A, E> event = new AnimationEvent<>((O) spatial, currentAnimation2D[animIdx], (E) AbstractAnimation2DControl.this, currentNameAnimation2D, idx, frame);
            if (post) {
                listener.afterAnimation2DChange(event);
            } else {
                listener.beforeAnimation2DChange(event);
            }
        });
    }
    
    /**
     * Activate all {@link org.je3gl.listener.AnimationTimeChangeListener} listeners
     */
    protected final void fireAnimatedTimeChangeListener() {
        this.timeChangeListeners.stream().filter(Objects::nonNull).forEachOrdered(listener -> {
            float var = (elapsedeTime / animationFrameTime) * 100.0F;
            listener.onTime(var, elapsedeTime);
        });
    }
    
    /**
     * Add a new animation to this animated control.
     * @param name animation name
     * @param frames animation frames
     */
    public void addAnimation(String name, A[] frames) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Invalid name >> [ " + name +"]");
        }
        if (frames == null) {
            throw new IllegalArgumentException("Invalid animated frames >> " + Arrays.toString(frames));
        }
        if (frames.length < 1) {
            LOG.log(Level.WARNING, "Animation \"{0}\" does not have enough frames to be rendered", name);
        }
        if (animations.containsKey(name)) {
            LOG.log(Level.WARNING, "Animation [{0}] existing.", name);
        } else {
            animations.put(name, frames);
        }
    }
    
    /**
     * Plays an animation previously registered to this animated control.
     * @param name animation name
     * @param timePerFrame the time (duration) of the animation
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public boolean playAnimation(String name, float timePerFrame) {
        if (name == null || timePerFrame <= 0) {
            throw new IllegalArgumentException("Both the name and the time per frame must be valid: [name != null & timePerFrame > 0]");
        }
        
        A[] animation = animations.get(name);
        if (animation == null) {
            LOG.log(Level.WARNING, "[{0}] animation not found.", name);
            return false;
        }
        
        if (!(name.equals(currentNameAnimation2D)) && animation.length > 0) {
            animationFrameTime     = timePerFrame;
            currentAnimation2D     = animation;
            currentNameAnimation2D = name;
            
            if (spatial != null) {
                handlerFunction.applyAnimation2DControl((O) spatial, animation[0], (E)this);
            }
            animation2DIndex = 0;
            elapsedeTime     = 0;
            return true;
        }
        return false;
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#read(com.jme3.export.JmeImporter) 
     * 
     * @param im {@link com.jme3.export.JmeImporter}
     * @throws IOException hrows
     */
    @Override
    @SuppressWarnings("unchecked")
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        handlerFunction = (AnimatedMaterialsHandlerFunction<O, A, E>) in.readSavable("HandlerFunction", handlerFunction);
        
        String[] myAnimations = in.readStringArray("MyAnimations", new String[0]);
        for (final String name : myAnimations) {
            Savable[] nsavable = in.readSavableArray("jMe3GL2@Animation2D?=" + name, new Savable[0]);
            Animation2D[] nanim = new Animation2D[nsavable.length];
            
            for (int i = 0; i < nsavable.length; i++) {
                if (!(nsavable[i] instanceof Animation2D)) {
                    throw new IllegalStateException("[ jMe3GL2 ] Invalid animation: " + nsavable[i].getClass().getCanonicalName());
                }
                nanim[i] = (Animation2D) nsavable[i];
            }
            animations.put(name, (A[]) nanim);
        }
        
        setAnimationLoop(in.readBoolean("Animation2Dloop", animation2Dloop));
        setAnimationSpeed(in.readFloat("Animation2DSpeed", animation2DSpeed));
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#write(com.jme3.export.JmeExporter) 
     * 
     * @param ex {@link com.jme3.export.JmeExporter}
     * @throws IOException throws
     */
    @Override
    @SuppressWarnings("unchecked")
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);        
        out.write(handlerFunction, "HandlerFunction", null);
        
        String[] myAnimations = getAnimations().toArray(String[]::new);
        out.write(myAnimations, "MyAnimations", null);
        
        for (final Map.Entry<String, A[]> entry : animations.entrySet()) {
            out.write(entry.getValue(), "jMe3GL2@Animation2D?=" + entry.getKey(), new Animation2D[0]);
        }
        
        out.write(animation2Dloop, "Animation2Dloop", true);
        out.write(animation2DSpeed, "Animation2DSpeed", 1);
    }
}
