package net.danielchen.core;

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
    private final List<Point> model;
    private final List<Point> points;
    private final Game game;
    private final World world;
    private final Body body;
    private final Entity entity;

    private Point center;
    private float angle;

    public EntityBody(Entity entity, Game game, List<Point> points, Point center) {
        this.entity = entity;
        this.game = game;
        this.world = game.world;
        this.model = points;
        this.points = new ArrayList<>();
        for (Point point : this.model) {
            this.points.add(point.copy());
        }
        this.center = center;
        this.angle = 0;

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

        game.bodyMap.put(body, entity);
    }

    void wrap(Edge edge) {
        Point p = this.center.copy();
        p.add(new Point(edge.dx, edge.dy));
        moveTo(p, this.angle);
    }

    public Set<Edge> getOutOfBounds() {
        Set<Edge> edges = new HashSet<>();
        for (Point p : this.points) {
            if (this.center.x + p.x > 1) edges.add(Edge.RIGHT);
            if (this.center.x + p.x < 0) edges.add(Edge.LEFT);
            if (this.center.y + p.y > 1) edges.add(Edge.DOWN);
            if (this.center.y + p.y < 0) edges.add(Edge.UP);
        }
        return edges;
    }

    EntityBody copy() {
        return new EntityBody(this.entity, this.game, new ArrayList<>(this.model), this.center.copy());
    }

    public List<Point> getPoints() {
        return this.points;
    }

    public Point getCenter() {
        return this.center;
    }

    void moveTo(Point point, float angle) {
        this.center = point.copy();
        this.angle = angle;
        for (int i = 0; i < this.model.size(); ++i) {
            Point p = this.points.get(i);
            Point modelP = this.model.get(i);
            p.x = modelP.x * Math.cos(angle) - modelP.y * Math.sin(angle);
            p.y = modelP.y * Math.cos(angle) + modelP.x * Math.sin(angle);
        }
        this.body.setTransform(new Vec2((float) this.center.x, (float) this.center.y), angle);
    }

    Body getBody() {
        return this.body;
    }

    float getAngle() {
        return this.angle;
    }

    void destroy() {
        this.game.bodyMap.remove(body);
        this.world.destroyBody(body);
    }

    @Override
    public String toString() {
        return this.center.toString();
    }
}

public class Entity {
    public String type;
    // Relative to the center. x,y are normalized to [0, 1].
    protected EntityBody primaryBody;
    protected Map<Set<Edge>, EntityBody> wrapBodies;

    // traveling and orientation variables.
    double dx;
    double dy;
    // units of the angles are radians.
    float travelAngle;
    float bodyAngle;

    protected boolean active;

    // For this many game ticks, this entity cannot be destroyed.
    protected int immortalTime = 5;

    protected final Game game;
    protected final Random rand;

    public Entity(String type, Game game) {
        this.game = game;
        this.rand = game.rand;
        this.wrapBodies = new HashMap<>();
        this.active = true;
        this.type = type;
        this.travelAngle = 0;
        this.bodyAngle = 0;
    }

    public void update() {
        if (this.immortalTime > 0) --this.immortalTime;
        this.createWrapBodies();

        if (this.primaryBody.getOutOfBounds().isEmpty()) {
            // The ship's primary body is fully in the window. Destroy all wrap bodies.
            for (EntityBody body : this.wrapBodies.values()) body.destroy();
            this.wrapBodies.clear();
        } else {
            if (this.wrapBodies.isEmpty()) throw new IllegalStateException("Expected wrap bodies");
            for (EntityBody wrapBody : this.wrapBodies.values()) {
                // If any wrap body is fully in the window, move the primary body there and
                // destroy all wrap bodies.
                if (wrapBody.getOutOfBounds().isEmpty()) {
                    this.primaryBody.moveTo(wrapBody.getCenter(), wrapBody.getAngle());
                    for (EntityBody body : this.wrapBodies.values()) body.destroy();
                    this.wrapBodies.clear();
                    break;
                }
            }
        }

        Point p = this.primaryBody.getCenter().copy();
        p.add(new Point(this.dx, this.dy));
        this.primaryBody.moveTo(p, this.bodyAngle);
        for (EntityBody wrapBody : this.wrapBodies.values()) {
            Point p1 = wrapBody.getCenter().copy();
            p1.add(new Point(this.dx, this.dy));
            wrapBody.moveTo(p1, this.bodyAngle);
        }
    }

    private void createWrapBodies() {
        // Create any wrap bodies when the primary body is out of bounds.
        Set<Edge> outOfBounds = this.primaryBody.getOutOfBounds();
        if (outOfBounds.size() > 2) {
            throw new IllegalStateException("Cannot have more than 2 edges out of bounds.");
        }
        for (Edge edge : outOfBounds) {
            Set<Edge> key = Collections.singleton(edge);
            if (!this.wrapBodies.containsKey(key)) {
                EntityBody body = this.primaryBody.copy();
                body.wrap(edge);
                this.wrapBodies.put(key, body);
            }
        }
        if (outOfBounds.size() == 2) {
            if (!this.wrapBodies.containsKey(outOfBounds)) {
                EntityBody body = this.primaryBody.copy();
                for (Edge edge : outOfBounds) body.wrap(edge);
                this.wrapBodies.put(outOfBounds, body);
            }
        }
    }

    public void destroy() {
        this.game.bodyMap.remove(this.primaryBody.getBody());
        this.game.world.destroyBody(this.primaryBody.getBody());
        for (EntityBody entityBody : this.wrapBodies.values()) {
            this.game.bodyMap.remove(entityBody.getBody());
            this.game.world.destroyBody(entityBody.getBody());
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
        this.wrapBodies.clear();
    }

    public boolean isActive() {
        return this.active;
    }

    public void contact(Entity other) {
        if (this.immortalTime > 0) return;
        this.active = false;
    }

    public int getContactDelay(Entity other) {
        return 0;
    }
}
