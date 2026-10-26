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
 * Class responsible for managing Box2D debugger settings.
 *
 * @author wil
 * @version 1.0.0
 * @since 3.2.0
 */
public final class DrawSettings extends HashMap<String, Boolean> {

    /*  Names of the settings supported by the Box2D debugger. */
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

    /**
     * A map with the debugger's default settings.
     */
    private static final Map<String, Boolean> DEFAULT = new HashMap<>();

    static {
        DEFAULT.put(DRAW_ANCHOR_A, false);
        DEFAULT.put(DRAW_BODY_NAMES, false);
        DEFAULT.put(DRAW_BOUNDS, false);
        DEFAULT.put(DRAW_CHAIN_NORMALS, false);
        DEFAULT.put(DRAW_CONTACTS, false);
        DEFAULT.put(DRAW_CONTACT_FEATURES, false);
        DEFAULT.put(DRAW_CONTACT_NORMALS, false);
        DEFAULT.put(DRAW_FRICTION_FORCES, false);
        DEFAULT.put(DRAW_GRAPH_COLORS, false);
        DEFAULT.put(DRAW_ISLANDS, false);
        DEFAULT.put(DRAW_JOINTS, false);
        DEFAULT.put(DRAW_JOINT_EXTRAS, false);
        DEFAULT.put(DRAW_MASS, false);
        DEFAULT.put(DRAW_SHAPES, true);
    }

    /**
     * An indicator that signals to the debugger manager whether an update has
     * occurred.
     *
     * <p>
     * A value of {@code true} means that an indicator has changed, while a
     * value of {@code false} indicates that no change has taken place.
     * </p>
     */
    private boolean needUpdate = false;

    /** Constructor. */
    public DrawSettings() {
        putAll(DEFAULT);
    }

    // -----------------------------------

    public DrawSettings drawShapes(boolean value)          { put(DRAW_SHAPES, value); return this;           }
    public DrawSettings drawBodyNames(boolean value)       { put(DRAW_BODY_NAMES, value); return this;       }
    public DrawSettings drawJoints(boolean value)          { put(DRAW_JOINTS, value); return this;           }
    public DrawSettings drawJointExtras(boolean value)     { put(DRAW_JOINT_EXTRAS, value); return this;     }
    public DrawSettings drawAnchorA(boolean value)         { put(DRAW_ANCHOR_A, value); return this;         }
    public DrawSettings drawChainNormals(boolean value)    { put(DRAW_CHAIN_NORMALS, value); return this;    }
    public DrawSettings drawContacts(boolean value)        { put(DRAW_CONTACTS, value); return this;         }
    public DrawSettings drawContactFeatures(boolean value) { put(DRAW_CONTACT_FEATURES, value); return this; }
    public DrawSettings drawContactNormals(boolean value)  { put(DRAW_CONTACT_NORMALS, value); return this;  }
    public DrawSettings drawFrictionForces(boolean value)  { put(DRAW_FRICTION_FORCES, value); return this;  }
    public DrawSettings drawGraphColors(boolean value)     { put(DRAW_GRAPH_COLORS, value); return this;     }
    public DrawSettings drawIslands(boolean value)         { put(DRAW_ISLANDS, value); return this;          }
    public DrawSettings drawMass(boolean value)            { put(DRAW_MASS, value); return this;             }
    public DrawSettings drawBounds(boolean value)          { put(DRAW_BOUNDS, value); return this;           }
    
    public boolean drawShapes()          { return get(DRAW_SHAPES);           }
    public boolean drawBodyNames()       { return get(DRAW_BODY_NAMES);       }
    public boolean drawJoints()          { return get(DRAW_JOINTS);           }
    public boolean drawJointExtras()     { return get(DRAW_JOINT_EXTRAS);     }
    public boolean drawAnchorA()         { return get(DRAW_ANCHOR_A);         }
    public boolean drawChainNormals()    { return get(DRAW_CHAIN_NORMALS);    }
    public boolean drawContacts()        { return get(DRAW_CONTACTS);         }
    public boolean drawContactFeatures() { return get(DRAW_CONTACT_FEATURES); }
    public boolean drawContactNormals()  { return get(DRAW_CONTACT_NORMALS);  }
    public boolean drawFrictionForces()  { return get(DRAW_FRICTION_FORCES);  }
    public boolean drawGraphColors()     { return get(DRAW_GRAPH_COLORS);     }
    public boolean drawIslands()         { return get(DRAW_ISLANDS);          }
    public boolean drawMass()            { return get(DRAW_MASS);             }
    public boolean drawBounds()          { return get(DRAW_BOUNDS);           }

    // -----------------------------------

    /**
     * (non-Javadoc)
     * 
     * @param key String
     * @param value Boolean
     * @return Boolean
     */
    @Override
    public Boolean put(String key, Boolean value) {
        needUpdate = true;
        return super.put(key, value);
    }

    /**
     * Sets the flag indicating that changes have been applied to the debugger.
     *
     * @return DrawSettings
     */
    public DrawSettings update() {
        needUpdate = false;
        return this;
    }

    /**
     * Check if there are any changes to apply.
     * 
     * @return boolean
     */
    public boolean isNeedUpdate() {
        return needUpdate;
    }
}
