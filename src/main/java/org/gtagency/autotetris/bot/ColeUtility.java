package org.gtagency.autotetris.bot;

import org.gtagency.autotetris.field.Field;
import org.gtagency.autotetris.bot.Utility;

public class ColeUtility implements Utility {

    /**
     * Returns the number of completed rows
     *
     * @return number of completed rows (0 - 20)
     */

    public double value(Field f) {
        int completedRows = 0;
        for (int i = 0; i < f.getHeight(); i++) {
            boolean complete = true;
            boolean isEmpty = true;
            for (int j = 0; j < f.getWidth(); j++) {
                if (f.getCell(j, i).isBlock()) {
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

    /**
     * Determines how left the block is.
     * 
     * @return the value indicating how left the block is between 0 and 10.
     * High values indicate being on the left side of the board.
     */

    public double leftValue(Field f) {
        int value = 0;
        for (int j = 0; j < f.getWidth(); j++) {
            for (int i = 0; i < f.getHeight(); i++) {
                if (f.getCell(j, i).isBlock()) {
                    value += (f.getWidth() - i);
                }
            }
        }

        // Scale down to be a number between 0 and 10 
        double maxValue = f.getHeight() * ((f.getWidth() * (f.getWidth() - 1)) / 2);
        return (value * 10) / maxValue;
    }

    /**
     * @return the height of the field by finding the first block or solid
     */

    public int getHeight(Field f) {
        int height = 0;
        for (int i = f.getHeight() - 1; i >= 0; i--) {
            for (int j = 0; j < f.getWidth(); j++) {
                if (f.getCell(j, i).isBlock() || f.getCell(j, i).isSolid()) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * Get smoothness of the top.
     * Higher number means the top is smoother.
     *
     * @return a value indicating how smooth the surface is
     */

    public double getSmoothnessOfSurface(Field f) {
        int blockValueAboveFullRows = 0;
        int minHeight = (int) value(f);

        for (int j = 0; j < f.getWidth(); j++) {
            for (int i = minHeight; i < f.getHeight(); i++) {
                if (f.getCell(j, i).isBlock() | f.getCell(j, i).isSolid()) {
                    blockValueAboveFullRows += i; // higher blocks are worth more
                }
            }
        }

        int maxHeight = f.getHeight() - minHeight;

        return 0;
    }
}
