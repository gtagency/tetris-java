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

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

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
    
    private Field(Field field) {
        this.width = field.getWidth();
        this.height = field.getHeight();
        this.grid = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Cell(x, y, field.getCell(x, y).getState());
            }
        }
    }
    
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

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Cell[width][height];   
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Cell(x, y, CellType.EMPTY);
            }
        }
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
                if(cellCode != 0)
                    this.grid[x][y].setColor(Color.BLACK);
            }
        }
    }

    public Shape liftShape(ShapeType type, Point location){
        Shape tempShape = new Shape(type, this, location);
        boolean correct;
        do{
            correct = true;
            tempShape.turnRight();
            for(int i=location.x; i < tempShape.getSize() + location.x; i++) {
                for(int j=location.y; j < tempShape.getSize() + location.y; j++) {
                    if(getCell(i, j) != null && tempShape.isAt(new Point(i,j)) != grid[i][j].isShape()){
                        correct = false;
                    }
                }
            }
        } while(!correct);
        for(int i=location.x; i < tempShape.getSize() + location.x; i++) {
            for(int j=location.y; j < tempShape.getSize() + location.y; j++) {
                if(getCell(i,j) != null && grid[i][j].isShape())
                    grid[i][j].setState(CellType.EMPTY);
            }
        }
        return tempShape;
    }

    public Cell getCell(int x, int y) {
        if(x < 0 || x >= this.width || y < 0 || y >= this.height)
            return null;
        return this.grid[x][y];
    }

    public void setCell(int x, int y, Cell c) {
        this.grid[x][y] = c;
    }

    public int removeFullRows(){
        int oldRow=height-1;
        int newRow=oldRow;
        int cleared=0;
        while (newRow>=0){
            while (oldRow>=0 && isFull(oldRow)){
                oldRow-=1;
            }
            if (oldRow>=0){
                for(int i=0; i<width; i++){
                    grid[i][newRow]=grid[i][oldRow];
                }
            }
            else{
                cleared++;
                for(int i=0; i<width; i++){
                    grid[i][newRow]=new Cell();
                }
            }
            newRow-=1;
            oldRow-=1;
        }
        return cleared;
    }

    public boolean isFull(int row){
        for (int i=0; i<grid.length; i++){
            if(!grid[i][row].isBlock())
                return false;
        }
        return true;
    }

    public String toString(){
        StringBuilder field = new StringBuilder();
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                field.append(grid[j][i].getState().ordinal());
                if(j != width-1)
                    field.append(",");
            }
            if(i != height-1)
                field.append("\n");
        }
        return field.toString();
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }
    
    public Field clone(){
        return new Field(this);
    }
}
