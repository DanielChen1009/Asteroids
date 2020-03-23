package io.github.danielchen1009.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Rock extends Entity {
    private Point center;
    private double size;
    private double rotationSpeed;
    private double speed;
    private Random rand;

    public Rock(Point center, double size) {
        this.center = center;
        this.size = size;
        rand = new Random();
        this.setPrimaryBody(this.createRock());
        this.speed = (rand.nextDouble() + 0.5) * 0.001;
        this.rotationSpeed = (rand.nextDouble() + 0.5) * Math.PI / 20;
        this.travelAngle = rand.nextDouble() * 2 * Math.PI;
    }

    @Override
    public void update() {
        dx = speed * Math.cos(travelAngle);
        dy = speed * Math.sin(travelAngle);
        this.rotateBody(rotationSpeed);
        super.update();
    }

    public Body createRock() {
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
        return new Body(body, center);
    }


}
