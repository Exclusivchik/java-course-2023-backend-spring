package edu.java.scrapper.domain.jpa;

import edu.java.api.domain.repository.jpa.JpaChatRepository;
import edu.java.api.domain.repository.jpa.JpaLinkRepository;
import edu.java.api.service.LinkService;
import edu.java.api.service.jpa.JpaLinkService;
import edu.java.exceptions.NotFoundException;
import edu.java.models.LinkResponse;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JpaLinkServiceTest extends IntegrationTest {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private final LinkService linkService;

    @Autowired
    public JpaLinkServiceTest(
        JdbcTemplate jdbcTemplate,
        JpaChatRepository jpaChatRepository,
        JpaLinkRepository jpaLinkRepository
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.linkService = new JpaLinkService(jpaChatRepository, jpaLinkRepository);
    }

    @Test
    @Transactional
    @Rollback
    void shouldAddLink() {
        jdbcTemplate.update("INSERT INTO chat VALUES (1)");
        URI url = URI.create("https://github.com/Exclusivchik");

        linkService.add(1L, url);
        Long linkCount = jdbcTemplate.queryForObject(
            "SELECT count(*) from link WHERE url = 'https://github.com/Exclusivchik'",
            Long.class
        );
        String link = jdbcTemplate.queryForObject(
            "SELECT url FROM ((SELECT * FROM link_chat_mapping WHERE chat_id = ?)"
                + " AS subtable JOIN link USING (link_id))",
            String.class,
            1L
        );

        assertAll(
            () -> assertEquals(1L, linkCount),
            () -> assertEquals(url.toString(), link)
        );
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveLink() {
        jdbcTemplate.update("INSERT INTO chat VALUES (1)");
        URI url = URI.create("https://github.com/Exclusivchik");
        linkService.add(1L, url);

        linkService.remove(1L, url);
        Long linkTableLinksCount = jdbcTemplate.queryForObject(
            "SELECT count(*) from link WHERE url = 'https://github.com/Exclusivchik'",
            Long.class
        );
        Long joinTableLinksCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM ((SELECT * FROM link_chat_mapping WHERE chat_id = ?)"
                + " AS subtable JOIN link USING (link_id))",
            Long.class,
            1L
        );

        assertAll(
            () -> assertEquals(1L, linkTableLinksCount),
            () -> assertEquals(0L, joinTableLinksCount)
        );
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileRemovingLink() {
        jdbcTemplate.update("INSERT INTO chat VALUES (1)");
        URI link = URI.create("https://github.com/Exclusivchik");

        Throwable ex = catchThrowable(() -> linkService.remove(1L, link));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnAllLinks() {
        jdbcTemplate.update("INSERT INTO chat VALUES (1)");
        URI link = URI.create("https://github.com/Exclusivchik");
        linkService.add(1L, link);
        List<LinkResponse> result = linkService.listAll(1L);

        assertAll(
            () -> assertEquals(1, result.size()),
            () -> assertEquals(link, result.getFirst().url())
        );
    }
}
