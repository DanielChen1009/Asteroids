import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class GUI extends JPanel implements KeyListener, ActionListener {
    private int width, height;
    private Timer timer;
    private Game game;

    public GUI(int width, int height) {
        this.width = width;
        this.height = height;
        this.game = new Game(this.width, this.height);
        timer = new Timer(1, this);
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
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(game.getShip().p1.x, game.getShip().p1.y, game.getShip().p2.x, game.getShip().p2.y);
        g2d.drawLine(game.getShip().p1.x, game.getShip().p1.y, game.getShip().p3.x, game.getShip().p3.y);
        g2d.drawLine(game.getShip().p3.x, game.getShip().p3.y, game.getShip() .p2.x, game.getShip().p2.y);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.game.getShip().p1.x = 10;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
