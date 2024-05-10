package org.tabula.demo.cdi;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@ApplicationScoped
public class CDIHandler extends Handler {

    @Inject
    private Event<String> logEvent;

    @PostConstruct
    public void init() {
        Logger.getLogger("").addHandler(this);
    }

    @Override
    public void publish(LogRecord record) {
        String message = getFormatter().format(record);
        logEvent.fire(message);
    }

    @Override
    public void flush() {
        // No implementation needed in this context
    }

    @Override
    public void close() throws SecurityException {
        // No implementation needed in this context
    }

}
