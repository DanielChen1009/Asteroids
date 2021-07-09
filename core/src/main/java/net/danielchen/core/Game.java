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
    // The main player ship entity.
    Ship ship;
    // Jbox2d object containing physics world for collision detection.
    World world;
    private final List<Entity> entities;
    private final Map<Pair<Entity, Entity>, ContactData> contacts;
    private static final int COOLDOWN = 4;
    private boolean isFiring;

    final Random rand;
    final Map<Body, Entity> bodyMap;
    int score;

    public Game() {
        this.rand = new Random();
        this.contacts = new HashMap<>();
        this.bodyMap = new HashMap<>();
        this.entities = new ArrayList<>();

        restart();
    }

    public void restart() {
        this.score = 0;
        this.bodyMap.clear();
        this.entities.clear();

        this.world = new World(new Vec2(0, 0));
        this.world.setWarmStarting(true);
        this.world.setAutoClearForces(true);
        this.world.setContactListener(this);

        Point startPoint = new Point(0.5, 0.5);
        this.isFiring = false;
        this.ship = new Ship(this, startPoint);
        this.addEntity(this.ship);
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    public void update() {
        // Continuously spawn rocks, raising the limit as the score goes up.
        while (this.world.getBodyCount() < this.score * 0.1 + 5) {
            Point point = this.rand.nextDouble() < 0.5 ? new Point(this.rand.nextDouble(), 1) : new Point(1, this.rand.nextDouble());
            Rock rock = new Rock(this, point, this.rand.nextGaussian() * 0.01 + 0.06);
            this.addEntity(rock);
        }

        // Delete inactive entities.
        Iterator<Entity> itr = entities.iterator();
        while (itr.hasNext()) {
            Entity entity = itr.next();
            if (!entity.isActive()) {
                entity.destroy();
                itr.remove();
            } else entity.update();
        }

        // Manage ship firing logic.
        if (this.ship.isActive() && this.ship.ammo > 0) {
            if (isFiring && Bullet.cooldown == 0) {
                Point bulletLoc = this.ship.primaryBody.getCenter().copy();
                bulletLoc.add(ship.primaryBody.getPoints().get(0));
                this.addEntity(new Bullet(this, bulletLoc, this.ship.bodyAngle, Bullet.SPEED));
                Bullet.cooldown = COOLDOWN;
                this.ship.ammo--;
            }
            if (Bullet.cooldown > 0) Bullet.cooldown--;
        }

        // Spawn ammo occasionally if the player is low.
        if (this.ship.isActive() && this.ship.ammo < 10 && this.rand.nextDouble() < 0.01) {
            Point point = this.rand.nextDouble() < 0.5 ? new Point(this.rand.nextDouble(), 1) : new Point(1, this.rand.nextDouble());
            this.addEntity(new Powerup(this, point, Powerup.Type.AMMO));
        }

        world.step(1f / 1000f, 0, 0);
        processContacts();
    }

    static class ContactData {
        Contact contact;
        int delay;

        public ContactData(Contact contact, int delay) {
            this.contact = contact;
            this.delay = delay;
        }
    }

    public void processContacts() {
        Iterator<Map.Entry<Pair<Entity, Entity>, ContactData>> iterator = this.contacts.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Pair<Entity, Entity>, ContactData> entry = iterator.next();
            if (entry.getValue().delay > 0) {
                entry.getValue().delay--;
                continue;
            }
            entry.getKey().val0.contact(entry.getKey().val1);
            entry.getKey().val1.contact(entry.getKey().val0);
            iterator.remove();
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
        return new ArrayList<>(this.bodyMap.values());
    }

    public void setFiring(boolean isFiring) {
        this.isFiring = isFiring;
    }

    public int score() {
        return this.score;
    }

    @Override
    public void beginContact(Contact contact) {
        if (contact.isEnabled() && contact.isTouching()) {
            Entity entityA = bodyMap.get(contact.m_fixtureA.m_body);
            Entity entityB = bodyMap.get(contact.m_fixtureB.m_body);
            if (entityA == null || entityB == null) return;
            if (contacts.containsKey(new Pair<>(entityA, entityB))) return;
            int contactDelay = entityA.getContactDelay(entityB) + entityB.getContactDelay(entityA);
            contacts.put(new Pair<>(entityA, entityB), new ContactData(contact, contactDelay));
        }
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