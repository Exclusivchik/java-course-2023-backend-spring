package edu.java.api.domain.repository.jdbc;

import edu.java.api.domain.dto.LinkDto;
import edu.java.api.domain.mappers.LinkDtoRowMapper;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class JdbcLinkRepository {
    private final JdbcTemplate jdbcTemplate;
    private final String dataAccessMessage = "Server error";
    private final String dataAccessDescription = "Ошибка сервера: нет доступа к данным";
    private final String getQuery = "SELECT * FROM link WHERE url = ?";

    @Transactional
    public Long add(URI uri) {
        try {
            return jdbcTemplate.queryForObject(
                "INSERT INTO link (url, last_check, last_update) VALUES (?, ?, ?) RETURNING link_id",
                Long.class,
                uri.toString(), OffsetDateTime.now(), OffsetDateTime.now()
            );
        } catch (DataAccessException ex) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public LinkDto findByUrl(URI link) {
        try {
            List<LinkDto> links = jdbcTemplate.query(
                getQuery,
                new LinkDtoRowMapper(),
                link.toString()
            );
            if (links.isEmpty()) {
                String noLinksMessage = "There's not links";
                String noLinksDescription = "Отсутствуют ресурсы";
                throw new NotFoundException(
                    noLinksMessage,
                    noLinksDescription
                );
            }
            return links.getFirst();
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public void remove(URI uri) {
        try {
            int deletedRow = jdbcTemplate.update(
                "DELETE FROM link WHERE url = ?",
                uri.toString()
            );
            if (deletedRow == 0) {
                throw new NotFoundException(
                    "Invalid link",
                    "This link is doesn't exist"
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
    public List<LinkDto> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM link",
            new LinkDtoRowMapper()
        );
    }

    @Transactional
    public void updateLink(URI url, OffsetDateTime lastUpdate) {
        try {
            jdbcTemplate.update(
                "UPDATE link SET last_update = ?, last_check = current_timestamp WHERE url = ?",
                lastUpdate, url.toString()
            );
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public void setLastCheck(URI checkedLink) {
        try {
            jdbcTemplate.update(
                "UPDATE link SET last_check = current_timestamp WHERE url = ?",
                checkedLink.toString()
            );
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public List<LinkDto> findByLastCheck(int minutes) {
        try {
            return jdbcTemplate.query(
                "SELECT * FROM link WHERE current_timestamp - last_check > '" + minutes + " minutes'",
                new LinkDtoRowMapper()
            );
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public boolean isExist(URI url) {
        try {
            List<Long> links = jdbcTemplate.query(
                getQuery,
                (rowSet, rowNum) -> rowSet.getLong("link_id"),
                url.toString()
            );
            return !links.isEmpty();
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }
}
