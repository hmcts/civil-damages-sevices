package uk.gov.hmcts.reform.unspec.handler.callback.camunda.caseevents;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.handler.callback.BaseCallbackHandlerTest;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;

@SpringBootTest(classes = {
    ProceedOfflineForUnRepresentedCallbackHandler.class,
    ProceedOfflineForUnRegisteredCallbackHandler.class,
    JacksonAutoConfiguration.class
})
class ProceedOfflineCallbackHandlerTest extends BaseCallbackHandlerTest {

    @Nested
    class UnregisteredCallback {

        @Autowired
        ProceedOfflineForUnRegisteredCallbackHandler handler;

        @Test
        void shouldCaptureTakenOfflineDate_whenProceedInHeritageSystemRequested() {
            CaseData caseData = CaseDataBuilder.builder().atStatePendingClaimIssuedUnRepresentedDefendant().build();
            CallbackParams params = callbackParamsOf(caseData, ABOUT_TO_SUBMIT);

            var response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getData()).extracting("takenOfflineDate").isNotNull();
        }

        @Test
        void shouldReturnCorrectActivityId_whenRequested() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimSubmitted().build();

            CallbackParams params = callbackParamsOf(caseData, ABOUT_TO_SUBMIT);

            assertThat(handler.camundaActivityId(params)).isEqualTo("ProceedOfflineForUnregisteredFirm");
        }
    }

    @Nested
    class UnrepresentedCallback {

        @Autowired
        ProceedOfflineForUnRepresentedCallbackHandler handler;

        @Test
        void shouldCaptureTakenOfflineDate_whenProceedInHeritageSystemRequested() {
            CaseData caseData = CaseDataBuilder.builder().atStatePendingClaimIssuedUnRepresentedDefendant().build();
            CallbackParams params = callbackParamsOf(caseData, ABOUT_TO_SUBMIT);

            var response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getData()).extracting("takenOfflineDate").isNotNull();
        }

        @Test
        void shouldReturnCorrectActivityId_whenRequested() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimSubmitted().build();

            CallbackParams params = callbackParamsOf(caseData, ABOUT_TO_SUBMIT);

            assertThat(handler.camundaActivityId(params)).isEqualTo("ProceedOfflineForUnRepresentedSolicitor");
        }
    }
}
