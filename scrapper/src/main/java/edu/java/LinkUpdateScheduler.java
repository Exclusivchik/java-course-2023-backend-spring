package edu.java;

import edu.java.api.service.LinkUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true")
public class LinkUpdateScheduler {
    private final LinkUpdateService linkUpdateService;

    @Scheduled(fixedDelayString = "#{@applicationConfig.scheduler.interval}")
    public void update() {
        log.info("Checking for updates");
        int checkedLinksCount = linkUpdateService.update();
        log.info("{} links checked", checkedLinksCount);
    }
}
