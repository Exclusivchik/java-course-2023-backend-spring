package edu.java.scrapper.domain.jdbc;

import edu.java.api.domain.dto.LinkChatMappingDto;
import edu.java.api.domain.repository.jdbc.JdbcChatRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkChatMappingRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.models.LinkResponse;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
public class LinkChatMappingRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcChatRepository jdbcChatRepository;
    @Autowired
    private JdbcLinkRepository jdbcLinkRepository;
    @Autowired
    private JdbcLinkChatMappingRepository jdbcLinkChatMappingRepository;

    @Test
    @Transactional
    @Rollback
    void shouldAddRecord() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jdbcChatRepository.add(chatId);
        Long linkId = jdbcLinkRepository.add(link);
        jdbcLinkChatMappingRepository.add(chatId, linkId);
        List<LinkChatMappingDto> chatsToLinks = jdbcLinkChatMappingRepository.findAll();

        assertThat(chatsToLinks).containsOnly(new LinkChatMappingDto(chatId, linkId));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowDuplicateKeyExceptionWhileAddingRecord() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jdbcChatRepository.add(chatId);
        Long linkId = jdbcLinkRepository.add(link);
        jdbcLinkChatMappingRepository.add(chatId, linkId);
        Throwable ex = catchThrowable(() -> jdbcLinkChatMappingRepository.add(chatId, linkId));

        assertThat(ex).isInstanceOf(BadRequestException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveLink() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        jdbcChatRepository.add(chatId);
        Long linkId = jdbcLinkRepository.add(link);

        jdbcLinkChatMappingRepository.add(chatId, linkId);
        jdbcLinkChatMappingRepository.remove(chatId, linkId);
        List<LinkChatMappingDto> links = jdbcLinkChatMappingRepository.findAll();

        assertThat(links).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileRemovingLink() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        jdbcChatRepository.add(chatId);
        Long linkId = jdbcLinkRepository.add(link);

        Throwable ex = catchThrowable(() -> jdbcLinkChatMappingRepository.remove(chatId, linkId));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldFindByTgChat() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        jdbcChatRepository.add(chatId);
        Long linkId = jdbcLinkRepository.add(link);
        jdbcLinkChatMappingRepository.add(chatId, linkId);

        List<LinkResponse> linkResponseList = jdbcLinkChatMappingRepository.findAllByChatId(chatId);

        assertThat(linkResponseList).containsOnly(new LinkResponse(linkId, link));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileFindingByTgChat() {
        Long chatId = 1L;

        Throwable ex = catchThrowable(() -> jdbcLinkChatMappingRepository.findAllByChatId(chatId));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldFindByLinkId() {
        Long firstChatId = 1L;
        URI firstLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        Long secondChatId = 2L;
        URI secondLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023");
        jdbcChatRepository.add(firstChatId);
        Long firstLinkId = jdbcLinkRepository.add(firstLink);
        jdbcChatRepository.add(secondChatId);
        Long secondLinkId = jdbcLinkRepository.add(secondLink);
        jdbcLinkChatMappingRepository.add(firstChatId, firstLinkId);
        jdbcLinkChatMappingRepository.add(secondChatId, secondLinkId);

        List<LinkChatMappingDto> linkResponseList = jdbcLinkChatMappingRepository.findAllByLinkId(secondLinkId);

        assertThat(linkResponseList).containsOnly(new LinkChatMappingDto(secondChatId, secondLinkId));
    }
}
