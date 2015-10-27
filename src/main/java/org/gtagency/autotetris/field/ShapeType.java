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

/**
 * ShapeType class
 * 
 * Enum for all possible Shape types
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

public enum ShapeType {
    I , J, L, O, S, T, Z, NONE;

    public Color color(){
        switch(ordinal()){
        case 0:
            return Color.CYAN;
        case 1:
            return Color.PINK;
        case 2:
            return Color.ORANGE;
        case 3:
            return Color.YELLOW;
        case 4:
            return Color.GREEN;
        case 5:
            return Color.MAGENTA;
        case 6:
            return Color.RED;
        default:
            return Color.BLACK;
        }
    }

    public Point startPos(){
        switch(ordinal()){
        case 0:
            return new Point(3, -1);
            //case 1:
            //   return Color.PINK;
            //case 2:
            //   return Color.ORANGE;
        case 3:
            return new Point(4,0);
            //case 4:
            //  return Color.GREEN;
            //case 5:
            //  return Color.MAGENTA;
            //case 6:
            //    return Color.RED;
        default:
            return new Point(3,0);
        }    
    }
}
