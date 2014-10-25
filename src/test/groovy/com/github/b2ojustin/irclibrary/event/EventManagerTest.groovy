package com.github.b2ojustin.irclibrary.event

import org.junit.Before
import org.junit.Test


class EventManagerTest extends GroovyTestCase {
    public final class TestEventListener implements EventListener {
        protected boolean handlerCalled = false
        protected boolean nonHandlerCalled = false
        protected ConnectionEvent event
        @EventHandler
        void onConnect(ConnectionEvent event) {
            handlerCalled = true
            this.event = event
        }

        void notAHandler() {
            nonHandlerCalled = true
        }
    }

    EventManager eM;
    TestEventListener eL;

    @Before
    void setUp() {
        eM = new EventManager()
        eL = new TestEventListener()
    }

    @Test
    void testAddListener() {
        eM.addListener(eL)
        assertTrue "Event type not found in event map", eM.methodMap.containsKey(ConnectionEvent.class)
        assertTrue "Event listener not found in map", eM.methodMap.get(ConnectionEvent.class).containsKey(eL)
        assertTrue "Event handler method not found", eM.methodMap.get(ConnectionEvent.class)
                                                    .get(eL).contains(eL.class.getMethod("onConnect", ConnectionEvent.class))
        assertFalse "Non handler method found in map", eM.methodMap.get(ConnectionEvent.class)
                                                    .get(eL).contains(eL.class.getMethod("notAHandler"))
    }

    @Test
    void testRemoveListener() {
        eM.addListener(eL)
        eM.removeListener(eL)
        assertFalse "Map still contains event key", eM.methodMap.containsKey(ConnectionEvent.class)
    }

    @Test
    void testFireEvent() {
        eM.addListener(eL)
        ConnectionEvent event = [localAddress: "local", remoteAddress: "remote"]
        eM.fireEvent(event)
        println eL.event.dump()
        assertTrue "Handler was not called", eL.handlerCalled
        assertFalse "Non-handler method was called", eL.nonHandlerCalled
        assertTrue "Event parameter passed incorrectly", (
            eL.event.is(event)
        )
    }
}
