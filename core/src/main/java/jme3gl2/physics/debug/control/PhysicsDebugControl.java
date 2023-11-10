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
package jme3gl2.physics.debug.control;

import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import jme3gl2.physics.control.PhysicsBody2D;
import jme3gl2.physics.debug.Color;
import jme3gl2.physics.debug.Graphics2DRenderer;
import jme3gl2.physics.debug.Id;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;

/**
 * Clase <code>PhysicsDebugControl</code> encargado de controlar las formas
 * físicas de un cuerpo en el espacio.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT 
 * 
 * @since 2.5.0
 */
public class PhysicsDebugControl extends AbstractPhysicsDebugControl<PhysicsBody2D> {
    
    /** Renderizador para las formas físcas. */
    protected Graphics2DRenderer renderer;
    
    /**
     * Mapa para las formas físicas.
     */
    protected Map<UUID, BodyFixture> shapes 
            = new HashMap<>();
    
    /**
     * Mapa geometría encargado de almacenar los nodos que dan forma física a los
     * <code>BodyFixture</code> del cuepor físico.
     */
    protected Map<UUID, Node> geometries 
            = new HashMap<>();
    
    /** Nodo padre que contiene las formas físicas.*/
    protected Node spatialAsNode = null;
    
    /** Gestor de ID. */
    protected Id id;
    
    /**
     * Control de la clase <code>PhysicsDebugControl</code> donde se inicializa
     * las formas físicos contenido en los cerpos físicos-
     * 
     * @param renderer renderizador para los cuerpos físicos.
     * @param body cuerpo físico.
     */
    public PhysicsDebugControl(Graphics2DRenderer renderer, PhysicsBody2D body) {
        super(body);
        this.id = new Id();
        this.renderer = renderer;
        for (final BodyFixture bf : body.getFixtures()) {
            processRender(bf);
        }
    }
    
    /**
     * Método encargado de establecer el <code>Spatial</code> a controlar, 
     * normalmente será un <code>Node</code> al cual se agregaran las
     * geometrías de los cuerpos físicos depurados.
     * <p>
     * Solo se agregan los cuerpos que esten disponible en la pila.
     * 
     * @param spatial <code>Spatial</code> a controlar.
     */
    @Override
    public void setSpatial(final Spatial spatial) {
        if (spatial != null && spatial instanceof Node) {
            this.spatialAsNode = (Node) spatial;
            for (final Node node : this.geometries.values()) {
                this.spatialAsNode.attachChild(node);
            }
        } else if (spatial == null && this.spatial != null) {
            for (final Node node : this.geometries.values()) {
                this.spatialAsNode.detachChild(node);
            }
        }
        super.setSpatial(spatial);
    }

    /**
     * Método encargado de actualizar la lista de nodos para las formas físicas
     * del cuerpo.
     * <p>
     * Si hay un cambio o una nueva formas, se notifica al nodo padre para
     * desechar los hijos que tiene para luego agregar la nueva pila de
     * geometrías.
     * 
     * @param tpf tipo por cuadors por segundo.
     */
    @Override
    protected void controlUpdate(float tpf) {
        final List<UUID> currentIDs = new ArrayList<>();
        for (final BodyFixture bodyFixture : this.body.getFixtures()) {
            final Convex shape = bodyFixture.getShape();
            final UUID uuid    = id.getId(shape);
            
            currentIDs.add(uuid);

            if (!this.shapes.containsKey(uuid)) {
                // Nuevo fixture: cree un espacio para la forma, agréguelo a 
                // la lista de geometría y adjunte a la raíz espacial.
                processRender(bodyFixture);
            }
        }
        
        // Elimina formas que no están presentes en la lista de 
        // características del cuerpo.
        boolean b = this.shapes.keySet().retainAll(currentIDs) &&
                    this.geometries.keySet().retainAll(currentIDs) &&
                    this.id.retain(currentIDs);
        
        
        // Establecer material según el estado del cuerpo.
        if (b) {
            this.spatialAsNode.detachAllChildren();
            this.updateMat(true);
        } else {
            this.updateMat(false);
        }
        super.controlUpdate(tpf);
    }
    
    /**
     * Método encargado de actualizar los materiales de las formas físicas
     * generadas.
     * 
     * @param attach <code>true</code> si hay una nuva pila de nodos disponibles
     * en espera por agregarse, de lo contrario <code>false</code>.
     */
    protected void updateMat(boolean attach) {
        Material material;
        if (this.body.isEnabled()) {
            if (this.body.isBullet()) {
                material = this.renderer.renderMat(Color.RED);
            } else if (this.body.isStatic()) {
                material = this.renderer.renderMat(Color.ORANGE);
            } else if (this.body.isKinematic()) {
                material = this.renderer.renderMat(Color.YELLOW);
            } else if (this.body.isAtRest()) {
                material = this.renderer.renderMat(Color.BLUE);
            } else {
                material = this.renderer.renderMat(Color.MAGENTA);
            }
        } else {
            material = this.renderer.renderMat(Color.GRAY);
        }        
        for (final Map.Entry<UUID, Node> entry : this.geometries.entrySet()) {
            final Node node = entry.getValue();
            
            if (attach) {
                this.spatialAsNode.attachChild(node);
            }
            final Spatial geom = node.getChild(node.getName());
            if (geom != null) {
                geom.setMaterial(material);
            }
        }
    }
    
        
    /**
     * Método encargado de procesar el renderizado de los cuepor físicos, es 
     * decir su forma.
     * @param shape forma física.
     * @return forma gráfica.
     */
    private Node processRender(BodyFixture bf) {
        final Convex shape = bf.getShape();
        final Node node = renderer.render(shape, null);        
        final UUID uuid = id.getId(shape);
        
        this.shapes.put(uuid, bf);
        this.geometries.put(uuid, node);
        return node;
    }
}
