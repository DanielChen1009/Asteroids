package net.danielchen.core;

import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Rock extends Entity {
    private final Point center;
    private final double size;
    private final double rotationSpeed;
    private final double speed;

    public Rock(Game game, Point center, double size) {
        super("ROCK", game);
        this.center = center;
        this.size = size;
        this.setPrimaryBody(this.createRock(game));
        this.speed = (this.rand.nextDouble() + 1) * 0.003;
        this.rotationSpeed = (this.rand.nextDouble() + 0.5) * Math.PI / 20;
        this.travelAngle = this.rand.nextFloat() * 2 * (float) Math.PI;
    }

    public Rock(Rock parent, Entity collider, double angle) {
        super("ROCK", parent.game);
        this.center = parent.primaryBody.getCenter().copy();
        this.size = parent.size * 0.5;
        this.setPrimaryBody(this.createRock(game));
        this.speed = parent.speed * 0.8;
        this.rotationSpeed = parent.rotationSpeed * 0.8;
        this.travelAngle = collider.travelAngle + (float) angle;
    }

    @Override
    public void update() {
        dx = speed * Math.cos(travelAngle);
        dy = speed * Math.sin(travelAngle);
        this.rotateBody(rotationSpeed);
        super.update();
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
    public void contact(Entity other) {
        if (other instanceof Rock || this.immortalTime > 0) return;
        super.contact(other);
        if (this.size > 0.03) {
            int numChildren = this.rand.nextInt(3) + 2;
            for (int i = 0; i < numChildren; i++) {
                this.game.addEntity(new Rock(this, other, (rand.nextFloat() - 0.5f) * Math.PI * 0.5 * i));
            }
        }
    }
}
