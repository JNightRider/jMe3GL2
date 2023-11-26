/* Copyright (c) 2009-2023 jMonkeyEngine.
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
package jme3gl2.physics.debug;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import jme3gl2.physics.PhysicsSpace;
import jme3gl2.physics.control.PhysicsBody2D;
import jme3gl2.physics.debug.control.BoundsDebugControl;
import jme3gl2.physics.debug.control.PhysicsDebugControl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase <code>Dyn4JDebugAppState</code> encargado de gestionar un estado para
 * la depuración de las formas físicas de los cuerpo que se agregan al mundo 
 * de <b>Dyn4j</b>.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT 
 * @param <E> tipo-cuepos.
 * 
 * @since 2.5.0
 */
public class Dyn4JDebugAppState<E extends PhysicsBody2D> extends BaseAppState {

    /** Logger de la clase. */
    private static final Logger LOGGER = Logger.getLogger(Dyn4JDebugAppState.class.getName());
    
    /** Aplicación principal <code>JME</code>. */
    protected Application application;
    
    /** Administradro de recursos <code>JME</code>. */
    protected AssetManager assetManager;
    
    /*
        Parte gráfica.
    */
    /** Espacio físico de los cuerpos. */
    private final PhysicsSpace<E> physicsSpace;
    
    /**
     * Todos los objetos-cuepor para las formas físicas que agregarán en este
     * nodo fuera de escena para que no interfiera con el nodo raíz principal.
     */
    private Node debugNode;
    
    ///** Vista depuración. */
    //private ViewPort devugViewPort;
    
    /* Administrador de renderizado. */
    private Graphics2DRenderer renderer;    // deuprador.
    //private RenderManager renderManager;    // renderizador jme.
    
    /*
        Cuerpos físicos y articulaciones.
    */
    /** Mapa cuerpos físicos. */
    protected Map<E, Spatial> bodies 
            = new HashMap<>();;

    /**
     * Constructor de la clase <code>Dyn4JDebugAppState</code> donde pide el
     * espacio físico para gestionar las formas de los cuerpos.
     * 
     * @param physicsSpace espacio-físico.
     */
    public Dyn4JDebugAppState(PhysicsSpace<E> physicsSpace) {
        this.physicsSpace = physicsSpace;
    }
    
    /**
     * (non-JavaDoc)
     * @see com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application) 
     * @param app Application.
     */
    @Override
    public void initialize(Application app) {
        assetManager  = app.getAssetManager();
        application   = app;
        
        /* inicializamos la escena-depuración. */
        renderer  = new Graphics2DRenderer(assetManager);
        debugNode = new Node("Debug Node");
        debugNode.setCullHint(Spatial.CullHint.Never);
        debugNode.addControl(new BoundsDebugControl<>(physicsSpace.getPhysicsWorld(), renderer));
    }

    /**
     * (non-JavaDoc)
     */
    @Override
    protected void onEnable() {
        if (getApplication() instanceof SimpleApplication) {
            ((SimpleApplication) getApplication()).getRootNode().attachChild(debugNode);
        }
    }

    /**
     * (non-JavaDoc)
     */
    @Override
    protected void onDisable() {
        this.debugNode.removeFromParent();
    }
    
    /**
     * (non-JavaDoc)
     * @see com.jme3.app.state.AbstractAppState#cleanup() 
     * @param app Application
     */
    @Override
    protected void cleanup(Application app) {
        this.debugNode.removeFromParent();
    }
    
    /**
     * (non-JavaDoc)
     * @see com.jme3.app.state.AbstractAppState#update(float) 
     * @param tpf float.
     */
    @Override
    public void update(float tpf) {
        updateBodies();
    }
    
    /**
     * Método encargado de actualizar los cuerpos físicos.
     */
    private void updateBodies() {
        final Map<E, Spatial> oldBodies = this.bodies;        
        this.bodies = new HashMap<>();
        
        final Collection<E> currentBodies 
                = this.physicsSpace.getBodies();

        /* Crear nuevo mapa de cuerpos. */
        for (final E body : currentBodies) {            
            if (oldBodies.containsKey(body)) {

                /* Coapiar el Spatial existente. */
                final Spatial spatial = oldBodies.get(body);
                this.bodies.put(body, spatial);
                oldBodies.remove(body);
            } else {
                LOGGER.log(Level.FINE, "**Create new debug PhysicsBody2D**");
                
                /* Crear un nuevo Spatial */
                final Node node = new Node(body.toString());
                node.addControl(new PhysicsDebugControl(renderer, body));
                
                this.bodies.put(body, node);
                this.debugNode.attachChild(node);
            }
        }
        for (final Spatial spatial : oldBodies.values()) {
            spatial.removeFromParent();
        }
    }
}
