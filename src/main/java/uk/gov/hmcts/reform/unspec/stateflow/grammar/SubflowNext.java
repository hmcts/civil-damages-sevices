package uk.gov.hmcts.reform.unspec.stateflow.grammar;

/**
 * This specifies what can come after a SUBFLOW clause.
 */
public interface SubflowNext<S> extends State<S>, Subflow<S>, Build {

}
