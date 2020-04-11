package io.github.danielchen1009.core;

import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;

public class Bullet extends Entity {
    private static final double SPEED = 0.03;
    private static final int LIFETIME = 20;
    private int lifetimeRemaining;
    static int cooldown = 0;

    public Bullet(World world, Point center, double angle) {
        super("BULLET");
        List<Point> primaryBody = new ArrayList<>();
        primaryBody.add(new Point(1.0 / 800, 0));
        primaryBody.add(new Point(-1.0 / 800, 0));
        primaryBody.add(new Point(0, 1.0 / 800));
        primaryBody.add(new Point(0, -1.0 / 800));
        super.setPrimaryBody(new EntityBody(world, primaryBody, center));
        super.dx = SPEED * Math.cos(angle);
        super.dy = SPEED * Math.sin(angle);
        lifetimeRemaining = LIFETIME;

    }

    @Override
    public void update() {
        lifetimeRemaining--;
        active = lifetimeRemaining >= 0;
        super.update();
    }
}
