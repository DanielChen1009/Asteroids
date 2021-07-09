package net.danielchen.core;

import java.util.ArrayList;
import java.util.List;

public class Rock extends Entity {
    private final Point center;
    private final double size;
    private double rotationSpeed;
    private double speed;
    private int lifetime = Integer.MAX_VALUE;
    boolean isDebris = false;

    public Rock(Game game, Point center, double size) {
        super("ROCK", game);
        this.center = center;
        this.size = size;
        this.setPrimaryBody(this.createRock(game));
        this.speed = (this.rand.nextDouble() + 1) * 0.003;
        this.rotationSpeed = (this.rand.nextDouble() + 0.5) * Math.PI / 20;
        this.travelAngle = (float) (this.rand.nextGaussian() * 2.0 * Math.PI);
    }

    public Rock(Rock parent, Entity collider, double angle) {
        super("ROCK", parent.game);
        this.size = parent.size * (this.rand.nextDouble() + 0.5) * 0.5;
        this.center = parent.primaryBody.getCenter().copy();
        this.center.x = this.center.x + 0.2 * this.rand.nextGaussian() * (parent.size + this.size);
        this.center.y = this.center.y + 0.2 * this.rand.nextGaussian() * (parent.size + this.size);
        this.setPrimaryBody(this.createRock(game));
        this.speed = parent.speed * (this.rand.nextGaussian() * 0.1 + 0.8);
        this.rotationSpeed = parent.rotationSpeed * (this.rand.nextGaussian() * 0.1 + 0.8);
        this.travelAngle = collider.travelAngle + (float) angle;
    }

    @Override
    public void update() {
        dx = speed * Math.cos(travelAngle);
        dy = speed * Math.sin(travelAngle);
        this.rotateBody(rotationSpeed);
        super.update();
        if (this.lifetime > 0 && this.lifetime != Integer.MAX_VALUE) this.lifetime--;
        if (this.lifetime <= 0) this.active = false;
    }

    /**
     * Creates a rock with randomized body vertices.
     */
    public EntityBody createRock(Game game) {
        List<Point> body = new ArrayList<>();
        int numPoints = rand.nextInt(4) + 5;
        double angle = 0;
        for (int i = 0; i < numPoints; ++i) {
            angle += (rand.nextDouble() + 0.75) * Math.PI / numPoints + Math.PI / (3 * (double) numPoints / 2);
            double radius = (rand.nextDouble() + 0.75) * size;
            if (radius > size) radius = size;
            if (radius < size / 2) radius = size / 2;
            double x = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;
            body.add(new Point(x, y));
        }
        return new EntityBody(this, game, body, this.center);
    }

    @Override
    public int getContactDelay(Entity other) {
        if (other instanceof Rock) return 2;
        if (other instanceof Ship) return 3;
        return super.getContactDelay(other);
    }

    @Override
    public void contact(Entity other) {
        if (this.immortalTime > 0 || this.isDebris) return;

        if (other instanceof Ammo) return;

        // Handle collisions with other rocks to simulate bouncing-off effect.
        if (other instanceof Rock) {
            Rock otherRock = (Rock) other;
            if (otherRock.isDebris) return;
            Point p1 = this.primaryBody.getCenter();
            Point p2 = other.primaryBody.getCenter();
            double dy = p1.y - p2.y + this.rand.nextGaussian() * 0.0005;
            double dx = p1.x - p2.x + this.rand.nextGaussian() * 0.0005;
            this.travelAngle = (float) Math.atan2(dy, dx);
            other.travelAngle = this.travelAngle + (float) Math.PI;
            double avgSpeed = (this.speed + otherRock.speed) * 0.5;
            this.speed = avgSpeed;
            otherRock.speed = avgSpeed;
            double avgRotSpeed = (this.rotationSpeed + otherRock.rotationSpeed) * 0.5;
            this.rotationSpeed = avgRotSpeed;
            otherRock.rotationSpeed = avgRotSpeed;
            return;
        }

        // Spawn some debris to simulate explosion.
        int numDebris = this.rand.nextInt(5) + 5;
        for (int i = 0; i < numDebris; i++) {
            Rock debris = new Rock(this.game, this.primaryBody.getCenter().copy(), this.size * 0.1);
            debris.lifetime = 5 + (int) (this.rand.nextGaussian() * 2);
            debris.speed = 0.02 + this.rand.nextGaussian() * 0.01;
            debris.isDebris = true;
            this.game.addEntity(debris);
        }

        // Spawn some smaller rocks to simulate breaking up the bigger rock.
        if (this.size > 0.03) {
            int numChildren = this.rand.nextInt(3) + 2;
            for (int i = 0; i < numChildren; i++) {
                Rock child = new Rock(this, other, this.rand.nextGaussian() * Math.PI * 0.5 * i);
                this.game.addEntity(child);
            }
        }

        // Chance to drop ammo for the player, which decreases as the number of game objects increase.
        if (this.rand.nextDouble() < 5.0 / this.game.world.getBodyCount()) {
            this.game.addEntity(new Ammo(this.game, this.primaryBody.getCenter().copy()));
        }

        // This destroys the current rock.
        this.game.score += this.size * 100;
        super.contact(other);
    }
}
