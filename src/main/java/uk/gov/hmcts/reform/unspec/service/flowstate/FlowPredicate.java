package uk.gov.hmcts.reform.unspec.service.flowstate;

import uk.gov.hmcts.reform.unspec.enums.YesOrNo;
import uk.gov.hmcts.reform.unspec.model.CaseData;

import java.util.function.Predicate;

import static uk.gov.hmcts.reform.unspec.enums.CaseState.AWAITING_CASE_DETAILS_NOTIFICATION;
import static uk.gov.hmcts.reform.unspec.enums.CaseState.AWAITING_CASE_NOTIFICATION;
import static uk.gov.hmcts.reform.unspec.enums.CaseState.CLOSED;
import static uk.gov.hmcts.reform.unspec.enums.CaseState.PROCEEDS_WITH_OFFLINE_JOURNEY;
import static uk.gov.hmcts.reform.unspec.enums.CaseState.STAYED;
import static uk.gov.hmcts.reform.unspec.enums.PaymentStatus.FAILED;
import static uk.gov.hmcts.reform.unspec.enums.PaymentStatus.SUCCESS;

public class FlowPredicate {

    public static final Predicate<CaseData> pendingCaseIssued = caseData ->
        caseData.getLegacyCaseReference() != null && caseData.getRespondent1Represented() == YesOrNo.YES;

    public static final Predicate<CaseData> respondent1NotRepresented = caseData ->
        caseData.getRespondent1Represented() == YesOrNo.NO;

    public static final Predicate<CaseData> paymentFailed = caseData ->
        caseData.getPaymentDetails() != null && caseData.getPaymentDetails().getStatus() == FAILED;

    public static final Predicate<CaseData> paymentSuccessful = caseData ->
        caseData.getPaymentDetails() != null && caseData.getPaymentDetails().getStatus() == SUCCESS;

    public static final Predicate<CaseData> claimIssued = caseData ->
        caseData.getClaimIssuedDate() != null;

    //Temporary backwards compatibility
    public static final Predicate<CaseData> needsToBeBackwardsCompatible = caseData ->
        caseData.getCcdState() != AWAITING_CASE_NOTIFICATION
            && caseData.getCcdState() != AWAITING_CASE_DETAILS_NOTIFICATION;

    public static final Predicate<CaseData> claimNotified = caseData ->
        caseData.getClaimNotificationDate() != null;

    public static final Predicate<CaseData> claimDetailsNotified = caseData ->
        caseData.getClaimDetailsNotificationDate() != null;

    public static final Predicate<CaseData> respondentAcknowledgeService = caseData ->
        caseData.getRespondent1ClaimResponseIntentionType() != null
            && caseData.getRespondent1ClaimResponseDocument() == null
            && caseData.getCcdState() != CLOSED;

    public static final Predicate<CaseData> respondentRespondToClaim = caseData ->
        caseData.getRespondent1ClaimResponseDocument() != null
            && caseData.getCcdState() != CLOSED
            && caseData.getCcdState() != STAYED;

    public static final Predicate<CaseData> applicantRespondToDefence = caseData ->
        caseData.getApplicant1ProceedWithClaim() != null
            && caseData.getApplicant1DefenceResponseDocument() != null;

    public static final Predicate<CaseData> schedulerStayClaim = caseData ->
        caseData.getCcdState() == STAYED;

    public static final Predicate<CaseData> claimWithdrawn = caseData ->
        caseData.getWithdrawClaim() != null
            && caseData.getCcdState() == CLOSED;

    public static final Predicate<CaseData> claimDiscontinued = caseData ->
        caseData.getDiscontinueClaim() != null
            && caseData.getCcdState() == CLOSED;

    public static final Predicate<CaseData> claimTakenOffline = caseData ->
        caseData.getCcdState() == PROCEEDS_WITH_OFFLINE_JOURNEY;

    public static final Predicate<CaseData> caseProceedsInCaseman = caseData ->
        caseData.getClaimProceedsInCaseman() != null;

    private FlowPredicate() {
        //Utility class
    }

}
