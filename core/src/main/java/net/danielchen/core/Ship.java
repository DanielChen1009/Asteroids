package net.danielchen.core;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import java.util.*;

public class Ship extends Entity {
    double acceleration;
    boolean turningLeft, turningRight;
    // Map from powerup type to how much time of that powerup is remaining.
    Map<Powerup.Type, Integer> powerups;
    int ammo = Config.INITIAL_AMMO;
    int cooldown = Config.COOLDOWN;

    public Ship(Game game, Vec2 center) {
        super(Type.SHIP, game);
        this.powerups = new HashMap<>();
        this.setPrimaryBody(new EntityBody(this, game, new ArrayList<>(
                Arrays.asList(new Vec2(4 * Config.SHIP_SIZE / 5, 0), new Vec2(
                        (float) (Config.SHIP_SIZE * Math
                                .sin(4 * Config.SHIP_ANGLE)),
                        (float) (Config.SHIP_SIZE * Math
                                .cos(4 * Config.SHIP_ANGLE))), new Vec2(
                        (float) (Config.SHIP_SIZE * Math
                                .sin(-Config.SHIP_ANGLE)),
                        (float) (Config.SHIP_SIZE * Math
                                .cos(-Config.SHIP_ANGLE))))), center));
    }

    @Override
    public void update() {
        // Manage movement forces.
        if (this.turningLeft)
            this.setAngularVelocity(-Config.SHIP_TURN_SPEED);
        if (this.turningRight)
            this.setAngularVelocity(Config.SHIP_TURN_SPEED);
        if (!this.turningLeft && !this.turningRight)
            this.setAngularVelocity(0);
        if (this.acceleration > 0) {
            float fx = (float) (this.acceleration * Math
                    .cos(this.primaryBody.getAngle()));
            float fy = (float) (this.acceleration * Math
                    .sin(this.primaryBody.getAngle()));
            this.applyForce(new Vec2(fx, fy));
        }
        else {
            this.applyForce(this.primaryBody.getLinearVelocity().negate());
        }

        // Manage powerup lifetimes
        Iterator<Map.Entry<Powerup.Type, Integer>> iterator = this.powerups
                .entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Powerup.Type, Integer> entry = iterator.next();
            if (entry.getValue() > 0)
                entry.setValue(entry.getValue() - 1);
            else
                iterator.remove();
        }

        super.update();
    }

    public void accelerate(double a) {
        this.acceleration = a;
    }

    @Override
    public int excludedCollisions() {
        int exclusions = Type.BULLET.category;
        if (this.powerups.containsKey(Powerup.Type.INVINCIBLE))
            exclusions |= Type.ROCK.category;
        return exclusions;
    }

    @Override
    public void contact(Entity other, Body myBody, Body otherBody) {
        if (other instanceof Bullet || other instanceof Ship)
            return;
        if (other instanceof Rock && ((Rock) other).isDebris)
            return;

        // Process contact with powerups. Powerup times stack.
        if (other instanceof Powerup) {
            Powerup powerup = (Powerup) other;
            powerup.active = false;
            if (powerup.type == Powerup.Type.AMMO)
                this.ammo += Config.POWERUP_AMMO_INCREASE;
            if (!this.powerups.containsKey(powerup.type)) {
                this.powerups.put(powerup.type, powerup.type.time);
            }
            else {
                this.powerups.put(powerup.type, this.powerups
                        .get(powerup.type) + powerup.type.time);
            }
            return;
        }

        // Process death only if we are not invincible.
        if (!this.powerups.containsKey(Powerup.Type.INVINCIBLE)) {
            super.contact(other, myBody, otherBody);
            // When the ship dies, do a radial burst of bullets for special
            // effect.
            int numBullets = this.rand.nextInt(20) + 20;
            for (int i = 0; i < numBullets; i++) {
                this.game.addEntity(new Bullet(this.game,
                                               this.primaryBody.getCenter()
                                                       .clone(),
                                               (float) (i * ((this.rand
                                                       .nextFloat() + 1) * Math.PI / numBullets)),
                                               Config.BULLET_SPEED * this.rand
                                                       .nextFloat()));
            }
        }
    }
}
