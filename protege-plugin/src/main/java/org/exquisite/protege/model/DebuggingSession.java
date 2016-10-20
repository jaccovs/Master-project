package org.exquisite.protege.model;

/**
 * A debugging session object that represents the different states such a debugging session can reach.
 */
public class DebuggingSession {

    private State state;

    public DebuggingSession() {
        this.state = State.STOPPED;
    }

    public enum State {
        STOPPED,
        STARTED
    }

    public void startSession() {
        this.state = State.STARTED;
    }

    public void stopSession() {
        this.state = State.STOPPED;
    }

    public State getState() {
        return state;
    }
}
