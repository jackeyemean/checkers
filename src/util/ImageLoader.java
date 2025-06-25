// src/util/ImageLoader.java
package util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import javax.imageio.ImageIO;

public class ImageLoader {
    private static final String IMAGE_DIR = "images/";

    public enum Asset {
        MENU, BOARD, RED_PAWN, BLACK_PAWN, RED_KING, BLACK_KING,
        RED_SIDEBAR, BLACK_SIDEBAR, CONFIRM_RESIG,
        BLACK_WINS, RED_WINS, OFFER_DRAW, DRAW, RULES
    }

    private static final EnumMap<Asset,BufferedImage> map =
        new EnumMap<>(Asset.class);

    public static void loadAll() throws IOException {
        map.put(Asset.MENU,         load("menu.png"));
        map.put(Asset.BOARD,        load("board.png"));
        map.put(Asset.RED_PAWN,     load("redPawn.png"));
        map.put(Asset.BLACK_PAWN,   load("blackPawn.png"));
        map.put(Asset.RED_KING,     load("redKing.png"));
        map.put(Asset.BLACK_KING,   load("blackKing.png"));
        map.put(Asset.RED_SIDEBAR,  load("redSideBar.png"));
        map.put(Asset.BLACK_SIDEBAR,load("blackSideBar.png"));
        map.put(Asset.CONFIRM_RESIG,load("confirmResig.png"));
        map.put(Asset.BLACK_WINS,   load("blackWins.png"));
        map.put(Asset.RED_WINS,     load("redWins.png"));
        map.put(Asset.OFFER_DRAW,   load("offerDraw.png"));
        map.put(Asset.DRAW,         load("draw.png"));
        map.put(Asset.RULES,        load("rules.png"));
    }

    private static BufferedImage load(String filename) throws IOException {
        return ImageIO.read(new File(IMAGE_DIR + filename));
    }

    public static BufferedImage get(Asset a) {
        return map.get(a);
    }
}
