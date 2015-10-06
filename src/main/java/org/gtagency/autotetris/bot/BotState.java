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
import java.util.HashMap;
import java.util.Map;

import org.gtagency.autotetris.player.Player;

import org.gtagency.autotetris.field.CellType;
import org.gtagency.autotetris.field.Cell;
import org.gtagency.autotetris.field.Field;
import org.gtagency.autotetris.field.ShapeType;
import org.gtagency.autotetris.moves.MoveType;

/**
 * BotState class
 * 
 * In this class all the information about the game is stored.
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class BotState {
	
	private int round;
	private int timebank;
	private HashMap<String, Player> players;
	private Player myBot;
	private ShapeType currentShape;
	private ShapeType nextShape;
	private Point shapeLocation;
	
	private int MAX_TIMEBANK;
	private int TIME_PER_MOVE;
	private int FIELD_WIDTH;
	private int FIELD_HEIGHT;

	public BotState() {
		this.round = 0;
		this.players = new HashMap<String, Player>();
	}
	
	public void updateSettings(String key, String value) {
		switch(key) {
			case "timebank":
				this.MAX_TIMEBANK = Integer.parseInt(value);
				timebank = MAX_TIMEBANK;
				break;
			case "time_per_move":
				this.TIME_PER_MOVE = Integer.parseInt(value);
				break;
			case "player_names":
				String[] playerNames = value.split(",");
				for(int i=0; i<playerNames.length; i++)
					players.put(playerNames[i], new Player(playerNames[i]));
				break;
			case "your_bot":
				this.myBot = players.get(value); 
				break;
			case "field_width":
				this.FIELD_WIDTH = Integer.parseInt(value);
				break;
			case "field_height":
				this.FIELD_HEIGHT = Integer.parseInt(value);
				break;
			default:
				System.err.printf("Cannot parse settings with key \"%s\"\n", key);
				break;
		}
	}
	
	public void updateState(String player, String key, String value) {
		switch(key) {
			case "round":
				this.round = Integer.parseInt(value);
				break;
			case "this_piece_type":
				this.currentShape = ShapeType.valueOf(value);
				break;
			case "next_piece_type":
				this.nextShape = ShapeType.valueOf(value);
				break;
			case "row_points":
				this.players.get(player).setPoints(Integer.parseInt(value));
				break;
			case "combo":
				this.players.get(player).setCombo(Integer.parseInt(value));
				break;
			case "field":
				this.players.get(player).setField(new Field(this.FIELD_WIDTH, this.FIELD_HEIGHT, value));
				break;
			case "this_piece_position":
				String[] split = value.split(",");
				this.shapeLocation = new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
				break;
			default:
				System.err.printf("Cannot parse updates with key \"%s\"\n", key);
				break;
		}
	}
	
	public Player getOpponent() {
		for(Map.Entry<String, Player> entry : this.players.entrySet())
			if(entry.getKey() != this.myBot.getName())
				return entry.getValue();
		return null;
	}
	
	public Field getMyField() {
		return this.myBot.getField();
	}

    public static Field getNextField(Field field, MoveType move) {  
        Field newField = new Field(field);
        for (int i = 0; i < field.getWidth(); i++) {
            for (int j = 0; j < field.getHeight(); j++) {
                Cell c = field.getCell(i, j);
                if (c.isShape()) {
                    if (move == MoveType.LEFT) {
                        if (i == 0) {
                            // hit wall, return same as before
                            return new Field(field);
                        }
                        newField.setCell(i - 1,j, c);
                        c.setLocation(i - 1, j);
                        newField.setCell(i,j, new Cell());
                    } else if (move == MoveType.RIGHT) {
                        if (i == field.getWidth() - 1) {
                            // hit wall, return same as before
                            return new Field(field);
                        }
                        newField.setCell(i + 1,j, c);
                        c.setLocation(i + 1, j);
                        newField.setCell(i,j, new Cell());
                    } else if (move == MoveType.DOWN) {
                        if (j == 0) {
                            // hit wall, return same as before
                            return new Field(field);
                        }
                        newField.setCell(i,j-1, c);
                        c.setLocation(i, j - 1);
                        newField.setCell(i,j, new Cell());
                    } 
                }
            }
        }
        for (int i = 0; i < newField.getWidth(); i++) {
            for (int j = 0; j < newField.getHeight(); j++) {
                Cell cell = newField.getCell(i, j);
                for (Cell c : cell.getNeighbors(newField)) {
                    if (c.isBlock()) {
                        cell.setBlock();
                    }
                }
            }
        }
        boolean filled = true;
        while (filled) {
            for (int i = 0; i < newField.getWidth() && filled; i++) {
                Cell cell = newField.getCell(i, 0);
                filled = filled && cell.isBlock();
            }
            if (filled) {
                newField.clearRow();
            }
        }
   
        return newField;
    }
	
	public Field getOpponentField() {
		return getOpponent().getField();
	}
	
	public ShapeType getCurrentShape() {
		return this.currentShape;
	}
	
	public ShapeType getNextShape() {
		return this.nextShape;
	}
	
	public Point getShapeLocation() {
		return this.shapeLocation;
	}
	
	public int getRound() {
		return this.round;
	}
}
