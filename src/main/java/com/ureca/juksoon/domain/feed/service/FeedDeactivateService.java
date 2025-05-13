package com.ureca.juksoon.domain.feed.service;

import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.entity.Status;
import com.ureca.juksoon.domain.feed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedDeactivateService {
    private final FeedService feedService;

    @Scheduled(cron = "0 59 23 * * * ")
    public void deactivateFeedProcess(){
        feedService.deactivateFeedByScheduler();
    }
}
