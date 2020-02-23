import java.util.ArrayList;
import java.util.List;

enum Direction {
    LEFT, RIGHT
}

class Point {
    double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point add(Point p) {
        return new Point(this.x + p.x, this.y + p.y);
    }

    @Override
    public String toString() {
        return x + ", " + y;
    }
}

class Ship {
    List<Point> points;
    double dx, dy, ax, ay;
    double angle;
    private Point center;
    private final int degreeIncrement = 15;

    public Ship(Point p1, double angle, double dx, double dy) {
        points = new ArrayList<>();
        this.dx = dx;
        this.dy = dy;
        center = p1.add(new Point(14.5 * Math.cos(angle), 14.5 * Math.sin(angle)));
        points.add(p1);
        this.angle = angle;
        double angleCenter = Math.PI / 2 - angle;
        double degreeCenter = Math.acos(10.5 / 14.5);
        points.add(center.add(new Point(-14.5 * Math.cos(degreeCenter + angleCenter), 14.5 * Math.sin(degreeCenter + angleCenter))));
        points.add(center.add(new Point(14.5 * Math.cos(degreeCenter - angleCenter), 14.5 * Math.sin(degreeCenter - angleCenter))));
    }

    public double getAngle() {
        return angle;
    }

    public void move(double a) {
        ax = a * Math.cos(angle);
        ay = a * Math.sin(angle);
        ax = ax - dx * Math.abs(dx) * 0.007;
        ay = ay - dy * Math.abs(dy) * 0.007;
        dx += ax;
        dy += ay;
        points.get(0).x += dx;
        points.get(0).y += dy;
        points.get(1).x += dx;
        points.get(1).y += dy;
        points.get(2).x += dx;
        points.get(2).y += dy;
        center.x += dx;
        center.y += dy;
    }

    public void rotate(Direction direction) {
        points.get(0).x -= center.x;
        points.get(0).y -= center.y;
        int dir = 1;
        if (direction == Direction.LEFT) {
            dir = -1;
        }
        double newx1 = points.get(0).x * Math.cos(dir * degreeIncrement * Math.PI / 180) - points.get(0).y * Math.sin(dir * degreeIncrement * Math.PI / 180);
        double newy1 = points.get(0).x * Math.sin(dir * degreeIncrement * Math.PI / 180) + points.get(0).y * Math.cos(dir * degreeIncrement * Math.PI / 180);

        points.get(0).x = newx1 + center.x;
        points.get(0).y = newy1 + center.y;


        points.get(1).x -= center.x;
        points.get(1).y -= center.y;

        double newx2 = points.get(1).x * Math.cos(dir * degreeIncrement * Math.PI / 180) - points.get(1).y * Math.sin(dir * degreeIncrement * Math.PI / 180);
        double newy2 = points.get(1).x * Math.sin(dir * degreeIncrement * Math.PI / 180) + points.get(1).y * Math.cos(dir * degreeIncrement * Math.PI / 180);

        points.get(1).x = newx2 + center.x;
        points.get(1).y = newy2 + center.y;


        points.get(2).x -= center.x;
        points.get(2).y -= center.y;

        double newx3 = points.get(2).x * Math.cos(dir * degreeIncrement * Math.PI / 180) - points.get(2).y * Math.sin(dir * degreeIncrement * Math.PI / 180);
        double newy3 = points.get(2).x * Math.sin(dir * degreeIncrement * Math.PI / 180) + points.get(2).y * Math.cos(dir * degreeIncrement * Math.PI / 180);

        points.get(2).x = newx3 + center.x;
        points.get(2).y = newy3 + center.y;
        angle += dir * degreeIncrement * Math.PI / 180;
    }

    public boolean greaterThan(int num) {
        return (points.get(0).x > num && points.get(1).x > num && points.get(2).x > num) || (points.get(0).y > num && points.get(1).y > num && points.get(2).y > num);
    }

    public boolean lessThan(int num) {
        return (points.get(0).x < num && points.get(1).x < num && points.get(2).x < num) || (points.get(0).y < num && points.get(1).y < num && points.get(2).y < num);
    }

    public void shoot() {

    }

}

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
        Point startPoint = new Point((double) this.width / 2, (double) this.height / 2);
        ship = new Ship(startPoint, Math.PI / 2, 0, 0);
    }

    public Ship getShip() {
        return ship;
    }

    public void update() {
        isTransitioning = transitionShip != null;
        if (isTurningLeft) ship.rotate(Direction.LEFT);
        if (isTurningRight) ship.rotate(Direction.RIGHT);
        double acceleration = isAccelerating ? -0.3 : 0;
        this.ship.move(acceleration);
        for (Point point : this.ship.points) {
            if (point.x > this.width && !isTransitioningL) {
                isTransitioningR = true;
            } else if (point.x < 0 && !isTransitioningR) {
                isTransitioningL = true;
            } else if (point.y > this.height && !isTransitioningU) {
                isTransitioningD = true;
            } else if (point.y < 0 && !isTransitioningD) {
                isTransitioningU = true;
            }
        }
        Point transitionPoint = this.ship.points.get(0);
        if (isTransitioningR) {
            if (transitionShip == null)
                transitionShip = new Ship(new Point(transitionPoint.x - this.width, transitionPoint.y % this.height), this.ship.angle, this.ship.dx, this.ship.dy);
            transitionShip.move(acceleration);
            if (isTurningLeft) transitionShip.rotate(Direction.LEFT);
            if (isTurningRight) transitionShip.rotate(Direction.RIGHT);
            if (this.ship.lessThan(this.width) && this.transitionShip.lessThan(0)) {
                transitionShip = null;
                isTransitioningR = false;
            } else if (this.ship.greaterThan(this.width)) {
                ship = null;
                ship = transitionShip;
                transitionShip = null;
                isTransitioningR = false;
            }
        }
        if (isTransitioningL) {
            if (transitionShip == null)
                transitionShip = new Ship(new Point(transitionPoint.x + this.width, transitionPoint.y % this.height), this.ship.angle, this.ship.dx, this.ship.dy);
            transitionShip.move(acceleration);
            if (isTurningLeft) transitionShip.rotate(Direction.LEFT);
            if (isTurningRight) transitionShip.rotate(Direction.RIGHT);
            if (this.ship.greaterThan(0) && this.transitionShip.greaterThan(this.width)) {
                transitionShip = null;
                isTransitioningL = false;
            } else if (this.ship.lessThan(0)) {
                ship = null;
                ship = transitionShip;
                transitionShip = null;
                isTransitioningL = false;
            }
        }
        if (isTransitioningD) {
            if (transitionShip == null)
                transitionShip = new Ship(new Point(transitionPoint.x, transitionPoint.y - this.height), this.ship.angle, this.ship.dx, this.ship.dy);
            transitionShip.move(acceleration);
            if (isTurningLeft) transitionShip.rotate(Direction.LEFT);
            if (isTurningRight) transitionShip.rotate(Direction.RIGHT);
            if (this.ship.lessThan(this.height) && this.transitionShip.lessThan(0)) {
                transitionShip = null;
                isTransitioningD = false;
            } else if (this.ship.greaterThan(this.height)) {
                ship = null;
                ship = transitionShip;
                transitionShip = null;
                isTransitioningD = false;
            }
        }
        if (isTransitioningU) {
            if (transitionShip == null)
                transitionShip = new Ship(new Point(transitionPoint.x, transitionPoint.y + this.height), this.ship.angle, this.ship.dx, this.ship.dy);
            transitionShip.move(acceleration);
            if (isTurningLeft) transitionShip.rotate(Direction.LEFT);
            if (isTurningRight) transitionShip.rotate(Direction.RIGHT);
            if (this.ship.greaterThan(0) && this.transitionShip.greaterThan(this.height)) {
                transitionShip = null;
                isTransitioningU = false;
            } else if (this.ship.lessThan(0)) {
                ship = null;
                ship = transitionShip;
                transitionShip = null;
                isTransitioningU = false;
            }
        }
        System.out.println(isTransitioning + " " + isTransitioningR + " " + isTransitioningL + " " + isTransitioningU + " " + isTransitioningD);
    }

    public void setAccelerating(boolean bool) {
        isAccelerating = bool;
    }

    public double getV() {
        return v;
    }

    public void setTurningLeft(boolean turningLeft) {
        isTurningLeft = turningLeft;
    }

    public void setTurningRight(boolean turningRight) {
        isTurningRight = turningRight;
    }

}
