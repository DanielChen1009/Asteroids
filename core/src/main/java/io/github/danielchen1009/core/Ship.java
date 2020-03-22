package io.github.danielchen1009.core;

import java.util.ArrayList;
import java.util.Arrays;

public class Ship extends Entity {
    private static final double SIZE = 0.05;
    private static final double ANGLE = Math.PI / 3;
    double acceleration;

    public Ship(Point center) {
        super(center, new ArrayList<>(Arrays.asList(
                new Point(SIZE, 0),
                new Point(SIZE * Math.sin(4 * ANGLE), SIZE * Math.cos(4 * ANGLE)),
                new Point(SIZE * Math.sin(-ANGLE), SIZE * Math.cos(-ANGLE)))));
    }

    @Override
    public void update() {
        //acceleration -= speed * speed * 0.014;
        speed += acceleration;
        if (speed < 0) speed = 0;
        if (speed > 0.05) speed = 0.05;
        super.update();
    }

    @Override
    public void rotateTravel(double angle) {
        super.rotateTravel(angle);
        super.rotateBody(angle);
    }

    public void accelerate(double a) {
        this.acceleration = a;
    }

}
