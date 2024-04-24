import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Random;

public class ColorFlood extends Window {
    public static Random rnd = new Random();
    public static final int W = 1000, H = 700;
    public static Board board = new Board(new Board.Layout(12, 25));
    public static Stats stats = new Stats();
    public ColorFlood(){
        super("Color Flood", W, H);
        stats.startNewGame();
    }
    public void paintComponent(Graphics g){
        g.setColor(Color.white);
        g.fillRect(0, 0, 5000, 5000);
        g.setColor(Color.black);
        board.show(g);
        g.setColor(Color.black);
        stats.show(g);
        if(clickedColor > -1){
            g.setColor(Board.Layout.colors[clickedColor]);
            g.fillOval(900, 50, 50, 50);
        }
    }
    public static int clickedColor = -1;
    public void mousePressed(MouseEvent me){
        if(stats.again){
            stats.startNewGame();
        }else{
            int iC = board.iColor(me.getX(), me.getY());
            clickedColor = iC;
            board.flood(iC);
            stats.afterTurn();
        }
        repaint();
    }
    public static void main(String[]args){
        PANEL = new ColorFlood();
        Window.launch();
    }
    // ------------------- Board ----------------------
    public static class Board{
        public int [][] cells;
        public Layout layout;
        public Board(Layout layout){
            this.layout = layout;
            cells = layout.newCells();
            rndColors();
        }
        public void rndColors(){
            int nCell = layout.nCell;
            for(int iX = 0;iX < nCell;iX++){
                for(int iY = 0;iY < nCell;iY++){
                    cells[iX][iY] = rnd.nextInt(layout.nColor);
                }
            }
            while(cells[0][0] == cells[0][1] || cells[0][0] == cells[1][0]){
                cells[0][0] = rnd.nextInt(layout.nColor);
            }
        }
        public void show(Graphics g){
            int nCell = layout.nCell, cW = layout.cW;
            for(int iX = 0;iX < nCell;iX++){
                for(int iY = 0;iY < nCell;iY++){
                    g.setColor(Layout.colors[cells[iX][iY]]);
                    g.fillRect(layout.x(iX), layout.y(iY), cW, cW);
                }
            }
        }
        public int iColor(int x, int y){ // Return -1 for illegal click
            int iX = layout.iX(x), iY = layout.iY(y);
            if(iX == -1 || iY == -1 || iX >= layout.nCell || iY >= layout.nCell){
                return -1;
            }else{
                return cells[iX][iY];
            }
        }

        public static int ownedColor, targetColor;
        public void flood(int clickedColor){
            ownedColor = cells[0][0];
            targetColor = clickedColor;
            if(ownedColor == targetColor || targetColor == -1){return;}
            rFlood(0, 0);
        }
        public void rFlood(int iX, int iY){ // Recursive routine
            if(iX < 0 || iY < 0 || iX == layout.nCell || iY == layout.nCell || cells[iX][iY] != ownedColor){return;}
            cells[iX][iY] = targetColor;
            rFlood(iX - 1, iY);
            rFlood(iX + 1, iY);
            rFlood(iX, iY + 1);
            rFlood(iX, iY - 1);
        }
        public boolean isWon(){
            int temp = cells[0][0];
            for(int iX = 0;iX < layout.nCell; iX++){
                for(int iY = 0; iY < layout.nCell; iY++){
                    if(cells[iX][iY] != temp){
                        return false;
                    }
                }
            }
            return true;
        }
        // ------------------- Layout ----------------------
        public static class Layout{
            public int xOff, yOff; // Margins for color array
            public int nMoves, nCell, nColor = 6;
            public int cW; // cell Width
            public static Color[] colors = {Color.red, Color.blue, Color.green, Color.yellow, Color.pink, Color.cyan};

            public Layout(int nCell, int nMoves){
                this.nCell = nCell;
                this.nMoves = nMoves;
                cW = H/(nCell + 4);
                int cellsWidth = nCell*cW;
                xOff = (W - cellsWidth)/2;
                yOff = (H - cellsWidth)/2;
            }
            public int x(int iX){return xOff + iX*cW;}
            public int y(int iY){return yOff + iY*cW;}
            public int iX(int x){if(x < xOff){return -1;}return (x - xOff)/cW;}
            public int iY(int y){if(y < yOff){return -1;}return (y - yOff)/cW;}
            public int [][] newCells(){return new int[nCell][nCell];}
        }
    }

    // ------------------- Stats ----------------------
    public static class Stats{
        public int nMoves, nWon, nPlayed;
        public boolean again = false, won;
        public void show(Graphics g){
            if(again){
                int xOff = board.layout.xOff, yOff = board.layout.yOff;
                int wW = board.layout.nCell*board.layout.cW;
                g.setColor(Color.white);
                g.fillRect(xOff + 40, yOff + 40, wW - 80, wW - 80);
                g.setColor(Color.black);
                g.drawString("You " + (won? "WON!":"LOST!") + " Play Again?", W/2, H/2);
            }else{
                g.drawString("Moves Left: " + nMoves, 10, 20);
                g.drawString("You've won: " + nWon + " out of  " + nPlayed, 300, 20);
            }
        }
        public void afterTurn(){
            if(board.isWon()){
                won = true;
                nWon++;
                nPlayed++;
                again = true;
                return;
            }
            nMoves--;
            if(nMoves <= 0){
                won = false;
                nPlayed++;
                again = true;
                return;
            }
        }
        public void startNewGame(){
            nMoves = board.layout.nMoves;
            board.rndColors();
            again = false;
        }
    }
}

