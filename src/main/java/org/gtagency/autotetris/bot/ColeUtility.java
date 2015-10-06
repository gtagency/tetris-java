package org.gtagency.autotetris.bot;

import org.gtagency.autotetris.field.Field;
import org.gtagency.autotetris.bot.Utility;

public class ColeUtility implements Utility {

    // Return the number of completed rows
    public double value(Field f) {
        int completedRows = 0;
        for (int i = 0; i < f.getHeight(); i++) {
            boolean complete = true;
            boolean isEmpty = true;
            for (int j = 0; j < f.getWidth(); j++) {
                if (f.getCell(i, j).isBlock()) {
                    isEmpty = false;
                } else {
                    complete = false;
                }
            }
            if (complete) {
                completedRows++;
            } else if (!isEmpty) {
                break;
            }
        }
        return completedRows;
    }
/*
    public int getHeight(Field f) {
        int height = 0;
        for (int i = f.getHeight() - 1; i >= 0; i--) {
            for (int j = 
        }
    }
    */
}
