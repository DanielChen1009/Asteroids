package net.danielchen.core;


import playn.core.*;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;
import playn.scene.Layer;
import pythagoras.f.IDimension;

public class GameView extends GroupLayer {
    private static final float LINE_WIDTH = 2;
    private final Game game;
    private final Platform plat;

    public GameView(Game game, Platform plat) {
        this.game = game;
        this.plat = plat;
        this.addAt(new GameLayer(game, plat), 0, 0);
        this.addAt(this.createTextLayer("Press ESC to restart"), 0 ,0);
    }

    static class GameLayer extends Layer {
        private final IDimension viewSize;
        private final Game game;

        GameLayer(Game game, Platform plat) {
            this.game = game;
            this.viewSize = plat.graphics().viewSize;
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
            surf.fillRect(0, 0, width(), height());
            surf.setFillColor(0xFFFFFFFF);
            for (Entity entity : game.getEntities()) paintEntity(surf, entity);
        }

        private void paintEntity(Surface surf, Entity entity) {
            paintBody(surf, entity.primaryBody);
            for (EntityBody wrapBody : entity.wrapBodies.values()) {
                paintBody(surf, wrapBody);
            }
        }

        private void paintBody(Surface surf, EntityBody body) {
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

    protected Layer createTextLayer(String text) {
        Font font = new Font("Helvetica", Font.Style.BOLD, 16f);
        TextFormat format = new TextFormat(font);
        TextLayout layout = this.plat.graphics().layoutText(text, format);
        Canvas canvas = this.plat.graphics().createCanvas(layout.size);
        canvas.setFillColor(0xFFFFFFFF).fillText(layout, 0, 0);
        return new ImageLayer(canvas.toTexture());
    }
}