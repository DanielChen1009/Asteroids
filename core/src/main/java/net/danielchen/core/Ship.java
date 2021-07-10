package net.danielchen.core;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import java.util.*;

public class Ship extends Entity {
    double acceleration;
    // Map from powerup type to how much time of that powerup is remaining.
    Map<Powerup.Type, Integer> powerups;
    int ammo = Config.INITIAL_AMMO; // Amount of ammo remaining.
    int cooldown = Config.COOLDOWN; // Firing cooldown.
    int extraLives = 0; // Gained from the EXTRA_LIVES powerup.

    // These are set when the user is using arrow keys to turn.
    boolean turningLeft, turningRight;

    // These are set when the user is using mouse/touch to move around.
    float pointerTurn = Float.NaN;
    float pointerDistance = Float.NaN;

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
        // Manage turning.
        if (Float.isNaN(this.pointerTurn)) {
            // This is where the user is using keyboard to turn.
            if (this.turningLeft)
                this.setAngularVelocity(-Config.SHIP_TURN_SPEED);
            if (this.turningRight)
                this.setAngularVelocity(Config.SHIP_TURN_SPEED);
            if (!this.turningLeft && !this.turningRight)
                this.setAngularVelocity(0);
        }
        else {
            // This is where user is using mouse/touch to turn.
            this.setAngularVelocity(
                    Config.SHIP_POINTER_TURN_SPEED * this.pointerTurn);
        }

        if (!Float.isNaN(this.pointerDistance)) {
            // This is where user is using mouse/touch to move.
            this.acceleration =
                    Config.SHIP_POINTER_ACCELERATION * this.pointerDistance;
        }
        if (this.acceleration > 0 && this.getSpeed() < Config.SHIP_MAX_SPEED) {
            // Acceleration is modeled as a force
            float fx = (float) (this.acceleration * Math.cos(this.getAngle()));
            float fy = (float) (this.acceleration * Math.sin(this.getAngle()));
            this.applyForce(new Vec2(fx, fy));
        }
        else {
            // If we are not accelerating, apply a force in the opposite
            // direction to our velocity to slow down.
            this.applyForce(this.getLinearVelocity().negate());
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
        this.pointerDistance = Float.NaN;
        this.acceleration = a;
    }

    @Override
    public void contact(Entity other, Body myBody, Body otherBody) {
        if (other instanceof Bullet || other instanceof Ship)
            return;
        if (other instanceof Rock && ((Rock) other).isDebris)
            return;
        if (this.immortalTime > 0)
            return;

        // Process contact with powerups. Powerup times stack.
        if (other instanceof Powerup) {
            Powerup powerup = (Powerup) other;
            powerup.active = false;
            if (powerup.type == Powerup.Type.AMMO)
                this.ammo += Config.POWERUP_AMMO_INCREASE;
            if (powerup.type == Powerup.Type.EXTRA_LIFE)
                this.extraLives++;
            if (!this.powerups.containsKey(powerup.type)) {
                this.powerups.put(powerup.type, powerup.type.time);
            }
            else {
                this.powerups.put(powerup.type,
                        this.powerups.get(powerup.type) + powerup.type.time);
            }
            return;
        }

        // Process death only if we are not invincible.
        if (!this.powerups.containsKey(Powerup.Type.INVINCIBLE)) {
            // If we have extra lives, use that instead of actually dying.
            if (this.extraLives > 0) {
                this.extraLives--;
                this.immortalTime = 5;
            }
            else
                super.contact(other, myBody, otherBody);
            // Do a radial burst of bullets for special effect.
            int numBullets = this.rand.nextInt(20) + 20;
            for (int i = 0; i < numBullets; i++) {
                this.game.addEntity(
                        new Bullet(this.game, this.getCenter().clone(),
                                (float) (i * ((this.rand
                                        .nextFloat() + 1) * Math.PI / numBullets)),
                                Config.BULLET_SPEED * this.rand.nextFloat()));
            }
        }
    }
}
