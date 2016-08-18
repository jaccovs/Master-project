package org.exquisite.protege.model;

/**
 * A debugging session object that represents the different states such a debugging session can reach.
 */
class DebuggingSession {

    private State state;

    DebuggingSession() {
        this.state = State.STOPPED;
    }

    public enum State {
        STOPPED,
        STARTED
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
