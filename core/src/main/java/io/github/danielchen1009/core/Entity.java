package io.github.danielchen1009.core;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

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

class EntityBody {
    boolean wrapped;
    private List<Point> model;
    private List<Point> points;
    private Point center;
    private World world;
    private Body body;
    private float angle;


    public EntityBody(World world, List<Point> points, Point center) {
        this.world = world;
        this.model = points;
        this.points = new ArrayList<>();
        for (Point point : model) {
            this.points.add(point.copy());
        }
        this.center = center;
        this.angle = 0;
        this.wrapped = false;

        FixtureDef fixtureDef = new FixtureDef();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position = new Vec2(0, 0);
        this.body = world.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        Vec2[] polygon = new Vec2[points.size()];
        for (int i = 0; i < points.size(); ++i) {
            Point point = points.get(i);
            polygon[i] = new Vec2((float) point.x, (float) point.y);
        }
        polygonShape.set(polygon, polygon.length);
        fixtureDef.shape = polygonShape;
        fixtureDef.restitution = 1f;
        body.createFixture(fixtureDef);
        body.setTransform(new Vec2((float) center.x, (float) center.y), angle);
    }


    void wrap(Edge edge) {
        Point p = this.center.copy();
        p.add(new Point(edge.dx, edge.dy));
        moveTo(p, angle);
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

    EntityBody copy() {
        return new EntityBody(world, new ArrayList<>(model), center.copy());
    }

    public List<Point> getPoints() {
        return points;
    }

    public Point getCenter() {
        return center;
    }

    void moveTo(Point point, float angle) {
        center = point.copy();
        this.angle = angle;
        for (int i = 0; i < model.size(); ++i) {
            Point p = points.get(i);
            Point modelP = model.get(i);
            p.x = modelP.x * Math.cos(angle) - modelP.y * Math.sin(angle);
            p.y = modelP.y * Math.cos(angle) + modelP.x * Math.sin(angle);
        }
        body.setTransform(new Vec2((float) center.x, (float) center.y), angle);
    }

    Body getBody() {
        return body;
    }

    float getAngle() {
        return angle;
    }

    @Override
    public String toString() {
        return center.toString();
    }


}

public class Entity {
    public String type;
    // Relative to the center. x,y are normalized to [0, 1].
    protected EntityBody primaryBody;
    protected Map<Edge, EntityBody> wrapBodies;

    // traveling and orientation variables.
    protected double dx;
    protected double dy;
    // units of the angles are radians.
    protected float travelAngle;
    protected float bodyAngle;

    protected boolean active;

    public Entity(String type) {
        this.wrapBodies = new HashMap<>();
        active = true;
        this.type = type;
        travelAngle = 0;
        bodyAngle = 0;
    }

    public void update() {
        // Wrap any bodies when primary body is out of bounds.
        Set<Edge> outOfBounds = primaryBody.isOutOfBounds();
        for (Edge edge : outOfBounds) {
            EntityBody body = wrapBodies.get(edge);
            if (!body.wrapped) {
                body.wrap(edge);
                body.wrapped = true;
                System.out.println(edge);
            }
        }

        if (primaryBody.isOutOfBounds().isEmpty()) {
            for (EntityBody body : wrapBodies.values()) {
                body.moveTo(primaryBody.getCenter(), primaryBody.getAngle());
                body.wrapped = false;
            }
        } else {
            for (EntityBody wrapBody : wrapBodies.values()) {
                if (wrapBody.isOutOfBounds().isEmpty()) {
                    primaryBody.moveTo(wrapBody.getCenter(), wrapBody.getAngle());
                    for (EntityBody body : wrapBodies.values()) {
                        body.moveTo(wrapBody.getCenter(), wrapBody.getAngle());
                        body.wrapped = false;
                    }
                    System.out.println("***REMOVED***");
                    break;
                }
            }
        }

        // TODO: fix edge case where entity disappears
        Point p = primaryBody.getCenter().copy();
        p.add(new Point(dx, dy));
        primaryBody.moveTo(p, bodyAngle);
        for (EntityBody wrapBody : wrapBodies.values()) {
            Point p1 = wrapBody.getCenter().copy();
            p1.add(new Point(dx, dy));
            wrapBody.moveTo(p1, bodyAngle);
        }
    }

    public void rotateBody(double dTheta) {
        this.bodyAngle += dTheta;
    }

    public void rotateTravel(double dTheta) {
        this.travelAngle += dTheta;
    }

    public void setPrimaryBody(EntityBody primaryBody) {
        this.primaryBody = primaryBody;
        wrapBodies.put(Edge.DOWN, primaryBody.copy());
        wrapBodies.put(Edge.UP, primaryBody.copy());
        wrapBodies.put(Edge.RIGHT, primaryBody.copy());
        wrapBodies.put(Edge.LEFT, primaryBody.copy());
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

