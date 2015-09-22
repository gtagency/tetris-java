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
import java.util.Scanner;

import org.gtagency.autotetris.moves.MoveType;

/**
 * BotParser class
 * 
 * Main class that will keep reading output from the engine.
 * Will either update the bot state or get actions.
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class BotParser {
	
	final Scanner scan;
	
	final BotStarter bot;
	
	BotState currentState;
	
	public BotParser(BotStarter bot)
	{
		this.scan = new Scanner(System.in);
		this.bot = bot;
		this.currentState = new BotState();
	}
	
	public void run()
	{
		while(scan.hasNextLine())
		{
			String line = scan.nextLine().trim();
			if(line.length() == 0) { continue; }
			String[] parts = line.split(" ");
			switch(parts[0]) {
				case "settings":
					this.currentState.updateSettings(parts[1], parts[2]);
					break;
				case "update":
					this.currentState.updateState(parts[1], parts[2], parts[3]);
					break;
				case "action":
					StringBuffer output = new StringBuffer();
					String moveJoin = "";
					
					ArrayList<MoveType> moves = bot.getMoves(currentState, Long.valueOf(parts[2]));
					
					if(moves.size() > 0)
						for(MoveType move : moves) {
							output.append(moveJoin);
							output.append(move.toString());
							moveJoin = ",";
						}
					else
						output.append("no_moves");
					
					System.out.println(output);
					break;
				default:
					System.err.printf("Unable to parse line '%s'\n", line);
			}
		}
	}

}
