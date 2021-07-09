package net.danielchen.core;

import playn.core.*;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;
import playn.scene.Layer;
import pythagoras.f.IDimension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            this.textLayer.addText(new Text(
                    "Press ESC to restart. Score: " + this.game.score() + " Ammo: " + this.game.ship.ammo, 0, 0));
            if (!this.game.ship.isActive()) {
                Text message = new Text(
                        "GAME OVER", this.viewSize.width() / 2, this.viewSize.height() /2);
                message.centered = true;
                this.textLayer.addText(message);
            }
            if (!this.game.ship.powerups.isEmpty()) {
                StringBuffer message = new StringBuffer();
                for (Map.Entry<Powerup.Type, Integer> entry : this.game.ship.powerups.entrySet()) {
                    message.append(entry.getKey().message);
                    if (entry.getKey().showTime) message.append(" (").append(entry.getValue()).append("). ");
                    else message.append(". ");
                }
                Point p = this.game.ship.primaryBody.getCenter();
                Text text = new Text(message.toString(),
                        (float) p.x * this.viewSize.width(),
                        (float) (p.y - 0.035) * this.viewSize.height());
                text.size = 10;
                text.centered = true;
                this.textLayer.addText(text);
            }
            this.textLayer.showText();
        }

        private void paintEntity(Surface surf, Entity entity) {
            if (entity instanceof Ship) {
                Ship ship = (Ship) entity;
                if (!ship.powerups.isEmpty()) {
                    // If the ship has powerups, paint it a random color from its powerups.
                    Powerup.Type a = new ArrayList<>(
                            ship.powerups.keySet()).get(this.game.rand.nextInt(ship.powerups.size()));
                    surf.setFillColor(a.color);
                } else surf.setFillColor(0xFF00FF00);
            }
            else if (entity instanceof Bullet) surf.setFillColor(0xFF2288AA);
            else if (entity instanceof Rock) surf.setFillColor(0xFFAAAAAA);
            else if (entity instanceof Powerup) surf.setFillColor(((Powerup) entity).type.color);
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

    // Represents a string text with associated coordinates.
    static class Text {
        String text;
        float x, y;
        float size;
        boolean centered;

        Text(String text, float x, float y) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.size = 16f;
            this.centered = false;
        }
    }

    static class TextLayer extends ImageLayer {
        private final Platform plat;
        private final List<Text> texts;

        TextLayer(Platform plat) {
            this.plat = plat;
            texts = new ArrayList<>();
        }

        void addText(Text text) {
            texts.add(text);
        }

        void showText() {
            Canvas canvas = this.plat.graphics().createCanvas(this.plat.graphics().viewSize);
            canvas.setFillColor(0x00000000).fillRect(0, 0, canvas.width, canvas.height);
            for (Text text: texts) {
                Font font = new Font("Helvetica", Font.Style.BOLD, text.size);
                TextFormat format = new TextFormat(font);
                TextLayout layout = this.plat.graphics().layoutText(text.text, format);
                canvas.setFillColor(0xFFFFFFFF).fillText(layout,
                        text.centered ? text.x - layout.size.width() / 2 : text.x, text.y);
            }
            this.setTile(canvas.toTexture());
            texts.clear();
        }
    }
}