package org.example.functional;

import org.example.Main;

public class ThreadEventMaker extends Thread {
    public void run () {
        Main.boolWorkEventMaker = false;

        for (int i = 0; i < 10; i++){
            EventNew newEvent = new EventNew();

            try {
                newEvent.createImg(Main.getFrm(), Main.getEventTimeCreate());
            } catch (Exception ex) {

            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
