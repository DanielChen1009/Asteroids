package io.github.danielchen1009.core;

import java.util.ArrayList;
import java.util.List;

public class Bullet extends Entity {
    private static final double SPEED = 0.01;
    private static final int LIFETIME = 60;
    private int lifetimeRemaining;

    public Bullet(Point center, double angle) {
        List<Point> primaryBody = new ArrayList<>();
        primaryBody.add(new Point(1.0 / 800, 0));
        primaryBody.add(new Point(-1.0 / 800, 0));
        super.setPrimaryBody(new Body(primaryBody, center));
        super.dx = SPEED * Math.cos(angle);
        super.dy = SPEED * Math.sin(angle);
        lifetimeRemaining = LIFETIME;
    }

    @Override
    public void update() {
        lifetimeRemaining--;
        super.update();
    }

    @Override
    public boolean isActive() {
        return lifetimeRemaining >= 0;
    }
}
