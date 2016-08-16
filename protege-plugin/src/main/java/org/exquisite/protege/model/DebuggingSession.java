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
        STARTED,
        PAUSED
    }

    void startSession() {
        this.state = State.STARTED;
    }

    void stopSession() {
        this.state = State.STOPPED;
    }

    void pauseSession() { this.state = State.PAUSED; }

    void resumeSession() {
        this.state = State.STARTED;
    }

    public State getState() {
        return state;
    }
}
