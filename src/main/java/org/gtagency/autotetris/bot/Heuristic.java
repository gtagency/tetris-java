package org.gtagency.autotetris.bot;

import org.gtagency.autotetris.field.Field;

public interface Heuristic {
    double value(Field f);
}
