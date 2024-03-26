package jme3gl2.core;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import jme3gl2.physics.Dyn4jAppState;
import jme3gl2.physics.ThreadingType;
import jme3gl2.physics.control.PhysicsBody2D;
import jme3gl2.physics.control.RigidBody2D;
import jme3gl2.scene.shape.Sprite;
import jme3gl2.scene.tile.TileMap;
import static jme3gl2.utilities.TileMapUtilities.*;
import jme3gl2.utilities.GeometryUtilities;
import jme3gl2.utilities.MaterialUtilities;
import org.dyn4j.geometry.MassType;


public class TileMapTest extends SimpleApplication {

    public static void main(String[] args) {
        TileMapTest app = new TileMapTest();
        
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1024, 756);
        settings.setGammaCorrection(false);
        
        app.setSettings(settings);
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        Dyn4jAppState<PhysicsBody2D> dyn4jAppState = new Dyn4jAppState<>(ThreadingType.SEQUENTIAL);
        dyn4jAppState.setDebug(true);
        stateManager.attach(dyn4jAppState);

        TileMap map = getTileMap("TileMap/PixelPlatformer/tilemap_packed.png", 20, 9, assetManager);
        map.setPhysicsSpace(dyn4jAppState.getPhysicsSpace());
        
        map.addTile(getTile(1, 1, 1, 1, 0, 0, 0, true));
        map.addTile(getTile(2, 1, 1, 1, 1, 0, 0, true));
        map.addTile(getTile(5, 1, 1, 1, 0, -1, 0, false));
        
        map.addTile(getTile(2, 3, 1, 1, 2, 0, 0, true));
        map.addTile(getTile(3, 3, 1, 1, 3, 0, 0, true));
        map.addTile(getTile(4, 1, 1, 1, 3, -1, 0, false));
        
        rootNode.attachChild(map);
        setupCube();
    }

    private void setupCube() {
        Sprite mesh = new Sprite(1, 1);
        Geometry cube = new Geometry("Cube", mesh);
        cube.setMaterial(MaterialUtilities.getUnshadedColorMaterialFromClassPath(assetManager, ColorRGBA.randomColor()));

        @SuppressWarnings("unchecked")
        Dyn4jAppState<PhysicsBody2D> dyn4jAppState = stateManager.getState(Dyn4jAppState.class);
        RigidBody2D body2D = new RigidBody2D();

        body2D.addFixture(GeometryUtilities.createRectangle(1, 1));
        body2D.setMass(MassType.NORMAL);

        body2D.translate(0, 2);
        cube.addControl(body2D);

        dyn4jAppState.getPhysicsSpace().addBody(body2D);
        rootNode.attachChild(cube);
    }
}
