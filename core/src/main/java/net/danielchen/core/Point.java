package net.danielchen.core;

class Point {
    double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void add(Point p) {
        this.x += p.x;
        this.y += p.y;
    }

    public Point copy() {
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return x + ", " + y;
    }
}