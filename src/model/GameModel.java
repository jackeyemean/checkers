// src/model/GameModel.java
package model;

public class GameModel {
    public enum State {
        MENU, PLAYING, BLACK_WIN, WHITE_WIN,
        OFFER_DRAW, DRAW, RULES_IN_GAME,
        RESIGN_CONFIRM, RULES_IN_MENU
    }

    private State gameState;
    private boolean gameSaved, playingSaved;
    private boolean consecutiveCapture, moving;
    private int selectedPiece;
    private int pieceX, pieceY;
    private int[][] grid;

    public GameModel() {
        gameState = State.MENU;
        gameSaved = playingSaved = false;
        consecutiveCapture = moving = false;
        resetBoard();
    }

    public void resetBoard() {
        grid = new int[][] {
            { 0, 1, 0, 1, 0, 1, 0, 1 },
            { 1, 0, 1, 0, 1, 0, 1, 0 },
            { 0, 1, 0, 1, 0, 1, 0, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0 },
            {-1, 0,-1, 0,-1, 0,-1, 0 },
            { 0,-1, 0,-1, 0,-1, 0,-1 },
            {-1, 0,-1, 0,-1, 0,-1, 0 }
        };
        selectedPiece = -1;  // Black starts
        pieceX = pieceY = 0;
        consecutiveCapture = moving = false;
    }

    // ───── Getters / Setters ───────────────────────────────────────────────
    public State getGameState()            { return gameState; }
    public void setGameState(State s)      { gameState = s; }
    public boolean isGameSaved()           { return gameSaved; }
    public void setGameSaved(boolean f)    { gameSaved = f; }
    public boolean isPlayingSaved()        { return playingSaved; }
    public void setPlayingSaved(boolean f) { playingSaved = f; }
    public boolean isMoving()              { return moving; }
    public boolean isConsecutiveCapture()  { return consecutiveCapture; }
    public int getSelectedPiece()          { return selectedPiece; }
    public void setSelectedPiece(int sp)   { selectedPiece = sp; }
    public int getPieceX()                 { return pieceX; }
    public int getPieceY()                 { return pieceY; }
    public int[][] getGrid()               { return grid; }

    /** Called when the user clicks the board cell (bx,by). */
    public void handleBoardClick(int bx, int by) {
        if (!moving) {
            if (pieceClicked(bx, by)) {
                moving = true;
                selectedPiece = grid[by][bx];
                pieceX = bx; pieceY = by;
            }
        } else {
            grid = boardUpdater(bx, by, pieceX, pieceY, grid);
            moving = consecutiveCapture;
            // fixed: hasMove takes only turn, not grid+turn
            if (!hasMove(-1) && selectedPiece < 0) {
                gameState = State.WHITE_WIN;
            } else if (!hasMove(1) && selectedPiece > 0) {
                gameState = State.BLACK_WIN;
            }
        }
    }

    // ───── Core Move Logic ─────────────────────────────────────────────────
    private int[][] boardUpdater(int x,int y,int px,int py,int[][] g) {
        if (Math.abs(x-px)==2 && Math.abs(y-py)==2) 
            return capture(x,y,px,py,g);
        if (Math.abs(selectedPiece)==1 && !consecutiveCapture)
            return pawn(x,y,px,py,g);
        if (Math.abs(selectedPiece)==2 && !consecutiveCapture)
            return king(x,y,px,py,g);
        return g;
    }

    private int[][] pawn(int x,int y,int px,int py,int[][] g) {
        if ((x==px-1||x==px+1)
         && y==py+selectedPiece && g[y][x]==0) {
            g[py][px]=0;
            g[y][x]=promotionCheck(y,selectedPiece);
            selectedPiece *= -1;
        }
        return g;
    }

    private int[][] king(int x,int y,int px,int py,int[][] g) {
        if (g[y][x]==0 
         && Math.abs(x-px)==1 
         && Math.abs(y-py)==1) {
            g[py][px]=0;
            g[y][x]=selectedPiece;
            selectedPiece *= -1;
        }
        return g;
    }

    private int[][] capture(int x,int y,int px,int py,int[][] g) {
        if (g[y][x]!=0) return g;
        int mx=(px+x)/2, my=(py+y)/2;
        if (g[my][mx]*g[py][px]<0) {
            g[my][mx] = 0;
            g[py][px] = 0;
            g[y][x] = (Math.abs(selectedPiece)==1)
                       ? promotionCheck(y,selectedPiece)
                       : selectedPiece;
            selectedPiece = g[y][x];
            selectedPiece = anotherCapture(x,y,g);
        }
        return g;
    }

    private int anotherCapture(int x,int y,int[][] g) {
        int[][] dirs = {{-2,-2},{-2,2},{2,-2},{2,2}};
        for (int[] d: dirs) {
            int nx=x+d[0], ny=y+d[1],
                jx=x+d[0]/2, jy=y+d[1]/2;
            if (nx>=0&&nx<8&&ny>=0&&ny<8
             && g[jy][jx]*selectedPiece<0
             && g[ny][nx]==0
             && !((Math.abs(selectedPiece)==1)
                  && ((d[1]/2)*Integer.signum(selectedPiece)<0))
            ) {
                consecutiveCapture = true;
                pieceX = x; pieceY = y;
                return selectedPiece;
            }
        }
        consecutiveCapture = false;
        return selectedPiece * -1;
    }

    private int promotionCheck(int y,int p) {
        if ((y==0 && p<0) || (y==7 && p>0)) p *= 2;
        return p;
    }

    private boolean pieceClicked(int x,int y) {
        return (selectedPiece<0 && grid[y][x]<0)
            || (selectedPiece>0 && grid[y][x]>0);
    }

    /** Has any legal move for given turn (-1 black, +1 white)? */
    public boolean hasMove(int turn) {
        for (int i=0;i<8;i++) for (int j=0;j<8;j++) {
            int cell = grid[j][i]*turn;
            if (cell==1) {
                if (hasCapture(i,j,grid[j][i]) || hasPawnMove(i,j,grid[j][i]))
                    return true;
            }
            if (cell==2) {
                if (hasCapture(i,j,grid[j][i]) || hasKingMove(i,j,grid[j][i]))
                    return true;
            }
        }
        return false;
    }

    private boolean hasCapture(int x,int y,int p) {
        if (y-2>=0 && x-2>=0
         && grid[y-1][x-1]*p<0
         && grid[y-2][x-2]==0
         && p!=1) return true;
        if (y+2<=7 && x-2>=0
         && grid[y+1][x-1]*p<0
         && grid[y+2][x-2]==0
         && p!=-1) return true;
        if (y-2>=0 && x+2<=7
         && grid[y-1][x+1]*p<0
         && grid[y-2][x+2]==0
         && p!=1) return true;
        if (y+2<=7 && x+2<=7
         && grid[y+1][x+1]*p<0
         && grid[y+2][x+2]==0
         && p!=-1) return true;
        return false;
    }

    private boolean hasPawnMove(int x,int y,int p) {
        if (p<0 && y-1>=0 && x-1>=0 && grid[y-1][x-1]==0) return true;
        if (p>0 && y+1<=7 && x-1>=0 && grid[y+1][x-1]==0) return true;
        if (p<0 && y-1>=0 && x+1<=7 && grid[y-1][x+1]==0) return true;
        if (p>0 && y+1<=7 && x+1<=7 && grid[y+1][x+1]==0) return true;
        return false;
    }

    private boolean hasKingMove(int x,int y,int p) {
        if (y-1>=0 && x-1>=0 && grid[y-1][x-1]==0) return true;
        if (y+1<=7 && x-1>=0 && grid[y+1][x-1]==0) return true;
        if (y-1>=0 && x+1<=7 && grid[y-1][x+1]==0) return true;
        if (y+1<=7 && x+1<=7 && grid[y+1][x+1]==0) return true;
        return false;
    }
}
