
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.Border;

import java.lang.Thread;

public class TetrisGUI{
    private String playerName;
    private EnginePlayer player;

    private ArrayList<String> moves;
    private Timer timer;
    private Board board;
    private Block nextPiece;
    private Block fallingPiece;
    private Random r;
    private int round;
    private StringBuilder messages;

    private JFrame frame;
    private JPanel boardPanel;
    private JPanel refPanel;
    private JLabel[][] grid;
    private JTextArea dump;
    private JTextArea msgBox;
    private JScrollPane dumpScroll;
    private JScrollPane msgScroll;
    private JLabel nextPieceLabel;
    private static final Border BORDER= BorderFactory.createLineBorder(Color.BLACK, 2);

    private final long TIME_PER_MOVE = 500l;        // time in milliseconds that bots get per move
    private final long TIMEBANK_MAX = 10000l;       // time bank each bot receives
    private final long FIELD_WIDTH = 10l;
    private final long FIELD_HEIGHT = 20l;
    
    /*
     * An interface that simulates a Tetris game for 1 player. 
     * Handles all IO with the bot and the game mechanics themselves
     * To run, first change the string on line 307 to execute the bot class file
     * To display messages from the bot, use System.err.println
     * Pause by pressing P
     * @author Mason Liu
     * */
    public TetrisGUI()
    {
        messages = new StringBuilder();
        this.playerName = "player1";
        round = -1;
        r=new Random();
    }


    public void setupGame(IOPlayer ioPlayer) throws IOException
    {
        messages.append("setting up game... \n");

        player = new EnginePlayer(playerName, ioPlayer, TIMEBANK_MAX, TIME_PER_MOVE);

        // start the match player and send setup info to bots
        messages.append("starting game ... \n");

        // set the timebank to maximum amount to start with and send timebank info

        player.setTimeBank(TIMEBANK_MAX);
        sendSettings(player);
        finishSetUp();
    }



    // close the bot processes, save, exit program
    public void finish() throws Exception
    {

        player.getBot().finish();

        Thread.sleep(100);

        System.out.println("Done.");

        System.exit(0);
    }

    private void sendSettings(EnginePlayer player)
    {
        player.sendInfo("settings your_bot " + player.getName());
        player.sendInfo("settings timebank " + TIMEBANK_MAX);
        player.sendInfo("settings time_per_move " + TIME_PER_MOVE); 
        player.sendInfo("settings field_width " + FIELD_WIDTH); 
        player.sendInfo("settings field_height " + FIELD_HEIGHT); 
        player.sendInfo("settings player_names player1");
    }

    public void finishSetUp(){
        moves= new ArrayList<String>();
        frame = new JFrame("Tetris");
        boardPanel = new JPanel();
        refPanel = new JPanel();
        nextPieceLabel = new JLabel();
        frame.setSize(700, 550);
        frame.setLayout(new BorderLayout());
        boardPanel.setLayout(new GridLayout(20,10));
        boardPanel.setPreferredSize(new Dimension(300,550));
        refPanel.setLayout(new BorderLayout());
        refPanel.setPreferredSize(new Dimension(300,550));
        frame.setResizable(false);
        dump= new JTextArea(player.getDump());
        dumpScroll = new JScrollPane(dump);
        dump.setEditable(false);
        dumpScroll.setPreferredSize(new Dimension(300, 300));
        msgBox = new JTextArea(messages.toString());
        msgBox.setEditable(false);
        msgScroll = new JScrollPane(msgBox);
        msgScroll.setPreferredSize(new Dimension(300,300));
        refPanel.add(dumpScroll, BorderLayout.CENTER);
        refPanel.add(nextPieceLabel, BorderLayout.NORTH);
        refPanel.add(msgScroll, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(boardPanel, BorderLayout.WEST);
        frame.add(refPanel, BorderLayout.EAST);
        frame.setAlwaysOnTop(true);
        frame.setFocusable(true);
        frame.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == 'p'){
                    if(timer.isRunning()){
                        timer.stop();
                        messages.append("Game Paused \n");
                        msgBox.setText(messages.toString());
                    } else {
                        messages.append("Game Restarted \n");
                        msgBox.setText(messages.toString());
                        timer.start();
                    }
                }
            }
            @Override public void keyPressed(KeyEvent e) {}
            @Override public void keyReleased(KeyEvent e) {}
        });
        grid= new JLabel[20][10];
        for(int i=0; i<grid.length; i++){
            for(int j=0; j<grid[i].length; j++){
                grid[i][j]= new JLabel();
                grid[i][j].setBackground(Color.BLUE);
                grid[i][j].setBorder(BORDER);
                grid[i][j].setOpaque(true);
                boardPanel.add(grid[i][j]);
            }
        }
        timer= new Timer(500, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(moves.size()==0){
                    player.sendInfo("update game this_piece_position " + fallingPiece.row + "," + fallingPiece.col);
                    StringBuilder field = new StringBuilder();
                    for(int i=0; i<board.length(); i++){
                        for(int j=0; j<board.width(); j++){
                            if (fallingPiece.isAt(i, j)){
                                field.append(1);
                            } else if(!board.get(i, j).equals(Color.BLUE)){
                                field.append(2);
                            } else {
                                field.append(0);
                            }
                            if(j != board.width()-1)
                                field.append(",");
                        }
                        if(i != board.length()-1)
                            field.append(";");
                    }
                    
                    player.sendInfo("update player1 field " + field);
                    String input = player.requestMove();
                    String[] inputs = input.split(",");
                    for(String move: inputs)
                        moves.add(move);
                }
                processMove(moves.remove(0));
                dump.setText(player.getDump());
                msgBox.setText(messages.toString());
                frame.requestFocus();
            }
        });
        timer.setRepeats(true);
        timer.start();
        frame.setVisible(true);
        board=new Board();
        /*c.data.trash=[]
        c.data.rowsToSend=0
        c.data.rowsToReceive=0*/
        nextPiece=genRandomPiece();
        newFallingPiece();
        drawBoard();
        redrawPiece();
    }

    public Block genRandomPiece(){
        return new Block(r.nextInt(7), board);
    }

    public void newFallingPiece(){
        fallingPiece=nextPiece;
        nextPiece=genRandomPiece();
        player.sendInfo("update game this_piece_type " + fallingPiece.type);
        player.sendInfo("update game next_piece_type " + nextPiece.type);
        player.sendInfo("update game round " + ++round);
        nextPieceLabel.setText("Next Piece:  " + nextPiece.type);
    }
    public void movePieceDown(){
        if (!fallingPiece.move(1, 0)){
            fallingPiece.place();
            board.removeFullRows();
            //board.addReceivedRows()
            newFallingPiece();
            if(!fallingPiece.isValid()){
                timer.stop();
                messages.append("You Lose");
                msgBox.setText(messages.toString());
            }
            drawBoard();
        }
        redrawPiece();
    }
    public void drawCell(int row, int col, Color c){
        grid[row][col].setBackground(c);
    }

    public void drawBoard(){
        for(int i=0; i<board.length(); i++){
            for(int j=0; j<board.width(); j++){
                drawCell(i,j,board.get(i, j));
            }
        }
    }


    public void redrawPiece(){
        //while len(c.data.trash)>0:
        //   c.delete(c.data.trash.pop()) 
        for(int i=0; i<fallingPiece.length(); i++){
            for(int j=0; j<fallingPiece.width(); j++){
                if(fallingPiece.get(i, j)==true)
                    drawCell(i+fallingPiece.row, j+fallingPiece.col, fallingPiece.color);
            }}
    }
    public void processMove(String s){
        messages.append(s + "\n");
        switch(s){
        case "left":
            fallingPiece.move(0, -1);
            drawBoard();
            redrawPiece();
            break;
        case "right":
            fallingPiece.move(0, 1);
            drawBoard();
            redrawPiece();
            break;
        case "down":
            movePieceDown();
            drawBoard();
            redrawPiece();
            break;
        case "turnleft":
            fallingPiece.rotate(true);
            drawBoard();
            redrawPiece();
            break;
        case "turnright":
            fallingPiece.rotate(false);
            fallingPiece.rotate(false);
            if (!fallingPiece.rotate(true)){
                fallingPiece.rotate(false);
                fallingPiece.rotate(true);
            }
            drawBoard();
            redrawPiece();
            break;
        case "drop":
            while(fallingPiece.move(1, 0));
            movePieceDown();
            break;
        }
    }

    public static void main(String args[]) throws Exception
    {   
        IOPlayer player = new IOPlayer(Runtime.getRuntime().exec("java -cp bin bot/BotStarter"));
        player.run();
        new TetrisGUI().setupGame(player);
    }


}