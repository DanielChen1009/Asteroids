package net.danielchen.core;

import org.jbox2d.dynamics.Body;

import java.util.ArrayList;
import java.util.List;

public class Powerup extends Entity {
    // The type of this powerup with associated powerup-specific parameters.
    enum Type {
        AMMO(0xFF2288AA, 20, "+" + Config.POWERUP_AMMO_INCREASE + " ammo",
                false,
                Config.AMMO_SPAWN_RATE),
        INVINCIBLE(0xFFFFFF00, 200, "invincible", true,
                Config.INVINCIBLE_SPAWN_RATE),
        MEGAGUN(0xFFFF2222, 200, "megagun", true, Config.MEGAGUN_SPAWN_RATE);

        Type(int color, int time, String message, boolean showTime,
             double spawnRate) {
            this.color = color;
            this.time = time;
            this.message = message;
            this.showTime = showTime;
            this.spawnRate = spawnRate;
        }

        int color;
        int time;
        String message; // Message to show the user on the UI about the powerup.
        boolean showTime; // Whether to show the remaining time on the UI.
        double spawnRate;
    }

    private int lifetimeRemaining;
    private double speed;
    private double angle;
    Type type;

    public Powerup(Game game, Point center, Type type) {
        super("POWERUP", game);
        this.type = type;
        List<Point> primaryBody = new ArrayList<>();
        // All powerups are squares.
        double size = Config.POWERUP_SIZE;
        primaryBody.add(new Point(-size, -size));
        primaryBody.add(new Point(-size, size));
        primaryBody.add(new Point(size, size));
        primaryBody.add(new Point(size, -size));
        this.setPrimaryBody(new EntityBody(this, game, primaryBody, center));
        this.speed = this.rand.nextGaussian() * 0.001 + 0.005;
        this.angle = this.rand.nextGaussian() * Math.PI * 2.0;
        this.lifetimeRemaining = Config.POWERUP_LIFETIME;
    }

    @Override
    public void update() {
        // Powerup has a limited lifetime and randomly jiggles as it moves.
        if (this.lifetimeRemaining > 0)
            this.lifetimeRemaining--;
        else
            this.active = false;
        this.speed *= this.rand.nextGaussian() * 0.01 + 1.0;
        this.angle *= this.rand.nextGaussian() * 0.01 + 1.0;

        // Make the powerup attract towards the ship at a certain distance.
        Point p1 = this.primaryBody.getCenter();
        Point p2 = this.game.ship.primaryBody.getCenter();
        if (this.game.ship.isActive() &&
                p1.distance(p2) < Config.POWERUP_ATTRACT_DISTANCE) {
            double v = this.rand.nextGaussian() * 0.003 + 0.02;
            this.dx += v * (p2.x - p1.x);
            this.dy += v * (p2.y - p1.y);
        } else {
            this.dx = this.speed * Math.cos(this.angle);
            this.dy = this.speed * Math.sin(this.angle);
        }
        super.update();
    }

    @Override
    public void contact(Entity other, Body myBody, Body otherBody) {
        // Powerup doesn't touch any object except ships.
        if (other instanceof Ship)
            super.contact(other, myBody, otherBody);
    }
}
