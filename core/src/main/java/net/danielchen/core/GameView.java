package net.danielchen.core;

import playn.core.*;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;
import playn.scene.Layer;
import pythagoras.f.IDimension;

public class GameView extends GroupLayer {
    private static final float LINE_WIDTH = 2;

    public GameView(Game game, Platform plat) {
        TextLayer textLayer = new TextLayer(plat);
        this.addAt(new GameLayer(game, plat, textLayer), 0, 0);
        this.addAt(textLayer, 0, 0);
    }

    static class GameLayer extends Layer {
        private final IDimension viewSize;
        private final Game game;
        private final TextLayer textLayer;

        GameLayer(Game game, Platform plat, TextLayer textLayer) {
            this.game = game;
            this.viewSize = plat.graphics().viewSize;
            this.textLayer = textLayer;
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
            for (Entity entity : this.game.getEntities()) paintEntity(surf, entity);
            this.textLayer.showText("Press ESC to restart. Score: " + this.game.score());
        }

        private void paintEntity(Surface surf, Entity entity) {
            if (entity instanceof Ship) surf.setFillColor(0xFF00FF00);
            else if (entity instanceof Bullet) surf.setFillColor(0xFF2288AA);
            else if (entity instanceof Rock) surf.setFillColor(0xFFAAAAAA);
            else surf.setFillColor(0xFFFFFFFF);
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

    static class TextLayer extends ImageLayer {
        private final Platform plat;

        TextLayer(Platform plat) {this.plat = plat;}

        void showText(String text) {
            Font font = new Font("Helvetica", Font.Style.BOLD, 16f);
            TextFormat format = new TextFormat(font);
            TextLayout layout = this.plat.graphics().layoutText(text, format);
            Canvas canvas = this.plat.graphics().createCanvas(layout.size);
            canvas.setFillColor(0xFFFFFFFF).fillText(layout, 0, 0);
            this.setTile(canvas.toTexture());
        }
    }
}