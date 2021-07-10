package net.danielchen.core;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import java.util.ArrayList;
import java.util.List;

public class Bullet extends Entity {
    private int lifetimeRemaining;
    private final Vec2 direction;

    public Bullet(Game game, Vec2 center, float angle, float speed) {
        super(Type.BULLET, game);
        List<Vec2> primaryBody = new ArrayList<>();
        primaryBody.add(new Vec2(Config.BULLET_SIZE, 0));
        primaryBody.add(new Vec2(-Config.BULLET_SIZE, 0));
        primaryBody.add(new Vec2(0, Config.BULLET_SIZE));
        primaryBody.add(new Vec2(0, -Config.BULLET_SIZE));
        this.setPrimaryBody(new EntityBody(this, game, primaryBody, center));
        this.primaryBody.setTransform(center, angle);
        this.direction = new Vec2((float) (speed * Math.cos(angle)),
                                  (float) (speed * Math.sin(angle)));
        this.lifetimeRemaining = Config.BULLET_LIFETIME;
        this.setLinearVelocity(this.direction);
    }

    /**
     * This constructor is for Mega bullet.
     */
    public Bullet(Game game, Vec2 center, float angle) {
        super(Type.BULLET, game);
        List<Vec2> primaryBody = new ArrayList<>();
        primaryBody.add(new Vec2(Config.MEGABULLET_SIZE, 0));
        primaryBody.add(new Vec2(-Config.MEGABULLET_SIZE, 0));
        primaryBody.add(new Vec2(0, Config.MEGABULLET_SIZE));
        primaryBody.add(new Vec2(0, -Config.MEGABULLET_SIZE));
        this.setPrimaryBody(new EntityBody(this, game, primaryBody, center));
        this.primaryBody.setTransform(center, angle);
        this.direction = new Vec2(
                Config.MEGABULLET_SPEED * (float) Math.cos(angle),
                Config.MEGABULLET_SPEED * (float) Math.sin(angle));
        this.lifetimeRemaining = Config.MEGABULLET_LIFETIME;
        this.setLinearVelocity(this.direction);
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
        if (other instanceof Bullet || other instanceof Ship || other instanceof Powerup)
            return;
        super.contact(other, myBody, otherBody);
    }
}
