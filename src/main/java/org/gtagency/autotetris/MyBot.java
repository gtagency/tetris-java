package org.gtagency.autotetris;

import java.util.Scanner;

public class MyBot {

    private Scanner scan = new Scanner(System.in);

    public void run()
    {
        while(scan.hasNextLine()) {
            String line = scan.nextLine();
            if(line.length() == 0) { continue; }
            String[] parts = line.split(" ");

            switch(parts[0]) {
                case "settings":
                    // store game settings
                    break;
                case "update":
                    // store game updates
                    break;
                case "action":
                    System.out.println("drop");
                    System.out.flush();
                    break;
                default:
                    // error
            }
        }
    }

    public static void main(String[] args) {
        (new MyBot()).run();
    }
}
