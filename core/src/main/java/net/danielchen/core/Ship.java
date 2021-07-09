package net.danielchen.core;

import net.danielchen.core.Powerup.Type;

import java.util.*;

public class Ship extends Entity {
    private static final double SIZE = 0.025;
    private static final double ANGLE = Math.PI / 3;
    double acceleration;
    boolean turningLeft, turningRight;
    // Map from powerup type to how much time of that powerup is remaining.
    Map<Type, Integer> powerups;
    int ammo = 30;

    public Ship(Game game, Point center) {
        super("SHIP", game);
        this.powerups = new HashMap<>();
        this.setPrimaryBody(new EntityBody(this, game, new ArrayList<>(Arrays.asList(
                new Point(4 * SIZE / 5, 0),
                new Point(SIZE * Math.sin(4 * ANGLE), SIZE * Math.cos(4 * ANGLE)),
                new Point(SIZE * Math.sin(-ANGLE), SIZE * Math.cos(-ANGLE)))), center));
    }

    @Override
    public void update() {
        if (turningLeft) rotateTravel(-Math.PI / 12);
        if (turningRight) rotateTravel(Math.PI / 12);
        double ax = acceleration * Math.cos(travelAngle) - dx * Math.abs(dx) * 1;
        double ay = acceleration * Math.sin(travelAngle) - dy * Math.abs(dy) * 1;
        dx += ax;
        dy += ay;

        // Manage powerup lifetimes
        Iterator<Map.Entry<Type, Integer>> iterator = this.powerups.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Type, Integer> entry = iterator.next();
            if (entry.getValue() > 0) entry.setValue(entry.getValue() - 1);
            else iterator.remove();
        }

        super.update();
    }

    @Override
    public void rotateTravel(double angle) {
        super.rotateTravel(angle);
        super.rotateBody(angle);
    }

    public void accelerate(double a) {
        this.acceleration = a;
    }

    @Override
    public void contact(Entity other) {
        if (other instanceof Bullet || other instanceof Ship) return;
        if (other instanceof Rock && ((Rock) other).isDebris) return;

        // Process contact with powerups. Powerup times stack.
        if (other instanceof Powerup) {
            Powerup powerup = (Powerup) other;
            powerup.active = false;
            if (powerup.type == Type.AMMO) this.ammo += 10;
            if (!this.powerups.containsKey(powerup.type)) {
                this.powerups.put(powerup.type, powerup.type.time);
            } else {
                this.powerups.put(powerup.type, this.powerups.get(powerup.type) + powerup.type.time);
            }
            return;
        }

        // Process death only if we are not invincible.
        if (!this.powerups.containsKey(Type.INVINCIBLE)) {
            super.contact(other);
            // When the ship dies, do a radial burst of bullets for special effect.
            int numBullets = this.rand.nextInt(20) + 20;
            for (int i = 0; i < numBullets; i++) {
                this.game.addEntity(new Bullet(this.game, this.primaryBody.getCenter().copy(),
                        i * ((this.rand.nextDouble() + 1) * Math.PI / numBullets),
                        Bullet.SPEED * this.rand.nextDouble()));
            }
        }
    }
}
