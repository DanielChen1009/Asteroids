package net.danielchen.core;

import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;

public class Bullet extends Entity {
    public static final double SPEED = 0.03;
    private static final int LIFETIME = 20;
    private int lifetimeRemaining;
    static int cooldown = 0;

    public Bullet(Game game, Point center, double angle, double speed) {
        super("BULLET", game);
        List<Point> primaryBody = new ArrayList<>();
        primaryBody.add(new Point(1.0 / 800, 0));
        primaryBody.add(new Point(-1.0 / 800, 0));
        primaryBody.add(new Point(0, 1.0 / 800));
        primaryBody.add(new Point(0, -1.0 / 800));
        this.setPrimaryBody(new EntityBody(this, game, primaryBody, center));
        this.dx = speed * Math.cos(angle);
        this.dy = speed * Math.sin(angle);
        this.lifetimeRemaining = LIFETIME;
    }

    @Override
    public void update() {
        if (lifetimeRemaining > 0) lifetimeRemaining--;
        else active = false;
        super.update();
    }

    @Override
    public void contact(Entity other) {
        if (other instanceof Bullet || other instanceof Ship) return;
        super.contact(other);
    }
}
