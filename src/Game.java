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
    Point p1, p2, p3;
    double dx, dy;
    private double angle;
    private Point center;
    private int degreeIncrement = 10;

    public Ship(Point p1) {
        this.p1 = p1;
        this.p2 = p1.add(new Point(10, 25));
        this.p3 = p1.add(new Point(-10, 25));
        angle = Math.PI / 2;
        center = p1.add(new Point(0, 14.5));
    }

    public void move(double a) {
        double v = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        a = a - v * v * -0.03;
        dx += a * Math.cos(angle);
        dy += a * Math.sin(angle);
        p1.x += dx;
        p1.y += dy;
        p2.x += dx;
        p2.y += dy;
        p3.x += dx;
        p3.y += dy;
        center.x += dx;
        center.y += dy;
    }

    public void rotateL() {
        p1.x -= center.x;
        p1.y -= center.y;

        double newx1 = p1.x * Math.cos(-1 * degreeIncrement * Math.PI / 180) - p1.y * Math.sin(-1 * degreeIncrement * Math.PI / 180);
        double newy1 = p1.x * Math.sin(-1 * degreeIncrement * Math.PI / 180) + p1.y * Math.cos(-1 * degreeIncrement * Math.PI / 180);

        p1.x = newx1 + center.x;
        p1.y = newy1 + center.y;


        p2.x -= center.x;
        p2.y -= center.y;

        double newx2 = p2.x * Math.cos(-1 * degreeIncrement * Math.PI / 180) - p2.y * Math.sin(-1 * degreeIncrement * Math.PI / 180);
        double newy2 = p2.x * Math.sin(-1 * degreeIncrement * Math.PI / 180) + p2.y * Math.cos(-1 * degreeIncrement * Math.PI / 180);

        p2.x = newx2 + center.x;
        p2.y = newy2 + center.y;


        p3.x -= center.x;
        p3.y -= center.y;

        double newx3 = p3.x * Math.cos(-1 * degreeIncrement * Math.PI / 180) - p3.y * Math.sin(-1 * degreeIncrement * Math.PI / 180);
        double newy3 = p3.x * Math.sin(-1 * degreeIncrement * Math.PI / 180) + p3.y * Math.cos(-1 * degreeIncrement * Math.PI / 180);

        p3.x = newx3 + center.x;
        p3.y = newy3 + center.y;
        angle += -1 * degreeIncrement * Math.PI / 180;
    }

    public void rotateR() {
        p1.x -= center.x;
        p1.y -= center.y;

        double newx1 = p1.x * Math.cos(degreeIncrement * Math.PI / 180) - p1.y * Math.sin(degreeIncrement * Math.PI / 180);
        double newy1 = p1.x * Math.sin(degreeIncrement * Math.PI / 180) + p1.y * Math.cos(degreeIncrement * Math.PI / 180);

        p1.x = newx1 + center.x;
        p1.y = newy1 + center.y;


        p2.x -= center.x;
        p2.y -= center.y;

        double newx2 = p2.x * Math.cos(degreeIncrement * Math.PI / 180) - p2.y * Math.sin(degreeIncrement * Math.PI / 180);
        double newy2 = p2.x * Math.sin(degreeIncrement * Math.PI / 180) + p2.y * Math.cos(degreeIncrement * Math.PI / 180);

        p2.x = newx2 + center.x;
        p2.y = newy2 + center.y;


        p3.x -= center.x;
        p3.y -= center.y;

        double newx3 = p3.x * Math.cos(degreeIncrement * Math.PI / 180) - p3.y * Math.sin(degreeIncrement * Math.PI / 180);
        double newy3 = p3.x * Math.sin(degreeIncrement * Math.PI / 180) + p3.y * Math.cos(degreeIncrement * Math.PI / 180);

        p3.x = newx3 + center.x;
        p3.y = newy3 + center.y;
        angle += degreeIncrement * Math.PI / 180;
    }

}

public class Game {
    private boolean isAccelerating;
    private boolean isTurningLeft;
    private boolean isTurningRight;
    private Ship ship;
    private double v;

    public Game(int width, int height) {
        Point startPoint = new Point((double) width / 2, (double) height / 2);
        ship = new Ship(startPoint);
    }

    public Ship getShip() {
        return ship;
    }

    public void update() {
        if (isTurningLeft) ship.rotateL();
        if (isTurningRight) ship.rotateR();
        int acceleration = isAccelerating ? -1 : 0;
        this.ship.move(acceleration);
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
