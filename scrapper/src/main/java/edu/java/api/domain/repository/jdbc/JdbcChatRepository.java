package edu.java.api.domain.repository.jdbc;

import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class JdbcChatRepository {
    private final JdbcTemplate jdbcTemplate;
    private final String dataAccessMessage = "Server error";
    private final String dataAccessDescription = "Ошибка сервера: нет доступа к данным";
    private final String chatIdString = "chat_id";

    @Transactional
    public void add(Long chatId) {
        try {
            jdbcTemplate.update(
                "INSERT INTO chat VALUES (?)",
                chatId
            );
        } catch (DuplicateKeyException e) {
            throw new BadRequestException(
                "User with the given chat id is already registered",
                "Пользователь уже зарегистрирован"
            );
        } catch (DataAccessException ex) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public void remove(Long chatId) {
        try {
            int deletedRow = jdbcTemplate.update(
                "DELETE FROM chat WHERE chat_id = ?",
                chatId
            );
            if (deletedRow == 0) {
                throw new NotFoundException(
                    "The user with the given chat id is not registered",
                    "Пользователь не зарегистрирован"
                );
            }
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public List<Long> findAll() {
        try {
            return jdbcTemplate.query(
                "SELECT * FROM chat",
                (rowSet, rowNum) -> rowSet.getLong(chatIdString)
            );
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public boolean isNotRegistered(Long chatId) {
        try {
            List<Long> chats = jdbcTemplate.query(
                "SELECT * FROM chat WHERE chat_id = ?",
                (rowSet, rowNum) -> rowSet.getLong(chatIdString),
                chatId
            );
            return chats.isEmpty();
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }
}
