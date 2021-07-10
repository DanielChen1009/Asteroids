package net.danielchen.core;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import java.util.ArrayList;
import java.util.List;

public class Rock extends Entity {
    private final double size;
    private final float rotationSpeed;
    private final float travelAngle;
    private double speed;
    int lifetime = Integer.MAX_VALUE;
    boolean isDebris;

    public Rock(Game game, Vec2 center, double size) {
        this(game, center, size, false);
    }

    public Rock(Game game, Vec2 center, double size, boolean isDebris) {
        super(Type.ROCK, game);
        this.size = size;
        this.isDebris = isDebris;
        this.setPrimaryBody(this.createRock(game, center));
        this.speed = (this.rand.nextFloat() + 1) * Config.BASE_ROCK_SPEED;
        this.rotationSpeed = (float) ((this.rand
                .nextFloat() + 0.5) * Math.PI / 20);
        this.travelAngle = (float) (this.rand.nextGaussian() * 2.0 * Math.PI);
        this.setVelocities();
    }

    public Rock(Rock parent, Entity collider, double angle) {
        super(Type.ROCK, parent.game);
        this.isDebris = false;
        this.size = parent.size * (this.rand.nextDouble() + 0.5) * 0.5;
        Vec2 center = parent.primaryBody.getCenter().clone();
        center.x += 0.2 * this.rand.nextGaussian() * (parent.size + this.size);
        center.y += 0.2 * this.rand.nextGaussian() * (parent.size + this.size);
        this.setPrimaryBody(this.createRock(this.game, center));
        this.speed = parent.speed * (this.rand.nextGaussian() * 0.1 + 0.8);
        this.rotationSpeed = parent.rotationSpeed * (float) (this.rand
                .nextGaussian() * 0.1 + 0.8);
        this.travelAngle = collider.primaryBody.getAngle() + (float) angle;
        this.setVelocities();
    }

    private void setVelocities() {
        this.setAngularVelocity(this.rotationSpeed);
        float vx = (float) (this.speed * Math.cos(this.travelAngle));
        float vy = (float) (this.speed * Math.sin(this.travelAngle));
        this.setLinearVelocity(new Vec2(vx, vy));
    }

    @Override
    public void update() {
        super.update();
        if (this.lifetime > 0 && this.lifetime != Integer.MAX_VALUE)
            this.lifetime--;
        if (this.lifetime <= 0)
            this.active = false;
    }

    /**
     * Creates a rock with randomized body vertices.
     */
    public EntityBody createRock(Game game, Vec2 center) {
        List<Vec2> body = new ArrayList<>();
        int numPoints = this.rand.nextInt(4) + 5;
        double angle = 0;
        for (int i = 0; i < numPoints; ++i) {
            angle += (this.rand
                    .nextDouble() + 0.75) * Math.PI / numPoints + Math.PI / (3 * (double) numPoints / 2);
            double radius = (this.rand.nextDouble() + 0.75) * this.size;
            if (radius > this.size)
                radius = this.size;
            if (radius < this.size / 2)
                radius = this.size / 2;
            double x = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;
            body.add(new Vec2((float) x, (float) y));
        }
        return new EntityBody(this, game, body, center.clone(), !this.isDebris);
    }

    @Override
    public boolean shouldCollide() {
        return !this.isDebris;
    }

    @Override
    public void contact(Entity other, Body myBody, Body otherBody) {
        if (this.isDebris)
            return;
        if (other instanceof Powerup || other instanceof Rock)
            return;
        // If the rock is marked immortal, we don't process it further,
        if (this.immortalTime > 0)
            return;

        // At this point, the rock is contacted something that will cause it to
        // blow up.

        // Spawn some debris to simulate explosion.
        int numDebris = this.rand.nextInt(4) + 3;
        for (int i = 0; i < numDebris; i++) {
            Rock debris = new Rock(this.game,
                                   this.primaryBody.getCenter().clone(),
                                   this.size * 0.1, true);
            debris.lifetime = Config.DEBRIS_LIFETIME + (int) (this.rand
                    .nextGaussian() * Config.DEBRIS_LIFETIME / 5);
            debris.speed = this.primaryBody.getLinearVelocity()
                    .length() * 3 + this.rand
                    .nextGaussian() * Config.BASE_ROCK_SPEED / 10;
            this.game.addEntity(debris);
        }

        // Spawn some smaller rocks to simulate breaking up the bigger rock.
        if (this.size > Config.MIN_ROCK_BREAK_UP_SIZE) {
            int numChildren = this.rand.nextInt(2) + 1;
            for (int i = 0; i < numChildren; i++) {
                Rock child = new Rock(this, other, this.rand
                        .nextGaussian() * Math.PI * 0.5 * i);
                this.game.addEntity(child);
            }
        }

        // Chance to drop powerups for the player, which decreases as the number
        // of total game objects increase.
        for (Powerup.Type powerup : Powerup.Type.values()) {
            if (this.rand.nextDouble() < powerup.spawnRate / this.game.world
                    .getBodyCount()) {
                this.game.addEntity(new Powerup(this.game,
                                                this.primaryBody.getCenter()
                                                        .clone(), powerup));
            }
        }

        // This destroys the current rock.
        this.game.score += this.size * 100;
        super.contact(other, myBody, otherBody);
    }

    @Override
    public String toString() {
        return super.toString() + (this.isDebris ? " (debris)" : "");
    }
}
