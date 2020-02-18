import java.util.ArrayList;
import java.util.List;

class Point {
    double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point add(Point p) {
        return new Point(this.x + p.x, this.y + p.y);
    }
}

class Ship {
    List<Point> points;
    double dx, dy, ax, ay;
    private double angle;
    private Point center;
    private int degreeIncrement = 15;

    public Ship(Point p1) {
        points = new ArrayList<>();
        points.add(p1);
        points.add(p1.add(new Point(10, 25)));
        points.add(p1.add(new Point(-10, 25)));
        angle = Math.PI / 2;
        center = p1.add(new Point(0, 14.5));
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

    public void rotateL() {
        points.get(0).x -= center.x;
        points.get(0).y -= center.y;

        double newx1 = points.get(0).x * Math.cos(-1 * degreeIncrement * Math.PI / 180) - points.get(0).y * Math.sin(-1 * degreeIncrement * Math.PI / 180);
        double newy1 = points.get(0).x * Math.sin(-1 * degreeIncrement * Math.PI / 180) + points.get(0).y * Math.cos(-1 * degreeIncrement * Math.PI / 180);

        points.get(0).x = newx1 + center.x;
        points.get(0).y = newy1 + center.y;


        points.get(1).x -= center.x;
        points.get(1).y -= center.y;

        double newx2 = points.get(1).x * Math.cos(-1 * degreeIncrement * Math.PI / 180) - points.get(1).y * Math.sin(-1 * degreeIncrement * Math.PI / 180);
        double newy2 = points.get(1).x * Math.sin(-1 * degreeIncrement * Math.PI / 180) + points.get(1).y * Math.cos(-1 * degreeIncrement * Math.PI / 180);

        points.get(1).x = newx2 + center.x;
        points.get(1).y = newy2 + center.y;


        points.get(2).x -= center.x;
        points.get(2).y -= center.y;

        double newx3 = points.get(2).x * Math.cos(-1 * degreeIncrement * Math.PI / 180) - points.get(2).y * Math.sin(-1 * degreeIncrement * Math.PI / 180);
        double newy3 = points.get(2).x * Math.sin(-1 * degreeIncrement * Math.PI / 180) + points.get(2).y * Math.cos(-1 * degreeIncrement * Math.PI / 180);

        points.get(2).x = newx3 + center.x;
        points.get(2).y = newy3 + center.y;
        angle += -1 * degreeIncrement * Math.PI / 180;
    }

    public void rotateR() {
        points.get(0).x -= center.x;
        points.get(0).y -= center.y;

        double newx1 = points.get(0).x * Math.cos(degreeIncrement * Math.PI / 180) - points.get(0).y * Math.sin(degreeIncrement * Math.PI / 180);
        double newy1 = points.get(0).x * Math.sin(degreeIncrement * Math.PI / 180) + points.get(0).y * Math.cos(degreeIncrement * Math.PI / 180);
        
        points.get(0).x = newx1 + center.x;
        points.get(0).y = newy1 + center.y;


        points.get(1).x -= center.x;
        points.get(1).y -= center.y;

        double newx2 = points.get(1).x * Math.cos(degreeIncrement * Math.PI / 180) - points.get(1).y * Math.sin(degreeIncrement * Math.PI / 180);
        double newy2 = points.get(1).x * Math.sin(degreeIncrement * Math.PI / 180) + points.get(1).y * Math.cos(degreeIncrement * Math.PI / 180);

        points.get(1).x = newx2 + center.x;
        points.get(1).y = newy2 + center.y;


        points.get(2).x -= center.x;
        points.get(2).y -= center.y;

        double newx3 = points.get(2).x * Math.cos(degreeIncrement * Math.PI / 180) - points.get(2).y * Math.sin(degreeIncrement * Math.PI / 180);
        double newy3 = points.get(2).x * Math.sin(degreeIncrement * Math.PI / 180) + points.get(2).y * Math.cos(degreeIncrement * Math.PI / 180);

        points.get(2).x = newx3 + center.x;
        points.get(2).y = newy3 + center.y;
        angle += degreeIncrement * Math.PI / 180;
    }

    public void shoot() {

    }

    public void wrap(int width , int height) {
        for(Point point : this.points) {
            point.x = (point.x + width) % width;
            point.y = (point.y + height) % height;
        }
        center.x = (center.x + width) % width;
        center.y = (center.y + height) % height;
    }

}

public class Game {
    private boolean isAccelerating;
    private boolean isTurningLeft;
    private boolean isTurningRight;
    private Ship ship;
    private int width, height;
    boolean isTransitioning;
    private double v;

    public Game(int width, int height) {
        this.width = width;
        this.height = height;
        Point startPoint = new Point((double) this.width / 2, (double) this.height / 2);
        ship = new Ship(startPoint);
    }

    public Ship getShip() {
        return ship;
    }

    public void update() {
        if (isTurningLeft) ship.rotateL();
        if (isTurningRight) ship.rotateR();
        double acceleration = isAccelerating ? -0.3 : 0;
        this.ship.move(acceleration);
        for(Point point : this.ship.points) {
            if(point.x > this.width || point.x < 0 || point.y > this.height || point.y < 0) this.ship.wrap(this.width, this.height);
        }
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
