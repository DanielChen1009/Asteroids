package net.danielchen.core;

import java.util.ArrayList;
import java.util.List;

public class Powerup extends Entity {
    enum Type {
        AMMO(0xFF2288AA);

        Type(int color) {
            this.color = color;
        }
        int color;
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
        double size = -1.0 / 100;
        primaryBody.add(new Point(-size, -size));
        primaryBody.add(new Point(-size, size));
        primaryBody.add(new Point(size, size));
        primaryBody.add(new Point(size, -size));
        this.setPrimaryBody(new EntityBody(this, game, primaryBody, center));
        this.speed = this.rand.nextGaussian() * 0.001 + 0.005;
        this.angle = this.rand.nextGaussian() * Math.PI * 2.0;
        this.lifetimeRemaining = 500;
    }

    @Override
    public void update() {
        // Powerup has a limited lifetime and randomly jiggles as it moves.
        if (lifetimeRemaining > 0) lifetimeRemaining--;
        else active = false;
        this.speed *= this.rand.nextGaussian() * 0.01 + 1.0;
        this.angle *= this.rand.nextGaussian() * 0.01 + 1.0;

        // Make the powerup attract towards the ship at a certain distance.
        Point p1 = this.primaryBody.getCenter();
        Point p2 = this.game.ship.primaryBody.getCenter();
        if (this.game.ship.isActive() && p1.distance(p2) < 0.1) {
            double v = this.rand.nextGaussian() * 0.002 + 0.01;
            this.dx += v * (p2.x - p1.x);
            this.dy += v * (p2.y - p1.y);
        } else {
            this.dx = this.speed * Math.cos(this.angle);
            this.dy = this.speed * Math.sin(this.angle);
        }
        super.update();
    }

    @Override
    public void contact(Entity other) {
        // Powerup doesn't touch any object except ships.
        if (other instanceof Ship) super.contact(other);
    }
}
