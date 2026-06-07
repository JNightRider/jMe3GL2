/*
BSD 3-Clause License

Copyright (c) 2023-2026, Night Rider (Wilson)

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
package org.je3gl.renderer;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.FlyByCamera;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.util.TempVars;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.je3gl.math.Rect;

/**
 * An object of the class <code>Camera2DAppSate</code> is responsible for preparing
 * and operating the 2D camera.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class Camera2DAppSate extends AbstractAppState {
    /** Logger class. */
    private static final Logger LOGGER = Logger.getLogger(Camera2DAppSate.class.getName());

    /**
     * Camera display modes.
     */
    public static enum KeepMode {
        /**
         * The screen width remains constant, and only the height is adjusted.
         */
        Width,
        /**
         * The screen height remains constant and only the width is adjusted.
         */
        Height;
    }

    /**
     * The camera manages this state so that it can be used in a 2D (projected)
     * world.
     */
    private Camera camera;

    /**
     * A target that the camera must follow; if no target is set ({@code null}),
     * the camera does not move autonomously.
     */
    private Spatial target;

    /** Mode - camera */
    private KeepMode mode;
    /** Size of the square that is consistently displayed on the screen */
    private float viewSize;
    /** The zoom. */
    private float zoom;

    /**
     * To allow the camera to move without affecting the objects, it is
     * necessary to apply smoothing to both coordinates independently.
     */
    private Vector2f smooth;

    /**
     * Camera displacement relative to its calculated position.
     */
    private Vector2f offset;

    /**
     * Vector where the new camera position is stored.
     *
     * @see #offset
     * @see #clipping
     * @see #target
     */
    private Vector3f location;

    /**
     * A rectangle that stores the camera's boundaries; this is used to limit
     * the camera's movement (position).
     */
    private Rect clipping;

    /** comparator */
    private UnitComparator unitComparator;
    /** JME application.*/
    private Application application;

    /**
     * Generate a new controller for a 2D camera using a 3D camera provided by
     * JME or a custom camera.
     */
    public Camera2DAppSate() {
        this(1.0f);
    }

    /**
     * Generate a new controller for a 2D camera using a 3D camera provided by
     * JME or a custom camera.
     *
     * @param zoom float
     */
    public Camera2DAppSate(float zoom) {
        this(null, zoom);
    }

    /**
     * Generate a new controller for a 2D camera using a 3D camera provided by
     * JME or a custom camera.
     *
     * @param camera Camera
     * @param zoom float
     */
    public Camera2DAppSate(Camera camera, float zoom) {
        this(camera, 10.0f, zoom);
    }

    /**
     * Generate a new controller for a 2D camera using a 3D camera provided by
     * JME or a custom camera.
     *
     * @param camera Camera
     * @param viewSize float
     * @param zoom float
     */
    public Camera2DAppSate(Camera camera, float viewSize, float zoom) {
        this(camera, KeepMode.Height, viewSize, zoom);
    }

    /**
     * Generate a new controller for a 2D camera using a 3D camera provided by
     * JME or a custom camera.
     *
     * @param camera Camera
     * @param mode KeepMode
     * @param viewSize float
     * @param zoom float
     */
    public Camera2DAppSate(Camera camera, KeepMode mode, float viewSize, float zoom) {
        this.smooth   = new Vector2f(12, 4);
        this.location = new Vector3f();
        this.camera   = camera;
        this.mode     = mode;
        this.viewSize = viewSize;
        this.zoom     = zoom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if (app instanceof SimpleApplication myapp) {
            FlyByCamera flyByCamera = myapp.getFlyByCamera();
            flyByCamera.setEnabled(false);
            flyByCamera.unregisterInput();
            
            StringBuilder sb = new StringBuilder();
            sb.append("SimpleApplicationdetected")
              .append('\n').append(" * PlatformerCameraState is removing default fly camera");

            LOGGER.log(Level.INFO, String.valueOf(sb));
        }

        application = app;
        if (camera == null) {
            camera = application.getCamera();
        }

        applyProjection();
        camera.setLocation(new Vector3f(0, 0, 0));
        camera.lookAtDirection(new Vector3f(0f, 0f, -1f), Vector3f.UNIT_Y);
 
        applyUnitComparator();
        super.initialize(stateManager, app);
    }
    
    /**
     * Configure a distance comparator on a specific axis for the camera.
     * 
     * @param unit axis for the comparator
     * @param type comparator type
     * @param buckets the application layer
     */
    public void setUnitComparator(Vector3f unit, UnitComparator.UType type, RenderQueue.Bucket ...buckets) {
        unitComparator = new UnitComparator(unit, type);
        unitComparator.setLayers(buckets);
        if (isInitialized()) {
            applyUnitComparator();
        }
    }
    
    /**
     * Apply the comparator
     */
    private void applyUnitComparator() {
        if (unitComparator == null) {
            return;
        }
        
        for (final RenderQueue.Bucket bucket : unitComparator.getLayers()) {
            application.getViewPort()
                    .getQueue()
                    .setGeometryComparator(bucket, unitComparator);
        }
    }

    /**
     * Apply the projection
     */
    private void applyProjection() {
        if (!camera.isParallelProjection()) {
            camera.setParallelProjection(true);
        }

        float aspect = (float) camera.getWidth() / (float) camera.getHeight();
        float width, height;

        float scaledSize = viewSize * zoom;
        if (mode == KeepMode.Width) {
            width  = scaledSize;
            height = scaledSize / aspect;
        } else {
            height = scaledSize;
            width  = scaledSize * aspect;
        }
        camera.setFrustum(-1000f, 1000f, -width * 0.5f, width * 0.5f, height * 0.5f, -height * 0.5f);
    }

    /**
     * Apply the projection
     */
    public void updateProjection() {
        applyProjection();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(float tpf) {
        TempVars vars = TempVars.get();
        Vector2f __offset = offset == null ? Vector2f.ZERO : offset;        
        Vector3f __point  = target == null ? Vector3f.ZERO : target.getWorldTranslation();

        Vector2f __result;
        if (clipping != null) {
            __result = clipping.clipping(__point, __offset);
        } else {
            __result = vars.vect2d2.set(__point.x + __offset.x, __point.y + __offset.y);
        }

        float alphaX = 1f - FastMath.exp(-smooth.x * tpf);
        float alphaY = 1f - FastMath.exp(-smooth.y * tpf);

        Vector3f camPos = camera.getLocation();
        location.set( camPos.x + (__result.x - camPos.x) * alphaX,
                      camPos.y + (__result.y - camPos.y) * alphaY,
                     camPos.z );

        camera.setLocation(location);
        vars.release();
    }

    /**
     * Sets the value of the {@code smooth} attribute
     *
     * @param smooth Vector2f
     */
    public void setSmooth(Vector2f smooth) {
        this.smooth = smooth;
    }

    /**
     * Sets the value of the {@code zoom} attribute
     *
     * @param zoom float
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
        applyProjection();
    }

    /**
     * Sets the value of the {@code target} attribute
     *
     * @param target Spatial
     */
    public void setTarget(Spatial target) {
        if (target != null) {
            Vector3f npos = target.getWorldTranslation();
            location.set(npos);

            if (isInitialized()) {
                Vector3f camPos = camera.getLocation().clone();
                camPos.setX(npos.x)
                      .setY(npos.y);
                camera.setLocation(camPos);
            }
        }
        this.target = target;
    }

    /**
     * Sets the value of the {@code offset} attribute
     *
     * @param offset Vector2f
     */
    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }

    /**
     * Sets the value of the {@code clipping} attribute
     *
     * @param min Vector2f
     * @param max Vector2f
     */
    public void setLimitRect(Vector2f min, Vector2f max) {
        setLimitRect(new Rect(min.x, min.y, max.x, max.y));
    }

    /**
     * Sets the value of the {@code clipping} attribute
     *
     * @param rect rect
     */
    public void setLimitRect(Rect rect) {
        this.clipping = rect;
    }

    /**
     * Sets the value of the {@code mode} attribute
     *
     * @param mode KeepMode
     */
    public void setMode(KeepMode mode) {
        this.mode = mode;
        this.applyProjection();
    }

    /**
     * Returns the value of the attribute {@code mode}
     *
     * @return KeepMode
     */
    public KeepMode getMode() {
        return mode;
    }

    /**
     * Returns the value of the attribute {@code clipping}
     *
     * @return Rect
     */
    public Rect getLimitRect() {
        return clipping;
    }

    /**
     * Returns the value of the attribute {@code offset}
     *
     * @return Vector2f
     */
    public Vector2f getOffset() {
        return offset;
    }

    /**
     * Returns the value of the attribute {@code camera}
     *
     * @return Camera
     */
    public Camera getCamera() {
        return camera;
    }
}
