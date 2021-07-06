package net.danielchen.java;

import net.danielchen.core.Asteroids;
import playn.java.LWJGLPlatform;

public class AsteroidsJava {

    public static void main(String[] args) {
        LWJGLPlatform.Config config = new LWJGLPlatform.Config();
        config.width = 800;
        config.height = 800;
        // use config to customize the Java platform, if needed
        LWJGLPlatform plat = new LWJGLPlatform(config);
        new Asteroids(plat);
        plat.start();
    }
}
