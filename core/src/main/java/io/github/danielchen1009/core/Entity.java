package io.github.danielchen1009.core;

import java.util.*;

enum Edge {
    NONE(0, 0), LEFT(1, 0), RIGHT(-1, 0), UP(0, 1), DOWN(0, -1);

    double dx;
    double dy;

    Edge(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }
}

class Body {
    private List<Point> model;
    private List<Point> points;
    private Point center;

    public Body(List<Point> points, Point center) {
        this.model = points;
        this.points = new ArrayList<>();
        for (Point point : model) {
            this.points.add(point.copy());
        }
        this.center = center;
    }

    void rotateTo(double angle) {
        for (int i = 0; i < model.size(); ++i) {
            Point p = points.get(i);
            Point modelP = model.get(i);
            p.x = modelP.x * Math.cos(angle) - modelP.y * Math.sin(angle);
            p.y = modelP.y * Math.cos(angle) + modelP.x * Math.sin(angle);
        }
    }

    void wrap(Edge edge) {
        this.center.x += edge.dx;
        this.center.y += edge.dy;
    }

    public Set<Edge> isOutOfBounds() {
        Set<Edge> edges = new HashSet<>();
        for (Point p : points) {
            if (center.x + p.x > 1) edges.add(Edge.RIGHT);
            if (center.x + p.x < 0) edges.add(Edge.LEFT);
            if (center.y + p.y > 1) edges.add(Edge.DOWN);
            if (center.y + p.y < 0) edges.add(Edge.UP);
        }
        return edges;
    }

    Body copy() {
        return new Body(new ArrayList<>(model), center.copy());
    }

    public List<Point> getPoints() {
        return points;
    }

    public Point getCenter() {
        return center;
    }

    void translate(Point p) {
        center.add(p);
    }

    @Override
    public String toString() {
        return center.toString();
    }
}

public class Entity {
    // Relative to the center. x,y are normalized to [0, 1].
    protected Body primaryBody;
    protected Map<Edge, Body> wrapBodies;

    // traveling and orientation variables.
    protected double dx;
    protected double dy;
    // units of the angles are radians.
    protected double travelAngle;
    protected double bodyAngle;

    public Entity() {
        this.wrapBodies = new HashMap<>();
    }

    public void update() {
        Set<Edge> outOfBounds = primaryBody.isOutOfBounds();
        Body prevBody = wrapBodies.isEmpty() ? primaryBody : wrapBodies.values().iterator().next();
        for (Edge edge : outOfBounds) {
            if (!wrapBodies.containsKey(edge)) {
                Body wrapBody = prevBody.copy();
                wrapBody.wrap(edge);
                wrapBodies.put(edge, wrapBody);
            }
            prevBody = wrapBodies.get(edge);
        }

        for (Body wrapBody : wrapBodies.values()) {
            if (wrapBody.isOutOfBounds().isEmpty()) {
                primaryBody = wrapBody.copy();
                wrapBodies.clear();
                break;
            }
        }

        if (primaryBody.isOutOfBounds().isEmpty()) wrapBodies.clear();

        primaryBody.rotateTo(bodyAngle);
        primaryBody.translate(new Point(dx, dy));
        for (Body wrapBody : wrapBodies.values()) {
            wrapBody.rotateTo(bodyAngle);
            wrapBody.translate(new Point(dx, dy));
        }
    }

    public void rotateBody(double angle) {
        this.bodyAngle += angle;
    }

    public void rotateTravel(double angle) {
        this.travelAngle += angle;
    }

    public void setPrimaryBody(Body primaryBody) {
        this.primaryBody = primaryBody;
    }

    public boolean isActive() {
        return true;
    }
}

