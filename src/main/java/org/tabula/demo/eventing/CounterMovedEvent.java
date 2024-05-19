package org.tabula.demo.eventing;

import javafx.event.Event;
import javafx.event.EventType;

public class CounterMovedEvent extends Event {

    public static final EventType<CounterMovedEvent> COUNTER_MOVED_EVENT_TYPE =
            new EventType<>(Event.ANY, "COUNTER_MOVED_EVENT");

    public CounterMovedEvent() {
        super(COUNTER_MOVED_EVENT_TYPE);
    }

}

