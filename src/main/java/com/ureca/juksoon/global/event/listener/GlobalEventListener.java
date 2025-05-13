package com.ureca.juksoon.global.event.listener;

import com.ureca.juksoon.global.event.event.CreationFeedEvent;
import com.ureca.juksoon.global.event.event.MessageConsumeEvent;
import com.ureca.juksoon.global.event.handler.CreationRedisFeedEventHandler;
import com.ureca.juksoon.global.event.handler.MessageConsumeEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalEventListener {
    private final MessageConsumeEventHandler messageConsumeEventHandler;
    private final CreationRedisFeedEventHandler creationRedisFeedEventHandler;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCreationFeedEvent(CreationFeedEvent creationFeedEvent){
        creationRedisFeedEventHandler.makeTicketPublisher(creationFeedEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageConsumeEventHandler(MessageConsumeEvent messageConsumeEvent){
        messageConsumeEventHandler.removeTicketRecord(messageConsumeEvent);
    }

}
