package edu.java.api.service.linkupdater;

import edu.java.Responses.GitHubResponse;
import edu.java.api.domain.dto.LinkChatMappingDto;
import edu.java.api.domain.dto.LinkDto;
import edu.java.api.domain.repository.jdbc.JdbcLinkChatMappingRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.clients.BotClient;
import edu.java.clients.GitHubClient.GitHubClient;
import edu.java.models.LinkUpdate;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class GitHubLinkUpdater implements LinkUpdater {
    private final String host = "github.com";
    private final GitHubClient gitHubClient;
    private final JdbcLinkRepository linkRepository;
    private final JdbcLinkChatMappingRepository linkChatMappingRepository;
    private final BotClient botClient;

    @Override
    public int process(LinkDto link) {
        String[] splitLink = link.url().getPath().split("/");
        String owner = splitLink[splitLink.length - 2];
        String repo = splitLink[splitLink.length - 1];
        GitHubResponse response = gitHubClient.fetchData(owner, repo)
            .orElseThrow(IllegalArgumentException::new);
        if (link.lastUpdate().isAfter(response.createdAt())) {
            List<LinkChatMappingDto> joinTableDtos = linkChatMappingRepository.findAllByLinkId(link.linkId());
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

    private String getDescription(GitHubResponse response) {
        return "Произошло обновление типа " + response.type()
            + " в репозитории " + response.repo() + " от автора "
            + response.actor();
    }
}
