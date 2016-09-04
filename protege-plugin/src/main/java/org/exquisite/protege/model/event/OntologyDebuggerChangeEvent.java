package org.exquisite.protege.model.event;

import javax.swing.event.ChangeEvent;

/**
 * A subclass of ChangeEvent that is used to notify interested parties that
 * state has changed in the event source. An EventType also informs about the
 * kind of change. This enables the listener to decide if this kind of event is
 * important.
 */
public class OntologyDebuggerChangeEvent extends ChangeEvent {

    private EventType type;

    public OntologyDebuggerChangeEvent(Object source, EventType type) {
        super(source);
        this.type = type;
    }

    /**
     * The event type of the occurred change event.
     *
     * @return The event type of the occurred change event.
     */
    public EventType getType() {
        return type;
    }

    /**
     * Check if the this change event is of this event type.
     *
     * @param type The event type to check on this change event's event type.
     * @return <code>true</code> if this change event has this event type,
     * otherwise <code>false</code>.
     */
    public boolean isType(EventType type) {
        return this.type.equals(type);
    }

    @Override
    public String toString() {
        return "OntologyDebuggerChangeEvent{" +
                "source= " + source +
                "type=" + type +
                '}';
    }
}
