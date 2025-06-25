package controller;

import java.awt.event.*;
import model.GameModel;
import model.GameModel.State;
import util.SaveLoadManager;
import view.BoardPanel;

public class GameController implements MouseListener {
    private final GameModel model;
    private final BoardPanel view;

    public GameController(GameModel m, BoardPanel v) {
        model = m;
        view  = v;
        v.addMouseListener(this);

        // on startup, load saved-game flag & board if present
        boolean saved = SaveLoadManager.readGameSavedFlag();
        model.setGameSaved(saved);
        if (saved) {
            SaveLoadManager.loadBoard(model);
            model.setPlayingSaved(true);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        State s = model.getGameState();

        if (s == State.MENU) {
            if (x>=255&&x<=547&&y>=186&&y<=221) {
                model.resetBoard();
                model.setPlayingSaved(false);
                model.setGameState(State.PLAYING);
            } else if (x>=152&&x<=651&&y>=299&&y<=334 && model.isGameSaved()) {
                SaveLoadManager.loadBoard(model);
                model.setPlayingSaved(true);
                model.setGameState(State.PLAYING);
            } else if (x>=309&&x<=492&&y>=423&&y<=459) {
                model.setGameState(State.RULES_IN_MENU);
            }
        }
        else if (s == State.PLAYING) {
            if (x > 600) {
                if (x>=650&&x<=753&&y>=256&&y<=322) {
                    model.setGameState(State.OFFER_DRAW);
                } else if (x>=638&&x<=760&&y>=358&&y<=389) {
                    model.setGameState(State.RESIGN_CONFIRM);
                } else if (x>=643&&x<=756&&y>=427&&y<=462) {
                    model.setGameState(State.RULES_IN_GAME);
                } else if (x>=651&&x<=752&&y>=505&&y<=577) {
                    SaveLoadManager.saveBoard(model);
                    model.setGameSaved(true);
                    SaveLoadManager.writeGameSavedFlag(true);
                    model.setGameState(State.MENU);
                }
            } else {
                model.handleBoardClick(x/75, y/75);
            }
        }
        else if (s == State.BLACK_WIN || s == State.WHITE_WIN) {
            if (x>=232&&x<=365&&y>=333&&y<=377) {
                model.setGameState(State.MENU);
            } else if (x>=447&&x<=560&&y>=333&&y<=377) {
                SaveLoadManager.writeGameSavedFlag(model.isGameSaved());
                System.exit(0);
            }
        }
        else if (s == State.OFFER_DRAW) {
            if (x>=143&&x<=263&&y>=345&&y<=403) {
                model.setGameState(State.DRAW);
            } else if (x>=354&&x<=443&&y>=345&&y<=407) {
                model.setGameState(State.PLAYING);
            }
        }
        else if (s == State.DRAW) {
            if (x>=236&&x<=384&&y>=353&&y<=403) {
                model.setGameState(State.MENU);
            } else if (x>=474&&x<=596&&y>=351&&y<=401) {
                SaveLoadManager.writeGameSavedFlag(model.isGameSaved());
                System.exit(0);
            }
        }
        else if (s == State.RULES_IN_GAME) {
            if (x>=258&&x<=483&&y>=341&&y<=513) {
                model.setGameState(State.PLAYING);
            }
        }
        else if (s == State.RESIGN_CONFIRM) {
            if (x>=134&&x<=260&&y>=349&&y<=413) {
                model.setGameState(
                  model.getSelectedPiece()>0 ? State.BLACK_WIN : State.WHITE_WIN
                );
            } else if (x>=359&&x<=453&&y>=349&&y<=413) {
                model.setGameState(State.PLAYING);
            }
        }
        else if (s == State.RULES_IN_MENU) {
            if (x>=360&&x<=441&&y>=483&&y<=513) {
                model.setGameState(State.MENU);
            }
        }

        view.repaint();
    }

    @Override public void mouseClicked(MouseEvent e)  {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e)  {}
    @Override public void mouseExited(MouseEvent e)   {}
}
