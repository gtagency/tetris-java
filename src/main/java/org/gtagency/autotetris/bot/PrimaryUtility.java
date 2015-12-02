package org.gtagency.autotetris.bot;

import java.util.ArrayList;
import org.gtagency.autotetris.field.Field;
import org.gtagency.autotetris.field.Cell;

public class PrimaryUtility implements Utility {

    /**
     *@Override
     *TODO fix turning?, horizontal empty block utility for sides / allow 1 col,
     */
    public double value(Field field, int firstCleared, int secondCleared, BotState state) {
        /////////////////////////////////////
        //cell neighbor utilities
        
        Cell[] neighbors = new Cell[4];
        int h = 0;
        int maxHeight = field.getHeight();
        for(int i=0; i<field.getWidth(); i++){
            int localMaxHeight = field.getHeight();
            for (int j=0; j<field.getHeight(); j++){
                Cell c = field.getCell(i,j);
                if(!c.isEmpty()){

                    // Higher is closer to 0
                    if (j < localMaxHeight) {
                        localMaxHeight = j;
                    }

                    neighbors[0]=field.getCell(i-1,j);
                    neighbors[1]=field.getCell(i+1,j);
                    neighbors[2]=field.getCell(i,j+1);
                    neighbors[3]=field.getCell(i,j-1);

                    for (Cell n: neighbors){
                        if (n!= null && n.isEmpty()) {
                            h++; //arclen
                        }
                    }
                } else {
                    Cell n;
                    Cell l;
                    Cell r;
                    int k = 1;
                    if(i != 0 && i != field.getWidth() - 1){
                        do{
                            k++;
                            l = field.getCell(i-1, j-k);
                            r = field.getCell(i+1, j-k);
                            if(l!= null && (!l.isEmpty()) 
                                    && (!r.isEmpty())){
                                h+= 4 * k; //for upper diagonal blocks
                            }
                        }while(l!= null && !l.isEmpty() && !r.isEmpty());
                    }

                    /////////////////////////////////////

                    if (j > localMaxHeight){
                        h+= 10; //for multiple empty blocks in same col
                    }

                    k = 0;
                    do{
                        k++;
                        n = field.getCell(i, j-k);
                        if(n!= null && (!n.isEmpty())){
                            h+= 20 * (1/((double)k)); //for multiple filled blocks over empty block
                        }
                    }while(n!= null && (!n.isEmpty()));
                }
            }
            if(localMaxHeight < maxHeight){
                maxHeight = localMaxHeight;
            }
        }
        maxHeight = field.getHeight()- maxHeight;
        //penalizes extreme heights
        h += 180*(Math.pow(((double)Math.max((maxHeight - field.getHeight()/2),0)),2)/Math.pow((double)field.getHeight()/2,2));

        /////////////////////////////////////////////////////
        //contiguity utilities
        
        int h2 = 200;
        for (int row = 0; row < field.getHeight(); row++) {
            ArrayList<Integer> contig = new ArrayList<>();
            int currentCount = 0;
            for (int col = 0; col < field.getWidth(); col++) {
                Cell c = field.getCell(col, row);
                if (!c.isEmpty()) {
                    currentCount++;
                } else {
                    currentCount = 0;
                }
                contig.add(currentCount);
            }

            int sum = 0;
            for(int i : contig){
                sum += i;
            }
            sum = (45 - sum)/2;

            //penalizes non-contiguous blocks with height weighed
            h2 += ((double)sum * (.15 + .85*Math.pow(((double)(field.getHeight() + 1 - row)),2)/Math.pow((double)field.getHeight(),2)));
        }
        
        /////////////////////////////
        //rows cleared utilities
        int h3 = 0;
        //-1 is highly desirable, 1 is highly undesirable
        double clearDesirability =  ((maxHeight > field.getHeight()/2)?-1:1) * Math.pow(((double)(maxHeight - field.getHeight()/2)),2)/Math.pow((double)field.getHeight()/2,2);
        
        int oneCleared = ((firstCleared == 1)?1:0) + ((secondCleared == 1)?1:0);
        h3 += 8 * oneCleared * clearDesirability;
        
        int twoCleared = ((firstCleared == 2)?1:0) + ((secondCleared == 2)?1:0);
        h3 += 8* twoCleared * (clearDesirability-.5*twoCleared);
        
        int manyCleared = ((firstCleared >2)?1:0) + ((secondCleared > 2)?1:0);
        h3 -= 40* Math.pow(manyCleared,2);
        
        /////////////////////////////
        h = 2*h + h2 + h3;

        if (h < 0) {
            return 0;
        }
        return h;
    }
}