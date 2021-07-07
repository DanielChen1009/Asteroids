package net.danielchen.core;

import playn.core.*;
import playn.scene.ImageLayer;
import playn.scene.Layer;
import playn.scene.SceneGame;
import pythagoras.f.IDimension;

public class Asteroids extends SceneGame {
    private final Game game;
    private final Platform plat;
    private Canvas canvas;

    public Asteroids(Platform plat) {
        super(plat, 50); // update our "simulation" 50ms (20 times per second)
        // figure out how big the game view is
        final IDimension size = plat.graphics().viewSize;
        this.game = new Game();
        this.plat = plat;
        this.plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
            @Override
            public void onEmit(Keyboard.KeyEvent event) {
                switch (event.key) {
                    case LEFT:
                        game.setTurningLeft(event.down);
                        break;
                    case UP:
                        game.setAccelerating(event.down);
                        break;
                    case RIGHT:
                        game.setTurningRight(event.down);
                        break;
                    case SPACE:
                        game.setFiring(event.down);
                        break;
                    case ESCAPE:
                        if (!event.down) game.restart();
                        break;
                }
            }
        });
        // Creates a layer that just draws a black background.
        this.rootLayer.add(new Layer() {
            protected void paintImpl(Surface surf) {
                surf.setFillColor(0xFFFFFFFF).fillRect(0, 0, size.width(), size.height());
            }
        });

        // Creates and add a game view for asteroids objects.
        this.rootLayer.addAt(new GameView(this.game, this.plat), 0, 0);
    }

    @Override
    public void update(Clock clock) {
        super.update(clock);
        game.update();
    }
}
