package com.ureca.juksoon.domain.feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedScheduler {
    private final FeedService feedService;

    @Scheduled(cron = "0 0 0 * * *")
    public void deactivateFeedProcess(){
        feedService.deactivateFeedByScheduler();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void activateFeedProcess(){
        feedService.activateFeedByScheduler();
    }
}
