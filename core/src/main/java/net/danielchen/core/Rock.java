package net.danielchen.core;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import java.util.ArrayList;
import java.util.List;

public class Rock extends Entity {
    private final double size;
    private double rotationSpeed;
    private double speed;
    private int lifetime = Integer.MAX_VALUE;
    boolean isDebris;

    public Rock(Game game, Vec2 center, double size) {
        this(game, center, size, false);
    }

    public Rock(Game game, Vec2 center, double size, boolean isDebris) {
        super("ROCK", game);
        this.size = size;
        this.isDebris = isDebris;
        this.setPrimaryBody(this.createRock(game, center));
        this.speed = (this.rand.nextDouble() + 1) * Config.BASE_ROCK_SPEED;
        this.rotationSpeed = (this.rand.nextDouble() + 0.5) * Math.PI / 20;
        this.travelAngle = (float) (this.rand.nextGaussian() * 2.0 * Math.PI);
    }

    public Rock(Rock parent, Entity collider, double angle) {
        super("ROCK", parent.game);
        this.isDebris = false;
        this.size = parent.size * (this.rand.nextDouble() + 0.5) * 0.5;
        Vec2 center = parent.primaryBody.getCenter().clone();
        center.x +=
                0.2 * this.rand.nextGaussian() * (parent.size + this.size);
        center.y +=
                0.2 * this.rand.nextGaussian() * (parent.size + this.size);
        this.setPrimaryBody(this.createRock(this.game, center));
        this.speed = parent.speed * (this.rand.nextGaussian() * 0.1 + 0.8);
        this.rotationSpeed =
                parent.rotationSpeed * (this.rand.nextGaussian() * 0.1 + 0.8);
        this.travelAngle = collider.travelAngle + (float) angle;
    }

    @Override
    public void update() {
        this.dx = (float) (this.speed * Math.cos(this.travelAngle));
        this.dy = (float) (this.speed * Math.sin(this.travelAngle));
        this.rotateBody(this.rotationSpeed);
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
            angle += (this.rand.nextDouble() + 0.75) * Math.PI / numPoints +
                    Math.PI / (3 * (double) numPoints / 2);
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
    public int getContactDelay(Entity other) {
        if (other instanceof Rock)
            return 2;
        if (other instanceof Ship)
            return 3;
        return super.getContactDelay(other);
    }

    @Override
    public void contact(Entity other, Body myBody, Body otherBody) {
        if (this.isDebris)
            return;
        if (other instanceof Powerup)
            return;

        // If the rock is marked immortal, we don't process it further,
        if (this.immortalTime > 0)
            return;

        // Handle collisions with other rocks to simulate bouncing-off effect.
        if (other instanceof Rock) {
            Rock otherRock = (Rock) other;
            if (otherRock.isDebris)
                return;
            Vec2 p1 = myBody.getWorldCenter();
            Vec2 p2 = otherBody.getWorldCenter();
            double dy = p1.y - p2.y + this.rand.nextGaussian() * 0.0005;
            double dx = p1.x - p2.x + this.rand.nextGaussian() * 0.0005;
            this.travelAngle = (float) Math.atan2(dy, dx);
            other.travelAngle = this.travelAngle + (float) Math.PI;
            double avgSpeed = (this.speed + otherRock.speed) * 0.5;
            this.speed = avgSpeed;
            otherRock.speed = avgSpeed;
            double avgRotSpeed =
                    (this.rotationSpeed + otherRock.rotationSpeed) * 0.5;
            this.rotationSpeed = avgRotSpeed;
            otherRock.rotationSpeed = avgRotSpeed;
            return;
        }

        // Spawn some debris to simulate explosion.
        int numDebris = this.rand.nextInt(3) + 3;
        for (int i = 0; i < numDebris; i++) {
            Rock debris = new Rock(this.game,
                    this.primaryBody.getCenter().clone(), this.size * 0.1,
                    true);
            debris.lifetime = 5 + (int) (this.rand.nextGaussian() * 2);
            debris.speed = 0.02 + this.rand.nextGaussian() * 0.01;
            this.game.addEntity(debris);
        }

        // Spawn some smaller rocks to simulate breaking up the bigger rock.
        if (this.size > Config.MIN_ROCK_BREAK_UP_SIZE) {
            int numChildren = this.rand.nextInt(3) + 2;
            for (int i = 0; i < numChildren; i++) {
                Rock child = new Rock(this, other,
                        this.rand.nextGaussian() * Math.PI * 0.5 * i);
                this.game.addEntity(child);
            }
        }

        // Chance to drop powerups for the player, which decreases as the number
        // of total game objects increase.
        for (Powerup.Type powerup : Powerup.Type.values()) {
            if (this.rand.nextDouble() <
                    powerup.spawnRate / this.game.world.getBodyCount()) {
                this.game.addEntity(
                        new Powerup(this.game,
                                this.primaryBody.getCenter().clone(),
                                powerup));
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
