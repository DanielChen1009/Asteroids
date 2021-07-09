package net.danielchen.core;

import playn.core.Clock;
import playn.core.Keyboard;
import playn.core.Platform;
import playn.core.Surface;
import playn.scene.Layer;
import playn.scene.SceneGame;
import pythagoras.f.IDimension;

public class Asteroids extends SceneGame {
    private final Game game;

    public Asteroids(Platform plat) {
        super(plat, 50); // update our "simulation" 50ms (20 times per second)
        // figure out how big the game view is
        final IDimension size = plat.graphics().viewSize;
        this.game = new Game();
        plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
            @Override
            public void onEmit(Keyboard.KeyEvent event) {
                switch (event.key) {
                    case LEFT:
                        Asteroids.this.game.setTurningLeft(event.down);
                        break;
                    case UP:
                        Asteroids.this.game.setAccelerating(event.down);
                        break;
                    case RIGHT:
                        Asteroids.this.game.setTurningRight(event.down);
                        break;
                    case SPACE:
                        Asteroids.this.game.setFiring(event.down);
                        break;
                    case ESCAPE:
                        if (!event.down)
                            Asteroids.this.game.restart();
                        break;
                }
            }
        });
        // Creates a layer that just draws a black background.
        this.rootLayer.add(new Layer() {
            @Override
            protected void paintImpl(Surface surf) {
                surf.setFillColor(0xFFFFFFFF)
                        .fillRect(0, 0, size.width(), size.height());
            }
        });

        // Creates and add a game view for asteroids objects.
        this.rootLayer.addAt(new GameView(this.game, plat), 0, 0);
    }

    @Override
    public void update(Clock clock) {
        super.update(clock);
        this.game.update();
    }
}
