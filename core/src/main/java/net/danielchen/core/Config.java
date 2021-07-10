package net.danielchen.core;

public class Config {
    /**
     * For debugging purposes only.
     */
    public static boolean GOD_MODE = false;

    /**
     * GUI related config.
     */
    // Default entity colors
    public static int SHIP_COLOR = 0xFF00FF00;
    public static int BULLET_COLOR = 0xFF2288AA;
    public static int MEGABULLET_COLOR = 0xFFFF88AA;
    public static int ROCK_COLOR = 0xFFAAAAAA;
    public static int UNKNOWN_COLOR = 0xFFFFFFFF;

    // Line width to draw with.
    public static float LINE_WIDTH = 2;

    // The vertical gap size between the ship's message string and the ship.
    public static float SHIP_MESSAGE_VERTICAL_OFFSET = 0.038f;

    /**
     * Player related config.
     */
    public static final float SHIP_SIZE = 0.025f;
    public static final float SHIP_ANGLE = (float) (Math.PI / 3.0);

    // The acceleration of the ship when spacebar is pressed/held.
    public static float SHIP_ACCELERATION = 15f;

    // The turn speed of the ship when arrow keys are pressed/held.
    public static float SHIP_TURN_SPEED = 250;

    // The acceleration of the ship when mouse/touch is pressed/held.
    public static float SHIP_POINTER_ACCELERATION = 200;

    // The turn speed of the ship when mouse/touch is pressed/held.
    public static float SHIP_POINTER_TURN_SPEED = 400;

    // The max speed the ship can reach.
    public static float SHIP_MAX_SPEED = 20;

    // The amount of ammo the player starts with.
    public static int INITIAL_AMMO = 100;

    // The amount of additional ammo the player gets through powerup.
    public static int POWERUP_AMMO_INCREASE = 25;

    // The firing cooldown for the main gun, in # frames.
    public static int COOLDOWN = 4;

    // Threshold for what is considered low ammo and the spawn rate for
    // new ammo powerups.
    public static int LOW_AMMO_THRESHOLD = 10;
    public static double LOW_AMMO_SPAWN_RATE = 0.01;

    public static float BULLET_SIZE = 1.0f / 500;
    public static float BULLET_SPEED = 38f;
    public static int BULLET_LIFETIME = 18;

    public static float MEGABULLET_SIZE = BULLET_SIZE * 4;
    public static float MEGABULLET_SPEED = BULLET_SPEED * 1.2f;
    public static int MEGABULLET_LIFETIME = (int) (BULLET_LIFETIME * 1.2);

    public static int MEGAGUN_SHOTS = 5;
    public static float MEGAGUN_SPREAD = 0.1f;

    /**
     * Powerup related config.
     */
    public static float POWERUP_SIZE = 0.01f;
    public static int POWERUP_LIFETIME = 500;
    public static float BASE_POWERUP_SPEED = 3;
    public static float MAX_POWERUP_SPEED = 6;

    // The distance at which the powerup starts getting attracted towards the
    // player ship.
    public static double POWERUP_ATTRACT_DISTANCE = 0.18;

    // The force at which the powerup is attracted to the player.
    public static double POWERUP_ATTRACT_FORCE = 3000;

    /**
     * Physics related config.
     */
    // The step size in the world physics simulation.
    public static float WORLD_STEP_DT = 1f / 1000f;

    // The scale that the physics world runs on, instead of [0, 1] coordinates
    // it would be [0, WORLD_SCALE].
    public static float WORLD_SCALE = 10.0f;

    /**
     * Entity generation related config.
     */
    // The number of initial rocks.
    public static int SPAWN_INITIAL = 3;

    // The higher this number is, the more rocks/stuff will spawn over time.
    public static double SPAWN_RATE = 0.03;

    // The higher this number is, the more ammo powerup will spawn.
    public static double AMMO_SPAWN_RATE = 2.0;

    // The higher this number is, the more invincible powerup will spawn.
    public static double INVINCIBLE_SPAWN_RATE = 0.3;

    // The higher this number is, the more megagun powerup will spawn.
    public static double MEGAGUN_SPAWN_RATE = 0.12;

    // The higher this number is, the more exetra lif powerup will spawn.
    public static double EXTRA_LIFE_SPAWN_RATE = 0.3;

    // The average rock size that is randomly generated.
    public static double BASE_ROCK_SIZE = 0.05;

    // The average rock speed that is randomly generated.
    public static double BASE_ROCK_SPEED = 3;

    // The minimum size a rock can be in order to spawn smaller rocks upon
    // being shot.
    public static double MIN_ROCK_BREAK_UP_SIZE = 0.04;

    // The maximum number of rocks that should exist on-screen.
    public static int MAX_ROCKS = 50;

    // How long debris from broke rocks stay on-screen.
    public static int DEBRIS_LIFETIME = 20;
}
