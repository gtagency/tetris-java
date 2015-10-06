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

/**
 * BotStarter class
 * 
 * This class is where the main logic should be. Implement getMoves() to
 * return something better than random moves.
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class BotStarter implements Heuristic {

	public BotStarter() {}
	
	/**
	 * Returns a random amount of random moves
	 * @param state : current state of the bot
	 * @param timeout : time to respond
	 * @return : a list of moves to execute
	 */
	public ArrayList<MoveType> getMoves(BotState state, long timeout) {
		ArrayList<MoveType> moves = new ArrayList<MoveType>();
		Random rnd = new Random();
		
		int nrOfMoves = rnd.nextInt(41);
		List<MoveType> allMoves = Collections.unmodifiableList(Arrays.asList(MoveType.values()));
		for(int n=0; n<=nrOfMoves; n++) {
			moves.add(allMoves.get(rnd.nextInt(allMoves.size())));
		}
		
		return moves;
	}
	
	public int filledCells(Field f) {
		int total = 0;
		for (int i = height - 1; i > 0; i-- ) {
			for (int j = 0; j < width; j++) {
				Cell cell = f.getCell(i,j);
				if (!cell.isEmpty()) {
					total++;
				}
			}
		}
		return total;
	}
	
	public static void main(String[] args)
	{
		BotParser parser = new BotParser(new BotStarter());
		parser.run();
	}
	
	public int cost(Field field) {
		int width = field.getWidth();
		int height = field.getHeight();
		
		for (int i = height - 1; i > 0; i-- ) {
			for (int j = 0; j < width; j++) {
				Cell cell = field.getCell(i,j);
				if (cell.isSolid() || cell.isBlock()) {
					return i;
				}
			}
		}
		return 0;
	}
}
