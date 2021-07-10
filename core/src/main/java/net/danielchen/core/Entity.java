package net.danielchen.core;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.*;

enum Edge {
    NONE(0, 0), LEFT(1, 0), RIGHT(-1, 0), UP(0, 1), DOWN(0, -1);

    float dx;
    float dy;

    Edge(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }
}

class EntityBody {
    private final List<Vec2> points;
    private final Game game;
    private final World world;
    private final Body body;
    final Entity entity;
    private final boolean physicsEnabled;

    public EntityBody(Entity entity, Game game, List<Vec2> points,
                      Vec2 center) {
        this(entity, game, points, center, true);
    }

    public EntityBody(Entity entity, Game game, List<Vec2> points, Vec2 center,
                      boolean physicsEnabled) {
        this.entity = entity;
        this.game = game;
        this.world = game.world;
        this.points = points;
        this.physicsEnabled = physicsEnabled;
        FixtureDef fixtureDef = new FixtureDef();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position = new Vec2(0, 0);
        this.body = this.world.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(points.stream().map(v -> v.mul(Config.WORLD_SCALE))
                .toArray(Vec2[]::new), points.size());
        fixtureDef.shape = polygonShape;
        fixtureDef.restitution = 0.9f;
        fixtureDef.density = 1.0f;
        fixtureDef.isSensor = !entity.shouldCollide();
        fixtureDef.filter.categoryBits = entity.type.category;
        fixtureDef.filter.maskBits &= ~entity.excludedCollisions();
        this.body.createFixture(fixtureDef);
        this.body.setTransform(new Vec2(center.x * Config.WORLD_SCALE,
                center.y * Config.WORLD_SCALE), 0);
        this.body.setBullet(entity instanceof Bullet);

        if (physicsEnabled)
            game.bodyMap.put(this.body, entity);
    }

    void setTransform(Vec2 pos, float angle) {
        this.body.setTransform(pos.mul(Config.WORLD_SCALE), angle);
    }

    void setAngularVelocity(float v) {
        this.body.setAngularVelocity(v);
    }

    void setLinearVelocity(Vec2 v) {
        this.body.setLinearVelocity(v.mul(Config.WORLD_SCALE));
    }

    void applyForce(Vec2 v) {
        this.body.applyForce(v.mul(Config.WORLD_SCALE),
                this.body.getWorldCenter());
    }

    // Returns the normalized center [0, 1].
    Vec2 getCenter() {
        return this.body.getWorldCenter().mul(1 / Config.WORLD_SCALE);
    }

    Vec2[] getPoints() {
        return this.points.stream()
                .map(p -> this.body.getWorldPoint(p.mul(Config.WORLD_SCALE))
                        .mul(1 / Config.WORLD_SCALE)).toArray(Vec2[]::new);
    }

    float getAngularVelocity() {
        return this.body.getAngularVelocity();
    }

    Vec2 getLinearVelocity() {
        return this.body.getLinearVelocity().mul(1 / Config.WORLD_SCALE);
    }

    void wrap(Edge edge) {
        this.body.setTransform(this.body.getWorldCenter()
                        .add(new Vec2(edge.dx, edge.dy).mul(Config.WORLD_SCALE)),
                this.body.getAngle());
    }

    public Set<Edge> getOutOfBounds() {
        Set<Edge> edges = new HashSet<>();
        for (Vec2 p : this.getPoints()) {
            if (p.x > 1)
                edges.add(Edge.RIGHT);
            if (p.x < 0)
                edges.add(Edge.LEFT);
            if (p.y > 1)
                edges.add(Edge.DOWN);
            if (p.y < 0)
                edges.add(Edge.UP);
        }
        return edges;
    }

    EntityBody copy() {
        EntityBody copied = new EntityBody(this.entity, this.game,
                new ArrayList<>(this.points), this.getCenter().clone(),
                this.physicsEnabled);
        copied.setTransform(this.getCenter(), this.getAngle());
        copied.setAngularVelocity(this.getAngularVelocity());
        copied.setLinearVelocity(this.getLinearVelocity());
        return copied;
    }

    float getAngle() {
        return this.body.getAngle();
    }

    void destroy() {
        if (this.body != null) {
            this.game.bodyMap.remove(this.body);
            this.world.destroyBody(this.body);
        }
    }

    @Override
    public String toString() {
        return this.body.toString();
    }
}

public class Entity {
    enum Type {
        SHIP(0x01), ROCK(0x02), BULLET(0x04), POWERUP(0x08);

        Type(int category) {
            this.category = category;
        }

        int category; // For FixtureDef's filter category and maskBits.
    }

    // The type of this entity, with type-specific metadata.
    public Type type;

    // x,y are normalized to [0, 1].
    protected EntityBody primaryBody;

    // When the entity goes out of bounds, wrap bodies are created to make it
    // look like the entity wrapped around the screen.
    protected Map<Set<Edge>, EntityBody> wrapBodies;

    // Whether this entity is active.
    protected boolean active;

    // For this many game ticks, this entity cannot be destroyed.
    protected int immortalTime;

    protected final Game game;
    protected final Random rand;

    public Entity(Type type, Game game) {
        this.game = game;
        this.rand = game.rand;
        this.wrapBodies = new HashMap<>();
        this.active = true;
        this.type = type;
    }

    public void update() {
        if (this.immortalTime > 0)
            --this.immortalTime;
        this.createWrapBodies();

        if (this.primaryBody.getOutOfBounds().isEmpty()) {
            // The ship's primary body is fully in the window. Destroy all
            // wrap bodies.
            for (EntityBody body : this.wrapBodies.values())
                body.destroy();
            this.wrapBodies.clear();
        }
        else {
            if (this.wrapBodies.isEmpty())
                throw new IllegalStateException("Expected wrap bodies");
            for (EntityBody wrapBody : this.wrapBodies.values()) {
                // If any wrap body is fully in the window, move the primary
                // body there and
                // destroy all wrap bodies.
                if (wrapBody.getOutOfBounds().isEmpty()) {
                    this.primaryBody.setTransform(wrapBody.getCenter(),
                            wrapBody.getAngle());
                    this.primaryBody
                            .setAngularVelocity(wrapBody.getAngularVelocity());
                    this.primaryBody
                            .setLinearVelocity(wrapBody.getLinearVelocity());
                    for (EntityBody body : this.wrapBodies.values())
                        body.destroy();
                    this.wrapBodies.clear();
                    break;
                }
            }
        }
    }

    private void createWrapBodies() {
        // Create any wrap bodies when the primary body is out of bounds.
        Set<Edge> outOfBounds = this.primaryBody.getOutOfBounds();
        if (outOfBounds.size() > 2) {
            throw new IllegalStateException(
                    "Cannot have more than 2 edges out of bounds.");
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
                for (Edge edge : outOfBounds)
                    body.wrap(edge);
                this.wrapBodies.put(outOfBounds, body);
            }
        }
    }

    void applyForce(Vec2 v) {
        this.primaryBody.applyForce(v);
        for (EntityBody entityBody : this.wrapBodies.values()) {
            entityBody.applyForce(v);
        }
    }

    void setAngularVelocity(float v) {
        this.primaryBody.setAngularVelocity(v);
        for (EntityBody entityBody : this.wrapBodies.values()) {
            entityBody.setAngularVelocity(v);
        }
    }

    void setLinearVelocity(Vec2 v) {
        this.primaryBody.setLinearVelocity(v);
        for (EntityBody entityBody : this.wrapBodies.values()) {
            entityBody.setLinearVelocity(v);
        }
    }

    Vec2 getLinearVelocity() {
        return this.primaryBody.getLinearVelocity();
    }

    float getAngle() {
        return this.primaryBody.getAngle();
    }

    float getSpeed() {
        return this.primaryBody.getLinearVelocity().length();
    }

    Vec2 getCenter() {
        return this.primaryBody.getCenter();
    }

    float getDistance(Entity other) {
        return MathUtils.distance(this.getCenter(), other.getCenter());
    }

    public void destroy() {
        this.primaryBody.destroy();
        for (EntityBody entityBody : this.wrapBodies.values()) {
            entityBody.destroy();
        }
    }

    public void setPrimaryBody(EntityBody primaryBody) {
        this.primaryBody = primaryBody;
        this.wrapBodies.clear();
    }

    public void contact(Entity other, Body myBody, Body otherBody) {
        if (this.immortalTime > 0)
            return;
        this.active = false;
    }

    /**
     * Whether this entity should participate in JBox2D collision simulations.
     */
    public boolean shouldCollide() {
        return false;
    }

    public int excludedCollisions() {
        return 0;
    }

    @Override
    public String toString() {
        return this.type.toString();
    }
}
