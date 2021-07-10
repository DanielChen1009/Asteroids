package net.danielchen.core;

import org.jbox2d.common.Vec2;
import playn.core.*;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;
import playn.scene.Layer;
import playn.scene.Pointer;
import pythagoras.f.IDimension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameView extends GroupLayer {
    private final Platform plat;

    public GameView(Game game, Platform plat) {
        this.plat = plat;
        TextLayer textLayer = new TextLayer(plat);
        this.addAt(new GameLayer(game, plat, textLayer), 0, 0);
        this.addAt(textLayer, 0, 0);

        this.events().connect(new Pointer.Listener() {
            @Override
            public void onStart(Pointer.Interaction event) {
                game.processPointerStart(event.x() / GameView.this.width(),
                        event.y() / GameView.this.height());
            }

            @Override
            public void onEnd(Pointer.Interaction event) {
                game.processPointerEnd(event.x() / GameView.this.width(),
                        event.y() / GameView.this.height());
            }

            @Override
            public void onDrag(Pointer.Interaction event) {
                game.processPointerDrag(event.x() / GameView.this.width(),
                        event.y() / GameView.this.height());
            }
        });
    }

    @Override
    public float width() {
        return this.plat.graphics().viewSize.width();
    }

    @Override
    public float height() {
        return this.plat.graphics().viewSize.height();
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

        // we want two extra pixels in width/height to account for the grid
        // lines
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
            surf.fillRect(0, 0, this.width(), this.height());
            for (Entity entity : this.game.getEntities())
                this.paintEntity(surf, entity);
            this.textLayer.addText(new Text("Score: " + this.game
                    .score() + " Ammo: " + this.game.ship.ammo + " - Press " + "ESC " + "to restart",
                    5, 5));
            if (!this.game.ship.active) {
                Text message = new Text("GAME OVER", this.viewSize.width() / 2,
                        this.viewSize.height() / 2);
                message.centered = true;
                this.textLayer.addText(message);
            }
            else {
                StringBuilder message = new StringBuilder();
                if (this.game.ship.ammo <= 0) {
                    message.append("Out of ammo! ");
                }
                else if (this.game.ship.ammo < Config.LOW_AMMO_THRESHOLD) {
                    message.append("Low ammo! ");
                }
                if (this.game.ship.extraLives > 0) {
                    message.append("Extra lives: ")
                            .append(this.game.ship.extraLives).append(". ");
                }
                if (!this.game.ship.powerups.isEmpty()) {
                    for (Map.Entry<Powerup.Type, Integer> entry :
                            this.game.ship.powerups
                            .entrySet()) {
                        message.append(entry.getKey().message);
                        if (entry.getKey().showTime)
                            message.append(" (").append(entry.getValue())
                                    .append("). ");
                        else
                            message.append(". ");
                    }
                }
                if (message.length() > 0) {
                    Vec2 p = this.game.ship.primaryBody.getCenter();
                    Text text = new Text(message.toString(),
                            p.x * this.viewSize.width(),
                            (p.y - Config.SHIP_MESSAGE_VERTICAL_OFFSET) * this.viewSize
                                    .height());
                    text.size = 10;
                    text.centered = true;
                    this.textLayer.addText(text);
                }
            }
            this.textLayer.showText();
        }

        private void paintEntity(Surface surf, Entity entity) {
            surf.setAlpha(1f);
            if (entity instanceof Ship) {
                Ship ship = (Ship) entity;
                if (!ship.powerups.isEmpty()) {
                    // If the ship has powerups, paint it a random color from
                    // its powerups to give a flickering effect.
                    List<Integer> choices = new ArrayList<>(
                            ship.powerups.keySet()).stream()
                            .map((Powerup.Type type) -> type.color)
                            .collect(Collectors.toList());
                    choices.add(Config.SHIP_COLOR);
                    surf.setFillColor(choices.get(
                            this.game.rand.nextInt(choices.size())));
                }
                else
                    surf.setFillColor(Config.SHIP_COLOR);
            }
            else if (entity instanceof Bullet)
                surf.setFillColor(Config.BULLET_COLOR);
            else if (entity instanceof Rock) {
                surf.setFillColor(Config.ROCK_COLOR);
                Rock rock = (Rock) entity;
                // Give a fading effect to debris.
                surf.setAlpha(rock.isDebris ?
                        (float) rock.lifetime / Config.DEBRIS_LIFETIME : 1f);
            }
            else if (entity instanceof Powerup)
                surf.setFillColor(((Powerup) entity).type.color);
            else
                surf.setFillColor(Config.UNKNOWN_COLOR);
            this.paintBody(surf, entity.primaryBody);
            for (EntityBody wrapBody : entity.wrapBodies.values()) {
                this.paintBody(surf, wrapBody);
            }
        }

        private void paintBody(Surface surf, EntityBody body) {
            Vec2[] points = body.getPoints();
            int size = points.length;
            for (int i = 0; i < size; ++i) {
                surf.drawLine((points[i % size].x) * this.width(),
                        (points[i % size].y) * this.height(),
                        (points[(i + 1) % size].x) * this.width(),
                        (points[(i + 1) % size].y) * this.height(),
                        Config.LINE_WIDTH);
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
            this.texts = new ArrayList<>();
        }

        void addText(Text text) {
            this.texts.add(text);
        }

        void showText() {
            Canvas canvas = this.plat.graphics()
                    .createCanvas(this.plat.graphics().viewSize);
            canvas.setFillColor(0x00000000)
                    .fillRect(0, 0, canvas.width, canvas.height);
            for (Text text : this.texts) {
                Font font = new Font("Helvetica", Font.Style.BOLD, text.size);
                TextFormat format = new TextFormat(font);
                TextLayout layout = this.plat.graphics()
                        .layoutText(text.text, format);
                canvas.setFillColor(0xFFFFFFFF).fillText(layout,
                        text.centered ? text.x - layout.size.width() / 2 :
                                text.x, text.y);
            }
            this.setTile(canvas.toTexture());
            this.texts.clear();
        }
    }
}