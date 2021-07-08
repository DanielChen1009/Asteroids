package net.danielchen.core;

import java.util.ArrayList;
import java.util.List;

public class Ammo extends Entity {
    private int lifetimeRemaining;
    private double speed;
    private double angle;

    public Ammo(Game game, Point center) {
        super("AMMO", game);
        List<Point> primaryBody = new ArrayList<>();
        // Ammo is just a square powerup.
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
        // Ammo has a limited lifetime and randomly jiggles as it moves.
        if (lifetimeRemaining > 0) lifetimeRemaining--;
        else active = false;
        this.speed *= this.rand.nextGaussian() * 0.01 + 1.0;
        this.angle *= this.rand.nextGaussian() * 0.01 + 1.0;
        this.dx = this.speed * Math.cos(this.angle);
        this.dy = this.speed * Math.sin(this.angle);
        super.update();
    }

    @Override
    public void contact(Entity other) {
        // Ammo doesn't touch any object except ships.
        if (other instanceof Ship) super.contact(other);
    }
}
