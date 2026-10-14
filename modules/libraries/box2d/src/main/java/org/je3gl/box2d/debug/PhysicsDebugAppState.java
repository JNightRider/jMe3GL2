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
package org.je3gl.box2d.debug;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

import java.util.logging.Logger;

import org.je3gl.box2d.Box2dAppState;
import org.je3gl.box2d.PhysicsSpace;
import org.je3gl.box2d.ThreadingType;
/**
 * Class <code>PhysicsDebugAppState</code> responsible for managing a state for
 * the debugging of the physical forms of the bodies that are added to the world
 * of
 * <b>Box2d</b>.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public class PhysicsDebugAppState extends AbstractAppState {

    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(PhysicsDebugAppState.class.getName());

    private Box2dAppState box2dAppState;
    private PhysicsDebugSceneProcessor debugProcessor;

    /**
     * Debugger view.
     */
    protected ViewPort viewPort;
    /** <code>JME3</code> renderer.
     */
    protected RenderManager rm;
    private Application application;

    private Camera camera;

    private Node ui;

    private int width;
    private int height;

    public PhysicsDebugAppState() {

    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        AssetManager assetManager   = app.getAssetManager();
        RenderManager renderManager = app.getRenderManager();

        box2dAppState = stateManager.getState(Box2dAppState.class);
        PhysicsSpace physicsSpace = box2dAppState.getPhysicsSpace();
        physicsSpace.setDebugAppState(this);

        application = app;
        camera      = app.getCamera();
        rm          = renderManager;
        ui          = new Node("Box2d-JNI UI");
        ((SimpleApplication) app).getGuiNode().attachChild(ui);

        ViewPort uiViewPort = ((SimpleApplication) app).getGuiViewPort();
        Camera uicam = uiViewPort.getCamera();
        width  = uicam.getWidth();
        height = uicam.getHeight();

        viewPort = renderManager.createMainView("Physics Debug Overlay", app.getCamera());
        viewPort.setClearFlags(false, true, true);

        debugProcessor = new PhysicsDebugSceneProcessor(assetManager, stateManager);
        viewPort.addProcessor(debugProcessor);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        if (debugProcessor != null && debugProcessor.isInitialized()) {
            debugProcessor.updateUI(ui, camera, width, height);
        }
    }


    @Override
    public void setEnabled(boolean enabled) {
//        if (isInitialized()) {
//            debugProcessor.setEnabled(enabled);
//        }
        super.setEnabled(enabled);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        
        ui.detachAllChildren();

        viewPort.removeProcessor(debugProcessor);
        rm.removeMainView(viewPort);
    }

    public void flush() {
        if (debugProcessor != null) {
            debugProcessor.flush();
        }
    }

    public PhysicsDebugSceneProcessor getDebugProcessor() {
        return debugProcessor;
    }
    
}
