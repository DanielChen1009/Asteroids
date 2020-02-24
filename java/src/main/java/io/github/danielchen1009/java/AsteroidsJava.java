package io.github.danielchen1009.java;

import io.github.danielchen1009.core.Asteroids;
import playn.java.LWJGLPlatform;

public class AsteroidsJava {

    public static void main(String[] args) {
        LWJGLPlatform.Config config = new LWJGLPlatform.Config();
        // use config to customize the Java platform, if needed
        LWJGLPlatform plat = new LWJGLPlatform(config);
        new Asteroids(plat);
        plat.start();
    }
}
