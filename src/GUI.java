import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;


public class GUI extends JPanel implements KeyListener, ActionListener {
    private int width, height;
    private Timer timer;
    private Game game;
    private JLabel label;

    public GUI(int width, int height) {
        this.width = width;
        this.height = height;
        this.addKeyListener(this);
        this.game = new Game(this.width, this.height);
        label = new JLabel("Angle" + this.game.getShip().getAngle());
        timer = new Timer(30, this);
        timer.start();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.width, this.height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.width, this.height);
        g.setColor(Color.WHITE);
        Graphics2D g2d = (Graphics2D) g;
        int size = this.game.getShip().points.size();
        for (int i = 0; i < size; ++i) {
            g2d.draw(new Line2D.Double(this.game.getShip().points.get(i % size).x, this.game.getShip().points.get(i % size).y, this.game.getShip().points.get((i + 1) % size).x, this.game.getShip().points.get((i + 1) % size).y));
        }
        if (this.game.isTransitioning && this.game.transitionShip != null) {
            for (int i = 0; i < this.game.transitionShip.points.size(); ++i) {
                g2d.draw(new Line2D.Double(this.game.transitionShip.points.get(i % size).x, this.game.transitionShip.points.get(i % size).y, this.game.transitionShip.points.get((i + 1) % size).x, this.game.transitionShip.points.get((i + 1) % size).y));
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) this.game.setAccelerating(true);
        if (e.getKeyCode() == KeyEvent.VK_LEFT) this.game.setTurningLeft(true);
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) this.game.setTurningRight(true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) this.game.setAccelerating(false);
        if (e.getKeyCode() == KeyEvent.VK_LEFT) this.game.setTurningLeft(false);
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) this.game.setTurningRight(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.add(label);
        this.game.update();
        this.repaint();
    }
}
