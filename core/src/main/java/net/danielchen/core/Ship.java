package net.danielchen.core;

import java.util.ArrayList;
import java.util.Arrays;

public class Ship extends Entity {
    private static final double SIZE = 0.025;
    private static final double ANGLE = Math.PI / 3;
    double acceleration;
    boolean turningLeft, turningRight;
    int ammo = 30;

    public Ship(Game game, Point center) {
        super("SHIP", game);
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

        if (other instanceof Ammo) {
            other.active = false;
            this.ammo += 10;
            return;
        }
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
