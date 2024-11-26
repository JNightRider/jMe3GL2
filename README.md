# jMe3GL2 - [jMonkeyEngine3 Graphics Library 2D]
jMe3GL2 is a set of classes that can be used to develop a 2D game in jMonkeyEngine3. 
It is a mapping library for JME3 to Dyn4J.

To use jMe3GL2, prior knowledge of how the jme3(https://jmonkeyengine.org/) graphics 
engine works, as well as the dyn4J(https://dyn4j.org/)physics engine, is required 
to create wonderful 2D worlds.

**Some features provided by this library:**
1. Creation of 2D models using a *Sprite* mesh.
2. Integration of the Dyn4J engine through *Dyn4jAppState*.
3. Control to manage 2D animations.
4. Support for strongly customized *TileMap*
5. Support for exporting and importing 2D (binary) models.
6. Has a built-in debugger.

## Dyn4j
As mentioned, jMe3GL2 uses Dyn4J as its physics engine. If you are not familiar 
with this topic, we advise you to check out the following resources:

- **[Documentación - Dyn4j](https://dyn4j.org/pages/getting-started)**
- **[Samples - Dyn4j](https://github.com/dyn4j/dyn4j-samples)**
  
## Requirements
- Java 11 or higher.
- jMonkeyEngine3 version 3.7.0-stable.
- Dyn4J version 5.0.2

## Building with jMe3GL2

jMe3GL2 can be added as a normal dependency using the [stable](https://github.com/JNightRider/jMe3GL2/releases/tag/v3.0.0) 
jar files or using 'JitPack' as follows:

**Step 1. Add the JitPack maven repository**

```gradle
    maven { url "https://jitpack.io"  }
```

**Step 2. Add the necessary dependencies**

```gradle
    implementation 'com.github.JNightRider.jMe3GL2:jMe3GL2-core:3.0.0'
    implementation 'com.github.JNightRider.jMe3GL2:jMe3GL2-jawt:3.0.0'
    implementation 'com.github.JNightRider.jMe3GL2:jMe3GL2-plugins:3.0.0'
```
