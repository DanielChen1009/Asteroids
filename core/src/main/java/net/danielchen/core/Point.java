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

    public double distance(Point p) {
        double dx = this.x - p.x;
        double dy = this.y - p.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public Point copy() {
        return new Point(this.x, this.y);
    }

    @Override
    public String toString() {
        return this.x + ", " + this.y;
    }
}