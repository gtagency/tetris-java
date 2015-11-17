package org.gtagency.autotetris.bot;

import org.gtagency.autotetris.field.Field;
import org.gtagency.autotetris.field.Shape;
import org.gtagency.autotetris.bot.BotState;

public interface Utility {
    double value(Field field, Shape tempShape, BotState state);
}
