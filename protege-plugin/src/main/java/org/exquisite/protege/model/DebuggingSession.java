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
        RUNNING
    }

    public void startSession() {
        this.state = State.RUNNING;
    }

    public void stopSession() {
        this.state = State.STOPPED;
    }

    public State getState() {
        return state;
    }
}
