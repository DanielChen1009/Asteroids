class Point {
    int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point add(Point p) {
        return new Point(this.x + p.x, this.y + p.y);
    }
}

class Ship {
    Point p1, p2, p3;

    public Ship(Point p1) {
        this.p1 = p1;
        this.p2 = p1.add(new Point(10, 25));
        this.p3 = p1.add(new Point(-10, 25));
    }

    public void transform(Point p) {
        p1 = p1.add(p);
        p2 = p2.add(p);
        p3 = p3.add(p);
    }

}

public class Game {
    public int acceleration;
    private Ship ship;
    private int dy;

    public Game(int width, int height) {
        Point startPoint = new Point(width / 2, height / 2);
        ship = new Ship(startPoint);
    }

    public Ship getShip() {
        return ship;
    }

    public void update() {
        dy = dy + acceleration;
        if(dy == 0) acceleration = 0;
        this.ship.transform(new Point(0, dy));
    }

    public int getAcceleration() {
        return acceleration;
    }

    public int getDy() {
        return dy;
    }

}
