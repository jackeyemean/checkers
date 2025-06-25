// src/Checkers.java
import javax.swing.*;
import model.GameModel;
import view.BoardPanel;
import controller.GameController;
import util.ImageLoader;

public class Checkers {
    public static void main(String[] args) throws Exception {
        ImageLoader.loadAll();
        GameModel model      = new GameModel();
        BoardPanel view      = new BoardPanel(model);
        new GameController(model, view);

        JFrame frame = new JFrame("Checkers");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.pack();
        frame.setVisible(true);
    }
}
