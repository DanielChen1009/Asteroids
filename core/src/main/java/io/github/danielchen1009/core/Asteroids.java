package io.github.danielchen1009.core;

import playn.core.Clock;
import playn.core.Keyboard;
import playn.core.Platform;
import playn.core.Surface;
import playn.scene.Layer;
import playn.scene.SceneGame;
import pythagoras.f.IDimension;

public class Asteroids extends SceneGame {
  private Game game;

  public Asteroids(Platform plat) {
    super(plat, 33); // update our "simulation" 33ms (30 times per second)
    // figure out how big the game view is
    final IDimension size = plat.graphics().viewSize;
    game = new Game();
    plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
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
            game.setFiring();
            break;
          default:
            break; // nada
        }
      }
    });
    // create a layer that just draws a black background
    rootLayer.add(new Layer() {
      protected void paintImpl(Surface surf) {
        surf.setFillColor(0xFFFFFFFF).fillRect(0, 0, size.width(), size.height());
      }
    });

    // create and add a board view
    rootLayer.addCenterAt(new GameView(game, size), size.width() / 2, size.height() / 2);
  }

  @Override
  public void update(Clock clock) {
    super.update(clock);
    game.update();
  }
}
