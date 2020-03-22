package io.github.danielchen1009.core;

public class Game {
    boolean isTransitioningR, isTransitioningL, isTransitioningU, isTransitioningD, isTransitioning;
    Ship transitionShip;
    private boolean isAccelerating;
    private boolean isTurningLeft;
    private boolean isTurningRight;
    private Ship ship;
    private int width, height;
    private double v;

    public Game(int width, int height) {
        this.width = width;
        this.height = height;
        Point startPoint = new Point(0.5, 0.5);
        this.ship = new Ship(startPoint);
    }

    public Ship getShip() {
        return ship;
    }

    public void update() {
        ship.update();
    }

    public void setAccelerating(boolean accelerating) {
        ship.accelerate(accelerating ? 0.0005 : -0.0005);
    }

    public void setTurningLeft() {
        ship.rotateTravel(-0.07);
    }

    public void setTurningRight() {
        ship.rotateTravel(0.07);
    }
}
