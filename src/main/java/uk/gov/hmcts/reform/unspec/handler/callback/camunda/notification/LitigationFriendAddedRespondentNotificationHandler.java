package uk.gov.hmcts.reform.unspec.handler.callback.camunda.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.Callback;
import uk.gov.hmcts.reform.unspec.callback.CallbackHandler;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationsProperties;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.service.NotificationService;

import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.NOTIFY_RESPONDENT_SOLICITOR1_FOR_LITIGATION_FRIEND_ADDED;

@Service
@RequiredArgsConstructor
public class LitigationFriendAddedRespondentNotificationHandler extends CallbackHandler implements NotificationData {

    private static final List<CaseEvent> EVENTS = List.of(NOTIFY_RESPONDENT_SOLICITOR1_FOR_LITIGATION_FRIEND_ADDED);

    public static final String TASK_ID = "LitigationFriendAddedNotifyRespondentSolicitor1";
    private static final String REFERENCE_TEMPLATE = "litigation-friend-added-respondent-notification-%s";

    private final NotificationService notificationService;
    private final NotificationsProperties notificationsProperties;

    @Override
    protected Map<String, Callback> callbacks() {
        return Map.of(
            callbackKey(ABOUT_TO_SUBMIT), this::notifyRespondentSolicitorForLitigationFriendAdded
        );
    }

    @Override
    public String camundaActivityId(CallbackParams callbackParams) {
        return TASK_ID;
    }

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    private CallbackResponse notifyRespondentSolicitorForLitigationFriendAdded(CallbackParams callbackParams) {
        CaseData caseData = callbackParams.getCaseData();

        notificationService.sendMail(
            caseData.getRespondentSolicitor1EmailAddress(),
            notificationsProperties.getSolicitorLitigationFriendAdded(),
            addProperties(caseData),
            String.format(REFERENCE_TEMPLATE, caseData.getLegacyCaseReference())
        );
        return AboutToStartOrSubmitCallbackResponse.builder().build();
    }

    @Override
    public Map<String, String> addProperties(CaseData caseData) {
        return Map.of(
            CLAIM_REFERENCE_NUMBER, caseData.getLegacyCaseReference(),
            FRONTEND_BASE_URL_KEY, FRONTEND_BASE_URL
        );
    }
}
