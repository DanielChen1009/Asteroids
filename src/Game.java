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

}

public class Game {
    private Ship ship;
    private Point startPoint;

    public Game(int width, int height) {
        startPoint = new Point(width / 2, height / 2);
        ship = new Ship(startPoint);
    }

    public Ship getShip() {
        return ship;
    }
}
