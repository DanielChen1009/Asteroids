package net.danielchen.core;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import java.util.ArrayList;
import java.util.List;

public class Powerup extends Entity {
    // The type of this powerup with associated powerup-specific parameters.
    enum Type {
        AMMO(0xFF2288AA, 20, "+" + Config.POWERUP_AMMO_INCREASE + " ammo",
                false, Config.AMMO_SPAWN_RATE),
        INVINCIBLE(0xFFFFFF00, 200, "invincible", true,
                Config.INVINCIBLE_SPAWN_RATE),
        MEGAGUN(0xFFFF2222, 200, "megagun", true, Config.MEGAGUN_SPAWN_RATE),
        EXTRA_LIFE(0xFF00FF00, 20, "extra life!", false,
                Config.EXTRA_LIFE_SPAWN_RATE);

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

    public Powerup(Game game, Vec2 center, Type type) {
        super(Entity.Type.POWERUP, game);
        this.type = type;
        List<Vec2> primaryBody = new ArrayList<>();
        // All powerups are squares.
        float size = Config.POWERUP_SIZE;
        primaryBody.add(new Vec2(-size, -size));
        primaryBody.add(new Vec2(-size, size));
        primaryBody.add(new Vec2(size, size));
        primaryBody.add(new Vec2(size, -size));
        this.setPrimaryBody(new EntityBody(this, game, primaryBody, center));
        this.speed = this.rand
                .nextGaussian() * Config.BASE_POWERUP_SPEED / 10 + Config.BASE_POWERUP_SPEED;
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
        Vec2 p1 = this.primaryBody.getCenter();
        Vec2 p2 = this.game.ship.primaryBody.getCenter();
        if (this.game.ship.active && MathUtils
                .distance(p1, p2) < Config.POWERUP_ATTRACT_DISTANCE) {
            float v = (float) (this.rand
                    .nextGaussian() * Config.POWERUP_ATTRACT_FORCE / 10 + Config.POWERUP_ATTRACT_FORCE);
            this.applyForce(new Vec2(p2.x - p1.x, p2.y - p1.y).mul(v));
        }
        else if (this.primaryBody.getLinearVelocity()
                .length() < Config.MAX_POWERUP_SPEED) {
            float dx = (float) (this.speed * Math.cos(this.angle));
            float dy = (float) (this.speed * Math.sin(this.angle));
            this.applyForce(new Vec2(dx, dy));
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
