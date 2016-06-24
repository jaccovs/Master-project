package org.exquisite.protege.model;

/**
 * A debugging session object that represents the different states such a debugging session can reach.
 */
public class DebuggingSession {

    private State state;

    DebuggingSession() {
        this.state = State.STOPPED;
    }

    public enum State {
        STOPPED,
        STARTED
    }

    public enum Action {
        PRESS_START,
        PRESS_STOP,
        PRESS_COMMIT,
        PRESS_ALTERNATIVE
    }

    void startSession() {
        this.state = State.STARTED;
    }

    void stopSession() {
        this.state = State.STOPPED;
    }

    public State getState() {
        return state;
    }
}
