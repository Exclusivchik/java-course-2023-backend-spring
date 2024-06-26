package edu.java.api.domain.repository.jdbc;

import edu.java.api.domain.dto.LinkChatMappingDto;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.models.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class JdbcLinkChatMappingRepository {
    private final JdbcTemplate template;
    private final String dataAccessMessage = "Server error";
    private final String dataAccessDescription = "Ошибка сервера: нет доступа к данным";
    private final String chatIdString = "chat_id";
    private final String linkIdString = "link_id";

    @Transactional
    public void add(Long chatId, Long linkId) {
        try {
            template.update(
                "INSERT INTO link_chat_mapping (chat_id, link_id) VALUES (?, ?)",
                chatId,
                linkId
            );
        } catch (DuplicateKeyException e) {
            throw new BadRequestException(
                "User with the given chat id is already tracking this link",
                "Пользователь уже отслеживает данную ссылку"
            );
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public void remove(Long chatId, Long linkId) {
        try {
            int deletedRow = template.update(
                "DELETE FROM link_chat_mapping WHERE chat_id = ? AND link_id = ?",
                chatId,
                linkId
            );
            if (deletedRow == 0) {
                throw new NotFoundException(
                    "User with the given chat id is not tracking this link",
                    "Пользователь не отслеживает данную ссылку"
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
    public List<LinkChatMappingDto> findAll() {
        try {
            return template.query(
                "SELECT * FROM link_chat_mapping",
                (rowSet, rowNum) -> new LinkChatMappingDto(
                    rowSet.getLong(chatIdString),
                    rowSet.getLong(linkIdString)
                )
            );
        } catch (Exception e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public List<LinkResponse> findAllByChatId(Long chatId) {
        try {
            List<LinkResponse> responses = template.query(
                "SELECT link_id, url "
                    + "FROM ((SELECT * FROM link_chat_mapping WHERE chat_id = ?) AS sub "
                    + "JOIN link USING (link_id))",
                (rowSet, rowNum) -> new LinkResponse(
                    rowSet.getLong(linkIdString),
                    URI.create(rowSet.getString("url"))
                ),
                chatId
            );
            if (responses.isEmpty()) {
                throw new NotFoundException(
                    "User has no subscribes",
                    "У пользователя отсутствуют подписки"
                );
            }
            return responses;
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public List<LinkChatMappingDto> findAllByLinkId(Long linkId) {
        try {
            return template.query(
                "SELECT * FROM link_chat_mapping WHERE link_id = ?",
                (rowSet, rowNum) -> new LinkChatMappingDto(
                    rowSet.getLong(chatIdString),
                    rowSet.getLong(linkIdString)
                ),
                linkId
            );
        } catch (Exception e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }
}
