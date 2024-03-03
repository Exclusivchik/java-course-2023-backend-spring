package edu.java.bot.api.services;

import edu.java.exceptions.BotApiException;
import edu.java.models.LinkUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Slf4j
@Service
public class BotService {
    public void postUpdate(LinkUpdate request, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new BotApiException("Invalid HTTP request params");
        }

        log.info("Message has been send");
    }
}
