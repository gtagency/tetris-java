import java.awt.Color;
/* represents the Tetris field in the simulator
 * I translated this from an old Tetris program I wrote in python 
 * so some things are odd/bad
 * @author Mason Liu
 * */
public class Board{
    private Color[][] grid;

    public Board(){
        grid = new Color[20][10];
        for(int i=0; i<grid.length; i++){
            for(int j=0; j<grid[i].length; j++){
                grid[i][j]=Color.BLUE;
            }
        }
    }
    public Board(int[][] g){
        grid = new Color[20][10];
        for(int i=0; i<grid.length; i++){
            for(int j=0; j<grid[i].length; j++){
                if(g[i][j] == 0){
                    grid[i][j]=Color.BLUE;
                } else {
                    grid[i][j]=Color.BLACK;
                }
            }
        }
    }
    public Color get(int w, int h){
        return grid[w][h];
    }
    public void set(int w, int h, Color c){
        grid[w][h]=c;
    }
    public int length(){
        return grid.length;
    }
    public int width(){
        return grid[0].length;
    }
    public void removeFullRows(){
        int oldRow=grid.length-1;
        int newRow=oldRow;
        //int count=0;
        int temp;
        while (newRow>=0){
            while (oldRow>=0 && isFull(oldRow)){
                oldRow-=1;
            }
            if (oldRow>=0){
                grid[newRow]=grid[oldRow];
            }
            else{
                //count+=1;
                temp=grid[newRow].length;
                grid[newRow]=new Color[temp];
                for(int i=0; i<temp; i++){
                    grid[newRow][i]=Color.BLUE;
                }
            }
            newRow-=1;
            oldRow-=1;
        }
        //c.data.rowsToSend+=2*max(0,count-1)
    }

    public boolean isFull(int row){
        for (int i=0; i<grid[row].length; i++){
            if(grid[row][i].equals(Color.BLUE))
                return false;
        }
        return true;
    }

    /*def addReceivedRows(self):
            if c.data.rowsToReceive>0:
                x=0
                for x in xrange(len(self)-c.data.rowsToReceive):
                    self[x]=self[x+c.data.rowsToReceive]
                x+=1
                while x<len(self):
                    temp=len(self[x])
                    self[x]=[]
                    for y in xrange(temp):
                        self[x].append("grey")
                    self[x][random.randint(0, len(self[x])-1)]="blue"
                    x+=1
                c.data.rowsToReceive=0
     */
}