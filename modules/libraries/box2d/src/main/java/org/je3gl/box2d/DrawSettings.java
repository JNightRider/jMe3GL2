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
package org.je3gl.box2d;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public final class DrawSettings extends HashMap<String, Boolean> {
    
    public static final String
            DRAW_SHAPES             = "drawShapes",
            DRAW_BODY_NAMES         = "drawBodyNames",
            DRAW_JOINTS             = "drawJoints",
            DRAW_JOINT_EXTRAS       = "drawJointExtras",
            DRAW_ANCHOR_A           = "drawAnchorA",
            DRAW_CHAIN_NORMALS      = "drawChainNormals",
            DRAW_CONTACTS           = "drawContacts",
            DRAW_CONTACT_FEATURES   = "drawContactFeatures",
            DRAW_CONTACT_NORMALS    = "drawContactNormals",
            DRAW_FRICTION_FORCES    = "drawFrictionForces",
            DRAW_GRAPH_COLORS       = "drawGraphColors",
            DRAW_ISLANDS            = "drawIslands",
            DRAW_MASS               = "drawMass",
            DRAW_BOUNDS             = "drawBounds";

    private static final Map<String, Boolean> DEFAULT = new HashMap<>();
    
    static {
        DEFAULT.put(DRAW_ANCHOR_A, true);
        DEFAULT.put(DRAW_BODY_NAMES, true);
        DEFAULT.put(DRAW_BOUNDS, true);
        DEFAULT.put(DRAW_CHAIN_NORMALS, true);
        DEFAULT.put(DRAW_CONTACTS, true);
        DEFAULT.put(DRAW_CONTACT_FEATURES, true);
        DEFAULT.put(DRAW_CONTACT_NORMALS, true);
        DEFAULT.put(DRAW_FRICTION_FORCES, true);
        DEFAULT.put(DRAW_GRAPH_COLORS, true);
        DEFAULT.put(DRAW_ISLANDS, true);
        DEFAULT.put(DRAW_JOINTS, true);
        DEFAULT.put(DRAW_JOINT_EXTRAS, true);
        DEFAULT.put(DRAW_MASS, true);
        DEFAULT.put(DRAW_SHAPES, true);
    }
    
    private boolean needUpdate = false;
    
    public DrawSettings() {
        putAll(DEFAULT);
    }
    
    public DrawSettings drawShapes(boolean value) {
        put(DRAW_SHAPES, value);
        return this;
    }
    
    public boolean drawShapes() {
        return get(DRAW_SHAPES);
    }

    public DrawSettings drawBodyNames(boolean value) {
        put(DRAW_BODY_NAMES, value);
        return this;
    }

    public boolean drawBodyNames() {
        return get(DRAW_BODY_NAMES);
    }

    public DrawSettings drawJoints(boolean value) {
        put(DRAW_JOINTS, value);
        return this;
    }

    public boolean drawJoints() {
        return get(DRAW_JOINTS);
    }

    public DrawSettings drawJointExtras(boolean value) {
        put(DRAW_JOINT_EXTRAS, value);
        return this;
    }

    public boolean drawJointExtras() {
        return get(DRAW_JOINT_EXTRAS);
    }

    public DrawSettings drawAnchorA(boolean value) {
        put(DRAW_ANCHOR_A, value);
        return this;
    }

    public boolean drawAnchorA() {
        return get(DRAW_ANCHOR_A);
    }

    public DrawSettings drawChainNormals(boolean value) {
        put(DRAW_CHAIN_NORMALS, value);
        return this;
    }

    public boolean drawChainNormals() {
        return get(DRAW_CHAIN_NORMALS);
    }

    public DrawSettings drawContacts(boolean value) {
        put(DRAW_CONTACTS, value);
        return this;
    }

    public boolean drawContacts() {
        return get(DRAW_CONTACTS);
    }

    public DrawSettings drawContactFeatures(boolean value) {
        put(DRAW_CONTACT_FEATURES, value);
        return this;
    }

    public boolean drawContactFeatures() {
        return get(DRAW_CONTACT_FEATURES);
    }

    public DrawSettings drawContactNormals(boolean value) {
        put(DRAW_CONTACT_NORMALS, value);
        return this;
    }

    public boolean drawContactNormals() {
        return get(DRAW_CONTACT_NORMALS);
    }

    public DrawSettings drawFrictionForces(boolean value) {
        put(DRAW_FRICTION_FORCES, value);
        return this;
    }

    public boolean drawFrictionForces() {
        return get(DRAW_FRICTION_FORCES);
    }

    public DrawSettings drawGraphColors(boolean value) {
        put(DRAW_GRAPH_COLORS, value);
        return this;
    }

    public boolean drawGraphColors() {
        return get(DRAW_GRAPH_COLORS);
    }

    public DrawSettings drawIslands(boolean value) {
        put(DRAW_ISLANDS, value);
        return this;
    }

    public boolean drawIslands() {
        return get(DRAW_ISLANDS);
    }

    public DrawSettings drawMass(boolean value) {
        put(DRAW_MASS, value);
        return this;
    }

    public boolean drawMass() {
        return get(DRAW_MASS);
    }

    public DrawSettings drawBounds(boolean value) {
        put(DRAW_BOUNDS, value);
        return this;
    }

    public boolean drawBounds() {
        return get(DRAW_BOUNDS);
    }

    @Override
    public Boolean put(String key, Boolean value) {
        needUpdate = true;
        return super.put(key, value);
    }
    
    public DrawSettings update() {
        needUpdate = true;
        return this;
    }

    public boolean isNeedUpdate() {
        return needUpdate;
    }
}
