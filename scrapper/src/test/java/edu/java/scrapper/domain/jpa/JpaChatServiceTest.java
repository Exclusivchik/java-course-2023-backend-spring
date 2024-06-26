package edu.java.scrapper.domain.jpa;

import edu.java.api.domain.repository.jpa.JpaChatRepository;
import edu.java.api.service.TgChatService;
import edu.java.api.service.jpa.JpaTgChatService;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JpaChatServiceTest extends IntegrationTest {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private final TgChatService tgChatService;

    @Autowired
    public JpaChatServiceTest(
        JdbcTemplate jdbcTemplate,
        JpaChatRepository jpaChatRepository
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.tgChatService = new JpaTgChatService(jpaChatRepository);
    }

    @Test
    @Transactional
    @Rollback
    void shouldAddChat() {
        Long chatId = 78L;

        tgChatService.register(chatId);
        Long result = jdbcTemplate.queryForObject("SELECT * FROM chat WHERE chat_id = 78", Long.class);

        assertEquals(78, result);
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowDuplicatedKeyException() {
        Long chatId = 78L;

        tgChatService.register(chatId);
        var ex = catchThrowable(() -> tgChatService.register(chatId));

        assertThat(ex).isInstanceOf(BadRequestException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveChat() {
        Long chatId = 1L;

        tgChatService.register(chatId);
        tgChatService.unregister(chatId);
        Long result = jdbcTemplate.queryForObject("SELECT count(*) from chat WHERE chat_id = 1", Long.class);

        assertEquals(0, result);
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileRemovingChat() {
        Long chatId = 1L;

        Throwable ex = catchThrowable(() -> tgChatService.unregister(chatId));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }
}
