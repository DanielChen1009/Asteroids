package net.danielchen.core;

import org.jbox2d.dynamics.Body;

import java.util.ArrayList;
import java.util.List;

public class Bullet extends Entity {
    private int lifetimeRemaining;
    static int cooldown = 0;

    public Bullet(Game game, Point center, double angle, double speed) {
        super("BULLET", game);
        List<Point> primaryBody = new ArrayList<>();
        primaryBody.add(new Point(Config.BULLET_SIZE, 0));
        primaryBody.add(new Point(-Config.BULLET_SIZE, 0));
        primaryBody.add(new Point(0, Config.BULLET_SIZE));
        primaryBody.add(new Point(0, -Config.BULLET_SIZE));
        this.setPrimaryBody(new EntityBody(this, game, primaryBody, center));
        this.dx = speed * Math.cos(angle);
        this.dy = speed * Math.sin(angle);
        this.lifetimeRemaining = Config.BULLET_LIFETIME;
    }

    /**
     * This constructor is for Mega bullet.
     */
    public Bullet(Game game, Point center, double angle) {
        super("BULLET", game);
        List<Point> primaryBody = new ArrayList<>();
        primaryBody.add(new Point(Config.MEGABULLET_SIZE, 0));
        primaryBody.add(new Point(-Config.MEGABULLET_SIZE, 0));
        primaryBody.add(new Point(0, Config.MEGABULLET_SIZE));
        primaryBody.add(new Point(0, -Config.MEGABULLET_SIZE));
        this.setPrimaryBody(new EntityBody(this, game, primaryBody, center));
        this.dx = Config.MEGABULLET_SPEED * Math.cos(angle);
        this.dy = Config.MEGABULLET_SPEED * Math.sin(angle);
        this.lifetimeRemaining = Config.MEGABULLET_LIFETIME;
    }

    @Override
    public void update() {
        if (this.lifetimeRemaining > 0)
            this.lifetimeRemaining--;
        else
            this.active = false;
        super.update();
    }

    @Override
    public void contact(Entity other, Body myBody, Body otherBody) {
        if (other instanceof Bullet || other instanceof Ship ||
                other instanceof Powerup)
            return;
        super.contact(other, myBody, otherBody);
    }
}
