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
    public static double SHIP_ACCELERATION = 0.0003;

    // The amount of ammo the player starts with.
    public static int INITIAL_AMMO = 50;

    // The amount of additional ammo the player gets through powerup.
    public static int POWERUP_AMMO_INCREASE = 20;

    // The firing cooldown for the main gun, in # frames.
    public static int COOLDOWN = 4;

    // Threshold for what is considered low ammo and the spawn rate for
    // new ammo powerups.
    public static int LOW_AMMO_THRESHOLD = 10;
    public static double LOW_AMMO_SPAWN_RATE = 0.01;

    public static float BULLET_SIZE = 1.0f / 800;
    public static float BULLET_SPEED = 0.03f;
    public static int BULLET_LIFETIME = 18;

    public static float MEGABULLET_SIZE = 1.0f / 200;
    public static float MEGABULLET_SPEED = 0.05f;
    public static int MEGABULLET_LIFETIME = 30;

    public static int MEGAGUN_SHOTS = 5;
    public static float MEGAGUN_SPREAD = 0.1f;

    /**
     * Powerup related config.
     */
    public static float POWERUP_SIZE = 0.01f;
    public static int POWERUP_LIFETIME = 500;

    // The distance at which the powerup starts getting attracted towards the
    // player ship.
    public static double POWERUP_ATTRACT_DISTANCE = 0.15;

    /**
     * Physics related config.
     */
    // The step size in the world physics simulation.
    public static float WORLD_STEP_DT = 1f / 1000f;

    // The max number of contacts outstanding in the world where we
    // still perform contact delay for more realistic collisions.
    public static int MAX_CONTACTS_FOR_DELAY = 100;

    /**
     * Entity generation related config.
     */
    // The higher this number is, the more rocks/stuff will spawn over time.
    public static double SPAWN_RATE = 0.05;

    // The higher this number is, the more ammo powerup will spawn.
    public static double AMMO_SPAWN_RATE = 2.0;

    // The higher this number is, the more invincible powerup will spawn.
    public static double INVINCIBLE_SPAWN_RATE = 0.5;

    // The higher this number is, the more megagun powerup will spawn.
    public static double MEGAGUN_SPAWN_RATE = 0.2;

    // The average rock size that is randomly generated.
    public static double BASE_ROCK_SIZE = 0.05;

    // The average rock speed that is randomly generated.
    public static double BASE_ROCK_SPEED = 0.003;

    // The minimum size a rock can be in order to spawn smaller rocks upon
    // being shot.
    public static double MIN_ROCK_BREAK_UP_SIZE = 0.03;
}
