package org.gtagency.autotetris.bot;

import java.util.ArrayList;
import java.util.Collections;

import org.gtagency.autotetris.field.Shape;
import org.gtagency.autotetris.field.Field;
import org.gtagency.autotetris.field.Cell;

public class PrimaryUtility implements Utility {

    /**
     *@Override
     *
     */
    public double value(Field field, Shape tempShape, BotState state) {
        Cell[] neighbors = new Cell[4];
        int h = 0;
        int maxHeight = field.getHeight();
        for(int i=0; i<field.getWidth(); i++){
            for (int j=0; j<field.getHeight(); j++){
                Cell c = field.getCell(i,j);
                if(!c.isEmpty() || tempShape.isAt(c.getLocation())){

                    // Higher is closer to 0
                    if (j < maxHeight) {
                        maxHeight = j;
                    }

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

        int h2 = 0;
        //int emptyCells = 0;
        for (int row = 0; row < field.getHeight(); row++) {
            ArrayList<Integer> contig = new ArrayList<>();
            int currentCount = 0;
            for (int col = 0; col < field.getWidth(); col++) {
                Cell c = field.getCell(col, row);
                if (!c.isEmpty() || tempShape.isAt(c.getLocation())) {
                    currentCount++;
                    if (currentCount == field.getWidth()) {
                        contig.add(currentCount);
                    }
                } else {
                    /*if (row > maxHeight) {
                        emptyCells++;
                    }*/
                    contig.add(currentCount);
                    currentCount = 0;
                }
            }

            /*if (contig.size() == 0) {
                contig.add(0);
            }*/

            int maxVal = Collections.max(contig);
            //System.err.println(row + "," + maxVal);
            if (maxVal == field.getWidth()) {
                return 0;
            }

            h2 += (maxVal + (field.getHeight() - maxHeight));
        }

        h += .5*h2;

        if (h < 0) {
            return 0;
        }

        return h;
    }
}