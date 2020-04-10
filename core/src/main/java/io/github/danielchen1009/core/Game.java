package io.github.danielchen1009.core;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.*;

public class Game implements ContactListener {
    private Ship ship;
    // box2d object containing physics world
    protected World world;
    private List<Entity> entities;
    private int cooldown;
    private Map<Body, Entity> bodyMap;
    private Stack<Contact> contacts;

    public Game() {
        contacts = new Stack<>();
        world = new World(new Vec2(0, 0));
        world.setWarmStarting(true);
        world.setAutoClearForces(true);
        world.setContactListener(this);

        Point startPoint = new Point(0.5, 0.5);
        this.ship = new Ship(world, startPoint);
        this.bodyMap = new HashMap<>();
        this.entities = new ArrayList<>();
        addEntity(this.ship);
        for (int i = 0; i < 10; ++i) {
            Random rand = new Random();
            double x = rand.nextDouble();
            addEntity(new Rock(world, new Point(x, 1), 0.1));
        }
    }

    public void update() {
        Iterator<Entity> itr = entities.iterator();
        while (itr.hasNext()) {
            Entity entity = itr.next();
            if (!entity.isActive()) {
                itr.remove();
                world.destroyBody(entity.primaryBody.getBody());
            } else entity.update();
        }
        world.step(1f / 1000f, 10, 10);
        processContacts();
    }

    public void processContacts() {
        while (!this.contacts.empty()) {
            Contact contact = contacts.pop();
            Entity entityA = bodyMap.get(contact.m_fixtureA.m_body);
            Entity entityB = bodyMap.get(contact.m_fixtureB.m_body);
            if (entityA == null || entityB == null) continue;
            if (entityA.type.equals(entityB.type)) continue;
            if (entityA instanceof Bullet && entityB instanceof Ship) continue;
            if (entityA instanceof Ship && entityB instanceof Bullet) continue;
            entityA.setActive(false);
            entityB.setActive(false);
            System.out.println(entityA.type + "         " + entityB.type);
        }
    }

    public void setAccelerating(boolean accelerating) {
        ship.accelerate(accelerating ? 0.0005 : 0);
    }

    public void setTurningLeft(boolean turningLeft) {
        ship.turningLeft = turningLeft;
    }

    public void setTurningRight(boolean turningRight) {
        ship.turningRight = turningRight;
    }

    public List<Entity> getEntities() {
        return new ArrayList<>(bodyMap.values());
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        bodyMap.put(entity.primaryBody.getBody(), entity);
        for (EntityBody entityBody : entity.wrapBodies.values()) {
            bodyMap.put(entityBody.getBody(), entity);
        }
    }

    public void setFiring() {
        Point bulletLoc = this.ship.primaryBody.getCenter().copy();
        bulletLoc.add(ship.primaryBody.getPoints().get(0));
        addEntity(new Bullet(world, bulletLoc, this.ship.bodyAngle));
    }

    @Override
    public void beginContact(Contact contact) {
        contacts.push(contact);
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
