package uk.gov.hmcts.reform.unspec.service.flowstate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.stateflow.StateFlow;
import uk.gov.hmcts.reform.unspec.stateflow.StateFlowBuilder;
import uk.gov.hmcts.reform.unspec.stateflow.model.State;

import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.applicantOutOfTime;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.caseDismissed;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.caseDismissedAfterClaimAcknowledged;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.caseProceedsInCaseman;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimDetailsExtension;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimDetailsNotified;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimDiscontinued;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimIssued;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.counterClaim;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.fullAdmission;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.fullDefence;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.offlineNotRepresented;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.offlineNotRegistered;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.partAdmission;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.pendingClaimIssued;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimNotified;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimSubmitted;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimTakenOffline;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimWithdrawn;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.failToNotifyClaim;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.fullDefenceNotProceed;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.fullDefenceProceed;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.pastClaimDetailsNotificationDeadline;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.paymentFailed;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.paymentSuccessful;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondent1NotRepresented;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondent1OrgNotRegistered;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondentAcknowledgeClaim;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondentAgreedExtension;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondentCounterClaim;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondentFullAdmission;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondentFullDefence;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondentPartAdmission;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.AWAITING_CASE_DETAILS_NOTIFICATION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.AWAITING_CASE_NOTIFICATION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CASE_PROCEEDS_IN_CASEMAN;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_ACKNOWLEDGED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_DETAILS_NOTIFIED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_DETAILS_NOTIFIED_TIME_EXTENSION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_DISCONTINUED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_DISMISSED_DEFENDANT_OUT_OF_TIME;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_DISMISSED_PAST_CLAIM_DETAILS_NOTIFICATION_DEADLINE;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_DISMISSED_PAST_CLAIM_NOTIFICATION_DEADLINE;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_ISSUED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_ISSUED_PAYMENT_FAILED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_ISSUED_PAYMENT_SUCCESSFUL;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_NOTIFIED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_SUBMITTED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.CLAIM_WITHDRAWN;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.COUNTER_CLAIM;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.DRAFT;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.EXTENSION_REQUESTED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.FLOW_NAME;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.FULL_ADMISSION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.FULL_DEFENCE;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.FULL_DEFENCE_NOT_PROCEED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.FULL_DEFENCE_PROCEED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PART_ADMISSION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PENDING_CLAIM_ISSUED;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PENDING_CLAIM_ISSUED_UNREGISTERED_DEFENDANT;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PENDING_CLAIM_ISSUED_UNREPRESENTED_DEFENDANT;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.PROCEEDS_OFFLINE_ADMIT_OR_COUNTER_CLAIM;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.TAKEN_OFFLINE_UNREGISTERED_DEFENDANT;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.TAKEN_OFFLINE_UNREPRESENTED_DEFENDANT;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_COUNTER_CLAIM;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_FULL_ADMISSION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_FULL_DEFENCE;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.RESPONDENT_PART_ADMISSION;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowState.Main.TAKEN_OFFLINE_PAST_APPLICANT_RESPONSE_DEADLINE;

@Component
@RequiredArgsConstructor
public class StateFlowEngine {

    private final CaseDetailsConverter caseDetailsConverter;

    public StateFlow build() {
        return StateFlowBuilder.<FlowState.Main>flow(FLOW_NAME)
            .initial(DRAFT)
                .transitionTo(CLAIM_SUBMITTED).onlyIf(claimSubmitted)
            .state(CLAIM_SUBMITTED)
                .transitionTo(CLAIM_ISSUED_PAYMENT_SUCCESSFUL).onlyIf(paymentSuccessful)
                .transitionTo(CLAIM_ISSUED_PAYMENT_FAILED).onlyIf(paymentFailed)
            .state(CLAIM_ISSUED_PAYMENT_FAILED)
                .transitionTo(CLAIM_ISSUED_PAYMENT_SUCCESSFUL).onlyIf(paymentSuccessful)
            .state(CLAIM_ISSUED_PAYMENT_SUCCESSFUL)
                .transitionTo(PENDING_CLAIM_ISSUED).onlyIf(pendingClaimIssued)
                .transitionTo(PENDING_CLAIM_ISSUED_UNREPRESENTED_DEFENDANT).onlyIf(respondent1NotRepresented)
                .transitionTo(PENDING_CLAIM_ISSUED_UNREGISTERED_DEFENDANT).onlyIf(respondent1OrgNotRegistered)
            .state(PENDING_CLAIM_ISSUED)
                .transitionTo(CLAIM_ISSUED).onlyIf(claimIssued)
            .state(PENDING_CLAIM_ISSUED_UNREGISTERED_DEFENDANT)
                .transitionTo(TAKEN_OFFLINE_UNREGISTERED_DEFENDANT).onlyIf(offlineNotRegistered)
            .state(PENDING_CLAIM_ISSUED_UNREPRESENTED_DEFENDANT)
                .transitionTo(TAKEN_OFFLINE_UNREPRESENTED_DEFENDANT).onlyIf(offlineNotRepresented)
            .state(CLAIM_ISSUED)
                .transitionTo(CLAIM_NOTIFIED).onlyIf(claimNotified)
            .state(CLAIM_NOTIFIED)
                .transitionTo(CLAIM_DETAILS_NOTIFIED).onlyIf(claimDetailsNotified)
            .state(CLAIM_DETAILS_NOTIFIED)
                .transitionTo(FULL_DEFENCE).onlyIf(fullDefence)
                .transitionTo(FULL_ADMISSION).onlyIf(fullAdmission)
                .transitionTo(PART_ADMISSION).onlyIf(partAdmission)
                .transitionTo(COUNTER_CLAIM).onlyIf(counterClaim)
                .transitionTo(CLAIM_DETAILS_NOTIFIED_TIME_EXTENSION).onlyIf(claimDetailsExtension)
            .state(CLAIM_DETAILS_NOTIFIED_TIME_EXTENSION)
                .transitionTo(FULL_DEFENCE).onlyIf(fullDefence)
                .transitionTo(FULL_ADMISSION).onlyIf(fullAdmission)
                .transitionTo(PART_ADMISSION).onlyIf(partAdmission)
                .transitionTo(COUNTER_CLAIM).onlyIf(counterClaim)
            .state(FULL_DEFENCE)
                .transitionTo(CLAIM_ACKNOWLEDGED).onlyIf(respondentAcknowledgeClaim)
                .transitionTo(RESPONDENT_FULL_DEFENCE).onlyIf(respondentFullDefence)
                .transitionTo(RESPONDENT_FULL_ADMISSION).onlyIf(respondentFullAdmission)
                .transitionTo(RESPONDENT_PART_ADMISSION).onlyIf(respondentPartAdmission)
                .transitionTo(RESPONDENT_COUNTER_CLAIM).onlyIf(respondentCounterClaim)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
                .transitionTo(CLAIM_DISMISSED_DEFENDANT_OUT_OF_TIME).onlyIf(caseDismissed)
            .state(AWAITING_CASE_NOTIFICATION)
                .transitionTo(AWAITING_CASE_DETAILS_NOTIFICATION).onlyIf(claimNotified)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
                .transitionTo(CLAIM_DISMISSED_PAST_CLAIM_NOTIFICATION_DEADLINE).onlyIf(failToNotifyClaim)
            .state(AWAITING_CASE_DETAILS_NOTIFICATION)
                .transitionTo(CLAIM_ISSUED).onlyIf(claimDetailsNotified)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
                .transitionTo(CLAIM_DISMISSED_PAST_CLAIM_DETAILS_NOTIFICATION_DEADLINE)
                    .onlyIf(pastClaimDetailsNotificationDeadline)
            .state(CLAIM_ACKNOWLEDGED)
                .transitionTo(EXTENSION_REQUESTED).onlyIf(respondentAgreedExtension)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
                .transitionTo(CLAIM_DISMISSED_DEFENDANT_OUT_OF_TIME).onlyIf(caseDismissedAfterClaimAcknowledged)
            .state(EXTENSION_REQUESTED)
                .transitionTo(RESPONDENT_FULL_DEFENCE).onlyIf(respondentFullDefence)
                .transitionTo(RESPONDENT_FULL_ADMISSION).onlyIf(respondentFullAdmission)
                .transitionTo(RESPONDENT_PART_ADMISSION).onlyIf(respondentPartAdmission)
                .transitionTo(RESPONDENT_COUNTER_CLAIM).onlyIf(respondentCounterClaim)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
            .state(FULL_DEFENCE)
                .transitionTo(TAKEN_OFFLINE_PAST_APPLICANT_RESPONSE_DEADLINE).onlyIf(applicantOutOfTime)
                .transitionTo(FULL_DEFENCE_PROCEED).onlyIf(fullDefenceProceed)
                .transitionTo(FULL_DEFENCE_NOT_PROCEED).onlyIf(fullDefenceNotProceed)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
            .state(RESPONDENT_FULL_ADMISSION)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
                .transitionTo(PROCEEDS_OFFLINE_ADMIT_OR_COUNTER_CLAIM).onlyIf(claimTakenOffline)
            .state(RESPONDENT_PART_ADMISSION)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
                .transitionTo(PROCEEDS_OFFLINE_ADMIT_OR_COUNTER_CLAIM).onlyIf(claimTakenOffline)
            .state(RESPONDENT_COUNTER_CLAIM)
                .transitionTo(CLAIM_DISCONTINUED).onlyIf(claimDiscontinued)
                .transitionTo(CLAIM_WITHDRAWN).onlyIf(claimWithdrawn)
                .transitionTo(CASE_PROCEEDS_IN_CASEMAN).onlyIf(caseProceedsInCaseman)
                .transitionTo(PROCEEDS_OFFLINE_ADMIT_OR_COUNTER_CLAIM).onlyIf(claimTakenOffline)
            .state(FULL_DEFENCE_PROCEED)
            .state(FULL_DEFENCE_NOT_PROCEED)
            .state(CASE_PROCEEDS_IN_CASEMAN)
            .state(CLAIM_DISMISSED_DEFENDANT_OUT_OF_TIME)
            .state(TAKEN_OFFLINE_UNREPRESENTED_DEFENDANT)
            .state(TAKEN_OFFLINE_UNREGISTERED_DEFENDANT)
            .state(PROCEEDS_OFFLINE_ADMIT_OR_COUNTER_CLAIM)
            .state(CLAIM_WITHDRAWN)
            .state(CLAIM_DISCONTINUED)
            .state(CLAIM_DISMISSED_PAST_CLAIM_DETAILS_NOTIFICATION_DEADLINE)
            .state(TAKEN_OFFLINE_PAST_APPLICANT_RESPONSE_DEADLINE)
            .state(CLAIM_DISMISSED_PAST_CLAIM_NOTIFICATION_DEADLINE)
            .build();
    }

    public StateFlow evaluate(CaseDetails caseDetails) {
        return evaluate(caseDetailsConverter.toCaseData(caseDetails));
    }

    public StateFlow evaluate(CaseData caseData) {
        return build().evaluate(caseData);
    }

    public boolean hasTransitionedTo(CaseDetails caseDetails, FlowState.Main state) {
        return evaluate(caseDetails).getStateHistory().stream()
            .map(State::getName)
            .anyMatch(name -> name.equals(state.fullName()));
    }
}
