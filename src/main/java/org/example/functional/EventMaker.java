package org.example.functional;

import org.example.Main;

public class EventMaker {
    public void EventMaker () {
        EventNew newEvent = new EventNew();

        Thread th = new Thread(newEvent.createImg(Main.getFrm(), Main.getEventTimeCreate()));
        th.start();
    }
}
