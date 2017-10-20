package org.exquisite.protege.model.event;

/**
 * Event type used to decide on the listener side, if a reaction to the type
 * of event is necessary.
 */
public enum EventType {

    /** The active ontology has changed. */
    ACTIVE_ONTOLOGY_CHANGED,

    /** The debugging session state changed. */
    SESSION_STATE_CHANGED,

    /** The diagnosis model's faulty and possibly faulty axioms have changed. */
    INPUT_ONTOLOGY_CHANGED,

    /** The user marked a query formula as entailed or non-entailed. */
    QUERY_ANSWER_EVENT,

    /** A new query has been calculated. */
    QUERY_CALCULATED,

    /** A diagnosis has been found. */
    DIAGNOSIS_FOUND,

    /** The diagnosis model has changed externally. */
    DIAGNOSIS_MODEL_CHANGED

}
