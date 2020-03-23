package io.github.danielchen1009.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {
    private Ship ship;
    private List<Entity> entities;
    private int cooldown;

    public Game() {
        Point startPoint = new Point(0.5, 0.5);
        this.ship = new Ship(startPoint);
        this.entities = new ArrayList<>();
        entities.add(this.ship);

        for (int i = 0; i < 10; ++i) {
            Rock rock = new Rock(startPoint.copy(), 0.1);
            entities.add(rock);
        }
    }

    public void update() {
        Iterator<Entity> itr = entities.iterator();
        while (itr.hasNext()) {
            Entity entity = itr.next();
            if (!entity.isActive()) itr.remove();
            else entity.update();
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
        return entities;
    }

    public void setFiring() {

        Point bulletLoc = this.ship.primaryBody.getCenter().copy();
        bulletLoc.add(ship.primaryBody.getPoints().get(0));
        Bullet bullet = new Bullet(bulletLoc, this.ship.bodyAngle);
        entities.add(bullet);
    }
}
