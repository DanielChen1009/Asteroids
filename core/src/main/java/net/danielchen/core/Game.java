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
    private final Map<Pair<Body, Body>, ContactData> contacts;
    private boolean isFiring;

    final Random rand;
    final Map<Body, Entity> bodyMap;
    int score;
    int numRocks;

    public Game() {
        this.rand = new Random();
        this.contacts = new HashMap<>();
        this.bodyMap = new HashMap<>();
        this.entities = new ArrayList<>();

        this.restart();
    }

    public void restart() {
        this.score = 0;
        this.numRocks = 0;
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

        if (Config.GOD_MODE) {
            this.ship.powerups.put(Powerup.Type.INVINCIBLE, 99999);
            this.ship.powerups.put(Powerup.Type.MEGAGUN, 99999);
            this.ship.ammo = 99999;
        }
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    public void update() {
        // Continuously spawn rocks, raising the limit as the score goes up.
        while (this.numRocks < this.score * Config.SPAWN_RATE + 2) {
            Point point = this.rand.nextDouble() < 0.5 ? new Point(
                    this.rand.nextDouble(), 1) : new Point(1,
                    this.rand.nextDouble());
            Rock rock = new Rock(this, point,
                    this.rand.nextGaussian() * Config.BASE_ROCK_SIZE / 10 +
                            Config.BASE_ROCK_SIZE);
            this.addEntity(rock);
            this.numRocks++;
        }

        // Delete inactive entities.
        Iterator<Entity> itr = this.entities.iterator();
        this.numRocks = 0;
        while (itr.hasNext()) {
            Entity entity = itr.next();
            if (entity instanceof Rock && !((Rock) entity).isDebris)
                this.numRocks++;
            if (!entity.isActive()) {
                entity.destroy();
                itr.remove();
            } else
                entity.update();
        }

        // Manage ship firing logic.
        if (this.ship.isActive() && this.ship.ammo > 0) {
            if (this.isFiring && Bullet.cooldown == 0) {
                Point bulletLoc = this.ship.primaryBody.getCenter().copy();
                bulletLoc.add(this.ship.primaryBody.getPoints().get(0));
                if (this.ship.powerups.containsKey(Powerup.Type.MEGAGUN)) {
                    for (int i = -Config.MEGAGUN_SHOTS / 2;
                         i <= Config.MEGAGUN_SHOTS / 2; i++) {
                        this.addEntity(
                                new Bullet(this, bulletLoc,
                                        this.ship.bodyAngle +
                                                Config.MEGAGUN_SPREAD * i
                                ));
                    }
                } else
                    this.addEntity(
                            new Bullet(this, bulletLoc, this.ship.bodyAngle,
                                    Config.BULLET_SPEED));
                Bullet.cooldown = Config.COOLDOWN;
                this.ship.ammo--;
            }
            if (Bullet.cooldown > 0)
                Bullet.cooldown--;
        }

        // Spawn ammo occasionally if the player is low.
        if (this.ship.isActive() &&
                this.ship.ammo < Config.LOW_AMMO_THRESHOLD &&
                this.rand.nextDouble() < Config.LOW_AMMO_SPAWN_RATE) {
            Point point = this.rand.nextDouble() < 0.5 ? new Point(
                    this.rand.nextDouble(), 1) : new Point(1,
                    this.rand.nextDouble());
            this.addEntity(new Powerup(this, point, Powerup.Type.AMMO));
        }

        this.world.step(Config.WORLD_STEP_DT, 0, 0);
        this.processContacts();
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
        Iterator<Map.Entry<Pair<Body, Body>, ContactData>> iterator =
                this.contacts
                        .entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Pair<Body, Body>, ContactData> entry = iterator.next();
            if (entry.getValue().delay > 0 &&
                    this.contacts.size() < Config.MAX_CONTACTS_FOR_DELAY) {
                entry.getValue().delay--;
                continue;
            }
            Entity entityA = this.bodyMap.get(entry.getKey().val0);
            Entity entityB = this.bodyMap.get(entry.getKey().val1);
            if (entityA == null || entityB == null) {
                iterator.remove();
                continue;
            }
            entityA.contact(entityB, entry.getKey().val0, entry.getKey().val1);
            entityB.contact(entityA, entry.getKey().val1, entry.getKey().val0);
            iterator.remove();
        }
    }

    public void setAccelerating(boolean accelerating) {
        this.ship.accelerate(accelerating ? Config.SHIP_ACCELERATION : 0);
    }

    public void setTurningLeft(boolean turningLeft) {
        this.ship.turningLeft = turningLeft;
    }

    public void setTurningRight(boolean turningRight) {
        this.ship.turningRight = turningRight;
    }

    public List<Entity> getEntities() {
        return this.entities;
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
            Entity entityA = this.bodyMap.get(contact.m_fixtureA.m_body);
            Entity entityB = this.bodyMap.get(contact.m_fixtureB.m_body);
            if (entityA == null || entityB == null)
                return;
            if (this.contacts.containsKey(new Pair<>(contact.m_fixtureA.m_body,
                    contact.m_fixtureB.m_body)))
                return;
            int contactDelay = entityA.getContactDelay(entityB) +
                    entityB.getContactDelay(entityA);
            this.contacts.put(new Pair<>(contact.m_fixtureA.m_body,
                            contact.m_fixtureB.m_body),
                    new ContactData(contact, contactDelay));
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