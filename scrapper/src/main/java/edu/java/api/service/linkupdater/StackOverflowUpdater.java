package edu.java.api.service.linkupdater;

import edu.java.Responses.StackOverflowResponse;
import edu.java.api.domain.dto.LinkChatMappingDto;
import edu.java.api.domain.dto.LinkDto;
import edu.java.api.domain.repository.jdbc.JdbcLinkChatMappingRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.clients.BotClient;
import edu.java.clients.StackOverflowClient.StackOverflowClient;
import edu.java.models.LinkUpdate;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
@Slf4j
public class StackOverflowUpdater implements LinkUpdater {
    private final String host = "stackoverflow.com";
    private final StackOverflowClient stackOverflowClient;
    private final JdbcLinkRepository linkRepository;
    private final JdbcLinkChatMappingRepository joinTableRepository;
    private final BotClient botClient;

    @Override
    public int process(LinkDto link) {
        String[] splitLink = link.url().getPath().split("/");
        Long questionId = Long.parseLong(splitLink[splitLink.length - 1]);
        StackOverflowResponse response = stackOverflowClient.fetchData(questionId)
            .orElseThrow(IllegalArgumentException::new);
        if (link.lastUpdate().isAfter(response.lastActivityDate())) {
            List<LinkChatMappingDto> joinTableDtos = joinTableRepository.findAllByLinkId(link.linkId());
            if (joinTableDtos.isEmpty()) {
                linkRepository.remove(link.url());
                return 1;
            }
            List<Long> tgChatIds = joinTableDtos.stream().map(LinkChatMappingDto::chatId).toList();
            botClient.postUpdates(new LinkUpdate(
                link.linkId(),
                link.url(),
                getDescription(response),
                tgChatIds
            ));
        }
        return 1;
    }

    private String getDescription(StackOverflowResponse response) {
        return "На вопрос " + response.questionId() + " пришел новый ответ на Stackoverflow от "
            + response.owner().displayName();
    }
}
