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

package org.gtagency.autotetris.field;

import java.awt.Point;
import org.gtagency.autotetris.field.Cell;

/**
 * Field class
 * 
 * Represents the playing field for one player.
 * Has some basic methods already implemented.
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class Field {
	
	private int width;
	private int height;
	private Cell grid[][];
    
    public Field(Field field, Shape shape) {
		this.width = field.getWidth();
		this.height = field.getHeight();
        this.grid = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!field.getCell(x, y).isShape()) {
                    grid[x][y] = field.getCell(x, y);
                } else {
                    grid[x][y] = new Cell(x, y, CellType.EMPTY);
                }
            }
        }

        for (Cell cell : shape.getBlocks()) {
            Point loc = cell.getLocation();
            grid[loc.x][loc.y] = cell;
        }
        
    }

	public Field(int width, int height, String fieldString) {
		this.width = width;
		this.height = height;
		
		parse(fieldString);
	}
	
	/**
	 * Parses the input string to get a grid with Cell objects
	 * @param fieldString : input string
	 */
	private void parse(String fieldString) {
		
		this.grid = new Cell[this.width][this.height];
		
		// get the separate rows
		String[] rows = fieldString.split(";");
		for(int y=0; y < this.height; y++) {
			String[] rowCells = rows[y].split(",");
			
			// parse each cell of the row
			for(int x=0; x < this.width; x++) {
				int cellCode = Integer.parseInt(rowCells[x]);
				this.grid[x][y] = new Cell(x, y, CellType.values()[cellCode]);
			}
		}
	}
	
	public Cell getCell(int x, int y) {
		if(x < 0 || x >= this.width || y < 0 || y >= this.height)
			return null;
		return this.grid[x][y];
	}

    public void setCell(int x, int y, Cell c) {
        this.grid[x][y] = c;
    }

    public void clearRow() {
        for (int y = 0; y < getHeight() - 1; y++) {
            for (int x = 0; x < getWidth(); x++) {
                this.grid[x][y] = this.grid[x][y + 1];
            }
        }
        for (int x = 0; x < getWidth(); x++) {
            this.grid[x][getHeight() - 1] = new Cell(x, getHeight() - 1, 
                CellType.EMPTY);
        }
    }

	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
}
