package io.github.danielchen1009.core;

import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Rock extends Entity {
    private Point center;
    private double size;
    private double rotationSpeed;
    private double speed;
    private Random rand;

    public Rock(World world, Point center, double size) {
        super("ROCK");
        this.center = center;
        this.size = size;
        rand = new Random();
        this.setPrimaryBody(this.createRock(world));
        this.speed = (rand.nextDouble() + 1) * 0.001;
        this.rotationSpeed = (rand.nextDouble() + 0.5) * Math.PI / 20;
        this.travelAngle = rand.nextFloat() * 2 * (float) Math.PI;
    }

    @Override
    public void update() {
        dx = speed * Math.cos(travelAngle);
        dy = speed * Math.sin(travelAngle);
        this.rotateBody(rotationSpeed);
        super.update();
    }

    public EntityBody createRock(World world) {
        List<Point> body = new ArrayList<>();
        int numPoints = rand.nextInt(4) + 5;
        double angle = 0;
        for (int i = 0; i < numPoints; ++i) {
            angle += (rand.nextDouble() + 0.5) * Math.PI / numPoints + Math.PI / (3 * (double) numPoints / 2);
            double radius = (rand.nextDouble() + 0.5) * size;
            if (radius > size) radius = size;
            if (radius < size / 2) radius = size / 2;
            double x = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;
            body.add(new Point(x, y));
        }
        return new EntityBody(world, body, center);
    }


}