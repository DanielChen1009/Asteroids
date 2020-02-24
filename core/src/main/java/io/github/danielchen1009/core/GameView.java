package io.github.danielchen1009.core;


import playn.core.Surface;
import playn.scene.Layer;
import pythagoras.f.IDimension;

public class GameView extends Layer {
    private static final float LINE_WIDTH = 2;
    private final Game game;
    private final IDimension viewSize;

    public GameView(Game game, IDimension viewSize) {
        this.game = game;
        this.viewSize = viewSize;
        float maxBoardSize = Math.min(viewSize.width(), viewSize.height()) - 20;
    }

    // we want two extra pixels in width/height to account for the grid lines
    @Override
    public float width() {
        return this.viewSize.width();
    }

    @Override
    public float height() {
        return this.viewSize.height();
    } // width == height

    @Override
    protected void paintImpl(Surface surf) {
        surf.setFillColor(0xFF000000); // black with full alpha
        float top = 0, bot = height(), left = 0, right = width();
        int size = this.game.getShip().points.size();
        surf.setFillColor(0xFFFFFFFF);
        for (int i = 0; i < size; ++i) {
            surf.drawLine((float) this.game.getShip().points.get(i % size).x, (float) this.game.getShip().points.get(i % size).y, (float) this.game.getShip().points.get((i + 1) % size).x, (float) this.game.getShip().points.get((i + 1) % size).y, LINE_WIDTH);
        }
        if (this.game.isTransitioning && this.game.transitionShip != null) {
            for (int i = 0; i < this.game.transitionShip.points.size(); ++i) {
                surf.drawLine((float) this.game.transitionShip.points.get(i % size).x, (float) this.game.transitionShip.points.get(i % size).y, (float) this.game.transitionShip.points.get((i + 1) % size).x, (float) this.game.transitionShip.points.get((i + 1) % size).y, LINE_WIDTH);
            }
        }
    }
}