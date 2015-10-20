package org.gtagency.autotetris;
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


import java.io.IOException;

/**
 * Class that represents one Robot object and stores additional information such as the name that the bot receives and
 * which person is the author.
 * modified by Mason Liu
 */
public class EnginePlayer
{
    private String name;
    private IOPlayer bot;
    private long timeBank;
    private long maxTimeBank;
    private long timePerMove;
    
    public EnginePlayer(String name, IOPlayer bot, long maxTimeBank, long timePerMove)
    {
        this.bot = bot;
        this.name = name;
        this.maxTimeBank=maxTimeBank;
        this.timePerMove=timePerMove;
    }
    
    /**
     * @return The String name of this Player
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return The time left in this player's time bank
     */
    public long getTimeBank() {
        return timeBank;
    }
    
    /**
     * @return The Bot object of this Player
     */
    public IOPlayer getBot() {
        return bot;
    }
    
    /**
     * sets the time bank directly
     */
    public void setTimeBank(long time) {
        this.timeBank = time;
    }
    
    /**
     * updates the time bank for this player, cannot get bigger than maximal time bank or smaller than zero
     * @param time : time consumed from the time bank
     */
    public void updateTimeBank(long time) 
    {
        this.timeBank = Math.max(this.timeBank - time, 0);
        this.timeBank = Math.min(this.timeBank + this.timePerMove, this.maxTimeBank);
    }
    
    public void sendInfo(String info) 
    {
        try {
            this.bot.process(info, "input");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getDump(){
        return bot.getDump();
    }
    
    public String requestMove() 
    {
        long startTime = System.currentTimeMillis();
        try {
            this.bot.process("action moves " + timeBank, "input");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        String response = this.bot.getResponse(this.timeBank);
        long timeElapsed = System.currentTimeMillis() - startTime;
        updateTimeBank(timeElapsed);
        return response;
    }
}
