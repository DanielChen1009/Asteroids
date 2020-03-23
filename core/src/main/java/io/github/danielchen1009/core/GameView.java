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
        float maxBoardSize = Math.min(viewSize.width(), viewSize.height());
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
        surf.fillRect(0, 0, width(), height());
        surf.setFillColor(0xFFFFFFFF);
        for (Entity entity : game.getEntities()) paintEntity(surf, entity);
    }

    private void paintEntity(Surface surf, Entity entity) {
        paintBody(surf, entity.primaryBody);
        for (Body wrapBody : entity.wrapBodies.values()) {
            paintBody(surf, wrapBody);
        }
    }

    private void paintBody(Surface surf, Body body) {
        int size = body.getPoints().size();
        for (int i = 0; i < size; ++i) {
            surf.drawLine(
                    (float) (body.getCenter().x + body.getPoints().get(i % size).x) * width(),
                    (float) (body.getCenter().y + body.getPoints().get(i % size).y) * height(),
                    (float) (body.getCenter().x + body.getPoints().get((i + 1) % size).x) * width(),
                    (float) (body.getCenter().y + body.getPoints().get((i + 1) % size).y) * height(),
                    LINE_WIDTH);
        }
    }
}