
package org.gtagency.autotetris;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
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

import org.gtagency.autotetris.field.Field;
import org.gtagency.autotetris.field.Shape;
import org.gtagency.autotetris.field.ShapeType;

import java.lang.Thread;


public class TetrisGUI{
    private String playerName;
    private EnginePlayer player;

    private ArrayList<String> moves;
    private Timer timer;
    private Field board;
    private Shape nextPiece;
    private Shape fallingPiece;
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
    private final long TIMEBANK_MAX = 1000l;       // time bank each bot receives
    private final long FIELD_WIDTH = 10l;
    private final long FIELD_HEIGHT = 20l;
    private final Field testBoard = new Field(10, 20,
            "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "0,0,0,0,0,0,0,0,0,0;"+
                    "2,2,0,0,0,0,0,0,0,0;"+
            "2,2,0,0,0,0,0,0,0,0");
    /**
     * An interface that simulates a Tetris game for 1 player. 
     * Handles all IO with the bot and the game mechanics themselves
     * To run, first change the string on line 340 to execute the bot class file
     * To display messages from the bot, use System.err.println
     * Pause by pressing P
     * Change the test board on line 58, Load test board by pressing L
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
        player.sendInfo("settings player_names player1");
        player.sendInfo("settings your_bot " + player.getName());
        player.sendInfo("settings timebank " + TIMEBANK_MAX);
        player.sendInfo("settings time_per_move " + TIME_PER_MOVE); 
        player.sendInfo("settings field_width " + FIELD_WIDTH); 
        player.sendInfo("settings field_height " + FIELD_HEIGHT); 
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
        dumpScroll.setPreferredSize(new Dimension(300, 500));
        msgBox = new JTextArea(messages.toString());
        msgBox.setEditable(false);
        msgScroll = new JScrollPane(msgBox);
        msgScroll.setPreferredSize(new Dimension(300,200));
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
                if(e.getKeyChar() == 'l'){
                    board = testBoard;
                    messages.append("Test Board Loaded \n");
                    msgBox.setText(messages.toString());
                    newFallingPiece();
                    newFallingPiece();
                    drawBoard();
                }
                if(e.getKeyChar() == 'o'){
                    while(!moves.isEmpty()){
                        processMove(moves.remove(0));
                    }
                    dump.setText(player.getDump());
                    msgBox.setText(messages.toString());
                    frame.requestFocus();
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
                    player.sendInfo("update game this_piece_position " + fallingPiece.getLocation().x + "," + fallingPiece.getLocation().y);
                    StringBuilder field = new StringBuilder();
                    for(int i=0; i<board.getHeight(); i++){
                        for(int j=0; j<board.getWidth(); j++){
                            if (fallingPiece.isAt(new Point(j,i))){
                                field.append(1);
                            } else {
                                field.append(board.getCell(j, i).getState().ordinal());
                            }
                            if(j != board.getWidth()-1)
                                field.append(",");
                        }
                        if(i != board.getHeight()-1)
                            field.append(";");
                    }

                    player.sendInfo("update player1 field " + field);
                    messages.append("Requesting Moves\n");
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
        board=new Field((int)FIELD_WIDTH, (int)FIELD_HEIGHT);
        nextPiece=genRandomPiece();
        newFallingPiece();
        drawBoard();
        redrawPiece();
    }

    public Shape genRandomPiece(){
        ShapeType temp = ShapeType.values()[r.nextInt(7)];
        return new Shape(temp, board, temp.startPos());
    }

    public void newFallingPiece(){
        messages.append("New Piece\n");
        fallingPiece=nextPiece;
        nextPiece=genRandomPiece();
        player.sendInfo("update game this_piece_type " + fallingPiece.type);
        player.sendInfo("update game next_piece_type " + nextPiece.type);
        player.sendInfo("update game round " + ++round);
        nextPieceLabel.setText("Next Piece:  " + nextPiece.type);
    }
    public boolean movePieceDown(){
        fallingPiece.oneDown();
        if(fallingPiece.hasCollision(board) || fallingPiece.isOutOfBoundaries(board)){
            fallingPiece.oneUp();
            fallingPiece.place(board);
            board.removeFullRows();
            newFallingPiece();
            if(fallingPiece.hasCollision(board) || fallingPiece.isOutOfBoundaries(board)){
                timer.stop();
                messages.append("You Lose");
                msgBox.setText(messages.toString());
            }
            drawBoard();
            return false;
        }
        redrawPiece();
        return true;
    }
    public void drawCell(int width, int length, Color c){
        if(length >= 0 && width >=0)
            grid[length][width].setBackground(c);
    }

    public void drawBoard(){
        for(int i=0; i<board.getWidth(); i++){
            for(int j=0; j<board.getHeight(); j++){
                drawCell(i,j,board.getCell(i, j).getColor());
            }
        }
    }

    public void redrawPiece(){
        for(int i=fallingPiece.getLocation().x; i< fallingPiece.getLocation().x + fallingPiece.getSize(); i++){
            for(int j=fallingPiece.getLocation().y; j<fallingPiece.getLocation().y+ fallingPiece.getSize(); j++){
                if(fallingPiece.isAt(new Point(i, j)))
                    drawCell(i, j, fallingPiece.getType().color());
            }
        }
    }

    public void processMove(String s){
        messages.append(s + "\n");
        switch(s){
        case "left":
            fallingPiece.oneLeft();
            if(fallingPiece.hasCollision(board) || fallingPiece.isOutOfBoundaries(board))
                fallingPiece.oneRight();
            drawBoard();
            redrawPiece();
            break;
        case "right":
            fallingPiece.oneRight();
            if(fallingPiece.hasCollision(board) || fallingPiece.isOutOfBoundaries(board))
                fallingPiece.oneLeft();
            drawBoard();
            redrawPiece();
            break;
        case "down":
            movePieceDown();
            drawBoard();
            redrawPiece();
            break;
        case "turnleft":
            fallingPiece.turnLeft();
            if(fallingPiece.hasCollision(board) || fallingPiece.isOutOfBoundaries(board))
                fallingPiece.turnRight();
            drawBoard();
            redrawPiece();
            break;
        case "turnright":
            fallingPiece.turnRight();
            if(fallingPiece.hasCollision(board) || fallingPiece.isOutOfBoundaries(board))
                fallingPiece.turnLeft();
            drawBoard();
            redrawPiece();
            break;
        case "drop":
            while(movePieceDown());
            break;
        }
    }

    public static void main(String args[]) throws Exception
    {   
        IOPlayer player = new IOPlayer(Runtime.getRuntime().exec("java -cp bin org/gtagency/autotetris/bot/BotStarter"));
        player.run();
        new TetrisGUI().setupGame(player);
    }


}
