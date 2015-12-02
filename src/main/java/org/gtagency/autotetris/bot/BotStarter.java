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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import org.gtagency.autotetris.moves.MoveType;
import org.gtagency.autotetris.field.Field;
import org.gtagency.autotetris.field.Shape;
import org.gtagency.autotetris.field.ShapeType;

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
        Utility u = new PrimaryUtility();
        Field field = state.getMyField();
        Shape tempShape = field.liftShape(state.getCurrentShape(), state.getShapeLocation());
        Shape nextShape = new Shape(state.getNextShape(), field, new Point());

        ArrayList<Node> terminal = findTerminalStates(field, tempShape);
        PriorityQueue<Node> sortedTerminal = new PriorityQueue<Node>(new NodeComparator());
        for(Node i:terminal){
            i.u = Integer.MAX_VALUE;
            nextShape.setLocation(-1, 4);
            while(nextShape.getOrientation() != 0){
                nextShape.turnRight();
            }
            ArrayList<Node> secondDepth = findTerminalStates(i.f, nextShape);
            for(Node j: secondDepth){
                j.u = (int) u.value(j.f, i.cleared, j.cleared, state);
                if(j.u < i.u){
                    i.u = j.u;
                }
            }
            sortedTerminal.add(i);
        }

        Node temp = sortedTerminal.poll();
        System.err.println(temp.u);
        while(temp.parent != null){
            moves.add(0,temp.findMove(temp.parent));
            temp = temp.parent;
        }
        moves.add(MoveType.DOWN);
        return moves;
    }


    private ArrayList<Node> findTerminalStates(Field field, Shape tempShape){
        ArrayList<Node> terminal = new ArrayList<>();
        HashSet<Node> traversed = new HashSet<Node>();
        Node current = new Node(tempShape.getLocation().x, tempShape.getLocation().y, tempShape.getOrientation());
        traversed.add(current);
        Queue<Node> queue = new LinkedList<Node>();
        do{
            if(current.isValid(field, tempShape) && current.isTerminal(field, tempShape)){
                current.f = field.clone();
                tempShape.setLocation(current.x, current.y);
                while(tempShape.getOrientation() != current.o){
                    tempShape.turnRight();
                }
                tempShape.place(current.f);
                current.cleared = current.f.removeFullRows();
                terminal.add(current);
            }
            ArrayList<Node> branches = new ArrayList<Node>();
            for(int i=-1; i<3; i+=2){
                branches.add(new Node(current.x + i, current.y, current.o));
                branches.add(new Node(current.x, current.y, (current.o+4+i)%4));
            }
            branches.add(new Node(current.x, current.y + 1, current.o));

            for(Node n: branches){
                if(!traversed.contains(n) && n.isValid(field, tempShape)){
                    traversed.add(n);
                    n.parent = current;
                    queue.add(n);
                }
            }
            //System.out.println(current.x + "," + current.y + "," + current.o);
            current = queue.poll();
        } while(current != null);
        return terminal;
    }


    public static void main(String[] args)
    {

        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }

    private static class NodeComparator implements Comparator<Node>{

        @Override
        public int compare(Node o1, Node o2) {
            return o1.u-o2.u;
        }

    }

    private static class Node{
        private int x;
        private int y;
        private int o;
        private int u;

        private Field f;
        private int cleared;
        private Node parent;

        public Node(int x, int y, int o){
            this.x=x;
            this.y=y;
            this.o=o;
        }

        public MoveType findMove(Node n){//finds the move that turns n into this node
            if((o+1)%4 == n.o)
                return MoveType.TURNLEFT;
            if((o+3)%4 == n.o)
                return MoveType.TURNRIGHT;
            if (x - n.x == 1)
                return MoveType.RIGHT;
            if (x - n.x == -1)
                return MoveType.LEFT;
            return MoveType.DOWN;
        }

        public boolean isValid(Field f, Shape s){
            while(s.getOrientation() != o){
                s.turnRight();
            }
            s.setLocation(x, y);
            return !(s.hasCollision(f) || s.isOutOfBoundaries(f));
        }

        public boolean isTerminal(Field f, Shape s){//does NOT check if currently valid
            while(s.getOrientation() != o){
                s.turnRight();
            }
            s.setLocation(x, y+1);
            return (s.hasCollision(f) || s.isOutOfBoundaries(f));
        }

        @Override
        public boolean equals(Object n){
            if(!(n instanceof Node))
                return false;
            return (x == ((Node)n).x) && (y == ((Node)n).y) && (o == ((Node)n).o);
        }

        @Override
        public int hashCode(){
            return 10000*x+100*y+o;
        }

    }

}