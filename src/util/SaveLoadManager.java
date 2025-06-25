// src/util/SaveLoadManager.java
package util;

import java.io.*;
import java.util.Scanner;
import model.GameModel;

public class SaveLoadManager {
    /** Read the boolean from gameSaved.txt (first run → false) */
    public static boolean readGameSavedFlag() {
        try (Scanner in = new Scanner(new File("gameSaved.txt"))) {
            return Boolean.parseBoolean(in.next());
        } catch (IOException e) {
            return false;
        }
    }

    /** Overwrite gameSaved.txt with the given flag */
    public static void writeGameSavedFlag(boolean flag) {
        try (PrintWriter out = new PrintWriter("gameSaved.txt")) {
            out.print(flag);
        } catch (IOException e) { }
    }

    /** Save the current board & selectedPiece to savedGame.txt */
    public static void saveBoard(GameModel m) {
        try (PrintWriter out = new PrintWriter("savedGame.txt")) {
            int[][] g = m.getGrid();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    out.print(g[i][j] + (j==7? "\n":" "));
                }
            }
            out.print(m.getSelectedPiece());
        } catch (IOException e) { }
    }

    /** Load board & selectedPiece from savedGame.txt */
    public static void loadBoard(GameModel m) {
        try (Scanner in = new Scanner(new File("savedGame.txt"))) {
            int[][] g = m.getGrid();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    g[i][j] = in.nextInt();
                }
            }
            int sp = in.nextInt();
            m.setSelectedPiece(sp);
        } catch (IOException e) { }
    }
}
