import java.awt.Color;
import java.awt.Point;
/* represents individual Tetris pieces in the simulator
 * I translated this from an old Tetris program I wrote in python 
 * so some things are odd/bad
 * @author Mason Liu
 * */
public class Block{
    private boolean[][] grid; 
    public int row;
    public int col;
    public String type;
    private Board board;
    public Color color;

    public static final int IBLOCK=0;
    public static final int JBLOCK=1;
    public static final int LBLOCK=2;
    public static final int OBLOCK=3;
    public static final int SBLOCK=4;
    public static final int TBLOCK=5;
    public static final int ZBLOCK=6;

    public Block(int type, Board b){
        board=b;
        switch(type){
        case IBLOCK:
            grid=new boolean[][]{{true,true,true,true}};
            color=Color.CYAN;
            this.type="I";
            break;
        case JBLOCK:
            grid=new boolean[][]{{ true, false, false},
                { true, true,  true}};
                color=Color.PINK;
                this.type="J";
                break;
        case LBLOCK:
            grid=new boolean[][]{{ false, false, true},
                { true,  true,  true}};
                color=Color.ORANGE;
                this.type="L";
                break;
        case OBLOCK:
            grid= new boolean[][]{{ true, true},
                { true, true}};
                color=Color.YELLOW;
                this.type="O";
                break;
        case SBLOCK:
            grid=new boolean[][]{{ false, true, true},
                { true,  true, false }};
                color=Color.GREEN;
                this.type="S";
                break;
        case TBLOCK:
            grid= new boolean[][]{{ false, true, false },
                { true,  true, true}};
                color=Color.MAGENTA;
                this.type="T";
                break;
        case ZBLOCK:
            grid=new boolean[][]{{ true,  true, false },
                { false, true, true}};
                color=Color.RED;
                this.type="Z";
                break;
        }
        row=0;
        col=5-(grid[0].length+1)/2;
    }
    public boolean get(int w, int h){
        return grid[w][h];
    }
    public int length(){
        return grid.length;
    }
    public int width(){
        return grid[0].length;
    }
    public boolean rotate(boolean check){ //rotates LEFT
        boolean[][] tempGrid=grid;
        int tempCol=col;
        int tempRow=row;
        Point tempCenter=getCenter();
        grid= new boolean[tempGrid[0].length][tempGrid.length];
        for (int i=0; i<tempGrid.length; i++){
            for(int j=0; j<tempGrid[i].length; j++){
                grid[j][i]=tempGrid[i][tempGrid[0].length-1-j];
            }
        }
        Point newCenter=getCenter();
        row+=tempCenter.x-newCenter.x;
        col+=tempCenter.y-newCenter.y;
        if (check && !isValid()){
            grid=tempGrid;
            row=tempRow;
            col=tempCol;
            return false;
        }
        return true;
    }

    public Point getCenter(){
        return new Point(row+grid.length/2, col+grid[0].length/2);
    }

    public boolean isValid(){
        for(int i=0; i<grid.length; i++){
            for(int j=0; j<grid[i].length; j++){
                int tempRow=row+i;
                int tempCol=col+j;
                if(grid[i][j]==true && (tempRow<0 || tempCol<0 || tempRow>=board.length() || tempCol>=board.width() || !board.get(tempRow, tempCol).equals(Color.BLUE)))
                    return false;

            }
        }
        return true;
    }    
    public boolean isAt(int w, int h){
        for(int i=0; i<grid.length; i++){
            for(int j=0; j<grid[i].length; j++){
                int tempRow=row+i;
                int tempCol=col+j;
                if(grid[i][j]==true && tempRow == w && tempCol == h)
                    return true;
            }
        }
        return false;
    }
    public boolean move(int drow, int dcol){
        row+=drow;
        col+=dcol;
        if(!isValid()){
            row-=drow;
            col-=dcol;
            return false;
        }
        return true;
    }

    public void place(){
        for(int i=0; i<grid.length; i++){
            for(int j=0; j<grid[i].length; j++){
                if(grid[i][j]==true)
                    board.set(i+row, j+col, color);
            }}
    }

}
