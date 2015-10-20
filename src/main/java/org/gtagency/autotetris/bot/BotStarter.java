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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.gtagency.autotetris.moves.MoveType;
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

        List<MoveType> allMoves = Collections.unmodifiableList(Arrays.asList(MoveType.values()));
        double score = 0;
        MoveType bestMove = MoveType.DROP;
        Field field = state.getMyField();
        Shape shape = new Shape(state.getCurrentShape(), field,
            state.getShapeLocation());
        for (MoveType move : MoveType.values()) {
            Field newField = BotState.getNextField(field, shape, move);
            double u = utility(newField, shape);
            if ( u >= score ) {
                bestMove = move;
                score = u;
            }
        }
        final MoveType bestM = bestMove;
        return new ArrayList() {{ add(bestM); }};
    }

    private double utility(Field f, Shape shape) {
        int shapeY = shape.getLocation().y;
        for(int y = f.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < f.getWidth(); x++) {
                if (f.getCell(x, y).isBlock() || f.getCell(x,y).isSolid()) {
                    return shapeY - y; 
                }
            }

        }

        return shapeY;
    }

    public static void main(String[] args)
    {
        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }
}
