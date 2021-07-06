package net.danielchen.core;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.*;

public class Game implements ContactListener {
    private final Ship ship;
    // box2d object containing physics world
    World world;
    private final List<Entity> entities;
    private final Stack<Contact> contacts;
    private static final int COOLDOWN = 4;
    private boolean isFiring;

    final Map<Body, Entity> bodyMap;

    public Game() {
        contacts = new Stack<>();
        world = new World(new Vec2(0, 0));
        world.setWarmStarting(true);
        world.setAutoClearForces(true);
        world.setContactListener(this);

        Point startPoint = new Point(0.5, 0.5);
        this.bodyMap = new HashMap<>();
        this.entities = new ArrayList<>();
        this.isFiring = false;

        this.ship = new Ship(this, startPoint);
        entities.add(this.ship);
        for (int i = 0; i < 5; ++i) {
            Random rand = new Random();
            double x = rand.nextDouble();
            entities.add(new Rock(this, new Point(x, 1), 0.06));
        }
    }

    public void update() {
        Iterator<Entity> itr = entities.iterator();
        while (itr.hasNext()) {
            Entity entity = itr.next();
            if (!entity.isActive()) {
                entity.destroy();
                itr.remove();
            } else entity.update();
        }

        if (isFiring) {
            if (Bullet.cooldown == 0) {
                Point bulletLoc = this.ship.primaryBody.getCenter().copy();
                bulletLoc.add(ship.primaryBody.getPoints().get(0));
                entities.add(new Bullet(this, bulletLoc, this.ship.bodyAngle));
                Bullet.cooldown = COOLDOWN;
            }
        }
        if (Bullet.cooldown > 0) Bullet.cooldown--;

        world.step(1f / 200f, 10, 10);
        processContacts();
    }

    public void processContacts() {
        while (!this.contacts.empty()) {
            Contact contact = contacts.pop();
            Entity entityA = bodyMap.get(contact.m_fixtureA.m_body);
            Entity entityB = bodyMap.get(contact.m_fixtureB.m_body);
            if (entityA == null || entityB == null) continue;
            if (entityA.type.equals(entityB.type)) continue;
            entityA.contact(entityB);
            entityB.contact(entityA);
            System.out.println("Processed contact between " + entityA.type + " and " + entityB.type);
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

    public void setFiring(boolean isFiring) {
        this.isFiring = isFiring;
    }

    @Override
    public void beginContact(Contact contact) {
        if (contact.isEnabled() && contact.isTouching()) contacts.push(contact);
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
