// Copyright 2015 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//	
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package org.gtagency.autotetris.bot;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.TreeMap;

import org.gtagency.autotetris.moves.MoveType;
import org.gtagency.autotetris.field.Cell;
import org.gtagency.autotetris.field.Field;
import org.gtagency.autotetris.field.Shape;

/**
 * BotStarter class
 * 
 * This class is where the main logic should be. Implement getMoves() to
 * return something better than random moves.
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class BotStarter {

    public BotStarter() {}

    /**
     * Returns a random amount of random moves
     * @param state : current state of the bot
     * @param timeout : time to respond
     * @return : a list of moves to execute
     */
    public ArrayList<MoveType> getMoves(BotState state, long timeout) {
        ArrayList<MoveType> moves = new ArrayList<MoveType>();
        Field field = state.getMyField();
        Shape currentShape = field.liftShape(state.getCurrentShape(), state.getShapeLocation());
        Shape tempShape = new Shape(state.getCurrentShape(), field, state.getShapeLocation());

        ArrayList<Node> terminal = findTerminalStates(field, tempShape);
        TreeMap<Integer, Node> sortedTerminal = new TreeMap<Integer, Node>();
        for(Node i:terminal){
            tempShape.setLocation(i.x, i.y);
            while(tempShape.getOrientation()!=i.o){
                tempShape.turnRight();
            }
            sortedTerminal.put(eval(field, tempShape, state), i);
        }
        do{
            moves.clear();
            Node temp = sortedTerminal.pollFirstEntry().getValue();
            tempShape.setLocation(temp.x, temp.y);
            while(tempShape.getOrientation()!=temp.o){
                tempShape.turnRight();
            }
        }while(!findPath(moves, field, currentShape, tempShape));
        return moves;
    }

    private boolean findPath(ArrayList<MoveType> moves, Field field, Shape startShape, Shape targetShape){
        PriorityQueue startQ = new PriorityQueue();
        PriorityQueue endQ = new PriorityQueue();
        return true;    
    }

    private ArrayList<Node> findTerminalStates(Field field, Shape tempShape){
        ArrayList<Node> terminal = new ArrayList<>();
        for(int k=0; k<4; k++){
            for(int i=0; i<field.getWidth(); i++){
                for(int j=0; j<field.getHeight(); j++){
                    tempShape.setLocation(i, j);
                    if(!(tempShape.isOutOfBoundaries(field) || tempShape.hasCollision(field))){
                        tempShape.setLocation(i, j+1);
                        if(tempShape.isOutOfBoundaries(field) || tempShape.hasCollision(field)){
                            terminal.add(new Node(i,j,k));
                        }
                    }
                }
            }
            tempShape.turnRight();
        }
        return terminal;
    }

    private int eval(Field field, Shape tempShape, BotState state){
        Cell[] neighbors = new Cell[4];
        int h=0;
        for(int i=0; i<field.getWidth(); i++){
            for (int j=0; j<field.getHeight(); j++){
                Cell c = field.getCell(i,j);
                if(!c.isEmpty() || tempShape.isAt(c.getLocation())){
                    neighbors[0]=field.getCell(i-1,j);
                    neighbors[1]=field.getCell(i+1,j);
                    neighbors[2]=field.getCell(i,j+1);
                    neighbors[3]=field.getCell(i,j-1);

                    for (Cell n: neighbors){
                        if (n!= null && n.isEmpty() && !tempShape.isAt(n.getLocation())) {
                            h++;
                        }
                    }
                }
            }
        }
        return h;
    }


    public static void main(String[] args)
    {
        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }
    
    private static class Node{
        private int x;
        private int y;
        private int o;
        Node parent;
        
        public Node(int x, int y, int o){
            this.x=x;
            this.y=y;
            this.o=o;
        }
        
        @Override
        public boolean equals(Object n){
            if(!(n instanceof Node))
                return false;
            return (x == ((Node)n).x) && (y == ((Node)n).y) && (o == ((Node)n).o);
        }
        
    }
    
}
