import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Asteroids");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GUI gui = new GUI(500, 500);
        frame.add(gui);
        frame.pack();
        frame.setVisible(true);
        gui.setFocusable(true);
        gui.requestFocusInWindow();
    }
}
