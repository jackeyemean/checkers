package view;

import javax.swing.*;
import java.awt.*;
import model.GameModel;
import util.ImageLoader;
import util.ImageLoader.Asset;

public class BoardPanel extends JPanel {
    private final GameModel model;

    public BoardPanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (model.getGameState()) {
            case MENU:
                g.drawImage(ImageLoader.get(Asset.MENU), 0, 0, null);
                break;
            case PLAYING:
                drawBoard(g);
                break;
            case BLACK_WIN:
            case WHITE_WIN:
            case DRAW:
                drawEndScreen(g);
                break;
            case OFFER_DRAW:
                g.drawImage(ImageLoader.get(Asset.OFFER_DRAW), 100, 158, null);
                break;
            case RULES_IN_GAME:
                g.drawImage(ImageLoader.get(Asset.RULES), 74, 75, null);
                break;
            case RESIGN_CONFIRM:
                g.drawImage(ImageLoader.get(Asset.CONFIRM_RESIG), 90, 150, null);
                break;
            case RULES_IN_MENU:
                g.drawImage(ImageLoader.get(Asset.RULES), 175, 75, null);
                break;
        }
    }

    private void drawBoard(Graphics buf) {
        buf.drawImage(ImageLoader.get(Asset.BOARD), 0, 0, null);
        buf.drawImage(
          model.getSelectedPiece() < 0
            ? ImageLoader.get(Asset.BLACK_SIDEBAR)
            : ImageLoader.get(Asset.RED_SIDEBAR),
          600, 0, null
        );

        if (model.isMoving()) {
            buf.setColor(new Color(15, 122, 15));
            buf.fillRect(
              model.getPieceX() * 75,
              model.getPieceY() * 75,
              75, 75
            );
        }

        int[][] grid = model.getGrid();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                switch (grid[y][x]) {
                    case  1: buf.drawImage(ImageLoader.get(Asset.RED_PAWN),   x*75, y*75, null); break;
                    case -1: buf.drawImage(ImageLoader.get(Asset.BLACK_PAWN), x*75, y*75, null); break;
                    case  2: buf.drawImage(ImageLoader.get(Asset.RED_KING),   x*75, y*75, null); break;
                    case -2: buf.drawImage(ImageLoader.get(Asset.BLACK_KING), x*75, y*75, null); break;
                }
            }
        }
    }

    private void drawEndScreen(Graphics buf) {
        Asset fill1 = (model.getGameState()==GameModel.State.BLACK_WIN || model.getGameState()==GameModel.State.DRAW)
                      ? Asset.BLACK_KING : Asset.RED_KING;
        Asset fill2 = (model.getGameState()==GameModel.State.WHITE_WIN)
                      ? Asset.RED_PAWN : Asset.BLACK_PAWN;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 8; j++) {
                boolean checker = ((i+j)%2)==0;
                buf.drawImage(
                  ImageLoader.get(checker ? fill1 : fill2),
                  i*75, j*75, null
                );
            }
        }
        Asset msg = (model.getGameState()==GameModel.State.BLACK_WIN)
                    ? Asset.BLACK_WINS
                  : (model.getGameState()==GameModel.State.WHITE_WIN)
                    ? Asset.RED_WINS
                    : Asset.DRAW;
        buf.drawImage(ImageLoader.get(msg), 225, 200, null);
    }
}
