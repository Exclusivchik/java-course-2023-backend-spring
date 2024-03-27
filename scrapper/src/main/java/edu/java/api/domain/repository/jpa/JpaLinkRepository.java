package edu.java.api.domain.repository.jpa;

import edu.java.models.jpa.Link;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findLinkByUrl(String uri);
}
