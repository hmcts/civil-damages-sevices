package uk.gov.hmcts.reform.unspec.handler.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.service.EventEmitterService;
import uk.gov.hmcts.reform.unspec.service.search.CaseReadyBusinessProcessSearchService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnExpression("${polling.event.emitter.enabled:true}")
@ExternalTaskSubscription(topicName = "POLLING_EVENT_EMITTER")
public class PollingEventEmitterHandler implements BaseExternalTaskHandler {

    private final CaseReadyBusinessProcessSearchService caseSearchService;
    private final CaseDetailsConverter caseDetailsConverter;
    private final EventEmitterService eventEmitterService;

    @Override
    public void handleTask(ExternalTask externalTask) {
        List<CaseDetails> cases = caseSearchService.getCases();
        log.info("Job '{}' found {} case(s)", externalTask.getTopicName(), cases.size());
        cases.stream()
            .map(caseDetailsConverter::toCaseData)
            .forEach(mappedCase -> eventEmitterService.emitBusinessProcessCamundaEvent(mappedCase, true));
    }

    @Override
    public int getMaxAttempts() {
        return 1;
    }
}
