# jMe3GL2 - (jMonkeyEngine3 Graphics Library 2D)

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jnightrider/jMe3GL2-core)](https://central.sonatype.com/search?q=jMe3GL2&namespace=io.github.jnightrider)
[![License: BSD](https://img.shields.io/badge/License-BSD--3-yel)](https://opensource.org/license/BSD-3-clause)

jMe3GL2 is a set of classes that can be used to develop a 2D game in jMonkeyEngine3. 
It is a mapping library for JME3 to Dyn4J.

![jMe3GL2-Sample](./modules/samples/src/main/resources/Textures/jme3gl2-sample.jpeg)

To use jMe3GL2, prior knowledge of how the [JME3](https://jmonkeyengine.org/) graphics 
engine works, as well as the [dyn4j](https://dyn4j.org/) physics engine, is required 
to create wonderful 2D worlds.

**Some features provided by this library:**
1. Creation of 2D models using a *Sprite* mesh.
2. Integration with physics engines (**Box2D** and **Dyn4j**)
3. Control to manage 2D animations.
4. Support for strongly customized *TileMap*
5. Support for exporting and importing 2D (binary) models.
6. Has a built-in debugger.

## How to use Box2D-JNI in projects

[Go to the test example](./modules/samples/src/main/java/org/je3gl/demo/core/Sprite2D.java)

[Some examples of games](https://github.com/JNightRider/jMe3GL2-examples)

## External links

[jMonkeyEngine3 graphics engine](https://jmonkeyengine.org/)

[Official Dyn4j repository on GitHub](https://github.com/dyn4j/dyn4j)

[Official Dyn4j documentation](https://dyn4j.org/pages/getting-started)

[Official Box2D-JNI repository on GitHub](https://github.com/JNightRider/box2d-jni)

[Official Box2D documentation](https://box2d.org/documentation/)

## Building with jMe3GL2

jMe3GL2 can be added as a normal dependency using the [stable](https://github.com/JNightRider/jMe3GL2/releases/tag/v3.1.1) 
jar files or using Maven as follows:

**1. Add the necessary dependencies**

```gradle
project.ext.jme3gl2Version = "3.2.0"

dependencies {
    implementation("io.github.jnightrider:jMe3GL2-core:$jme3gl2Version")
    implementation("io.github.jnightrider:jMe3GL2-dyn4j:$jme3gl2Version")
    implementation("io.github.jnightrider:jMe3GL2-box2d:$jme3gl2Version")
    implementation("io.github.jnightrider:jMe3GL2-jawt:$jme3gl2Version")
    implementation("io.github.jnightrider:jMe3GL2-plugins:$jme3gl2Version")
}
```

**2. Starting jMe3GL2**

If this is your first time using jMe3GL2, you can consult some of these resources 
to guide you in using this great library.

- [Complete examples](https://github.com/JNightRider/jMe3GL2-examples)
- [Go to tutorial](https://github.com/JNightRider/jMe3GL2-examples/wiki)

